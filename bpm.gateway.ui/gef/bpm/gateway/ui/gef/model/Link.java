package bpm.gateway.ui.gef.model;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.ui.gef.part.LinkEditPart;

public class Link {
	public static final String BEND_POINT_CHANGED = "bendPointsChanged"; //$NON-NLS-1$
	
	private Node source;
	private Node target;
	private boolean isConnected = false;
	private List<Point> bendPoints ;
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	
	
	/**
	 * @return the bendPoints
	 */
	public List<Point> getBendPoints() {
		if (bendPoints == null){
			//init bendpoints from source and target transfirmation
			bendPoints = new ArrayList<Point>(source.getGatewayModel().getBendPoints(target.getGatewayModel()));

		}
		
		return new ArrayList<Point>(bendPoints);
	}
	public void setBendPoint(int index, Point newLocation) {
		bendPoints.set(index, newLocation);
		listeners.firePropertyChange(BEND_POINT_CHANGED, null, newLocation);
	}

	public void addBendPoint(int pos, Point p){
		bendPoints.add(pos, p);
		listeners.firePropertyChange(BEND_POINT_CHANGED, null, p);
	}
	public void removeBendPoint(Point p ){
		bendPoints.remove(p);
		listeners.firePropertyChange(BEND_POINT_CHANGED, null, p);
	}
	public void removeBendPoint(int index){
		bendPoints.remove(index);
		listeners.firePropertyChange(BEND_POINT_CHANGED, null, index);
	}
	public void swapBendPoints(int i1, int i2){
		Point p = bendPoints.get(i1);
		bendPoints.set(i1, bendPoints.get(i2));
		bendPoints.set(i2,p);
		listeners.firePropertyChange(BEND_POINT_CHANGED, null, p);
	}
	
	/**
	 * @param bendPoints the bendPoints to set
	 */
	public void setBendPoints(List<Point> bendPoints) {
		this.bendPoints = bendPoints;
		listeners.firePropertyChange(BEND_POINT_CHANGED, null, bendPoints);
	}

	public Link(Node source, Node target)throws Exception{
		this.source = source;
		this.target = target;
		reconnect(source, target);
	}
	
	public Node getSource(){
		return source;
	}
	
	public Node getTarget(){
		return target;
	}
	
	public void reconnect()  throws Exception{
		if (!isConnected) {
			
			try{
				source.addLink(this);
				target.addLink(this);
				isConnected = true;
			}catch(Exception e){
				source.getSourceLink().remove(this);
				target.getSourceLink().remove(this);
				isConnected = false;
				
				throw e;
			}
			
//			source.getGatewayModel().addOutput(target.getGatewayModel());
			
			
//			target.getGatewayModel().addInput(source.getGatewayModel());
			
			
		}
	}
	
	public void reconnect(Node newSource, Node newTarget) throws Exception{
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}
	
	public void disconnect() {
		if (isConnected) {
			source.removeLink(this);
			source.getGatewayModel().removeOutput(target.getGatewayModel());
			
			target.removeLink(this);
			target.getGatewayModel().removeInput(source.getGatewayModel());
			isConnected = false;
		}
	}
	public void removePropertyListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
		
	}
	public void addPropertyListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
		
	}

	
}
