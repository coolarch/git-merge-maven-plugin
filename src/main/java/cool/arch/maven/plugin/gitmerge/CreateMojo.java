package cool.arch.maven.plugin.gitmerge;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

@Mojo(name="merge", requiresOnline=true, requiresProject=true)
public class CreateMojo extends AbstractMojo {
	
	private static final String RELEASES_JSON = "releases.json";
	
	private static final String NO_DOT_GIT_MESSAGE = "No .git directory found! This project is not managed by Git.";
	
	@Component
	private MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final File baseDirectory = discoverRepoDirectory();
		
		if (baseDirectory == null || !baseDirectory.exists()) {
			getLog().error(NO_DOT_GIT_MESSAGE);
			
			throw new MojoFailureException(NO_DOT_GIT_MESSAGE);
		}
		
		if (!baseDirectory.equals(project.getBasedir())) {
			getLog().debug("Executing on child module.  Not executings");
			return;
		}
		
		final File releasesJson = new File(baseDirectory, RELEASES_JSON);
	}
	
	private File discoverRepoDirectory() throws MojoFailureException {
		File baseDirectory = null;
		Repository repository = null;
		
		try {
			repository = new FileRepositoryBuilder().readEnvironment().findGitDir().build();
			baseDirectory = repository.getDirectory();
		} catch (IOException e) {
			throw new MojoFailureException("", e);
		} finally {
			if (repository != null) {
				repository.close();
			}
		}
		
		return baseDirectory;
	}
}
