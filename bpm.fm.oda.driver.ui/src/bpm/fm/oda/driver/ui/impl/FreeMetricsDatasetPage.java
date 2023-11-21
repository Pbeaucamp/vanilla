package bpm.fm.oda.driver.ui.impl;

import java.text.SimpleDateFormat;
import java.util.List;

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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Metric;
import bpm.fm.oda.driver.Activator;
import bpm.fm.oda.driver.impl.Connection;
import bpm.fm.oda.driver.impl.QueryParser;
import bpm.freemetrics.api.manager.client.FmClientAccessor;


public class FreeMetricsDatasetPage extends DataSetWizardPage {

	private Button btnSelectMetric, btnParamMetric, btnSelectAxe, btnParamAxe, btnSelectDate, btnParamDate;
	private Button chkParamDate, chkParamMetric, chkParamAxe;
	private ComboViewer cbMetric, cbAxe;
	private Text txtDateStart, txtDateEnd, txtDatePattern;
	private FmClientAccessor fmClient;

	public FreeMetricsDatasetPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createPageCustomControl(Composite parent) {

		Composite root = new Composite(parent, SWT.NONE);

		root.setLayout(new GridLayout());
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createMetricPart(root);

		createAxePart(root);

		createDatePart(root);

//		createDatasetPart(root);

		try {
			initData();
		} catch(Exception e) {
			MessageDialog.openError(this.getShell(), "Error", "An error occured while loading the data : " + e.getMessage());
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
		
		
		cbAxe.setInput(fmClient.getAxis());
		cbMetric.setInput(fmClient.getMetrics());
		


//		cbAxe.setInput(fmClient.getApplications().toArray());
//		
//		cbObservatory.setInput(fmClient.getObservatoires().toArray());
//
		// try to load dataset if we are on edit
		try {
			DataSetDesign dataSetDesign = getInitializationDesign();
			if(dataSetDesign == null) {
				return; // nothing to initialize
			}
			String queryText = dataSetDesign.getQueryText();
			if(queryText == null || queryText.equals("")) {
				return; // nothing to initialize
			}
			QueryParser parser = new QueryParser(queryText);
			parser.parse();
//
//			txtDatePattern.setText(parser.getDateFormat());
//			
//			if(parser.isDateParameter()) {
//				btnParamDate.setSelection(true);
//				btnSelectDate.setSelection(false);
//				chkParamDate.setEnabled(true);
//				if(parser.isGenerateDateParameter()) {
//					chkParamDate.setSelection(true);
//				}
//			}
//			if(parser.isApplicationAsParameter()) {
//				btnParamAxe.setSelection(true);
//				btnSelectAxe.setSelection(false);
//				chkParamAxe.setEnabled(true);
//				cbAxe.getCombo().setEnabled(false);
//				if(parser.isGenerateAxeParameter()) {
//					chkParamAxe.setSelection(true);
//				}
//			}
//			if(parser.isMetricAsParameter()) {
//				btnParamMetric.setSelection(true);
//				btnSelectMetric.setSelection(false);
//				chkParamMetric.setEnabled(true);
//				cbMetric.getCombo().setEnabled(false);
//				if(parser.isGenerateMetricParameter()) {
//					chkParamMetric.setSelection(true);
//				}
//			}
//
			SimpleDateFormat sdf = new SimpleDateFormat(parser.getDateFormat());
			if(parser.getStartDate() != null) {
				txtDateStart.setText(sdf.format(parser.getStartDate()));
			}
			if(parser.getEndDate() != null) {
				txtDateEnd.setText(sdf.format(parser.getEndDate()));
			}
//			
			for(Object a : (List) cbAxe.getInput()) {
				if(((Axis)a).getId() == parser.getApplicationId()) {
					cbAxe.setSelection(new StructuredSelection(a), true);
					break;
				}
			}
//			for(Metric a : fmClient.getManager().getMetrics()) {
//				if(a.getId() == parser.getMetricId()) {
//					int themeId = a.getMdGlThemeId();
//					int obsId = fmClient.getObservatoryForTheme(themeId);
//					
//					for(Object obss : ((Object[])cbObservatory.getInput())) {
//						Observatoire obs = (Observatoire) obss;
//						if(obs.getId() == obsId) {
//							cbObservatory.setSelection(new StructuredSelection(obs), true);
//							break;
//						}
//					}
//					cbTheme.setInput(fmClient.getThemeByObservatoire(obsId));
//					for(Object obss : (List)cbTheme.getInput()) {
//						Theme obs = (Theme) obss;
//						if(obs.getId() == themeId) {
//							cbTheme.setSelection(new StructuredSelection(obs), true);
//							break;
//						}
//					}
//					cbMetric.setInput(fmClient.getMetricByTheme(themeId));
					for(Object obss : (List)cbMetric.getInput()) {
						Metric obs = (Metric) obss;
						if(obs.getId() == parser.getMetricId()) {
							cbMetric.setSelection(new StructuredSelection(obs), true);
							break;
						}
					}
//					break;
//				}
//			}
		} catch(Throwable e) {
			e.printStackTrace();
		}

	}

	private void createDatasetPart(Composite root) {
		
		Group grpParameters = new Group(root, SWT.NONE);
		grpParameters.setLayout(new GridLayout(1, false));
		grpParameters.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpParameters.setText("Paramètres");
		
		chkParamDate = new Button(grpParameters, SWT.CHECK);
		chkParamDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		chkParamDate.setText("Générer le jeu de données de dates");
		chkParamDate.setEnabled(false);
		
		chkParamAxe = new Button(grpParameters, SWT.CHECK);
		chkParamAxe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		chkParamAxe.setText("Générer le jeu de données des axes");
		chkParamAxe.setEnabled(false);
		
		chkParamMetric = new Button(grpParameters, SWT.CHECK);
		chkParamMetric.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		chkParamMetric.setText("Générer le jeu de données des indicateurs");
		chkParamMetric.setEnabled(false);

	}

	private void createDatePart(Composite root) {
		Group grpDate = new Group(root, SWT.NONE);
		grpDate.setLayout(new GridLayout(3, false));
		grpDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpDate.setText("Date");

		btnSelectDate = new Button(grpDate, SWT.RADIO);
		btnSelectDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnSelectDate.setText("Choix de la date");
		btnSelectDate.setSelection(true);
		btnSelectDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtDateStart.setEnabled(true);
				txtDateEnd.setEnabled(true);
			}
		});

		txtDateStart = new Text(grpDate, SWT.BORDER);
		txtDateStart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Label lblDate = new Label(grpDate, SWT.NONE);
		lblDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblDate.setText(" End Date :");

		txtDateEnd = new Text(grpDate, SWT.BORDER);
		txtDateEnd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		btnParamDate = new Button(grpDate, SWT.RADIO);
		btnParamDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		btnParamDate.setText("Date en tant que param�tre");
		btnParamDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtDateStart.setEnabled(false);
				txtDateEnd.setEnabled(false);
			}
		});

		Label lblDatePattern = new Label(grpDate, SWT.NONE);
		lblDatePattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblDatePattern.setText("Format de date");

		txtDatePattern = new Text(grpDate, SWT.BORDER);
		txtDatePattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		txtDatePattern.setText("yyyy-MM-dd");
	}

	private void createAxePart(Composite root) {
		Group grpAxe = new Group(root, SWT.NONE);
		grpAxe.setLayout(new GridLayout(2, false));
		grpAxe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpAxe.setText("Axe");

		btnSelectAxe = new Button(grpAxe, SWT.RADIO);
		btnSelectAxe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnSelectAxe.setText("Choisir un axe");
		btnSelectAxe.setSelection(true);
		btnSelectAxe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbAxe.getCombo().setEnabled(true);
			}
		});

		cbAxe = new ComboViewer(grpAxe, SWT.BORDER | SWT.DROP_DOWN);
		cbAxe.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cbAxe.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Axis m = (Axis) element;
				return m.getName();
			}
		});
		cbAxe.setContentProvider(new ArrayContentProvider());

		btnParamAxe = new Button(grpAxe, SWT.RADIO);
		btnParamAxe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		btnParamAxe.setText("Axe en tant que param�tre");
		btnParamAxe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbAxe.getCombo().setEnabled(false);
			}
		});
	}

	private void createMetricPart(Composite root) {
		Group grpMetric = new Group(root, SWT.NONE);
		grpMetric.setLayout(new GridLayout(2, false));
		grpMetric.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpMetric.setText("Indicateur");
		
//		Label lblObs = new Label(grpMetric, SWT.NONE);
//		lblObs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		lblObs.setText("Observatoire");
//		
//		cbObservatory = new ComboViewer(grpMetric, SWT.DROP_DOWN);
//		cbObservatory.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		cbObservatory.setLabelProvider(new LabelProvider() {
//			@Override
//			public String getText(Object element) {
//				return ((Observatoire)element).getName();
//			}
//		});
//		cbObservatory.setContentProvider(new ArrayContentProvider());
//		cbObservatory.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				Observatoire obs = (Observatoire) ((IStructuredSelection)event.getSelection()).getFirstElement();
//				cbTheme.setInput(fmClient.getThemeByObservatoire(obs.getId()));
//			}
//		});
//		
//		Label lblTheme = new Label(grpMetric, SWT.NONE);
//		lblTheme.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		lblTheme.setText("Thème");
//
//		cbTheme = new ComboViewer(grpMetric, SWT.DROP_DOWN);
//		cbTheme.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
//		cbTheme.setLabelProvider(new LabelProvider() {
//			@Override
//			public String getText(Object element) {
//				return ((Theme)element).getName();
//			}
//		});
//		cbTheme.setContentProvider(new ArrayContentProvider());
//		cbTheme.addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				Theme obs = (Theme) ((IStructuredSelection)event.getSelection()).getFirstElement();
//				cbMetric.setInput(fmClient.getMetricByTheme(obs.getId()));
//			}
//		});
		
		btnSelectMetric = new Button(grpMetric, SWT.RADIO);
		btnSelectMetric.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnSelectMetric.setText("Choisir un indicateur");
		btnSelectMetric.setSelection(true);
		btnSelectMetric.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbMetric.getCombo().setEnabled(true);
			}
		});

		cbMetric = new ComboViewer(grpMetric, SWT.BORDER | SWT.DROP_DOWN);
		cbMetric.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cbMetric.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Metric m = (Metric) element;
				return m.getName();
			}
		});
		cbMetric.setContentProvider(new ArrayContentProvider());

		btnParamMetric = new Button(grpMetric, SWT.RADIO);
		btnParamMetric.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		btnParamMetric.setText("Indicateur en tant que param�tre");
		btnParamMetric.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbMetric.getCombo().setEnabled(false);
			}
		});
	}

	/**
	 * Obtains the user-defined query text of this data set from page control.
	 * 
	 * @return query text
	 */
	private String getQueryText() {
		StringBuffer buf = new StringBuffer();
		buf.append("<queryAssoc>\n");
		if(btnParamAxe.getSelection()) {
			buf.append("<fmApplicationAsParameter>" + btnParamAxe.getSelection() + "</fmApplicationAsParameter>\n");
		}
		else {
			IStructuredSelection ss = (IStructuredSelection) cbAxe.getSelection();
			Axis app = (Axis) ss.getFirstElement();
			buf.append("<fmApplicationAsParameter>" + btnParamAxe.getSelection() + "</fmApplicationAsParameter>\n");
			buf.append("<fmApplicationId>" + app.getId() + "</fmApplicationId>\n");
		}

		if(btnParamMetric.getSelection()) {
			buf.append("<fmMetricAsParameter>" + btnParamMetric.getSelection() + "</fmMetricAsParameter>\n");
		}
		else {
			IStructuredSelection ss = (IStructuredSelection) cbMetric.getSelection();
			Metric met = (Metric) ss.getFirstElement();
			buf.append("<fmMetricAsParameter>" + btnParamMetric.getSelection() + "</fmMetricAsParameter>\n");
			buf.append("<fmMetricId>" + met.getId() + "</fmMetricId>\n");
		}

		if(btnParamDate.getSelection()) {
			buf.append("<dateAsParameter>" + btnParamDate.getSelection() + "</dateAsParameter>\n");
		}
		else {
			String dateStart = txtDateStart.getText();
			String dateEnd = txtDateEnd.getText();

			buf.append("<dateStart>" + dateStart + "</dateStart>\n");
			buf.append("<dateEnd>" + dateEnd + "</dateEnd>\n");
			buf.append("<dateAsParameter>" + btnParamDate.getSelection() + "</dateAsParameter>\n");
		}

		buf.append("<dateFormat>" + txtDatePattern.getText() + "</dateFormat>\n");
		
		if(btnParamDate.getSelection()) {
			buf.append("<generateDateDataset>" + btnParamDate.getSelection() + "</generateDateDataset>\n");
		}
		if(btnParamAxe.getSelection()) {
			buf.append("<generateAxeDataset>" + btnParamAxe.getSelection() + "</generateAxeDataset>\n");
		}
		if(btnParamMetric.getSelection()) {
			buf.append("<generateMetricDataset>" + btnParamMetric.getSelection() + "</generateMetricDataset>\n");
		}
		
		buf.append("</queryAssoc>\n");
		return buf.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage#collectDataSetDesign(org.eclipse.datatools.connectivity.oda.design.DataSetDesign)
	 */
	protected DataSetDesign collectDataSetDesign(DataSetDesign design) {
		savePage(design);
		return design;
	}

	/**
	 * Saves the user-defined value in this page, and updates the specified dataSetDesign with the latest design definition.
	 */
	private void savePage(DataSetDesign dataSetDesign) {
		// save user-defined query text
		String queryText = getQueryText();
		dataSetDesign.setQueryText(queryText);

		// obtain query's current runtime metadata, and maps it to the dataSetDesign
		IConnection customConn = null;
		try {
			// instantiate your custom ODA runtime driver class
			/*
			 * Note: You may need to manually update your ODA runtime extension's plug-in manifest to export its package for visibility here.
			 */
			IDriver customDriver = new bpm.fm.oda.driver.impl.Driver();

			// obtain and open a live connection
			customConn = customDriver.getConnection(null);
			java.util.Properties connProps = DesignSessionUtil.getEffectiveDataSourceProperties(getInitializationDesign().getDataSourceDesign());
			customConn.open(connProps);

			// update the data set design with the
			// query's current runtime metadata
			updateDesign(dataSetDesign, customConn, queryText);
		} catch(OdaException e) {
			// not able to get current metadata, reset previous derived metadata
			dataSetDesign.setResultSets(null);
			dataSetDesign.setParameters(null);

			e.printStackTrace();
		} finally {
			closeConnection(customConn);
		}
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
		DataSetParameters paramDesign = DesignSessionUtil.toDataSetParametersDesign(paramMd, DesignSessionUtil.toParameterModeDesign(IParameterMetaData.parameterModeIn));

		dataSetDesign.setParameters(paramDesign);
		if(paramDesign == null)
			return;

		paramDesign.setDerivedMetaData(true);

		if(paramDesign.getParameterDefinitions().size() > 0) {
			ParameterDefinition paramDef = (ParameterDefinition) paramDesign.getParameterDefinitions().get(0);
			if(paramDef != null) {
				paramDef.setDefaultScalarValue("dummy default value");
			}
		}
	}

	/**
	 * Attempts to close given ODA connection.
	 */
	private void closeConnection(IConnection conn) {
		try {
			if(conn != null && conn.isOpen())
				conn.close();
		} catch(OdaException e) {
			e.printStackTrace();
		}
	}
}
