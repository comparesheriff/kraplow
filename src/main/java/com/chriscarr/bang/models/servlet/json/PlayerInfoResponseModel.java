package com.chriscarr.bang.models.servlet.json;

public class PlayerInfoResponseModel extends JsonResponseModel {
    private String name;
    private String role;
    private String goal;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }
}
