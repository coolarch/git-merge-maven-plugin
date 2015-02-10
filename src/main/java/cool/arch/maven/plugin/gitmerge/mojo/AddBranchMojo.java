package cool.arch.maven.plugin.gitmerge.mojo;

import static cool.arch.maven.plugin.gitmerge.Constants.RELEASES_JSON;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import cool.arch.maven.plugin.gitmerge.model.Branch;
import cool.arch.maven.plugin.gitmerge.model.MergeStrategy;
import cool.arch.maven.plugin.gitmerge.model.Release;
import cool.arch.maven.plugin.gitmerge.model.Releases;

/**
 *
 */
@Mojo(name="add-branch", requiresOnline=true, requiresProject=true)
public class AddBranchMojo extends AbstractGitMergeMojo {

	@Parameter(defaultValue="${targetBranch}")
	private String targetBranch;

  @Parameter(defaultValue="${mergeBranch}")
  private String mergeBranch;

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

		if (targetBranch == null || !targetBranch.startsWith("release/")) {
      throw new MojoFailureException("Invalid target branch name.  Branch names must start with release/");
		}

    if (mergeBranch == null) {
      throw new MojoFailureException("Invalid merge branch name.");
    }

		if (!releasesJsonExists()) {
			throw new MojoFailureException("releases.json does not exist.  Please run git-merge:create to create the releases.json first");
		}

		final Releases releases = readReleasesJson();

		if (releases.getReleases() == null) {
		  releases.setReleases(new ArrayList<Release>());
		}

		if (!branchExists(targetBranch)) {
		  throw new MojoFailureException(String.format("Target branch %s does not exists", targetBranch));
		}

    if (!branchExists(mergeBranch)) {
      throw new MojoFailureException(String.format("Merge branch %s does not exists", mergeBranch));
    }

		if (!containsBranch(releases, targetBranch)) {
      throw new MojoFailureException(String.format("Branch %s not configured in releases.json", targetBranch));
		}

		Release foundRelease = null;

		for (final Release release : releases.getReleases()) {
		  if (targetBranch.equals(release.getTargetBranch())) {
		    foundRelease = release;
		    break;
		  }
		}

		if (foundRelease.getBranches() == null) {
		  foundRelease.setBranches(new ArrayList<Branch>());
		}

		boolean foundBranch = false;

		for (final Branch branch : foundRelease.getBranches()) {
		  if (mergeBranch.equals(branch.getName())) {
		    foundBranch = true;
		    break;
		  }
		}

		if (!foundBranch) {
		  final Branch branch = new Branch();
		  branch.setName(mergeBranch);
		  branch.setDescription("");
		  foundRelease.getBranches().add(branch);
		}

    final Release release = new Release();
    release.setTargetBranch(targetBranch);
    release.setStrategy(MergeStrategy.OCTOPUS);
    release.setBranches(new ArrayList<Branch>());

    releases.getReleases().add(release);

		writeReleasesJson(releases);
	}

	private boolean releasesJsonExists() {
		return (releasesJson != null && releasesJson.isFile());
	}

  private Releases readReleasesJson() throws MojoFailureException {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    Releases releases = null;

    try {
      releases = mapper.readValue(releasesJson, Releases.class);

    } catch (final IOException e) {
      throw new MojoFailureException("Error writing releases.json", e);
    }

    return releases;
  }

	private void writeReleasesJson(final Releases releases) throws MojoFailureException {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			mapper.writeValue(releasesJson, releases);
		} catch (final IOException e) {
			throw new MojoFailureException("Error writing releases.json", e);
		}
	}

	private boolean containsBranch(final Releases releases, final String targetBranch) {
	  requireNonNull(targetBranch, "targetBranch shall not be null");

	  boolean branchFound = false;

	  for (final Release release : releases.getReleases()) {
	    if (targetBranch.equals(release.getTargetBranch())) {
	      branchFound = true;
	      break;
	    }
	  }

	  return branchFound;
	}

	public File getReleasesJson() {
		return releasesJson;
	}

	public void setReleasesJson(final File releasesJson) {
		this.releasesJson = releasesJson;
	}
}
