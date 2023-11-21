package bpm.gateway.core.transformations.vanilla;

import java.io.File;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FreeDashHelper {

	private IRepositoryContext repContext;
	
	public FreeDashHelper(IRepositoryContext repContext){
		this.repContext = repContext;
	}
	
	private IRepositoryApi createSock() throws Exception{
		if (repContext == null){
			throw new Exception("Cannot use Metadata without a IRepositoryContext");
		}
		
		IRepositoryApi sock = new RemoteRepositoryApi(repContext);
		
		return sock;
	}
	public void createDescriptor(FreedashFormInput transfo)throws Exception{
		RepositoryItem item = null;
		try {
			IRepositoryApi sock = createSock();
			
			try{
				item = sock.getRepositoryService().getDirectoryItem(transfo.getDirectoryItemId());
				transfo.setDirectoryItem(item);
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception("Item with id " + transfo.getDirectoryItemId() + " not found : " + ex.getMessage());
			}
			
			File f = new File("./tmp/fd");
			f.mkdirs();
			f.deleteOnExit();
			
			FdProject project =null;
			
			try{
				//project = ModelLoader.loadProject(sock, item, f.getAbsolutePath());
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception("Error when loading FD Form project " + transfo.getDirectoryItemId() + " not found : " + ex.getMessage());
			}
				
			
			
			DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
			
			for(IComponentDefinition c : project.getFdModel().getComponents().keySet()){
				StreamElement e = new StreamElement();
				e.className = "java.lang.String";
				e.transfoName = transfo.getName();
				e.originTransfo = transfo.getName();
				
				if (c instanceof ComponentFilterDefinition){
					e.name = ((ComponentFilterDefinition)c).getId();
					desc.addColumn(e);
				}
				else if (c instanceof ComponentDataGrid){
					e.name = ((ComponentFilterDefinition)c).getId();
					desc.addColumn(e);
				}
			}
			
			transfo.setDescriptor(desc );
			
		} catch (Exception e) {
			
			throw e;
		}
	}
}
