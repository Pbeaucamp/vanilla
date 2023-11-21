package bpm.gateway.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 
 * This class represent a Commenyt for a Gateay Model.
 * t can be added on the model definition
 * @author LCA
 *
 */
public class Comment {
	
	public static final String PROPERTY_LAYOUT="layout";
	public static final String PROPERTY_TEXT = "text";
	public static final String PROPERTY_TYPE = "type";
	
	public static final int NORMAL_NOTE = 0;
	public static final int WARNING_NOTE = 1;

	
	
	private String content = "";
	private int x,y;
	private int width = 50, height = 10;
	private int typeNote = 0;
	
	
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	
	public Comment(){
		
	}
	
	public int getTypeNote(){
		return typeNote;
	}
	
	public void setTypeNote(int type){
		int old = typeNote;
		this.typeNote = type;
		listeners.firePropertyChange(PROPERTY_TYPE, old, typeNote);
	}
	
	public void setTypeNote(String type){
		int old = typeNote;
		this.typeNote = Integer.parseInt(type);
		listeners.firePropertyChange(PROPERTY_TYPE, old, typeNote);
	}
	
	public Comment(String value){
		
		setContent(content);
	}
	
	/**
	 * 
	 * @return the width of the Comment Box
	 */
	public int getWidth() {
		return width;
	}
	
	
	/**
	 * set th Comment Box width
	 * @param width
	 */
	public void setWidth(int width) {
		int old = this.width;
		if (width < 50){
			this.width = 50;
		}
		else{
			this.width = width;
		}
		listeners.firePropertyChange(PROPERTY_LAYOUT, null, width);
	}
	
	
	/**
	 * set th Comment Box width
	 * @param width
	 */
	public void setWidth(String width) {
		setWidth(Integer.parseInt(width));
	}
	
	
	/**
	 * 
	 * @return the Comment box height
	 */
	public int getHeight() {
		
		
		return height;
	}
	
	/**
	 * set the comment b ox Height
	 * @param height
	 */
	public void setHeight(int height) {
		int old = this.height;
		if (height < 10){
			this.height = 10;
		}
		else{
			this.height = height;
		}
		listeners.firePropertyChange(PROPERTY_LAYOUT, null, height);
		
	}
	
	
	/**
	 * set the comment b ox Height
	 * @param height
	 */
	public void setHeight(String height) {
		try{
			setHeight(Integer.parseInt(height));
		}catch(NumberFormatException e){
			
		}
		
	}
	
	/**
	 * 
	 * @return the comment content
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * 
	 * @param content the Comment content
	 */
	public void setContent(String content) {
		String old = new String(this.content);
		this.content = content;
		listeners.firePropertyChange(PROPERTY_TEXT, old, content);
	}
	
	/**
	 * 
	 * @return the comment box X position
	 */
	public int getX() {
		return x;
	}
	
	/**
	 *  
	 * @param x the comment box X position
	 */
	public void setX(int x) {
		this.x = x;
		listeners.firePropertyChange(PROPERTY_LAYOUT, null, x);
	}
	
	
	/**
	 *  
	 * @param x the comment box X position
	 */
	public void setX(String x) {
		try{
			this.x = Integer.parseInt(x);
			listeners.firePropertyChange(PROPERTY_LAYOUT, null, x);
		}catch(NumberFormatException e){
			
		}
	}
	
	/**
	 * 
	 * @return the Y Position
	 */
	public int getY() {
		return y;
	}
	
	/**
	 *  
	 * @param y the comment box Y position
	 */
	public void setY(int y) {
		this.y = y;
		listeners.firePropertyChange(PROPERTY_LAYOUT, null, y);
	}
	
	
	/**
	 *  
	 * @param y the comment box Y position
	 */
	public void setY(String y) {
		try{
			this.y = Integer.parseInt(y);
			listeners.firePropertyChange(PROPERTY_LAYOUT, null, y);
		}catch(NumberFormatException e){
			
		}
	}
	
	public Element getElement(){
	
		Element e = DocumentHelper.createElement("annote");
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("width").setText("" + width);
		e.addElement("height").setText("" + height);
		e.addElement("content").setText(content);
		e.addElement("typeNote").setText("" + typeNote);
		return e;
	}
	
	/**
	 * add a listener 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	/**
	 * remove the listener
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
	}
	
	/**
	 * 
	 * @return the listeners
	 */
	public PropertyChangeSupport getListeners(){
		return listeners;
	}
}
