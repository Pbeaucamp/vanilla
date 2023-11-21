package bpm.forms.core.design;

import java.util.Date;
import java.util.List;

/**
 * This interface represent a VanillaForm.
 * A Form is a logical entity on which you will define versions that will be associated
 * with IFormUi for their submission, ITargetTable to store the submited datas in a dataBase.
 * 
 * 
 * IForm can require validation after submission before inserting datas in the database.
 * 
 *  Once a IFormInstance is submited, the datas are stored temporary in a specific table.
 *  Once validated, the datas are stored in the  ItargetTables.
 *  
 *  An IForm without Validators group perform a Validation when it is submitted.
 *  
 * 
 * @author ludo
 *
 */
public interface IForm {

	/**
	 * 
	 * @return the IForm Name
	 */
	public String getName();
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name);
	/**
	 * 
	 * @return the IFormDefintion Id
	 */
	public long getId();
	
	
	/**
	 * 
	 * @return the creation Date of the IForm
	 */
	public Date getCreationDate();
	
	public void setCreationDate(Date creationDate);
	
	/**
	 * 
	 * @return the name of the form creator
	 */
	public String getCreatorName();
	
	public void setCreatorName();
	
	/**
	 * 
	 * @return the id of the form's creator in the Vanilla DB
	 */
	public int getCreatorId();
	
	public void setCreatorId(int creatorId);
	
	public String getDescription();
	
	public void setDescription(String description);
	
	/**
	 * 
	 * @return all the IFormDefinition for this IForm
	 */
	public List<IFormDefinition> getDefinitionsVersions();

	/**
	 * add a IFormDefinition
	 * The IFormDefinition is not automatically added to the FormStorageSystem
	 * @param fd
	 */
	public void addDefinition(IFormDefinition fd);
	
	/**
	 * remove a IFormDefinition
	 * The IFormDefinition is not automatically removed to the FormStorageSystem
	 * @param fd
	 */
	public void removeDefinition(IFormDefinition fd);
	
	/**
	 * 
	 * @return the Rules of Instanciation of the IForm.
	 * IForm can be created Manually but can also be Scheduled by the Vanilla BPMN.
	 * 
	 */
	public IInstanciationRule getInstanciationRule();
	
	/**
	 * 
	 * @return the List of the Vanilla Groups Id that can perform a Validation ona submitted IFormInstance
	 */
	public List<Integer> getValidatorGroups();

	
	public void addValidationGroup(Integer groupid);

	public void removeValidationGroup(Integer groupid);
	
	/**
	 * 
	 * @return The number of hours between the creation of an IformInstance and the time when this IFormInstance should be submited
	 */
	public int getLifeExpectancyHours();
	
	public void setLifeExpectancyHours(int lifeExpectancyHours);
	
	/**
	 * 
	 * @return The number of days between the creation of an IformInstance and the time when this IFormInstance should be submited
	 */
	public int getLifeExpectancyDays();
	
	public void setLifeExpectancyDays(int lifeExpectancyDays);
	
	/**
	 * 
	 * @return The number of months between the creation of an IformInstance and the time when this IFormInstance should be submited
	 */
	public int getLifeExpectancyMonths();
	
	public void setLifeExpectancyMonths(int lifeExpectancyMonths);
	
	/**
	 * 
	 * @return The number of years between the creation of an IformInstance and the time when this IFormInstance should be submited
	 */
	public int getLifeExpectancyYears();
	
	public void setLifeExpectancyYears(int lifeExpectancyYears);

	
	public void setId(long l);
}
