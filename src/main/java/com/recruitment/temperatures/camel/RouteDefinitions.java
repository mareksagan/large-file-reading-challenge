package com.recruitment.temperatures.camel;

import com.recruitment.temperatures.RecruitmentChallengeCSVProperties;
import com.recruitment.temperatures.git.GitRepository;
import com.recruitment.temperatures.models.CommitMessage;
import com.recruitment.temperatures.models.FileName;
import com.recruitment.temperatures.models.GitDiffLine;
import com.recruitment.temperatures.temperatures.jpa.LineChangesHandler;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * Apache Camel Route Configuration for filesystem change detection
 */
@Component
public class RouteDefinitions extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(RouteDefinitions.class);

    private final GitRepository gitRepository;
    private final RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties;
    private final LineChangesHandler linesChangeHandler;
    private static final String CHANGE_DETECTION_COMMIT_MESSAGE = "Apache Camel Runtime Change Detection";

    public RouteDefinitions(GitRepository gitRepository, RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties, LineChangesHandler linesChangeHandler) {
        this.gitRepository = gitRepository;
        this.recruitmentChallengeCSVProperties = recruitmentChallengeCSVProperties;
        this.linesChangeHandler = linesChangeHandler;
    }

    /**
     * Listener on MODIFY events for the CSV file
     */
    @Override
    public void configure() {

        from("file-watch:" + recruitmentChallengeCSVProperties.getDirectory() + "?events=MODIFY")
                .filter(val -> val.getIn().getBody(File.class).getName().equals(recruitmentChallengeCSVProperties.getFileName()))
                .process(exchange -> {
                    log.info("Processing runtime change for the given CSV file");
                    gitRepository.commit(new FileName(recruitmentChallengeCSVProperties.getFileName()), new CommitMessage(CHANGE_DETECTION_COMMIT_MESSAGE));
                    List<String> lines = gitRepository.headEdits(new FileName(recruitmentChallengeCSVProperties.getFileName()));
                    lines.forEach(val -> linesChangeHandler.handle(new GitDiffLine(val)));
                });
    }
}
