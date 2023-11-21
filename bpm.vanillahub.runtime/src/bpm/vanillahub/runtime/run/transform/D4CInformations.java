package bpm.vanillahub.runtime.run.transform;

public class D4CInformations {

	private boolean isMainResource;
	private boolean isFile;
	private String format;
	
	public D4CInformations(boolean isMainResource, boolean isFile, String format) {
		this.isMainResource = isMainResource;
		this.isFile = isFile;
		this.format = format;
	}
	
	public boolean isMainResource() {
		return isMainResource;
	}
	
	public boolean isFile() {
		return isFile;
	}
	
	public String getFormat() {
		return format;
	}
}
