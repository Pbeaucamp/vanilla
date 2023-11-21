package bpm.workflow.runtime.model;

/**
 * Interface of the forms
 * @author CHARBONNIER, MARTIN
 *
 */
public interface IManual {
	
	/**
	 * Set the group's name for the validation
	 * @param group
	 */
	public void setGroupForValidation(String group);
	/**
	 * 
	 * @return the group which will have to validate the form
	 */
	public String getGroupForValidation();
	/**
	 * 
	 * @return the name of the form
	 */
	public String getName();
	/**
	 * 
	 * @return the id of the directory item 
	 */
	public String getId();

}
