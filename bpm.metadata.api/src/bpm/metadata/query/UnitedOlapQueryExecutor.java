package bpm.metadata.query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.resource.Prompt;
import bpm.metadata.tools.Log;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class UnitedOlapQueryExecutor implements IQueryExecutor{

	private UnitedOlapBusinessPackage pack;

	public UnitedOlapQueryExecutor(UnitedOlapBusinessPackage pack){
		this.pack = pack;
	}
	
	@Override
	public List<List<Object>> execute(IVanillaContext context, IQuery query,
			String connectionName, List<List<String>> promptsValues)
			throws Exception {
		
		
		
		UnitedOlapQuery q = (UnitedOlapQuery) query;
		if (promptsValues != null && promptsValues.size() != q.getPrompts().size()){
			throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
		}
		UnitedOlapConnection conn = (UnitedOlapConnection)pack.getConnection(q.getGroupName(), connectionName);
		
		HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
		
		for(int i = 0; i< q.getPrompts().size(); i++){
			values.put(q.getPrompts().get(i), promptsValues.get(i));
		}
		
		String queryMdx = UnitedOlapQueryGenerator.getQuery(null, 
				context, pack, q, 
				q.getGroupName(), false, values).getGeneratedQuery();
		
		Date start = new Date();
		Log.getLogger().debug("Call UOlap service started");
		
		
		int groupId = getVanillaApi(context).getVanillaSecurityManager().getGroupByName(q.getGroupName()).getId();
		IRuntimeContext actualCtx = new RuntimeContext(context.getLogin(), context.getPassword(), q.getGroupName(), groupId);
		

		
		
		OlapResult res = UnitedOlapServiceProvider.getInstance().getRuntimeService().executeQueryForFmdt(
				queryMdx, 
				conn.getCube().getSchemaId(), 
				conn.getCubeName(), true, actualCtx);
		
		Log.getLogger().debug("Call UOlap service finished in : " + (new Date().getTime() - start.getTime()) + " ms");
		
		return UnitedOlapResultHelper.createFmdtResultFromOlapResult(res, q);

	}

	private IVanillaAPI getVanillaApi(IVanillaContext ctx) {
		IVanillaAPI api = new RemoteVanillaPlatform(ctx);
		return api;
	}
}
