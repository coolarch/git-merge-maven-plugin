package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import cool.arch.maven.plugin.gitmerge.model.Release;
import cool.arch.maven.plugin.gitmerge.model.Releases;

/**
 *
 */
@Mojo(name="create", requiresOnline=true, requiresProject=true)
public class CreateMojo extends AbstractGitMergeMojo {

	/**
	 * Filename of the releases tracking file.
	 */
	private static final String RELEASES_JSON = "releases.json";

	/**
	 * File where the releases.json is located.
	 */
	private	File releasesJson = null;

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

		releasesJson = new File(getBaseDirectory(), RELEASES_JSON);

		if (releasesJsonExists()) {
			return;
		}

		createReleasesJson();
	}

	private boolean releasesJsonExists() {
		return (releasesJson != null && releasesJson.isFile());
	}

	private void createReleasesJson() throws MojoFailureException {
		final Releases releases = buildReleases();
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			mapper.writeValue(releasesJson, releases);
		} catch (final IOException e) {
			throw new MojoFailureException("Error writing releases.json", e);
		}
	}

	private Releases buildReleases() {
		final Releases releases = new Releases();
		releases.setReleases(new ArrayList<Release>());

		return releases;
	}

	public File getReleasesJson() {
		return releasesJson;
	}

	public void setReleasesJson(final File releasesJson) {
		this.releasesJson = releasesJson;
	}
}
