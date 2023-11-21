package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class OLAPDirectory extends RepositoryComponent {

	private List<RepositoryComponent> children;

	public OLAPDirectory(RepositoryDirectory dir, List<RepositoryComponent> children) {
		super(dir.getId(), dir.getName(), true,null);
		this.children = children;
	}

	public List<RepositoryComponent> getChildren() {
		return children;
	}

}
