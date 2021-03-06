package ru.lokincompany.lokengine.render.frame.frameparts.gui;

import ru.lokincompany.lokengine.render.Texture;
import ru.lokincompany.lokengine.render.enums.FramePartType;
import ru.lokincompany.lokengine.render.frame.FramePart;
import ru.lokincompany.lokengine.render.frame.RenderProperties;
import ru.lokincompany.lokengine.tools.OpenGLFastTools;
import ru.lokincompany.lokengine.tools.color.Color;
import ru.lokincompany.lokengine.tools.color.Colors;
import ru.lokincompany.lokengine.tools.vectori.Vector2i;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class GUIImageFramePart extends FramePart {

    public Texture texture;
    public Vector2i position;
    public Vector2i size;
    public Color color = Colors.white();

    public GUIImageFramePart(Vector2i position, Vector2i size, String path) {
        super(FramePartType.GUI);
        this.texture = new Texture(path);
        this.position = position;
        if (size.x <= 0 || size.y <= 0) {
            size = new Vector2i(texture.getSizeX(), texture.getSizeY());
        }
        this.size = size;
    }

    public GUIImageFramePart(Vector2i position, Vector2i size) {
        super(FramePartType.GUI);
        this.position = position;
        this.size = size;
    }

    @Override
    public void init(RenderProperties renderProperties) {
    }

    @Override
    public void partRender(RenderProperties renderProperties) {
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        if (texture != null && texture.getBuffer() != -1) {
            glBindTexture(GL_TEXTURE_2D, texture.getBuffer());
            glColor4d(color.red, color.green, color.blue, color.alpha);

            OpenGLFastTools.drawSquare(position, size);

            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }
}
