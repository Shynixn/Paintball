package me.synapz.paintball.enums;

public enum Databases {

    SQL_ENABLED("SQL.enabled", false, ReturnType.BOOLEAN),
    MY_SQL("SQL.MySQL", true, ReturnType.BOOLEAN),
    HOST("SQL.host", "localhost", ReturnType.STRING),
    PORT("SQL.port", 1433, ReturnType.INT),
    USERNAME("SQL.username", "admin", ReturnType.STRING),
    PASSWORD("SQL.password", "password", ReturnType.STRING),
    DATABASE("SQL.database", "Paintball", ReturnType.STRING),
    TABLE("SQL.table", "Paintball_Stats", ReturnType.STRING),

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
