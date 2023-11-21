package bpm.gateway.runtime2.transformations.kpi;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.application.Application;
import bpm.freemetrics.api.organisation.metrics.Metric;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.security.FmUser;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.core.transformations.freemetrics.KPIOutput;
import bpm.gateway.runtime2.RuntimeEngine;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunKPILoad extends RuntimeStep{

	
	private static final String insertQuery = "insert into fm_metricsvalues(MV_ID, MV_CREATION_DATE,MV_VALUE,MV_PERIOD_DATE,MV_GL_ASSOC_ID) values(?,?,?,?,?)";
	private static final String updateQuery = "update fm_metricsvalues set MV_VALUE=? where MV_ID=?";
	private static final String selectQuery = "select * from fm_metricsvalues where MV_GL_ASSOC_ID=? AND MV_PERIOD_DATE=?";
	private IManager fmMgr;
	private FmUser user;
	private Connection socket;
	private PreparedStatement insertStmt;
	private PreparedStatement updateStmt;
	private PreparedStatement selectStmt;
	
	private Statement commitStmt;
	private int maxId = -1;
	private int batchSize = 0;
	
	public RunKPILoad(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	
	@Override
	public void init(Object adapter) throws Exception {
		
		/*
		 * create the Connection to Freemetrics from its serverDefinition
		 * and Store the FmUser
		 */
		Properties prop = new Properties();
		
		KPIOutput transfo = (KPIOutput)getTransformation();
		FreemetricServer server = (FreemetricServer)transfo.getServer();
		
		
//		DriverInfo dInfo = JdbcConnectionProvider.getListDriver().getInfo(((DataBaseConnection)server.getCurrentConnection(adapter)).getDriverName());
//		
//		String dbUrl = dInfo.getUrlPrefix() + ((DataBaseConnection)server.getCurrentConnection(adapter)).getHost() + ":" + ((DataBaseConnection)server.getCurrentConnection(adapter)).getPort() + "/" + ((DataBaseConnection)server.getCurrentConnection(adapter)).getDataBaseName();
//		
//		prop.setProperty("driverClassName", dInfo.getClassName());
//		prop.setProperty("username", ((DataBaseConnection)server.getCurrentConnection(adapter)).getLogin());
//		prop.setProperty("password", ((DataBaseConnection)server.getCurrentConnection(adapter)).getPassword());
//		prop.setProperty("url", dbUrl);
		
		FactoryManager.init("", 0);
		fmMgr = FactoryManager.getManager();
		
		user = fmMgr.getUserByNameAndPass(server.getFmLogin(), server.getFmPassword());
		
		if (user == null){
			throw new Exception("The FmUser informations are not valid.");
		}
		
		//initConnection
		Object o = FactoryManager.getInstance(/*prop, "resources/freeMetricsContext.xml"*/).getBean("dataSource");
		Method m = o.getClass().getMethod("getConnection");
		socket = (Connection)m.invoke(o);
		
		//create Statements
		commitStmt = socket.createStatement();
		info( " created commit statement");
		
		insertStmt = socket.prepareStatement(insertQuery);
		info( " create Insert PreparedStatement");
		
		if (transfo.isPerformUpdateOnOldValues()){
			updateStmt = socket.prepareStatement(updateQuery);
			info(" create Update PreparedStatement");
			
			selectStmt = socket.prepareStatement(selectQuery);
			info(" create select PreparedStatement");
		}
		
		//get the max MV_ID
		try{
			ResultSet _rs = commitStmt.executeQuery("select MAX(MV_ID) from fm_metricsvalues");
			_rs.next();
			maxId = _rs.getInt(1) + 1;
			_rs.close();
			info( " MetricsValues biggest id gotten");
			
		}catch(Exception e){
			error(" error when getting the biggest Id for the metricValues", e);
			throw e;
		}
		
		
		
		isInited = true;
		info(" inited");
	}

	
	
	
	@Override
	public void performRow() throws Exception {
		KPIOutput kpiTr = (KPIOutput)getTransformation();
		
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(Exception e){
				
			}
		}
		
		Row row = readRow();
		
		Object date = row.get(kpiTr.getInputDateIndex() );
		Object application = kpiTr.getInputApplicationIndex() != null ? row.get(kpiTr.getInputApplicationIndex()) : null;
		Object metric = kpiTr.getInputMetricIndex() != null ? row.get(kpiTr.getInputMetricIndex()) : null;
		Object o = null;
		Integer assocId = null;
		
		if (kpiTr.getInputAssocIndex() != null && kpiTr.getInputAssocIndex() > -1){
			o = row.get(kpiTr.getInputAssocIndex());
			if (o instanceof Long){
				assocId = kpiTr.getInputAssocIndex() != null ? ((Long)row.get(kpiTr.getInputAssocIndex())).intValue() : null;
			}
			else if (o instanceof Double){
				assocId = kpiTr.getInputAssocIndex() != null ? ((Double)row.get(kpiTr.getInputAssocIndex())).intValue() : null;
			}
			else if (o instanceof Integer){
				assocId = kpiTr.getInputAssocIndex() != null ? (Integer)row.get(kpiTr.getInputAssocIndex()) : null;
			}
			else {
				try {
					assocId = kpiTr.getInputAssocIndex() != null ? Integer.parseInt(row.get(kpiTr.getInputAssocIndex()).toString()) : null;
				} catch (Exception e) { }
			}
		}
		
		
		
		Object value = kpiTr.getInputValueIndex() != null ? row.get(kpiTr.getInputValueIndex()) : null;
		
		if ((assocId == null && (application == null || metric == null)) 
			|| date == null || value == null){
			
			error(" informations are null, they must have a value ");
			throw new Exception("Transformation parameter are not set properly, some requeired datas are missing");
		}
		
		
		//if the assoId is a parameter, use only this one
		//else check if the application is hierarchical and if there's more than one assoId
		//the application name can be composed by more than one application (like ServiceA_GradeA_SectionA)
		
		List<Integer> assoIds = new ArrayList<Integer>();
		
		/*
		 * look for Fm MetricApplicationAssociation
		 */
//		int metricId = -1;
		if (assocId == null){
			
			if(kpiTr.isMetricName() && kpiTr.isApplicationName()) {
				//look if there's only one assoId
				String assocName = metric.toString() + "_" + application.toString();
				Assoc_Application_Metric assoc = fmMgr.getAssoc_Territoire_MetricByName(assocName);
				if(assoc != null) {
					assoIds.add(assoc.getId());
				}
				//find all the assoIds for these metric and application
				else {
					Metric met = fmMgr.getMetricByName(metric.toString());
					Application app = fmMgr.getApplicationByName(application.toString());
					
					List<Assoc_Application_Metric> assos = fmMgr.getAssoMetricAppByMetricAndAppIds(app.getId(), met.getId());
					for(Assoc_Application_Metric asso : assos) {
						assoIds.add(asso.getId());
					}
					
				}
			}
			
//			if (kpiTr.isMetricName()){
//				metricId = fmMgr.getMetricByName(metric.toString()).getId();
//			}
//			else{
//				if (metric instanceof String){
//					metricId = Integer.parseInt((String)metric);
//				}
//				else{
//					metricId = (Integer)metric;
//				}
//			}
//			
//			int applicationId = -1;
//			if (kpiTr.isApplicationName()){
//				applicationId = fmMgr.getApplicationByName(application.toString()).getId();
//			}else{
//				if (application instanceof String){
//					applicationId = Integer.parseInt((String)application);
//				}
//				else{
//					applicationId  = (Integer)application;
//				}
//			}
//			assocId = fmMgr.getAssociationIdForMetrIdAndAppId(metricId, applicationId);
		}
		else {
			assoIds.add(assocId);
		}
		
		for(Integer idAsso : assoIds) {
		
			MetricValues mValue = new MetricValues();
			mValue.setMvGlAssoc_ID(idAsso);
			mValue.setId(maxId);
			
			SimpleDateFormat sdf = null;				
				
			if (kpiTr.getDateFormat() == null || "".equals(kpiTr.getDateFormat())){
				sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			}
			else{
				sdf = new SimpleDateFormat(kpiTr.getDateFormat());
			}
			
			if (date instanceof String){
				mValue.setMvPeriodDate(sdf.parse(date.toString()));
			}
			else if (date instanceof Integer){
				mValue.setMvPeriodDate(new Date(((Integer)date)));
			}
			else if (date instanceof Long){
				mValue.setMvPeriodDate(new Date(((Long)date)));
			}
			else{
				mValue.setMvPeriodDate((Date)date);
			}
				
	
			if (value == null){
				mValue.setMvValue(null);
			}
			else if (value instanceof Float){
				mValue.setMvValue((Float)value);
			}
			else{
				mValue.setMvValue(Float.valueOf(value.toString().replace(",", ".")));
			}
			
			
			mValue.setMvValueDate(Calendar.getInstance().getTime());
			mValue.setMvCreationDate(Calendar.getInstance().getTime());
			
			
			if (kpiTr.isPerformUpdateOnOldValues()){
				insertOrUpdate(mValue);
			}
			else{
				insert(mValue);
			}
		}
	}
	
	
	private void executeBatch() throws Exception{
		try{
			int c = 0;
			int[] a = insertStmt.executeBatch();
			for (int i : a){
				c += i;
			}
			writedRows += c;
			debug( " executed batch " + c + " row inserted on " + a.length);
			
			if (updateStmt != null){
				c = 0;
				a = updateStmt.executeBatch();
				for (int i : a){
					c += i;
				}
				debug(" executed batch " + c + " row updated on " + a.length);
				writedRows += c;
			}
			
//			commitStmt.execute("commit");
			
			batchSize = 0;
		}catch(Exception e){
			error(" error when executing insert batch", e);
			throw e;
		}
	}
	
	
	/**
	 * add a batch to the given statement
	 * if batchSize == 1000 launch execute batch
	 * @param stmt
	 * @throws Exception
	 */
	private void addbatch(PreparedStatement stmt) throws Exception{
		if (batchSize < RuntimeEngine.MAX_ROWS){
			try{
				stmt.addBatch();
				batchSize++; 
			}catch(Exception e){
				error(" error when adding batch to insert statement", e);
				throw e;
			}
			
		}
		else{
			try{
				stmt.addBatch();
				executeBatch();
			}catch(Exception e){
				error(" error when adding batch to insert statement", e);
				throw e;
			}
		
			
		}
	}

	@Override
	protected synchronized void setEnd() {
		try {
			executeBatch();
		} catch (Exception e) {
			error(" unable to execute last batch", e);
		}
		super.setEnd();
	}

	private void insert(MetricValues value) throws Exception {
		try{
			insertStmt.setObject(1, maxId++);
			insertStmt.setObject(2, value.getMvValueDate());
			insertStmt.setObject(3, value.getMvValue());
			insertStmt.setObject(4, value.getMvPeriodDate());
			insertStmt.setObject(5, value.getMvGlAssoc_ID());
		}catch(SQLException ex){
			error( " error when setting values on insert statement", ex);
			throw ex;
		}
		addbatch(insertStmt);
		
	}
	
	private void update(MetricValues value) throws Exception {
		try{
			updateStmt.setObject(1,value.getMvValue());
			updateStmt.setObject(2, value.getId());
			
		}catch(SQLException ex){
			error(" error when setting values on insert statement", ex);
			throw ex;
		}
		addbatch(updateStmt);
		
	}
	
	private void insertOrUpdate(MetricValues value) throws Exception {
		selectStmt.setInt(1, value.getMvGlAssoc_ID());
		selectStmt.setDate(2,  new java.sql.Date(value.getMvPeriodDate().getTime()));
		
		ResultSet rs = selectStmt.executeQuery();
		boolean updated = false;
		while(rs.next()){
			updated = true;
			debug(" need an update");
			int id = rs.getInt(1);
			value.setId(id);
			break;
		}
		
		rs.close();
		
		if (!updated){
			insert(value);
		}
		else{
			update(value);
		}
		
	}
	
	@Override
	public void releaseResources() {
		
		
	}

}
