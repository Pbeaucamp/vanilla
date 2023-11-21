package bpm.gateway.ui.views.property.sections.transformations;

import java.util.List;

public class SelectionLookupSection extends SelectionSection {


	
	
	@Override
	public void refresh() {
//		SelectionTransformation transfo = ((Lookup)node.getGatewayModel()).getSelectionTransformation();
//		
//			
//		try {
//			List<StreamElement> l = new ArrayList<StreamElement>();
//			
//			for(Transformation t : transfo.getInputs()){
//				l.addAll(t.getDescriptor().getStreamElements());
//			}
//			
//			streamComposite.fillDatas(l);
//		} catch (ServerException e) {
//			
//			e.printStackTrace();
//		}
	}


		
	@Override
	protected void performAddColumns(List<Integer>  positions){
//		SelectionTransformation tr = ((Lookup)node.getGatewayModel()).getSelectionTransformation();
//		
//		for(Integer i : positions){
//			((Lookup)node.getGatewayModel()).activeStreamElement(i);
////			tr.activeStreamElement(i);
//			
//		}
		
	}
	
	@Override
	protected void performDelColumns(List<Integer>  positions){
//		SelectionTransformation tr = ((Lookup)node.getGatewayModel()).getSelectionTransformation();
//		
//		for(Integer i : positions){
//			
////			tr.desactiveStreamElement(i);
//			((Lookup)node.getGatewayModel()).desactiveStreamElement(i);
//			
//		}
		
	}
	
}
