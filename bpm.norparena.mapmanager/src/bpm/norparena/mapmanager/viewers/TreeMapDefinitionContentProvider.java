package bpm.norparena.mapmanager.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.norparena.mapmanager.Activator;
import bpm.vanilla.map.core.design.IMapDefinition;

public class TreeMapDefinitionContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IMapDefinition){
			IMapDefinition mapDefinition = (IMapDefinition)parentElement;
			List<IMapDefinition> childs = mapDefinition.getMapChilds();
			if(childs == null || childs.isEmpty()){
				childs = new ArrayList<IMapDefinition>();
				try {
					childs = Activator.getDefault().getDefinitionService().getMapDefinitionChild(mapDefinition);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return childs.toArray(new IMapDefinition[childs.size()]);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IMapDefinition){
			return ((IMapDefinition)element).hasChild();
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<IMapDefinition> mapDefinitions = (List<IMapDefinition>)inputElement;
		return mapDefinitions.toArray(new IMapDefinition[mapDefinitions.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
