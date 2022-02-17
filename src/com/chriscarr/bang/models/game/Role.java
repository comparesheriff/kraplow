package com.chriscarr.bang.models.game;

public enum Role {
    SHERIFF("Sheriff", "Kill the outlaws and renegade"),
    OUTLAW("Outlaw", "Kill the sheriff"),
    DEPUTY("Deputy", "Kill the outlaws and renegade"),
    RENEGADE("Renegade", "Be the last one alive"),
    RANDOM("random", "Random Role");

    private final String goal;
    private final String roleName;

    Role(String roleName, String goal) {
        this.roleName = roleName;
        this.goal = goal;
    }

    public static Role getRole(int ordinal) {
        for (Role value : values()) {
            if (value.ordinal() == ordinal) {
                return value;
            }
        }
        return OUTLAW;
    }

    public static Role getRole(String roleName) {
        for (Role value : values()) {
            if (value.roleName.equals(roleName)) {
                return value;
            }
        }
        return OUTLAW;
    }

    public String roleName() {
        return roleName;
    }

    public String goal() {
        return goal;
    }
}
