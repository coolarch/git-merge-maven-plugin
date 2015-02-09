/**
 * 
 */
package cool.arch.maven.plugin.gitmerge.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 *
 */
public class CreateMojoTest {
	
	@Mock
	private MavenProject mockMavenProject;
	
	private CreateMojo specimen;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		specimen = new CreateMojo();
		specimen.setProject(mockMavenProject);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for {@link cool.arch.maven.plugin.gitmerge.mojo.CreateMojo#execute()}.
	 * @throws MojoFailureException 
	 * @throws MojoExecutionException 
	 */
	@Test
	public final void testExecute() throws MojoExecutionException, MojoFailureException {
		specimen.execute();
	}
}
