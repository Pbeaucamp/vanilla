package bpm.gateway.core;

import bpm.gateway.core.internal.GraphNodeBuilder;
import bpm.gateway.core.internal.ModelGraphNode;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.vanilla.platform.core.IRepositoryContext;

public class GatewayModelCleaner {

	public void clean(DocumentGateway doc, IRepositoryContext repContext){
		doc.setRepositoryContext(repContext);
		ModelGraphNode graph = new GraphNodeBuilder().createTree(doc);
		
		Activator.getLogger().debug("Model graph \n" + graph.dump(""));;
		graph.initTransfo();
		
		
		
		for(Transformation t : doc.getTransformations()){
			
			AbstractTransformation tr = (AbstractTransformation)t;
			
			int i =0;
			tr.setInited();
			if (t instanceof SimpleMappingTransformation){
				((SimpleMappingTransformation)t).setInited();
			}
			
			try{
				for(StreamElement f : tr.getDescriptor(null).getStreamElements()){
					if (tr.getLoadedFields() != null){
						if (i < tr.getLoadedFields().size()){
							String s = tr.getLoadedFields().get(i).name;
							boolean exists = false;
							for(StreamElement _f : tr.getDescriptor(null).getStreamElements()){
								if (_f.name.equals(s)){
									exists = true;
									break;
								}
							}
							if (!exists){
								f.name = s;
							}
							
							
						}
					}
					
					i++;
				}
			}catch(Exception e){
				e.printStackTrace();
				
			}
			
			
		}
		
	}
}
