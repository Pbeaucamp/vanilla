package org.fasd.cubewizard.oda;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.fasd.datasource.DataObjectOda;

public class OdaDatasetContentPage extends WizardPage {

	private TableViewer viewer;
	private Composite main;
	private IQuery query;
	private DataObjectOda dataset;

	public OdaDatasetContentPage(String pageName, DataObjectOda dataset) {
		super(pageName);
		this.dataset = dataset;
	}

	public void createControl(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());

		createViewer(dataset);

		setControl(main);

	}

	public void createViewer(DataObjectOda dataSet) {
		if (viewer != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().dispose();
		}

		viewer = new TableViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				IResultSet rs = (IResultSet) inputElement;
				try {
					IResultSetMetaData mtd = rs.getMetaData();

					List<List<Object>> datas = new ArrayList<List<Object>>();

					while (rs.next()) {
						List<Object> row = new ArrayList<Object>();

						for (int i = 1; i <= mtd.getColumnCount(); i++) {
							row.add(rs.getString(i));
						}
						datas.add(row);
					}
					rs.close();
					query.close();
					return datas.toArray(new Object[datas.size()]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;

			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {

				try {
					return ((List) element).get(columnIndex) + ""; //$NON-NLS-1$
				} catch (Exception ex) {
					ex.printStackTrace();
					return "error"; //$NON-NLS-1$
				}
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

		});
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		main.getParent().layout(true);
		main.layout();
	}

}
