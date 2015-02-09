package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public abstract class AbstractGitMergeMojo extends AbstractMojo {

  /**
   * Message to display if the project isn't a Git project.
   */
  public static final String NO_DOT_GIT_MESSAGE = "No .git directory found! This project is not managed by Git.";

  /**
   * Base directory of the Git repository.
   */
  private File baseDirectory = null;

  /**
   * Maven project instance.
   */
  @Component
  private MavenProject project;

  /**
   * Discovers the Git repository directory to be used as the base directory of the project.
   * @throws MojoFailureException If an error occurred while attempting to locate the Git repository directory
   */
  protected final void discoverRepoDirectory() throws MojoFailureException {
    Repository repository = null;

    try {
      repository = new FileRepositoryBuilder().readEnvironment().findGitDir().build();
      final File dotGitDirectory = repository.getDirectory();

      if (dotGitDirectory != null) {
        setBaseDirectory(dotGitDirectory.getParentFile());
      }

      getLog().debug("Discovered baseDirectory at: " + getBaseDirectory().getAbsolutePath());
    } catch (final IOException e) {
      throw new MojoFailureException("Error looking up Git repository path", e);
    } finally {
      if (repository != null) {
        repository.close();
      }
    }
  }

  protected final boolean branchExists(final String branchName) throws MojoFailureException {
    Repository repository = null;

    final String fullBranchName = "refs/remotes/origin/" + branchName;

    boolean branchExists = false;

    try {
      repository = new FileRepositoryBuilder().readEnvironment().findGitDir().build();
      final List<Ref> call = new Git(repository).branchList().setListMode(ListMode.ALL).call();

      for (final Ref reference : call) {
        getLog().debug("Found Branch: " + reference.getName());

        if (reference.getName().equals(fullBranchName)) {
          branchExists = true;
          break;
        }
      }
    } catch (final IOException e) {
      throw new MojoFailureException("Error looking up Git repository path", e);
    }
    catch (final GitAPIException e) {
      throw new MojoFailureException("Error looking up Git branches", e);
    } finally {
      if (repository != null) {
        repository.close();
      }
    }

    return branchExists;
  }

  /**
   * Verifies that the repository folder actual exists.
   * @throws MojoFailureException
   */
  protected final void validateRepoExists() throws MojoFailureException {
    if (baseDirectory == null || !baseDirectory.exists()) {
      getLog().error(NO_DOT_GIT_MESSAGE);

      throw new MojoFailureException(NO_DOT_GIT_MESSAGE);
    }
  }

  protected final boolean isChildProject() {
    boolean childProject = false;

    if (!getBaseDirectory().equals(getProject().getBasedir())) {
      childProject = true;
      getLog().debug("Executing on child module.  Not executings");
    }

    return childProject;
  }

  public final File getBaseDirectory() {
    return baseDirectory;
  }

  public final void setBaseDirectory(final File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  public final MavenProject getProject() {
    return project;
  }

  public final void setProject(final MavenProject project) {
    this.project = project;
  }
}
