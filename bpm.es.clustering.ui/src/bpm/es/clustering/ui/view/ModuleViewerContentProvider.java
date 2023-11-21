package bpm.es.clustering.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.gef.GefModel;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.beans.Server;

public class ModuleViewerContentProvider implements ITreeContentProvider{
	
	private static final String REPOSITORIES = Messages.ModuleViewerContentProvider_0;
	private static final String DEFAULT_MODULES = Messages.ModuleViewerContentProvider_1;
	private static final String DEFAULT_CLIENTS = Messages.ModuleViewerContentProvider_2;
	private static final String REGISTERED_MODULES = Messages.ModuleViewerContentProvider_3;
	
//	private List<Server> clients = new ArrayList<Server>();
	
	private GefModel model;
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}
	
	public void dispose() {
		
		
	}
	
	public Object[] getElements(Object inputElement) {
		this.model = (GefModel)inputElement;
		Object[]  o = {REPOSITORIES, DEFAULT_MODULES, REGISTERED_MODULES};//DEFAULT_CLIENTS, 
		return o;
	}

	
	
	
	public Object[] getChildren(Object parentElement) {
		List l = new ArrayList();
		if (parentElement  == DEFAULT_CLIENTS){
			l.addAll(model.getClients());
		}
		if (parentElement  == REPOSITORIES){
			l.addAll( model.getRepositories());
		}
		if (parentElement  == DEFAULT_MODULES){
			l.add(model.getDefaultModule());
		}
		if (parentElement  == REGISTERED_MODULES){
			l.addAll(model.getModules());
		}
		
		if (parentElement instanceof VanillaPlatformModule){
			l.addAll(((VanillaPlatformModule)parentElement).getRegisteredModules());
		}
		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {
		
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element  == DEFAULT_CLIENTS){
			return !model.getClients().isEmpty();
		}
		if (element  == REPOSITORIES){
			try{
				return !model.getRepositories().isEmpty();
			}catch(Exception ex){}
		}
		if (element  == DEFAULT_MODULES){
			return model.getDefaultModule() != null;
		}
		if (element  == REGISTERED_MODULES){
			return ! model.getModules().isEmpty();
		}
		
		if (element instanceof Server){
			return true;
		}
		if (element instanceof VanillaPlatformModule){
			return true;
		}
		return false;
	}
}
