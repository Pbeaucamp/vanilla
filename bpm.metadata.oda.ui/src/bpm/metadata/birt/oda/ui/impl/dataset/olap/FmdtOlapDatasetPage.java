package bpm.metadata.birt.oda.ui.impl.dataset.olap;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ParameterDefinition;
import org.eclipse.datatools.connectivity.oda.design.ParameterMode;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.metadata.birt.oda.runtime.impl.ConnectionManager;
import bpm.metadata.birt.oda.runtime.impl.UnitedOlapConnection;
import bpm.metadata.birt.oda.ui.Activator;
import bpm.metadata.birt.oda.ui.trees.TreeBusinessTable;
import bpm.metadata.birt.oda.ui.trees.TreeContentProvider;
import bpm.metadata.birt.oda.ui.trees.TreeDataStreamElement;
import bpm.metadata.birt.oda.ui.trees.TreeFilter;
import bpm.metadata.birt.oda.ui.trees.TreeLabelProvider;
import bpm.metadata.birt.oda.ui.trees.TreeObject;
import bpm.metadata.birt.oda.ui.trees.TreeParent;
import bpm.metadata.birt.oda.ui.trees.TreePrompt;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;

public class FmdtOlapDatasetPage extends DataSetWizardPage {

	private TreeViewer treeColumns;
	private ListViewer listColumns;
	
	private TableViewer promptParameters;
	private TableViewer selectedFilters;
	
	private List<Prompt> prompts = new ArrayList<Prompt>();
	private List<IFilter> filters = new ArrayList<IFilter>();
	
	private Button cbHideNull;
	
	public FmdtOlapDatasetPage(String pageName) {
		super(pageName);
	}
	
	private boolean listContains(IDataStreamElement e){
		for(IDataStreamElement el : (List<IDataStreamElement>)listColumns.getInput()){
			if (el == e){
				return true;
			}
		}
		return false;
	}

	@Override
	public void createPageCustomControl(Composite arg0) {
		
		Composite container = new Composite(arg0, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		
		Label l = new Label(container,  SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Activator.getResourceString("FmdtDataSetPage.SelectCols")); //$NON-NLS-1$
		
		
		Composite c = new Composite(container, SWT.NONE);
		c.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, true, 1, 2));
		c.setLayout(new GridLayout());
		
		
		Composite cc = new Composite(c, SWT.NONE);
		cc.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, true));
		cc.setLayout(new GridLayout());
		
		Button add = new Button(cc, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		add.setImage(Activator.getDefault().getImageRegistry().get("addCol")); //$NON-NLS-1$
		add.setToolTipText(Activator.getResourceString("FmdtDataSetPage.AddCols")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (treeColumns.getSelection().isEmpty()){
					return;
				}
				
				for(Object o : ((IStructuredSelection)treeColumns.getSelection()).toList()){
					if (o instanceof TreeBusinessTable){
						for(TreeObject t : ((TreeBusinessTable)o).getChildren()){
							IDataStreamElement el = ((TreeDataStreamElement)t).getDataStreamElement();
							
							if (!listContains(el)){
								((List<IDataStreamElement>)listColumns.getInput()).add(el);
							}
						}
					}
					else if (o instanceof TreeDataStreamElement){
						if (!listContains(((TreeDataStreamElement)o).getDataStreamElement())){
							((List<IDataStreamElement>)listColumns.getInput()).add(((TreeDataStreamElement)o).getDataStreamElement());
						}
					}
					else if (o instanceof TreePrompt){
						if (! prompts.contains(((TreePrompt)o).getPrompt())){
							prompts.add(((TreePrompt)o).getPrompt());
						}
							
						promptParameters.refresh();
					}
					else if (o instanceof TreeFilter){
						if (! filters.contains(((TreeFilter)o).getFilter())){
							filters.add(((TreeFilter)o).getFilter());
						}
							
						selectedFilters.refresh();
					}
					
				}
				listColumns.refresh();
			
				getContainer().updateButtons();
			}
			
		});
		
		
		Button remove = new Button(cc, SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		remove.setImage(Activator.getDefault().getImageRegistry().get("delCol")); //$NON-NLS-1$
		remove.setToolTipText(Activator.getResourceString("FmdtDataSetPage.RemoveCols")); //$NON-NLS-1$
		remove.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				IStructuredSelection ss = (IStructuredSelection)listColumns.getSelection();
				
				for(IDataStreamElement c : (List<IDataStreamElement>)ss.toList()){
					((List<IDataStreamElement>)listColumns.getInput()).remove(c);
				}
				
				listColumns.refresh();
				ss = (IStructuredSelection)promptParameters.getSelection();
				
				for(Prompt c : (List<Prompt>)ss.toList()){
					prompts.remove(c);
				}
				
				promptParameters.refresh();
				
				ss = (IStructuredSelection)selectedFilters.getSelection();
				
				for(IFilter c : (List<IFilter>)ss.toList()){
					filters.remove(c);
				}
				
				selectedFilters.refresh();
			
				getContainer().updateButtons();
			}
			
		});
		
		Label l2 = new Label(container,  SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Activator.getResourceString("FmdtDataSetPage.SelectedCols")); //$NON-NLS-1$
		
		
		treeColumns = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		treeColumns.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		treeColumns.setLabelProvider(new TreeLabelProvider());
		treeColumns.setContentProvider(new TreeContentProvider());
		treeColumns.setComparator(new ViewerComparator(){

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof TreeDataStreamElement && e2 instanceof TreeDataStreamElement){
					
					return ((TreeDataStreamElement)e1).getName().compareTo(((TreeDataStreamElement)e2).getName());
					
				}
				else if ((e1 instanceof TreeFilter || e1 instanceof TreePrompt) && (e2 instanceof TreeFilter || e2 instanceof TreePrompt)){
					
					Class c1 = null;
					Class c2 = null;
					IResource r1 = null;
					IResource r2 = null;
					if (e1 instanceof TreeFilter){
						c1 = ((TreeFilter)e1).getFilter().getClass();
						r1 = ((TreeFilter)e1).getFilter();
					}
					else{
						c1 = ((TreePrompt)e1).getPrompt().getClass();
						r1 = ((TreePrompt)e1).getPrompt();
					}
					
					if (e2 instanceof TreeFilter){
						c2 = ((TreeFilter)e2).getFilter().getClass();
						r2 = ((TreeFilter)e2).getFilter();
					}
					else{
						c2 = ((TreePrompt)e2).getPrompt().getClass();
						r2 = ((TreePrompt)e2).getPrompt();
					}
					if (c1 != null && c2 != null){
						if (c1 == c2){
							return r1.getName().compareTo(r2.getName());
						}
						return c1.getName().compareTo(c2.getName());
					}
				}
				
				return super.compare(viewer, e1, e2);
			}
			
		});
		
		
		
		listColumns = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		listColumns.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		listColumns.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IDataStreamElement)element).getName();
			}
			
		});
		listColumns.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<IDataStreamElement> l = (List<IDataStreamElement>)inputElement;
				return l.toArray(new IDataStreamElement[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		listColumns.setInput(new ArrayList<IDataStreamElement>());
		
		Label lPrompt = new Label(container, SWT.NONE);
		lPrompt.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		lPrompt.setText(Activator.getResourceString("FmdtDataSetPage.selectPrompt"));

		new Label(container, SWT.NONE).setLayoutData(new GridData());
		Label lFilters = new Label(container, SWT.NONE);
		lFilters.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		lFilters.setText("Selected Filters");

		
		promptParameters = new TableViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		promptParameters.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		promptParameters.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Prompt)element).getOutputName();
			}

			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get("prompt");
			}
			
			
			
		});
		promptParameters.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return prompts.toArray(new Prompt[prompts.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		promptParameters.setComparator(new ViewerComparator(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1.getClass() == e2.getClass()){
					return e1.getClass().getName().compareTo(e2.getClass().getName());
				}
				else{
					return ((IResource)e1).getName().compareTo(((IResource)e2).getName());
				}
				
			}
		});
		promptParameters.setInput(prompts);
		
		
		new Label(container, SWT.NONE).setLayoutData(new GridData());
		selectedFilters = new TableViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		selectedFilters.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		selectedFilters.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IFilter)element).getOutputName();
			}

			@Override
			public Image getImage(Object element) {

				if (element instanceof Filter){
					return Activator.getDefault().getImageRegistry().get("filter");
				}

				return Activator.getDefault().getImageRegistry().get("filter");
			}
			
			
			
		});
		selectedFilters.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				return filters.toArray(new IFilter[filters.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		selectedFilters.setInput(filters);
		selectedFilters.setComparator(new ViewerComparator(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1.getClass() == e2.getClass()){
					return e1.getClass().getName().compareTo(e2.getClass().getName());
				}
				else{
					return ((IResource)e1).getName().compareTo(((IResource)e2).getName());
				}
				
			}
		});
		
		cbHideNull = new Button(container, SWT.CHECK);
		cbHideNull.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		cbHideNull.setText("Hide null values");
		
		setControl(container);
		
		initialiseControl();
	}
	
	private void populateTree(IConnection conn){
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
			
		for(IBusinessTable t : ((UnitedOlapConnection)conn).getFmdtPackage().getOrderedTables(((UnitedOlapConnection)conn).getGroupName())){
			root.addChild(new TreeBusinessTable(t, ((UnitedOlapConnection)conn).getGroupName())); //$NON-NLS-1$
			
		}
		
		
		for(IResource r: ((UnitedOlapConnection)conn).getFmdtPackage().getResources(((UnitedOlapConnection)conn).getGroupName())){
			if (r instanceof Prompt){
				root.addChild(new TreePrompt((Prompt)r)); //$NON-NLS-1$
			}
			if (r instanceof IFilter){
				root.addChild(new TreeFilter((IFilter)r)); //$NON-NLS-1$
			}
		}
		treeColumns.setInput(root);

	}
	
	private UnitedOlapQuery getQuery(DataSetDesign dataSetDesign) {
	   	Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
    	UnitedOlapQuery query = null;
    	
    	if (listColumns != null){
    		query = new UnitedOlapQuery((List<IDataStreamElement>)listColumns.getInput(), filters, prompts, connProps.getProperty(UnitedOlapConnection.GROUP_NAME), cbHideNull.getSelection());
    	}else{
    		try{
	    		IConnection conn =  ConnectionManager.getConnection(connProps,getInitializationDesign().getDataSourceDesign().getOdaExtensionDataSourceId());
	    		query = new SqlQueryDigester(IOUtils.toInputStream(dataSetDesign.getQueryText(), "UTF-8"), ((UnitedOlapConnection)conn).getGroupName(), ((UnitedOlapConnection)conn).getFmdtPackage()).getUOlapModel();

    		}catch(Exception ex){
    			
    		}
    	}
    	
    		
    	return query;
	}
	
	 private static java.util.Properties getBlankPageProperties() {
	        java.util.Properties prop = new java.util.Properties();
	        prop.setProperty(UnitedOlapConnection.BUSINESS_MODEL, ""); //$NON-NLS-1$
	        prop.setProperty(UnitedOlapConnection.BUSINESS_PACKAGE, ""); //$NON-NLS-1$
	        prop.setProperty(UnitedOlapConnection.DIRECTORY_ITEM_ID, ""); //$NON-NLS-1$
	        prop.setProperty(UnitedOlapConnection.GROUP_NAME, ""); //$NON-NLS-1$
	        prop.setProperty(UnitedOlapConnection.PASSWORD, ""); //$NON-NLS-1$
	        prop.setProperty(UnitedOlapConnection.URL, ""); //$NON-NLS-1$
	        prop.setProperty(UnitedOlapConnection.USER, ""); //$NON-NLS-1$


	        return prop;
	 }
	
	 
	 
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {
        String queryText = getQuery(dataSetDesign).getXml();
        dataSetDesign.setQueryText(queryText);

        if (dataSetDesign.getPublicProperties() == null) {
            try {
                dataSetDesign.setPublicProperties(DesignSessionUtil.createDataSetPublicProperties(dataSetDesign.getOdaExtensionDataSourceId(), dataSetDesign.getOdaExtensionDataSetId(),getBlankPageProperties()));
            } catch (OdaException e) {
                e.printStackTrace();
            }
        }

        // obtain query's current runtime metadata, and maps it to the
        // dataSetDesign
        IConnection conn = null;
        try {
            // obtain and open a live connection
           
            java.util.Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
            
            conn =  ConnectionManager.getConnection(connProps,getInitializationDesign().getDataSourceDesign().getOdaExtensionDataSourceId());
            
            updateDesign(dataSetDesign, conn, queryText);
        } catch (OdaException e) {
            // not able to get current metadata, reset previous derived metadata
            dataSetDesign.setResultSets(null);
            dataSetDesign.setParameters(null);

            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
		
		return dataSetDesign;
	}
	
	 /**
     * Attempts to close given ODA connection.
     * 
     * @param conn the conn
     */
    private void closeConnection(IConnection conn) {
        try {
            if (conn != null && conn.isOpen())
                conn.close();
        } catch (OdaException e) {
            e.printStackTrace();
        }
    }
    
	private void updateDesign(DataSetDesign dataSetDesign, IConnection conn, String queryText) throws OdaException {
		IQuery query = conn.newQuery(null);
		query.prepare(queryText);
		
		try {
			IResultSetMetaData md = query.getMetaData();
			updateResultSetDesign(md, dataSetDesign);
			 /*
	         *parameters 
	         */
	        IParameterMetaData pMd = query.getParameterMetaData();
	        DataSetParameters dataSetParameter = DesignSessionUtil.toDataSetParametersDesign( pMd,ParameterMode.IN_LITERAL );
	        if ( dataSetParameter != null )
			{
				Iterator iter = dataSetParameter.getParameterDefinitions( ).iterator( );
				while ( iter.hasNext( ) )
				{
					ParameterDefinition defn = (ParameterDefinition) iter.next( );
					proccessParamDefn( defn, dataSetParameter );
				}
			}
			dataSetDesign.setParameters( dataSetParameter );

			
			
		} catch (OdaException e) {
			// no result set definition available, reset previous derived
			// metadata
			dataSetDesign.setResultSets(null);
			e.printStackTrace();
		}
	}
	
	 private void updateResultSetDesign(IResultSetMetaData md, DataSetDesign dataSetDesign) throws OdaException {
        ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign(md);

        ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE.createResultSetDefinition();
        resultSetDefn.setResultSetColumns(columns);

        // no exception in conversion; go ahead and assign to specified
        // dataSetDesign
        dataSetDesign.setPrimaryResultSet(resultSetDefn);
        dataSetDesign.getResultSets().setDerivedMetaData(true);
    }
	 
	private static void proccessParamDefn( ParameterDefinition defn,DataSetParameters parameters ) {
		if ( defn.getAttributes( ).getNativeDataTypeCode( ) == Types.NULL ) {
			defn.getAttributes( ).setNativeDataTypeCode( Types.CHAR );
		}
	}
	
	private void initialiseControl(){
		
		try {
			Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
			IConnection conn =  ConnectionManager.getConnection(connProps,getInitializationDesign().getDataSourceDesign().getOdaExtensionDataSourceId());
			populateTree(conn);
			
			// Restores the last saved data set design
	        DataSetDesign dataSetDesign = getInitializationDesign();
	        
	        if (dataSetDesign == null || dataSetDesign.getQueryText() == null || dataSetDesign.getQueryText().trim().equals("")){ //$NON-NLS-1$
	        	return;
	        }
	        
	        try{
	        	
	        	UnitedOlapQuery q = new SqlQueryDigester(IOUtils.toInputStream(dataSetDesign.getQueryText(), "UTF-8"), ((UnitedOlapConnection)conn).getGroupName(), ((UnitedOlapConnection)conn).getFmdtPackage()).getUOlapModel();
	        	for(IDataStreamElement e : q.getSelect()){
	        		((List<IDataStreamElement>)listColumns.getInput()).add(e);
	        	}
	        	prompts.clear();
	        	for(Prompt p : q.getPrompts()){
	        		prompts.add(p);
	        	}
	        	promptParameters.refresh();
	        	
	        	filters.clear();
	        	for(IFilter f : q.getFilters()){
	        		filters.add(f);
	        	}
	        	selectedFilters.refresh();
	        	
	        	listColumns.refresh();
	        }catch(Exception e){
	        	e.printStackTrace();
	        	MessageDialog.openError(getShell(), Activator.getResourceString("FmdtDataSetPage.Error"), e.getMessage()); //$NON-NLS-1$
	        }
	        
		} catch (OdaException e1) {
			MessageDialog.openError(getShell(), Activator.getResourceString("FmdtDataSetPage.Error"), e1.getMessage()); //$NON-NLS-1$ // FP IDE
		}
	}
	
	@Override
	public boolean isPageComplete() {
		return checkQuery();
	}

	private boolean checkQuery() {
		UnitedOlapQuery query = getQuery(getInitializationDesign());
		
		int nbLevel = 0;
		int nbMeasure = 0;
		
		for(IDataStreamElement elem : query.getSelect()) {
			if (elem.getType().getParentType() == IDataStreamElement.Type.MEASURE){
				nbMeasure++;
			}
			else{
				nbLevel++;
			}
			// XXX this piece of commented code wont work
			// the FMDT model has been lightweighted re-build, so 
			// no dataStreamElement origin's are available (except if you previously run a query)
			
//			if(elem.getOrigin() != null) {
//				if(elem.getOrigin().getName().startsWith("[Measures]")) {
//					nbMeasure++;
//				}
//				else {
//					nbLevel++;
//				}
//			}
//			else {
//				if(elem.getOriginName().startsWith("[Measures]")) {
//					nbMeasure++;
//				}
//				else {
//					nbLevel++;
//				}
//			}
		}
		
		return nbLevel > 0 && nbMeasure > 0;
	}
}
