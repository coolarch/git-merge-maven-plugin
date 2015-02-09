package cool.arch.maven.plugin.gitmerge.mojo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

@Mojo(name="merge", requiresOnline=true, requiresProject=true)
public class MergeMojo extends AbstractGitMergeMojo {

	@Component
	private MavenProject project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final FileRepositoryBuilder builder = new FileRepositoryBuilder();

		try {
			final Repository repository = builder.readEnvironment().findGitDir().build();

			final File repoRoot = repository.getDirectory();

			final List<Ref> call = new Git(repository).branchList().setListMode(ListMode.ALL).call();

			for (final Ref ref : call) {
				System.out.println("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
			}

			repository.close();
		} catch (final IOException e) {
			throw new MojoFailureException("", e);
		} catch (final GitAPIException e) {
			throw new MojoFailureException("", e);
		}
	}
}
