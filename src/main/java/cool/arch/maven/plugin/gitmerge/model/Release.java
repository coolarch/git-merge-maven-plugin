package cool.arch.maven.plugin.gitmerge.model;

import java.util.List;

public class Release {
	
	private String targetBranch;
	
	private MergeStrategy strategy;
	
	private List<Branch> branches;

	public String getTargetBranch() {
		return targetBranch;
	}

	public void setTargetBranch(String targetBranch) {
		this.targetBranch = targetBranch;
	}

	public MergeStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(MergeStrategy strategy) {
		this.strategy = strategy;
	}

	public List<Branch> getBranches() {
		return branches;
	}

	public void setBranches(List<Branch> branches) {
		this.branches = branches;
	}
}
