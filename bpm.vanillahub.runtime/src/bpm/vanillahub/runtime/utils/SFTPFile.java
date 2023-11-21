package bpm.vanillahub.runtime.utils;

import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SFTPFile {

	private String parentFolder;
	private LsEntry file;
	
	public SFTPFile(String parentFolder, LsEntry file) {
		this.parentFolder = parentFolder;
		this.file = file;
	}
	
	public boolean isDirectory() {
		return file != null && file.getAttrs().isDir();
	}
	
	public String getParentFolder() {
		return parentFolder;
	}
	
	public LsEntry getFile() {
		return file;
	}
}
