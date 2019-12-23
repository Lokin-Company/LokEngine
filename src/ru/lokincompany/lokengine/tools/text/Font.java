package ru.lokincompany.lokengine.tools.text;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import ru.lokincompany.lokengine.loaders.TextureLoader;
import ru.lokincompany.lokengine.render.Texture;
import ru.lokincompany.lokengine.tools.utilities.Vector2i;
import ru.lokincompany.lokengine.tools.utilities.color.Color;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class Font {

    private HashMap<Character, Glyph> glyphs;
    private Texture texture;
    private int fontHeight;
    private float spaceSize;

    public HashMap<Character, Glyph> getGlyphs() {
        return glyphs;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getFontHeight() {
        return fontHeight;
    }

    public Font(Texture texture, HashMap<Character, Glyph> glyphs, int fontHeight) {
        this.texture = texture;
        this.glyphs = glyphs;
        this.fontHeight = fontHeight;
    }

    public int getWidth(CharSequence text) {
        int width = 0;
        int lineWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Glyph g = glyphs.get(c);
            if (g == null) {
                lineWidth += spaceSize;
                continue;
            }

            if (c == '\r') continue;

            if (c == '\n') {
                width = Math.max(width, lineWidth);
                lineWidth = 0;
                continue;
            }

            lineWidth += g.width;
            spaceSize += g.width;
            spaceSize /= 2f;
        }
        width = Math.max(width, lineWidth);
        return width;
    }

    public int getHeight(CharSequence text) {
        int height = 0;
        int lineHeight = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Glyph g = glyphs.get(c);
            if (g == null) continue;
            if (c == '\r') continue;
            if (c == '\n') {
                height += lineHeight;
                lineHeight = 0;
                continue;
            }

            lineHeight = Math.max(lineHeight, g.height);
        }
        height += lineHeight;
        return height;
    }

    public void drawText(String text, Vector2i position, int maxWidth, TextColorShader shader) {
        int drawX = position.x;
        int drawY = position.y;

        GL11.glBindTexture(GL_TEXTURE_2D, texture.buffer);
        glBegin(GL_QUADS);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                drawY += fontHeight;
                drawX = position.x;
                continue;
            }
            if (ch == '\r') continue;

            Glyph g = glyphs.get(ch);

            if (g == null) {
                drawX += spaceSize;
                continue;
            }

            int width = drawX + g.width;
            int height = drawY + g.height;

            if (maxWidth != -1 && width > maxWidth)
                continue;

            float glTexX = g.x / (float) texture.sizeX;
            float glTexY = g.y / (float) texture.sizeY;
            float glTexWidth = (g.x + g.width) / (float) texture.sizeX;
            float glTexHeight = (g.y + g.height) / (float) texture.sizeY;

            Color color = shader.getColor(new Vector2i(drawX - position.x, drawY - position.y));
            glColor4d(color.red, color.green, color.blue, color.alpha);

            glTexCoord2f(glTexX, glTexHeight);
            glVertex3f(drawX, drawY, 0);

            glTexCoord2f(glTexWidth, glTexHeight);
            glVertex3f(width, drawY, 0);

            glTexCoord2f(glTexWidth, glTexY);
            glVertex3f(width, height, 0);

            glTexCoord2f(glTexX, glTexY);
            glVertex3f(drawX, height, 0);

            spaceSize += g.width;
            spaceSize /= 2f;
            drawX += g.width;
        }
        glEnd();
        GL11.glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void drawText(String text, Vector2i position, int maxWidth, Color color) {
        drawText(text, position, maxWidth, charPos -> color);
    }

    public void drawText(String text, Vector2i position, int maxWidth) {
        drawText(text, position, maxWidth, new Color(1, 1, 1, 1));
    }

    public void drawText(String text, Vector2i position, Color color) {
        drawText(text, position, -1, charPos -> color);
    }

    public void drawText(String text, Vector2i position) {
        drawText(text, position, new Color(1, 1, 1, 1));
    }


    public void dispose() {
        TextureLoader.unloadTexture(texture);
    }
}