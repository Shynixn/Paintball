package me.synapz.paintball.enums;

public enum Databases {


    SQL_ENABLED("SQL.enabled", false),
    HOST("SQL.host", "localhost"),
    PORT("SQL.port", 1433),
    USERNAME("SQL.username", "admin"),
    PASSWORD("SQL.password", "password"),
    DATABASE("SQL.database", "Paintball"),

    BUNGEE_ENABLED("Bungee.enabled", false),
    SERVER_ID("Bungee.serverID", "Generate");

    private String path;
    private String defaultString;
    private boolean defaultBoolean;
    private int defaultInt;

    Databases(String path, String defaultValue) {
        this.path = path;
        this.defaultString = defaultValue;
    }

    Databases(String path, int defaultValue) {
        this.path = path;
        this.defaultInt = defaultValue;
    }

    Databases(String path, boolean defaultValue) {
        this.path = path;
        this.defaultBoolean = defaultValue;
    }

    public String getPath() {
        return path;
    }

    public int getDefaultInt() {
        return defaultInt;
    }

    public String getDefaultString() {
        return defaultString;
    }

    public boolean getDefaultBoolean() {
        return defaultBoolean;
    }
}
