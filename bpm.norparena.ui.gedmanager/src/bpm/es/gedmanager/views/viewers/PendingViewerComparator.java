package bpm.es.gedmanager.views.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class PendingViewerComparator extends ViewerComparator {

	private static final int NAME_INDEX = 0;
	private static final int CREATION_INDEX = 1;
	private static final int PATH_INDEX = 3;
	private static final int FORMAT_INDEX = 2;

	private int columnIndex;

	public PendingViewerComparator(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (columnIndex == NAME_INDEX) {
			return ((GedDocument) e1).getName().compareTo(((GedDocument) e2).getName());
		}
		else if (columnIndex == PATH_INDEX) {
			// return
			// 1;//((GedDocument)e1).getRelativPath().compareTo(((GedDocument)e2).getRelativPath());
		}
		else if (columnIndex == CREATION_INDEX) {
			if (((GedDocument) e1).getCreationDate() != null && ((GedDocument) e2).getCreationDate() != null) {
				return ((GedDocument) e1).getCreationDate().compareTo(((GedDocument) e2).getCreationDate());
			}
		}
		else if (columnIndex == FORMAT_INDEX) {
			// return
			// ((GedDocument)e1).getFormat().compareTo(((GedDocument)e2).getFormat());
		}

		return 0;
	}
}
