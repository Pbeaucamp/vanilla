/*
 *************************************************************************
 * Copyright (c) 2009 <<Your Company Name here>>
 *  
 *************************************************************************
 */

package bpm.vanilla.listdata.oda.driver.ui.impl;

import java.util.Collection;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSetParameters;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ParameterDefinition;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import bpm.vanilla.listdata.oda.driver.impl.Connection;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.repository.services.IDataProviderService;
import bpm.vanilla.platform.core.utils.MD5Helper;

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
public class CustomDataSetWizardPage extends DataSetWizardPage
{

    private static String DEFAULT_MESSAGE = "Define the query text for the data set";
    
    private ComboViewer datasProviders;
    private IDataProviderService dataProviderAdmin;
    

	/**
     * Constructor
	 * @param pageName
	 */
	public CustomDataSetWizardPage( String pageName )
	{
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
	public CustomDataSetWizardPage( String pageName, String title,
			ImageDescriptor titleImage )
	{
        super( pageName, title, titleImage );
        setMessage( DEFAULT_MESSAGE );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#createPageCustomControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPageCustomControl( Composite parent )
	{
        setControl( createPageControl( parent ) );
        initializeControl();
	}
    
    /**
     * Creates custom control for user-defined query text.
     */
    private Control createPageControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );
        composite.setLayoutData( new GridData(GridData.FILL_BOTH) );
        
        

        Label l = new Label( composite, SWT.NONE );
        l.setText( "DatasProvider" );
        
        datasProviders = new ComboViewer(composite, SWT.READ_ONLY);
        datasProviders.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        datasProviders.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
        datasProviders.setLabelProvider(new LabelProvider(){
        	@Override
        	public String getText(Object element) {
        		return ((DatasProvider)element).getName();
        	}
        });
        
        datasProviders.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				validateData();
				
			}
		});
       
        setPageComplete( false );
        return composite;
    }
    
    private void initWidgets(){
  	  try {
  		java.util.Properties initialProps =  DesignSessionUtil.getEffectiveDataSourceProperties(getInitializationDesign().getDataSourceDesign() );
  		String vanillaUrl = initialProps.getProperty(Connection.PROP_VANILLA_URL);
  		String login = initialProps.getProperty(Connection.PROP_VANILLA_LOGIN);
		String password =  initialProps.getProperty(Connection.PROP_VANILLA_PASSWORD);
		int id = Integer.parseInt(initialProps.getProperty(Connection.PROP_REPOSITORY_ID));
		Integer groupid = -1;
		
		try{
			groupid = Integer.parseInt(initialProps.getProperty(Connection.PROP_GROUP_ID));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		User vanillaUser = null;
		IVanillaAPI api =  new RemoteVanillaPlatform(vanillaUrl, login, password);
		
		 try{
        	vanillaUser = api.getVanillaSecurityManager().getUserByLogin(login);
        }catch(Exception ex){
        	throw new OdaException("Unable to fnd User, " + ex.getMessage());
        }
	        
        if (vanillaUser == null){
        	throw new OdaException("The User " + login + " does not exist.");
        }
        
        String pass = password;
        
        if (!pass.matches("[0-9a-f]{32}")) {
        	pass = MD5Helper.encode(pass);
        }
        
        if (!vanillaUser.getPassword().equals(pass)){
        	throw new OdaException("Bad password.");
        }
		
		try{
			Repository def = null;
			for(Repository d : api.getVanillaRepositoryManager().getUserRepositories(vanillaUser.getLogin())){
				if (d.getId() == id){
					def = d;
					break;
				}
			}
			
			IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, login, password);
			
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, api.getVanillaSecurityManager().getGroupById(groupid), def);
			
			 IRepositoryApi socket = new RemoteRepositoryApi(ctx);
		        
			
			 dataProviderAdmin = socket.getDatasProviderService();
			 
			 datasProviders.setInput(dataProviderAdmin.getAll());
			
		}catch(Exception ex){
			MessageDialog.openError(getShell(), "Unable to load repositories", ex.getMessage());
		}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Initialiation problem", e.getMessage());
		}
  }

	/**
	 * Initializes the page control with the last edited data set design.
	 */
	private void initializeControl( )
	{
        /* 
         * To optionally restore the designer state of the previous design session, use
         *      getInitializationDesignerState(); 
         */
		initWidgets();
		
        // Restores the last saved data set design
        DataSetDesign dataSetDesign = getInitializationDesign();
        if( dataSetDesign == null )
            return; // nothing to initialize

        String queryText = dataSetDesign.getQueryText();
        if( queryText == null )
            return; // nothing to initialize

        // initialize control
       
        try{
        	for(DatasProvider d : (Collection<DatasProvider>)datasProviders.getInput()){
            	if (d.getId() == Integer.parseInt(queryText)){
            		datasProviders.setSelection(new StructuredSelection(d));
            		break;
            	}
            }
        }catch(Exception ex){
        	
        }
        
        
        validateData();
        setMessage( DEFAULT_MESSAGE );

        /*
         * To optionally honor the request for an editable or
         * read-only design session, use
         *      isSessionEditable();
         */
	}

    /**
     * Obtains the user-defined query text of this data set from page control.
     * @return query text
     */
    private String getQueryText( )
    {
    	try{
    	return ""+  ((DatasProvider)((IStructuredSelection)datasProviders.getSelection()).getFirstElement()).getId();
    	}catch(Exception ex){
    		return null;
    	}
        
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
        boolean isValid = !datasProviders.getSelection().isEmpty();

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
        IConnection customConn = null;
        try
        {
            // instantiate your custom ODA runtime driver class
            /* Note: You may need to manually update your ODA runtime extension's
             * plug-in manifest to export its package for visibility here.
             */
            IDriver customDriver = new bpm.vanilla.listdata.oda.driver.impl.Driver();
            
            // obtain and open a live connection
            customConn = customDriver.getConnection( null );
            java.util.Properties connProps = 
                DesignSessionUtil.getEffectiveDataSourceProperties( 
                         getInitializationDesign().getDataSourceDesign() );
            customConn.open( connProps );

            // update the data set design with the 
            // query's current runtime metadata
            updateDesign( dataSetDesign, customConn, queryText );
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
            closeConnection( customConn );
        }
	}

    /**
     * Updates the given dataSetDesign with the queryText and its derived metadata
     * obtained from the ODA runtime connection.
     */
    private void updateDesign( DataSetDesign dataSetDesign,
                               IConnection conn, String queryText )
        throws OdaException
    {
        IQuery query = conn.newQuery( null );
        query.prepare( queryText );
        
        // TODO a runtime driver might require a query to first execute before
        // its metadata is available
//      query.setMaxRows( 1 );
//      query.executeQuery();
        
        try
        {
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
        try
        {
            IParameterMetaData paramMd = query.getParameterMetaData();
            updateParameterDesign( paramMd, dataSetDesign );
        }
        catch( OdaException ex )
        {
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
        throws OdaException
    {
        DataSetParameters paramDesign = 
            DesignSessionUtil.toDataSetParametersDesign( paramMd, 
                    DesignSessionUtil.toParameterModeDesign( IParameterMetaData.parameterModeIn ) );
        
        // no exception in conversion; go ahead and assign to specified dataSetDesign
        dataSetDesign.setParameters( paramDesign );        
        if( paramDesign == null )
            return;     // no parameter definitions; done with update
        
        paramDesign.setDerivedMetaData( true );

        // TODO replace below with data source specific implementation;
        // hard-coded parameter's default value for demo purpose
        if( paramDesign.getParameterDefinitions().size() > 0 )
        {
            ParameterDefinition paramDef = 
                (ParameterDefinition) paramDesign.getParameterDefinitions().get( 0 );
            if( paramDef != null )
                paramDef.setDefaultScalarValue( "dummy default value" );
        }
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
