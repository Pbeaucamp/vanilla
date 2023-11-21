package bpm.vanilla.server.client.ui.clustering.menu.views.massreport;

import java.util.HashMap;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class RepositoryLabelHelper {
	private static HashMap<IObjectIdentifier, String> itemNames = new HashMap<IObjectIdentifier, String>();
	
	public static String getModelName(IObjectIdentifier id) {
		for(IObjectIdentifier i : itemNames.keySet()){
			if (i.getRepositoryId() == id.getRepositoryId() && i.getDirectoryItemId() == id.getDirectoryItemId()){
				return itemNames.get(i);
			}
		}
		
		try{
			IVanillaContext ctx = Activator.getDefault().getVanillaContext();
			Repository rep = Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositoryById(id.getRepositoryId());
			Group group = new Group();
			group.setId(-1);
			IRepositoryContext repCtx = new BaseRepositoryContext(Activator.getDefault().getVanillaContext(), group, rep);
			
			IRepositoryApi sock = new RemoteRepositoryApi(repCtx);
			
			RepositoryItem it = sock.getRepositoryService().getDirectoryItem(id.getDirectoryItemId());
			
			if (it != null){
				itemNames.put(id, it.getItemName());
				return it.getItemName();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return Messages.RepositoryLabelHelper_0;
		
		
	}
}
