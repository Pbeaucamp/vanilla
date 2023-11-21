package bpm.gateway.ui.gef.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.model.IWorkbenchAdapter;

import bpm.gateway.core.Comment;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.GlobalDefinitionInput;
import bpm.gateway.ui.editors.GatewayEditorInput;

public class ContainerPanelModel implements IAdaptable{
	public static final String PROPERTY_ADD_CHILD = "newChild"; //$NON-NLS-1$
	public static final String PROPERTY_REMOVE_CHILD = "removeChild"; //$NON-NLS-1$

	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private List<Node> contents = new ArrayList<Node>();
	private List<Comment>  annotes = new ArrayList<Comment>();
	
	
	protected Rectangle layout;
	
	private DocumentGateway gatewayDocument;
	private GatewayEditorInput input;
	
	public ContainerPanelModel(GatewayEditorInput gatewayInput){
		this.input = gatewayInput;
		input.setContainer(this);
		
		gatewayDocument = gatewayInput.getDocumentGateway();
		rebuildModel();
	}
	
	
	private void rebuildModel(){
		
		for(Comment  c : gatewayDocument.getAnnotes()){
			annotes.add(c);
		}
		
		for(Transformation tr : gatewayDocument.getTransformations()){
			
			try {
				
				if (tr instanceof GlobalDefinitionInput){
					GIDModel n = new GIDModel(input);
					n.setName(tr.getName());
					n.setTransformation(tr);
					n.setX(tr.getPositionX());
					n.setY(tr.getPositionY());
					n.setWidth(((GlobalDefinitionInput)tr).getWidth());
					n.setHeight(((GlobalDefinitionInput)tr).getHeight());
					
					
					contents.add(n);
				}
				else if (tr.getContainer() != null){
					for (Node p : contents){
						if (p.getName().equals(tr.getContainer())){
							Node n = new Node();
							n.setX(tr.getPositionX());
							n.setY(tr.getPositionY()) ;
							n.setName(tr.getName());
							n.setTransformation(tr);
							
							((GIDModel)p).addChild(n);
						}
					}
					
					
				}
				else{
					Node n = new Node();
					n.setX(tr.getPositionX());
					n.setY(tr.getPositionY());
					n.setName(tr.getName());
					n.setTransformation(tr);
					
					contents.add(n);
				}
				
				
				
			} catch (NodeException e) {
				
				e.printStackTrace();
			}
			
		}
		
		
		HashMap<Node, List<Node>> outputs = new HashMap<Node, List<Node>>();
		
		for(Node n  : contents){
			for(Transformation t : n.getGatewayModel().getOutputs()){
				
				for(Node _n : contents){
					if (_n.getGatewayModel() == t && n != _n){
						
						if (outputs.get(n) == null){
							outputs.put(n, new ArrayList<Node>());
						}
						if (!outputs.get(n).contains(_n)){
							outputs.get(n).add(_n);
						}
						
//						break;
					}
				}
			}
			
			if (n instanceof GIDModel){
				for (Node p : ((GIDModel)n).getContents()){
					for(Transformation t : p.getGatewayModel().getOutputs()){
						for(Node _n : ((GIDModel)n).getContents()){
							if (_n.getGatewayModel() == t && n != _n){
								
								if (outputs.get(p) == null){
									outputs.put(p, new ArrayList<Node>());
								}
								if (!outputs.get(p).contains(_n)){
									outputs.get(p).add(_n);
								}
								
//								break;
							}
						}
					}
				}
				
				
			}
		}
		
		
		
		
		for(Node k : outputs.keySet()){
			for(Node n : outputs.get(k)){
				try {
					Link l = new Link(k, n);
				} catch (Exception e) {
					
					e.printStackTrace();
				}

			}
		}
		
		
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		listeners.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		listeners.removePropertyChangeListener(listener);
	}
	
	public Rectangle getLayout(){
		return this.layout;
	}
	
	
	
	public PropertyChangeSupport getListeners(){
		return listeners;
	}
	
	public boolean removeChild(Node child){
		boolean b = this.contents.remove(child);
		if(b){
			gatewayDocument.removeTransformation(child.getGatewayModel());
			getListeners().firePropertyChange(PROPERTY_REMOVE_CHILD,child,null);
		}
			
		return b;
	}
	
	public boolean addChild(Node child){
		if (contains(child)){
			return false;
		}
		boolean b = this.contents.add(child);
		if(b){
			
			gatewayDocument.addTransformation(child.getGatewayModel());
			child.getGatewayModel().initDescriptor();
			child.setName(child.getGatewayModel().getName());

			getListeners().firePropertyChange(PROPERTY_ADD_CHILD,null,child);		
		}
		return b;

	}
	
	private boolean contains(Node child){
		return contents.contains(child);
	}
	
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class){
			return this;
		}
		
		return null;
	}

	public List getChildren() {
		List l = new ArrayList();
		l.addAll(contents);
		l.addAll(annotes);
		return l;
	}


	public boolean addAnnote(Comment annote) {
		if (annotes.contains(annote)){
			return false;
		}
		boolean b = this.annotes.add(annote);
		if(b){
			
			gatewayDocument.addAnnote(annote);

			getListeners().firePropertyChange(PROPERTY_ADD_CHILD,null,annote);		
		}
		return b;
		
		
	}
	
	public boolean removeAnnote(Comment annote){
		boolean b = this.annotes.remove(annote);
		if(b){
			gatewayDocument.removeAnnote(annote);
			getListeners().firePropertyChange(PROPERTY_REMOVE_CHILD,annote,null);
		}
			
		return b;
	}
	
	
	
	
}
