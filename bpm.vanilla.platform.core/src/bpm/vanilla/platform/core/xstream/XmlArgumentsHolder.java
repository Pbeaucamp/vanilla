package bpm.vanilla.platform.core.xstream;

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
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		for(Object o : arguments) {
			try {
				buf.append(" , " + o.toString());
			} catch (NullPointerException e) {
				buf.append(" , null");
			}
		}
		return buf.toString();
	}
}
