package com.recruitment.temperatures.git;

import com.recruitment.temperatures.models.CommitMessage;
import com.recruitment.temperatures.models.FileName;
import jakarta.annotation.PostConstruct;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;


@Component
public class GitRepositoryImpl implements GitRepository {
    private final Git git;

    public GitRepositoryImpl(Git git) {
        this.git = git;
    }


    @Override
    public void commit(FileName fileName, CommitMessage message) throws GitAPIException {
        git.add().addFilepattern(fileName.fileName()).call();
        git.commit().setMessage(message.message()).call();
    }

    @Override
    public List<String> headEdits(FileName fileName) throws IOException {
        return headEdits(val -> val.getOldPath().equals(fileName.fileName()) && val.getNewPath().equals(fileName.fileName()));
    }
    @Override
    public List<String> headEdits() throws IOException {
        return headEdits(val -> true);
    }

    @Override
    public ObjectId latestObjectId() throws IOException {
        return git.getRepository().resolve("HEAD^{tree}");
    }

    @Override
    public Status status() throws GitAPIException {
        return git.status().call();
    }

    private List<String> headEdits(Predicate<DiffEntry> predicate) throws IOException {
        Repository repository = git.getRepository();
        ObjectId oldHead = repository.resolve("HEAD~1^{tree}");
        ObjectId newHead = repository.resolve("HEAD^{tree}");
        ObjectReader reader = repository.newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        oldTreeIter.reset(reader, oldHead);
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        newTreeIter.reset(reader, newHead);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(byteArrayOutputStream);
        df.setRepository(git.getRepository());
        List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);
        List<String> edits = new ArrayList<>();
        for (DiffEntry entry : entries) {
            if (predicate.test(entry)) {
                df.format(entry);
                List<String> diff = Arrays.stream(String.valueOf(byteArrayOutputStream).split("\n"))
                        .filter(val -> val.startsWith("-") || val.startsWith("+"))
                        .skip(2).toList();
                edits.addAll(diff.stream()
                        .filter(val -> diff.stream().map(other -> other.substring(1)).filter(other -> other.equals(val.substring(1))).count() == 1)

                        .toList());
            }
        }
        return edits;
    }

}
