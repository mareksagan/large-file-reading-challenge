package com.recruitment.temperatures.models;

import com.recruitment.temperatures.assertions.Assert;

public record CommitMessage(String message) {
    public CommitMessage {
        Assert.notBlank("message", message);
    }
}
