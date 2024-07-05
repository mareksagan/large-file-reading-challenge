package com.recruitment.temperatures.git;

import com.recruitment.temperatures.RecruitmentChallengeCSVProperties;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class GitConfiguration {
    private final RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties;
    public static final String GIT_DIRECTORY_NAME = ".git";

    public GitConfiguration(RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties) {
        this.recruitmentChallengeCSVProperties = recruitmentChallengeCSVProperties;
    }

    @Bean
    public Git repository() throws IOException {
        File file = new File(recruitmentChallengeCSVProperties.getDirectory(), GIT_DIRECTORY_NAME);
        Repository repository = FileRepositoryBuilder.create(file);
        if (!file.exists()) {
            repository.create();
        }
        return new Git(repository);
    }
}
