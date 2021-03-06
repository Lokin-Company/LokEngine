package ru.lokincompany.lokengine.sceneenvironment.defaultenvironment.components.additionalobjects.rigidbody.shapes;

import org.lwjgl.util.vector.Vector2f;
import ru.lokincompany.lokengine.sceneenvironment.defaultenvironment.components.componentstools.ShapeCreator;
import ru.lokincompany.lokengine.tools.Base64;
import ru.lokincompany.lokengine.tools.saveworker.Saveable;

public class BoxShape extends Shape {

    public Vector2f collideSize;

    public BoxShape() {
    }

    public BoxShape(Vector2f collideSize, org.jbox2d.collision.shapes.Shape shape) {
        this.collideSize = collideSize;
        this.shape = shape;
    }

    @Override
    public String save() {
        return Base64.toBase64(collideSize.x + "\n" + collideSize.y);
    }

    @Override
    public Saveable load(String savedString) {
        String[] stringVectors = Base64.fromBase64(savedString).split("\n");
        this.collideSize = new Vector2f(Float.parseFloat(stringVectors[0]), Float.parseFloat(stringVectors[1]));
        this.shape = ShapeCreator.CreateBoxShape(collideSize).shape;

        return this;
    }
}
