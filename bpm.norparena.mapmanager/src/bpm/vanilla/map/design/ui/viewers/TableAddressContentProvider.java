package bpm.vanilla.map.design.ui.viewers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.map.core.design.IAddress;

public class TableAddressContentProvider implements IStructuredContentProvider{

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
