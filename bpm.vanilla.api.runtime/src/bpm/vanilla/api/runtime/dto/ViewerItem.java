package bpm.vanilla.api.runtime.dto;

import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ViewerItem extends RepositoryComponent {
	
	private int directoryId;
	
	public ViewerItem(RepositoryItem it,String type) throws Exception {
		super(it.getId(), it.getName(), false,type);
		this.directoryId = it.getDirectoryId();
	}

	public int getDirectoryId() {
		return directoryId;
	}
	
}
