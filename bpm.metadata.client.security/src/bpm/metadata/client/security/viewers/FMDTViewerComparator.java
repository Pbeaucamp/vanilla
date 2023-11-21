package bpm.metadata.client.security.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IResource;

public class FMDTViewerComparator extends ViewerComparator{
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof IDataStream && e2 instanceof IDataStream){
			return ((IDataStream)e1).getName().compareTo(((IDataStream)e2).getName());
		}
		
		if (e1 instanceof IDataStreamElement && e2 instanceof IDataStreamElement){
			return ((IDataStreamElement)e1).getName().compareTo(((IDataStreamElement)e2).getName());
		}
		
		if (e1 instanceof IResource && e2 instanceof IResource){
			IResource r1 = ((IResource)e1);
			IResource r2 = ((IResource)e2);
			
			if (r1.getClass() == r2.getClass()){
				return r1.getName().compareTo(r2.getName());
			}
			else{
				return r1.getClass().getName().compareTo(r2.getClass().getName());
			}
			
			
		}
		return 0;
	}
}
