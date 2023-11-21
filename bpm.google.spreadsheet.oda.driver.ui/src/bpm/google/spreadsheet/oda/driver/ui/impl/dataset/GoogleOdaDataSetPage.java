package bpm.google.spreadsheet.oda.driver.ui.impl.dataset;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.ParameterMode;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;

import bpm.google.spreadsheet.oda.driver.runtime.impl.Connection;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleDataSetFilterQuery;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleParameter;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleProperties;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheet;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheetColumn;
import bpm.google.spreadsheet.oda.driver.ui.Activator;
import bpm.google.spreadsheet.oda.driver.ui.Messages;
import bpm.google.spreadsheet.oda.driver.ui.icons.IconsName;




public class GoogleOdaDataSetPage extends DataSetWizardPage{


	private IConnection conn;

	private ArrayList<OdaGoogleSheet> listDataSetSheets;

	private PGroup groupeCols, groupSheets;
	private Combo comboSheets;
	private Button btnSkip;

	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll,btnAddParam,btnDelParam;

	private String sheetNameSelected;
	private OdaGoogleSheet sheetSelected;

	private List listCol;
	
	// Column's Table viewer
	private IStructuredSelection selection;
	private TableViewer viewerColSelected ;
	private Table tableColSelected;
	private ArrayList<OdaGoogleSheetColumn> listInputColums;
	
	// Parameter's Table viewer
	private ArrayList<OdaGoogleParameter> listInputParam;
	private TableViewer viewerParameters;
	private Table tableParameters;
	private IStructuredSelection selectionParam;
	

	


	public GoogleOdaDataSetPage( String pageName )
	{
		super( pageName );
		setTitle(Messages.GoogleOdaDataSetPage_0);
		

	}


	@Override
	public void createPageCustomControl(Composite parent) {
		
		Composite inCompo = new Composite(parent, SWT.NONE);
		inCompo.setLayout(new GridLayout(1, false)); 
		setControl( createPageControl(inCompo));
		
//		if(getShell() != null){
//			getShell().setSize(700, 700);
//		}
		
		
		
	}


	private Control createPageControl(Composite parent) {

		setMessage(Messages.GoogleOdaDataSetPage_1);
		setPageComplete(false);

		//DataSources Properties
		final Properties connProps = 
			DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

		
		//Runtime Connection
		
	      try {
	          new ProgressMonitorDialog(getShell()).run(true, true,
	              new IRunnableWithProgress(){

			
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						
						monitor.beginTask(Messages.GoogleOdaDataSetPage_2, IProgressMonitor.UNKNOWN);
					
						try{
							conn  = new Connection();
							conn.open(connProps);

							listDataSetSheets = new ArrayList<OdaGoogleSheet>();
							listDataSetSheets.addAll(((Connection)conn).getListSheets());

						} catch (Exception e) {
							Display.getDefault().asyncExec(new Runnable(){
								public void run(){
									MessageDialog .openError(getShell(), 
											Messages.GoogleOdaDataSetPage_3,
											Messages.GoogleOdaDataSetPage_4);

								}
							});
							e.printStackTrace();
						}
						
						monitor.done();
							
					}
	        	  
	          });
	        } catch (InvocationTargetException e2) {
	        	e2.printStackTrace();
	        } catch (InterruptedException e3) {
	        	e3.printStackTrace();
	        }
		

		listInputColums = new ArrayList<OdaGoogleSheetColumn>();
		listInputParam = new ArrayList<OdaGoogleParameter>();

		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		parent.setLayout(new GridLayout(2, false));	


		//**** Group to select a sheet

		groupSheets = new PGroup(parent, SWT.SMOOTH);
		groupSheets.setText(Messages.GoogleOdaDataSetPage_5);
		groupSheets.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
		groupSheets.setLayout(new GridLayout(3, false));
		groupSheets.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_VIEW_LIST));

		Label lbl = new Label(groupSheets, SWT.NONE);
		lbl.setText(Messages.GoogleOdaDataSetPage_6);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, false, 1, 1));

		comboSheets = new Combo(groupSheets, SWT.SINGLE | SWT.READ_ONLY);
		comboSheets.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

		for(OdaGoogleSheet currentSheet: listDataSetSheets){	
			comboSheets.add(currentSheet.getSheetName());
		}
		
		
		comboSheets.addSelectionListener(new SelectionListener(){
		
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {

				//Change Name selected Sheet
				sheetNameSelected = comboSheets.getText();

				//Change Message
				setMessage(Messages.GoogleOdaDataSetPage_7 + comboSheets.getText());

				btnSkip.setEnabled(true);



				//Replace the final Selected Sheet
				for(OdaGoogleSheet sheet: listDataSetSheets){

					if(sheet.getSheetName().equals(sheetNameSelected)){
						sheetSelected = sheet;		
					}

				}

				listInputColums.clear();
				viewerColSelected.refresh();

				//Init Combo Columns:
				refreshListCol(sheetSelected);

			}

		});

		//------- Option : Skip the first row
		btnSkip = new Button(groupSheets,SWT.CHECK);
		btnSkip.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		btnSkip.setText(Messages.GoogleOdaDataSetPage_8);
		btnSkip.setEnabled(false);

		lbl = new Label(parent, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,3, 1));


		//------- Option : Select Columns
		groupeCols = new PGroup(parent, SWT.SMOOTH);
		groupeCols.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 2));
		groupeCols.setLayout(new GridLayout(2, false));
		groupeCols.setText(Messages.GoogleOdaDataSetPage_9);
		groupeCols.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_COLUMN));


		lbl = new Label(groupeCols, SWT.NONE);
		lbl.setText(Messages.GoogleOdaDataSetPage_10);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.VERTICAL_ALIGN_CENTER, false, true, 2, 2));


		Composite groupListColumn = new Composite(groupeCols, SWT.BORDER);
		groupListColumn.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		groupListColumn.setLayout(new GridLayout(2, false));


		listCol = new List(groupListColumn, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER);
		listCol.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));


		btnAdd = new Button(groupListColumn, SWT.PUSH);
		btnAdd.setToolTipText(Messages.GoogleOdaDataSetPage_11);
		btnAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_ADD));

		btnRemove = new Button(groupListColumn, SWT.PUSH);
		btnRemove.setToolTipText(Messages.GoogleOdaDataSetPage_12);
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemove.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DELETE));

		btnAddAll = new Button(groupListColumn, SWT.PUSH);
		btnAddAll.setToolTipText(Messages.GoogleOdaDataSetPage_13);
		btnAddAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAddAll.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_ADD_ALL));


		btnRemoveAll = new Button(groupListColumn, SWT.PUSH);
		btnRemoveAll.setToolTipText(Messages.GoogleOdaDataSetPage_14);
		btnRemoveAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemoveAll.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DELETE_ALL));


		initListenersBtnTool();


	//Init Liste Selection

		Composite groupListSelected = new Composite(groupeCols, SWT.NONE);
		groupListSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		groupListSelected.setLayout(new GridLayout(1, false));

		
	//Table viewer
		viewerColSelected = new TableViewer(groupListSelected, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		tableColSelected = viewerColSelected.getTable();
		tableColSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 5));
	
		
	//Content Provider
		viewerColSelected.setContentProvider(new IStructuredContentProvider(){

		
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				java.util.List<List> currentElement = (ArrayList)inputElement;
				return currentElement.toArray();
			}

		
			public void dispose() {
			}

	
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});		
		
		
		viewerColSelected.setInput(listInputColums);
		
	//Table column : NAME
		TableViewerColumn column = new TableViewerColumn(viewerColSelected,SWT.NONE);
		column.getColumn().setText(Messages.GoogleOdaDataSetPage_15);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				OdaGoogleSheetColumn current = (OdaGoogleSheetColumn)element;
				
				return current.getColName();
			}
			
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(IconsName.ICO_GO_TABLE);
			}
			

		});
		
		
		
	// Table column : LABEL
		
		column = new TableViewerColumn(viewerColSelected,SWT.NONE);
		column.getColumn().setText(Messages.GoogleOdaDataSetPage_16);
		column.setEditingSupport(new MyEditingSupport(viewerColSelected));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				OdaGoogleSheetColumn current = (OdaGoogleSheetColumn)element;
				
				if(current.getColLabelName() == null){
					current.setColLabelName(current.getColName());
					
				}
				
				return current.getColLabelName();
			}
			
		});
		
	    for (int i = 0, n = tableColSelected.getColumnCount(); i < n; i++) {
	    	tableColSelected.getColumn(i).setWidth(145);
	      }	    
	    
	    tableColSelected.setHeaderVisible(true);
	    tableColSelected.setLinesVisible(true);


		viewerColSelected.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {    	  
				selection = (IStructuredSelection)event.getSelection();
			}
		});

		
	//-------- Parameter's options
		
		
		lbl = new Label(parent, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,3, 1));


		PGroup groupeParam = new PGroup(parent, SWT.SMOOTH);
		groupeParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5));
		groupeParam.setLayout(new GridLayout(2, false));
		groupeParam.setText(Messages.GoogleOdaDataSetPage_17);
		groupeParam.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_OBJECT));
		
		lbl = new Label(groupeParam, SWT.NONE);
		lbl.setText(Messages.GoogleOdaDataSetPage_18);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

		initViewerParam(groupeParam);
		
		//Control panel
		Composite compoParam = new Composite(groupeParam, SWT.NONE);
		compoParam.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL, GridData.FILL, true, true, 1, 3));
		compoParam.setLayout(new GridLayout(1, false));

		btnAddParam = new Button(compoParam, SWT.PUSH);
		btnAddParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		btnAddParam.setText(Messages.GoogleOdaDataSetPage_19);
		btnAddParam.setEnabled(false);
		btnAddParam.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				listInputParam.add(new OdaGoogleParameter());
				changeEnableButtonParam();
				viewerParameters.refresh();
				
			}
			
		});
		
		btnDelParam = new Button(compoParam, SWT.PUSH);
		btnDelParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		btnDelParam.setText(Messages.GoogleOdaDataSetPage_20);
		btnDelParam.setEnabled(false);
		btnDelParam.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				
				if(selectionParam == null){
					MessageDialog.openInformation(getShell(),
							Messages.GoogleOdaDataSetPage_21,
							Messages.GoogleOdaDataSetPage_22);
				}
				
				else{
					for(Object obj : selectionParam.toArray()){
						listInputParam.remove(((OdaGoogleParameter)obj));
					}
					
					viewerParameters.refresh();
					
					changeEnableButtonParam();
				}
			}
			
		});
		
		lbl = new Label(compoParam, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		lbl = new Label(compoParam, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		

	//Init widgets if dataset is not null
		DataSetDesign dataSetDesigned = getInitializationDesign();
		if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$

		}
		else{
			initWidgetWithDataset(dataSetDesigned);
		}



		return parent;

	}

//******************************* Parameter's Table viewer
	
	private void initViewerParam(PGroup groupeParam) {
		
		viewerParameters = new TableViewer(groupeParam, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		tableParameters = viewerParameters.getTable();
		tableParameters.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL, GridData.FILL, true, true, 1, 3));
	
		
	//Content Provider
		viewerParameters.setContentProvider(new IStructuredContentProvider(){

		
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				java.util.List<List> currentElement = (ArrayList)inputElement;
				return currentElement.toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});		
		
		
		viewerParameters.setInput(listInputParam);
		
	//Table column : COLUMN 
		TableViewerColumn column = new TableViewerColumn(viewerParameters,SWT.NONE);
		column.getColumn().setText(Messages.GoogleOdaDataSetPage_23);
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				OdaGoogleParameter current = (OdaGoogleParameter)element;
				return current.getParamColumn();
			}
			
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(IconsName.ICO_OK);
			}
			
		});
		
		column.setEditingSupport(new ComboEditingSupport(viewerParameters, 1));
		
		
		
	// Table column : Operator
		
		column = new TableViewerColumn(viewerParameters,SWT.NONE);
		column.getColumn().setText(Messages.GoogleOdaDataSetPage_24);
		column.setEditingSupport(new ComboEditingSupport(viewerParameters, 2));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				OdaGoogleParameter current = (OdaGoogleParameter)element;
				
				return current.getParamOperator();
			}
		});
		
		
	    for (int i = 0, n = tableParameters.getColumnCount(); i < n; i++) {
	    	tableParameters.getColumn(i).setWidth(150);
	      }	    
	    
	    tableParameters.setHeaderVisible(true);
	    tableParameters.setLinesVisible(true);

	    viewerParameters.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {    	  
				selectionParam = (IStructuredSelection)event.getSelection();
			}
		});
	    
	}


	//*************************** Listeners for toolbar's button
	private void initListenersBtnTool() {


		//Add selected columns
		btnAdd.addSelectionListener(new SelectionListener(){

		
			public void widgetDefaultSelected(SelectionEvent e) {

			}

		
			public void widgetSelected(SelectionEvent e) {

				setPageComplete(true);

				for(String nameSelect : listCol.getSelection()){

					for(OdaGoogleSheetColumn col: sheetSelected.getSheetListCol()){

						if(nameSelect.equals(col.getColName())){

							listInputColums.add(col);
							
							viewerColSelected.refresh();

						}

					}

				}			
				
			//List Columns
				listCol.remove(listCol.getSelectionIndices());
				
			//Parameters
				changeEnableButtonParam();
			
				viewerColSelected.refresh();

			}

		});


		//Add all columns

		btnAddAll.addSelectionListener(new SelectionListener(){


			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {

				setPageComplete(true);

				listCol.removeAll();

				listInputColums.clear();
				listInputColums.addAll(sheetSelected.getSheetListCol());

				changeEnableButtonParam();

				viewerColSelected.refresh();

			}
		});

		
	//Remove selected columns
		btnRemove.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {

				ArrayList<OdaGoogleSheetColumn> listColToRemove = new ArrayList<OdaGoogleSheetColumn>();

				for(Object obj : selection.toArray()){

					listColToRemove.add(((OdaGoogleSheetColumn)obj));
				}

				//Remove columns from viewer
				listInputColums.removeAll(listColToRemove);
				viewerColSelected.refresh();

				//Add removed columns into the list
				for(OdaGoogleSheetColumn col: listColToRemove){
					listCol.add(col.getColName());
				}

				if(listInputColums.size() == 0){
					setPageComplete(false);
				}
				else{
					setPageComplete(true);
				}
				
			//Update parameters
				changeEnableButtonParam();
				ArrayList<OdaGoogleParameter> listParamToRemove = new ArrayList<OdaGoogleParameter>();
				for(OdaGoogleSheetColumn col: listColToRemove){
					
					for(OdaGoogleParameter param : listInputParam){
						if(param.getParamColumn().equals(col.getColName())){
							listParamToRemove.add(param);
						}
					}
					
				}
				listInputParam.removeAll(listParamToRemove);
				viewerParameters.refresh();
			}

		});

		//Remove all columns
		btnRemoveAll.addSelectionListener(new SelectionListener(){

	
			public void widgetDefaultSelected(SelectionEvent e) {
			}

	
			public void widgetSelected(SelectionEvent e) {

				setPageComplete(false);

				listCol.removeAll();

				listInputColums.clear();
				viewerColSelected.refresh();

				for(OdaGoogleSheetColumn currentNameCol: sheetSelected.getSheetListCol()){

					listCol.add(currentNameCol.getColName());
				}

				changeEnableButtonParam();
				listInputParam.clear();
				viewerParameters.refresh();
			}
			
			

		});



	}


	protected void refreshListCol(OdaGoogleSheet sheetSelected2) {

		//-- List Columns
		listCol.removeAll();

		for(OdaGoogleSheetColumn col : sheetSelected2.getSheetListCol()){
			listCol.add(col.getColName());
		}

	}
	
	static class MyEditingSupport extends EditingSupport {

		private CellEditor editor;
		private Viewer myViewer;
		
		public MyEditingSupport(ColumnViewer viewer) {
			super(viewer);
			myViewer = viewer;
			editor = new TextCellEditor((Composite)viewer.getControl());
		}
		protected boolean canEdit(Object element) {
			return true;
		}

		protected CellEditor getCellEditor(Object element) {
			return editor;
		}	

		protected Object getValue(Object element) {
			OdaGoogleSheetColumn current = (OdaGoogleSheetColumn)element;
			return current.getColLabelName();
		}

		protected void setValue(Object element, Object value) {
			OdaGoogleSheetColumn current = (OdaGoogleSheetColumn)element;
			current.setColLabelName(((String)value));
			myViewer.refresh();
		}		
	}


	private class ComboEditingSupport extends EditingSupport {

		private CellEditor editor;
		private Viewer myViewer;
		private String[] tabParamOperators;
		private int typeCombo;
		
		public ComboEditingSupport(TableViewer viewer, int type) {
			super(viewer);
			
			myViewer = viewer;
			typeCombo = type; //If type = 1 > "columns". else > "operators"
			tabParamOperators = GoogleOdaFilterPage.OPERATORS_TAB;
			
		}
		protected boolean canEdit(Object element) {
			return true;
		}

		protected CellEditor getCellEditor(Object element) {
			
			if(typeCombo == 1){
				editor = new ComboBoxCellEditor((Composite)myViewer.getControl(),getTabColumnSelected() );
			}
			else{
				editor = new ComboBoxCellEditor((Composite)myViewer.getControl(),tabParamOperators );
			}
			
			return editor;
		}	

		protected Object getValue(Object element) {

			return new Integer(0); 
		}

		protected void setValue(Object element, Object value) {
			OdaGoogleParameter current = (OdaGoogleParameter)element;
			int indexCombo = Integer.valueOf(value.toString());
			
			if(typeCombo == 1){
				current.setParamColumn(getTabColumnSelected()[indexCombo]);
			}
			else{
				current.setParamOperator(tabParamOperators[indexCombo]);
			}
			
			myViewer.refresh();
		}	
	}
	
	
	protected OdaGoogleSheetColumn findColumnSheet(String colName){

		OdaGoogleSheetColumn tempCol = null;

		for(OdaGoogleSheetColumn col: sheetSelected.getSheetListCol()){

			if(col.equals(colName)){

				tempCol = col;
			}
		}

		return tempCol;
	}

	protected void changeEnableButtonParam(){
		
		if(listInputColums.size() == 0 ){
			btnAddParam.setEnabled(false);
			btnDelParam.setEnabled(false);
			
		}
		else{
			btnAddParam.setEnabled(true);
			btnDelParam.setEnabled(true);
		}
		
	}
	
	protected String[] getTabColumnSelected(){
		String[] tabColSel = new String[listInputColums.size()];
		
		for(int i = 0; i < listInputColums.size(); i++){
			tabColSel[i] = listInputColums.get(i).getColName();
		}
		
		return tabColSel;
	}
	
	
	private void initWidgetWithDataset(DataSetDesign dataSetDesigned) {


		String queryDataset = dataSetDesigned.getQueryText();

		String[] tabElementsQuery = queryDataset.split(OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT);


		//------- First element: Sheet selected : Get the selected Sheet by his name
		for(OdaGoogleSheet currentSheet : listDataSetSheets){

			if(currentSheet.getSheetName().equals(tabElementsQuery[0])){
				sheetSelected = currentSheet;
				sheetNameSelected = currentSheet.getSheetName();
			}
		}		

		//+++ Init combo with selected  Sheet
		comboSheets.setText(sheetNameSelected);


		//------- Second element: Skip the first row or not
		String rowSkipped = tabElementsQuery[1];
		btnSkip.setEnabled(true);

		if(rowSkipped.equals(OdaGoogleProperties.QUERY_SKIP_TRUE)){
			btnSkip.setSelection(true);
		}
		else{
			btnSkip.setSelection(false);
		}


		//------- Third element: Get selected columns

		String queryColSelected = tabElementsQuery[2];

		String[] listColsSelected = queryColSelected.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
		listInputColums.clear();
		listCol.removeAll();

		for(OdaGoogleSheetColumn col: sheetSelected.getSheetListCol()){

			boolean colIsSelected = false;

			//Search if the current column is selected or not
			for(String nameColSelected : listColsSelected){

				if(nameColSelected.equals(col.getColName())){

					colIsSelected = true;
				}
			}

			//If Yes, add the column into the list viewer. Else, into the list
			if(colIsSelected){
				listInputColums.add(col);
			}

			else{
				listCol.add(col.getColName());
			}

			viewerColSelected.refresh();

		}
		
		
	//------- Fourth element: Get selected columns label
		
		String queryColSelectedLabel = tabElementsQuery[3];
		
		String[] listColsSelectedLabels = queryColSelectedLabel.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
		
		//Insert Labels name
		int indexLabel = 0;
		for(OdaGoogleSheetColumn currentCol : listInputColums){
			
			currentCol.setColLabelName(listColsSelectedLabels[indexLabel]);
			indexLabel++;
		}
		
		viewerColSelected.refresh();
		
	//----- 6� element: Parameters
		
		String queryParameters = tabElementsQuery[5];
		
		if(queryParameters.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_PARAMETER) == false	){
			String[] listParameters = queryParameters.split(OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY);

			for(String currentDetailsParam : listParameters){
				
				String[] detailsParam = currentDetailsParam.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
				listInputParam.add(new OdaGoogleParameter(detailsParam[0], detailsParam[1]));
			}
			
			viewerParameters.refresh();
			btnDelParam.setEnabled(true);
		}
		
		else{
			btnDelParam.setEnabled(false);
		}
		
		btnAddParam.setEnabled(true);
	
	}



	@Override
	public DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {
		
		//Query
		String queryText = getQueryText(dataSetDesign);
		dataSetDesign.setQueryText(queryText);

		//Parameters
		if(conn == null){
			dataSetDesign.setParameters( null );
		}
		else{
			DataSetParameters dataSetParameter = null;
			try {
				IQuery query = conn.newQuery(null);
				query.prepare(queryText);
				 IParameterMetaData pMd = query.getParameterMetaData();
				  
				 dataSetParameter = DesignSessionUtil.toDataSetParametersDesign( pMd,ParameterMode.IN_LITERAL );
			} catch (OdaException e) {
				e.printStackTrace();
			}
					
			dataSetDesign.setParameters( dataSetParameter );
		}
		
		dataSetDesign.setResultSets(null);

		return dataSetDesign;
	}


	private String getQueryText(DataSetDesign dataSetDesign) {
		String query = ""; //$NON-NLS-1$
		
		if(btnSkip != null){

		//Add the selected Sheet
			if(sheetNameSelected != null){
				query += (sheetNameSelected);
			}

			query += OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;

		// Add if user wants to skip the first row

			if(btnSkip.getSelection()){
				query += OdaGoogleProperties.QUERY_SKIP_TRUE;
			}
			else{
				query += OdaGoogleProperties.QUERY_SKIP_FALSE;
			}

			query +=  OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;


		//Add columns selected
			for(OdaGoogleSheetColumn col: listInputColums){

				query += col.getColName() + OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED;

			}

			query += OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
			
		//Add columns label selected
			
			for(OdaGoogleSheetColumn col: listInputColums){
				query += col.getColLabelName() + OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED;

			}

			query += OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
			
		// ***** filter(s): 

			if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$

				query += OdaGoogleDataSetFilterQuery.QUERY_NO_FILTER + OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
			

			}

			else{
				String dataSetQuery = dataSetDesign.getQueryText();

				String[] tabQueryColAndFilter = dataSetQuery.split(OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT);

				String strQueryFilter = tabQueryColAndFilter[4];
				
				query += strQueryFilter + OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
			}
			
		// **** Param
			
			if(listInputParam.isEmpty()){

				query += OdaGoogleDataSetFilterQuery.QUERY_NO_PARAMETER;
			}

			else{
				for(OdaGoogleParameter currentParam: listInputParam){

					query += currentParam.getParamColumn() + OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED;
					query += currentParam.getParamOperator();
					query += OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED + OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY;

				}
			}


			query += OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
						
		}

		else{

			query = dataSetDesign.getQueryText();
		}

		return query;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	
	@Override
	public boolean isPageComplete() {
		return true;
	}
}
