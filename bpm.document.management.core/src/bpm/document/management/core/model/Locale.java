package bpm.document.management.core.model;

import java.io.Serializable;

public class Locale implements Serializable{
	private static final long serialVersionUID = 1L;

	private int localeId=0;
	private String localeName="";
	private String localeDescription="";
	private String localeExtension="";
	
	public int getLocaleId() {
		return localeId;
	}
	public void setLocaleId(int localeId) {
		this.localeId = localeId;
	}
	public String getLocaleName() {
		return localeName;
	}
	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}
	public String getLocaleDescription() {
		return localeDescription;
	}
	public void setLocaleDescription(String localeDescription) {
		this.localeDescription = localeDescription;
	}
	public String getLocaleExtension() {
		return localeExtension;
	}
	public void setLocaleExtension(String localeExtension) {
		this.localeExtension = localeExtension;
	}
}
