package bpm.fwr.oda.ui.impl.dataset;

import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.oda.runtime.impl.ConnectionManager;
import bpm.fwr.oda.runtime.impl.DatasHelper;
import bpm.vanilla.platform.core.IRepositoryApi;

public class FwrDataSetPage extends DataSetWizardPage {
	
	private TableViewer tableViewer, tableDataSetViewer, tableFWRFilters;
	private Document document;

	public FwrDataSetPage(String pageName) {
		super(pageName);
	}

	public FwrDataSetPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		setControl(createPageControl(parent));

		try {
			initialiseControl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Control createPageControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, true));

		ISelectionChangedListener selLst = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof DataSet)) {
					return;
				}
				Object inputColumn = ((DataSet) ss.getFirstElement()).getColumns();
				Object inputFilter = ((DataSet) ss.getFirstElement()).getFwrFilters();
				tableDataSetViewer.setInput(inputColumn);
				tableFWRFilters.setInput(inputFilter);
			}

		};

		tableViewer = new TableViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			@Override
			public void dispose() { }

			@Override
			public Object[] getElements(Object inputElement) {
				List<DataSet> dataSets = (List<DataSet>) inputElement;
				return dataSets.toArray(new Object[dataSets.size()]);
			}
		});

		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DataSet) {
					return ((DataSet) element).getName();
				}
				return null;
			}
		});
		tableViewer.addSelectionChangedListener(selLst);

		TabFolder folder = new TabFolder(container, SWT.NONE);
		folder.setLayout(new GridLayout(2, false));
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));

		tableDataSetViewer = new TableViewer(folder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		tableDataSetViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		tableDataSetViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			@Override
			public void dispose() { }

			@Override
			public Object[] getElements(Object inputElement) {
				List<Column> columns = (List<Column>) inputElement;
				return columns.toArray(new Object[columns.size()]);
			}
		});
		tableDataSetViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Column) {
					return ((Column) element).getName();
				}
				return null;
			}
		});

		TabItem tabDataSet = new TabItem(folder, SWT.NONE);
		tabDataSet.setText("DataSet");
		tabDataSet.setControl(tableDataSetViewer.getTable());

		tableFWRFilters = new TableViewer(folder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableFWRFilters.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		tableFWRFilters.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

			@Override
			public void dispose() { }

			@Override
			public Object[] getElements(Object inputElement) {
				List<FWRFilter> columns = (List<FWRFilter>) inputElement;
				return columns.toArray(new Object[columns.size()]);
			}
		});
		tableFWRFilters.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void removeListener(ILabelProviderListener listener) { }

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void dispose() { }

			@Override
			public void addListener(ILabelProviderListener listener) { }

			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
					case 0:
						return ((FWRFilter) element).getName();
					case 1:
						return (((FWRFilter) element).getColumnName());
					case 2:
						if (((FWRFilter) element).getOperator() != null)
							return ((FWRFilter) element).getOperator();
						else
							return "";
					case 3:
						List<String> values = ((FWRFilter) element).getValues();
						String value = null;
						if (!values.isEmpty()) {
							if (values.size() == 1) {
								value = values.get(0);
							}
							else {
								for (String str : values) {
									value = value + " + " + str;
								}
							}
						}
						return value;
				}
				return "";
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});

		TabItem tabFilter = new TabItem(folder, SWT.NONE);
		tabFilter.setText("FWR Filters");
		tabFilter.setControl(tableFWRFilters.getTable());

		TableColumn name = new TableColumn(tableFWRFilters.getTable(), SWT.NONE);
		name.setText("Name");
		name.setWidth(80);

		TableColumn format = new TableColumn(tableFWRFilters.getTable(), SWT.NONE);
		format.setText("DataSet Column");
		format.setWidth(250);

		TableColumn comment = new TableColumn(tableFWRFilters.getTable(), SWT.NONE);
		comment.setText("Operator");
		comment.setWidth(100);

		TableColumn version = new TableColumn(tableFWRFilters.getTable(), SWT.NONE);
		version.setText("Values");
		version.setWidth(80);

		tableFWRFilters.getTable().setHeaderVisible(true);
		tableFWRFilters.getTable().setLinesVisible(true);

		return container;
	}

	private void initialiseControl() throws Exception {
		FWRReport model = initialiseFwrReport();

		// Restores the last saved data set design
		DataSetDesign dataSetDesign = getInitializationDesign();

		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")) { //$NON-NLS-1$
			List<DataSet> dataSets = model.getAllDatasets();
			Object input = dataSets;

			tableViewer.setInput(input);
		}
		else {
			String datasetname = restoreSelection(dataSetDesign, model);

			List<DataSet> dataSets = model.getAllDatasets();
			DataSet dataset = null;
			boolean found = false;
			for (DataSet ds : dataSets) {
				if (ds.getName().equals(datasetname)) {
					found = true;
					dataset = ds;
				}
			}

			if (found) {
				Object datasetobject = dataset;
				Object input = dataSets;
				tableViewer.setInput(input);
				tableViewer.setSelection(new StructuredSelection(datasetobject));
			}
			else {
				throw new Exception("The dataset does not exist");
			}

		}
	}

	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
		IRepositoryApi conn = null;
		try {
			conn = ConnectionManager.getConnection(connProps);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DataSet ds = null;
		FWRReport model = initialiseFwrReport();

		// Restores the last saved data set design
		DataSetDesign dataSetDesign = getInitializationDesign();
		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")) { //$NON-NLS-1$	
			IStructuredSelection ss = (IStructuredSelection) tableViewer.getSelection();
			if (ss.isEmpty() || !(ss.getFirstElement() instanceof DataSet)) {
				MessageDialog.openError(getShell(), "Error", "No DataSet has been choose.");
			}
			ds = ((DataSet) ss.getFirstElement());
		}
		else {
			String datasetname = restoreSelection(dataSetDesign, model);

			List<DataSet> dataSets = model.getAllDatasets();
			DataSet dataset = null;
			boolean found = false;
			for (DataSet datas : dataSets) {
				if (datas.getName().equals(datasetname)) {
					found = true;
					ds = datas;
				}
			}

		}
		ds.getDatasource().setUrl(conn.getContext().getRepository().getUrl());
		String propertiesPart = ds.getDatasource().getXml();
		String query = DatasHelper.getInstance().buildQuery(ds, conn);
		query = "<rootElement>\n<dataSet>\n<dataSetName>" + ds.getName() + "</dataSetName>\n</dataSet>\n" + propertiesPart + query + "</rootElement>";
		design.setQueryText(query);
		return design;
	}

	private FWRReport initialiseFwrReport() {
		Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

		FWRReport model = null;
		try {
			model = ConnectionManager.getFWRReport(connProps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	private String restoreSelection(DataSetDesign dataSetDesign, FWRReport model) {
		try {
			document = DocumentHelper.parseText(dataSetDesign.getQueryText());
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		Element e = document.getRootElement().element("dataSet");
		if (e.element("dataSetName") != null) {
			Element d = e.element("dataSetName");
			if (d != null) {
				return d.getStringValue();
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}

	}
}
