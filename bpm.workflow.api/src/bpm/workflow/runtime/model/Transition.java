package bpm.workflow.runtime.model;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Transition between two nodes
 * @author CHARBONNIER, MARTIN
 *
 */
public class Transition {
	private IActivity source;
	private IActivity target;
	
	private Condition  condition;
	private String sourceRefId;
	private String targetRefId;
	private String color;
	/**
	 * do not use, only for XML parsing
	 */
	public Transition(){
		
	}
	
	/**
	 * 
	 * @return the color of the transition
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Set the color of the transition
	 * @param color
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Create a transition between the two activities
	 * @param source : activity
	 * @param target : activity
	 */
	public Transition(IActivity source, IActivity target){
		this.source = source;
		this.target = target;
		
	}
	
	/**
	 * 
	 * @return the source activity of this transition
	 */
	public IActivity getSource() {
		return source;
	}

	/**
	 * Set the source activity of this transition
	 * @param source
	 */
	public void setSource(IActivity source) {
		this.source = source;
	}

	
	
	/**
	 * used only for XML parsing
	 * @param sourceRef
	 */
	public void setSourceRef(String sourceRef){
		this.sourceRefId = sourceRef.replaceAll("[^a-zA-Z0-9]", "");
	}
	

	/**
	 * used only for XML parsing
	 * @param sourceRef
	 */
	public void setTargetRef(String targetRef){
		this.targetRefId = targetRef.replaceAll("[^a-zA-Z0-9]", "");
	}
	
	
	/**
	 * 
	 * @return the target activity of this transition
	 */
	public IActivity getTarget() {
		return target;
	}

	/**
	 * Set the target activity of thsi transition
	 * @param target
	 */
	public void setTarget(IActivity target) {
		this.target = target;
	}

	/**
	 * 
	 * @return the condition of the transition
	 */
	public Condition getCondition() {
		if(condition == null) {
			condition = new Condition();
		}
		return condition;
	}

	/**
	 * Set the condition of the transition
	 * @param condition
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	
	/**
	 * 
	 * @return the label of the condition of the transition
	 */
	public String getLabelCondition() {
		return this.condition.getLabelCondition();
	}
	
	public Element getXPDL(){
		Element tr = DocumentHelper.createElement("Transition");
		tr.addAttribute("Id", source.getId() + "_" + target.getId());
		tr.addAttribute("Name", source.getId() + "_" + target.getId());
		tr.addAttribute("From", source.getId());
		tr.addAttribute("To", target.getId());
		
		if (condition != null){
			Element condition = tr.addElement("Condition");
			condition.addAttribute("Type", "CONDITION");
			condition.setText(this.condition.getLabelCondition());
		}
		
		
		return tr;
	}

	public Element getXmlNode() {
		Element e = DocumentHelper.createElement("transition");
		e.addElement("sourceRef").setText(source.getId());
		e.addElement("targetRef").setText(target.getId());
		
		if (condition != null){
			e.add(condition.getXmlNode());
		}
		if(color != null){
			e.addElement("color").setText(color);
		}
		
		return e;
	}

	
	
	/**
	 * do not use
	 * only for rebuilding model after parsing its XML representation
	 * @param model the model containing this transitions, and his activities
	 * @throws WorkflowException if the references to the source/target activyties id cannot be resolved in the model
	 */
	public void rebuildReferences(WorkflowModel model) throws WorkflowException{
		IActivity a = model.getActivity(sourceRefId);
		
		if (a == null){
			throw new WorkflowException(" unable to rebuild Transaction because the activity id " + sourceRefId + " cannot be found in the model");
		}
		
		setSource(a);
		a = model.getActivity(targetRefId);
		
		if (a == null){
			throw new WorkflowException(" unable to rebuild Transaction because the activity id " + targetRefId + " cannot be found in the model");
		}
		
		setTarget(a);
		
		if (!source.getTargets().contains(target)){
			source.addTarget(target);
		}
		
		if (!target.getSources().contains(source)){
			target.addSource(source);
		}
	}

	
	public void disconnect() {
		source.deleteTarget(target);
		target.deleteSource(source);
		
	}

	/**
	 * 
	 * @return the name of the transition
	 */
	public String getName() {
		String n = this.getSource().getName() + "_"  + this.getTarget().getName();
		return n;
	}
}
