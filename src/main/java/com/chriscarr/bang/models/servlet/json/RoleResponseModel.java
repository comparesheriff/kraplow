package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleResponseModel {
    @JsonProperty("role")
    private final String role;

    public RoleResponseModel(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
