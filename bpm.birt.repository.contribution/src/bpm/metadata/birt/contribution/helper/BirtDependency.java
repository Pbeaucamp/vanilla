package bpm.metadata.birt.contribution.helper;

import java.io.File;

public class BirtDependency {

	private boolean hasError = false;
	private String errmsg = "";
	private String fileName;
	private File depFile;
	
	private boolean isSharedResource;
	
	public BirtDependency(boolean hasError, String fileName, File depFile, String errmsg, boolean isSharedResource) {
		this.hasError = hasError;
		this.fileName = fileName;
		this.depFile = depFile;
		this.errmsg = errmsg;
		this.isSharedResource = isSharedResource;
	}
	
	public File getDepFile() {
		return depFile;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public boolean hasError() {
		return hasError;
	}
	
	public String getErrmsg() {
		return errmsg;
	}
	
	public boolean isSharedResource() {
		return isSharedResource;
	}
}
