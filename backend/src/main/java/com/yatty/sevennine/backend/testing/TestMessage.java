package com.yatty.sevennine.backend.testing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestMessage {
    private String data;

    public static final String TYPE = "TestMessage";

    public TestMessage() {
    }

    public TestMessage(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @JsonProperty(value="_type", access = JsonProperty.Access.READ_ONLY)
    public String getTYPE() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "TestMessage{" +
                "data='" + data + '\'' +
                '}';
    }
}
