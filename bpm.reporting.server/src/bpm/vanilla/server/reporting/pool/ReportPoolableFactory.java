package bpm.vanilla.server.reporting.pool;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.IReportEngine;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.pool.PoolableModelFactory;
import bpm.vanilla.server.commons.pool.VanillaItemKey;

public class ReportPoolableFactory extends PoolableModelFactory{
	private IReportEngine birtEngine;
	
	public ReportPoolableFactory(Logger logger, IReportEngine birtEngine) {
		super(logger);
		this.birtEngine = birtEngine;
	}
	

	@Override
	/**
	 * if item is a FWR, return a PoolableModel<FWRReport> 
	 * 
	 */
	protected PoolableModel<?> createPoolableModel(RepositoryItem item,String modelXml, VanillaItemKey itemKey) throws Exception {
//		System.setProperty("-Duser.timezone", "Etc/GMT+2");
		//What the actual fuck is that ?????
		System.setProperty("user.timezone", "Etc/GMT+2");
		if (!item.isOn()){
			throw new VanillaException("The Report Model has been turned off rom Entreprise Services.");
		}
		PoolableModel model = null;
		if (item.getType() == IRepositoryApi.FWR_TYPE){
			model = new FwrPoolableModel(item, modelXml, itemKey, birtEngine);
		}
		else if (item.getType() == IRepositoryApi.CUST_TYPE){
			if (item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
				model = new BirtPoolableModel(item, modelXml, itemKey, birtEngine);
			}
			else if (item.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE){
				model = new JasperPoolableModel(item, modelXml, itemKey);
				
			}
		}
		
		return model;
	}

}
