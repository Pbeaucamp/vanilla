package bpm.nosql.oda.ui.cassandra.impl;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.nosql.oda.runtime.impl.CassandraConnection;
import bpm.nosql.oda.runtime.impl.CassandraDriver;
import bpm.nosql.oda.runtime.impl.CassandraQuery;

public class CassandraDataSetWizardPage extends DataSetWizardPage{

  private static String DEFAULT_MESSAGE = "Define the query text for the data set";
  private transient Text m_queryTextField;
	 
  public CassandraDataSetWizardPage() {
	  super("");
  }
	public CassandraDataSetWizardPage(String pageName) {
	    super(pageName);
	    setTitle(pageName);
	    setMessage(DEFAULT_MESSAGE);
	}
	
	public CassandraDataSetWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	    setMessage(DEFAULT_MESSAGE);	}

	@Override
	public void createPageCustomControl(Composite parent) {
	    setControl(createPageControl(parent));
	    initializeControl();		
	}

	private Control createPageControl(Composite parent) {
	    Composite composite = new Composite(parent, 0);
	    composite.setLayout(new GridLayout(1, false));
	    GridData gridData = new GridData(272);

	    composite.setLayoutData(gridData);

	    Label fieldLabel = new Label(composite, 0);
	    fieldLabel.setText("&Query Text:");

	    this.m_queryTextField = new Text(composite, 2816);

	    GridData data = new GridData(768);
	    data.heightHint = 100;
	    this.m_queryTextField.setLayoutData(data);
	    this.m_queryTextField.addModifyListener(new ModifyListener()
	    {
	      public void modifyText(ModifyEvent e)
	      {
	    	  CassandraDataSetWizardPage.this.validateData();
	      }
	    });
	    setPageComplete(false);
	    return composite;
	}

	private void initializeControl() {
	    DataSetDesign dataSetDesign = getInitializationDesign();
	    if (dataSetDesign == null) {
	      return;
	    }
	    String queryText = dataSetDesign.getQueryText();
	    if (queryText == null) {
	      return;
	    }

	    this.m_queryTextField.setText(queryText);
	    validateData();
	    setMessage(DEFAULT_MESSAGE);		
	}

	  private void validateData()
	  {
	    boolean isValid = (this.m_queryTextField != null) && 
	      (getQueryText() != null) && (((String) getQueryText()).trim().length() > 0);

	    if (isValid)
	      setMessage(DEFAULT_MESSAGE);
	    else {
	      setMessage("Requires input value.", 3);
	    }
	    setPageComplete(isValid);
	  }

	private Object getQueryText() {
		return this.m_queryTextField.getText();
	}
	

	  protected DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign)
	  {
		    String queryText = (String) getQueryText();
		    dataSetDesign.setQueryText(queryText);

		    CassandraConnection customConn = null;
		    try
		    {
		    	CassandraDriver customDriver = new CassandraDriver();

		      customConn = customDriver.getConnection(null);
		      java.util.Properties connProps = 
		        (java.util.Properties) DesignSessionUtil.getEffectiveDataSourceProperties(
			getInitializationDesign().getDataSourceDesign());
		      customConn.open(connProps);

		      updateDesign(dataSetDesign, customConn, queryText);
		    }
		    catch (OdaException e)
		    {
		      dataSetDesign.setResultSets(null);
		      dataSetDesign.setParameters(null);

		      e.printStackTrace();
		    }
		    finally
		    {
		      closeConnection(customConn);
		    }
	    return dataSetDesign;
	  }
	  
	  protected void collectResponseState()
	  {
	    super.collectResponseState();
	  }
	  
	  protected boolean canLeave()
	  {
	    return isPageComplete();
	  }
	  
	  private boolean hasValidData()
	  {
	    validateData();

	    return canLeave();
	  }
	  
//	  private void savePage(DataSetDesign dataSetDesign)
//	  {
		  
//	    String queryText = (String) getQueryText();
//	    dataSetDesign.setQueryText(queryText);
//
//	    CassandraConnection customConn = null;
//	    try
//	    {
//	    	CassandraDriver customDriver = new CassandraDriver();
//
//	      customConn = customDriver.getConnection(null);
//	      java.util.Properties connProps = 
//	        (java.util.Properties) DesignSessionUtil.getEffectiveDataSourceProperties(
//		getInitializationDesign().getDataSourceDesign());
//	      customConn.open(connProps);
//
//	      updateDesign(dataSetDesign, customConn, queryText);
//	    }
//	    catch (OdaException e)
//	    {
//	      dataSetDesign.setResultSets(null);
//	      dataSetDesign.setParameters(null);
//
//	      e.printStackTrace();
//	    }
//	    finally
//	    {
//	      closeConnection(customConn);
//	    }
//	  }
	  
	  private void updateDesign(DataSetDesign dataSetDesign, CassandraConnection conn, String queryText)
	    throws OdaException
	  {
	    CassandraQuery query =  (CassandraQuery) conn.newQuery(null);
	    query.prepare(queryText.toString());
	    try
	    {
	      IResultSetMetaData md = query.getMetaData();
	      updateResultSetDesign(md, dataSetDesign);
	    }
	    catch (OdaException e)
	    {
	      dataSetDesign.setResultSets(null);
	      e.printStackTrace();
	    }

	    try
	    {
	      IParameterMetaData paramMd = query.getParameterMetaData();
	      updateParameterDesign(paramMd, dataSetDesign);
	    }
	    catch (OdaException ex)
	    {
	      dataSetDesign.setParameters(null);
	      ex.printStackTrace();
	    }
	  }
	  
	  private void updateResultSetDesign(IResultSetMetaData md, DataSetDesign dataSetDesign)
	    throws OdaException
	  {
	    ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign(md);

	    ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE.createResultSetDefinition();

	    resultSetDefn.setResultSetColumns(columns);

	    dataSetDesign.setPrimaryResultSet(resultSetDefn);
	    dataSetDesign.getResultSets().setDerivedMetaData(true);
	  }
	  
	  private void updateParameterDesign(IParameterMetaData paramMd, DataSetDesign dataSetDesign)
	    throws OdaException
	  {
	    DataSetParameters paramDesign = DesignSessionUtil.toDataSetParametersDesign(paramMd,DesignSessionUtil.toParameterModeDesign(1));

	    dataSetDesign.setParameters(paramDesign);
	    
	    if (paramDesign == null) {
	      return;
	    }
	    paramDesign.setDerivedMetaData(true);

	    if (paramDesign.getParameterDefinitions().size() > 0)
	    {
	      ParameterDefinition paramDef = (ParameterDefinition)paramDesign.getParameterDefinitions().get(0);
	      
	      if (paramDef != null)
	      {
	    	  paramDef.setDefaultScalarValue("dummy default value");
	      }
	    }
	  }
	  
	  private void closeConnection(IConnection conn)
	  {
	    try
	    {
	      if ((conn != null) && (conn.isOpen())) {
	        conn.close();
	      }
	    }
	    catch (OdaException e)
	    {
	      e.printStackTrace();
	    }
	  }
}
