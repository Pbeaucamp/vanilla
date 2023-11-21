package bpm.fd.design.ui.viewer.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import bpm.fd.api.internal.ILabelable;

public class LabelableComparator extends ViewerComparator{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {
			if (!(e1 instanceof ILabelable)){
				return (e1.toString().compareTo(((ILabelable)e2).getLabel()));
			}
			else if (!(e2 instanceof ILabelable)){
				return ((ILabelable)e1).getLabel().compareTo(e2.toString());
			}
			return ((ILabelable)e1).getLabel().compareTo(((ILabelable)e2).getLabel());
		} catch(Exception e) {
			return 0;
		}
	}

}
