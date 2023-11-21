package bpm.gateway.core.transformations.inputs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.metadata.MetaDataReader;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.query.IQuery;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FMDTHelper {
	
	private static HashMap<String, HashMap<Integer, Collection<IBusinessModel>>> backupReaded = new HashMap<String, HashMap<Integer, Collection<IBusinessModel>>>();
	
	
	private IRepositoryContext repContext;
	
	public FMDTHelper( IRepositoryContext repContext){
		this.repContext = repContext;
	}
	
	private IRepositoryApi createSock() throws Exception{
		if (repContext == null){
			throw new Exception("Cannot use Metadata without a IRepositoryContext");
		}
		
		IRepositoryApi sock = new RemoteRepositoryApi(repContext);
		
		return sock;
	}
	
	private void createVanillaApi() throws Exception{
		if (repContext == null){
			throw new Exception("Cannot use Metadata without a IRepositoryContext");
		}
		
//		IVanillaContext ctx = new BaseVanillaContext(repContext.getVanillaContext().getVanillaUrl(), 
//				repContext.getVanillaContext().getLogin(), repContext.getVanillaContext().getPassword());
//		return new RemoteVanillaPlatform(ctx);
	}
	
	public Collection<IBusinessModel> getFmdtModel(FMDTInput input)throws Exception {
		return getFmdtModel(input.getRepositoryItemId());
	}
	
	public Collection<IBusinessModel> getFmdtModel(Integer repositoryItemId)throws Exception{
		/*
		 * load the model
		 */		

		IRepositoryApi sock =  createSock();
		createVanillaApi();
		IObjectIdentifier id = new ObjectIdentifier(repContext.getRepository().getId(), repositoryItemId);
		
		
		
		if (backupReaded.get(getIdentifierKey(id)) == null){
			backupReaded.put(getIdentifierKey(id), new HashMap<Integer, Collection<IBusinessModel>>());
		}
		
		if (backupReaded.get(getIdentifierKey(id)).get(repositoryItemId) == null){
			RepositoryItem directoryItem = sock.getRepositoryService().getDirectoryItem(repositoryItemId);
			String fmdtModelXml = null;
			try {
				fmdtModelXml = sock.getRepositoryService().loadModel(directoryItem);
			} catch (Exception e) {
				throw new Exception("Error when loading Metadata from Repository - " + e.getMessage(), e);
			}
			
			
			/*
			 * parse the model
			 */

			//TODO : replace the groupname by a real one
			Collection<IBusinessModel> businessModels = null;
			try {
				businessModels = MetaDataReader.read(repContext.getGroup().getName(), IOUtils.toInputStream(fmdtModelXml, "UTF-8"), sock, false);
				backupReaded.get(getIdentifierKey(id)).put(repositoryItemId, businessModels);
			} catch (Exception e) {
				throw new Exception("Error when parsing Metadata model - " + e.getMessage(), e);
			}
			
			return businessModels;

		}
		else{
			return backupReaded.get(getIdentifierKey(id)).get(repositoryItemId);
		}
		
	}
	
	private String getIdentifierKey(IObjectIdentifier obj){
		return obj.getRepositoryId() + "-" + obj.getDirectoryItemId();
	}
	
	public StreamDescriptor getDescriptor(FMDTInput transfo) throws Exception{
		Collection<IBusinessModel> businessModels = getFmdtModel(transfo);

		IQuery querySql = null;
		
		for(IBusinessModel m : businessModels){
			if (m.getName().equals(transfo.getBusinessModelName())){
				
				//TODO : replace the groupname by a real one
				for(IBusinessPackage p : m.getBusinessPackages(repContext.getGroup().getName())){
					if (p.getName().equals(transfo.getBusinessPackageName())){
						
						try {
							//TODO : replace the groupname by a real one
							SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(transfo.getDefinition()), repContext.getGroup().getName() , p);
							if(p instanceof UnitedOlapBusinessPackage) {
								querySql = dig.getUOlapModel();
							}
							else {
								querySql = dig.getModel();
							}
							
							((FMDTInput)transfo).setQueryFmdt(querySql);
							
						} catch (Exception e) {
							throw new Exception("Unable to parse Query-" + e.getMessage(), e);
						}
						break;
					}
				}
			}
		}
		
		if (querySql == null){
			throw new Exception("Unable to find specifed BusinessModel nor BusinessPackage in Metadata model");
		}
		
		
		/*
		 * build the Descriptor by looking the query
		 */
		
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		
		for(IDataStreamElement col : querySql.getSelect()){
			StreamElement se = new StreamElement();
			
			IColumn sqlCol = col.getOrigin();
			
			se.name = sqlCol.getName();
			try {
				se.className = sqlCol.getJavaClass().getName();
			} catch (Exception e) {
				se.className = "java.lang.Object";
			}
			se.isNullable = true;
			se.originTransfo = transfo.getName();
			se.tableName = sqlCol.getTable().getName();
			if(sqlCol instanceof SQLColumn) {
				se.typeName = ((SQLColumn)sqlCol).getSqlType();
				se.type = ((SQLColumn)sqlCol).getSqlTypeCode();
			}
			
			
			desc.addColumn(se);
		}
		
		//TODO : add Aggregates
		
		return desc;
	}
	
	public List<List<Object>> getCountDistinctFieldValues(Collection<IBusinessPackage> fmdtPackages, List<StreamElement> cols, 
			StreamElement field, FMDTInput transfo) throws Exception {

		IBusinessPackage pack = null;

		try {
			for (IBusinessPackage p : fmdtPackages) {
				if (p.getName().equals(transfo.getBusinessPackageName())) {
					pack = p;
					break;
				}
			}

			IQuery query = transfo.getQueryFmdt();

			// TODO: apply parameters values
			List<List<String>> promptValues = new ArrayList<List<String>>();

			for (Prompt p : query.getPrompts()) {
				List<String> v = new ArrayList<String>();
				for (String s : transfo.getPromptValues(p.getName())) {
					v.add(transfo.getDocument().getStringParser().getValue(transfo.getDocument(), s));
				}

				promptValues.add(v);

			}

			List<List<Object>> fmdtValues = (List) pack.executeQuery(null, null, transfo.getConnectioName(), query, promptValues);
			
			int colPos = -1;
			for(int i=0; i<cols.size(); i++) {
				if(cols.get(i) == field) {
					colPos = i;
					break;
				}
			}
			
			List<List<Object>> values = new ArrayList<List<Object>>();
			if(colPos != -1) {
				for(int i = 0; i < fmdtValues.size(); i++){
					Object c = fmdtValues.get(i).get(colPos);
					
					boolean present = false;
					for(List<Object> l : values){
						if (l.get(0).equals(c)){
							l.set(1, (Integer)l.get(1) + 1);
							present = true;
							break;
						}
					}
					
					if (!present){
						List<Object> l = new ArrayList<Object>();
						l.add(c);
						l.add(1);
						values.add(l);
					}
				}
			}

			return values;
		} catch(Exception e) {
			throw e;
		}

	}
}
