package com.chriscarr.bang;

public enum Role {
    SHERIFF("Sheriff", "Kill the outlaws and renegade"),
    OUTLAW("Outlaw", "Kill the sheriff"),
    DEPUTY("Deputy", "Kill the outlaws and renegade"),
    RENEGADE("Renegade", "Be the last one alive"),
    RANDOM("Random", "Random");

    private final String roleName;
    private final String goal;

    Role(String roleName, String goal) {
        this.roleName = roleName;
        this.goal = goal;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getGoal() {
        return goal;
    }
}
