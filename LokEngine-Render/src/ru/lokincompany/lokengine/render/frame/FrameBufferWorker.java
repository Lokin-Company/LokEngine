package ru.lokincompany.lokengine.render.frame;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import ru.lokincompany.lokengine.render.enums.DrawMode;
import ru.lokincompany.lokengine.tools.vectori.Vector2i;
import ru.lokincompany.lokengine.tools.vectori.Vector4i;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_BINDING;

public class FrameBufferWorker {
    private int frameBuffer;
    private int depthBuffer;
    private int textureBuffer;

    private int lastFrameBuffer;

    private Vector2i lastView = new Vector2i();
    private Vector4i lastOrthoView = new Vector4i();
    private Vector2i sourceResolution;
    private Vector2i bufferResolution;
    private DrawMode lastDrawMode;
    private RenderProperties activeProperties;

    private int multisampledColorRenderBuffer;
    private int multisampledDepthRenderBuffer;
    private int multisampledFbo;

    private int multisampleSamples;
    private boolean multisampled;

    public FrameBufferWorker(Vector2i resolution) {
        sourceResolution = resolution;
        bufferResolution = new Vector2i(resolution.x, resolution.y);

        initialiseFrameBuffer(bufferResolution.x, bufferResolution.y);
    }

    public FrameBufferWorker(Vector2i resolution, int samples) {
        this(resolution);
        this.multisampleSamples = samples;
        multisampled = true;
        initialiseMultisampledFrameBuffer(samples, resolution.x, resolution.y);
    }

    public void cleanUp() {
        GL30.glDeleteFramebuffers(frameBuffer);
        GL11.glDeleteTextures(textureBuffer);
        GL11.glDeleteTextures(depthBuffer);

        if (multisampled) {
            GL30.glDeleteFramebuffers(multisampledFbo);
            glDeleteRenderbuffers(multisampledColorRenderBuffer);
            glDeleteRenderbuffers(multisampledDepthRenderBuffer);
        }
    }

    public Vector2i getResolution() {
        return bufferResolution;
    }

    public void setResolution(Vector2i resolution) {
        sourceResolution = resolution;
        bufferResolution.x = resolution.x;
        bufferResolution.y = resolution.y;

        cleanUp();
        initialiseFrameBuffer(bufferResolution.x, bufferResolution.y);
        initialiseMultisampledFrameBuffer(multisampleSamples, resolution.x, resolution.y);
    }

    public void bindFrameBuffer(DrawMode drawMode, RenderProperties properties, Vector2i offset) {
        if (!sourceResolution.equals(bufferResolution)) {
            setResolution(sourceResolution);
        }
        bindFrameBuffer(multisampled ? multisampledFbo : frameBuffer, properties);
        lastDrawMode = properties.getActiveDrawMode();
        activeProperties = properties;
        properties.setDrawMode(drawMode, bufferResolution, new Vector4i(bufferResolution.x, bufferResolution.y, offset.x, offset.y));
    }

    public void bindFrameBuffer(DrawMode drawMode, RenderProperties properties) {
        bindFrameBuffer(drawMode, properties, new Vector2i());
    }

    public void unbindCurrentFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, lastFrameBuffer);
        activeProperties.setDrawMode(lastDrawMode, lastView, lastOrthoView);
    }

    public int getTextureBuffer() {
        if (multisampled)
            resolveMultisampled();
        return textureBuffer;
    }

    public int getDepthBuffer() {
        return depthBuffer;
    }

    public int getFrameBuffer() {
        return frameBuffer;
    }

    public int getMultisampledColorRenderBuffer() {
        return multisampledColorRenderBuffer;
    }

    public int getMultisampledDepthRenderBuffer() {
        return multisampledDepthRenderBuffer;
    }

    public int getMultisampledFbo() {
        return multisampledFbo;
    }

    public int getMultisampleSamples() {
        return multisampleSamples;
    }

    public boolean isMultisampled() {
        return multisampled;
    }

    private void initialiseFrameBuffer(int x, int y) {
        frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        textureBuffer = createTextureAttachment(x, y);
        depthBuffer = createDepthAttachment(x, y);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, lastFrameBuffer);
    }

    private void initialiseMultisampledFrameBuffer(int samples, int x, int y) {
        multisampledColorRenderBuffer = glGenRenderbuffers();
        multisampledDepthRenderBuffer = glGenRenderbuffers();
        multisampledFbo = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, multisampledFbo);
        glBindRenderbuffer(GL_RENDERBUFFER, multisampledColorRenderBuffer);

        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA8, x, y);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, multisampledColorRenderBuffer);

        glBindRenderbuffer(GL_RENDERBUFFER, multisampledDepthRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, ARBFramebufferObject.GL_DEPTH24_STENCIL8, x, y);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, multisampledDepthRenderBuffer);

        int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void bindFrameBuffer(int frameBuffer, RenderProperties properties) {
        lastFrameBuffer = glGetInteger(GL_FRAMEBUFFER_BINDING);
        int[] view = new int[4];
        glGetIntegerv(GL_VIEWPORT, view);
        lastView.x = view[2];
        lastView.y = view[3];
        lastOrthoView = properties.getOrthoView();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
    }

    private void resolveMultisampled() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, multisampledFbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBuffer);
        glBlitFramebuffer(0, 0, bufferResolution.x, bufferResolution.y, 0, 0, bufferResolution.x, bufferResolution.y, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private int createDepthAttachment(int width, int height) {
        int depth = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depth);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, depth, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return depth;
    }

    private int createTextureAttachment(int width, int height) {
        int texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return texture;
    }

}
