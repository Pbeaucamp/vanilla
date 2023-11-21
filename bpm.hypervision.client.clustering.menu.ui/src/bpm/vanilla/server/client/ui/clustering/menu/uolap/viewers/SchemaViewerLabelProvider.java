package bpm.vanilla.server.client.ui.clustering.menu.uolap.viewers;

import java.util.HashMap;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;

public class SchemaViewerLabelProvider extends ColumnLabelProvider{
	public static enum Column{
		State, Path
	}
	
	private static HashMap<Integer, String> loadedItemsPath = new HashMap<Integer, String>();
	private Column type;
	private IRepositoryApi sock;
	private IRepositoryContext repositoryContext;
	
	public SchemaViewerLabelProvider(Column type){
		this.type = type;
	}
	
	public void setRepositoryContext(IRepositoryContext repositoryContext){
		this.repositoryContext = repositoryContext;
	}
	
	@Override
	public String getText(Object element) {
		RepositoryItem it = (RepositoryItem)element;
		
		if (type == Column.Path){
			if (loadedItemsPath.get(it.getId()) == null){
				return getFullPath(it);
			}
			
			return loadedItemsPath.get(it.getId());
			
		}
		else if (type == Column.State){
			return ""; //$NON-NLS-1$
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		RepositoryItem it = (RepositoryItem)element;
		if (type == Column.State){
			if (it.isOn()){
				return Activator.getDefault().getImageRegistry().get(Icons.CUBE_ON);
			}
			else{
				return Activator.getDefault().getImageRegistry().get(Icons.CUBE_OFF);
			}
		}
		return null;
	}
	
	private String getFullPath(RepositoryItem item){
		
		
		if (sock == null){
			sock = new RemoteRepositoryApi(repositoryContext);
		}
		Integer currentDirId = item.getDirectoryId();
		RepositoryDirectory parent = null;
		String path = item.getItemName();
		try {
			while( currentDirId != null && currentDirId > 0){
				parent = sock.getRepositoryService().getDirectory(currentDirId);
				path = parent.getName() + "/" + path; //$NON-NLS-1$
				currentDirId = parent.getParentId();	
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			path = "???/" + path; //$NON-NLS-1$
		} 
		
		
		loadedItemsPath.put(item.getId(), path);
		return path;
		
	}
	
}
