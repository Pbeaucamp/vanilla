package bpm.update.manager.api.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Update implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String version;
	private String description;
	
	private String properties;
	private String scripts;
	
	private Date updateDate;
	private List<String> appNames;
	
	public Update() { }
	
	public Update(String description, String properties,String scripts) {
		this.description = description;
		this.properties = properties;
		this.scripts = scripts;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getProperties() {
		return properties;
	}
	
	public String getScripts() {
		return scripts;
	}
	
	public void setAppNames(List<String> appNames) {
		this.appNames = appNames;
	}
	
	public List<String> getAppNames() {
		return appNames;
	}
	
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public Date getUpdateDate() {
		return updateDate;
	}
}
