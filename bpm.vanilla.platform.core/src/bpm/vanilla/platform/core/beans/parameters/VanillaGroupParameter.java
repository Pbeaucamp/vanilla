package bpm.vanilla.platform.core.beans.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to Group Parameters. A group of VanillaParameter means that they are all linked together.
 * The order of the VanillaParameters is the order of dependancies.
 *  - Means P0 need no datas to retrieve its values
 *  - P1 need P0 set to get its values
 *  - P(n) need P(n-1)
 *  
 *  You have to call addParameter in the right order.
 *  
 *  You can replace a VanillaParameter.
 *  
 *  Each VanillaParameter within the VanillaGroup must have a unique Name.
 *  
 *  Multiple dependencies is not supported.
 * 
 * 
 * @author ludo
 *
 */
public class VanillaGroupParameter implements Serializable {

	private static final long serialVersionUID = 3532683413731927311L;
	
	private String name;
	private String promptText;
	private String displayName;
	private List<VanillaParameter> parameters = new ArrayList<VanillaParameter>();
	private boolean isCascadingGroup = false;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setParameters(List<VanillaParameter> parameters) {
		this.parameters = parameters;
	}

	public List<VanillaParameter> getParameters() {
		return parameters;
	}
	
	/**
	 * 
	 * @param parameter
	 */
	public void addParameter(VanillaParameter parameter) throws Exception{
		if (parameter != null){
			
			for(VanillaParameter p : getParameters()){
				if (p.getName().equals(parameter.getName())){
					throw new Exception("A Parameter named " + parameter.getName() + " is already present in the ParameterGroup" );
				}
			}
			
			this.parameters.add(parameter);
		}
	}
	
	/**
	 * replace the VanillaParameter with the same name.
	 * If no VanillaParameter has the same name, it does nothing 
	 * @param parameter
	 */
	public void replace(VanillaParameter parameter){
		for(int i = 0; i < this.parameters.size(); i++){
			if (this.getParameters().get(i).getName().equals(parameter.getName())){
				
				this.parameters.set(i, parameter);
				break;
			}
		}
	}

	public void setCascadingGroup(boolean isCascadingGroup) {
		this.isCascadingGroup = isCascadingGroup;
	}

	public boolean isCascadingGroup() {
		return isCascadingGroup;
	}
}
