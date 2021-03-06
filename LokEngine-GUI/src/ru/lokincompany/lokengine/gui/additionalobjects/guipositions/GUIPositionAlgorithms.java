package ru.lokincompany.lokengine.gui.additionalobjects.guipositions;

import ru.lokincompany.lokengine.gui.additionalobjects.GUILocationAlgorithm;
import ru.lokincompany.lokengine.gui.canvases.GUICanvas;
import ru.lokincompany.lokengine.tools.vectori.Vector2i;

public class GUIPositionAlgorithms {
    public static GUILocationAlgorithm getAlgorithm(GUICanvas canvas, GUIPosition position) {
        GUILocationAlgorithm algorithm;
        switch (position) {
            case TopLeft:
                algorithm = object -> new Vector2i(0, 0);
                break;

            case TopCenter:
                algorithm = object -> new Vector2i(canvas.getSize().x / 2 - object.getSize().x / 2, 0);
                break;

            case TopRight:
                algorithm = object -> new Vector2i(canvas.getSize().x - object.getSize().x, 0);
                break;

            case CenterLeft:
                algorithm = object -> new Vector2i(0, canvas.getSize().y / 2 - object.getSize().y / 2);
                break;

            case Center:
                algorithm = object -> new Vector2i(canvas.getSize().x / 2 - object.getSize().x / 2, canvas.getSize().y / 2 - object.getSize().y / 2);
                break;

            case CenterRight:
                algorithm = object -> new Vector2i(canvas.getSize().x - object.getSize().x, canvas.getSize().y / 2 - object.getSize().y / 2);
                break;

            case BottomLeft:
                algorithm = object -> new Vector2i(0, canvas.getSize().y - object.getSize().y);
                break;

            case BottomCenter:
                algorithm = object -> new Vector2i(canvas.getSize().x / 2 - object.getSize().x / 2, canvas.getSize().y - object.getSize().y);
                break;

            case BottomRight:
                algorithm = object -> new Vector2i(canvas.getSize().x - object.getSize().x, canvas.getSize().y - object.getSize().y);
                break;

            default:
                algorithm = object -> new Vector2i();
        }

        return algorithm;
    }

}
