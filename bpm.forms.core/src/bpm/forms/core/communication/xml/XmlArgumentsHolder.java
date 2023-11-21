package bpm.forms.core.communication.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Object used to store the XmAction parameters
 * 
 * 
 * @author ludo
 *
 */
public class XmlArgumentsHolder {

	private List<Object> arguments = new ArrayList<Object>();
	
	/**
	 * add arguments in the arguments List
	 * @param argument
	 */
	public void addArgument(Object argument){
		arguments.add(argument);
	}
	
	/**
	 * 
	 * @return all the arguments Presents
	 */
	public List<Object> getArguments(){
		return arguments;
	}
}
