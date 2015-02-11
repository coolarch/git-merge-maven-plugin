package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import cool.arch.maven.plugin.gitmerge.model.Release;

/**
 *
 */
@Mojo(name="remove", requiresOnline=true, requiresProject=true)
public class RemoveMojo extends AbstractGitMergeMojo {

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

		if (!releasesContainsBranch(targetBranch)) {
      throw new MojoFailureException(String.format("Branch %s is not configured in releases.json", targetBranch));
		}

		Release foundRelease = null;

		for (final Release release : getReleasesDao().getReleases().getReleases()) {
		  if (targetBranch.equals(release.getTargetBranch())) {
		    foundRelease = release;
		    break;
		  }
		}

		if (foundRelease != null) {
		  getReleasesDao().getReleases().getReleases().remove(foundRelease);

		  try {
		    getReleasesDao().write();
      }
      catch (final IOException e) {
        throw new MojoFailureException("Error reading releases.json file");
      }
		}
	}
}
