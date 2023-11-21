package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

public class FileInformations implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String path;
	private long size;
	
	public FileInformations() {
	}
	
	public FileInformations(String path, long size) {
		this.path = path;
		this.size = size;
	}
	
	public String getPath() {
		return path;
	}
	
	public long getSize() {
		return size;
	}
}
