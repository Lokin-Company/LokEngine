package ru.lokinCompany.lokEngine.Render.Frame;

import ru.lokinCompany.lokEngine.Render.Enums.DrawMode;
import ru.lokinCompany.lokEngine.Tools.Logger;
import ru.lokinCompany.lokEngine.Tools.Utilities.Color.Color;
import ru.lokinCompany.lokEngine.Tools.Utilities.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.Vector;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

public class PartsBuilder {

    FrameBufferWorker frameBufferWorker;
    public Color clearColor = new Color(0, 0, 0, 0);
    Vector<FramePart> frameParts = new Vector<>();

    public void addPart(FramePart fp) {
        frameParts.add(fp);
    }

    public PartsBuilder() {
    }

    public PartsBuilder(Vector2i resolution) {
        setResolution(resolution);
    }

    public void setResolution(Vector2i resolution) {
        if (frameBufferWorker != null)
            frameBufferWorker.cleanUp();

        frameBufferWorker = new FrameBufferWorker(resolution);
    }

    public Vector2i getResolution(){
        return frameBufferWorker.getResolution();
    }

    public int build(Vector<FramePart> frameParts, DrawMode drawMode, BuilderProperties builderProperties) {
        if (frameBufferWorker == null)
            frameBufferWorker = new FrameBufferWorker(builderProperties.getBuilderWindow().getResolution());

        frameBufferWorker.bindFrameBuffer(drawMode, builderProperties);

        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glClearColor(clearColor.red, clearColor.green, clearColor.blue, clearColor.alpha);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        try {
            for (FramePart framePart : frameParts) {
                framePart.partRender(builderProperties);
            }
        } catch (Exception e) {
            Logger.error("Fail render frame part!", "LokEngine_PartsBuilder");
            Logger.printException(e);
        }
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        frameBufferWorker.unbindCurrentFrameBuffer();

        return frameBufferWorker.getTexture();
    }

    public int build(DrawMode drawMode, BuilderProperties builderProperties) {
        int result = build(frameParts, drawMode, builderProperties);
        frameParts.clear();
        return result;
    }

}