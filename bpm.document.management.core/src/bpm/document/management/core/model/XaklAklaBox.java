package bpm.document.management.core.model;

import java.io.Serializable;

public class XaklAklaBox implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int xaklAklaBoxId=0;
	private int xaklFilesId=0;
	private String filePath="";
	
	public int getXaklAklaBoxId() {
		return xaklAklaBoxId;
	}
	public void setXaklAklaBoxId(int xaklAklaBoxId) {
		this.xaklAklaBoxId = xaklAklaBoxId;
	}
	public int getXaklFilesId() {
		return xaklFilesId;
	}
	public void setXaklFilesId(int xaklFilesId) {
		this.xaklFilesId = xaklFilesId;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
