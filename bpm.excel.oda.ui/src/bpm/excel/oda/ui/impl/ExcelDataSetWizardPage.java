/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.excel.oda.ui.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

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
import org.eclipse.swt.widgets.Table;

import bpm.excel.oda.runtime.impl.Connection;
import bpm.excel.oda.runtime.impl.ConnectionManager;
import bpm.vanilla.oda.commons.Activator;
import bpm.vanilla.oda.commons.ui.icons.IconsNames;


/**
 * Auto-generated implementation of an ODA data set designer page
 * for an user to create or edit an ODA data set design instance.
 * This custom page provides a simple Query Text control for user input.  
 * It further extends the DTP design-time framework to update
 * an ODA data set design instance based on the query's derived meta-data.
 * <br>
 * A custom ODA designer is expected to change this exemplary implementation 
 * as appropriate. 
 */
public class ExcelDataSetWizardPage extends DataSetWizardPage {
	private static final CellComparator cellComparator = new CellComparator();
	
	private static String DEFAULT_MESSAGE = "Define the query text for the data set";

	private PGroup groupeCols, groupSheets;
	private ComboViewer comboSheets;
	private Button btnSkip;

	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll,btnAddParam,btnDelParam;
	private ListViewer listCol;

	// Column's Table viewer
	private TableViewer viewerColSelected ;
	

	// Parameter's Table viewer
	private TableViewer viewerParameters;
	private Table tableParameters;
	private IStructuredSelection selectionParam;
	
	private List<Cell> availableCells = new ArrayList<Cell>();
	private List<Cell> selectedCells = new ArrayList<Cell>();
	private Sheet selectedSheet;
	private Workbook workbookin;

	/**
	 * Constructor
	 * @param pageName
	 */
	public ExcelDataSetWizardPage( String pageName ) {
		super( pageName );
		setTitle( pageName );
		setMessage( DEFAULT_MESSAGE );
	}

	/**
	 * Constructor
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public ExcelDataSetWizardPage( String pageName, String title, ImageDescriptor titleImage ) {
		super( pageName, title, titleImage );
		setMessage( DEFAULT_MESSAGE );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#createPageCustomControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPageCustomControl( Composite parent ) {
		Composite inCompo = new Composite(parent, SWT.NONE);
		inCompo.setLayout(new GridLayout(1, false)); 
		setControl( createPageControl(inCompo));

//		if(getShell() != null){
//			getShell().setSize(700, 700);
//		}
		initializeControl();
	}

	/**
	 * Creates custom control for user-defined query text.
	 */
	private Control createPageControl( Composite parent ) {


		setMessage("Choose a Sheet, and apply if needed filters.");
		setPageComplete(false);



		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		parent.setLayout(new GridLayout(2, false));	


		//**** Group to select a sheet

		groupSheets = new PGroup(parent, SWT.SMOOTH);
		groupSheets.setText("Sheet Selection");
		groupSheets.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
		groupSheets.setLayout(new GridLayout(3, false));
		groupSheets.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_VIEW_LIST));

		Label lbl = new Label(groupSheets, SWT.NONE);
		lbl.setText("Choose a Sheet:");
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, false, 1, 1));

		comboSheets = new ComboViewer(groupSheets, SWT.SINGLE | SWT.READ_ONLY);
		comboSheets.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));


		comboSheets.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Sheet[] currentElement = (Sheet[])inputElement;
				return currentElement;
			}
		});
		
		comboSheets.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Sheet) element).getName();
			}
			
			
		});
		
		comboSheets.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				availableCells = new ArrayList<Cell>();
				selectedCells = new ArrayList<Cell>();
				viewerColSelected.setInput(selectedCells);
				listCol.setInput(availableCells);
				IStructuredSelection selection = (IStructuredSelection) comboSheets.getSelection();
				if (selection.isEmpty()) {
					return;
				}
				
				Object o = selection.getFirstElement();
				
				selectedSheet = (Sheet) o;
				int nbCol = selectedSheet.getColumns();
				
				for (int i = 0; i < nbCol; i++) {
					if (selectedSheet.getCell(i, 0).getContents().equalsIgnoreCase("")) {
						continue;
					}
					availableCells.add(selectedSheet.getCell(i, 0));		
				}

				listCol.setInput(availableCells);
				
			}
		});

		//------- Option : Skip the first row
//		btnSkip = new Button(groupSheets,SWT.CHECK);
//		btnSkip.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
//		btnSkip.setText("Skip the first row If this row is columns name");
//		btnSkip.setEnabled(false);

		lbl = new Label(parent, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,3, 1));


		//------- Option : Select Columns
		groupeCols = new PGroup(parent, SWT.SMOOTH);
		groupeCols.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
		groupeCols.setLayout(new GridLayout(2, false));
		groupeCols.setText("Column's Options");
		groupeCols.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_COLUMN));


		lbl = new Label(groupeCols, SWT.NONE);
		lbl.setText("Select Columns to display themself. Rename them if needed to edit the \" Column's Label\" in the table.");
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, true, 2, 2));


		Composite groupListColumn = new Composite(groupeCols, SWT.BORDER);
		groupListColumn.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		groupListColumn.setLayout(new GridLayout(2, false));


		listCol = new ListViewer(groupListColumn, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
		listCol.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
		listCol.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				java.util.List<Cell> currentElement = (ArrayList<Cell>)inputElement;
				return currentElement.toArray();
			}
		});
		
		listCol.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Cell) element).getContents();
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





		//Init Liste Selection

		Composite groupListSelected = new Composite(groupeCols, SWT.NONE);
		groupListSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		groupListSelected.setLayout(new GridLayout(1, false));


		//Table viewer
		viewerColSelected = new TableViewer(groupListSelected, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewerColSelected.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 5));


		//Content Provider
		viewerColSelected.setContentProvider(new IStructuredContentProvider(){


			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				java.util.List<Cell> currentElement = (ArrayList<Cell>)inputElement;
				return currentElement.toArray();
			}


			public void dispose() {
			}


			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});		



		//Table column : NAME
		TableViewerColumn column = new TableViewerColumn(viewerColSelected,SWT.NONE);
		column.getColumn().setText("Column's Name");
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Cell) element).getContents();
			}

			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(IconsNames.ICO_GO_TABLE);
			}


		});



		// Table column : LABEL
		column = new TableViewerColumn(viewerColSelected,SWT.NONE);
		column.getColumn().setText("Column's label");
		//		column.setEditingSupport(new MyEditingSupport(viewerColSelected));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Cell) element).getContents();
			}

		});

		for (int i = 0, n = viewerColSelected.getTable().getColumnCount(); i < n; i++) {
			viewerColSelected.getTable().getColumn(i).setWidth(145);
		}	    

		viewerColSelected.getTable().setHeaderVisible(true);
		viewerColSelected.getTable().setLinesVisible(true);


		viewerColSelected.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {    	  
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				//TODO
			}
		});


		//-------- Parameter's options
//		lbl = new Label(parent, SWT.NONE);
//		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,3, 1));
//
//
//		PGroup groupeParam = new PGroup(parent, SWT.SMOOTH);
//		groupeParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5));
//		groupeParam.setLayout(new GridLayout(2, false));
//		groupeParam.setText("Parameter's Options");
//		groupeParam.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.ICO_OBJECT));
//
//		lbl = new Label(groupeParam, SWT.NONE);
//		lbl.setText("Use Buttons to add or remove parameter(s). Click on cells to modify them.");
//		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
//
//
//
//		//Control panel
//		Composite compoParam = new Composite(groupeParam, SWT.NONE);
//		compoParam.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL, GridData.FILL, true, true, 1, 3));
//		compoParam.setLayout(new GridLayout(1, false));
//
//		btnAddParam = new Button(compoParam, SWT.PUSH);
//		btnAddParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
//		btnAddParam.setText("Add one empty parameter");
//		btnAddParam.setEnabled(false);
//		btnAddParam.addSelectionListener(new SelectionListener(){
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				//TODO
//			}
//
//		});
//
//		btnDelParam = new Button(compoParam, SWT.PUSH);
//		btnDelParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
//		btnDelParam.setText("Remove selected Parameter(s)");
//		btnDelParam.setEnabled(false);
//		btnDelParam.addSelectionListener(new SelectionListener(){
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				//TODO
//			}
//
//		});
//
//		lbl = new Label(compoParam, SWT.NONE);
//		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
//
//		lbl = new Label(compoParam, SWT.NONE);
//		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));



		//Init widgets if dataset is not null
		//		DataSetDesign dataSetDesigned = getInitializationDesign();
		//		if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$
		//
		//		}
		//		else{
		//			initWidgetWithDataset(dataSetDesigned);
		//		}

		initListenersBtnTool();

		return parent;


	}
	
	//*************************** Listeners for toolbar's button
	private void initListenersBtnTool() {


		//Add selected columns
		btnAdd.addSelectionListener(new SelectionListener(){

		
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) listCol.getSelection();
				if (selection.isEmpty()) {
					return;
				}
				
				Iterator<Cell> it = selection.iterator();
				while (it.hasNext()) {
					Cell cell = it.next();
					System.out.println(cell.getColumn());
					selectedCells.add(cell);
					availableCells.remove(cell);
				}
				
				Collections.sort(selectedCells, cellComparator);
				listCol.setInput(availableCells);
				viewerColSelected.setInput(selectedCells);
				setPageComplete(true);
			}

		});


		//Add all columns

		btnAddAll.addSelectionListener(new SelectionListener(){


			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				selectedCells.addAll(availableCells);
				availableCells = new ArrayList<Cell>();
				
				listCol.setInput(availableCells);
				Collections.sort(selectedCells, cellComparator);
				viewerColSelected.setInput(selectedCells);
				setPageComplete(true);

			}
		});

		
	//Remove selected columns
		btnRemove.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) viewerColSelected.getSelection();
				if (selection.isEmpty()) {
					return;
				}
				Iterator<Cell> it = selection.iterator();
				while (it.hasNext()) {
					Cell c = it.next();
					selectedCells.remove(c);
					availableCells.add(c);
				}
				
				Collections.sort(selectedCells, cellComparator);
				Collections.sort(availableCells, cellComparator);
				listCol.setInput(availableCells);
				viewerColSelected.setInput(selectedCells);
				setPageComplete(!selectedCells.isEmpty());
			}

		});

		//Remove all columns
		btnRemoveAll.addSelectionListener(new SelectionListener(){

	
			public void widgetDefaultSelected(SelectionEvent e) {
			}

	
			public void widgetSelected(SelectionEvent e) {
				availableCells.addAll(selectedCells);
				selectedCells = new ArrayList<Cell>();
				
				Collections.sort(availableCells, cellComparator);
				listCol.setInput(availableCells);
				viewerColSelected.setInput(selectedCells);
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
			
			IConnection conn =  ConnectionManager.getConnection(connProps, getInitializationDesign().getDataSourceDesign().getOdaExtensionDataSourceId());

			fillDatas((Connection) conn);

		} catch (OdaException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		// Restores the last saved data set design
		DataSetDesign dataSetDesign = getInitializationDesign();

		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$
			return;
		}
		else {
			try {
				initSelection(dataSetDesign);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}

	}

	private void initSelection(DataSetDesign dataSetDesign) throws DocumentException {
		String queryText = dataSetDesign.getQueryText();
		Document doc = DocumentHelper.parseText(queryText);
		Element root = doc.getRootElement();
		String sheetName = root.element("sheet").getText();
		
		selectedSheet = workbookin.getSheet(sheetName);
		
		comboSheets.setSelection(new StructuredSelection(selectedSheet));
		
		Element cells = root.element("cells");
		
		for (Object o : cells.elements("cell")) {
			Element e = (Element) o;
			Cell c = selectedSheet.getCell(new Integer(e.getStringValue()), 0);
			availableCells.remove(c);
			selectedCells.add(c);
		}
		
		Collections.sort(availableCells, cellComparator);
		Collections.sort(selectedCells, cellComparator);
		
		listCol.setInput(availableCells);
		viewerColSelected.setInput(selectedCells);
		
		setPageComplete(!selectedCells.isEmpty());
	}

	private void fillDatas(Connection conn) throws BiffException, IOException {
		String path = conn.getTemporaryFile();
		InputStream is = new FileInputStream(path);
		workbookin = Workbook.getWorkbook(is);
		is.close();

		comboSheets.setInput(workbookin.getSheets());

	}

	/**
	 * Obtains the user-defined query text of this data set from page control.
	 * @return query text
	 */
	private String getQueryText() {
		Element root = DocumentHelper.createElement("bpm.excel.oda.query");
	
		root.addElement("sheet").setText(selectedSheet.getName());
		Element cells = root.addElement("cells");
		
		for (Cell c : selectedCells) {
			cells.addElement("cell").setText(c.getColumn() + "");
		}
		
		return root.asXML();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#collectDataSetDesign(org.eclipse.datatools.connectivity.oda.design.DataSetDesign)
	 */
	protected DataSetDesign collectDataSetDesign( DataSetDesign design )
	{
		if( getControl() == null )     // page control was never created
			return design;             // no editing was done
		if( ! hasValidData() )
			return null;    // to trigger a design session error status
		savePage( design );
		return design;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#collectResponseState()
	 */
	protected void collectResponseState( )
	{
		super.collectResponseState( );
		/*
		 * To optionally assign a custom response state, for inclusion in the ODA
		 * design session response, use 
		 *      setResponseSessionStatus( SessionStatus status );
		 *      setResponseDesignerState( DesignerState customState );
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#canLeave()
	 */
	protected boolean canLeave( )
	{
		return isPageComplete();
	}

	/**
	 * Validates the user-defined value in the page control exists
	 * and not a blank text.
	 * Set page message accordingly.
	 */
	private void validateData( )
	{
		boolean isValid = (getQueryText() != null && getQueryText().trim().length() > 0 );

		if( isValid )
			setMessage( DEFAULT_MESSAGE );
		else
			setMessage( "Requires input value.", ERROR );

		setPageComplete( isValid );
	}

	/**
	 * Indicates whether the custom page has valid data to proceed 
	 * with defining a data set.
	 */
	private boolean hasValidData( )
	{
		validateData( );

		return canLeave();
	}

	/**
	 * Saves the user-defined value in this page, and updates the specified 
	 * dataSetDesign with the latest design definition.
	 */
	private void savePage( DataSetDesign dataSetDesign )
	{
		// save user-defined query text
		String queryText = getQueryText();
		dataSetDesign.setQueryText( queryText );

		// obtain query's current runtime metadata, and maps it to the dataSetDesign
		IConnection conn = null;
		try {
			
			java.util.Properties connProps = DesignUtil
            .convertDataSourceProperties(getInitializationDesign()
                    .getDataSourceDesign());
    
			conn =  ConnectionManager.getConnection(connProps, dataSetDesign.getOdaExtensionDataSourceId());
    
			updateDesign( dataSetDesign, conn, queryText );
		}
		catch( OdaException e )
		{
			// not able to get current metadata, reset previous derived metadata
			dataSetDesign.setResultSets( null );
			dataSetDesign.setParameters( null );

			e.printStackTrace();
		}
		finally
		{
			closeConnection( conn );
		}
	}

	/**
	 * Updates the given dataSetDesign with the queryText and its derived metadata
	 * obtained from the ODA runtime connection.
	 */
	private void updateDesign( DataSetDesign dataSetDesign, IConnection conn, String queryText ) throws OdaException {
		IQuery query = conn.newQuery( dataSetDesign.getOdaExtensionDataSetId() );
		query.prepare( queryText );

		// TODO a runtime driver might require a query to first execute before
		// its metadata is available
		//      query.setMaxRows( 1 );
		//      query.executeQuery();

		try {
			IResultSetMetaData md = query.getMetaData();
			updateResultSetDesign( md, dataSetDesign );
		}
		catch( OdaException e )
		{
			// no result set definition available, reset previous derived metadata
			dataSetDesign.setResultSets( null );
			e.printStackTrace();
		}

		// proceed to get parameter design definition
		try {
			IParameterMetaData paramMd = query.getParameterMetaData();
			updateParameterDesign( paramMd, dataSetDesign );
		}
		catch( OdaException ex ) {
			// no parameter definition available, reset previous derived metadata
			dataSetDesign.setParameters( null );
			ex.printStackTrace();
		}

		/*
		 * See DesignSessionUtil for more convenience methods
		 * to define a data set design instance.  
		 */     
	}

	/**
	 * Updates the specified data set design's result set definition based on the
	 * specified runtime metadata.
	 * @param md    runtime result set metadata instance
	 * @param dataSetDesign     data set design instance to update
	 * @throws OdaException
	 */
	private void updateResultSetDesign( IResultSetMetaData md,
			DataSetDesign dataSetDesign ) 
	throws OdaException
	{
		ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign( md );

		ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE
		.createResultSetDefinition();
		// resultSetDefn.setName( value );  // result set name
		resultSetDefn.setResultSetColumns( columns );

		// no exception in conversion; go ahead and assign to specified dataSetDesign
		dataSetDesign.setPrimaryResultSet( resultSetDefn );
		dataSetDesign.getResultSets().setDerivedMetaData( true );
	}

	/**
	 * Updates the specified data set design's parameter definition based on the
	 * specified runtime metadata.
	 * @param paramMd   runtime parameter metadata instance
	 * @param dataSetDesign     data set design instance to update
	 * @throws OdaException
	 */
	private void updateParameterDesign( IParameterMetaData paramMd,
			DataSetDesign dataSetDesign ) 
	throws OdaException {
		
	}

	/**
	 * Attempts to close given ODA connection.
	 */
	private void closeConnection( IConnection conn )
	{
		try
		{
			if( conn != null && conn.isOpen() )
				conn.close();
		}
		catch ( OdaException e )
		{
			// ignore
			e.printStackTrace();
		}
	}

}
