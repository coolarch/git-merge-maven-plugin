package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import cool.arch.maven.plugin.gitmerge.model.Branch;
import cool.arch.maven.plugin.gitmerge.model.Release;

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
	 * Executes the Maven Mojo.
	 */
	@Override
	public void executeWork() throws MojoExecutionException, MojoFailureException {
		if (targetBranch == null || !targetBranch.startsWith("release/")) {
      throw new MojoFailureException("Invalid target branch name.  Branch names must start with release/");
		}

    if (mergeBranch == null) {
      throw new MojoFailureException("Invalid merge branch name.");
    }

		if (!getReleasesDao().releasesJsonExists()) {
			throw new MojoFailureException("releases.json does not exist.  Please run git-merge:create to create the releases.json first");
		}

		try {
      getReleasesDao().read();
    }
    catch (final IOException e) {
      throw new MojoFailureException("Error reading releases.json file", e);
    }

		if (!branchExists(targetBranch)) {
		  throw new MojoFailureException(String.format("Target branch %s does not exists", targetBranch));
		}

    if (!branchExists(mergeBranch)) {
      throw new MojoFailureException(String.format("Merge branch %s does not exists", mergeBranch));
    }

		if (!releasesContainsBranch(targetBranch)) {
      throw new MojoFailureException(String.format("Branch %s not configured in releases.json", targetBranch));
		}

		Release foundRelease = null;

		for (final Release release : getReleasesDao().getReleases().getReleases()) {
		  if (targetBranch.equals(release.getTargetBranch())) {
		    foundRelease = release;
		    break;
		  }
		}

		if (foundRelease == null) {
      throw new MojoFailureException(String.format("Unknown target branch %s does not exists", targetBranch));
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
		  foundRelease.getBranches().add(branch);
		}

		try {
      getReleasesDao().write();
    }
    catch (final IOException e) {
      throw new MojoFailureException("Error reading releases.json file", e);
    }
	}
}
