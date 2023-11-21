package bpm.forms.core.design;

import java.util.List;
/**
 * This iintyerface represent the physical form informations :
 *  - all datas requested to access/build the physicall form are stored in it sproperties
 *  
 * @author ludo
 *
 */
public interface IFormUi {

	public long getId();
	
	public String getName(); 
	
	public long getFormDefinitionId();
	
	public List<IFormUIProperty> getProperties();
	
	public IFormUIProperty getProperty(String propertyName);
	
	public String getPropertyValue(String propertyName);
	
	public void setProperty(String propertyName, String propertyValue);

	public void setFormDefinitionId(long id);

	public void setName(String name);
}
