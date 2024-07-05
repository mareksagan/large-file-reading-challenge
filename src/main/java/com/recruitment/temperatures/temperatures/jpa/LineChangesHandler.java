package com.recruitment.temperatures.temperatures.jpa;

import com.recruitment.temperatures.models.GitDiffLine;

public interface LineChangesHandler {
    void handle(GitDiffLine csvLine);
}
