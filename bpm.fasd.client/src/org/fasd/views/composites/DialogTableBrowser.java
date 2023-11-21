package org.fasd.views.composites;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;

public class DialogTableBrowser extends Dialog {
	class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String[] row = (String[]) element;
			return row[columnIndex];
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}

	public class TableContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			ArrayList<String> arrayList = (ArrayList<String>) inputElement;
			return arrayList.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private TableViewer viewer;
	private ArrayList<String[]> input;
	private String[] headers;
	private String tableName;

	public DialogTableBrowser(Shell parentShell, ArrayList<DataObjectItem> columns, ArrayList<String[]> tableContent, String tableName) {
		super(parentShell);
		input = tableContent;
		headers = new String[columns.size()];
		this.tableName = tableName;
		for (int i = 0; i < columns.size(); i++)
			headers[i] = columns.get(i).getName();

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		viewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL | SWT.BORDER);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		for (String name : headers) {
			TableColumn column = new TableColumn(table, SWT.LEFT);
			column.setText(name);
			column.setWidth(100);

		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new TableContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		viewer.setInput(input);
		return composite;
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		Shell shell = this.getShell();
		Monitor primary = shell.getMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);
		shell.setText(LanguageText.DialogTableBrowser_0 + tableName);
	}

	@Override
	public boolean close() {
		return super.close();
	}

}
