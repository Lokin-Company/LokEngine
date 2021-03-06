package ru.lokincompany.lokengine.render.frame.frameparts.gui;

import ru.lokincompany.lokengine.gui.guiobjects.GUIFreeTextDrawer;
import ru.lokincompany.lokengine.render.enums.FramePartType;
import ru.lokincompany.lokengine.render.frame.FramePart;
import ru.lokincompany.lokengine.render.frame.RenderProperties;
import ru.lokincompany.lokengine.tools.color.Color;
import ru.lokincompany.lokengine.tools.vectori.Vector2i;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class GUIGraphFramePart extends FramePart {

    public Vector2i position;
    public Vector2i size;
    public Color color;
    public Color color2;
    public float maxHeight;
    public float minHeight;
    public int maxPoints;
    ArrayList<Float> points;
    GUIFreeTextDrawer freeTextDrawer;

    public GUIGraphFramePart(Vector2i position, Vector2i size, ArrayList<Float> points, float maxHeight, float minHeight, int maxPoints, Color color, Color color2, GUIFreeTextDrawer freeTextDrawer) {
        super(FramePartType.GUI);
        this.points = points;
        this.position = position;
        this.size = size;
        this.color = color;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.maxPoints = maxPoints;
        this.freeTextDrawer = freeTextDrawer;
        this.color2 = color2;
    }

    @Override
    public void init(RenderProperties renderProperties) {
    }

    @Override
    public void partRender(RenderProperties renderProperties) {
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_LINES);
        glColor4f(color2.red, color2.green, color2.blue, color2.alpha);

        for (int i = 0; i <= size.y; i += size.y / 5) {
            Vector2i pos = new Vector2i(position.x, position.y + (size.y - i));
            freeTextDrawer.draw(String.valueOf(Math.round(i * (maxHeight / size.y))), new Vector2i(pos.x, pos.y), color2);
            glVertex2f(pos.x, pos.y);
            glVertex2f(pos.x + size.x, pos.y);
        }

        glEnd();

        glBegin(GL_LINE_STRIP);
        glColor4f(color.red, color.green, color.blue, color.alpha);

        for (int i = 0; i < points.size(); i++) {
            glVertex2f(
                    ((float) i / (float) maxPoints * size.x) + position.x, position.y + size.y - size.y * (Math.min(maxHeight, Math.max(minHeight, points.get(i))) / (maxHeight + minHeight))
            );
        }

        glEnd();
    }

}
