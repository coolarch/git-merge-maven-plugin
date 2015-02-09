package cool.arch.maven.plugin.gitmerge.model;

import java.util.List;

public class Releases {
	
	private String projectName;
	
	private List<Release> releases;
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public List<Release> getReleases() {
		return releases;
	}
	
	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}
}
