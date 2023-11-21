package bpm.gateway.core.transformations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import bpm.gateway.core.AbrstractDigesterTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayDigester;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class SubTransformationHelper {

	private IRepositoryContext repContext;
	public SubTransformationHelper(IRepositoryContext repContext){
		this.repContext = repContext;
	}
	
	public DocumentGateway modelLoader(SubTransformation transfo) throws Exception{
		InputStream is = null;
		
		
		
		if (repContext != null){
			try{
				IRepositoryApi sock = new RemoteRepositoryApi(repContext);
				
				RepositoryItem it = sock.getRepositoryService().getDirectoryItem((Integer.parseInt(transfo.getDefinition())));
				
				String xml = sock.getRepositoryService().loadModel(it);
				
				is = IOUtils.toInputStream(xml, "UTF-8");
			}catch(Exception ex){
				throw new Exception("Error when loading SubTransformation content from repository", ex);
			}
			
			
		}
		else{
			try {
				is = new FileInputStream(transfo.getDefinition());
			} catch (FileNotFoundException e1) {
				throw new Exception("Error when loading SubTransformation content from FileSystem", e1);
			}
		}

		try{
			GatewayDigester dig = new GatewayDigester(is, new ArrayList<AbrstractDigesterTransformation>());
			return  dig.getDocument(repContext);
		}catch(Exception ex){
			throw new Exception("Error when rebuilding subtransformation model", ex);
		}
	}
	
	public DefaultStreamDescriptor getDescriptor(SubTransformation transfo) throws Exception{
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		
		DocumentGateway gwt = null;
		
		if (transfo.getFinalStep() == null){
			return desc;
		}
		
		

		try{
			
			gwt = modelLoader(transfo);
			
			Transformation t = gwt.getTransformation(transfo.getFinalStep());
			
			if ( t != null){
				for(StreamElement e : t.getDescriptor(null).getStreamElements()){
					desc.addColumn(e.clone(transfo.getName(), ""));
				}
			}
			
		}catch(Exception ex){
			throw new Exception("Error when rebuildong subtransformation model", ex);
		}
		
		
		return desc;
	}
}
