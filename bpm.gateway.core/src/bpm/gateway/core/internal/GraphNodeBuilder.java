package bpm.gateway.core.internal;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Transformation;
/**
 * To be used for performing a cleaning on the model once it has been digested from xml
 * 
 * It rebuilds a Tree of the Model, roots are Step without inputs, each node child is its 
 * inputs.
 * 
 * The purpose is to init a step only once its input have been inited, then 
 * each Step descriptor will be rightly rebuilt
 * @author ludo
 *
 */
public class GraphNodeBuilder {

	public ModelGraphNode createTree(List<Transformation> transfos){
		ModelGraphNode root = new ModelGraphNode(null);
		
		for(Transformation t : getFinalNodes(transfos)){
			ModelGraphNode n = new ModelGraphNode(t);
			root.addChild(n);
			buildChilds(n);
			
		}
		
		return root;
	}
	public ModelGraphNode createTree(DocumentGateway model){
		return createTree(model.getTransformations());
	}
	
	private List<Transformation> getFinalNodes(List<Transformation> transfos) {
		List<Transformation> nodes = new ArrayList<Transformation>();
		
		for(Transformation t : transfos){
			if (t.getOutputs().isEmpty()){
				nodes.add(t);
			}
		}
		return nodes;
	}

	private void buildChilds(ModelGraphNode parent){
		if (parent.getTransfo() != null){
			
			for(Transformation t : parent.getTransfo().getInputs()){
				ModelGraphNode n = new ModelGraphNode(t);
				parent.addChild(n);
				buildChilds(n);
			}
		}
	}
}
