package bpm.google.spreadsheet.oda.driver.ui.impl.dataset;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import bpm.google.spreadsheet.oda.driver.runtime.impl.Connection;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleDataSetFilterQuery;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleProperties;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheet;
import bpm.google.spreadsheet.oda.driver.runtime.model.OdaGoogleSheetColumn;
import bpm.google.spreadsheet.oda.driver.ui.Activator;
import bpm.google.spreadsheet.oda.driver.ui.Messages;
import bpm.google.spreadsheet.oda.driver.ui.icons.IconsName;



public class GoogleOdaFilterPage extends DataSetWizardPage {
	
	private IConnection conn;

	private ArrayList<OdaGoogleSheet> listDataSetSheets;

	private Combo comboCol, comboOperator, comboLogicalOp;

	private Text txtValue1, txtValue2;

	private OdaGoogleSheet sheetSelected;


	private Button btnAddFilter, btnDelFilter;

	private CheckboxTableViewer filterTableViewer;

	private Table table;
	
	private ArrayList<String> listDataSetFilter;
	private ArrayList<FilterDescription> listFilters;
	
	private String tempExpression, tempOperator, tempValues, tempNameFilter, tempValue1, tempValue2, tempLogical;


	public GoogleOdaFilterPage(String pageName) {
		super(pageName);
		setTitle(Messages.GoogleOdaFilterPage_0);
		setMessage(Messages.GoogleOdaFilterPage_1);
		
	}

	public GoogleOdaFilterPage(String pageName, String title,
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
		
	//------------------  DataSources Properties
		final Properties connProps = 
			DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

		
	//------------------  Runtime Connection


		try {
	          new ProgressMonitorDialog(getShell()).run(true, true,
	              new IRunnableWithProgress(){

	
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						
						monitor.beginTask(Messages.GoogleOdaFilterPage_2, IProgressMonitor.UNKNOWN);
					
						try{
							conn  = new Connection();
							conn.open(connProps);

							listDataSetSheets = new ArrayList<OdaGoogleSheet>();
							listDataSetSheets.addAll(((Connection)conn).getListSheets());

						} catch (Exception e) {
							e.printStackTrace();
						}
						
						monitor.done();
							
					}
	        	  
	          });
	        } catch (InvocationTargetException e) {
	          MessageDialog.openError(getShell(), Messages.GoogleOdaFilterPage_3, e.getMessage());
	        } catch (InterruptedException e) {
	          MessageDialog.openInformation(getShell(), Messages.GoogleOdaFilterPage_4, e.getMessage());
	        }

	 //------------------  Widgets
	        
	        listDataSetFilter = new ArrayList<String>();

			PGroup parentFilter = new PGroup(parent, SWT.SMOOTH);
			parentFilter.setText(Messages.GoogleOdaFilterPage_5);
			parentFilter.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 5));
			parentFilter.setLayout(new GridLayout(3, false));
			parentFilter.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_ADD));
			
			Label lblCol = new Label(parentFilter, SWT.SINGLE);
			lblCol.setText(Messages.GoogleOdaFilterPage_6);
			lblCol.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

			comboCol = new Combo(parentFilter, SWT.READ_ONLY);
			comboCol.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));


			Label lblOperator = new Label(parentFilter, SWT.SINGLE);
			lblOperator.setText(Messages.GoogleOdaFilterPage_7);
			lblOperator.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

			comboOperator = new Combo(parentFilter, SWT.READ_ONLY);
			comboOperator.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			comboOperator.setItems(OPERATORS_TAB);
			
			Label lblValue1 = new Label(parentFilter, SWT.SINGLE);
			lblValue1.setText(Messages.GoogleOdaFilterPage_8);
			lblValue1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

			txtValue1 = new Text(parentFilter, SWT.BORDER);
			txtValue1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			txtValue1.setEnabled(false);


			Label lblValue2 = new Label(parentFilter, SWT.SINGLE);
			lblValue2.setText(Messages.GoogleOdaFilterPage_9);
			lblValue2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

			txtValue2 = new Text(parentFilter, SWT.BORDER);
			txtValue2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			txtValue2.setEnabled(false);


			Label lblLogical = new Label(parentFilter, SWT.SINGLE);
			lblLogical.setText(Messages.GoogleOdaFilterPage_10);
			lblLogical.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

			comboLogicalOp = new Combo(parentFilter, SWT.READ_ONLY);
			comboLogicalOp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			comboLogicalOp.setItems(LOGICAL_OPERATORS_TAB);
			comboLogicalOp.setEnabled(false);    
			
			btnAddFilter = new Button(parentFilter, SWT.PUSH);
			btnAddFilter.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 1));
			btnAddFilter.setText(Messages.GoogleOdaFilterPage_11);
			btnAddFilter.setEnabled(false);
			
			initListennerText(txtValue1);
			initListennerText(txtValue2);
			initListennerBtnAddFilter();
			initListennerComboOperator();
			
			
			
			//------------------ Table viewer to show all filters

			PGroup groupTable = new PGroup(parent, SWT.SMOOTH);
			groupTable.setText(Messages.GoogleOdaFilterPage_12);
			groupTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 5));
			groupTable.setLayout(new GridLayout(1, false));
			groupTable.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_VIEW_LIST));

			filterTableViewer = FilterTableViewer.newCheckList(groupTable, SWT.BORDER);

			table = filterTableViewer.getTable();
			table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));


			new TableColumn(table, SWT.NONE).setText(Messages.GoogleOdaFilterPage_13);   
			new TableColumn(table, SWT.NONE).setText(Messages.GoogleOdaFilterPage_14);
			new TableColumn(table, SWT.NONE).setText(Messages.GoogleOdaFilterPage_15);
			new TableColumn(table, SWT.NONE).setText(Messages.GoogleOdaFilterPage_16);
			new TableColumn(table, SWT.NONE).setText(Messages.GoogleOdaFilterPage_17);

			for (int i = 0, n = table.getColumnCount(); i < n; i++) {
				table.getColumn(i).setWidth(130);
				table.getColumn(i).setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_COLUMN));
			}	   

			

			filterTableViewer.refresh();

			table.setHeaderVisible(true);
			table.setLinesVisible(true);

			initViewerWithDataset();



			//Btn del

			btnDelFilter = new Button(parent, SWT.PUSH);
			btnDelFilter.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
			btnDelFilter.setText(Messages.GoogleOdaFilterPage_18);
			initListennerBtnDelFilter();
			
			
			//Init widgets if dataset is not null
			DataSetDesign dataSetDesigned = getInitializationDesign();
			if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$

			}
			else{
				initWidgetWithDataset(dataSetDesigned);
			}
	        
		
		return parent;
	}
	
//++++++++++++++++++++++++ Get current dataSet to init Widgets	
	private void initWidgetWithDataset(DataSetDesign dataSetDesigned) {


		String queryDataset = dataSetDesigned.getQueryText();

		String[] tabElementsQuery = queryDataset.split(OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT);
	
	//------- First element: Sheet selected : Get the selected Sheet by his name
		for(OdaGoogleSheet currentSheet : listDataSetSheets){

			if(currentSheet.getSheetName().equals(tabElementsQuery[0])){
				sheetSelected = currentSheet;
			}
		}		
		
		
		
	//------- Third element: Get selected columns

		String queryColSelected = tabElementsQuery[2];

		String[] listColsSelected = queryColSelected.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);


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
				comboCol.add(col.getColName());
			}
		}
		
		
	//------- Fourth element: Get selected columns label
		
		String queryColSelectedLabel = tabElementsQuery[3];
		
		String[] listColsSelectedLabels = queryColSelectedLabel.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);
		
		
		for(int i  = 0; i < comboCol.getItemCount(); i ++){
			
			String currentLabel = listColsSelectedLabels[i];
			
			for(OdaGoogleSheetColumn currentCol : sheetSelected.getSheetListCol()){
				
				if(currentCol.getColName().equals(comboCol.getItem(i))){
					currentCol.setColLabelName(currentLabel);
				}
				else{
					currentCol.setColLabelName(currentCol.getColName());
				}
				
			}
			
		}
		
	//------- 5° element: Get filters

		String strQueryFilter = tabElementsQuery[4];


		if(strQueryFilter.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_FILTER)== false){
			comboLogicalOp.setEnabled(true);

			String[] tabFilters = strQueryFilter.split(OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY);
			
			for(int i = 0; i < tabFilters.length; i++){
				
				listDataSetFilter.add(tabFilters[i]);
				
			}
			
		}
		else{
			comboLogicalOp.setEnabled(false);
		}


	}
	

	
//++++++++++++++++++++++++ Listener on Text field
	private void initListennerText(final Text txtWidget) {

		txtWidget.addModifyListener(new ModifyListener(){

			@SuppressWarnings("unchecked")
			public void modifyText(ModifyEvent e) {

				if(txtWidget.getEnabled()){
					//------ test if user enter a character using by the dataset Query
					if((txtWidget.getText().contains(OdaGoogleDataSetFilterQuery.SEPARATOR_COLUMN_FILTER)) || (txtWidget.getText().contains(OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY))){

						setErrorMessage(OdaGoogleDataSetFilterQuery.SEPARATOR_COLUMN_FILTER + "  " + OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY + Messages.GoogleOdaFilterPage_20); //$NON-NLS-1$

						txtWidget.setBackground(new Color(Display.getDefault(),255,89,122));
						btnAddFilter.setEnabled(false);
					}

					else{

						//------ Get the value data Class
						Class tempClass = null;

						for(OdaGoogleSheetColumn col: sheetSelected.getSheetListCol()){
							if(col.getColName().equals(comboCol.getText())){
								tempClass = col.getColClass();
							}
						}


						if(txtWidget.getText().length() == 0){
							txtWidget.setBackground(null);
							btnAddFilter.setEnabled(false);
						}
						else{

							try {
								//------ test if a cast can be done. The text field become Blue if Yes...
								checkPars(txtWidget.getText(), tempClass);
								txtWidget.setBackground(new Color(Display.getDefault(),204,204,255));
								setMessage(null);
								btnAddFilter.setEnabled(true);

							} catch (Exception e2) {
								//------ ... And ref if no.
								txtWidget.setBackground(new Color(Display.getDefault(),255,89,122));
								setErrorMessage(Messages.GoogleOdaFilterPage_21 + tempClass.getSimpleName());
								btnAddFilter.setEnabled(false);
							}
							setMessage(null);

						}

					}
				}
			}



		});

	}

	//************* Method to test the parsing
	@SuppressWarnings("unchecked")
	private void checkPars(String obj, Class classObj){

		if(classObj == Integer.class){
			Integer.valueOf(obj);
		}

		else if(classObj == Double.class){
			Double.valueOf(obj);
		}

		else if(classObj == Boolean.class){
			Boolean.valueOf(obj);
		}

		else {

		}

	}

	
//++++++++++++++++++++++++ Init viewer with the current dataset
	
	private void initViewerWithDataset(){

		DataSetDesign dataSetDesigned = getInitializationDesign();

		if (dataSetDesigned == null || dataSetDesigned.getQueryText() == null || dataSetDesigned.getQueryText().trim().equals("")){ //$NON-NLS-1$
		}

		else{
			String dataSetQuery = dataSetDesigned.getQueryText();

			//++++++ Split  columns selected and filters for two new string : Columns and filters
			String[] tabQueryColAndFilter = dataSetQuery.split(OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT);

			String strQueryFilter = tabQueryColAndFilter[4];
			
			//++++++ Split Filters List, if there is greather than or equal 1 filter

			if(strQueryFilter.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_FILTER)== false){

				String[] tabFilters = strQueryFilter.split(OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY);

				listFilters = new ArrayList<FilterDescription>();

				for(int i = 0; i< tabFilters.length; i++){

					//Split the Filter to get the columns name, the operator and values
					String[] subTabFilter = tabFilters[i].split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);

					//Testes IF there is 0, 1 or 2 values
					if(subTabFilter.length == 3){
						String tempLogical = subTabFilter[2];
						if(tempLogical.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_LOGICAL)){
							tempLogical = ""; //$NON-NLS-1$
						}

						listFilters.add(new FilterDescription(subTabFilter[0],subTabFilter[1],"", "",tempLogical)); //$NON-NLS-1$ //$NON-NLS-2$
					}

					else if(subTabFilter.length == 4){

						String tempLogical = subTabFilter[3];
						if(tempLogical.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_LOGICAL)){
							tempLogical = ""; //$NON-NLS-1$
						}
						listFilters.add(new FilterDescription(subTabFilter[0],subTabFilter[1],subTabFilter[2], "", tempLogical)); //$NON-NLS-1$
					}
					else{
						String tempLogical = subTabFilter[4];
						if(tempLogical.equals(OdaGoogleDataSetFilterQuery.QUERY_NO_LOGICAL)){
							tempLogical = ""; //$NON-NLS-1$
						}
						listFilters.add(new FilterDescription(subTabFilter[0],subTabFilter[1],subTabFilter[2], subTabFilter[3],tempLogical));
					}
				}
				filterTableViewer.setInput(listFilters);
				filterTableViewer.refresh();
			}


		}
	}


	
//++++++++++++++++++++++++ Method to enable/disable values Text for each operator

	private void initListennerComboOperator() {
		comboOperator.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {	
			}

			public void widgetSelected(SelectionEvent e) {

				int indexOperatorSelected = comboOperator.getSelectionIndex();
				boolean stateBtnAddFilter = false;

				if(indexOperatorSelected == OPERATORS_INDEX_BETWEEN){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(true);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_BETWEEN_NOT){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(true);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_EQUALS){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(false);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_NOT_EQUALS){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(false);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_FALSE){
					txtValue1.setEnabled(false);
					txtValue2.setEnabled(false);
					stateBtnAddFilter = true;
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_GREATHER){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(false);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_GREATHER_EQUALS){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(false);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_LESS){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(false);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_LESS_EQUALS){
					txtValue1.setEnabled(true);
					txtValue2.setEnabled(false);
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_NULL){
					txtValue1.setEnabled(false);
					txtValue2.setEnabled(false);
					stateBtnAddFilter = true;

				}

				else if(indexOperatorSelected == OPERATORS_INDEX_NULL_NOT){
					txtValue1.setEnabled(false);
					txtValue2.setEnabled(false);
					stateBtnAddFilter = true;
				}

				else if(indexOperatorSelected == OPERATORS_INDEX_TRUE){
					txtValue1.setEnabled(false);
					txtValue2.setEnabled(false);
					stateBtnAddFilter = true;
				}
				else{
					//System.out.println(indexOperatorSelected +" " + TAB_OPERATOR[indexOperatorSelected]);
				}

				btnAddFilter.setEnabled(stateBtnAddFilter);


				txtValue1.setText(""); //$NON-NLS-1$
				txtValue2.setText(""); //$NON-NLS-1$

			}


		}
		);

	}
//++++++++++++++++++++++++++++++++++++ Listener btn del filter

	private void initListennerBtnDelFilter() {
		btnDelFilter.addSelectionListener(new SelectionAdapter(){

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {

				ArrayList<FilterDescription> listToRemoved = ((FilterTableViewer)filterTableViewer).getListFilterToRemoved();

				if(listToRemoved.isEmpty() == false){



					boolean confirmationStatus = MessageDialog.openConfirm(getShell(),
							Messages.GoogleOdaFilterPage_30,
					Messages.GoogleOdaFilterPage_31);

					if(confirmationStatus){


						//remove from the string query list
						String currentFilterName, currentFilterOp, currentFilterValue1, currentFilterValue2, currentFilterLogic = ""; //$NON-NLS-1$
						ArrayList<String> listStringtoRemoved = new ArrayList<String>();

						for(FilterDescription currentFilter: listToRemoved){

							currentFilterName = currentFilter.getFilterName();
							currentFilterOp = currentFilter.getFilterOperator();
							currentFilterValue1 = currentFilter.getFilterValue1();
							currentFilterValue2 = currentFilter.getFilterValue2();
							currentFilterLogic = currentFilter.getFilterLogicalOperator();

							if(currentFilterValue1.length() == 0){
								currentFilterValue1 = OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED;
							}

							if(currentFilterValue2.length() == 0){
								currentFilterValue2 = OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED;
							}

							if(currentFilterLogic.length() == 0){
								currentFilterLogic = OdaGoogleDataSetFilterQuery.QUERY_NO_LOGICAL;
							}




							for(String currentStringFilter: listDataSetFilter){

								if((currentStringFilter.contains(currentFilterName)) &&
										(currentStringFilter.contains(currentFilterOp)) &&
										(currentStringFilter.contains(currentFilterValue1)) &&
										(currentStringFilter.contains(currentFilterValue2)) &&
										(currentStringFilter.contains(currentFilterLogic))){

									listStringtoRemoved.add(currentStringFilter);

								}
							}


						}

						listDataSetFilter.removeAll(listStringtoRemoved);

						//Remove from the input list
						((ArrayList<FilterDescription>)filterTableViewer.getInput()).removeAll(listToRemoved);
						((FilterTableViewer)filterTableViewer).getListFilterToRemoved().clear();
						filterTableViewer.refresh();
						
						if(((ArrayList<FilterDescription>)filterTableViewer.getInput()).size() == 0){
							comboLogicalOp.setEnabled(false);
						}
						else{
							comboLogicalOp.setEnabled(true);
						}

					}

				}
			}	

		});
	}

//++++++++++++++++++++++++++++++++++++ Listener btn ADD filter
	private void initListennerBtnAddFilter() {
		
		btnAddFilter.addSelectionListener(new SelectionAdapter(){

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {

				
				tempExpression = comboCol.getItem(comboCol.getSelectionIndex());

				if(comboOperator.getSelectionIndex() == -1){
					tempOperator = ""; //$NON-NLS-1$
				}
				else{
					tempOperator = OPERATORS_TAB[comboOperator.getSelectionIndex()];
					
				}

				


				if((tempExpression.length() == 0) && tempOperator.length() == 0){
					setErrorMessage(Messages.GoogleOdaFilterPage_34);
				}

				else{

					tempNameFilter = ""; //$NON-NLS-1$
					tempValue1 = ""; //$NON-NLS-1$
					tempValue2 = ""; //$NON-NLS-1$

					//If 2 values are needed and 2 miss
					if((txtValue1.getEnabled()) && ( txtValue2.getEnabled()) && (txtValue1.getText().length() == 0) && (txtValue2.getText().length() == 0)){
						setErrorMessage(Messages.GoogleOdaFilterPage_38);
					}

					//If 2 values are needed and the first miss
					else if((txtValue1.getEnabled()) && ( txtValue2.getEnabled()) && (txtValue1.getText().length() == 0 == false) && (txtValue2.getText().length() == 0)){
						setErrorMessage(Messages.GoogleOdaFilterPage_39);
					}

					//If 2 values are needed and the second miss
					else if((txtValue1.getEnabled()) && ( txtValue2.getEnabled()) && (txtValue1.getText().length() == 0) && (txtValue2.getText().length() != 0 )){
						setErrorMessage(Messages.GoogleOdaFilterPage_40);
					}

					//Test if 1 value is needed
					else if((txtValue1.getEnabled()) && (txtValue2.getEnabled() == false) && (txtValue1.getText().length() == 0)){
						setErrorMessage(Messages.GoogleOdaFilterPage_41);
					}

					//If values are ok! 
					else{

						//Get the first value if needed
						if((txtValue1.getEnabled())){
							tempValues = txtValue1.getText();
							tempValue1 = txtValue1.getText();
						}

						//Get the second value if needed
						if((txtValue2.getEnabled())){
							tempValues += OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED + txtValue2.getText();
							tempValue2 = txtValue2.getText();
						}

						//Return the filter if there are one or two values
						tempNameFilter = tempExpression + OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED + tempOperator+ OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED + tempValues +  OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED;

						//Return the filter if there are no values
						if((txtValue1.getEnabled() == false) && (txtValue2.getEnabled() == false)){
							tempNameFilter = tempExpression + OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED + tempOperator + OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED;
						}
						//Get the logical Operator
						tempLogical = ""; //$NON-NLS-1$
						if(listDataSetFilter.size() >= 1){
							tempLogical = comboLogicalOp.getText();
						}


						//-----Add the new filter into the viewer
						((ArrayList<FilterDescription>)filterTableViewer.getInput()).add(new FilterDescription(tempExpression, tempOperator, tempValue1, tempValue2, tempLogical));
						filterTableViewer.refresh();

						//Put logical separator into the string

						if(tempLogical.length() == 0){
							tempLogical = OdaGoogleDataSetFilterQuery.QUERY_NO_LOGICAL;
						}
						tempNameFilter += tempLogical + OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED + OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY;

						//-----Add the new filter into the arrayList		
						listDataSetFilter.add(tempNameFilter);


						//Refresh the group filter
						txtValue1.setText(""); //$NON-NLS-1$
						txtValue2.setText(""); //$NON-NLS-1$

						//Add the possibily to add logical operator for a filter if there are more than 1 filter

						if(listDataSetFilter.size() == 1){
							comboLogicalOp.setEnabled(true);
						}
					}
				}
			}
		});
	
	}
	

	//++++++++++++++++++++++++ Collect Dataset
		@Override
		protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
			
			String queryText = getQueryText(design);

			design.setQueryText(queryText);

			design.setResultSets(null);
			
			//System.out.println("Query Filter : " + queryText);

			return design;
		}


		private String getQueryText(DataSetDesign dataSetDesign) {
			String query = ""; //$NON-NLS-1$

			if(comboCol != null){

				String queryDataset = dataSetDesign.getQueryText();

				String[] tabElementsQuery = queryDataset.split(OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT);
				
			//Update combo col if needed
				
				if(sheetSelected != null){
					majComboCol(dataSetDesign);
				}

			//get currents elements, to change only filters
				
				for(int i = 0; i <= 3; i++){
					
					query += tabElementsQuery[i]+ OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
				}
				
			// filters: 
				
				if(listDataSetFilter.isEmpty()){

					query += OdaGoogleDataSetFilterQuery.QUERY_NO_FILTER;
				}

				else{
					for(String current: listDataSetFilter){

						if(current.endsWith(OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY)== false){
							current += OdaGoogleDataSetFilterQuery.SEPARATOR_QUERY;
						}

						query += current ; 
					}
				}


				query += OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
				
			
				//Parameters
				query += tabElementsQuery[5]+ OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT;
			}

			else{

				query = dataSetDesign.getQueryText();
			}
			
			return query;
		}
	
	
	
	
	
	
private void majComboCol(DataSetDesign dataSetDesign) {
	
	comboCol.removeAll();
	
	String queryDataset = dataSetDesign.getQueryText();

	String[] tabElementsQuery = queryDataset.split(OdaGoogleProperties.QUERY_SEPARATOR_ELEMENT);

	String queryColSelected = tabElementsQuery[2];

	String[] listColsSelected = queryColSelected.split(OdaGoogleProperties.QUERY_SEPARATOR_COL_SELECTED);


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
			comboCol.add(col.getColName());
		}
	}
			
}






//------------------------------------------- CONSTANTS------------------------------------------ 
	
	
	// Operators
	public final static String[] OPERATORS_TAB = {Messages.GoogleOdaFilterPage_46, Messages.GoogleOdaFilterPage_47, Messages.GoogleOdaFilterPage_48,
		Messages.GoogleOdaFilterPage_49, Messages.GoogleOdaFilterPage_50, Messages.GoogleOdaFilterPage_51, 
		Messages.GoogleOdaFilterPage_52, Messages.GoogleOdaFilterPage_53,Messages.GoogleOdaFilterPage_54, Messages.GoogleOdaFilterPage_55,
		Messages.GoogleOdaFilterPage_56, Messages.GoogleOdaFilterPage_57};

	public final static int OPERATORS_INDEX_EQUALS = 0,
							OPERATORS_INDEX_NOT_EQUALS = 1,
							OPERATORS_INDEX_LESS = 2,
							OPERATORS_INDEX_LESS_EQUALS = 3,
							OPERATORS_INDEX_GREATHER = 4,
							OPERATORS_INDEX_GREATHER_EQUALS = 5,
							OPERATORS_INDEX_BETWEEN = 6,
							OPERATORS_INDEX_BETWEEN_NOT = 7,
							OPERATORS_INDEX_NULL = 8,
							OPERATORS_INDEX_NULL_NOT = 9,
							OPERATORS_INDEX_TRUE = 10,
							OPERATORS_INDEX_FALSE = 11;

	//------------------ Colums'filter viewer 
	public final static String[] VIEWER_COLUMNS_NAME = {Messages.GoogleOdaFilterPage_58,Messages.GoogleOdaFilterPage_59, Messages.GoogleOdaFilterPage_60, Messages.GoogleOdaFilterPage_61, Messages.GoogleOdaFilterPage_62};

	public final static int VIEWER_COLUMNS_INDEX_EXPRESSION = 0,
							VIEWER_COLUMNS_INDEX_OPERATOR = 1,
							VIEWER_COLUMNS_INDEX_VALUE1 = 2,
							VIEWER_COLUMNS_INDEX_VALUE2 = 3,	
							VIEWER_COLUMNS_INDEX_LOGICAL = 4;
	
	
	public final static String[] LOGICAL_OPERATORS_TAB = {"", Messages.GoogleOdaFilterPage_64, Messages.GoogleOdaFilterPage_65}; //$NON-NLS-1$
	public final static int LOGICAL_INDEX_NONE = 0;
	public final static int LOGICAL_INDEX_AND = 1;
	public final static int LOGICAL_INDEX_OR = 2;
	

}
