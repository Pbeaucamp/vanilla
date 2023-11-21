package bpm.nosql.oda.ui.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ParameterDefinition;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import bpm.nosql.oda.runtime.impl.Connection;
import bpm.nosql.oda.runtime.impl.ConnectionManager;
import bpm.nosql.oda.runtime.impl.Query;
import bpm.nosql.oda.runtime.impl.ResultSetMetaData;
import bpm.vanilla.oda.commons.Activator;
import bpm.vanilla.oda.commons.ui.icons.IconsNames;

public class NOSQLDataSetWizardPage extends DataSetWizardPage {

	private static String DEFAULT_MESSAGE = "Define the query text for the data set";
	private TableViewer viewerColSelected;
	private ListViewer listCol;
	private ArrayList<String> availableColumns;
	private ArrayList<String> selectedColumns;
	private Properties connProps;
	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll;
	private Query query;
	private ResultSetMetaData metadata;
	private Connection conn;

	public NOSQLDataSetWizardPage() {
		super("");

	}

	public NOSQLDataSetWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setMessage(DEFAULT_MESSAGE);
	}

	@Override
	public void createPageCustomControl(Composite parent) {

		Composite inCompo = new Composite(parent, SWT.NONE);
		inCompo.setLayout(new GridLayout(1, false));
		setControl(createPageControl(inCompo));

//		if (getShell() != null) {
//			getShell().setSize(700, 700);
//		}

		initializeControl();
	}

	private void initializeControl() {

		connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

		try {
			conn = (Connection) ConnectionManager.getConnection(connProps);

			fillDatas((Connection) conn);

		} catch (OdaException e) {
			e.printStackTrace();
		}

		DataSetDesign dataSetDesign = getInitializationDesign();

		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")) { //$NON-NLS-1$
			return;
		}
		else {
			initSelection(dataSetDesign);
		}
		validateData();
	}

	private void initSelection(DataSetDesign dataSetDesign) {

		String queryText = dataSetDesign.getQueryText();
		if (selectedColumns == null) {
			selectedColumns = new ArrayList<String>();
		}
		
		try {
			List<String> columns = Arrays.asList(queryText.substring(queryText.indexOf("select ") + 7, queryText.indexOf(" from ")).split(","));
			for (String col : columns) {
				selectedColumns.add(col);
				availableColumns.remove(col);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		viewerColSelected.setInput(selectedColumns);
		listCol.setInput(availableColumns);
	}

	private void fillDatas(Connection conn) throws OdaException {

		query = conn.newQuery(null);

		metadata = (ResultSetMetaData) query.getMetaData();

		try {
			availableColumns = metadata.getColumns();

		} catch (Exception e) {
			e.printStackTrace();
		}
		listCol.setInput(availableColumns);
	}

	private Control createPageControl(Composite parent) {

		setPageComplete(false);

		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		parent.setLayout(new GridLayout(2, false));

		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		comp.setLayout(new GridLayout(2, false));

		Label lbl = new Label(comp, SWT.NONE);
		lbl.setText("Select Columns to display themself.");
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, false, 2, 1));

		Composite groupListColumn = new Composite(comp, SWT.BORDER);
		groupListColumn.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		groupListColumn.setLayout(new GridLayout(2, false));

		listCol = new ListViewer(groupListColumn, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
		listCol.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
		listCol.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				ArrayList<Object> input = (ArrayList<Object>) inputElement;

				return input.toArray();
			}
		});

		listCol.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((String) element);
			}

		});

		btnAdd = new Button(groupListColumn, SWT.PUSH);
		btnAdd.setToolTipText("Add a column");
		btnAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_ADD));

		btnRemove = new Button(groupListColumn, SWT.PUSH);
		btnRemove.setToolTipText("Remove a column");
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemove.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_DELETE));

		btnAddAll = new Button(groupListColumn, SWT.PUSH);
		btnAddAll.setToolTipText("Add All columns");
		btnAddAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAddAll.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_ADD_ALL));

		btnRemoveAll = new Button(groupListColumn, SWT.PUSH);
		btnRemoveAll.setToolTipText("Remove All columns");
		btnRemoveAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemoveAll.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_DELETE_ALL));

		Composite groupListSelected = new Composite(comp, SWT.NONE);
		groupListSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		groupListSelected.setLayout(new GridLayout(1, false));

		// Table viewer
		viewerColSelected = new TableViewer(groupListSelected, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewerColSelected.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));

		// Content Provider
		viewerColSelected.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				ArrayList<Object> input = (ArrayList<Object>) inputElement;
				return input.toArray();
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});

		TableViewerColumn column = new TableViewerColumn(viewerColSelected, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		column.getColumn().setText("Column's Name");
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return (String) element;
			}

			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.ICO_GO_TABLE);
			}

		});

		column = new TableViewerColumn(viewerColSelected, SWT.NONE);
		column.getColumn().setText("Column's label");
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return (String) element;
			}

		});

		for (int i = 0, n = viewerColSelected.getTable().getColumnCount(); i < n; i++) {
			viewerColSelected.getTable().getColumn(i).setWidth(145);
		}

		viewerColSelected.getTable().setHeaderVisible(true);
		viewerColSelected.getTable().setLinesVisible(true);

		viewerColSelected.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.isEmpty()) {
					return;
				}
				Object o = selection.getFirstElement();
			}
		});

		initListenersBtnTool();

		return parent;
	}

	private void initListenersBtnTool() {
		btnAdd.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) listCol.getSelection();
				if (selection.isEmpty()) {
					return;
				}

				Iterator<Object> it = selection.iterator();
				Object c;
				while (it.hasNext()) {
					c = it.next();
					availableColumns.remove(c);

					if (selectedColumns == null) {
						selectedColumns = new ArrayList<String>();
					}
					selectedColumns.add((String) c);
					query.setSelectColumns(selectedColumns);
				}

				listCol.setInput(availableColumns);
				viewerColSelected.setInput(selectedColumns);

				setPageComplete(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		btnRemove.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) viewerColSelected.getSelection();
				if (selection.isEmpty()) {
					return;
				}

				Object c = selection.getFirstElement();

				selectedColumns.remove(c);
				availableColumns.add((String) c);
				listCol.setInput(availableColumns);

				viewerColSelected.setInput(selectedColumns);
				setPageComplete(!selectedColumns.isEmpty());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		btnAddAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedColumns == null) {
					selectedColumns = new ArrayList<String>();
				}
				selectedColumns.addAll(availableColumns);
				availableColumns = new ArrayList<String>();

				listCol.setInput(availableColumns);
				viewerColSelected.setInput(selectedColumns);

				setPageComplete(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		btnRemoveAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				availableColumns.addAll(selectedColumns);
				selectedColumns = new ArrayList<String>();

				listCol.setInput(availableColumns);
				viewerColSelected.setInput(selectedColumns);
				setPageComplete(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}



	private void closeConnection(IConnection conn) {
		try {
			if (conn != null && conn.isOpen())
				conn.close();
		} catch (OdaException e) {
			e.printStackTrace();
		}
	}

	protected DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {

		String queryText = getQueryText();
		dataSetDesign.setQueryText(queryText);

		IConnection conn = null;

		try {

			java.util.Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

			conn = ConnectionManager.getConnection(connProps);

			updateDesign(dataSetDesign, conn, queryText);
		} catch (OdaException e) {
			dataSetDesign.setResultSets(null);
			dataSetDesign.setParameters(null);

			e.printStackTrace();
		}

		finally {
			closeConnection(conn);
		}
		return dataSetDesign;
	}

	private boolean hasValidData() {
		validateData();

		return canLeave();
	}

	private void validateData() {

		boolean isValid = (getQueryText() != null && getQueryText().trim().length() > 0);

		if (isValid)
			setMessage(DEFAULT_MESSAGE);
		else
			setMessage("Requires input value.", ERROR);

		setPageComplete(isValid);
	}

	private String getQueryText() {

		StringBuilder b = new StringBuilder();

		b.append("select ");
		for(String col:selectedColumns) {
			b.append(col + ",");
		}
		String s = b.substring(0, b.length() - 1).toString();
		b = new StringBuilder();
		b.append(s + " from ");
		b.append(connProps.getProperty(Connection.DATABASE));
		
		return b.toString();
	}

	private void updateDesign(DataSetDesign dataSetDesign, IConnection conn, String queryText) throws OdaException {
		Query query = (Query) conn.newQuery(null);
		query.prepare(queryText);
		if (query.getSelectColumns() != null && !query.getSelectColumns().isEmpty()) {
			selectedColumns = (ArrayList<String>) query.getSelectColumns();
		}
		try {
			IResultSetMetaData md = query.getMetaData();
			updateResultSetDesign(md, dataSetDesign);
		} catch (OdaException e) {
			dataSetDesign.setResultSets(null);
			e.printStackTrace();
		}

		try {
			IParameterMetaData paramMd = query.getParameterMetaData();
			updateParameterDesign(paramMd, dataSetDesign);
		} catch (OdaException ex) {
			dataSetDesign.setParameters(null);
			ex.printStackTrace();
		}
	}

	private void updateResultSetDesign(IResultSetMetaData md, DataSetDesign dataSetDesign) throws OdaException {
		ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign(md);

		ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE.createResultSetDefinition();
		resultSetDefn.setResultSetColumns(columns);

		dataSetDesign.setPrimaryResultSet(resultSetDefn);
		dataSetDesign.getResultSets().setDerivedMetaData(true);
	}

	private void updateParameterDesign(IParameterMetaData paramMd, DataSetDesign dataSetDesign) throws OdaException {
		DataSetParameters paramDesign = DesignSessionUtil.toDataSetParametersDesign(paramMd, DesignSessionUtil.toParameterModeDesign(1));

		dataSetDesign.setParameters(paramDesign);

		if (paramDesign == null) {
			return;
		}
		paramDesign.setDerivedMetaData(true);

		if (paramDesign.getParameterDefinitions().size() > 0) {
			ParameterDefinition paramDef = (ParameterDefinition) paramDesign.getParameterDefinitions().get(0);

			if (paramDef != null) {
				paramDef.setDefaultScalarValue("dummy default value");
			}
		}
	}

	protected boolean canLeave() {

		return isPageComplete();
	}
}
