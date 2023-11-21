package groupviewer.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

public class ContainerModel {
	// Property Names
	public final static String LAYOUT_PROP = "LAYOUT PROP";
	public final static String RESIZE_PROP = "RESIZE PROP";
	public final static String MOVE_PROP = "MOVE PROP";
	public final static String TITLE_PROP = "TITLE PROP";
	
	/*
	 * Position Data & methods
	 * 
	 */
	private Rectangle layout;
	
	public Rectangle getLayout() {
		return layout;
	}
	
	public void setLayout(Rectangle layout) {
		Rectangle old = this.layout;
		this.layout = layout;
		getListeners().firePropertyChange(LAYOUT_PROP, old, layout);
	}

	public Dimension getSize(){
		return layout.getSize();
	}
	
	public void setDimension(Dimension size){
		Dimension old = layout.getSize();
		this.layout.setSize(size);
		getListeners().firePropertyChange(RESIZE_PROP, old, layout.getSize());
	}
	
	public Point getLocation()
	{
		return this.layout.getLocation();
	}
	
	public void setLocation(Point location)
	{
		Point old = this.layout.getLocation();
		this.layout.setLocation(location);
		getListeners().firePropertyChange(MOVE_PROP, old, layout.getLocation());
	}
	/*
	 * Listeners Handler
	 * 
	 */
	private PropertyChangeSupport listeners;
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
	}
	
	public PropertyChangeSupport getListeners(){
		return listeners;
	}
}
