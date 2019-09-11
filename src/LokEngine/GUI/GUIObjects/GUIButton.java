package LokEngine.GUI.GUIObjects;

import LokEngine.GUI.AdditionalObjects.GUIButtonScript;
import LokEngine.Tools.Misc;
import LokEngine.Tools.RuntimeFields;
import LokEngine.Tools.Utilities.Color;
import LokEngine.Tools.Utilities.Vector2i;

public class GUIButton extends GUIObject {

    public Color pressedColor;
    public Color calmStateColor;
    public GUIText text;
    public GUIPanel panel;

    private Color activeColor;
    private GUIButtonScript pressScript;
    private GUIButtonScript unpressScript;
    private boolean pressed;

    public boolean isPressed(){
        return pressed;
    }

    public GUIButton(Vector2i position, Vector2i size, Color pressed, Color calmState, GUIText text, GUIPanel panel) {
        super(position, size);
        this.text = text;
        if (size.x <= 0 || size.y <= 0){
            size = this.text.size;
            this.size = size;
        }
        this.pressedColor = pressed;
        this.calmStateColor = calmState;
        this.panel = panel;
        this.activeColor = new Color(calmStateColor.red,calmStateColor.green,calmStateColor.blue, calmStateColor.alpha);
    }

    public GUIButton(Vector2i position, Vector2i size, Color calmState, String text) {
        super(position, size);
        this.text = new GUIText(position,text);
        if (size.x <= 0 || size.y <= 0){
            size = this.text.size;
            this.size = size;
        }
        float colorChange = (calmState.red + calmState.green + calmState.blue) / 3 >= 0.5f ? -0.1f : 0.1f;
        this.pressedColor = new Color(calmState.red + colorChange,calmState.green + colorChange,calmState.blue + colorChange,calmState.alpha);
        this.calmStateColor = calmState;
        this.activeColor = new Color(calmStateColor.red,calmStateColor.green,calmStateColor.blue, calmStateColor.alpha);
        this.panel = new GUIPanel(position, size, activeColor);
    }

    public void setPressScript(GUIButtonScript script){
        this.pressScript = script;
    }

    public void setUnpressScript(GUIButtonScript script){
        this.unpressScript = script;
    }

    private void pressed(){
        activeColor.red = pressedColor.red;
        activeColor.green = pressedColor.green;
        activeColor.blue = pressedColor.blue;

        if (pressScript != null)
            pressScript.execute(this);
    }

    private void unpressed(){
        activeColor.red = calmStateColor.red;
        activeColor.green = calmStateColor.green;
        activeColor.blue = calmStateColor.blue;

        if (unpressScript != null)
            unpressScript.execute(this);
    }

    private void checkMouse(){
        boolean enterInBox = Misc.mouseInField(position,size);

        if (enterInBox && RuntimeFields.getMouseStatus().getPressedStatus()){
            if (!pressed){
                pressed();
                pressed = true;
            }
        }

        if ((!RuntimeFields.getMouseStatus().getPressedStatus() || !enterInBox) && pressed){
            unpressed();
            pressed = false;
        }
    }

    @Override
    public void setPosition(Vector2i position){
        this.position = position;
        text.setPosition(position);
        panel.setPosition(position);
    }

    @Override
    public void setSize(Vector2i size){
        this.size = size;
        text.setSize(size);
        panel.setSize(size);
    }

    @Override
    public void update(){
        panel.position.x = position.x;
        panel.position.y = position.y;
        panel.size.x = size.x;
        panel.size.y = size.y;
        text.position.x  = position.x;
        text.position.y = position.y;

        checkMouse();

        panel.update();
        text.update();
    }

}