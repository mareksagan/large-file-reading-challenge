package com.recruitment.temperatures.models;

import com.recruitment.temperatures.assertions.Assert;

public record GitDiffLine(String line) {
    public GitDiffLine {
        Assert.notBlank("line", line);
    }
}
