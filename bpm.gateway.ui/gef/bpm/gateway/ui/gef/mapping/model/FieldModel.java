package bpm.gateway.ui.gef.mapping.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.SimpleMappingTransformation;

public class FieldModel {
	
	private StreamElement model;
	private StreamModel parent;
	private List<Relation> sourceRelations = new ArrayList<Relation>();
	private List<Relation> targetRelations = new ArrayList<Relation>();
	private Transformation transfo;

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);	
	public FieldModel(StreamElement e, Transformation transformation) {
		this.model = e;
		this.transfo = transformation;
	}
	
	public void setParent(StreamModel m){
		this.parent = m;
	}
	
	public String getFieldName(){
		return model.name;
	}
	
	public String getFieldOrigin(){
		return model.originTransfo;
	}
	
	public StreamModel getParent(){
		return parent;
	}

	public void addRelation(Relation relation) {
		if (relation.getSource() == this){
			if(!sourceRelations.contains(relation)){
				sourceRelations.add(relation);
				getListeners().firePropertyChange("linksource", null, relation); //$NON-NLS-1$
			}
		}
		else{
			if(!targetRelations.contains(relation)){
				targetRelations.add(relation);
				getListeners().firePropertyChange("linktarget", null, relation); //$NON-NLS-1$
			}
		}		
	}
	
	public void removeRelation(Relation relation) {
		if (relation.getSource() == this){
			sourceRelations.remove(relation);
			getListeners().firePropertyChange("linksource", null, relation); //$NON-NLS-1$
		}
		else {
			targetRelations.remove(relation);
			getListeners().firePropertyChange("linktarget", null, relation); //$NON-NLS-1$
		}
	}

	public List<Relation> getSourceConnections() {
		return sourceRelations;
	}

	public List<Relation>  getTargetConnections() {
		return targetRelations;
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

	public Transformation getTransfo() {
		return transfo;
	}
	
}
