package bpm.vanilla.platform.core.beans.ged;

import java.util.List;


/**
 * specific to get
 * @author manu heck no!
 *
 */
public interface IComProperties {
	
	public void setProperty(Definition definition, String value);
	
	public void setValues(Definition definition, List<String> values) throws Exception;
	
	public String getValueForField(String pname);

	public List<String> getValuesForField(String name);
	
	public List<String> getKeys();
	
	public boolean isMultivalued(String pname);
}
