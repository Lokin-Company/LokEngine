package ru.lokincompany.lokengine.render;
import org.lwjgl.opengl.GL15;
import ru.lokincompany.lokengine.render.exceptions.GLFWNotInitializedError;
import ru.lokincompany.lokengine.tools.Logger;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL15C.*;

public class VBO {
    int vboID;

    public VBO(){
        if (!GLFW.isInited()) throw new GLFWNotInitializedError();
        vboID = GL15.glGenBuffers();
    }

    public VBO(float[] points){
        this();
        putData(points);
    }

    public VBO(ArrayList<Float> points){
        this();
        putData(points);
    }

    public void bind(){
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
    }

    public void unbind(){
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
    }

    public int getID(){
        return vboID;
    }

    public void putData(float[] points) throws GLFWNotInitializedError {
        if (!GLFW.isInited()) throw new GLFWNotInitializedError();

        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, points, GL15.GL_DYNAMIC_DRAW);
        unbind();
    }

    public void putData(ArrayList<Float> points) throws GLFWNotInitializedError {
        if (!GLFW.isInited()) throw new GLFWNotInitializedError();
        float[] pointsArray = new float[points.size()];

        for (int i = 0; i < pointsArray.length; i++) {
            pointsArray[i] = points.get(i);
        }

        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, pointsArray, GL15.GL_DYNAMIC_DRAW);
        unbind();
    }

    public void unload() throws GLFWNotInitializedError {
        if (!GLFW.isInited()) throw new GLFWNotInitializedError();
        GL15.glDeleteBuffers(vboID);
        vboID = -1;
    }

}
