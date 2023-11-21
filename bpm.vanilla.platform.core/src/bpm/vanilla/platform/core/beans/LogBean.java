package bpm.vanilla.platform.core.beans;


import java.text.SimpleDateFormat;
import java.util.Date;

//import org.dom4j.DocumentHelper;
//import org.dom4j.Element;

/**
 * this class is a bean to map the datas in the logs table from Vanilla' database
 * data came from execution of BIW or BIG models
 * @author LCA
 *
 */
public class LogBean {
	final public static transient SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss"); 
	
	private long id;
	private Date date;
	private String objectType;
	private String repositoryUrl;
	private long directoryItemId;
	private String stepName;
	private String message;
	private String levelInfo;
	private long runningInstanceId;
	private String stepClassName;
	
	private String runtimeInstanceUrl;
	
	
	
	/**
	 * @return the runtimeInstanceUrl
	 */
	public String getRuntimeInstanceUrl() {
		return runtimeInstanceUrl;
	}
	/**
	 * @param runtimeInstanceUrl the runtimeInstanceUrl to set
	 */
	public void setRuntimeInstanceUrl(String runtimeInstanceUrl) {
		this.runtimeInstanceUrl = runtimeInstanceUrl;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @param date the date to set (yyyy-MM-dd HH:mm:ss format)
	 */
	public void setDate(String date) {
		try{
			this.date = sdf.parse(date);
		}catch(Exception e){
			
		}
		
	}
	
	
	/**
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}
	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	/**
	 * @return the repositoryUrl
	 */
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	/**
	 * @param repositoryUrl the repositoryUrl to set
	 */
	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}
	/**
	 * @return the directoryItemId
	 */
	public long getDirectoryItemId() {
		return directoryItemId;
	}
	/**
	 * @param directoryItemId the directoryItemId to set
	 */
	public void setDirectoryItemId(long directoryItemId) {
		this.directoryItemId = directoryItemId;
	}
	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}
	/**
	 * @param stepName the stepName to set
	 */
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the levelInfo
	 */
	public String getLevelInfo() {
		return levelInfo;
	}
	/**
	 * @param levelInfo the levelInfo to set
	 */
	public void setLevelInfo(String levelInfo) {
		this.levelInfo = levelInfo;
	}
	/**
	 * @return the runningInstanceId
	 */
	public long getRunningInstanceId() {
		return runningInstanceId;
	}
	/**
	 * @param runningInstanceId the runningInstanceId to set
	 */
	public void setRunningInstanceId(long runningInstanceId) {
		this.runningInstanceId = runningInstanceId;
	}
	/**
	 * @return the stepClassName
	 */
	public String getStepClassName() {
		return stepClassName;
	}
	/**
	 * @param stepClassName the stepClassName to set
	 */
	public void setStepClassName(String stepClassName) {
		this.stepClassName = stepClassName;
	}
	
	
//	/**
//	 * 
//	 * @return a dom4j Element
//	 */
//	public Element getElement(){
//		Element e = DocumentHelper.createElement("log");
//		e.addElement("id").setText(id + "");
//		e.addElement("date").setText(sdf.format(date));
//		e.addElement("directoryItemId").setText(directoryItemId + "");
//		e.addElement("levelInfo").setText(levelInfo);
//		e.addElement("message").setText(message);
//		e.addElement("type").setText(objectType);
//		e.addElement("repositoryUrl").setText(repositoryUrl);
//		e.addElement("instanceId").setText(runningInstanceId + "");
//		e.addElement("stepName").setText(stepName);
//		e.addElement("stepClassName").setText(stepClassName);
//		e.addElement("runtimeServerUrl").setText(runtimeInstanceUrl);
//		return e;
//		
//	}
	
}
