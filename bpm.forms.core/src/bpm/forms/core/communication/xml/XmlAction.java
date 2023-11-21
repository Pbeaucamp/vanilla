package bpm.forms.core.communication.xml;


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
	public enum ActionType{
		//definition actions
		DEF_DELETE, DEF_ACTIVE_FORM, DEF_COLUMNS_FOR_TT, DEF_FORMS, DEF_FORM_DEF, DEF_FORM_VERS, DEF_TARGET_TABLE, DEF_SAVE, DEF_UPDATE,
		//instance actions
		INST_DELETE, INST_FIELD_STATE, INST_GETTOSUBMIT, INST_GETTOVALIDATE, INST_RUNNING, INST_SAVE, INST_UPDATE, INST_VALIDATE, INST_INVALIDATE,
		
		//launch actions
		LAU_INSTANCIATE
	}
	
	private XmlArgumentsHolder arguments;
	private ActionType actionType;
	
	/**
	 * 
	 * @param arguments : object will contains all the parameters objects for the action
	 * @param actionType : the action type
	 */
	public XmlAction(XmlArgumentsHolder arguments, ActionType actionType) {
		super();
		this.arguments = arguments;
		this.actionType = actionType;
	}
	/**
	 * 
	 * @return the arguments
	 */
	public XmlArgumentsHolder getArguments() {
		return arguments;
	}
	
	/**
	 * 
	 * @param arguments 
	 * set the argumnets of the action
	 */
	public void setArguments(XmlArgumentsHolder arguments) {
		this.arguments = arguments;
	}
	/**
	 * 
	 * @return the ActionType 
	 */
	public ActionType getActionType() {
		return actionType;
	}
	
	/**
	 * 
	 * @param actionType
	 */
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	
	
	
}
