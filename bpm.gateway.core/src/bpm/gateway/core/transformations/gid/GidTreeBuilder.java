package bpm.gateway.core.transformations.gid;

import java.util.ArrayList;
import java.util.List;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.GlobalDefinitionInput;

public class GidTreeBuilder {

	private FactoryGidNode factory = new FactoryGidNode();
	
	public GidNode buildTree(GlobalDefinitionInput gid)throws Exception {
		Transformation root = findRoot(gid);
		if(root == null) {
			return null;
		}
		GidNode rootNode = factory.createNode(root);
		createTree(rootNode);
		
		return rootNode;
		
	}
	
	
	public List<Server> getDataStreamsServers(GidNode node){
		List<Server> servers = new ArrayList<Server>();
		
		if (node.getTransformation() != null){
			if (node.getTransformation() instanceof DataStream){
				Server s = ((DataStream)node.getTransformation()).getServer();
				
				if (s!= null && !servers.contains(s)){
					servers.add(s);
				}
			}
		}
		
		for(Object n : node.getChilds()){
			for(Server s : getDataStreamsServers((GidNode)n)){
				if (s!= null && !servers.contains(s)){
					servers.add(s);
				}
			}
		}
		return servers;
	}
	
	private void createTree(GidNode node) throws Exception{
		
		for(Transformation t : node.getTransformation().getInputs()){
			GidNode child = factory.createNode(t);
			node.addChild(child);
			createTree(child);
		}
	}
	
	public Transformation findRoot(GlobalDefinitionInput gid) throws Exception{
		
		Transformation root = null;
		for(Transformation t : gid.getContent()){
			if (t.getOutputs().isEmpty()){
				if (root  != null){
//					throw new Exception("Gid cannot have more than one terminal step");
				}
				root = t;
			}
		}
//		if (root != null){
			return root;
//		}
//		throw new Exception("No terminal node found");
	}
}
