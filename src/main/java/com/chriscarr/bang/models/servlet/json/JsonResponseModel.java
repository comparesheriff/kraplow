package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class JsonResponseModel implements Serializable {
    @JsonIgnore
    private final ObjectMapper mapper;

    @JsonIgnore
    private String overwriteJson;

    public JsonResponseModel() {
        this.mapper = new ObjectMapper();
    }

    public String getOverwriteJson() {
        return overwriteJson;
    }

    public void setOverwriteJson(String overwriteJson) {
        this.overwriteJson = overwriteJson;
    }

    public String toJsonString(){
        try {
            return overwriteJson == null ? mapper.writeValueAsString(this) : overwriteJson;
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
