package org.fasd.views.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.fasd.charting.CompositePie3DChart;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.jfree.data.general.DefaultPieDataset;

public class DialogBrowseColumn extends Dialog {
	private TableViewer viewer, dviewer;
	private DataObjectItem item;
	private List<String> values, distincts;
	private CompositePie3DChart chart;
	private TabFolder tabFolder;

	public DialogBrowseColumn(Shell parentShell, List<String> values, List<String> distincts, DataObjectItem item) {
		super(parentShell);
		this.values = values;
		this.distincts = distincts;
		this.item = item;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NONE);
		tabFolder.setLayout(new FillLayout());
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem values = new TabItem(tabFolder, 0);
		values.setText(LanguageText.DialogBrowseColumn_All_Values);
		values.setControl(createValuesViewer(tabFolder));

		TabItem distincts = new TabItem(tabFolder, 1);
		distincts.setText(LanguageText.DialogBrowseColumn_Distinct_Values);
		distincts.setControl(createDistinctViewer(tabFolder));

		TabItem chart = new TabItem(tabFolder, 2);
		chart.setText(LanguageText.DialogBrowseColumn_0);
		chart.setControl(createChartViewer(tabFolder));

		return parent;
	}

	private Control createValuesViewer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		viewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL | SWT.BORDER);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText(item.getName());
		column.setWidth(100);

		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		viewer.setContentProvider(new TableContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		viewer.setInput(values.toArray(new String[values.size()]));

		return container;
	}

	private Control createDistinctViewer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		dviewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FILL | SWT.BORDER);
		Table table = dviewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText(item.getName());
		column.setWidth(100);
		TableColumn c = new TableColumn(table, SWT.LEFT);
		c.setText(LanguageText.DialogBrowseColumn_Count);
		c.setWidth(100);

		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		dviewer.setContentProvider(new TableContentProvider());
		dviewer.setLabelProvider(new TableLabelProvider());
		dviewer.setInput(distincts.toArray(new String[distincts.size()]));

		return container;
	}

	private Control createChartViewer(Composite parent) {

		DefaultPieDataset dataset = new DefaultPieDataset();

		for (String s : distincts) {
			String[] row = ((String) s).split(";"); //$NON-NLS-1$
			dataset.setValue(row[0], Float.parseFloat(row[1]));
		}

		chart = new CompositePie3DChart(parent, dataset, item.getName());

		return chart;
	}

	class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element == null)
				return ""; //$NON-NLS-1$

			String[] row = ((String) element).split(";"); //$NON-NLS-1$
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
			Object[] r = (Object[]) inputElement;
			List<String> l = new ArrayList<String>();

			for (Object rr : r) {
				if (rr != null)
					l.add((String) rr);
				else
					l.add(""); //$NON-NLS-1$
			}

			return l.toArray(new String[l.size()]);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	@Override
	protected void initializeBounds() {
		this.getShell().setText(LanguageText.DialogBrowseColumn_Content + item.getParent().getName() + "." + item.getName()); //$NON-NLS-1$
		super.initializeBounds();
		chart.drawChart(tabFolder.getBounds());
	}
}
