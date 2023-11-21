package bpm.gateway.ui.gef.model;


import bpm.gateway.core.Transformation;


public class NodeLinkerHelper {

	public static void add(Link link) throws Exception{
		
		Transformation source = (Transformation)link.getSource().getGatewayModel();
		Transformation target = (Transformation)link.getTarget().getGatewayModel();
		
		try{
			target.addInput(source);
			source.addOutput(target);
		}catch(Exception e){
			target.removeInput(source);
			source.removeOutput(target);
			throw e;
		}
		
		
		
		
		
		
	}
	public static void remove(Link link){
		
		Transformation source = (Transformation)link.getSource().getGatewayModel();
		Transformation target = (Transformation)link.getTarget().getGatewayModel();

		target.removeInput(source);
		source.removeOutput(target);
	}
}
