package bpm.vanilla.platform.core.beans.resources;

public class LimeSurveyFile {

	private String title;
	private String name;
	private String size;
	private String internalFileName;
	private String format;
	private byte[] content;
	
	public LimeSurveyFile() {
	}

	public LimeSurveyFile(String title, String name, String size, String internalFileName, String format, byte[] content) {
		this.title = title;
		this.name = name;
		this.size = size;
		this.internalFileName = internalFileName;
		this.format = format;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getInternalFileName() {
		return internalFileName;
	}

	public void setInternalFileName(String internalFileName) {
		this.internalFileName = internalFileName;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}
