package com.recruitment.temperatures;

import com.recruitment.temperatures.git.GitRepository;
import com.recruitment.temperatures.models.CommitMessage;
import com.recruitment.temperatures.models.FileName;
import com.recruitment.temperatures.models.GitDiffLine;
import com.recruitment.temperatures.temperatures.jpa.LineChangesHandler;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Class used to detect if there were any file changes that wasn't in sync when startup
 */
@Component
public class ServerStartupChangeDetector {
    private static final String CHANGE_DETECTION_COMMIT_MESSAGE = "Server startup Change Detection";
    private final GitRepository gitRepository;
    private final RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties;
    private final LineChangesHandler lineChangesHandler;

    public ServerStartupChangeDetector(GitRepository gitRepository, RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties, LineChangesHandler lineChangesHandler1) {
        this.gitRepository = gitRepository;
        this.recruitmentChallengeCSVProperties = recruitmentChallengeCSVProperties;
        this.lineChangesHandler = lineChangesHandler1;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void sync() throws GitAPIException, IOException {
        Status status = gitRepository.status();
        if (status.getModified().contains(recruitmentChallengeCSVProperties.getFileName())) {
            gitRepository.commit(new FileName(recruitmentChallengeCSVProperties.getFileName()), new CommitMessage(CHANGE_DETECTION_COMMIT_MESSAGE));
            List<String> lines = gitRepository.headEdits(new FileName(recruitmentChallengeCSVProperties.getFileName()));
            lines.forEach(val -> lineChangesHandler.handle(new GitDiffLine(val)));
        }
    }
}
