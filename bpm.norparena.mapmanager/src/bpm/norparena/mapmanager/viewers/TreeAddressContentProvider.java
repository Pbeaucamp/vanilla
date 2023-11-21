package bpm.norparena.mapmanager.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.norparena.mapmanager.Activator;
import bpm.vanilla.map.core.design.IAddress;

public class TreeAddressContentProvider implements ITreeContentProvider{

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IAddress){
			IAddress address = (IAddress)parentElement;
			List<IAddress> childs = address.getAddressChild();
			if(childs == null || childs.isEmpty()){
				childs = new ArrayList<IAddress>();
				try {
					childs = Activator.getDefault().getDefinitionService().getAddressChild(address);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return childs.toArray(new IAddress[childs.size()]);
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IAddress){
			return ((IAddress)element).hasChild();
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<IAddress> addresses = (List<IAddress>)inputElement;
		return addresses.toArray(new IAddress[addresses.size()]);
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
