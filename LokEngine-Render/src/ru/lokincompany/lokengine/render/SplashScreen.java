package ru.lokincompany.lokengine.render;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import ru.lokincompany.lokengine.render.enums.DrawMode;
import ru.lokincompany.lokengine.render.frame.frameparts.SplashScreenFramePart;
import ru.lokincompany.lokengine.render.window.Window;
import ru.lokincompany.lokengine.tools.vectori.Vector2i;

public class SplashScreen {

    private static SplashScreenFramePart framePart;
    private static Vector2i positionBar;
    private static Vector2i sizeBar;
    private static Window window;

    public static void init(Window window) {
        SplashScreen.framePart = new SplashScreenFramePart(new Vector2i(0, 0), new Vector2i(0, 0), "#/resources/textures/Splash.png");

        SplashScreen.framePart.position = new Vector2i(
                window.getResolution().x / 2 - SplashScreen.framePart.size.x / 2,
                window.getResolution().y / 2 - SplashScreen.framePart.size.y / 2
        );

        SplashScreen.window = window;

        sizeBar = new Vector2i(Math.round(window.getResolution().x * 0.8f), 4);
        positionBar = new Vector2i(Math.round(window.getResolution().x / 2f - sizeBar.x / 2f), window.getResolution().y - 10);
    }

    public static void updateStatus(float percentage) {
        window.getFrameBuilder().getRenderProperties().setDrawMode(DrawMode.RawGUI);
        GL11.glClearColor(0.1372549f, 0.1372549f, 0.1372549f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        framePart.partRender(null);

        if (percentage > 0) {
            GL11.glBegin(GL11.GL_QUADS);

            GL11.glColor4f(1, 1, 1, 1);

            float length = sizeBar.x * percentage;

            GL11.glVertex3f(positionBar.x, positionBar.y, 0);
            GL11.glVertex3f(length + positionBar.x, positionBar.y, 0);
            GL11.glVertex3f(length + positionBar.x, sizeBar.y + positionBar.y, 0);
            GL11.glVertex3f(positionBar.x, sizeBar.y + positionBar.y, 0);

            GL11.glColor4f(0.1f, 0.1f, 0.1f, 1);

            GL11.glVertex3f(length + positionBar.x, positionBar.y, 0);
            GL11.glVertex3f(sizeBar.x + positionBar.x, positionBar.y, 0);
            GL11.glVertex3f(sizeBar.x + positionBar.x, sizeBar.y + positionBar.y, 0);
            GL11.glVertex3f(length + positionBar.x, sizeBar.y + positionBar.y, 0);

            GL11.glEnd();
        }
        window.getVAO().bind();
        GLFW.glfwSwapBuffers(window.getId());
        window.getVAO().unbind();
    }
}
