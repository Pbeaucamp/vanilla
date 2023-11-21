package bpm.es.sessionmanager.views.providers;

import java.util.Date;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import bpm.es.sessionmanager.Messages;

public class LogSorter extends ViewerSorter {
	private int propertyIndex;
	// private static final int ASCENDING = 0;
	private static final int DESCENDING = 1;

	private int direction = DESCENDING;

	public LogSorter() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		TreeViewer tv = (TreeViewer) viewer;
		
		
		if (e1 instanceof Date) {
			Date d1 = (Date) e1;
			Date d2 = (Date) e2;
			return d1.compareTo(d2);
		}
		else if (e1 instanceof String) {
			String s1 = (String) e1;
			String s2 = (String) e2;
			return s1.compareTo(s2);
		}
		else {
			return 0;
		}
	}

}
