package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import cool.arch.maven.plugin.gitmerge.model.Branch;
import cool.arch.maven.plugin.gitmerge.model.MergeStrategy;
import cool.arch.maven.plugin.gitmerge.model.Release;

/**
 *
 */
@Mojo(name="add", requiresOnline=true, requiresProject=true)
public class AddMojo extends AbstractGitMergeMojo {

	@Parameter(defaultValue="${targetBranch}")
	private String targetBranch;

	/**
	 * Executes the Maven Mojo.
	 */
	@Override
	public void executeWork() throws MojoExecutionException, MojoFailureException {
		if (targetBranch == null || !targetBranch.startsWith("release/")) {
      throw new MojoFailureException("Invalid target branch name.  Branch names must start with release/");
		}

		if (!getReleasesDao().releasesJsonExists()) {
			throw new MojoFailureException("releases.json does not exist.  Please run git-merge:create to create the releases.json first");
		}

		if (!branchExists(targetBranch)) {
		  throw new MojoFailureException(String.format("Branch %s does not exists", targetBranch));
		}

		if (releasesContainsBranch(targetBranch)) {
      throw new MojoFailureException(String.format("Branch %s already is configured in releases.json", targetBranch));
		}

    final Release release = new Release();
    release.setTargetBranch(targetBranch);
    release.setStrategy(MergeStrategy.OCTOPUS);
    release.setBranches(new ArrayList<Branch>());

    getReleasesDao().getReleases().getReleases().add(release);

    try {
      getReleasesDao().write();
    }
    catch (final IOException e) {
      throw new MojoFailureException("Error writing releases.json",e);
    }
	}

}
