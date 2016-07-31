package me.synapz.paintball.enums;

public enum Databases {

    ENABLED("Database.enabled", false, ReturnType.BOOLEAN),
    HOST("Database.host", "localhost", ReturnType.STRING),
    PORT("Database.port", 3306, ReturnType.INT),
    USERNAME("Database.username", "username", ReturnType.STRING),
    PASSWORD("Database.password", "password", ReturnType.STRING),
    DATABASE("Database.database", "Paintball", ReturnType.STRING),
    MIN_CONNECTIONS("Database.min-connections", ReturnType.INT),
    MAX_CONNECTIONS("Database.max-connections", ReturnType.INT),
    TABLE("Database.table", "Paintball_Stats", ReturnType.STRING),

    BUNGEE_ENABLED("Bungee.enabled", false, ReturnType.BOOLEAN),
    SERVER_ID("Bungee.serverID", "Generating", ReturnType.STRING),
    BUNGEE_ID("Bungee.bungeeID", "BungeeServerNameHere", ReturnType.STRING);

    private String path;
    private ReturnType returnType;

    private String defaultString;
    private boolean defaultBoolean;
    private int defaultInt;

    private String string;
    private boolean bool;
    private int integer;

    Databases(String path, ReturnType returnType) {
        this.path = path;
        this.returnType = returnType;
    }

    Databases(String path, String defaultValue, ReturnType returnType) {
        this(path, returnType);
        this.defaultString = defaultValue;
    }

    Databases(String path, int defaultValue, ReturnType returnType) {
        this(path, returnType);
        this.defaultInt = defaultValue;
    }

    Databases(String path, boolean defaultValue, ReturnType returnType) {
        this(path, returnType);
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

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setBoolean(boolean bool) {
        this.bool = bool;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public int getInteger() {
        return integer;
    }

    public boolean getBoolean() {
        return bool;
    }

    public enum ReturnType {
        BOOLEAN,
        STRING,
        INT
    }
}
