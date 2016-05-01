package me.synapz.paintball.storage.files;

import me.synapz.paintball.enums.Databases;
import org.bukkit.plugin.Plugin;

/**
 * Created by Jeremy(Refrigerbater) on 4/29/2016.
 */
public class DatabaseFile extends PaintballFile {

    public DatabaseFile(Plugin pb) {
        super(pb, "database.yml");
        loadDatabaseValues();
    }

    public void setValue(String path, Object value) {
        fileConfig.set(path, value);
    }

    private void loadDatabaseValues() {
        for (Databases database : Databases.values()) {
            Databases.ReturnType returnType = database.getReturnType();

            switch (returnType) {
                case BOOLEAN:
                    database.setBoolean(loadBoolean(database));
                    break;
                case INT:
                    database.setInteger(loadInt(database));
                    break;
                case STRING:
                    database.setString(loadString(database));
                    break;
                default:
                    break;
            }
        }
    }

    private int loadInt(Databases type) {
        if (isFoundInConfig(type))
            return (int) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultInt());

        saveFile();

        return type.getDefaultInt();
    }

    private String loadString(Databases type) {
        if (isFoundInConfig(type))
            return (String) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultString());

        saveFile();

        return type.getDefaultString();
    }

    private boolean loadBoolean(Databases type) {
        if (isFoundInConfig(type))
            return (boolean) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultBoolean());

        saveFile();

        return type.getDefaultBoolean();
    }

    private Object loadValue(Databases type) {
        return fileConfig.get(type.getPath());
    }

    private boolean isFoundInConfig(Databases type) {
        Object value = fileConfig.get(type.getPath());

        return value != null;
    }
}
