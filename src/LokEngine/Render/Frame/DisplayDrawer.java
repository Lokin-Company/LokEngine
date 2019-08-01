package LokEngine.Render.Frame;

import LokEngine.Components.AdditionalObjects.Sprite;
import LokEngine.Loaders.BufferLoader;
import LokEngine.Render.Enums.DrawMode;
import LokEngine.Render.Enums.FramePartType;
import LokEngine.Render.Frame.FramePart;
import LokEngine.Render.Shader;
import LokEngine.Render.Texture;
import LokEngine.Render.Window;
import LokEngine.Tools.DefaultFields;
import LokEngine.Tools.RuntimeFields;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class DisplayDrawer {

    public static void bindTexture(String uniformName, int textureBuffer, int index){
        glUniform1i(glGetUniformLocation(Shader.currentShader.program, uniformName), index);

        glActiveTexture(GL_TEXTURE0 + index);
        glBindTexture(GL_TEXTURE_2D, textureBuffer);
    }

    public static int blurPostProcess(Window win, int postFrame, int originalFrame, FrameBufferWorker blurSceneFrameWorker1, FrameBufferWorker blurSceneFrameWorker2, FrameBufferWorker blurSceneFrameWorker3){
        blurSceneFrameWorker1.bindFrameBuffer();
        win.setDrawMode(DrawMode.Display);
        Shader.use(DefaultFields.PostProcessingShader);
        DisplayDrawer.bindTexture("postFrame", postFrame,1);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUniform2f(GL20.glGetUniformLocation(Shader.currentShader.program,"direction"),0, 1);
        DisplayDrawer.renderScreen(originalFrame);

        blurSceneFrameWorker1.unbindCurrentFrameBuffer();
        blurSceneFrameWorker2.bindFrameBuffer();
        win.setDrawMode(DrawMode.Display);
        Shader.use(DefaultFields.PostProcessingShader);
        DisplayDrawer.bindTexture("postFrame", postFrame,1);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUniform2f(GL20.glGetUniformLocation(Shader.currentShader.program,"direction"),0.866f / (win.getResolution().x / win.getResolution().y), 0.5f);
        DisplayDrawer.renderScreen(blurSceneFrameWorker1.getTexture());

        blurSceneFrameWorker2.unbindCurrentFrameBuffer();
        blurSceneFrameWorker3.bindFrameBuffer();
        win.setDrawMode(DrawMode.Display);
        Shader.use(DefaultFields.PostProcessingShader);
        DisplayDrawer.bindTexture("postFrame", postFrame,1);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUniform2f(GL20.glGetUniformLocation(Shader.currentShader.program,"direction"),0.866f / (win.getResolution().x / win.getResolution().y), -0.5f);
        DisplayDrawer.renderScreen(blurSceneFrameWorker2.getTexture());

        blurSceneFrameWorker3.unbindCurrentFrameBuffer();

        return blurSceneFrameWorker3.getTexture();
    }

    public static void renderScreen(int frameTextureBuffer){
        bindTexture("frame",frameTextureBuffer,0);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, DefaultFields.defaultUVBuffer);
        glVertexAttribPointer(
                1,
                2,
                GL_FLOAT,
                false,
                0,
                0);
        glVertexAttribDivisor(1, 0);

        glBindBuffer(GL_ARRAY_BUFFER, DefaultFields.defaultVertexScreenBuffer);
        glVertexAttribPointer(
                0,
                2,
                GL_FLOAT,
                false,
                0,
                0);
        glVertexAttribDivisor(0, 0);

        glDrawArrays(GL_QUADS,0,8);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }

}
