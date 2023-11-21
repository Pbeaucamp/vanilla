package bpm.fm.oda.driver.ui.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.ResultSetColumns;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.datatools.connectivity.oda.design.util.DesignUtil;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.oda.driver.Activator;
import bpm.fm.oda.driver.impl.Connection;
import bpm.fm.oda.driver.impl.QueryHelper;
import bpm.freemetrics.api.manager.client.FmClientAccessor;

public class DatasetPage extends DataSetWizardPage {

	private CheckboxTableViewer metricTable;
	private CheckboxTableViewer axisTable;
	
	private DateTime startDate;
	private DateTime endDate;
	
	private Button chkDateParam;
	
	private FmClientAccessor fmClient;
	
	public DatasetPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		
		Composite root = new Composite(parent, SWT.NONE);

		root.setLayout(new GridLayout());
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Group grpMetric = new Group(root, SWT.NONE);
		grpMetric.setLayout(new GridLayout());
		grpMetric.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpMetric.setText("Indicateur");
		
		metricTable = new CheckboxTableViewer(grpMetric, SWT.NONE);
		metricTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		metricTable.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Metric)element).getName();
			}
		});
		metricTable.setContentProvider(new ArrayContentProvider());
		
		Group grpAxis = new Group(root, SWT.NONE);
		grpAxis.setLayout(new GridLayout());
		grpAxis.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpAxis.setText("Axe");
		
		axisTable = new CheckboxTableViewer(grpAxis, SWT.NONE);
		axisTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		axisTable.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Axis)element).getName();
			}
		});
		axisTable.setContentProvider(new ArrayContentProvider());
		
		Group grpDate = new Group(root, SWT.NONE);
		grpDate.setLayout(new GridLayout(3, false));
		grpDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpDate.setText("Date");
		
		startDate = new DateTime(grpDate, SWT.CALENDAR);
		startDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblDate = new Label(grpDate, SWT.NONE);
		lblDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblDate.setText(" -> ");
		
		endDate = new DateTime(grpDate, SWT.CALENDAR);
		endDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		chkDateParam = new Button(grpDate, SWT.CHECK);
		chkDateParam.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		chkDateParam.setText("Date as parameter");
		
		try {
			initData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setControl(root);
	}

	private void initData() throws Exception {
		java.util.Properties connProps = DesignSessionUtil.getEffectiveDataSourceProperties(getInitializationDesign().getDataSourceDesign());
		String fmLogin = connProps.getProperty(Connection.PROP_FM_LOGIN);
		String fmPass = connProps.getProperty(Connection.PROP_FM_PASSWORD);
		fmClient = FmClientAccessor.getClient(Activator.getDefault().getFmManager(), fmLogin, fmPass, connProps.getProperty(Connection.PROP_FM_URL));
		
		try {
			int groupId = Integer.parseInt(connProps.getProperty(Connection.PROP_FM_GROUP_ID));
			int themeId = Integer.parseInt(connProps.getProperty(Connection.PROP_FM_THEME_ID));
			
			fmClient.setGroupId(groupId);
			fmClient.setThemeId(themeId);
		} catch (Exception e) {
		}
		
		List<Metric> metrics = fmClient.getMetrics();
		List<Axis> axis = fmClient.getAxis();
		
		metricTable.setInput(metrics);
		axisTable.setInput(axis);
		
		try {
			DataSetDesign dataSetDesign = getInitializationDesign();
			if(dataSetDesign == null) {
				return; // nothing to initialize
			}
			String queryText = dataSetDesign.getQueryText();
			if(queryText == null || queryText.equals("")) {
				return; // nothing to initialize
			}
			
			QueryHelper helper = new QueryHelper(fmClient);
			helper.parseQueryXml(queryText);
			metricTable.setCheckedElements(helper.getMetrics().toArray());
			axisTable.setCheckedElements(helper.getAxes().toArray());
			
			startDate.setDate(helper.getStartDate().getYear() + 1900, helper.getStartDate().getMonth(), helper.getStartDate().getDate());
			if(helper.getEndDate() != null) {
				endDate.setDate(helper.getEndDate().getYear() + 1900, helper.getEndDate().getMonth(), helper.getEndDate().getDate());
			}
			
			chkDateParam.setSelection(helper.isDateAsParameter());
			
		} catch(Exception e) {
			
		}
	}

	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		List<Metric> selectedMetric = new ArrayList<Metric>();
		for(Object o : metricTable.getCheckedElements()) {
			selectedMetric.add((Metric) o);
		}
		
		List<Axis> selectedAxis = new ArrayList<Axis>();
		for(Object o : axisTable.getCheckedElements()) {
			selectedAxis.add((Axis) o);
		}
		
	    int day = startDate.getDay();
	    int month = startDate.getMonth() + 1;
	    int year = startDate.getYear();

	    String strDate = year + "-";
	    strDate += (month < 10) ? "0" + month + "-" : month + "-";
	    strDate += (day < 10) ? "0" + day + "-" : day;
		
	    int day2 = endDate.getDay();
	    int month2 = endDate.getMonth() + 1;
	    int year2 = endDate.getYear();

	    String enDate = year2 + "-";
	    enDate += (month2 < 10) ? "0" + month2 + "-" : month2 + "-";
	    enDate += (day2 < 10) ? "0" + day2 + "-" : day2;
		
	    String xml = new QueryHelper().createQueryXml(selectedMetric, selectedAxis, strDate, enDate, chkDateParam.getSelection());
	    
	    IConnection conn = null;
		try {

			java.util.Properties connProps = DesignUtil.convertDataSourceProperties(getInitializationDesign().getDataSourceDesign());

			conn = new bpm.fm.oda.driver.impl.Driver().getConnection("");
			conn.open(connProps);

			design.setQueryText(xml);
			
			updateDesign(design, conn, xml);
		} catch(OdaException e) {
			// not able to get current metadata, reset previous derived metadata
			design.setResultSets(null);
			design.setParameters(null);

			e.printStackTrace();
		} finally {
			closeConnection(conn);
		}
	    
		return design;
	}
	
	/**
	 * Updates the given dataSetDesign with the queryText and its derived metadata obtained from the ODA runtime connection.
	 */
	private void updateDesign(DataSetDesign dataSetDesign, IConnection conn, String queryText) throws OdaException {
		IQuery query = conn.newQuery(null);
		query.prepare(queryText);

		try {
			IResultSetMetaData md = query.getMetaData();
			updateResultSetDesign(md, dataSetDesign);
		} catch(OdaException e) {
			// no result set definition available, reset previous derived metadata
			dataSetDesign.setResultSets(null);
			e.printStackTrace();
		}

		// proceed to get parameter design definition
		try {
			IParameterMetaData paramMd = query.getParameterMetaData();
			updateParameterDesign(paramMd, dataSetDesign);
		} catch(OdaException ex) {
			// no parameter definition available, reset previous derived metadata
			dataSetDesign.setParameters(null);
			ex.printStackTrace();
		}

		/*
		 * See DesignSessionUtil for more convenience methods to define a data set design instance.
		 */
	}

	/**
	 * Updates the specified data set design's result set definition based on the specified runtime metadata.
	 * 
	 * @param md
	 *            runtime result set metadata instance
	 * @param dataSetDesign
	 *            data set design instance to update
	 * @throws OdaException
	 */
	private void updateResultSetDesign(IResultSetMetaData md, DataSetDesign dataSetDesign) throws OdaException {
		ResultSetColumns columns = DesignSessionUtil.toResultSetColumnsDesign(md);

		ResultSetDefinition resultSetDefn = DesignFactory.eINSTANCE.createResultSetDefinition();
		resultSetDefn.setResultSetColumns(columns);

		// no exception in conversion; go ahead and assign to specified dataSetDesign
		dataSetDesign.setPrimaryResultSet(resultSetDefn);
		dataSetDesign.getResultSets().setDerivedMetaData(true);
	}
	
	/**
	 * Updates the specified data set design's parameter definition based on the specified runtime metadata.
	 * 
	 * @param paramMd
	 *            runtime parameter metadata instance
	 * @param dataSetDesign
	 *            data set design instance to update
	 * @throws OdaException
	 */
	private void updateParameterDesign(IParameterMetaData paramMd, DataSetDesign dataSetDesign) throws OdaException {

	}
	
	/**
	 * Attempts to close given ODA connection.
	 */
	private void closeConnection(IConnection conn) {
		try {
			if(conn != null && conn.isOpen()) {
				conn.close();
			}
		} catch(OdaException e) {
			// ignore
			e.printStackTrace();
		}
	}
}
