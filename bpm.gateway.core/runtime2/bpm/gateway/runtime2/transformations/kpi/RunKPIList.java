package bpm.gateway.runtime2.transformations.kpi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.security.FmUser;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.transformations.freemetrics.KPIList;
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.studio.jdbc.management.model.DriverInfo;

public class RunKPIList extends RuntimeStep{

	private IManager fmMgr;
	private FmUser user;
	private List<Integer> assocIds = new ArrayList<Integer>();
	private Iterator<Integer> iterator;
		
	
	public RunKPIList(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	
	@Override
	public void init(Object adapter) throws Exception {
		/*
		 * create the Connection to Freemetrics from its serverDefinition
		 * and Store the FmUser
		 */
		Properties prop = new Properties();
		
		FreemetricsKPI transfo = (FreemetricsKPI)getTransformation();
		FreemetricServer server = (FreemetricServer)transfo.getServer();
		
		
		DriverInfo dInfo = JdbcConnectionProvider.getListDriver().getInfo(((DataBaseConnection)server.getCurrentConnection(adapter)).getDriverName());
		
		String dbUrl = dInfo.getUrlPrefix() + ((DataBaseConnection)server.getCurrentConnection(adapter)).getHost() + ":" + ((DataBaseConnection)server.getCurrentConnection(adapter)).getPort() + "/" + ((DataBaseConnection)server.getCurrentConnection(adapter)).getDataBaseName();
		
		prop.setProperty("driverClassName", dInfo.getClassName());
		prop.setProperty("username", ((DataBaseConnection)server.getCurrentConnection(adapter)).getLogin());
		prop.setProperty("password", ((DataBaseConnection)server.getCurrentConnection(adapter)).getPassword());
		prop.setProperty("url", dbUrl);
		
		FactoryManager.init("", 0);
		
		fmMgr = FactoryManager.getManager();
		
		info( " Fm context loaded");
		user = fmMgr.getUserByNameAndPass(server.getFmLogin(), server.getFmPassword());
		
		if (user == null){
			throw new Exception("The FmUser informations are not valid.");
		}
		info(" FmUser identified");
		
		
		KPIList kpiTr = (KPIList)getTransformation();
		assocIds = kpiTr.getAssocIds();
		iterator = assocIds.iterator();
		
		info(" inited");
		isInited = true;
		
	}

	@Override
	public void performRow() throws Exception {
		
		if (iterator.hasNext()){
			Row row = RowFactory.createRow(this);
			Integer assId = null;
			try{
				assId = iterator.next();
				Assoc_Application_Metric ass = fmMgr.getAssoc_Territoire_MetricById(assId);
				
								
				String metname = fmMgr.getMetricById(ass.getMetr_ID()).getName();
				String appname = ass.getApplicationsName();
				
				
				
				row.set(0, metname);
				row.set(1, appname);
				row.set(2, assId);
				
				
				writeRow(row);
			}catch(Exception e){
				error(" Error for AssocId=" + assId, e);
				throw e;
			}
		}
		else{
			setEnd();
		}
		
		
		
		
		
	}

	@Override
	public void releaseResources() {
		
		
	}

}
