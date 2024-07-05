package com.recruitment.temperatures;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Wrapper for CSV file configuration
 */
@Configuration
@ConfigurationProperties(prefix = "recruitment.challenge.csv")
public class RecruitmentChallengeCSVProperties {
    private String delimiter;
    private String directory;
    private String fileName;

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
