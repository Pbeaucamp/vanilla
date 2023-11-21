package bpm.gateway.ui.gef.model;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.transformations.GlobalDefinitionInput;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;

public class GIDModel extends Node{

//	private int x;
//	private int y;
	
	private List<Node> contents = new ArrayList<Node>();
	
	private int width;
	private int height;
	
//	public static final String PROPERTY_LAYOUT = "layout";
	public static final String PROPERTY_ADD_CHILD = Messages.GIDModel_0;
//	private PropertyChangeSupport listeners;
	
	private DocumentGateway gatewayDocument; 
	public GIDModel(GatewayEditorInput gatewayInput){
		super();
		gatewayDocument = gatewayInput.getDocumentGateway();
		
	}
	private  GIDModel(){}

	
	public void addChild(Node t){
		if (!contents.contains(t)){
			contents.add(t);
			t.getGatewayModel().setContainer(getGatewayModel().getName());
			gatewayDocument.addTransformation(t.getGatewayModel());
			t.getGatewayModel().initDescriptor();

			getListeners().firePropertyChange(PROPERTY_ADD_CHILD, null, t);
		}
	}
	
	public void removeChild(Node t){
		contents.remove(t);
		((GlobalDefinitionInput)getGatewayModel()).removeContent(t.getGatewayModel());
		gatewayDocument.removeTransformation(t.getGatewayModel());
		getListeners().firePropertyChange(PROPERTY_ADD_CHILD,t,null);
	}
	
	public List<Node> getContents(){
		return new ArrayList<Node>(contents);
	}
	
	
//	/**
//	 * add a listener 
//	 * @param listener
//	 */
//	public void addPropertyChangeListener(PropertyChangeListener listener){
//		getListeners().addPropertyChangeListener(listener);
//	}
//	
//	/**
//	 * remove the listener
//	 * @param listener
//	 */
//	public void removePropertyChangeListener(PropertyChangeListener listener){
//		getListeners().removePropertyChangeListener(listener);
//	}
//	
//	/**
//	 * 
//	 * @return the listeners
//	 */
//	public PropertyChangeSupport getListeners(){
//		return listeners;
//	}

	public void setWidth(int width) {
		this.width = width;
		((GlobalDefinitionInput)getGatewayModel()).setWidth(width);
		getListeners().firePropertyChange(PROPERTY_LAYOUT, null, width);
		
	}

	public void setHeight(int height) {
		this.height = height;
		((GlobalDefinitionInput)getGatewayModel()).setHeight(height);
		getListeners().firePropertyChange(PROPERTY_LAYOUT, null, height);
		
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
//	/**
//	 * @return the x
//	 */
//	public final int getX() {
//		return x;
//	}
	/**
	 * @param x the x to set
	 */
	public final void setX(int x) {
		super.setX(x);
		((GlobalDefinitionInput)getGatewayModel()).setPositionX(x);
	}
	
	/**
	 * @param x the x to set
	 */
	public final void setY(int y) {
		super.setY(y);
		((GlobalDefinitionInput)getGatewayModel()).setPositionY(y);
	}
//	/**
//	 * @return the y
//	 */
//	public final int getY() {
//		return y;
//	}
//	/**
//	 * @param y the y to set
//	 */
//	public final void setY(int y) {
//		this.y = y;
//	}

}
