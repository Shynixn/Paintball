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
    STEPS;

    @Override
    public String toString() {
        return "%" + super.toString().toLowerCase() + "%"; // Turns ARENA into %arena%
    }
}
