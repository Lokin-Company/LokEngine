package ru.lokincompany.lokengine.render.frame;

import org.lwjgl.opengl.GL11;
import ru.lokincompany.lokengine.render.enums.DrawMode;
import ru.lokincompany.lokengine.tools.Logger;
import ru.lokincompany.lokengine.tools.color.Color;
import ru.lokincompany.lokengine.tools.vectori.Vector2i;

import java.util.Vector;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class PartsBuilder {

    public Color clearColor = new Color(0, 0, 0, 0);
    FrameBufferWorker frameBufferWorker;
    Vector<FramePart> frameParts = new Vector<>();

    public PartsBuilder() {
    }

    public PartsBuilder(Vector2i resolution) {
        setResolution(resolution);
    }

    public void addPart(FramePart fp) {
        frameParts.add(fp);
    }

    public Vector2i getResolution() {
        return frameBufferWorker.getResolution();
    }

    public void setResolution(Vector2i resolution) {
        if (frameBufferWorker != null)
            frameBufferWorker.cleanUp();

        frameBufferWorker = new FrameBufferWorker(resolution);
    }

    public int build(Vector<FramePart> frameParts, DrawMode drawMode, BuilderProperties builderProperties) {
        return build(frameParts, drawMode, builderProperties, new Vector2i());
    }

    public int build(Vector<FramePart> frameParts, DrawMode drawMode, BuilderProperties builderProperties, Vector2i viewOffset) {
        if (frameBufferWorker == null)
            frameBufferWorker = new FrameBufferWorker(builderProperties.getBuilderWindow().getResolution());

        frameBufferWorker.bindFrameBuffer(drawMode, builderProperties, viewOffset);

        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glClearColor(clearColor.red, clearColor.green, clearColor.blue, clearColor.alpha);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


            for (FramePart framePart : frameParts) {
                try {
                    if (!framePart.inited) {
                        framePart.init(builderProperties);
                        framePart.inited = true;
                    }
                    framePart.partRender(builderProperties);
                } catch (Exception e) {
                    Logger.error("Fail render frame part!", "LokEngine_PartsBuilder");
                    Logger.printException(e);
                }
            }

        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        frameBufferWorker.unbindCurrentFrameBuffer();

        return frameBufferWorker.getTexture();
    }

    public int build(DrawMode drawMode, BuilderProperties builderProperties, Vector2i viewOffset) {
        int result = build(frameParts, drawMode, builderProperties, viewOffset);
        frameParts.clear();
        return result;
    }

    public int build(DrawMode drawMode, BuilderProperties builderProperties) {
        return build(drawMode, builderProperties, new Vector2i());
    }

}
