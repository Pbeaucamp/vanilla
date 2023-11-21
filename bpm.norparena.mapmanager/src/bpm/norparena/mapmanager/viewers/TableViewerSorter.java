package bpm.norparena.mapmanager.viewers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class TableViewerSorter extends ViewerSorter {

	private final ITableLabelProvider labelProvider;
	private int columnIndex;
	private int order;

	public TableViewerSorter(ITableLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
		columnIndex = 0;
		order = 1;
	}

	public void doSort(int columnIndex) {
		this.columnIndex = columnIndex;
		order = -order;
	}

	public int compare(Viewer viewer, Object o1, Object o2) {
		final String text1 = labelProvider.getColumnText(o1, columnIndex);
		final String text2 = labelProvider.getColumnText(o2, columnIndex);
		return order * text1.compareTo(text2);
	}
}
