package bpm.vanilla.workplace.core.xstream;



/**
 * a Simple class to represent an Action to be performed by the server side of the vanilla forms
 * 
 * The server side is implemented by the plugin bpm.forms.runtime and contains Servlets
 * Each servlets will received orders by the form of an serialized XmlAction and rebuild the XmAction object from teh XML
 * 
 * @author ludo
 *
 */
public class XmlAction {
	/**
	 * The type of action for the servlets
	 * @author ludo
	 *
	 */
		
	private XmlArgumentsHolder argumentsHolder;
	private IXmlActionType actionType;
	
	public XmlAction(){
		
	}
	
	/**
	 * 
	 * @param arguments : object will contains all the parameters objects for the action
	 * @param actionType : the action type
	 */
	public XmlAction(XmlArgumentsHolder arguments, IXmlActionType actionType) {
		super();
		this.argumentsHolder = arguments;
		this.actionType = actionType;
	}
	/**
	 * 
	 * @return the arguments
	 */
	public XmlArgumentsHolder getArguments() {
		return argumentsHolder;
	}
	
	/**
	 * 
	 * @param arguments 
	 * set the argumnets of the action
	 */
	public void setArguments(XmlArgumentsHolder arguments) {
		this.argumentsHolder = arguments;
	}
	/**
	 * 
	 * @return the ActionType 
	 */
	public IXmlActionType getActionType() {
		return actionType;
	}
	
	/**
	 * 
	 * @param actionType
	 */
	public void setActionType(IXmlActionType actionType) {
		this.actionType = actionType;
	}
	
	
	
}
