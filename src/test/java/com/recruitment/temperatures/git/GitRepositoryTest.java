package com.recruitment.temperatures.git;

import com.recruitment.temperatures.UnitTest;
import com.recruitment.temperatures.models.CommitMessage;
import com.recruitment.temperatures.models.FileName;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.assertj.core.util.Files;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


@UnitTest
public class GitRepositoryTest {
    private static final Path directory = Files.currentFolder().toPath()
            .resolve("tmp");
    private GitRepository gitRepository;

    @BeforeEach
    public void setup() throws IOException {
        directory.toFile().mkdir();
        File file = directory.resolve(GitConfiguration.GIT_DIRECTORY_NAME).toFile();
        Repository repository = FileRepositoryBuilder.create(file);
        repository.create();
        gitRepository = new GitRepositoryImpl(new Git(repository));
    }

    @AfterEach
    public void clean() throws IOException {
        FileUtils.deleteDirectory(directory.toFile());
    }


    @Test
    void headEditTests() throws IOException, GitAPIException {
        String fileName = "dummy.csv";
        File csv = directory.resolve(fileName).toFile();
        String firstLine = "First line has been added";
        String secondLine = "Second line has been added";
        csv.createNewFile();
        try (FileWriter writer = new FileWriter(csv)) {
            writer.append(firstLine);
            writer.flush();
            gitRepository.commit(new FileName(fileName), new CommitMessage(firstLine + " commit"));

            writer.append("\n");
            writer.append(secondLine);
            writer.flush();
            gitRepository.commit(new FileName(fileName), new CommitMessage(secondLine + " commit"));

            Assertions.assertLinesMatch(gitRepository.headEdits(new FileName(fileName)), List.of("+" + secondLine));
        }

    }
}
