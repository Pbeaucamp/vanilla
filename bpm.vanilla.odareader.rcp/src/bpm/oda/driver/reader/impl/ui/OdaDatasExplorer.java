package bpm.oda.driver.reader.impl.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.nebula.widgets.pshelf.RedmondShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.actions.ActionExport;
import bpm.oda.driver.reader.icons.IconsName;
import bpm.oda.driver.reader.model.ILabelable;
import bpm.oda.driver.reader.model.ReaderModel;
import bpm.oda.driver.reader.model.Snapshot;
import bpm.oda.driver.reader.model.dataset.ColumnDescriptor;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.dataset.ParameterDescriptor;
import bpm.oda.driver.reader.model.dataset.QueryHelper;
import bpm.oda.driver.reader.model.datasource.DataSource;
import bpm.oda.driver.reader.viewer.ExplorerTreeContentProvider;
import bpm.oda.driver.reader.viewer.ExplorerTreeLabelProvider;
import bpm.oda.driver.reader.wizards.EditionDataSetWizard;
import bpm.oda.driver.reader.wizards.MultiPageWizardDialog;
import bpm.oda.driver.reader.wizards.OdaDataSetWizard;
import bpm.oda.driver.reader.wizards.OdaDataSourceWizard;
import bpm.oda.driver.reader.wizards.dialog.DialogParameterValues;
import bpm.oda.driver.reader.wizards.dialog.DialogSave;
import bpm.oda.driver.reader.wizards.gateway.GatewayWizard;

public class OdaDatasExplorer extends ViewPart {
	private Shell currentShell;
	private Composite parent;
	
	private TreeViewer viewer;
	private TreeSelection treeSelection;
	private TabFolder tabFolder;
	private ToolItem itemAddSource, itemAddSet, itemSave, itemEdit, itemDelete, itemRefresh, itemSnapshot, itemgateway;
	private ToolBar bar;
	
	private int countRow;
	private boolean canRename;
	private IQuery query;

	public void createPartControl(Composite pParent) {
		
		this.parent = pParent;
		parent.setLayout(new GridLayout(4, true));
		currentShell = parent.getShell();
		canRename = false;
		
	//++++ UI part : ToolBar
		bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,4,1));
		
		ToolItem toolItem = null;
		
	    itemAddSource = buildToolItem(bar,"Add a new Data Source",IconsName.ICO_DATASOURCE_ADD);
	    itemAddSet = buildToolItem(bar,"Add a new Data Set",IconsName.ICO_DATASET_ADD);
	    	toolItem = new ToolItem(bar, SWT.SEPARATOR);
	    	toolItem.setWidth(12);
	    itemSave = buildToolItem(bar,"Save current results",IconsName.ICO_SAVE);
	    itemSnapshot = buildToolItem(bar,"Create a Snapshot",IconsName.ICO_SNAPSHOT_ADD);
	    	toolItem = new ToolItem(bar, SWT.SEPARATOR);
	    	toolItem.setWidth(12);
	    itemEdit = buildToolItem(bar,"Edit the current selection",IconsName.ICO_EDIT);
	    itemDelete = buildToolItem(bar,"Remove the current Selection",IconsName.ICO_DELETE);
	    itemRefresh = buildToolItem(bar,"Refresh the selected DataSet",IconsName.ICO_REFRESH);
	    
	    itemgateway = buildToolItem(bar,"Generate Gateway",IconsName.ICO_GATEWAY);
	    for(ToolItem item : bar.getItems()){
			item.addSelectionListener(new ToolListeners());
		}
		
	//++++ UI part : Data Explorer
		
		Composite compoData = new Composite(parent, SWT.NONE);
		compoData.setLayout(new GridLayout(2, true));
		compoData.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,1,5));
		
		PShelf shelf = new PShelf(compoData, SWT.SMOOTH | SWT.BORDER);
		shelf.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,2,1));
		shelf.setRenderer(new RedmondShelfRenderer());
		
		PShelfItem item = new PShelfItem(shelf, SWT.NONE);
		item.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_LIST));
		item.setText("Datas Explorer");
		
		item.getBody().setLayout(new GridLayout(1, false));
		
		//Build tree Viewer
		createTreeViewer(item.getBody());
		
	//++++ UI part : Table for Results preview
		
		tabFolder = new TabFolder(parent,SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,3,1));
	
		//display Snapshots if needed
		if(!Activator.getInstance().getListSnapshots().isEmpty()){
			displaySnapshots();
		}
	}


/*
 * Tree different menus : data source - data set - and main menu.
 */
	
	private Menu getDataSourceMenu( ) {
		Menu menu = new Menu(viewer.getTree());

		MenuItem item = buildMenuItem(menu, "Edit", IconsName.ICO_EDIT, INDEX_EDIT);
		item = buildMenuItem(menu, "Rename", IconsName.ICO_RENAME, INDEX_RENAME);
		item = new MenuItem(menu, SWT.SEPARATOR);
		item = buildMenuItem(menu, "Delete", IconsName.ICO_DELETE, INDEX_DELETE);
		return menu;
	}
	
	private Menu getDataSetMenu() {
		Menu menu = new Menu(viewer.getTree());
		
		MenuItem item = buildMenuItem(menu, "Edit", IconsName.ICO_EDIT, INDEX_EDIT);
		item = buildMenuItem(menu, "Rename", IconsName.ICO_RENAME, INDEX_RENAME);
		item = new MenuItem(menu, SWT.SEPARATOR);
		item = buildMenuItem(menu, "Save", IconsName.ICO_SAVE, INDEX_SAVE);
		item = buildMenuItem(menu, "Snapshot", IconsName.ICO_SNAPSHOT, INDEX_SNAPSHOT);
		item = new MenuItem(menu, SWT.SEPARATOR);
		item = buildMenuItem(menu, "Delete", IconsName.ICO_DELETE,INDEX_DELETE);
		item = new MenuItem(menu, SWT.SEPARATOR);
		item = buildMenuItem(menu, "Refresh", IconsName.ICO_REFRESH, INDEX_REFRESH);
		item = new MenuItem(menu, SWT.SEPARATOR);
		item = buildMenuItem(menu, "Generate Gateway", IconsName.ICO_GATEWAY, INDEX_GATEWAY);

		
		return menu;
	}
	
	private Menu getMainMenu(Composite compoData){
		Menu menu = new Menu(compoData);
		MenuItem item = buildMenuItem(menu, "New Data Source", IconsName.ICO_DATASOURCE_ADD, INDEX_NEW_DATASOURCE);
		item = buildMenuItem(menu, "New Data Set", IconsName.ICO_DATASET_ADD, INDEX_NEW_DATASET);
		
	return menu;
	}
	
/*
 * Methods to build quickly tool items and menu items
 */
	
	private ToolItem buildToolItem(ToolBar bar, String toolText, String iconsName){
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setImage(Activator.getDefault().getImageRegistry().get(iconsName));
		item.setToolTipText(toolText);
		return item;
	}
	
	private MenuItem buildMenuItem(Menu menu, String text, String iconsName, final int indexAction){
		
		MenuItem item = new MenuItem (menu, SWT.PUSH);
		item.setText(text);
		item.setImage(Activator.getDefault().getImageRegistry().get(iconsName));
		
		item.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				
				switch (indexAction) {
				case INDEX_NEW_DATASOURCE:
					actionNewDataSource();
					break;
					
				case INDEX_NEW_DATASET:
					actionNewDataSet();
					break;
					
				case INDEX_DELETE:
					actionDelete();
					break;
					
				case INDEX_EDIT:
					actionEdit();
					break;
					
				case INDEX_REFRESH:
					actionRefresh();
					break;
					
				case INDEX_RENAME:
					actionRename();
					break;
					
				case INDEX_SAVE:
					actionSave();
					break;
					
				case INDEX_SNAPSHOT:
					actionSnapshot();
					break;
				case INDEX_GATEWAY:
					GatewayWizard wizard = new  GatewayWizard((DataSet) getSelectedObject());
					WizardDialog d = new WizardDialog(getSite().getShell(), wizard);
					d.setMinimumPageSize(800, 600);
					d.open();
					break;
				default:
					break;
				}
			}
		});
		return item;
	}
	
	
/*
 * Method to build the tree viewer to explore datas, and table viewer for results.
 */
	
	private void createTreeViewer(Composite parent){
		
	//Instance
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,1,1));
		
	//Content Provider
		viewer.setContentProvider(new ExplorerTreeContentProvider(viewer));

	//Label provider
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		viewer.setLabelProvider(new DecoratingLabelProvider(new ExplorerTreeLabelProvider(), decorator));
	
	//Edition for Rename
		CellEditor editors[] = new CellEditor[1];
		editors[0] = new TextCellEditor(viewer.getTree(), SWT.NONE){
			
		};
		
		viewer.setColumnProperties(new String[] {"NAME"});
		viewer.setCellEditors(editors);
		viewer.setCellModifier(new ICellModifier() {

			public boolean canModify(Object element, String property) {
				
				if((element instanceof DataSource) || (element instanceof DataSet)){
					return canRename;
				}
				
				else{
					return false;
				}
				
			}

			public Object getValue(Object element, String property) {
				
				if(element instanceof ILabelable){
					return ((ILabelable)element).getName();
				}
				return null;
			}

			public void modify(Object element, String property, Object value) {
				
				Object obj = getSelectedObject() ;
				if(property.equals("NAME")){
					
					if(obj instanceof DataSource){
						((DataSource)obj).setName(value.toString());
						canRename = false;
					}
					
					else if(obj instanceof DataSet){
						((DataSet)obj).setName(value.toString());
						canRename = false;
					}
					else{
					}
					viewer.refresh();
				}
			}
		});
		
	//Input
		viewer.setInput(Activator.getInstance());
		
	//Menus
		final Menu menuDataSource = getDataSourceMenu();
		final Menu menuDataSet = getDataSetMenu();
		final Menu mainMenu = getMainMenu(viewer.getTree());
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				treeSelection = (TreeSelection)event.getSelection();
				
				Object obj = getSelectedObject();
				
				if(obj != null){
					if(obj instanceof DataSource){
						viewer.getTree().setMenu(menuDataSource);
					}
					
					else if(obj instanceof DataSet){
						viewer.getTree().setMenu(menuDataSet);
					}
					
					else {
						viewer.getTree().setMenu(mainMenu);
					}
				}
			}
		});
		
		viewer.expandAll();
		viewer.refresh();
	}
	
	private void createResultsViewer(DataSet dataSet){
		 
		 //Get concerned Tabfolder
		 PreviewResultComposite compo = null;
		 
		 for(TabItem item : tabFolder.getItems()){
			 if(((PreviewResultComposite)item.getControl()).getDataSet() == dataSet){
				 compo = ((PreviewResultComposite)item.getControl());
				 break;
			 }
		 }
		 
		 TableViewer viewerRes = compo.getViewerResults();
		 Label labelRec = compo.getLabelRecords();
		 
		//Remove the old label and the old table.
		compo.getChildren()[3].dispose();
		compo.getChildren()[2].dispose();
		
		 viewerRes = new TableViewer(compo, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		 viewerRes.setContentProvider(new IStructuredContentProvider(){

			 public Object[] getElements(Object inputElement) {
				 IResultSet rs = (IResultSet)inputElement;
				 try{
					 IResultSetMetaData mtd = rs.getMetaData();
					 
					 List<List<Object>> datas = new ArrayList<List<Object>>();
					 
					 countRow = 0;
					 
					 while(rs.next()){
						 countRow++;
						 List<Object> row = new ArrayList<Object>();

						 for(int i = 1; i <= mtd.getColumnCount(); i++){
							 row.add(rs.getString(i));
						 }
						 datas.add(row);
					 }
					 
					 rs.close();
					 query.close();
					 
					 return datas.toArray(new Object[datas.size()]);
					 
				 }catch(Exception e){
					 e.printStackTrace();
				 }
				 return null;
			 }

			 public void dispose() {
			 }

			 public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			 }
		 });

		 viewerRes.setLabelProvider(new ITableLabelProvider(){

			 public Image getColumnImage(Object element, int columnIndex) {
				 return null;
			 }

			 public String getColumnText(Object element, int columnIndex) {
				 
				 return ((List)element).get(columnIndex) + "";
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
		 });
		 
		 Table table = viewerRes.getTable();
		 
		 table.setHeaderVisible(true);
		 table.setLinesVisible(true);
		 table.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,2,1));

		 //Build columns
		 for(ColumnDescriptor col : dataSet.getDescriptor().getColumnsDescriptors()){
			 TableColumn c = new TableColumn(table, SWT.NONE);
			 c.setText(col.getColumnLabel());
			 c.setWidth(150);
		 }

		 if (!dataSet.getDescriptor().getParametersDescriptors().isEmpty()){

			 DialogParameterValues d = new DialogParameterValues(currentShell, dataSet.getDescriptor().getParametersDescriptors());
			
			 if (d.open() == DialogParameterValues.OK){
				 try{
					 
					DataSource source = Activator.getInstance().getDataSource(dataSet);
					IQuery query = QueryHelper.buildquery(source, dataSet);
					dataSet.setResultSet(executeQuery(dataSet, d.getValues()));
					viewerRes.setInput(dataSet.getResultSet());
					 
				 }catch(Exception e){
					 e.printStackTrace();
				 }
			 }
		 }
		 else{
			 try{

				 dataSet.setResultSet(executeQuery(dataSet, null));
				 dataSet.setResultSetUpdated(false);

				 viewerRes.setInput(dataSet.getResultSet());

			 }catch(Exception e){
				 e.printStackTrace();
			 }
		 }

		 //Update the results count
		 labelRec = new Label(compo, SWT.NONE);
		 labelRec.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,2,1));
		 labelRec.setText("  Total: " + countRow+ " record(s) shown.");
		 
//		 viewerRes.refresh();
		 
		compo.layout();
		tabFolder.layout();
		parent.layout();
	 }

	private IResultSet executeQuery(DataSet ds, HashMap<ParameterDescriptor, String> pValues) throws Exception{
			
			IConnection c = ds.getConnectionDataSet();
			Properties prop = Activator.getInstance().getDataSource(ds).getProperties();
			c.close();
			c.open(prop);
			
			query = c.newQuery("");
			query.setMaxRows(query.getMaxRows());
			query.prepare(ds.getQueryText());
			
			if (pValues != null){
				for(ParameterDescriptor p : pValues.keySet()){
					query.setString(p.getPosition(), pValues.get(p));
				}
			}
			
			return query.executeQuery();
	 }

/*
 * List of actions, for the tools bar, and menus.
 */
	
	private void actionNewDataSource(){
		 try {
				OdaDataSourceWizard wizardDSource = new OdaDataSourceWizard(null);
				WizardDialog refWizardDialog = new WizardDialog(currentShell, wizardDSource);
				refWizardDialog.setMinimumPageSize(800, 600);
				
				refWizardDialog.open();
				viewer.expandAll();
				viewer.refresh();
				
			} catch (OdaException e1) {
				e1.printStackTrace();
			}
	 }
	 
	private void actionNewDataSet(){
		 
		 try {

			 int countOldSize = Activator.getInstance().getListDataSet().size();

			 OdaDataSetWizard wizardDSet = new OdaDataSetWizard(null);
			 WizardDialog refWizardDialog = new WizardDialog(currentShell, wizardDSet);
			 refWizardDialog.setMinimumPageSize(800, 600);
			 refWizardDialog.open();

			 int countNewSize = Activator.getInstance().getListDataSet().size();


			 //Add the new data set if user created it.
			 if(countNewSize != countOldSize){
				
				 //Refresh the tree viewer
				 viewer.expandAll();
				 viewer.refresh();

				 //Get created dataset
				 DataSet createdDs = Activator.getInstance().getListDataSet().get(countOldSize);

				 //Add the new tabitem
				 final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
				 tabItem.setText(createdDs.getName());
				 tabItem.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DATASET));
				 tabItem.setControl(new PreviewResultComposite(tabFolder,SWT.NONE, createdDs));

				((PreviewResultComposite) tabItem.getControl()).getBtnRefresh().addSelectionListener(new SelectionListener(){

					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						
						 DataSet ds = ((PreviewResultComposite) tabItem.getControl()).getDataSet();
						 if(ds != null){
							 if(ds.getDescriptor() != null){
								 createResultsViewer(ds);
							 }
						 }
					}
				});
				 
				 parent.layout();
			 }

		 } catch (OdaException e1) {
			 e1.printStackTrace();
		 }
	 }
	
	private void actionEdit(){
		 
	//Edit a data set
		if(getSelectedObject() instanceof DataSet){
			DataSet ds = (DataSet) getSelectedObject();
			try {
				OdaDataSetWizard wiz = new EditionDataSetWizard(ds);
				WizardDialog d = new MultiPageWizardDialog(currentShell, wiz);
				d.setMinimumPageSize(800, 600);
				if (d.open() == WizardDialog.OK){
				}
			} catch (OdaException e) {
				e.printStackTrace();
			}
		}

	//Edit a data Source
		else if(getSelectedObject() instanceof DataSource){
			DataSource selectedDs = null;
			selectedDs = (DataSource)getSelectedObject();

			try {
				OdaDataSourceWizard wizardDSource = new OdaDataSourceWizard(selectedDs);
				WizardDialog refWizardDialog = new WizardDialog(currentShell, wizardDSource);
				refWizardDialog.getShell().setSize(800, 600);
				refWizardDialog.open();
				viewer.refresh();

			} catch (OdaException e1) {
				e1.printStackTrace();
			}
		}
		
		else{
			MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, MSG_DIALOG_ERROR_SELECT);
		}
	 }

	private void actionRefresh(){
		 
		 Object obj = getSelectedObject();
		 
		 if(obj != null){
			
			 if(obj instanceof DataSet){

				 DataSet ds = (DataSet)obj;
				 
				 
				 if(ds != null){
					 if(ds.getDescriptor() != null){
						 createResultsViewer(ds);
					 }
				 }
			 }
			 
			 else{
				 MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, "Please, select a data set to refresh it.");
			 }
		 }
	 }
	
	
	private void setDataSetResultSet(DataSet dataSet){
		try{
			if (!dataSet.getDescriptor().getParametersDescriptors().isEmpty()){

				 DialogParameterValues d = new DialogParameterValues(currentShell, dataSet.getDescriptor().getParametersDescriptors());
				
				 if (d.open() == DialogParameterValues.OK){
					DataSource source = Activator.getInstance().getDataSource(dataSet);
					IQuery query = QueryHelper.buildquery(source, dataSet);
					dataSet.setResultSet(executeQuery(dataSet, d.getValues()));
				 }
			 }
			 else{
				 dataSet.setResultSet(executeQuery(dataSet, null));
				 dataSet.setResultSetUpdated(false);
			 }
		}catch(Exception e){
			 e.printStackTrace();
			 MessageDialog.openError(currentShell, "Executin query", "Unable to perform Query : " + e.getMessage());
			 return;
		 }
	}
	
	
	private void actionSave(){
		 
		Object obj = getSelectedObject();
		
			if(obj instanceof DataSet){
				setDataSetResultSet((DataSet)obj);
					 
				DialogSave dSave = new DialogSave(currentShell);
				dSave.open();
				String pathSelected = dSave.getPathSaved();

				boolean buildingOk = false;
				File file = new File(pathSelected);

				//Build a XML FILE
				if(pathSelected.endsWith(DialogSave.TAB_EXTENSION[DialogSave.INDEX_XML])){
					buildingOk = ActionExport.buildXmlFile(((DataSet)obj).getResultSet(), file);
				}

				//Build a CSV FILE
				if(pathSelected.endsWith(DialogSave.TAB_EXTENSION[DialogSave.INDEX_CSV])){
					buildingOk = ActionExport.buildCSVFile(((DataSet)obj).getResultSet(), file);
				}

				//Build a XLS FILE
				if(pathSelected.endsWith(DialogSave.TAB_EXTENSION[DialogSave.INDEX_EXCEL])){
					buildingOk = ActionExport.buildXlsFile(((DataSet)obj).getResultSet(), file);
				}

				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if(!buildingOk){
					MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, "Error on saving.");
				}
				
			}
			
			else{
				MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, "Please, select a data set to save it.");
			}
	}
	
	private void actionDelete(){
		
		if(getSelectedObject() instanceof DataSource){
			Activator.getInstance().getListDataSource().remove((DataSource)getSelectedObject());
			viewer.refresh();
		}
		
		else if(getSelectedObject() instanceof DataSet){
			
			DataSet dsToRemove = (DataSet)getSelectedObject();
			int indexItem = 0;
			
			for(TabItem item : tabFolder.getItems()){
				
				if(((PreviewResultComposite)item.getControl()).getDataSet() == dsToRemove){
					indexItem = tabFolder.indexOf(item);
				}
			}
			
			tabFolder.getItem(indexItem).dispose();
			tabFolder.layout();
			
			Activator.getInstance().getListDataSet().remove(dsToRemove);
			viewer.refresh();
		}
		
		else if(getSelectedObject() instanceof Snapshot){
			
			Snapshot snapToRemove = (Snapshot)getSelectedObject();
			
			boolean deletingOk = Activator.getInstance().removeSnapshot(snapToRemove);
			
			if(deletingOk){
				int indexItem = 0;
				for(TabItem item : tabFolder.getItems()){
					
					if(((PreviewResultComposite)item.getControl()).getSnapShot() == snapToRemove){
						indexItem = tabFolder.indexOf(item);
					}
				}
				
				tabFolder.getItem(indexItem).dispose();
				tabFolder.layout();
				viewer.refresh();
			}
			
			else{
				MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, "Unable to delete this snapshot");
			}
		}
		
		else{
			MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, MSG_DIALOG_ERROR_SELECT);
		}
	}
	
	private void actionRename(){
		
		if(getSelectedObject() instanceof DataSource){
			((DataSource)getSelectedObject()).setName("...");
			canRename = true;
		}
		
		else if(getSelectedObject() instanceof DataSet){
			((DataSet)getSelectedObject()).setName("...");
			canRename = true;
		}
		
		else{
			MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, MSG_DIALOG_ERROR_SELECT);
		}
		
		viewer.refresh();
	}
	
	private void actionSnapshot(){
		
		Object obj = getSelectedObject();
		if(obj instanceof DataSet){
			setDataSetResultSet((DataSet)obj);
			
				
			//Build the target folder if it doesn't exist
			 boolean buildingSnapshotOk = false;
			 String path = Platform.getInstanceLocation().getURL().getPath();
			 File nodeFile = new File(path, "temp");
			 nodeFile.mkdir();
			 
			 try {
				 String nameFileValues = Activator.getInstance().getSnapshotName(((DataSet)obj).getName());
				 
				//File values for disconnected mode
				 File fileValues = new File(nodeFile, nameFileValues + ".txt");
				 
				 //XML file for data set and data source definition
				 File fileXML = new File(nodeFile, nameFileValues + ReaderModel.DEFINITION_FILE + ".xml");
				 
				 buildingSnapshotOk = ActionExport.buildSnapshotFile(((DataSet)obj), fileValues, fileXML);

				 if(!buildingSnapshotOk){
					 MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, "Error on saving.");
				 }
				 else{
					 MessageDialog.openInformation(currentShell, MSG_DIALOG_TITLE, "Snapshot created.");

					 fileValues.createNewFile();
					 fileXML.createNewFile();
					 
					 //Add the created snapshot into the arraylist.
					 Snapshot snapCreated =  new Snapshot(nameFileValues,((DataSet)obj).getName());
					 Activator.getInstance().getListSnapshots().add(snapCreated);
					 snapCreated.fillValues();
					 //Add the new tab
					 final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
					 tabItem.setText(snapCreated.getName());
					 tabItem.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_SNAPSHOT));
					 tabItem.setControl(new PreviewResultComposite(tabFolder,SWT.NONE, snapCreated));
					 
					 tabFolder.layout();
					 parent.layout();
					 
					 viewer.expandAll();
					 viewer.refresh();
				 }

			 } catch (IOException e) {
				 e.printStackTrace();
			 }
			
		}
		
		else{
			MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, "Please, select a data set to save it.");
		}
	}
	
/*
 * Listeners for tool items
 */
	class ToolListeners implements SelectionListener{

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			
			if(e.getSource() == itemAddSet){
				actionNewDataSet();
			}

			else if(e.getSource() == itemAddSource){
				actionNewDataSource();
			}

			else if(e.getSource() == itemDelete){
				actionDelete();
			}

			else if(e.getSource() == itemEdit){
				actionEdit();
			}

			else if(e.getSource() == itemRefresh){
				actionRefresh();
			}

			else if(e.getSource() == itemSave){
				actionSave();
			}
			
			else if(e.getSource() == itemSnapshot){
				actionSnapshot();
			}
			else if (e.getSource() == itemgateway){
				GatewayWizard wizard = new  GatewayWizard((DataSet) getSelectedObject());
				WizardDialog d = new WizardDialog(getSite().getShell(), wizard);
				d.setMinimumPageSize(800, 600);
				d.open();
			}
		}
	}
	
/*
 *  Method to test the selected object in the tree Viewer
 */
	private Object getSelectedObject(){
		
		if(treeSelection != null){
			TreePath[] paths = treeSelection.getPaths();
			
			Object currentObj = null;
			for (TreePath treePath : paths) {
				currentObj = treePath.getLastSegment();
			}
			
			return currentObj;
		}
		
		else{
			return null;
		}
	}
	
/*
 * Method to display Snapshots: For each snapshots:
 *  1 - build a tab item with the snapshot content (mode disconnected
 *  2 - if the user click on refresh button:
 *  		a. Try to find the correspondent XML File which content Data source and data set description
 *  		b. Try to build Data set and data source
 *  		c. Add a new tab item with normal preview results
 */
	
	private void displaySnapshots() {
		
		for(final Snapshot currentSnap : Activator.getInstance().getListSnapshots()){

			//Add the new tabitem
			final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
			tabItem.setText(currentSnap.getName());
			tabItem.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_SNAPSHOT));
			tabItem.setControl(new PreviewResultComposite(tabFolder,SWT.NONE, currentSnap));

			((PreviewResultComposite) tabItem.getControl()).getBtnRefresh().addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {

					boolean rebuildingOk = Activator.getInstance().rebuildDefinition(currentSnap);

					if(rebuildingOk){
						((PreviewResultComposite) tabItem.getControl()).getBtnRefresh().setEnabled(false);
						viewer.expandAll();
						viewer.refresh();
						

						//Get created dataset
						DataSet createdDs = Activator.getInstance().getListDataSet().get(Activator.getInstance().getListDataSet().size()-1);

						//Add the new tabitem
						final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
						tabItem.setText(createdDs.getName());
						tabItem.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_DATASET));
						tabItem.setControl(new PreviewResultComposite(tabFolder,SWT.NONE, createdDs));

						((PreviewResultComposite) tabItem.getControl()).getBtnRefresh().addSelectionListener(new SelectionListener(){

							public void widgetDefaultSelected(SelectionEvent e) {
							}

							public void widgetSelected(SelectionEvent e) {

								DataSet ds = ((PreviewResultComposite) tabItem.getControl()).getDataSet();
								if(ds != null){
									if(ds.getDescriptor() != null){
										createResultsViewer(ds);
									}
								}
							}
						});

						parent.layout();
					}

					else{
						MessageDialog.openError(currentShell, MSG_DIALOG_TITLE, "Enable to rebuild the data set and/or the datasource.");
					}

				}
			});
		}

	}
	
	 @Override
	 public void setFocus() {

	 }
	 
/*
 * Constants
 */
	 public static final String ID = "bpm.oda.driver.reader.OdaDatasExplorer";

	 public static final String NODE_DATASOURCES = "DataSources";
	 public static final String NODE_DATASETS= "DataSets";
	 public static final String NODE_SNAPSHOTS= "Snapshots";
	 private static final String MSG_DIALOG_TITLE = "Vanilla Reader Informations";
	 private static final String MSG_DIALOG_ERROR_SELECT = "Before performing this action, selet a data.";

	 private static final int INDEX_NEW_DATASET = 1, INDEX_NEW_DATASOURCE = 2, INDEX_EDIT = 3,
	 INDEX_REFRESH = 4, INDEX_SAVE = 5, INDEX_DELETE = 6,INDEX_RENAME = 7, INDEX_SNAPSHOT = 8, INDEX_GATEWAY=9;
}