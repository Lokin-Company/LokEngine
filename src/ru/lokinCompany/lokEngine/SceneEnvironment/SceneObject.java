package ru.lokinCompany.lokEngine.SceneEnvironment;

import ru.lokinCompany.lokEngine.Components.ComponentList;
import ru.lokinCompany.lokEngine.Render.Frame.PartsBuilder;
import ru.lokinCompany.lokEngine.Tools.ApplicationRuntime;
import ru.lokinCompany.lokEngine.Tools.Base64.Base64;
import ru.lokinCompany.lokEngine.Tools.SaveWorker.Saveable;
import org.lwjgl.util.vector.Vector2f;

public class SceneObject implements Saveable {
    public Vector2f position = new Vector2f(0, 0);
    public float rollRotation = 0;
    public float renderPriority = 0;
    public ComponentList components;
    public Scene scene;
    public String name = "Unnamed object";

    public SceneObject() {
        components = new ComponentList();
    }

    public void init(Scene scene) {
        this.scene = scene;
    }

    public void update(ApplicationRuntime applicationRuntime, PartsBuilder partsBuilder) {
        components.update(this, applicationRuntime, partsBuilder);
    }

    @Override
    public String save() {
        return Base64.toBase64(position.x + "\n" + position.y + "\n" + rollRotation + "\n" + renderPriority + "\n" + components.save() + "\n" + name);
    }

    @Override
    public Saveable load(String savedString) {
        String[] lines = Base64.fromBase64(savedString).split("\n");

        position = new Vector2f(Float.valueOf(lines[0]), Float.valueOf(lines[1]));
        rollRotation = Float.valueOf(lines[2]);
        renderPriority = Float.valueOf(lines[3]);
        components.load(lines[4]);
        name = lines[5];

        return this;
    }
}