package com.recruitment.temperatures.batch;

import com.recruitment.temperatures.RecruitmentChallengeCSVProperties;
import com.recruitment.temperatures.git.GitRepository;
import com.recruitment.temperatures.models.CommitMessage;
import com.recruitment.temperatures.models.FileName;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;


@Component
public class StartupCSVLoader {
    private final JobLauncher jobLauncher;
    private final Job job;
    private final GitRepository gitRepository;
    private final RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties;
    private static final String INITIAL_BATCH_COMMIT = "initial csv file batch persistence";


    public StartupCSVLoader(JobLauncher jobLauncher, Job job, GitRepository gitRepository, RecruitmentChallengeCSVProperties recruitmentChallengeCSVProperties) {
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.gitRepository = gitRepository;
        this.recruitmentChallengeCSVProperties = recruitmentChallengeCSVProperties;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, GitAPIException, IOException {
        if (Objects.isNull(gitRepository.latestObjectId())) {
            jobLauncher.run(job, new JobParameters());
            gitRepository.commit(new FileName(recruitmentChallengeCSVProperties.getFileName()), new CommitMessage(INITIAL_BATCH_COMMIT));
        }
    }
}
