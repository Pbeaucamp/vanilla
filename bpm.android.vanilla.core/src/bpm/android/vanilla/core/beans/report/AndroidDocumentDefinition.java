package bpm.android.vanilla.core.beans.report;

import java.io.Serializable;

public class AndroidDocumentDefinition implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	private String title;
	private String version;
	private String summary;
	
	public AndroidDocumentDefinition(){
		
	}

	public AndroidDocumentDefinition(int id, String title, String version, String summary){
		this.title = title;
		this.id = id;
		this.version = version;
		this.summary = summary;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String toString(){
		return "Title : "+title;	
	}
}
