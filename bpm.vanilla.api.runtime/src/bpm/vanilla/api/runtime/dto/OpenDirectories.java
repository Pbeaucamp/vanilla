package bpm.vanilla.api.runtime.dto;

import java.util.List;

public class OpenDirectories {
	private List<Integer> openedDirs;
	private boolean openAll;

	public List<Integer> getOpenedDirs() {
		return openedDirs;
	}

	public void setOpenedDirs(List<Integer> openedDirs) {
		this.openedDirs = openedDirs;
	}

	public boolean isOpenAll() {
		return openAll;
	}

	public void setOpenAll(boolean openAll) {
		this.openAll = openAll;
	}
	
	
	
}
