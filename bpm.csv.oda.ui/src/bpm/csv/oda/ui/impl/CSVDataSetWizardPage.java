/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.csv.oda.ui.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.nebula.widgets.pgroup.PGroup;
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

import bpm.csv.oda.runtime.datas.CsvColumn;
import bpm.csv.oda.runtime.impl.Connection;
import bpm.csv.oda.runtime.impl.ConnectionManager;
import bpm.csv.oda.runtime.impl.ResultSetMetaData;
import bpm.csv.oda.ui.Messages;
import bpm.vanilla.oda.commons.Activator;
import bpm.vanilla.oda.commons.ui.icons.IconsNames;

/**
 * Auto-generated implementation of an ODA data set designer page for an user to create or edit an ODA data set design instance. This custom page provides a simple Query Text control for user input. It further extends the DTP design-time framework to update an ODA data set design instance based on the query's derived meta-data. <br>
 * A custom ODA designer is expected to change this exemplary implementation as appropriate.
 */
public class CSVDataSetWizardPage extends DataSetWizardPage {
	private static String DEFAULT_MESSAGE = Messages.CSVDataSetWizardPage_0;
	private static ColumnComparator comparator = new ColumnComparator();

	private PGroup groupeCols;

	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll;
	private ListViewer listCol;

	// Column's Table viewer
	private TableViewer viewerColSelected;

	private List<CsvColumn> availableColumns = new ArrayList<CsvColumn>();
	private List<CsvColumn> selectedColumns = new ArrayList<CsvColumn>();

	/**
	 * Constructor
	 * 
	 * @param pageName
	 */
	public CSVDataSetWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setMessage(DEFAULT_MESSAGE);
	}

	/**
	 * Constructor
	 * 
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public CSVDataSetWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage(DEFAULT_MESSAGE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#createPageCustomControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPageCustomControl(Composite parent) {
		Composite inCompo = new Composite(parent, SWT.NONE);
		inCompo.setLayout(new GridLayout(1, false));
		setControl(createPageControl(inCompo));

		// if(getShell() != null){
		// getShell().setSize(700, 700);
		// }
		initializeControl();
	}

	/**
	 * Creates custom control for user-defined query text.
	 */
	private Control createPageControl(Composite parent) {
		setMessage(Messages.CSVDataSetWizardPage_1);
		setPageComplete(false);

		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		parent.setLayout(new GridLayout(2, false));

		// ------- Option : Select Columns
		groupeCols = new PGroup(parent, SWT.SMOOTH);
		groupeCols.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
		groupeCols.setLayout(new GridLayout(2, false));
		groupeCols.setText(Messages.CSVDataSetWizardPage_2);
		groupeCols.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_COLUMN));

		Label lbl = new Label(groupeCols, SWT.NONE);
		lbl.setText(Messages.CSVDataSetWizardPage_3);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, true, 2, 2));

		Composite groupListColumn = new Composite(groupeCols, SWT.BORDER);
		groupListColumn.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		groupListColumn.setLayout(new GridLayout(2, false));

		listCol = new ListViewer(groupListColumn, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
		listCol.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
		listCol.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

			public void dispose() {}

			public Object[] getElements(Object inputElement) {
				return ((List<CsvColumn>) inputElement).toArray();
			}
		});

		listCol.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((CsvColumn) element).getName();
			}

		});

		btnAdd = new Button(groupListColumn, SWT.PUSH);
		btnAdd.setToolTipText(Messages.CSVDataSetWizardPage_4);
		btnAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_ADD));

		btnRemove = new Button(groupListColumn, SWT.PUSH);
		btnRemove.setToolTipText(Messages.CSVDataSetWizardPage_5);
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemove.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_DELETE));

		btnAddAll = new Button(groupListColumn, SWT.PUSH);
		btnAddAll.setToolTipText(Messages.CSVDataSetWizardPage_6);
		btnAddAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAddAll.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_ADD_ALL));

		btnRemoveAll = new Button(groupListColumn, SWT.PUSH);
		btnRemoveAll.setToolTipText(Messages.CSVDataSetWizardPage_7);
		btnRemoveAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemoveAll.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_DELETE_ALL));

		// Init Liste Selection

		Composite groupListSelected = new Composite(groupeCols, SWT.NONE);
		groupListSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		groupListSelected.setLayout(new GridLayout(1, false));

		// Table viewer
		viewerColSelected = new TableViewer(groupListSelected, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewerColSelected.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 5));

		// Content Provider
		viewerColSelected.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				return ((List<CsvColumn>) inputElement).toArray();
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});

		// Table column : NAME
		TableViewerColumn column = new TableViewerColumn(viewerColSelected, SWT.NONE);
		column.getColumn().setText(Messages.CSVDataSetWizardPage_8);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((CsvColumn) element).getName();
			}

			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.ICO_GO_TABLE);
			}

		});

		// Table column : LABEL
		column = new TableViewerColumn(viewerColSelected, SWT.NONE);
		column.getColumn().setText(Messages.CSVDataSetWizardPage_9);
		// column.setEditingSupport(new MyEditingSupport(viewerColSelected));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((CsvColumn) element).getName();
			}

		});

		for(int i = 0, n = viewerColSelected.getTable().getColumnCount(); i < n; i++) {
			viewerColSelected.getTable().getColumn(i).setWidth(145);
		}

		viewerColSelected.getTable().setHeaderVisible(true);
		viewerColSelected.getTable().setLinesVisible(true);

		viewerColSelected.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			}
		});

		initListenersBtnTool();

		return parent;

	}

	// *************************** Listeners for toolbar's button
	private void initListenersBtnTool() {

		// Add selected columns
		btnAdd.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) listCol.getSelection();
				if(selection.isEmpty()) {
					return;
				}

				CsvColumn c = (CsvColumn) selection.getFirstElement();
				availableColumns.remove(c);
				selectedColumns.add(c);

				Collections.sort(availableColumns, comparator);
				Collections.sort(selectedColumns, comparator);

				listCol.setInput(availableColumns);
				viewerColSelected.setInput(selectedColumns);
				setPageComplete(true);
			}

		});

		// Add all columns

		btnAddAll.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {

				selectedColumns.addAll(availableColumns);
				availableColumns = new ArrayList<CsvColumn>();

				Collections.sort(selectedColumns, comparator);

				listCol.setInput(availableColumns);
				viewerColSelected.setInput(selectedColumns);
				setPageComplete(true);

			}
		});

		// Remove selected columns
		btnRemove.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) viewerColSelected.getSelection();
				if(selection.isEmpty()) {
					return;
				}
				CsvColumn c = (CsvColumn) selection.getFirstElement();
				selectedColumns.remove(c);
				availableColumns.add(c);

				Collections.sort(availableColumns, comparator);
				Collections.sort(selectedColumns, comparator);

				listCol.setInput(availableColumns);
				viewerColSelected.setInput(selectedColumns);
				setPageComplete(!selectedColumns.isEmpty());
			}

		});

		// Remove all columns
		btnRemoveAll.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				availableColumns.addAll(selectedColumns);
				selectedColumns = new ArrayList<CsvColumn>();

				Collections.sort(availableColumns, comparator);
				setPageComplete(false);

			}

		});

	}

	/**
	 * Initializes the page control with the last edited data set design.
	 */
	private void initializeControl() {

		Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

		try {
			IConnection conn = ConnectionManager.getConnection(connProps);

			fillDatas((Connection) conn);

		} catch(OdaException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}

		// Restores the last saved data set design
		DataSetDesign dataSetDesign = getInitializationDesign();

		if(dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")) { //$NON-NLS-1$
			return;
		}
		else {
			try {
				initSelection(dataSetDesign);
			} catch(DocumentException e) {
				e.printStackTrace();
			}
		}

	}

	private void initSelection(DataSetDesign dataSetDesign) throws DocumentException {

		String queryText = dataSetDesign.getQueryText();
		Document doc = DocumentHelper.parseText(queryText);
		Element root = doc.getRootElement();

		Element cols = root.element("columns"); //$NON-NLS-1$

		for(Object o : cols.elements("column")) { //$NON-NLS-1$
			Element e = (Element) o;
			int pos = new Integer(e.getStringValue()).intValue();
			CsvColumn c = availableColumns.get(pos);
			selectedColumns.add(c);
		}
		availableColumns.removeAll(selectedColumns);
		Collections.sort(availableColumns, comparator);
		Collections.sort(selectedColumns, comparator);

		listCol.setInput(availableColumns);
		viewerColSelected.setInput(selectedColumns);
		setPageComplete(!selectedColumns.isEmpty());

	}

	private void fillDatas(Connection conn) throws IOException, OdaException {
		IQuery query = conn.newQuery(null);
		ResultSetMetaData metadata = (ResultSetMetaData) query.getMetaData();
		availableColumns = metadata.getColumns();
		listCol.setInput(availableColumns);
	}

	/**
	 * Obtains the user-defined query text of this data set from page control.
	 * 
	 * @return query text
	 */
	private String getQueryText() {
		Element root = DocumentHelper.createElement("bpm.csv.oda.query"); //$NON-NLS-1$

		Element cols = root.addElement("columns"); //$NON-NLS-1$
		for(CsvColumn c : selectedColumns) {
			cols.addElement("column").setText(c.getPosition() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return root.asXML();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#collectDataSetDesign(org.eclipse.datatools.connectivity.oda.design.DataSetDesign)
	 */
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		if(getControl() == null) // page control was never created
			return design; // no editing was done
		if(!hasValidData())
			return null; // to trigger a design session error status
		savePage(design);
		return design;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#collectResponseState()
	 */
	protected void collectResponseState() {
		super.collectResponseState();
		/*
		 * To optionally assign a custom response state, for inclusion in the ODA design session response, use setResponseSessionStatus( SessionStatus status ); setResponseDesignerState( DesignerState customState );
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#canLeave()
	 */
	protected boolean canLeave() {
		return isPageComplete();
	}

	/**
	 * Validates the user-defined value in the page control exists and not a blank text. Set page message accordingly.
	 */
	private void validateData() {
		boolean isValid = (getQueryText() != null && getQueryText().trim().length() > 0);

		if(isValid)
			setMessage(DEFAULT_MESSAGE);
		else
			setMessage(Messages.CSVDataSetWizardPage_16, ERROR);

		setPageComplete(isValid);
	}

	/**
	 * Indicates whether the custom page has valid data to proceed with defining a data set.
	 */
	private boolean hasValidData() {
		validateData();

		return canLeave();
	}

	/**
	 * Saves the user-defined value in this page, and updates the specified dataSetDesign with the latest design definition.
	 */
	private void savePage(DataSetDesign dataSetDesign) {
		// save user-defined query text
		String queryText = getQueryText();
		dataSetDesign.setQueryText(queryText);

		// obtain query's current runtime metadata, and maps it to the dataSetDesign
		IConnection conn = null;
		try {

			java.util.Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

			conn = ConnectionManager.getConnection(connProps);

			updateDesign(dataSetDesign, conn, queryText);
		} catch(OdaException e) {
			// not able to get current metadata, reset previous derived metadata
			dataSetDesign.setResultSets(null);
			dataSetDesign.setParameters(null);

			e.printStackTrace();
		} finally {
			closeConnection(conn);
		}
	}

	/**
	 * Updates the given dataSetDesign with the queryText and its derived metadata obtained from the ODA runtime connection.
	 */
	private void updateDesign(DataSetDesign dataSetDesign, IConnection conn, String queryText) throws OdaException {
		IQuery query = conn.newQuery(null);
		query.prepare(queryText);

		try {
			IResultSetMetaData md = query.getMetaData();
			updateResultSetDesign(md, dataSetDesign);
		} catch(OdaException e) {
			// no result set definition available, reset previous derived metadata
			dataSetDesign.setResultSets(null);
			e.printStackTrace();
		}

		// proceed to get parameter design definition
		try {
			IParameterMetaData paramMd = query.getParameterMetaData();
			updateParameterDesign(paramMd, dataSetDesign);
		} catch(OdaException ex) {
			// no parameter definition available, reset previous derived metadata
			dataSetDesign.setParameters(null);
			ex.printStackTrace();
		}

		/*
		 * See DesignSessionUtil for more convenience methods to define a data set design instance.
		 */
	}

	/**
	 * Updates the specified data set design's result set definition based on the specified runtime metadata.
	 * 
	 * @param md
	 *            runtime result set metadata instance
	 * @param dataSetDesign
	 *            data set design instance to update
	 * @throws OdaException
	 */
	private void updateResultSetDesign(IResultSetMetaData md, DataSetDesign dataSetDesign) throws OdaException {
		ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign(md);

		ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE.createResultSetDefinition();
		resultSetDefn.setResultSetColumns(columns);

		// no exception in conversion; go ahead and assign to specified dataSetDesign
		dataSetDesign.setPrimaryResultSet(resultSetDefn);
		dataSetDesign.getResultSets().setDerivedMetaData(true);
	}

	/**
	 * Updates the specified data set design's parameter definition based on the specified runtime metadata.
	 * 
	 * @param paramMd
	 *            runtime parameter metadata instance
	 * @param dataSetDesign
	 *            data set design instance to update
	 * @throws OdaException
	 */
	private void updateParameterDesign(IParameterMetaData paramMd, DataSetDesign dataSetDesign) throws OdaException {

	}

	/**
	 * Attempts to close given ODA connection.
	 */
	private void closeConnection(IConnection conn) {
		try {
			if(conn != null && conn.isOpen()) {
				conn.close();
			}
		} catch(OdaException e) {
			// ignore
			e.printStackTrace();
		}
	}

}
