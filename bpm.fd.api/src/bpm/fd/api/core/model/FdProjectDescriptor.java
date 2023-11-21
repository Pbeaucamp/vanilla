package bpm.fd.api.core.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class FdProjectDescriptor {
	protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public  static final String API_DESIGN_VERSION = "v 2.0";
	private String projectName = "projectName";
	private String projectVersion = "1.0";
	private String location ="";
	private String dictionaryName = "new dictionary";
	private String modelName = "new model";
	private String author = "";
	private Date creation = Calendar.getInstance().getTime();
	private String description = "";
	
	private String internalApiDesignVersion;
	
	
	
	/**
	 * @return the internalApiDesignVersion
	 */
	public String getInternalApiDesignVersion() {
		return internalApiDesignVersion;
	}
	/**
	 * @param internalApiDesignVersion the internalApiDesignVersion to set
	 */
	public void setInternalApiDesignVersion(String internalApiDesignVersion) {
		this.internalApiDesignVersion = internalApiDesignVersion;
	}
	/**
	 * @return the dictionaryName
	 */
	public String getDictionaryName() {
		return dictionaryName;
	}
	/**
	 * @param dictionaryName the dictionaryName to set
	 */
	public void setDictionaryName(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the projectVersion
	 */
	public String getProjectVersion() {
		return projectVersion;
	}
	/**
	 * @param projectVersion the projectVersion to set
	 */
	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	
	public void setDescription(String text) {
		this.description = text;
		
	}
	public void setAuthor(String text) {
		this.author = text;
		
	}
	public void setCreation(Date time) {
		this.creation = time;
		
	}
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @return the creation
	 */
	public Date getCreation() {
		return creation;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	public Element getElement() {
		Element e = DocumentHelper.createElement("projectDescriptor");
		e.addElement("projectName").setText(getProjectName());
		e.addElement("projectVersion").setText(getProjectVersion());
		e.addElement("dictionaryName").setText(getDictionaryName());
		e.addElement("modelName").setText(getModelName());
		e.addElement("author").setText(getAuthor());
		e.addElement("creation").setText(sdf.format(getCreation()));
		e.addElement("description").setText(getDescription());
		if (getInternalApiDesignVersion() != null){
			e.addElement("internalApiDesignVersion").setText(getInternalApiDesignVersion());
		}
		
		return e;
	}
	public void setCreation(String text) {
		try{
			creation = sdf.parse(text);
		}catch(Exception e){
			
		}
		
		
	}
	

	
	
	
	
	
}

