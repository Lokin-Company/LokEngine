package LokEngine.GUI.Canvases;

import LokEngine.GUI.AdditionalObjects.GUIObjectProperties;
import LokEngine.GUI.GUIObjects.GUIObject;
import LokEngine.Render.Frame.FrameParts.GUI.GUICanvasFramePart;
import LokEngine.Render.Frame.PartsBuilder;
import LokEngine.Tools.Utilities.Color.Color;
import LokEngine.Tools.Utilities.Vector2i;

import java.util.Vector;

public class GUICanvas extends GUIObject {

    Vector<GUIObject> objects = new Vector<>();
    PartsBuilder partsBuilder;
    GUICanvasFramePart framePart;

    public GUICanvas(Vector2i position, Vector2i size) {
        super(position, size);
        partsBuilder = new PartsBuilder();
        framePart = new GUICanvasFramePart(partsBuilder, position, size);
        properties = new GUIObjectProperties(position, size, null);
    }

    public int addObject(GUIObject object) {
        objects.add(object);
        return objects.size() - 1;
    }

    public void setColor(Color color) {
        framePart.color = color;
    }

    @Override
    public void setPosition(Vector2i position) {
        super.setPosition(position);
        framePart.position = position;
    }

    @Override
    public void setSize(Vector2i size) {
        super.setSize(size);
        partsBuilder.setResolution(size);
        framePart.size = size;
        properties.size.x = size.x;
        properties.size.y = size.y;
    }

    public void removeObject(int id) {
        objects.remove(id);
    }

    public void removeAll() {
        objects.clear();
    }

    public GUIObject getObject(int id) {
        return objects.get(id);
    }

    @Override
    public void update(PartsBuilder partsBuilder, GUIObjectProperties parentProperties) {
        super.update(partsBuilder, parentProperties);

        for (GUIObject object : objects) {
            if (!object.hidden)
                object.update(this.partsBuilder, properties);
        }
        partsBuilder.addPart(framePart);
    }
}
