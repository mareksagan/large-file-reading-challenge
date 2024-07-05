package com.recruitment.temperatures.models;

import com.recruitment.temperatures.assertions.Assert;

public record FileName(String fileName) {
    public FileName {
        Assert.notBlank("fileName", fileName);
    }
}
