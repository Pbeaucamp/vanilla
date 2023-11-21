package bpm.es.datasource.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class Auditer {

	
	public HashMap<AuditConfig, List<AuditResult>> audit(IRepositoryContext ctx, List<AuditConfig> configs){
		Logger.getLogger(getClass()).info("Launch Audit");
		
		
		
		HashMap<AuditConfig, List<AuditResult>> results = new HashMap<AuditConfig, List<AuditResult>>();
		
		for(AuditConfig conf : configs){
			Logger.getLogger(getClass()).info("******");
			Logger.getLogger(getClass()).info("Item = " + conf.getAnalyzedItem().getItemName());
			Logger.getLogger(getClass()).info("Group = " + (conf.getGroup() == null ? "null" : conf.getGroup().getName()));
			
			results.put(conf, new ArrayList<AuditResult>());
			
			List<OdaInput> dataSets = null;
			try{
				dataSets = conf.getAnalyzer().extractDataSets(conf.getXml());
				Logger.getLogger(getClass()).info("Query Numbers = " + dataSets.size());
			}catch(Exception ex){
				Logger.getLogger(getClass()).error("Failed to extract DataSets - " + ex.getMessage(), ex);
				continue;
			}
			
			for(OdaInput i : dataSets){
				
				i.overrideVanillaProperties(ctx.getRepository().getUrl(), conf.getGroup().getName(), ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword());
				
				IQuery q = null;
				  try{
					  q = QueryHelper.buildquery(i);
					  Logger.getLogger(getClass()).info("IQuery built successfully");
					  IResultSet rs = null;
					  
					  IParameterMetaData pmd = q.getParameterMetaData();
					  if (pmd.getParameterCount() > 0){
						  Logger.getLogger(getClass()).info("Query as Parameters");
						  
						  for(int k = 0; k < pmd.getParameterCount(); k++){
							  q.setString(k+1, "''");
						  }
						  
					  }
					  
					  
					  try{
						  
						  rs = q.executeQuery();
						  Logger.getLogger(getClass()).info("IQuery executed successfully");
						  results.get(conf).add(new AuditResult(true, q.getEffectiveQueryText(), i.getName(), null));
					  }catch(Exception ex){
						  String s = null;
						  try{
							  s = q.getEffectiveQueryText();
						  }catch(Exception e){
							  s = "Could not generate EffectiveQuery";
						  }
						  Logger.getLogger(getClass()).info("IQuery execution failed " + i.getQueryText(), ex);
						  results.get(conf).add(new AuditResult(true, s, i.getName(), ex.getMessage()));
					  }finally{
						  rs.close();
					  }
					  
				  }catch(Exception ex){
					  Logger.getLogger(getClass()).error("Failed to build IQUery " + i.getQueryText(), ex);
					  results.get(conf).add(new AuditResult(true, "coud not generate Query", i.getName(), ex.getMessage()));
				  }finally{
					  if (q != null){
						  try{
							  q.close();
							  QueryHelper.removeQuery(q);
							  QueryHelper.closeConnectionFor(q);
						  }catch(Exception ex){}
						  finally{
							  Logger.getLogger(getClass()).info("IQuery Released"); 
						  }

					  }
				  }
			}
			
			
			
		}
		return results;
	}
}
