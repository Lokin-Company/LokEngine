package ru.lokincompany.lokengine.tools.saveworker;

import ru.lokincompany.lokengine.tools.Base64;
import ru.lokincompany.lokengine.tools.Logger;

import java.io.*;
import java.lang.reflect.Field;

public interface AutoSaveable extends Serializable, Saveable {

    @Override
    default String save() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            return Base64.bytesToBase64(baos.toByteArray());
        } catch (IOException e) {
            Logger.warning("Fail auto save object!", "LokEngine_AutoSaveable");
            Logger.printThrowable(e);
        }
        return null;
    }

    @Override
    default Saveable load(String savedString) {
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(Base64.bytesFromBase64(savedString)));

            Object loadedObject = ois.readObject();
            ois.close();

            Field[] loadedObjectFields = loadedObject.getClass().getDeclaredFields();

            for (Field loadedField : loadedObjectFields) {
                loadedField.setAccessible(true);
                loadedField.set(this, loadedField.get(loadedObject));
                loadedField.setAccessible(false);
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
            Logger.warning("Fail auto load object!", "LokEngine_AutoSaveable");
            Logger.printThrowable(e);
        }

        return this;
    }
}
