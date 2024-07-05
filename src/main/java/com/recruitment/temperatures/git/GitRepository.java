package com.recruitment.temperatures.git;

import com.recruitment.temperatures.models.CommitMessage;
import com.recruitment.temperatures.models.FileName;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;

import java.io.IOException;
import java.util.List;

public interface GitRepository {
    void commit(FileName fileName, CommitMessage message) throws GitAPIException;

    List<String> headEdits(FileName fileName) throws IOException;

    List<String> headEdits() throws IOException;

    ObjectId latestObjectId() throws IOException;

    Status status() throws GitAPIException;
}
