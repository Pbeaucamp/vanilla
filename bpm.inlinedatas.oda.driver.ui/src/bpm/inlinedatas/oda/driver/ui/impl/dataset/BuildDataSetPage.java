package bpm.inlinedatas.oda.driver.ui.impl.dataset;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

import bpm.inlinedatas.oda.driver.runtime.impl.Connection;
import bpm.inlinedatas.oda.driver.runtime.impl.Query;
import bpm.inlinedatas.oda.driver.runtime.structureProperties.PropertieColumn;
import bpm.inlinedatas.oda.driver.ui.Activator;
import bpm.inlinedatas.oda.driver.ui.icons.IconNames;



public class BuildDataSetPage extends DataSetWizardPage{


	private static String DEFAULT_MESSAGE = "Columns selection";
	private Button btnAdd, btnRemove, btnAddAll, btnRemoveAll;
	private List listColSelected, listColumns;

	private IConnection conn;

	private String queryText;
	private  String[] tabColSelected;

	public BuildDataSetPage( String pageName )
	{
		super( pageName );
		setTitle("DataSet Building");
		setMessage( DEFAULT_MESSAGE );

		

	}


	@Override
	public void createPageCustomControl(Composite parent) {
		
		setControl(createPageControl(parent));
		
		initializeControl();

	}


	private Control createPageControl(Composite pParent) {
		
		Composite parent = new Composite(pParent, SWT.NONE);
		parent.setLayout(new GridLayout(3, false));
		
		
	//*** All columns from DataSource
		
		PShelf shelf = new PShelf(parent, SWT.SMOOTH | SWT.BORDER);
		shelf.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,1,1));
		shelf.setRenderer(new RedmondShelfRenderer());
		
		PShelfItem item = new PShelfItem(shelf, SWT.NONE);
		item.setImage(Activator.getDefault().getImageRegistry().get(IconNames.COLUMN));
		item.setText("Available columns");
		
		item.getBody().setLayout(new GridLayout(1, false));

		listColumns = new List(item.getBody(), SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
		listColumns.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));

	
	//*** Control buttons

		Composite compo = new Composite(parent, SWT.NONE);
		compo.setLayoutData(new GridData(GridData.CENTER, GridData.FILL,false,true,1,1));
		compo.setLayout(new GridLayout(1, false));
		
		btnAdd = new Button(compo, SWT.PUSH);
		btnAdd.setText("Add       ");
		btnAdd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAdd.setImage(Activator.getDefault().getImageRegistry().get(IconNames.ADD_COL));
		
		btnRemove = new Button(compo, SWT.PUSH);
		btnRemove.setText("Remove    ");
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemove.setImage(Activator.getDefault().getImageRegistry().get(IconNames.DEL_COL));
		

		btnAddAll = new Button(compo, SWT.PUSH);
		btnAddAll.setText("Add all   ");
		btnAddAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnAddAll.setImage(Activator.getDefault().getImageRegistry().get(IconNames.ADD_ALL));
		
		btnRemoveAll = new Button(compo, SWT.PUSH);
		btnRemoveAll.setText("Remove all");
		btnRemoveAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemoveAll.setImage(Activator.getDefault().getImageRegistry().get(IconNames.DEL_ALL));
		

	
	//*** Selected columns 
		
		shelf = new PShelf(parent, SWT.SMOOTH | SWT.BORDER);
		shelf.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,1,1));
		shelf.setRenderer(new RedmondShelfRenderer());
		
		item = new PShelfItem(shelf, SWT.NONE);
		item.setImage(Activator.getDefault().getImageRegistry().get(IconNames.OK));
		item.setText("Selected columns");
		
		item.getBody().setLayout(new GridLayout(1, false));

		listColSelected = new List(item.getBody(), SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listColSelected.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 5));

		initListennerButtons();


		return parent;
	}

	private void initializeControl() {
		

		//DataSources Properties
		Properties connProps = 

			DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());


		try {
			conn  = new Connection();
			conn.open(connProps);

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		initListColumns((Connection) conn);
		
	}

	
	protected void initListColumns(Connection conn){
		DataSetDesign dataSetDesign = getInitializationDesign();

		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$

			for(PropertieColumn current: conn.getListeProperties()){

				listColumns.add(current.getPropertieColName());

			}
		}

		else{

			String queryDataSet = dataSetDesign.getQueryText();

			//++++++ Split  columns selected and filters for two new string : Columns and filters
			String[] tabQueryColAndFilter = queryDataSet.split(Query.QUERY_SEPARATOR_ELEMENT);

			String strQueryCol = tabQueryColAndFilter[0];


			//++++++ Split columns list	
			String[] tabCols = strQueryCol.split(Query.QUERY_SEPARATOR_COL_SELECTED);

			listColSelected.setItems(tabCols);

			boolean finded;

			for(PropertieColumn col: conn.getListeProperties()){
				finded = false;

				for(int i = 0; i< tabCols.length; i++){

					if(col.getPropertieColName().equals(tabCols[i])){
						finded = true;
					}

				}

				if(finded == false){
					listColumns.add(col.getPropertieColName());
				}
			}

		}


	}


	protected void initListennerButtons( ) {

		//---------- BUTTON ADD COLUMNS	
		btnAdd.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(listColumns.getSelection().length == 0){
					setErrorMessage("Click on column's Name to select it");
				}
				else{

					String[] tabColSelected = listColumns.getSelection();

					for(int i = 0; i < tabColSelected.length; i++){

						//Add columns selected in the Selection list
						listColSelected.add(tabColSelected[i]);

						//Drop the current item of the columns list
						for(int j = 0; j< listColumns.getItemCount(); j++){
							if(listColumns.getItem(j).equals(tabColSelected[i])){
								listColumns.remove(j);
							}
						}


					}
				}

			}
		});



		//---------- BUTTON DROP COLUMNS	

		btnRemove.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(listColSelected.getSelection().length == 0){
					setErrorMessage("Click on column's Name to remove it");
				}
				else{

					String[] tabColToRemove = listColSelected.getSelection();

					for(int i = 0; i < tabColToRemove.length; i++){

						//ADD columns selected in the Columns list
						listColumns.add(tabColToRemove[i]);

						//Drop the current item of the Selected list
						for(int j = 0; j< listColSelected.getItemCount(); j++){


							if(listColSelected.getItem(j).equals(tabColToRemove[i])){
								listColSelected.remove(j);
							}
						}


					}
				}

			}
		});


		//----------- BTN ADD ALL COLUMNS

		btnAddAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {

				listColSelected.removeAll();
				listColumns.removeAll();

				for(PropertieColumn col : ((Connection)conn).getListeProperties()){	


					listColSelected.add(col.getPropertieColName());
				}

			}
		});		


		//----------- BTN DEL ALL COLUMNS

		btnRemoveAll.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {


				listColSelected.removeAll();
				listColumns.removeAll();

				for(PropertieColumn col : ((Connection)conn).getListeProperties()){	


					listColumns.add(col.getPropertieColName());
				}

			}
		});




	}



	@Override
	public DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {
		
		dataSetDesign.setQueryText(getQueryText(dataSetDesign));

		dataSetDesign.setResultSets(null);

		return dataSetDesign;
	}



	private String getQueryText(DataSetDesign dataSetDesign  ) {

		//Query from Datasource to get a dataset > **Columns**
		queryText = "";

		if(listColSelected != null){

			for(int i = 0; i < listColSelected.getItemCount(); i++){

				queryText += listColSelected.getItem(i) + Query.QUERY_SEPARATOR_COL_SELECTED;
			}

		}
		else{
			initTabColSelected();
			for(int i = 0; i < tabColSelected.length; i++){

				queryText += tabColSelected[i] + Query.QUERY_SEPARATOR_COL_SELECTED;
			}
		}


		queryText += Query.QUERY_SEPARATOR_ELEMENT;


		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$
			queryText += Query.NO_FILTER;
		}

		else{
			String queryDataSet = dataSetDesign.getQueryText();

			String[] tabQueryColAndFilter = queryDataSet.split(Query.QUERY_SEPARATOR_ELEMENT);
			String strQueryCFilter = tabQueryColAndFilter[1];

			queryText += strQueryCFilter;
		}

		queryText += Query.QUERY_SEPARATOR_ELEMENT;

		return queryText;
	}


	private void initTabColSelected(){
		DataSetDesign dataSetDesign = getInitializationDesign();

		if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$

		}

		else{

			String queryDataSet = dataSetDesign.getQueryText();

			//++++++ Split  columns selected and filters for two new string : Columns and filters
			String[] tabQueryColAndFilter = queryDataSet.split(Query.QUERY_SEPARATOR_ELEMENT);

			String strQueryCol = tabQueryColAndFilter[0];


			//++++++ Split columns list	
			String[] tabCols = strQueryCol.split(Query.QUERY_SEPARATOR_COL_SELECTED);

			tabColSelected = tabCols;
		}
	}

	public List getListColSelected() {
		return listColSelected;
	}


	public void setListColSelected(List listColSelected) {
		this.listColSelected = listColSelected;
	}


	@Override
	public boolean canFlipToNextPage() {
		return false;
	}


	

	



}
