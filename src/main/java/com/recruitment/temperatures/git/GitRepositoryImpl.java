package com.recruitment.temperatures.git;

import com.recruitment.temperatures.models.CommitMessage;
import com.recruitment.temperatures.models.FileName;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Builds API Layer Integration with Git
 */
@Component
public class GitRepositoryImpl implements GitRepository {
    private static final Logger log = LoggerFactory.getLogger(GitRepositoryImpl.class);
    private final Git git;

    public GitRepositoryImpl(Git git) {
        this.git = git;
    }


    @Override
    public void commit(FileName fileName, CommitMessage message) throws GitAPIException {
        log.debug("Committing latest changes for file {}",fileName);
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

    /**
     * Returns added and deleted lines between latest two commits from Git HEAD
     *
     * @param predicate Conditional check for one diff between commits.
     */
    private List<String> headEdits(Predicate<DiffEntry> predicate) throws IOException {
        Repository repository = git.getRepository();
        // Obtaining the tree of the parent of the HEAD commit
        ObjectId oldHead = repository.resolve("HEAD~1^{tree}");
        // Obtaining the tree of the head commit
        ObjectId newHead = repository.resolve("HEAD^{tree}");
        log.debug("Lookup line difference between {} and {}", oldHead, newHead);
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
                // Algorithm to detect real git diff and changes
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
