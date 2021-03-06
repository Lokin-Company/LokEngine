package ru.lokincompany.lokengine.sceneenvironment.defaultenvironment.components;

import ru.lokincompany.lokengine.applications.ApplicationRuntime;
import ru.lokincompany.lokengine.render.frame.PartsBuilder;
import ru.lokincompany.lokengine.sceneenvironment.defaultenvironment.SceneObject;
import ru.lokincompany.lokengine.tools.saveworker.ArraySaver;
import ru.lokincompany.lokengine.tools.saveworker.Saveable;
import ru.lokincompany.lokengine.tools.saveworker.SubclassSaver;

import java.util.ArrayList;

public class ComponentsList implements Saveable {

    private ArrayList<Component> components = new ArrayList<>();

    public int add(Component component) {
        components.add(component);
        return components.size() - 1;
    }

    public int getSize() {
        return components.size();
    }

    public void update(SceneObject source, ApplicationRuntime applicationRuntime, PartsBuilder partsBuilder) {
        for (Component component : components) {
            component.update(source, applicationRuntime, partsBuilder);
        }
    }

    public void remove(int id) {
        components.remove(id);
    }

    public <T extends Component> void remove(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getClass().getName().equals(componentClass.getName())) {
                remove(i);
                break;
            }
        }
    }

    public Component get(int id) {
        return components.get(id);
    }

    public <T extends Component> T get(Class<T> componentClass) {
        for (Component component : components) {
            if (component.getClass().getName().equals(componentClass.getName())) {
                return (T) component;
            }
        }
        return null;
    }

    @Override
    public String save() {
        ArraySaver arraySaver = new ArraySaver(SubclassSaver.class);

        for (Component component : components) {
            arraySaver.arrayList.add(new SubclassSaver(component));
        }

        return arraySaver.save();
    }

    @Override
    public Saveable load(String savedString) {
        ArraySaver arraySaver = (ArraySaver) new ArraySaver(SubclassSaver.class).load(savedString);

        for (Saveable loadedComponent : arraySaver.arrayList) {
            components.add((Component) ((SubclassSaver) loadedComponent).saveableObject);
        }

        return this;
    }

}
