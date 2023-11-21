package bpm.forms.core.design;

/**
 * This class represent a mapping between a form fields (the form provided by
 * an FdForm, or a JSP, HTML) and a DataBase physical column to persist the Data
 * from an IFormDefinition
 *
 *  IFormFieldMapping is linked to a ITagetTable and a IFormDefinition
 *  
 * @author ludo
 *
 */
public interface IFormFieldMapping {
	
	/**
	 * 
	 * @return the id the Database of this IFormFieldMapping
	 */
	public long getId();
	
	/**
	 * 
	 * @return true if this field can have more than one data persisted for the same instance
	 */
	public boolean isMultiValued();
	
	/**
	 * 
	 * @return a simple Label
	 */
	public String getFormFieldName();
	
	/**
	 * 
	 * @return the description of this IFormField
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return the Id in the UI(HTML, JSP, FdForm)
	 */
	public String getFormFieldId();
	
	/**
	 * 
	 * @return the name of the Column which will tore the persisted data in the application DataBase 
	 */
	public String getDatabasePhysicalName();
	
	/**
	 * 
	 * @return the IFormDefinition id for this IFormFieldMapping
	 */
	public long getFormDefinitionId();
	
	
	/**
	 * 
	 * @return the ITargetTableDefinition id for this IFormFieldMapping
	 */
	public Long getTargetTableId();
	
	/**
	 * 
	 * @return the Sql Type Name for this IFormFieldMapping
	 */
	public String getSqlDataType();

//	public void setTargetTableId(long id);

	public void setDatabasePhysicalName(String s);

	public void setIsMultivalued(boolean value);

	public void setDescription(String value);

	public void setSqlDataType(String value);

	public void setTargetTableId(Long object);

	public void setFormFieldId(String s);

	public void setFormDefinitionId(long id);
	
	
}
