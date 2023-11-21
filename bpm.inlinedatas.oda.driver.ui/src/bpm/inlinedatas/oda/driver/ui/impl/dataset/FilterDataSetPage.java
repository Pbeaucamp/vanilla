package bpm.inlinedatas.oda.driver.ui.impl.dataset;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.dialogs.MessageDialog;
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

import bpm.inlinedatas.oda.driver.runtime.impl.Connection;
import bpm.inlinedatas.oda.driver.runtime.impl.Query;
import bpm.inlinedatas.oda.driver.runtime.structureProperties.FilterDatas;
import bpm.inlinedatas.oda.driver.runtime.structureProperties.PropertieColumn;
import bpm.inlinedatas.oda.driver.ui.Activator;
import bpm.inlinedatas.oda.driver.ui.icons.IconNames;
import bpm.inlinedatas.oda.driver.ui.model.FilterDescription;


public class FilterDataSetPage extends DataSetWizardPage {

	private ArrayList<FilterDescription> inputFilters;
	private TableViewer viewer;
	private IStructuredSelection selectionFilter;
	private Table table;
	
	private ArrayList<PropertieColumn> listDataSelected;
	
	private Button btnAdd, btnRemove;
	
	private Connection connection;
	
	private final static String MSG_TITLE = "Define Filters: Add a filter, and clik on cells to set them.";


	public FilterDataSetPage(String pageName){
		super(pageName);
		setTitle("Filter's Options");
		setMessage(MSG_TITLE);
		
	}

	@Override
	public void createPageCustomControl(Composite parent) {

		Composite inCompo = new Composite(parent, SWT.NONE);
		inCompo.setLayout(new GridLayout(2, false));  

		setControl( createPageControl( inCompo ) );

	}


	private Control createPageControl(Composite parent) {
		
		inputFilters = new ArrayList<FilterDescription>();
		
		//Table viewer

		PGroup groupTable = new PGroup(parent, SWT.SMOOTH);
		groupTable.setText("Filters List");
		groupTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		groupTable.setLayout(new GridLayout(2, false));
		groupTable.setImage(Activator.getDefault().getImageRegistry().get(IconNames.COLUMN));

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
		viewer.setInput(inputFilters);
		
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
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconNames.ADD_COL));
		btnAdd.setText("Add");
		
		btnRemove = new Button(compoBtn, SWT.NONE);
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		btnRemove.setImage(Activator.getDefault().getImageRegistry().get(IconNames.DEL_COL));
		btnRemove.setText("Remove");
		
		btnAdd.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				inputFilters.add(new FilterDescription());
				
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
							inputFilters.remove(((FilterDescription)obj));
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
			
			listDataSelected = connection.getListeProperties();
			
			
		} catch (OdaException e) {
			e.printStackTrace();
		}
		
		
		if(listDataSelected != null){
			
			//Get filters
			
			DataSetDesign dataSetDesigned = getInitializationDesign();
			if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$

			}
			else{
				
				String[] tabElementsQuery = design.getQueryText().split(Query.QUERY_SEPARATOR_ELEMENT);
				
				if(!tabElementsQuery[1].equals(Query.NO_FILTER)){
					
					inputFilters.clear();

						String[] tabFilters = tabElementsQuery[1].split(Query.QUERY_SEPARATOR_COL_SELECTED);
					
						for(int i = 0; i< tabFilters.length; i++){

							//Split the Filter to get the coolumns name, the operator and values
							String[] subTabFilter = tabFilters[i].split(Query.QUERY_SEPARATOR_SUB);
							
								//Testes IF there is 0, 1 or 2 values
								if(subTabFilter.length == 3){
									inputFilters.add(new FilterDescription(subTabFilter[0],subTabFilter[1],Query.FILTER_NO_VALUES, Query.FILTER_NO_VALUES,subTabFilter[2]));
								}
								else if(subTabFilter.length == 4){
									inputFilters.add(new FilterDescription(subTabFilter[0],subTabFilter[1],subTabFilter[2], Query.FILTER_NO_VALUES,subTabFilter[3]));
								}
								else{
									inputFilters.add(new FilterDescription(subTabFilter[0],subTabFilter[1],subTabFilter[2], subTabFilter[3],subTabFilter[4]));
								}
							}
						
						viewer.refresh();
						
					}
				}
			}
		}
	
	protected class MyTableLabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex == 0){
				return Activator.getDefault().getImageRegistry().get(IconNames.OK);
			}
			else{
				return null;
			}
		}

		public String getColumnText(Object element, int columnIndex) {
			FilterDescription currentFilter = (FilterDescription)element;
			switch(columnIndex) {
			case 0 : return currentFilter.getFilterName();
			case 1 : return currentFilter.getFilterOperator();
			case 2 : return currentFilter.getFilterValue1();
			case 3 : return currentFilter.getFilterValue2();
			case 4 : return currentFilter.getFilterLogicalOperator();
			
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
			tabParamOperators = FilterDatas.TAB_OPERATOR;
			tabParamLogicals = FilterDatas.TAB_LOGICALS_OPERATOR;
			
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
			FilterDescription current = (FilterDescription)element;
			int indexCombo = Integer.valueOf(value.toString());
			
			if(typeCombo == 1){
				current.setFilterName(getTabColumnSelected()[indexCombo]);
			}
			
			else if (typeCombo == 2){
				current.setFilterOperator(tabParamOperators[indexCombo]);
			}
			else{
				current.setFilterLogicalOperator(tabParamLogicals[indexCombo]);
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
			FilterDescription current = (FilterDescription)element;
			
			return(checkOperatorToEnableValue(current, type));			
		}


		protected CellEditor getCellEditor(Object element) {
			return editor;
		}


		protected Object getValue(Object element) {
			FilterDescription current = (FilterDescription)element;
			
			//System.out.println("> " + current.getNameColFilter());
			
			if(type == 1){
				return current.getFilterValue1();
			}
			else{
				return current.getFilterValue2();
			}
		}


		protected void setValue(Object element, Object value) {
			FilterDescription current = (FilterDescription)element;
			
			if((type == 1) && checkValue(current, value)){
				current.setFilterValue1((String)value);
			}
			
			if((type == 2) && checkValue(current, value)){
				current.setFilterValue2((String)value);
			}
			
			
			myViewer.refresh();
		}
	}
	
	
	private String[] getTabColumnSelected(){
		
		DataSetDesign dataSetDesigned = getInitializationDesign();
		
		String[] tabColSel;
		
		if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$
			
			tabColSel = new String[listDataSelected.size()];
			
			for(int i = 0; i < listDataSelected.size(); i++){
				tabColSel[i] = listDataSelected.get(i).getPropertieColName();
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

	
	
	public boolean checkOperatorToEnableValue(FilterDescription current, int type) {
		
		String[] tab = FilterDatas.TAB_OPERATOR;
		
		String ope = current.getFilterOperator();
		
		if(ope.equals(tab[FilterDatas.INDEX_BETWEEN])){
			
			return true;
			
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_BETWEEN_NOT])){
			
			return true;
			
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_FALSE])){
			return false;
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_GREATHER])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_GREATHER_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_LESS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_LESS_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_NOT_EQUALS])){
			if(type ==1){
				return true;
			}
			else{
				return false;
			}
		}

		else if(ope.equals(tab[FilterDatas.INDEX_NULL])){
			return false;
		}
		
		else if(ope.equals(tab[FilterDatas.INDEX_NULL_NOT])){
			return false;
		}
		
		else if (ope.equals(tab[FilterDatas.INDEX_TRUE])){
			return false;
		}
		
		else{
			return false;
		}
	}

	public boolean checkValue(FilterDescription filter, Object value) {
		
		Class tempClass = null;
		
		//Find the column's class
		for(PropertieColumn col : listDataSelected){
			
			if(col.getPropertieColName().equals(filter.getFilterName())){
				tempClass = col.getPropertieColType();
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
			query += tabElementsQuery[0]+ Query.QUERY_SEPARATOR_ELEMENT;

			// filters: 
			if(inputFilters.isEmpty()){

				query += Query.NO_FILTER;
			}

			else{
				
				for(FilterDescription currentFilter : inputFilters){
					
					query += currentFilter.getFilterName() + Query.QUERY_SEPARATOR_SUB;
					query += currentFilter.getFilterOperator() + Query.QUERY_SEPARATOR_SUB;
					
					//Values 1 and 2
					if(currentFilter.getFilterValue1() .length() != 0){
						query += currentFilter.getFilterValue1() + Query.QUERY_SEPARATOR_SUB;
					}
					
					if(currentFilter.getFilterValue2().length() != 0){
						query += currentFilter.getFilterValue2() + Query.QUERY_SEPARATOR_SUB;
					}
					
					//Logical operator
					if(currentFilter.getFilterLogicalOperator().length() != 0){
						query += currentFilter.getFilterLogicalOperator() + Query.QUERY_SEPARATOR_SUB;
					}
					else{
						query += Query.NO_LOGICAL + Query.QUERY_SEPARATOR_SUB;
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