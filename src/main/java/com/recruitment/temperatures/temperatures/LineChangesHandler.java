package com.recruitment.temperatures.temperatures;


import com.recruitment.temperatures.models.GitDiffLine;

public interface LineChangesHandler {
    void handle(GitDiffLine csvLine);
}
