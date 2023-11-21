package bpm.metadata.layer.physical.olap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoaderFactory;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.repository.FaApiHelper;
import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.metadata.query.UnitedOlapQueryGenerator;
import bpm.metadata.query.UnitedOlapResultHelper;
import bpm.metadata.resource.Prompt;
import bpm.metadata.tools.Log;
import bpm.united.olap.api.result.OlapResult;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class UnitedOlapConnection implements IConnection {

	private IObjectIdentifier identifier;
	private String cubeName;
	private IRuntimeContext runtimeContext;
	private FaApiHelper helper;
	private String name;
	private List<ITable> tables;
	private OLAPCube cube;
	
	/**
	 * Forbidden, for digester
	 */
	public UnitedOlapConnection(){}
	
	public UnitedOlapConnection(String name, IObjectIdentifier identifier, String cubeName, IRuntimeContext runtimeContext, FaApiHelper helper) {
		
		this.name = name;
		this.identifier = identifier;
		this.cubeName = cubeName;
		this.runtimeContext = runtimeContext;
		
		this.helper = helper;
		
	}
	
	@Override
	public List<ITable> connect() throws Exception {
		
		if(helper == null) {
			String url = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
			helper = new FaApiHelper(url, UnitedOlapLoaderFactory.getLoader());
		}
		Collection<String> names = helper.getCubeNames(identifier, runtimeContext);
		for(String name : names) {
			if(name.equals(cubeName)) {
				cube = helper.getCube(identifier, runtimeContext, cubeName);
				return createOlapTables(cube);
			}
		}
		
		throw new Exception("Impossible to load cube with name : " + cubeName + " . Maybe the name was changed since last connection.");
	}

	private List<ITable> createOlapTables(OLAPCube cube) {
		
		tables = new ArrayList<ITable>();
		for(Dimension dim : cube.getDimensions()) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				UnitedOlapTable table = new UnitedOlapTable(hiera.getUniqueName(), this);
				tables.add(table);
			}
		}
		UnitedOlapTable table = new UnitedOlapTable(UnitedOlapTable.MEASURE_TABLE_NAME, this);
		tables.add(table);
		
		return tables;
	}

	
	@Override
	public List<List<String>> executeQuery(String query, Integer maxRows, boolean[] flags) throws Exception {
		return UnitedOlapServiceProvider.getInstance().getModelService().exploreDimension(query, cube.getSchemaId(), cubeName, runtimeContext);
	}
	
	@Override
	public Integer countQuery(String query, Integer maxRows) throws Exception {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public VanillaPreparedStatement getStreamStatement() throws Exception {
		
		return null;
	}

	@Override
	public ITable getTable(String name) throws Exception {
		if(tables == null || tables.isEmpty()) {
			connect();
		}
		for(ITable table : tables) {
			if(table.getName().equals(name)) {
				return table;
			}
		}
		return null;
	}

	@Override
	public String getXml() {
		StringBuilder buf = new StringBuilder();
		buf.append("		<unitedOlapConnection>\n");
		buf.append("			<name>" + name + "</name>\n");
		buf.append("			<identifier>\n");
		buf.append("				<fasdid>" + identifier.getDirectoryItemId() + "</fasdid>\n");
		buf.append("				<repositoryid>" + identifier.getRepositoryId() + "</repositoryid>\n");
		buf.append("			</identifier>\n");
		buf.append("			<cubename>" + cubeName + "</cubename>\n");
		buf.append("			<runtimeContext>\n");
		buf.append("				<user>" + runtimeContext.getLogin() + "</user>\n");
		buf.append("				<password>" + runtimeContext.getPassword() + "</password>\n");
		buf.append("				<groupname>" + runtimeContext.getGroupName() + "</groupname>\n");
		buf.append("				<groupid>" + runtimeContext.getGroupId() + "</groupid>\n");
		buf.append("			</runtimeContext>\n");
		buf.append("		</unitedOlapConnection>\n");
		return buf.toString();
	}

	@Override
	public void test() throws Exception {
//		if(tables == null) {
//			connect();
//		}
	}

	public IObjectIdentifier getIdentifier() {
		return identifier;
	}

	public List<IColumn> getColumns(UnitedOlapTable unitedOlapTable) throws Exception {
		
		if(cube == null) {
			connect();
		}
		
		List<IColumn> columns = new ArrayList<IColumn>();
		if(unitedOlapTable.getName().equals(UnitedOlapTable.MEASURE_TABLE_NAME)) {
			for(MeasureGroup grp : cube.getMeasures()) {
				for(Measure mes : grp.getMeasures()) {
					UnitedOlapMeasureColumn col = new UnitedOlapMeasureColumn(unitedOlapTable, mes);
					columns.add(col);
				}
			}
		}
		else {
			for(Dimension dim : cube.getDimensions()) {
				for(Hierarchy hiera : dim.getHierarchies()) {
					if(hiera.getUniqueName().equals(unitedOlapTable.getName())) {
						for(Level lvl : hiera.getLevel()) {
							UnitedOlapLevelColumn col = new UnitedOlapLevelColumn(unitedOlapTable, lvl);
							columns.add(col);
						}
					}
				}
			}
		}
		
		return columns;
	}

	public ITable createFromUniqueName(String originName) throws Exception {
		if(tables == null || tables.isEmpty()) {
			connect();
		}
		
		for(ITable table : tables) {
			if(table.getName().equals(originName)) {
				return table;
			}
		}
		
		return null;
	}
	
	public String getCubeName() {
		return cubeName;
	}

	public List<ITable> getTables() {
		if(tables != null || tables.isEmpty()) {
			try {
				connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tables;
	}
	
	public void setRuntimeContext(String user, String password, String groupName, String groupId) {
		runtimeContext = new RuntimeContext(user, password, groupName, Integer.parseInt(groupId));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}
	
	public void setIdentifier(String repId, String itemId) {
		identifier = new ObjectIdentifier(Integer.parseInt(repId), Integer.parseInt(itemId));
	}
	
	public OLAPCube getCube() throws Exception {
		if(cube == null) {
			connect();
		}
		return cube;
	}

	public List<List<String>> executeQuery(UnitedOlapQuery query, int maxRows, IVanillaContext vanillaCtx, List<List<String>> promptsValues, UnitedOlapBusinessPackage unitedOlapBusinessPackage) throws Exception {
		
		UnitedOlapQuery q = (UnitedOlapQuery) query;
		if (promptsValues != null && promptsValues.size() != q.getPrompts().size()){
			throw new Exception("The prompt values havent the same size than the number of Prompt in the query");
		}
		
		
		HashMap<Prompt, List<String>> values = new HashMap<Prompt, List<String>>();
		
		for(int i = 0; i< q.getPrompts().size(); i++){
			values.put(q.getPrompts().get(i), promptsValues.get(i));
		}
		
		String queryMdx = UnitedOlapQueryGenerator.getQuery(null, vanillaCtx, unitedOlapBusinessPackage, query, query.getGroupName(), false, values).getGeneratedQuery();
		
		Date start = new Date();
		Log.getLogger().debug("Call UOlap service started");
		
		IRuntimeContext actualCtx = null;
		
		if(vanillaCtx != null) {
			int groupId = getVanillaApi(vanillaCtx).getVanillaSecurityManager().getGroupByName(query.getGroupName()).getId();
			
			actualCtx = new RuntimeContext(vanillaCtx.getLogin(), vanillaCtx.getPassword(), query.getGroupName(), groupId);
		}
		else {
			actualCtx = runtimeContext;
		}
		
		OlapResult res = UnitedOlapServiceProvider.getInstance().getRuntimeService().executeQuery(queryMdx, cube.getSchemaId(), cubeName,maxRows, true, actualCtx);
		
		Log.getLogger().debug("Call UOlap service finished in : " + (new Date().getTime() - start.getTime()) + " ms");
		
		return UnitedOlapResultHelper.createFmdtResultFromOlapResultAsString(res, query);
	}

	public IRuntimeContext getRuntimeContext() {
		return runtimeContext;
	}
	
	public List<Relation> getRelations() throws Exception {
		if(cube == null) {
			connect();
		}
		
//		List<bpm.united.olap.api.datasource.Relation> relations = cube.getRelations();
		
		List<Relation> result = new ArrayList<Relation>();
		
//		for(bpm.united.olap.api.datasource.Relation rel : relations) {
//			
//		}
		
		return result;
	}
	
	public IVanillaAPI getVanillaApi(IVanillaContext ctx) {
		IVanillaAPI api = new RemoteVanillaPlatform(ctx);
		return api;
	}

	@Override
	public void configure(Object conf) {
		if (conf == null){
			helper = null;
		}
		else if (conf instanceof FaApiHelper){
			helper = (FaApiHelper)conf;
		}
		
		

		
	}

	@Override
	public ITable getTableByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
