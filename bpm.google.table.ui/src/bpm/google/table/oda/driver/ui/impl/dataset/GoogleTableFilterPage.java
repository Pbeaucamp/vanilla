package bpm.google.table.oda.driver.ui.impl.dataset;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;

import bpm.google.table.oda.driver.runtime.impl.Connection;
import bpm.google.table.oda.driver.runtime.impl.Query;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableColumn;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableFilter;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableModel;
import bpm.google.table.oda.driver.runtime.model.OdaGoogleTableParameter;
import bpm.google.table.oda.driver.ui.Activator;
import bpm.google.table.oda.driver.ui.dialogs.MessageTable;
import bpm.google.table.oda.driver.ui.icons.IconsName;

public class GoogleTableFilterPage extends DataSetWizardPage{
	
	private ArrayList<OdaGoogleTableFilter> listInputFilters;
	private TableViewer viewer;
	private IStructuredSelection selectionFilter;
	private Table table;
	
	private OdaGoogleTableModel tableSelected;
	
	private Button btnAdd, btnRemove;
	
	private Connection connection;
	
	private final static String MSG_TITLE = "Define Filters: Add a filter, and clik on cells to set them.";
	

	public GoogleTableFilterPage(String pageName) {
		super(pageName);
		setTitle("Filter's Options");
		setMessage(MSG_TITLE);
		
	}

	public GoogleTableFilterPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createPageCustomControl(Composite arg0) {
		Composite inCompo = new Composite(arg0, SWT.NONE);
		inCompo.setLayout(new GridLayout(2, false));  

		setControl( createPageControl( inCompo ) );
		

	}

	private Control createPageControl(Composite parent) {
		
		listInputFilters = new ArrayList<OdaGoogleTableFilter>();
		
		//Table viewer

		PGroup groupTable = new PGroup(parent, SWT.SMOOTH);
		groupTable.setText("Filters List");
		groupTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		groupTable.setLayout(new GridLayout(2, false));
		groupTable.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_VIEW_LIST));

	//Viewer
		 viewer = new TableViewer(groupTable, SWT.BORDER | SWT.FULL_SELECTION  | SWT.H_SCROLL | SWT.V_SCROLL);
		
	//Content provider
		viewer.setContentProvider(new IStructuredContentProvider(){

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
		
	//Columns
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("Expression");   
		column.setEditingSupport(new ComboEditingSupport(viewer, 1));
		
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("Operator");
		column.setEditingSupport(new ComboEditingSupport(viewer, 2));
		
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("Value1");
		column.setEditingSupport(new TextEditingSupport(viewer, 1));
		
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("Value2");
		column.setEditingSupport(new TextEditingSupport(viewer, 2));
		
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText("Logical.");
		column.setEditingSupport(new ComboEditingSupport(viewer, 3));
		
		
	//Label Provider
		viewer.setLabelProvider(new MyTableLabelProvider());
	
	//Input
		viewer.setInput(listInputFilters);
		
	//Selection
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {    	  
				selectionFilter = (IStructuredSelection)event.getSelection();
			}
		});
		
		
	//Table
		table = viewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));

		for (int i = 0, n = table.getColumnCount(); i < n; i++) {
			table.getColumn(i).setWidth(95);
		}	   

		

		viewer.refresh();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		
		
	//Control
		
		Composite compoBtn = new Composite(groupTable, SWT.NONE);
		compoBtn.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		compoBtn.setLayout(new GridLayout(1, false));
		
		btnAdd = new Button(compoBtn, SWT.NONE);
		btnAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_ADD));
		btnAdd.setText("Add");
		
		btnRemove = new Button(compoBtn, SWT.NONE);
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		btnRemove.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DELETE));
		btnRemove.setText("Remove");
		
		btnAdd.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				listInputFilters.add(new OdaGoogleTableFilter());
				
				viewer.refresh();
			}
			
		});
		
		btnRemove.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				
					if(selectionFilter == null){
						MessageDialog.openInformation(getShell(),
								"Oda Google driver Informations",
								"Please, select Filter(s) to remove.");
					}
					
					else{
						for(Object obj : selectionFilter.toArray()){
							listInputFilters.remove(((OdaGoogleTableFilter)obj));
						}
						viewer.refresh();
						
					}
				}
		});
		
		//Init widgets with the current data set if need
		DataSetDesign dataSetDesigned = getInitializationDesign();

		initViewerWithDataset(dataSetDesigned);
		
		return parent;
	}


	private void initViewerWithDataset(DataSetDesign design) {
		
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
			
			//Get filters
			
			DataSetDesign dataSetDesigned = getInitializationDesign();
			if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$

			}
			else{
				
				String[] tabElementsQuery = design.getQueryText().split(Query.QUERY_SEPARATOR_ELEMENT);
				
				if(!tabElementsQuery[3].equals(OdaGoogleTableParameter.QUERY_NO_FILTER)){
					
					listInputFilters.clear();
					
					//Split the query bu filters
					String[] filters = tabElementsQuery[3].split(Query.QUERY_SEPARATOR_COL_SELECTED);
					
					//Get each filters
					for(String filter : filters){
						
						//Split to get details
						String[] filterDetail = filter.split(Query.QUERY_SEPARATOR_SUB);
						
						//Init differents filters by their size : 0 / 1 or 2 values
						if(filterDetail.length == 3){
							listInputFilters.add(new OdaGoogleTableFilter(filterDetail[0], filterDetail[1],"","",filterDetail[2]));
						}
						
						else if(filterDetail.length == 4){
							listInputFilters.add(new OdaGoogleTableFilter(filterDetail[0], filterDetail[1],filterDetail[2],"",filterDetail[3]));
						}
						else{
							listInputFilters.add(new OdaGoogleTableFilter(filterDetail[0], filterDetail[1],filterDetail[2],filterDetail[3],filterDetail[4]));
						}
						
						viewer.refresh();
						
						
					}
				}
				
			}
		
			
			
			
		}
		
	}
	
	
	class MyTableLabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex == 0){
				return Activator.getDefault().getImageRegistry().get(IconsName.ICO_OK);
			}
			else{
				return null;
			}
		}

		public String getColumnText(Object element, int columnIndex) {
			OdaGoogleTableFilter currentFilter = (OdaGoogleTableFilter)element;
			switch(columnIndex) {
			case 0 : return currentFilter.getNameColFilter();
			case 1 : return currentFilter.getTypeFilter();
			case 2 : return currentFilter.getValue1Filter();
			case 3 : return currentFilter.getValue2Filter();
			case 4 : return currentFilter.getLogicalSeparator();
			
			default : return "";
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
	}
	
	
	protected class ComboEditingSupport extends EditingSupport {

		private CellEditor editor;
		private Viewer myViewer;
		private String[] tabParamOperators, tabParamLogicals;
		private int typeCombo;
		
		public ComboEditingSupport(TableViewer viewer, int type) {
			super(viewer);
			
			myViewer = viewer;
			typeCombo = type; //Type 1 > combo with column. type 2 > operator. type 3 > logicals
			tabParamOperators = OdaGoogleTableParameter.OPERATORS_TAB;
			tabParamLogicals = OdaGoogleTableParameter.LOGICAL_OPERATORS_TAB;
			
		}
		protected boolean canEdit(Object element) {
			return true;
		}

		protected CellEditor getCellEditor(Object element) {
			
			if(typeCombo == 1){
				editor = new ComboBoxCellEditor((Composite)myViewer.getControl(),getTabColumnSelected() );
			
			}
			
			else if(typeCombo == 2){
				editor = new ComboBoxCellEditor((Composite)myViewer.getControl(),tabParamOperators );
			}
			else{
				editor = new ComboBoxCellEditor((Composite)myViewer.getControl(),tabParamLogicals );
			}
			
			return editor;
		}	

		protected Object getValue(Object element) {

			return new Integer(0); 
		}

		protected void setValue(Object element, Object value) {
			OdaGoogleTableFilter current = (OdaGoogleTableFilter)element;
			int indexCombo = Integer.valueOf(value.toString());
			
			if(typeCombo == 1){
				current.setNameColFilter(getTabColumnSelected()[indexCombo]);
			}
			
			else if (typeCombo == 2){
				current.setTypeFilter(tabParamOperators[indexCombo]);
			}
			else{
				current.setLogicalSeparator(tabParamLogicals[indexCombo]);
			}
			
			myViewer.refresh();
		}	
	}
	
	protected class TextEditingSupport extends EditingSupport {
		
		private CellEditor editor;
		private Viewer myViewer;
		private int type;
		
		public TextEditingSupport(ColumnViewer viewer, int pType) {
			super(viewer);
			myViewer = viewer;
			editor = new TextCellEditor((Composite)viewer.getControl());
			type = pType;
		}
		
		protected boolean canEdit(Object element) {
			OdaGoogleTableFilter current = (OdaGoogleTableFilter)element;
			
			return(checkOperatorToEnableValue(current, type));			
		}


		protected CellEditor getCellEditor(Object element) {
			return editor;
		}


		protected Object getValue(Object element) {
			OdaGoogleTableFilter current = (OdaGoogleTableFilter)element;
			
			//System.out.println("> " + current.getNameColFilter());
			
			if(type == 1){
				return current.getValue1Filter();
			}
			else{
				return current.getValue2Filter();
			}
		}


		protected void setValue(Object element, Object value) {
			OdaGoogleTableFilter current = (OdaGoogleTableFilter)element;
			
			if((type == 1) && checkValue(current, value)){
				current.setValue1Filter((String)value);
			}
			
			if((type == 2) && checkValue(current, value)){
				current.setValue2Filter((String)value);
			}
			
			
			myViewer.refresh();
		}
	}
	
	
	protected String[] getTabColumnSelected(){
		
		
		DataSetDesign dataSetDesigned = getInitializationDesign();
		
		String[] tabColSel;
		
		
		
		if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$
			
			tabColSel = new String[tableSelected.getTableCountCol()];
			
			for(int i = 0; i < tableSelected.getTableCountCol(); i++){
				tabColSel[i] = tableSelected.getListColumns().get(i).getColName();
			}
		}
		
		else{
			
			String query = dataSetDesigned.getQueryText();
			
			String[] strElementsQuery = query.split(Query.QUERY_SEPARATOR_ELEMENT);
			
			String strNamesCols = strElementsQuery[0];
			
			tabColSel = strNamesCols.split(Query.QUERY_SEPARATOR_COL_SELECTED);
			
		}
		
		return tabColSel;
	}

	
	
	public boolean checkOperatorToEnableValue(OdaGoogleTableFilter current, int type) {
		
		String[] tab = OdaGoogleTableParameter.OPERATORS_TAB;
		
		if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_BETWEEN])){
			
			return true;
			
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_BETWEEN_NOT])){
			
			return true;
			
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_FALSE])){
			return false;
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_GREATHER])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_GREATHER_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_LESS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_LESS_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_NOT_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}

		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_NULL])){
			return false;
		}
		
		else if(current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_NULL_NOT])){
			return false;
		}
		
		else if (current.getTypeFilter().equals(tab[OdaGoogleTableParameter.OPERATORS_INDEX_TRUE])){
			return false;
		}
		
		else{
			return false;
		}
	}

	public boolean checkValue(OdaGoogleTableFilter filter, Object value) {
		
		Class tempClass = null;
		
		//Find the column's class
		for(OdaGoogleTableColumn col : tableSelected.getListColumns()){
			
			if(col.getColName().equals(filter.getNameColFilter())){
				tempClass = col.getColClass();
			}
		}
		
		//Compare the value.
		try {
			if(tempClass == Integer.class){
				Integer.valueOf(value.toString());
			}

			else if(tempClass == Double.class){
				Double.valueOf(value.toString());
			}

			else if(tempClass == Boolean.class){
				Boolean.valueOf(value.toString());
			}

			else { }
			
			setMessage(MSG_TITLE);
			return  true;
			
			
		} catch (Exception e) {
			
			setErrorMessage("This Value must be a " + tempClass.getSimpleName());
			return false;
		}
		
	}

	
	
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		
		String queryText = getQueryText(design);

		design.setQueryText(queryText);

		
		
		design.setResultSets(null);
		
		return design;
	}


	private String getQueryText(DataSetDesign dataSetDesign) {
		String query = "";

		if(viewer != null){

			String queryDataset = dataSetDesign.getQueryText();

			String[] tabElementsQuery = queryDataset.split(Query.QUERY_SEPARATOR_ELEMENT);

			//get currents elements, to change only filters
			for(int i = 0; i <= 2; i++){

				query += tabElementsQuery[i]+ Query.QUERY_SEPARATOR_ELEMENT;
			}

			// filters: 
			if(listInputFilters.isEmpty()){

				query += OdaGoogleTableParameter.QUERY_NO_FILTER;
			}

			else{
				
				for(OdaGoogleTableFilter currentFilter : listInputFilters){
					
					query += currentFilter.getNameColFilter() + Query.QUERY_SEPARATOR_SUB;
					query += currentFilter.getTypeFilter() + Query.QUERY_SEPARATOR_SUB;
					
					//Values 1 and 2
					if(currentFilter.getValue1Filter().length() != 0){
						query += currentFilter.getValue1Filter() + Query.QUERY_SEPARATOR_SUB;
					}
					
					if(currentFilter.getValue2Filter().length() != 0){
						query += currentFilter.getValue2Filter() + Query.QUERY_SEPARATOR_SUB;
					}
					
					//Logical operator
					if(currentFilter.getLogicalSeparator().length() != 0){
						query += currentFilter.getLogicalSeparator() + Query.QUERY_SEPARATOR_SUB;
					}
					else{
						query += OdaGoogleTableParameter.QUERY_NO_LOGICAL + Query.QUERY_SEPARATOR_SUB;
					}
					
					query += Query.QUERY_SEPARATOR_COL_SELECTED;
				}
			}


			query += Query.QUERY_SEPARATOR_ELEMENT;

		}

		else{

			query = dataSetDesign.getQueryText();
		}
		
		return query;
	}



}
