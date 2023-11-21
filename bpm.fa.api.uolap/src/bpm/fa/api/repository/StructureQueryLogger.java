package bpm.fa.api.repository;

import java.util.Date;

import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.UOlapQueryBean;

public class StructureQueryLogger {

	private IVanillaAPI vanillaApi;
	private IObjectIdentifier identifier;
	private IRuntimeContext ctx;
	
	public StructureQueryLogger(IVanillaAPI vanillaApi, IObjectIdentifier identifier, IRuntimeContext ctx){
		this.ctx = ctx;
		this.identifier = identifier;
		this.vanillaApi = vanillaApi;
	}
	public boolean storeMdxQuery(String mdx, Date executionDate, long executionTime) throws Exception{
		UOlapQueryBean bean = new UOlapQueryBean();
		bean.setDirectoryItemId(identifier.getDirectoryItemId());
		bean.setExecutionDate(executionDate);
		bean.setExecutionTime(executionTime);
		bean.setMdxQuery(mdx);
		bean.setRepositoryId(identifier.getRepositoryId());
		bean.setVanillaGroupId(ctx.getGroupId());
		
		return vanillaApi.getVanillaLoggingManager().addUolapQuery(bean);
	}

}
