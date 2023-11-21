package bpm.vanilla.platform.core.components.forms;

import java.util.Date;

/**
 * This class an HTML form that must be submited by a User.
 * 
 * The IForm can come from 2 origins:
 * 	- A Workflow Process Instance having a Manual activity(FdForm, Orbeon, Google beside i dont know what google is....)
 *  - An instance of "VanillaForm" that need to be validated or submited 
 * 
 * @author ludo
 *
 */
public interface IForm {
	public static enum IFormType{
		Workflow, VanillaFormSubmission, VanillaFormValidation
	}
	
	/**
	 * 
	 * @return the type of IForm
	 */
	public IFormType getType();
	
	/**
	 * 
	 * @return the date since when the form must be submitted
	 */
	public Date getCreatedDate();
	
	/**
	 * 
	 * @return the HTML form url that must be called to access the HTML page of the form
	 */
	public String getHtmlFormUrl();
	
	/**
	 * 
	 * @return the name of the object that created the IForm (VanillaForm Name or Workflow Name)
	 */
	public String getOriginName();
	
	/**
	 * in case of Workflow it is the ActivityId that must be set by this HTML forms
	 * @return
	 */
	public String getFormName();

	/**
	 * the VanillaGroupName responsible to submit the HTML form 
	 * @return
	 */
	public String getVanillaGroupName();
}
