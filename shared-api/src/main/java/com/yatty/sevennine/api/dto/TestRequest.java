package com.yatty.sevennine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestRequest {
    public static final String TYPE = "TestRequest";
    private String responseData;

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    @JsonProperty(value="_type", access = JsonProperty.Access.READ_ONLY)
    public String getTYPE() {
        return TYPE;
    }

}
