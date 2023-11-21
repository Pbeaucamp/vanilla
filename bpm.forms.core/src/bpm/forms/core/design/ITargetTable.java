package bpm.forms.core.design;
/**
 * Represent a TargetTable for a Form.
 * Each form can have more than one ITargetTable.
 * 
 * ITargetTable are used to store the data from a IFormInstance
 * 
 * @author ludo
 *
 */
public interface ITargetTable {

	/**
	 * 
	 * @return the ITargetTable name 
	 */
	public String getName();
	
	/**
	 * 
	 * @return the ITargetTable id 
	 */
	public long getId();
	
	/**
	 * 
	 * @return the ITargetTable description
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return the full name of the ITargetTable in the 
	 * application DataBase
	 */
	public String getDatabasePhysicalName();

	public void setDatabasePhysicalName(String text);

	public void setDescription(String text);

	public void setName(String text);

	public void setId(long id);
}
