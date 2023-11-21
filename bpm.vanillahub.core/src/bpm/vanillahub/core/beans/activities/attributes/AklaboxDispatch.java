package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;

public class AklaboxDispatch implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String pattern;
	private int aklaboxFolderId;
	private String aklaboxFolderName;
	
	public AklaboxDispatch() {
	}
	
	public AklaboxDispatch(String pattern, int aklaboxFolderId, String aklaboxFolderName) {
		this.pattern = pattern;
		this.aklaboxFolderId = aklaboxFolderId;
		this.aklaboxFolderName = aklaboxFolderName;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Integer getAklaboxFolderId() {
		return aklaboxFolderId;
	}

	public void setAklaboxFolderId(Integer aklaboxFolderId) {
		this.aklaboxFolderId = aklaboxFolderId;
	}

	public String getAklaboxFolderName() {
		return aklaboxFolderName;
	}

	public void setAklaboxFolderName(String aklaboxFolderName) {
		this.aklaboxFolderName = aklaboxFolderName;
	}
}
