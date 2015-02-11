package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 */
@Mojo(name="create", requiresOnline=true, requiresProject=true)
public class CreateMojo extends AbstractGitMergeMojo {

	/**
	 * Executes the Maven Mojo.
	 */
	@Override
	public void executeWork() throws MojoExecutionException, MojoFailureException {
	  getReleasesDao().reset();

		try {
      getReleasesDao().write();
    }
    catch (final IOException e) {
      throw new MojoFailureException("Error writing releases.json file", e);
    }
	}
}
