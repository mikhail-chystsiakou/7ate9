package com.yatty.sevennine.backend;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class TestMessage {
    private String data;

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

    @Override
    public String toString() {
        return "TestMessage{" +
                "data='" + data + '\'' +
                '}';
    }
}
