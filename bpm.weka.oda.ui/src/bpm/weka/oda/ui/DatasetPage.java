/*
 *************************************************************************
 * Copyright (c) 2013 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.weka.oda.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import weka.core.Attribute;
import bpm.vanilla.oda.commons.Activator;
import bpm.vanilla.oda.commons.ui.icons.IconsNames;
import bpm.weka.oda.runtime.Connection;
import bpm.weka.oda.runtime.Driver;

import com.thoughtworks.xstream.XStream;

/**
 * Auto-generated implementation of an ODA data set designer page for an user to
 * create or edit an ODA data set design instance. This custom page provides a
 * simple Query Text control for user input. It further extends the DTP
 * design-time framework to update an ODA data set design instance based on the
 * query's derived meta-data. <br>
 * A custom ODA designer is expected to change this exemplary implementation as
 * appropriate.
 */
public class DatasetPage extends DataSetWizardPage {
	
	private static String DEFAULT_MESSAGE = "Define the query text for the data set";

	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll;
	private ListViewer listCol;

	// Column's Table viewer
	private TableViewer viewerColSelected;

	private List<Attribute> availableColumns = new ArrayList<Attribute>();
	private List<Attribute> selectedColumns = new ArrayList<Attribute>();

	/**
	 * Constructor
	 * 
	 * @param pageName
	 */
	public DatasetPage(String pageName) {
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
	public DatasetPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setMessage(DEFAULT_MESSAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage
	 * #createPageCustomControl(org.eclipse.swt.widgets.Composite)
	 */
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
		setMessage("Select Columns.");
		setPageComplete(false);

		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		parent.setLayout(new GridLayout(2, false));

		// ------- Option : Select Columns
		Group groupeCols = new Group(parent, SWT.V_SCROLL);
		groupeCols.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
		groupeCols.setLayout(new GridLayout(2, false));
		groupeCols.setText("Column's Options");

		Label lbl = new Label(groupeCols, SWT.NONE);
		lbl.setText("Select Columns to display themself.");
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
				return ((List<Attribute>) inputElement).toArray();
			}
		});

		listCol.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Attribute) element).name();
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
				return ((List<Attribute>) inputElement).toArray();
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});

		// Table column : NAME
		TableViewerColumn column = new TableViewerColumn(viewerColSelected, SWT.NONE);
		column.getColumn().setText("Column's Name");
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Attribute) element).name();
			}

			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.ICO_GO_TABLE);
			}

		});

		// Table column : LABEL
		column = new TableViewerColumn(viewerColSelected, SWT.NONE);
		column.getColumn().setText("Column's label");
		// column.setEditingSupport(new MyEditingSupport(viewerColSelected));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Attribute) element).name();
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

				Attribute c = (Attribute) selection.getFirstElement();
				availableColumns.remove(c);
				selectedColumns.add(c);

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
				availableColumns = new ArrayList<Attribute>();

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
				Attribute c = (Attribute) selection.getFirstElement();
				selectedColumns.remove(c);
				availableColumns.add(c);

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
				selectedColumns = new ArrayList<Attribute>();
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

			IConnection conn = new Driver().getConnection("");
			conn.open(connProps);

			fillDatas((Connection) conn);

		} catch(Exception e) {
			e.printStackTrace();
		}

		// Restores the last saved data set design
		DataSetDesign dataSetDesign = getInitializationDesign();

		if(dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")) { //$NON-NLS-1$
			return;
		}
		else {
			
			initSelection(dataSetDesign);
		}

	}

	private void initSelection(DataSetDesign dataSetDesign) {

		String queryText = dataSetDesign.getQueryText();
		List<Attribute> attributes = (List<Attribute>) new XStream().fromXML(queryText);

		for(Attribute o : attributes) {
			selectedColumns.add(o);
		}
		availableColumns.removeAll(selectedColumns);

		listCol.setInput(availableColumns);
		viewerColSelected.setInput(selectedColumns);
		setPageComplete(!selectedColumns.isEmpty());

	}

	private void fillDatas(Connection conn) throws Exception {
		availableColumns = conn.getWekaFile().getColumns();
		listCol.setInput(availableColumns);
	}

	/**
	 * Obtains the user-defined query text of this data set from page control.
	 * 
	 * @return query text
	 */
	private String getQueryText() {

		return new XStream().toXML(selectedColumns);
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
			setMessage("Requires input value.", ERROR);

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

			conn = new Driver().getConnection("");
			conn.open(connProps);

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
