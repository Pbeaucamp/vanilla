package bpm.forms.core.design;

import java.util.Date;
import java.util.List;

/**
 * Common interface to define a IFormDefinition
 * 
 * 
 * A IFormDefinition contains the physical submission From description(JSP,HTML, URL,
 * vanillaFdForm), the mapping between the physicalForm and the database table target
 * 
 * 
 * if IFormDefinition.isDesigned() == true : 
 *  - the IFormDefinition cannot be updated (cannot be instanciated too)
 * if IFormDefinition.isActivated() == false :
 *  - this IFormDefinition wont be able to be instanciated
 *  
 *   the start/stop date allow to define a time range  :
 *   if an instanciation on this IformDefinition Iform's is asked and the date is inside the range
 *   this IFormDefinition will be used.
 *   if no start/stop and this IFormDefinition can be instanciated, it will be too
 *   
 * 
 * @author ludo
 *
 */
public interface IFormDefinition {

	
	
	/**
	 * 
	 * @return the IFormDefintion Id
	 */
	public long getId();
	
		
	
	/**
	 * 
	 * @return the formId
	 * 
	 * the form is the same for all version of the same form
	 */
	public long getFormId();
	
	/**
	 * 
	 * @return the IFormDefintion Version Number
	 */
	public int getVersion();
	
	
	
	/**
	 * 
	 * @return the Date from when this version of the Form is used
	 */
	public Date getStartDate();
	
	/**
	 * 
	 * @return the Date after when this version of the Form is deprecated and 
	 * cannot be instantiated
	 */
	public Date getStopDate();
	
	/**
	 * 
	 * @return true if this version is the one that will be instantiated
	 */
	public boolean isActivated();
	
	public void setActivated(boolean v);
	
	/**
	 * 
	 * @return false if this IFormDefinition is not yet ready to be used
	 */
	public boolean isDesigned();
	
	public void setDesigned(boolean value);
	
	/**
	 * 
	 * @return a description of this FormDefinition
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return all the ITargetTable linked to this IFormDefinition
	 */
	public List<ITargetTable> getITargetTables() ;
	
	
	/**
	 * 
	 * @return all the IFormFieldMapping for this IFormDefinition
	 */
	public List<IFormFieldMapping> getIFormFieldMappings();
	
	/**
	 * 
	 * @return the creation Date of the IFormDefinition
	 */
	public Date getCreationDate();
	
	/**
	 * 
	 * @return the UI definition used at runtime
	 */
	public IFormUi getFormUI();

	
	public void addTargetTable(ITargetTable o);

	public void removeTargetTable(ITargetTable o);

	public void removeFormFieldMapping(IFormFieldMapping f);

	public void addFormFieldMapping(IFormFieldMapping field);

	public void setIsDesigned(boolean value);

	public void setFormId(long id);



	public void setVersion(int i);



	public void setFormUI(IFormUi ui);



	public void setCreationDate(Date time);



	public void setId(long l);
	
	
	
	
	
}
