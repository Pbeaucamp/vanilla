package bpm.metadata.birt.oda.ui.impl.dataset;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import bpm.metadata.birt.oda.runtime.ConnectionPool;
import bpm.metadata.birt.oda.runtime.impl.Connection;
import bpm.metadata.birt.oda.runtime.impl.ConnectionManager;
import bpm.metadata.birt.oda.ui.Activator;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryGenerator;
import bpm.metadata.resource.Prompt;
import bpm.metadata.ui.query.composite.CompositeSqlQuery;


public class FmdtDataSetPage extends DataSetWizardPage {

	private CompositeSqlQuery composite;

	
	public FmdtDataSetPage(String pageName) {
		super(pageName);
	}

	

	public FmdtDataSetPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		setControl(createPageControl(parent));

		initialiseControl();
	}
	
	private Control createPageControl(Composite parent){
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		
		composite = new CompositeSqlQuery(container, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		return container;
		
	}

	private void initialiseControl(){
		
		try {
			
			Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
			
			IConnection conn =  ConnectionManager.getConnection(connProps,getInitializationDesign().getDataSourceDesign().getOdaExtensionDataSourceId());
			conn.close();
			conn = ConnectionManager.getConnection(connProps,getInitializationDesign().getDataSourceDesign().getOdaExtensionDataSourceId());
			
			Connection connection = (Connection) conn;
			
			
			composite.setConnection(((Connection)conn).getFmdtPackage(),
						((Connection)conn).getGroupName(), connection.getRepositoryConnection().getContext(), connection.getItemId());
			DataSetDesign dataSetDesign = getInitializationDesign();

			if (dataSetDesign == null || dataSetDesign.getQueryText() == null
					|| dataSetDesign.getQueryText().trim().equals("")) { //$NON-NLS-1$
				return;
			}
			QuerySql q = new SqlQueryDigester(IOUtils.toInputStream(
					dataSetDesign.getQueryText(), "UTF-8"), ((Connection) conn)
					.getGroupName(), ((Connection) conn).getFmdtPackage())
					.getModel();
			
			composite.fill(q);
			
		} catch (Exception e1) {
			MessageDialog.openError(getShell(), Activator.getResourceString("FmdtDataSetPage.Error"), e1.getMessage()); //$NON-NLS-1$ // FP IDE
		}
		
        
        
	}
	
	
	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {
		
        String queryText = composite == null ? dataSetDesign.getQueryText() : composite.createFmdtQuery().getXml();
        dataSetDesign.setQueryText(queryText);

        if (dataSetDesign.getPublicProperties() == null) {
            try {
                dataSetDesign.setPublicProperties(DesignSessionUtil
                        .createDataSetPublicProperties(dataSetDesign
                                .getOdaExtensionDataSourceId(), dataSetDesign
                                .getOdaExtensionDataSetId(),
                                getBlankPageProperties()));
            } catch (OdaException e) {

                e.printStackTrace();
            }
        }

      

        // obtain query's current runtime metadata, and maps it to the
        // dataSetDesign
        IConnection conn = null;
        try {
            // obtain and open a live connection
           
            java.util.Properties connProps = DesignUtil
                    .convertDataSourceProperties(getInitializationDesign()
                            .getDataSourceDesign());
            
            conn =  ConnectionManager.getConnection(connProps,getInitializationDesign().getDataSourceDesign().getOdaExtensionDataSourceId());
            // update the data set design with the query's current runtime
            // metadata
            
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
	
	@Override
	public IWizardPage getNextPage() {
//		try {
//			Composite parent = getControl().getParent();
//			getControl().dispose();
//			setControl(createPageControl(parent));
//
//			initialiseControl();
//			
//			parent.redraw();
//			parent.pack(true);
//			
//		} catch(Throwable e) {
//			e.printStackTrace();
//		}
		return super.getNextPage();
	}
	
	private void updateDesign(DataSetDesign dataSetDesign, IConnection conn,
		String queryText) throws OdaException {
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
				Iterator iter = dataSetParameter.getParameterDefinitions( )
						.iterator( );
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
	private static void proccessParamDefn(ParameterDefinition defn, DataSetParameters parameters) {
		if (defn.getAttributes().getNativeDataTypeCode() == Types.NULL) {
			defn.getAttributes().setNativeDataTypeCode(Types.CHAR);
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
	
	 private static java.util.Properties getBlankPageProperties() {
	        java.util.Properties prop = new java.util.Properties();
	        prop.setProperty(Connection.BUSINESS_MODEL, ""); //$NON-NLS-1$
	        prop.setProperty(Connection.BUSINESS_PACKAGE, ""); //$NON-NLS-1$
	        prop.setProperty(Connection.DIRECTORY_ITEM_ID, ""); //$NON-NLS-1$
	        prop.setProperty(Connection.GROUP_NAME, ""); //$NON-NLS-1$
	        prop.setProperty(Connection.PASSWORD, ""); //$NON-NLS-1$
	        prop.setProperty(Connection.URL, ""); //$NON-NLS-1$
	        prop.setProperty(Connection.USER, ""); //$NON-NLS-1$


	        return prop;
	 }
	 
	 /**
     * Attempts to close given ODA connection.
     * 
     * @param conn the conn
     */
    private void closeConnection(IConnection conn) {
        try {
            if (conn != null && conn.isOpen()) {
                conn.close();
            }
        } catch (OdaException e) {
            e.printStackTrace();
        }
    }


    

   


	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	/**
	 * try to generate the SQL query.
	 * if it fails, a popup with the error is launched
	 * and the error message is returned
	 * @return null if no errors
	 */
	private String checkQuery(){
		QuerySql fmdtQuery = composite.createFmdtQuery();
		
		Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());
		Collection<IBusinessModel> models = null;
		try{
			models = ConnectionPool.getConnection(connProps);
		}catch(Exception ex){
			ex.printStackTrace();
			return ex.getMessage();
		}
		
		HashMap<Prompt, List<String>> promptsDummyValues = new HashMap<Prompt, List<String>>();
		
		for(Prompt pmp : fmdtQuery.getPrompts()){
			promptsDummyValues.put(pmp, new ArrayList<String>());
		}
		
		for(IBusinessModel m : models){
			if (m.getName().equals(connProps.get(Connection.BUSINESS_MODEL))){
				
				IBusinessPackage p = m.getBusinessPackage(connProps.getProperty(Connection.BUSINESS_PACKAGE), connProps.getProperty(Connection.GROUP_NAME));
				try{
					SqlQueryGenerator.checkQuery(p, fmdtQuery, connProps.getProperty(Connection.GROUP_NAME), false, promptsDummyValues);
					return null;
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Query", ex.getMessage());
					return ex.getMessage();
				}
				
			}
		}
		
		return "BusinessModel or BusinessPackage not found for the DataSource properties";
	}
	
	@Override
	public boolean isPageComplete() {

		return super.isPageComplete();
	}
	
}
