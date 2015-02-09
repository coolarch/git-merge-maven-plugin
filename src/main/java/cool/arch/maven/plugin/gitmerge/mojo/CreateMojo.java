package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import cool.arch.maven.plugin.gitmerge.model.Release;
import cool.arch.maven.plugin.gitmerge.model.Releases;

/**
 *
 */
@Mojo(name="create", requiresOnline=true, requiresProject=true)
public class CreateMojo extends AbstractMojo {

	/**
	 * Filename of the releases tracking file.
	 */
	private static final String RELEASES_JSON = "releases.json";

	/**
	 * Message to display if the project isn't a Git project.
	 */
	private static final String NO_DOT_GIT_MESSAGE = "No .git directory found! This project is not managed by Git.";

	/**
	 * Base directory of the Git repository.
	 */
	private File baseDirectory = null;

	/**
	 * File where the releases.json is located.
	 */
	private	File releasesJson = null;

	/**
	 * Maven project instance.
	 */
	@Component
	private MavenProject project;

	/**
	 * Executes the Maven Mojo.
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		discoverRepoDirectory();
		validateRepoExists();

		if (isChildProject()) {
			return;
		}

		releasesJson = new File(baseDirectory, RELEASES_JSON);

		if (releasesJsonExists()) {
			return;
		}

		createReleasesJson();
	}

	/**
	 * Discovers the Git repository directory to be used as the base directory of the project.
	 * @throws MojoFailureException If an error occurred while attempting to locate the Git repository directory
	 */
	private void discoverRepoDirectory() throws MojoFailureException {
		Repository repository = null;

		try {
			repository = new FileRepositoryBuilder().readEnvironment().findGitDir().build();
			baseDirectory = repository.getDirectory();

			getLog().debug("Discovered baseDirectory at: " + baseDirectory.getAbsolutePath());
		} catch (final IOException e) {
			throw new MojoFailureException("Error looking up Git repository path", e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}
	}

	/**
	 * Verifies that the repository folder actual exists.
	 * @throws MojoFailureException
	 */
	private void validateRepoExists() throws MojoFailureException {
		if (baseDirectory == null || !baseDirectory.exists()) {
			getLog().error(NO_DOT_GIT_MESSAGE);

			throw new MojoFailureException(NO_DOT_GIT_MESSAGE);
		}
	}

	private boolean isChildProject() {
		boolean childProject = false;

		if (!baseDirectory.equals(project.getBasedir())) {
			childProject = true;
			getLog().debug("Executing on child module.  Not executings");
		}

		return childProject;
	}

	private boolean releasesJsonExists() {
		return (releasesJson != null && releasesJson.isFile());
	}

	private void createReleasesJson() throws MojoFailureException {
		final Releases releases = buildReleases();
		final ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(releasesJson, releases);
		} catch (final IOException e) {
			throw new MojoFailureException("Error writing releases.json", e);
		}
	}

	private Releases buildReleases() {
		final Releases releases = new Releases();
		releases.setProjectName("Project Name Here");
		releases.setReleases(new ArrayList<Release>());

		return releases;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(final File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public File getReleasesJson() {
		return releasesJson;
	}

	public void setReleasesJson(final File releasesJson) {
		this.releasesJson = releasesJson;
	}

	public MavenProject getProject() {
		return project;
	}

	public void setProject(final MavenProject project) {
		this.project = project;
	}
}
