package ru.lokincompany.lokengine.gui.guiobjects;

import ru.lokincompany.lokengine.gui.additionalobjects.GUILocationAlgorithm;
import ru.lokincompany.lokengine.gui.additionalobjects.GUIObjectProperties;
import ru.lokincompany.lokengine.gui.additionalobjects.GUIObjectUpdateScript;
import ru.lokincompany.lokengine.render.frame.PartsBuilder;
import ru.lokincompany.lokengine.tools.vectori.Vector2i;

public class GUIObject {
    public boolean ignoreCanvasUpdateOrder;
    public boolean hidden;
    public GUIObjectProperties properties;
    protected Vector2i position;
    protected Vector2i size;
    protected GUIObjectUpdateScript updateScript;
    protected GUILocationAlgorithm positionAlgorithm;
    protected GUILocationAlgorithm sizeAlgorithm;
    protected boolean touchable;
    protected boolean active;
    protected boolean focused;
    protected boolean retention;
    protected boolean mouseInField;

    public GUIObject(Vector2i position, Vector2i size) {
        this.position = position;
        this.size = size;
        properties = new GUIObjectProperties(position, size);
    }

    public GUIObject() {
        this(new Vector2i(), new Vector2i());
    }

    public Vector2i getPosition() {
        return position;
    }

    public <T extends GUIObject> T setPosition(Vector2i position) {
        this.position = position;
        return (T) this;
    }

    public <T extends GUIObject> T setPosition(GUILocationAlgorithm position) {
        positionAlgorithm = position;
        return (T) this;
    }

    public Vector2i getSize() {
        return size;
    }

    public <T extends GUIObject> T setSize(Vector2i size) {
        this.size = size;
        properties.size.x = size.x;
        properties.size.y = size.y;
        return (T) this;
    }

    public <T extends GUIObject> T setSize(GUILocationAlgorithm size) {
        sizeAlgorithm = size;
        return (T) this;
    }

    public GUIObjectUpdateScript getUpdateScript() {
        return updateScript;
    }

    public void setUpdateScript(GUIObjectUpdateScript updateScript) {
        this.updateScript = updateScript;
    }

    protected void pressed() {
    }

    protected void unpressed() {
    }

    protected void focused() {
    }

    protected void unfocused() {
    }

    protected void retention() {
    }

    protected void endRetention() {
    }

    protected void mouseInField() {

    }

    protected void mouseOutField() {

    }

    protected void updateAlgorithms() {
        if (positionAlgorithm != null) {
            Vector2i newPosition = positionAlgorithm.calculate(this);
            if (!newPosition.equals(position)) {
                setPosition(newPosition);
            }
        }

        if (sizeAlgorithm != null) {
            Vector2i newSize = sizeAlgorithm.calculate(this);
            if (!newSize.equals(size)) {
                setSize(newSize);
            }
        }
    }

    public void update(PartsBuilder partsBuilder, GUIObjectProperties parentProperties) {
        updateAlgorithms();
        properties.globalPosition.x = parentProperties.globalPosition.x + position.x;
        properties.globalPosition.y = parentProperties.globalPosition.y + position.y;
        properties.window = parentProperties.window;
        properties.mouseRaycastStatus = parentProperties.mouseRaycastStatus;

        boolean inField = properties.mouseRaycastStatus.mouse.inField(properties.globalPosition, size);
        boolean mousePressed = properties.mouseRaycastStatus.mouse.getPressedStatus();

        if (!properties.mouseRaycastStatus.touched && touchable) {
            if (inField) {
                properties.mouseRaycastStatus.touched = mousePressed;

                if (mousePressed && !properties.mouseRaycastStatus.lastFramePressed && !active) {
                    active = true;
                    focused = true;
                    pressed();
                    focused();
                } else if (!mousePressed && active) {
                    active = false;
                    unpressed();
                }

                if (!mouseInField) {
                    mouseInField = true;
                    mouseInField();
                }

            } else {
                if (mouseInField) {
                    mouseInField = false;
                    mouseOutField();
                }

                if (active) {
                    active = false;
                    focused = false;
                    unpressed();
                    unfocused();
                }
            }
        }

        if (!retention && mousePressed && !properties.mouseRaycastStatus.lastFramePressed && inField) {
            retention = true;
            retention();
        } else if (retention && !mousePressed) {
            retention = false;
            endRetention();
        }

        if (!inField && mousePressed || properties.mouseRaycastStatus.touched && !inField) {
            if (focused) {
                focused = false;
                unfocused();
            }
            if (active) {
                active = false;
                unpressed();
            }
        }
        if (updateScript != null)
            updateScript.execute(this);
    }

}
