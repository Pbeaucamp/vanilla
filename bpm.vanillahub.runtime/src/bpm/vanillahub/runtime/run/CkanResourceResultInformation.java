package bpm.vanillahub.runtime.run;

public class CkanResourceResultInformation implements IResultInformation {

	private boolean isMainResource;
	private boolean isFile;
	private String format;
	
	public CkanResourceResultInformation(boolean isMainResource, boolean isFile, String format) {
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
