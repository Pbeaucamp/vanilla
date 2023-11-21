package bpm.es.gedmanager.views.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class DocumentViewerComparator extends ViewerComparator {

	private static final int NAME_INDEX = 0;
	private static final int SUMMARY_INDEX = 1;
	private static final int CREATION_INDEX = 2;
	private static final int VERSION_INDEX = 3;
	private static final int FORMAT_INDEX = 4;

	private int columnIndex;

	public DocumentViewerComparator(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (columnIndex == NAME_INDEX) {
			return ((GedDocument) e1).getName().compareTo(((GedDocument) e2).getName());
		}
		else if (columnIndex == SUMMARY_INDEX) {
			// return
			// 1;//((GedDocument)e1).getSummary().compareTo(((GedDocument)e2).getSummary());
		}
		else if (columnIndex == CREATION_INDEX) {
			if (((GedDocument) e1).getCreationDate() != null && ((GedDocument) e2).getCreationDate() != null) {
				return ((GedDocument) e1).getCreationDate().compareTo(((GedDocument) e2).getCreationDate());
			}
		}
		else if (columnIndex == VERSION_INDEX) {
			// return
			// 3;//((GedDocument)e1).getVersion().compareTo(((GedDocument)e2).getVersion());
		}
		else if (columnIndex == FORMAT_INDEX) {
			// return
			// ((GedDocument)e1).getFormat().compareTo(((GedDocument)e2).getFormat());
		}

		return 0;
	}

}
