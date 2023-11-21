package bpm.google.table.oda.driver.ui.impl.dataset;

import java.util.ArrayList;
import java.util.Properties;

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
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;

import bpm.google.table.oda.driver.runtime.impl.Connection;
import bpm.google.table.oda.driver.runtime.impl.Query;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableColumn;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableModel;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableParameter;
import bpm.google.table.oda.driver.ui.Activator;
import bpm.google.table.oda.driver.ui.dialogs.MessageTable;
import bpm.google.table.oda.driver.ui.icons.IconsName;

public class GoogleTableDatasetPage extends DataSetWizardPage{
	
	private Connection connection;

	private OdaGoogleTableModel tableSelected;
	
	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll,btnAddParam,btnDelParam;
	
	private PGroup group;
	
	private ArrayList<OdaGoogleTableColumn> listMoving;

	private TableViewer viewerColSelected;
	private Table tableColSelected;
	private IStructuredSelection selectionTableViewer;
	private ArrayList<OdaGoogleTableColumn> listInputTableColumn;
	private final static String CURRENT_DRAG_FROM_TABLE = "Current drag come from the Table of selected Columns";
	
	private ListViewer viewerListColumn;
	private List myList;
	private IStructuredSelection selectionListViewer;
	private ArrayList<OdaGoogleTableColumn> listInputListColum;
	private final static String CURRENT_DRAG_FROM_LIST = "Current drag come from the List of availables columns";
	
	private TableViewer viewerParameters;
	private Table tableParameters;
	private IStructuredSelection selectionParameterViewer;
	private ArrayList<OdaGoogleTableParameter> listInputParameter;
	
	
	
	public GoogleTableDatasetPage(String pageName) {
		super(pageName);
		
		setMessage("Select Column(s) to display, and add parameter(s) if needed.");
		setPageComplete(false);
	}


	@Override
	public void createPageCustomControl(Composite parent) {
		
		
		setControl(createPageControl(parent));
		
		initialiseControl();
				
	}



	private Control createPageControl(Composite pParent) {
		
		Composite parent = new Composite(pParent, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));
		
		group = new PGroup(parent, SWT.NONE);
		group.setText("Columns Options");
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		group.setLayout(new GridLayout(3, false));
		group.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_VIEW_LIST));
		
		Label lbl = new Label(group, SWT.NONE);
		lbl.setText("Select Columns to diplay, with control buttons, or using drag and drop.");
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));


		Composite compoGroup = new Composite(group, SWT.BORDER);
		compoGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		compoGroup.setLayout(new GridLayout(2, false));


		viewerListColumn = new ListViewer(compoGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL| SWT.READ_ONLY | SWT.BORDER);

		myList = viewerListColumn.getList();
		myList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));
		createListViewer();

		btnAdd = new Button(compoGroup, SWT.PUSH);
		btnAdd.setToolTipText("Add a column");
		btnAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_ADD));
		
		btnRemove = new Button(compoGroup, SWT.PUSH);
		btnRemove.setToolTipText("Remove a column");
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemove.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DELETE));

		btnAddAll = new Button(compoGroup, SWT.PUSH);
		btnAddAll.setToolTipText("Add All columns");
		btnAddAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAddAll.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_ADD_ALL));


		btnRemoveAll = new Button(compoGroup, SWT.PUSH);
		btnRemoveAll.setToolTipText("Remove All columns");
		btnRemoveAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemoveAll.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DELETE_ALL));



	//Table viewer for selected column, and possibility to change the column label.

		compoGroup = new Composite(group, SWT.NONE);
		compoGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		compoGroup.setLayout(new GridLayout(1, false));
		
		viewerColSelected = new TableViewer(compoGroup, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		tableColSelected = viewerColSelected.getTable();
		tableColSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 5));
	
		createTableViewer();
		
		
	//Table viewer for parameters
		
//-------- Parameter's options
		
		
		lbl = new Label(parent, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false,3, 1));

		PGroup groupeParam = new PGroup(parent, SWT.SMOOTH);
		groupeParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5));
		groupeParam.setLayout(new GridLayout(2, false));
		groupeParam.setText("Parameter's Options");
		groupeParam.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_OBJECT));
		
		lbl = new Label(groupeParam, SWT.NONE);
		lbl.setText("Use Buttons to add or remove parameter(s). Click on cells to modify them.");
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
		
		viewerParameters = new TableViewer(groupeParam, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tableParameters = viewerParameters.getTable();
		tableParameters.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL, GridData.FILL, true, true, 1, 3));
		
		createParameterViewer();
		
		//Control panel
		Composite compoParam = new Composite(groupeParam, SWT.NONE);
		compoParam.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL, GridData.FILL, true, true, 1, 3));
		compoParam.setLayout(new GridLayout(1, false));
		
		btnAddParam = new Button(compoParam, SWT.PUSH);
		btnAddParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		btnAddParam.setText("Add one empty parameter");
		btnAddParam.setEnabled(false);
		
		btnDelParam = new Button(compoParam, SWT.PUSH);
		btnDelParam.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		btnDelParam.setText("Remove selected Parameter(s)");
		btnDelParam.setEnabled(false);
		
		lbl = new Label(compoParam, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		lbl = new Label(compoParam, SWT.NONE);
		lbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		
		
		
		return parent;
	}
	

	protected void initialiseControl(){
		Properties connProps = 
			DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

		try {
			connection = new Connection();
			connection.open(connProps);
			
			tableSelected = connection.getTableSelected();
			
			
		} catch (OdaException e) {
			MessageTable.showError(getShell(), "Unable to connect to this g-mail account. Please verify your login/password \n or if you are correctly connected to Internet.");
			e.printStackTrace();
		}
		
		
		if(tableSelected != null){
			
		//------- Init List viewer with the column list ------- 
			listInputListColum = new ArrayList<OdaGoogleTableColumn>();
			listInputListColum.addAll(tableSelected.getListColumns());
			viewerListColumn.setInput(listInputListColum);
			
			
		//------- Init Table viewer------- 
			listInputTableColumn = new ArrayList<OdaGoogleTableColumn>();
			viewerColSelected.setInput(listInputTableColumn);
			
		//------- Init Listners on tool buttons ------- 
			initListenersBtnTool();
			
		//------- Init Drag and drop ------- 
			listMoving = new ArrayList<OdaGoogleTableColumn>();
			initDragAndDropViewer();
			
		//------- Init Parameter Table viewer ------- 
			listInputParameter = new ArrayList<OdaGoogleTableParameter>();
			viewerParameters.setInput(listInputParameter);
		
		//------- Init Listners on parameters buttons ------- 
			initListenersBtnParameter();
			
		}
		else{
			MessageTable.showError(getShell(), "Error on the Google fusion table selected.");
		}
		
		//Init widgets with the current data set if need
		DataSetDesign dataSetDesigned = getInitializationDesign();

		if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$
		}

		else{
			initWithDataset(dataSetDesigned);
		}
		
		
	}



	


	private void createListViewer() {
		
		viewerListColumn.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				java.util.List<List> currentElement = (ArrayList)inputElement;
				return currentElement.toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
		
		
		
		viewerListColumn.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof OdaGoogleTableColumn) {
					return ((OdaGoogleTableColumn)element).getColName();
				} else {
					return "";
				}
			}
		});
		
		viewerListColumn.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {    	  
				selectionListViewer = (IStructuredSelection)event.getSelection();
			}
		});
		
	}

	private void createTableViewer(){
		
		
		//Content Provider
		viewerColSelected.setContentProvider(new IStructuredContentProvider(){

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				java.util.List<List> currentElement = (ArrayList)inputElement;
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
				OdaGoogleTableColumn current = (OdaGoogleTableColumn)element;
				return current.getColName();
			}
			
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get(IconsName.ICO_GO_TABLE);
			}
		});
		
		
	// Table column : LABEL
		
		column = new TableViewerColumn(viewerColSelected,SWT.NONE);
		column.getColumn().setText("Column's label");
		
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				OdaGoogleTableColumn current = (OdaGoogleTableColumn)element;
				
				if(current.getColLabelName() == null){
					
					current.setColLabelName(current.getColName());
					
				}
				
				return current.getColLabelName();
			}
		});
		
		column.setEditingSupport(new TableViewerEditingSupport(viewerColSelected));
		
		
		//Column size.
	    for (int i = 0, n = tableColSelected.getColumnCount(); i < n; i++) {
	    	tableColSelected.getColumn(i).setWidth(145);
	      }	    
	    
	    tableColSelected.setHeaderVisible(true);
	    tableColSelected.setLinesVisible(true);


		viewerColSelected.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {    	  
				selectionTableViewer = (IStructuredSelection)event.getSelection();
			}
		});
	}
	
	private void createParameterViewer(){
		
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
		
	//Table column : COLUMN 
		TableViewerColumn column = new TableViewerColumn(viewerParameters,SWT.NONE);
		column.getColumn().setText("Column's Name");
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				OdaGoogleTableParameter current = (OdaGoogleTableParameter)element;
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
		column.getColumn().setText("Operator");
		column.setEditingSupport(new ComboEditingSupport(viewerParameters, 2));
		column.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				OdaGoogleTableParameter current = (OdaGoogleTableParameter)element;
				
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
				selectionParameterViewer = (IStructuredSelection)event.getSelection();
			}
		});
		
		
	}
	
	private void initDragAndDropViewer( ) {
		
		int operations = DND.DROP_MOVE | DND.DROP_COPY ;
		Transfer[] transfers = new Transfer[] {TextTransfer.getInstance()};

	//--------------------------- LIST VIEWER -------------------- 


		viewerListColumn.addDragSupport(operations, transfers, new DragSourceListener(){

			public void dragStart(DragSourceEvent event) {

				if(selectionListViewer.isEmpty()){
					event.doit = false;
				}
			}

			public void dragSetData(DragSourceEvent event) {

				listMoving.clear();

				for(Object objSel : selectionListViewer.toArray()){

					listMoving.add((OdaGoogleTableColumn)objSel);
				}

				event.data = CURRENT_DRAG_FROM_LIST;
			}
			public void dragFinished(DragSourceEvent event) {

				
			}
		});


		viewerColSelected.addDropSupport(operations, transfers, new org.eclipse.swt.dnd.DropTargetListener(){

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
				

			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dragOperationChanged(DropTargetEvent event) {

			}

			public void dragOver(DropTargetEvent event) {

			}

			public void drop(DropTargetEvent event) {
				
				//Test if the drag source is the Table
				
				if(event.data.equals(CURRENT_DRAG_FROM_LIST)){
					for(OdaGoogleTableColumn col : listMoving){
						listInputTableColumn.add(col);
					}

					listInputListColum.removeAll(listMoving);

					viewerColSelected.refresh();
					viewerListColumn.refresh();
					
					checkCanFinish();
					changeEnableButtonParam();
				}
				

			}

			public void dropAccept(DropTargetEvent event) {
				
				event.detail = DND.DROP_COPY;
			}
		});

		
		int operations2 = DND.DROP_MOVE | DND.DROP_COPY ;
		Transfer[] transfers2 = new Transfer[] {TextTransfer.getInstance()};
		

		//--------------------------- TABLE VIEWER  -------------------- 
		viewerColSelected.addDragSupport(operations2, transfers2, new DragSourceListener(){

			public void dragStart(DragSourceEvent event) {
				
				if(selectionTableViewer != null){
					if(selectionTableViewer.isEmpty()){
						event.doit = false;
					}
				}
				
			}

			public void dragSetData(DragSourceEvent event) {

				listMoving.clear();

				for(Object objSel : selectionTableViewer.toArray()){

					listMoving.add((OdaGoogleTableColumn)objSel);
				}

			    event.data = CURRENT_DRAG_FROM_TABLE;

			}
			public void dragFinished(DragSourceEvent event) {

			}
		});


		viewerListColumn.addDropSupport(operations, transfers, new org.eclipse.swt.dnd.DropTargetListener(){

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;

			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dragOperationChanged(DropTargetEvent event) {

			}

			public void dragOver(DropTargetEvent event) {

			}

			public void drop(DropTargetEvent event) {
				
				if(event.data.equals(CURRENT_DRAG_FROM_TABLE)){
					
					for(OdaGoogleTableColumn col : listMoving){
						listInputListColum.add(col);
					}

					listInputTableColumn.removeAll(listMoving);

					viewerColSelected.refresh();
					viewerListColumn.refresh();
					
					checkCanFinish();
					changeEnableButtonParam();
				}


			}

			public void dropAccept(DropTargetEvent event) {
				
				
				event.detail = DND.DROP_COPY;
				
			}
		});



	}

	protected class TableViewerEditingSupport extends EditingSupport {

		private CellEditor editor;
		private Viewer myViewer;

		public TableViewerEditingSupport(ColumnViewer viewer) {
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
			OdaGoogleTableColumn current = (OdaGoogleTableColumn)element;
			return current.getColLabelName();
		}

		protected void setValue(Object element, Object value) {
			
			OdaGoogleTableColumn current = (OdaGoogleTableColumn)element;
			
			current.setColLabelName(((String)value));
			
			for(OdaGoogleTableColumn col : tableSelected.getListColumns()){
				if(col.equals(current.getColName())){
					col.setColLabelName(((String)value));
				}
			}
			
			myViewer.refresh();
		}		
	}

	protected class ComboEditingSupport extends EditingSupport {

		private CellEditor editor;
		private Viewer myViewer;
		private String[] tabParamOperators;
		private int typeCombo;
		
		public ComboEditingSupport(TableViewer viewer, int type) {
			super(viewer);
			
			myViewer = viewer;
			typeCombo = type; //If type = 1 > "columns". else > "operators"
			tabParamOperators = OdaGoogleTableParameter.OPERATORS_TAB;
			
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
			OdaGoogleTableParameter current = (OdaGoogleTableParameter)element;
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
	
	protected String[] getTabColumnSelected(){
		String[] tabColSel = new String[listInputTableColumn.size()];
		
		for(int i = 0; i < listInputTableColumn.size(); i++){
			tabColSel[i] = listInputTableColumn.get(i).getColName();
		}
		
		return tabColSel;
	}
	
	protected void initListenersBtnTool() {
		
		//Add selected columns
		btnAdd.addSelectionListener(new SelectionListener(){
		
			public void widgetDefaultSelected(SelectionEvent e) {
			}

		
			public void widgetSelected(SelectionEvent e) {
				
				String s  = "";
				for(OdaGoogleTableColumn col : listInputTableColumn){
					
					s += col.getColName() + " " + col.getColLabelName() + " " + col.getColClass().getSimpleName();
					s += "\n";
				}
				
				setPageComplete(true);

				for(Object colSelect : selectionListViewer.toArray()){
					listInputTableColumn.add((OdaGoogleTableColumn) colSelect);
					listInputListColum.remove((OdaGoogleTableColumn) colSelect);
				}			
				
				viewerColSelected.refresh();
				viewerListColumn.refresh();
				changeEnableButtonParam();

			}
		});


		btnAddAll.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				
				setPageComplete(true);
				
				for(OdaGoogleTableColumn col: listInputListColum){
					listInputTableColumn.add(col);
				}
				
				listInputListColum.clear();
				
				
				viewerListColumn.refresh();
				viewerColSelected.refresh();
				changeEnableButtonParam();

			}
		});

		
	//Remove selected columns
		btnRemove.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {

				ArrayList<OdaGoogleTableColumn> listColToRemove = new ArrayList<OdaGoogleTableColumn>();

				for(Object obj : selectionTableViewer.toArray()){
					listColToRemove.add(((OdaGoogleTableColumn)obj));
				}
				
				listInputTableColumn.removeAll(listColToRemove);
				listInputListColum.addAll(listColToRemove);
				
				viewerColSelected.refresh();
				viewerListColumn.refresh();
				changeEnableButtonParam();

				
			}

		});

		//Remove all columns
		btnRemoveAll.addSelectionListener(new SelectionListener(){
	
			public void widgetDefaultSelected(SelectionEvent e) {
			}
	
			public void widgetSelected(SelectionEvent e) {

				setPageComplete(false);
				
				for(OdaGoogleTableColumn col: listInputTableColumn){
					listInputListColum.add(col);
				}
				
				listInputTableColumn.clear();
				
				viewerListColumn.refresh();
				viewerColSelected.refresh();
				changeEnableButtonParam();

			}
		});
	}
	
	protected void initListenersBtnParameter() {
		
		btnAddParam.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				listInputParameter.add(new OdaGoogleTableParameter());
				
				viewerParameters.refresh();
				changeEnableButtonParam();
				
			}
			
		});
		
		
		btnDelParam.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				
				
				if(selectionParameterViewer == null){
					MessageDialog.openInformation(getShell(),
							"Oda Google driver Informations",
							"Please, select Parameter(s) to remove.");
				}
				
				else{
					for(Object obj : selectionParameterViewer.toArray()){
						listInputParameter.remove(((OdaGoogleTableParameter)obj));
					}
					changeEnableButtonParam();
					viewerParameters.refresh();
					
				}
			}
			
		});
		
	}
	
	protected void changeEnableButtonParam(){
		
		if(listInputTableColumn.size() == 0 ){
			btnAddParam.setEnabled(false);
			btnDelParam.setEnabled(false);
			
		}
		else{
			btnAddParam.setEnabled(true);
			btnDelParam.setEnabled(true);
		}
		
	}
	
	protected void  checkCanFinish(){
		
		if(listInputTableColumn.size() > 0){
			setPageComplete(true);
			
		}
		else{
			setPageComplete(false);
		}
		
		
	}
	
	
	private void initWithDataset(DataSetDesign dataSetDesigned) {
		
		String[] tabElementsQuery = dataSetDesigned.getQueryText().split(Query.QUERY_SEPARATOR_ELEMENT);

		/** First element: add Columns selected in the concerned viewer */
		
		
			String strColSelect = tabElementsQuery[0];
			
			String[] listColsSelected = strColSelect.split(Query.QUERY_SEPARATOR_COL_SELECTED);
			listInputTableColumn.clear();
			listInputListColum.clear();

		//Add selected column in the table viewer
			for(String nameColSelected : listColsSelected){
				
				for(OdaGoogleTableColumn col: tableSelected.getListColumns()){
					
					if(nameColSelected.equals(col.getColName())){
						listInputTableColumn.add(col);
					}
				}
			}
			viewerColSelected.refresh();
			
		//Add other columns in the list viewer
			
			for(OdaGoogleTableColumn col: tableSelected.getListColumns()){
				
				if(!listInputTableColumn.contains(col)){
					listInputListColum.add(col);
				}
			}
			
			
			viewerListColumn.refresh();
			
			
			
		/** Second element: Columns Labels */
			
			String queryColSelectedLabel = tabElementsQuery[1];
			
			String[] listColsSelectedLabels = queryColSelectedLabel.split(Query.QUERY_SEPARATOR_COL_SELECTED);
			
			int indexLabel = 0;
			
			for(OdaGoogleTableColumn currentCol : listInputTableColumn){
				
				currentCol.setColLabelName(listColsSelectedLabels[indexLabel]);
				indexLabel++;
			}
			
			viewerColSelected.refresh();
			
			
		/** Third element: Parameters */
			String queryParameters = tabElementsQuery[2];
		
			if(!queryParameters.equals(OdaGoogleTableParameter.QUERY_NO_PARAMETER)){
				
				String[] listParameters = queryParameters.split(Query.QUERY_SEPARATOR_COL_SELECTED);

				
				for(String currentDetailsParam : listParameters){
					
					String[] detailsParam = currentDetailsParam.split(Query.QUERY_SEPARATOR_SUB);
					
					String colParameter = detailsParam[0];
					String opeParameter = detailsParam[1];
					
					listInputParameter.add(new OdaGoogleTableParameter(colParameter, opeParameter));
				}
				
				
			}
					
			changeEnableButtonParam();
			viewerParameters.refresh();
		
			
		
		
	}
	
	
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		
		//Query
		String queryText = getQueryText(design);
		design.setQueryText(queryText);

		//Parameters
		if(connection == null){
			design.setParameters( null );
		}
		else{
			DataSetParameters dataSetParameter = null;
			try {
				IQuery query = connection.newQuery(null);
				query.prepare(queryText);
				 IParameterMetaData pMd = query.getParameterMetaData();
				  
				 dataSetParameter = DesignSessionUtil.toDataSetParametersDesign( pMd,ParameterMode.IN_LITERAL );
			} catch (OdaException e) {
				e.printStackTrace();
			}
					
			design.setParameters( dataSetParameter );
		}
		
		design.setResultSets(null);
		
		return design;
	}


	private String getQueryText(DataSetDesign design) {
		
		String query = "";
		
		if(listInputListColum != null){
			
		//First element in the query : Columns selected.
			for(OdaGoogleTableColumn col: listInputTableColumn){
				query += col.getColName() + Query.QUERY_SEPARATOR_COL_SELECTED;
			}

			query +=  Query.QUERY_SEPARATOR_ELEMENT;
			
		//Second element in the query : Columns Labels.
			for(OdaGoogleTableColumn col: listInputTableColumn){
				query += col.getColLabelName() + Query.QUERY_SEPARATOR_COL_SELECTED;

			}

			query += Query.QUERY_SEPARATOR_ELEMENT;
			
		//Third element in the query : parameters
			if(listInputParameter.size() == 0){
				query += OdaGoogleTableParameter.QUERY_NO_PARAMETER + Query.QUERY_SEPARATOR_ELEMENT;
			}
			
			else{
				
				for(OdaGoogleTableParameter currentParam: listInputParameter){

					query += currentParam.getParamColumn() + Query.QUERY_SEPARATOR_SUB;
					query += currentParam.getParamOperator()+ Query.QUERY_SEPARATOR_SUB;
					query += Query.QUERY_SEPARATOR_COL_SELECTED ;

				}
				
				query +=  Query.QUERY_SEPARATOR_ELEMENT;
			}
			
		//Fourth element in the query : filters
			// ***** filter(s): 

			if (design == null || design.getQueryText() == null || design.getQueryText().trim().equals("")){ //$NON-NLS-1$
				query += OdaGoogleTableParameter.QUERY_NO_FILTER + Query.QUERY_SEPARATOR_ELEMENT;
			}

			else{
				String dataSetQuery = design.getQueryText();

				String[] tabQueryColAndFilter = dataSetQuery.split(Query.QUERY_SEPARATOR_ELEMENT);

				String strQueryFilter = tabQueryColAndFilter[3];
				
				query += strQueryFilter + Query.QUERY_SEPARATOR_ELEMENT;
			}
			
			
		}
		
		else{

			query = design.getQueryText();
		}

		
		
		return query;
	}


	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	

}
