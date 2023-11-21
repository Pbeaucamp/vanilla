package bpm.document.management.core.model;

import java.io.Serializable;

public class ExtractZip implements Serializable{

	private static final long serialVersionUID = 1L;
	private String filePath="";
	private String folderName="";
	private String parentPath="";
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public String getParentPath() {
		return parentPath;
	}
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}
}
