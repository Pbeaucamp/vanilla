package bpm.vanilla.platform.core.beans.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * simple class to provide a List<VanillaGroupParameter> to create a IRuntimeCOnfig
 * from a simple HashMap
 * @author ludo
 *
 */
public class VanillaParameterCreator {

	/**
	 * convert a simple HashMap into List<VanillaGroupParameter>
	 * 
	 * each key having a non null value will generate a different VanillaGroupParameter
	 * holding a unique VanillaParameter named by this key with a single selectedValue
	 * which is the map value for this key
	 * @param parameterValues
	 * @return
	 */
	public static List<VanillaGroupParameter> createVanillaGroupParameters(HashMap<String, String> parameterValues){
		
		List<VanillaGroupParameter> vanillaParams = new ArrayList<VanillaGroupParameter>();
		for(String s : parameterValues.keySet()){
			
			String value = parameterValues.get(s);
			if (value == null){
				continue;
			}
			VanillaGroupParameter paramGroup = new VanillaGroupParameter();
			VanillaParameter p = new VanillaParameter();
			p.setName(s);
			p.addSelectedValue(value);
			try{
				paramGroup.addParameter(p);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			vanillaParams.add(paramGroup);
			
		}
		return vanillaParams;
	}
}
