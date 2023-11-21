package bpm.gateway.ui.viewer;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;

public class StreamDescriptorStructuredContentProvider implements IStructuredContentProvider{

	public Object[] getElements(Object inputElement) {
		 Assert.isTrue(inputElement != null);
		List<StreamElement> l = ((StreamDescriptor)inputElement).getStreamElements();
		return l.toArray(new StreamElement[l.size()]);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
