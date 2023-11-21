package bpm.vanilla.platform.core.components.forms;

import java.util.Date;
/**
 * 
 * @author ludo
 *
 */
public class Form implements IForm{
	private IFormType type ;
	private Date createdDate;
	private String htmlFormUrl;
	private String originName;
	private String formName;
	private String vanillaGroupName;
	
	/**
	 * @return the formName
	 */
	public String getFormName() {
		return formName;
	}
	/**
	 * @param formName the formName to set
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}
	/**
	 * @return the type
	 */
	public IFormType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(IFormType type) {
		this.type = type;
	}
	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the htmlFormUrl
	 */
	public String getHtmlFormUrl() {
		return htmlFormUrl;
	}
	/**
	 * @param htmlFormUrl the htmlFormUrl to set
	 */
	public void setHtmlFormUrl(String htmlFormUrl) {
		this.htmlFormUrl = htmlFormUrl;
	}
	/**
	 * @return the originName
	 */
	public String getOriginName() {
		return originName;
	}
	/**
	 * @param originName the originName to set
	 */
	public void setOriginName(String originName) {
		this.originName = originName;
	}
	public void setVanillaGroupName(String vanillaGroupName) {
		this.vanillaGroupName = vanillaGroupName;
		
	}
	public String getVanillaGroupName(){
		return this.vanillaGroupName;
	}
	
	
}
