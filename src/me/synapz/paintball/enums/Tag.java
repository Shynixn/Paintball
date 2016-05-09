package me.synapz.paintball.enums;

public enum Tag {

    ARENA,
    SENDER,
    AMOUNT,
    TEAM,
    TEAMS,
    STAT,
    STATS,
    ERROR,
    MAX,
    STEPS,
    ARENA_TYPE,
    ARENA_TYPES,
    COMMAND,
    THEME,
    TIME,
    SECONDARY,
    PREFIX;

    @Override
    public String toString() {
        return "%" + super.toString().toLowerCase().replace("_", "-") + "%"; // Turns ARENA into %arena%
    }
}
