package bpm.forms.core.design;

/**
 * simple interface to store a Property of a IFormUi
 * @author ludo
 *
 */
public interface IFormUIProperty {

	/**
	 * @return the formDefinitionId
	 */
	public long getFormDefinitionId();

	/**
	 * @return the formUiName
	 */
	public String getFormUiName() ;
	
	/**
	 * @return the id
	 */
	public long getId();
	
	/**
	 * @return the propertyName
	 */
	public String getPropertyName();
	
	/**
	 * @return the propertyValue
	 */
	public String getPropertyValue() ;

	
	public void setPropertyValue(String value);

	public void setFormUiName(String name);

	public void setFormDefinitionId(long l);

	public void setId(long id);
	
}
