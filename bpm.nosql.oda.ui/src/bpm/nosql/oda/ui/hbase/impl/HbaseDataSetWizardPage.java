package bpm.nosql.oda.ui.hbase.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

import bpm.nosql.oda.runtime.impl.ConnectionManager;
import bpm.nosql.oda.runtime.impl.HbaseConnection;
import bpm.nosql.oda.runtime.impl.HbaseQuery;
import bpm.nosql.oda.runtime.impl.HbaseResultSetMetaData;
import bpm.vanilla.oda.commons.Activator;
import bpm.vanilla.oda.commons.ui.icons.IconsNames;

public class HbaseDataSetWizardPage extends DataSetWizardPage{

	private static String DEFAULT_MESSAGE = "Define the query text for the data set";
	private Properties connProps;
	private HbaseConnection conn;
	private ArrayList<String> tablesNames, familiesList, availableColumns, selectedColumns;
	private ComboViewer comboTable, comboFamily;
	private String selectedTable, selectedFamily;
	private HTableDescriptor[] listTableDescriptor;
	private Collection<HColumnDescriptor> listFamilies;
	private TableViewer viewerColSelected;
	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll;
	private ListViewer listCol;
	private HbaseQuery query;	
	private HbaseResultSetMetaData metadata;
	
	public HbaseDataSetWizardPage() {
		super("");
	}
	
	public HbaseDataSetWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setMessage(DEFAULT_MESSAGE);
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		Composite inCompo = new Composite(parent, SWT.NONE);
		inCompo.setLayout(new GridLayout(1, false));
		setControl(createPageControl(inCompo));

		initializeControl();
	}

	private void initializeControl() {
		connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
		
		try {
			 conn = (HbaseConnection) ConnectionManager.getConnection(connProps);
			
				
			fillDatas((HbaseConnection)conn);
			
		} catch (OdaException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DataSetDesign dataSetDesign = getInitializationDesign();
	
		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$
			return;
		}
		else {			
				initSelection(dataSetDesign);	
		}
		validateData();		
	}

	private void initSelection(DataSetDesign dataSetDesign) {

		String queryText = dataSetDesign.getQueryText();
		
		List<String> columns = Arrays.asList(queryText.substring(queryText.indexOf("select ") + 7, queryText.indexOf(" from ")).split(","));

		comboTable.setSelection(new StructuredSelection(queryText.substring(queryText.indexOf(" from ")+6, queryText.indexOf("."))));
		comboFamily.setSelection(new StructuredSelection(queryText.substring(queryText.indexOf(".")+1)));
		
		if (selectedColumns == null) {
			selectedColumns = new ArrayList<String>();
		}
		for (String col : columns) {

			selectedColumns.add(col);
			availableColumns.remove(col);

		}

		viewerColSelected.setInput(selectedColumns);
		listCol.setInput(availableColumns);
	}

	private void validateData() {

		boolean isValid = (getQueryText() != null && ((String) getQueryText()).trim().length() > 0);

		if (isValid)
			setMessage(DEFAULT_MESSAGE);
		else
			setMessage("Requires input value.", ERROR);

		setPageComplete(isValid);
	}

	private Object getQueryText() {
		StringBuilder b = new StringBuilder();

		b.append("select ");
		for(String col:selectedColumns) {
			b.append(col + ",");
		}
		String s = b.substring(0, b.length() - 1).toString();
		b = new StringBuilder();
		b.append(s + " from ");
		b.append(selectedTable+"."+selectedFamily);
		
		return b.toString();
		
	}

	private void fillDatas(HbaseConnection conn) throws Exception {
		query = (HbaseQuery) conn.newQuery(null);

		metadata = (HbaseResultSetMetaData) query.getMetaData();
		
		listTableDescriptor = conn.getListTables();
		tablesNames = new ArrayList<String>();
		
		availableColumns = metadata.getColumns();
		
			for(HTableDescriptor tableDescr : listTableDescriptor){
				tablesNames.add(tableDescr.getNameAsString());
			}
		
		comboTable.setInput(tablesNames);
		
	}

	private Control createPageControl(Composite parent) {
		setPageComplete(false);
		
		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		parent.setLayout(new GridLayout(2, false));
		
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));
		
		Label lblTables = new Label(composite, SWT.NONE);
		lblTables.setText("Select Table");
		lblTables.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, false, 1, 1));

		comboTable = new ComboViewer(composite, SWT.SINGLE | SWT.READ_ONLY);
		comboTable.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		comboTable.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				Object[] currentElement = ((ArrayList<String>) inputElement).toArray();
				return currentElement;
			}

			@Override
			public void dispose() {
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
			}
			
		});
		
		comboTable.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				
				return  (String)element;
			}
			
		});
		
		comboTable.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) comboTable.getSelection();
					if (selection.isEmpty()) {
						return;
					}
					
				Object o = selection.getFirstElement();
				selectedTable = (String)o;
				getFamilys(selectedTable);
				query.setSelectedTable(selectedTable);
			}

			private void getFamilys(String table) {
				try {
					
					for(HTableDescriptor tableDescr : listTableDescriptor){
						if(tableDescr.getNameAsString().equals(table)){
							listFamilies = tableDescr.getFamilies();
							
							familiesList = new ArrayList<String>();
							for(HColumnDescriptor families : listFamilies){
								familiesList.add(families.getNameAsString());
							}
							comboFamily.setInput(familiesList);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText("Select Columns familie.");
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, false, 2, 1));
		
		comboFamily = new ComboViewer(composite, SWT.SINGLE | SWT.READ_ONLY);
		comboFamily.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		comboFamily.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				Object[] currentElement = ((ArrayList<String>) inputElement).toArray();
				return currentElement;
			}

			@Override
			public void dispose() {
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
			}
			
		});
		
		comboFamily.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return super.getText(element);
			}
		});
		
		comboFamily.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) comboFamily.getSelection();
					if (selection.isEmpty()) {
						return;
					}
				if(selectedColumns != null){
					selectedColumns.clear();
					viewerColSelected.refresh();
					
				}
				
					
				Object o = selection.getFirstElement();
				selectedFamily = (String)o;
				try {
					getColumns(listTableDescriptor, listFamilies);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				query.setSelectedFamilies(selectedFamily);
			}

			private void getColumns(HTableDescriptor[] listTableDescriptor,
					Collection<HColumnDescriptor> listFamilies) throws Exception {
					for(HColumnDescriptor column : listFamilies){
						if(column.getNameAsString().equals(selectedFamily)){
							HTable table;
							Scan s;
							ResultScanner result = null;
							try {
								table = new HTable(conn.getConfig(), selectedTable);
								s = new Scan();
								s.addFamily(Bytes.toBytes(selectedFamily));
								
								result = table.getScanner(s);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							Result r = result.next();
							
							availableColumns = new ArrayList<String>();
							
								  for (KeyValue kv : r.list()) {
									  
									  availableColumns.add(Bytes.toString(kv.getQualifier()));
									  
								  }
								
							listCol.setInput(availableColumns);
						
						}
					}
				
			}
			
		});
		


		Composite groupListColumn = new Composite(composite, SWT.BORDER);
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

		Composite groupListSelected = new Composite(composite, SWT.NONE);
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
		
		return composite;
	}
	
	private void closeConnection(IConnection conn){
		try {
			if((conn != null) && (conn.isOpen())){
				conn.close();
			}
		} catch (OdaException e) {
			e.printStackTrace();
		}
	}
	
	protected DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {

		String queryText = (String) getQueryText();
		dataSetDesign.setQueryText(queryText);

		IConnection conn = null;

		try {

			connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
			
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
	
	private void updateDesign(DataSetDesign dataSetDesign, IConnection conn2,
			String queryText) throws OdaException {
		
		query.prepare(queryText);
		
			if (query.getSelectColumns() != null && !query.getSelectColumns().isEmpty() ) {
				selectedColumns = (ArrayList<String>) query.getSelectColumns();
			}else{
				query.setSelectColumns(selectedColumns);
				
			}
			
		try {
			IResultSetMetaData md = new HbaseResultSetMetaData(this.query);
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

	private void updateParameterDesign(IParameterMetaData paramMd,
			DataSetDesign dataSetDesign) throws OdaException {
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

	private void updateResultSetDesign(IResultSetMetaData md,
			DataSetDesign dataSetDesign) throws OdaException {
		
		ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign(md);

		ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE.createResultSetDefinition();
		resultSetDefn.setResultSetColumns(columns);

		dataSetDesign.setPrimaryResultSet(resultSetDefn);
		dataSetDesign.getResultSets().setDerivedMetaData(true);
		
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
					
				}
				query.setSelectColumns(selectedColumns);
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
				query.setSelectColumns(selectedColumns);
				setPageComplete(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		btnRemoveAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				availableColumns.addAll(selectedColumns);
				selectedColumns = new ArrayList<String>();

				listCol.setInput(availableColumns);
				viewerColSelected.setInput(selectedColumns);
				setPageComplete(false);
			}


		});
	}
	
	@Override
	protected boolean canLeave() {
		
		return isPageComplete();
	}
}
