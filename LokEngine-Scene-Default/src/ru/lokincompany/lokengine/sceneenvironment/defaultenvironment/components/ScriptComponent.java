package ru.lokincompany.lokengine.sceneenvironment.defaultenvironment.components;

import ru.lokincompany.lokengine.applications.ApplicationRuntime;
import ru.lokincompany.lokengine.render.frame.PartsBuilder;
import ru.lokincompany.lokengine.sceneenvironment.defaultenvironment.SceneObject;
import ru.lokincompany.lokengine.sceneenvironment.defaultenvironment.components.additionalobjects.ScriptSceneObject;
import ru.lokincompany.lokengine.tools.saveworker.Saveable;

public class ScriptComponent extends Component {

    ScriptSceneObject script;

    public ScriptComponent() {
    }

    public ScriptComponent(ScriptSceneObject script) {
        this.script = script;
    }

    @Override
    public void update(SceneObject source, ApplicationRuntime applicationRuntime, PartsBuilder partsBuilder) {
        if (script != null)
            script.execute(source, applicationRuntime, partsBuilder);
    }

    @Override
    public String save() {
        return null;
    }

    @Override
    public Saveable load(String savedString) {
        return null;
    }
}
