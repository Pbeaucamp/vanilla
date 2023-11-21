package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.datatools.modelbase.sql.query.QuerySelect;
import org.eclipse.datatools.modelbase.sql.query.QuerySelectStatement;
import org.eclipse.datatools.modelbase.sql.query.QueryStatement;
import org.eclipse.datatools.modelbase.sql.query.ResultColumn;
import org.eclipse.datatools.modelbase.sql.query.TableExpression;
import org.eclipse.datatools.modelbase.sql.query.helper.StatementHelper;
import org.eclipse.datatools.modelbase.sql.query.impl.TableInDatabaseImpl;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManager;
import org.eclipse.datatools.sqltools.parsers.sql.query.SQLQueryParserManagerProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bpm.metadata.jdbc.driver.Column;
import bpm.metadata.jdbc.driver.FmdtDatabaseMetadata;
import bpm.metadata.jdbc.driver.Table;
import bpm.metadata.jdbc.driver.remote.QueryJDBC;
import bpm.metadata.jdbc.driver.remote.ActionJDBC;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.wrapper.VanillaCoreWrapper;
import java.io.*;


public class FmdtJdbcServlet extends HttpServlet {

	private VanillaCoreWrapper component;
	private bpm.vanilla.platform.core.repository.Repository repository;
	private Group currentGroup;
	private Repository currentRepository;
	private RemoteVanillaPlatform vanillaApi;
	private IRepository IRepository;
	private RemoteRepositoryApi socket;
	private IBusinessPackage bPack;
	private IVanillaContext vanillaContext;
	
	
	public FmdtJdbcServlet(VanillaCoreWrapper vanillaCoreWrapper) {
		component = vanillaCoreWrapper;
	}
	
	
	public void initSession(String login, String password, String groupName, int repId, int metadataId) {
		
		try {
		String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		
		
		IVanillaContext vanillaContext = new BaseVanillaContext(vanillaUrl, login, password);
		this.vanillaContext = vanillaContext;
		
		vanillaApi = new RemoteVanillaPlatform(vanillaContext);
		
		
		Group group = vanillaApi.getVanillaSecurityManager().getGroupByName(groupName);
		Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repId);
		
		this.currentRepository = rep;
		this.currentGroup = group;
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, group, rep);
		

		socket = new RemoteRepositoryApi(ctx);
		initRepository(IRepositoryApi.FMDT_TYPE);
		} catch  (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void initRepository(Integer type) {
		
		if(type == null){
			try {
				this.repository = new bpm.vanilla.platform.core.repository.Repository(socket);
			} catch (Exception e) {
				System.out.println("Error initRepository 1");
			}
		}
		else {
			try {
				this.repository = new bpm.vanilla.platform.core.repository.Repository(socket, type);
			} catch (Exception e) {
				System.out.println("Error initRepository 2");
				
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		InputStream is = req.getInputStream();
		QueryJDBC query = null;
		ActionJDBC action = null;
		
		if (is != null){
			ObjectInputStream ois = new ObjectInputStream(is);
			
			try{
				Object o = ois.readObject();
				if (o instanceof ActionJDBC){
					action = (ActionJDBC)o;
					
					switch(action.getType()){
					case 1:
						System.out.println("Essai connexion");
						
						try{
							
							initSession(action.getUser(), action.getPassword(), action.getGroupName(), action.getRepositoryId(), action.getMetadataId());
							
						} catch(Exception e) {
							e.printStackTrace();
						}
						
						break;
					case 2:
						System.out.println("Essai metadata");
						
						try{
							initSession(action.getUser(), action.getPassword(), action.getGroupName(), action.getRepositoryId(), action.getMetadataId());

							
							ArrayList<IBusinessModel> listModels = 
							metaData(action.getMetadataId(), this.currentGroup, action.getPack(), action.getModele());
							
							ArrayList<Table> metadata = convertMetadata(action.getQuery().getSql(), listModels);
							
							serializeFmdtDatabaseMetadata(new FmdtDatabaseMetadata(null, metadata), resp.getOutputStream());
						
						} catch(Exception e) {
							e.printStackTrace();
						}
						break;
					case 3:
						System.out.println("Essai requete");
						
						try{
							ArrayList<IBusinessModel> listModels =
								metaData(action.getMetadataId(), this.currentGroup, action.getPack(), action.getModele());
														
							List<IDataStreamElement> lstDSQuery = parseSQL(action.getQuery().getSql(), action.getMetadata(), listModels);
							
							ArrayList<HashMap<String, String>> lstWhere = parseWhere(action.getQuery().getSql(), action.getMetadata());
							
							List<IFilter> lstFilters = new ArrayList<IFilter>();
							
							for(int i = 0; i<lstWhere.size(); i++){
								ComplexFilter filter = new ComplexFilter();
								filter.setOrigin(findDataStreamElement(lstWhere.get(i).get("arg1"), listModels));
								if(lstWhere.get(i).get("op").equals("IN") || lstWhere.get(i).get("op").equals("NOT IN")){
									
									String[] lstArg = lstWhere.get(i).get("arg2").replace("(", "").replace(")", "").replace(" ", "").split(",");
									for(int y = 0; y<lstArg.length; y++){
										filter.setValue(lstArg[y]);
										System.out.println(filter.getValue());
									}
								} else {
									filter.setValue(lstWhere.get(i).get("arg2"));
								}
								filter.setOperator(lstWhere.get(i).get("op"));
								if(lstWhere.get(i).get("op").equals("BETWEEN")){
									filter.setValue(lstWhere.get(i).get("arg3"));
								}
								lstFilters.add(filter);
							}
							
							IQuery finalQuery = SqlQueryBuilder.getQuery(currentGroup.getName(), lstDSQuery, new HashMap<ListOfValue, String>(),
									new ArrayList(), null, lstFilters, new ArrayList());
					
				
							List<List<String>> resultQuery = this.bPack.executeQuery(currentGroup.getMaxSupportedWeightFmdt(),this.vanillaContext,
									this.bPack.getConnectionsNames(currentGroup.getName()).get(0), finalQuery, new ArrayList(new ArrayList()));
							
							serializeQueryResult(resultQuery, resp.getOutputStream());
							
						}catch(Exception e){
							e.printStackTrace();
						}
						break;
					}
				}
				else{
					throw new Exception("The received object is not an ActionJDBC");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try{
					ois.close();
				}finally{
					is.close();
				}
			}
		}	
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		doGet(req, resp);
	}
	
	public ArrayList<IBusinessModel> metaData(int metadataId, Group group, String packName, String modelName) {
		
		ArrayList<IBusinessModel> lbModels = new ArrayList<IBusinessModel>();
		
		try {
			
			bpm.vanilla.platform.core.repository.Repository iRep = this.repository;
					
			
			RepositoryItem item = iRep.getItem(metadataId);
			
			String result = socket.getRepositoryService().loadModel(item);
			InputStream input = IOUtils.toInputStream(result, "UTF-8");
			
			Collection<IBusinessModel> bmodels = bpm.metadata.MetaDataReader.read(group.getName(), input, socket, false);
			Iterator<IBusinessModel> itm = bmodels.iterator();
			while(itm.hasNext())  {
				IBusinessModel model = itm.next();
				
				if(model.getName().equals(modelName) ) {
					List<IBusinessPackage> lbPack = model.getBusinessPackages(group.getName());
					for(int i = 0; i < lbPack.size(); i++) {
						IBusinessPackage bPack = lbPack.get(i);
						this.bPack = bPack;
						
						if(bPack.getName().equals(packName) ) {
							lbModels.add(model);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lbModels;
	}
	
	private static void serializeQueryResult(List<List<String>> lstResult, OutputStream outputStream) throws Exception{
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(outputStream);
		}catch(Exception ex){
			throw new Exception("Unable to create an ObjectOutputStream " + ex.getMessage(), ex);
		}
		
		try {
			oos.writeObject(lstResult);
			oos.flush();
		}finally{
			try{
				oos.close();
			}finally{
				outputStream.close();
			}
		}
	}
	
	private static void serializeFmdtDatabaseMetadata(FmdtDatabaseMetadata metadata, OutputStream outputStream) throws Exception{
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(outputStream);
		}catch(Exception ex){
			throw new Exception("Unable to create an ObjectOutputStream " + ex.getMessage(), ex);
		}
		
		try {
			oos.writeObject(metadata);
			oos.flush();
		}finally{
			try{
				oos.close();
			}finally{
				outputStream.close();
			}
		}
	}
	
	public ArrayList<Table> convertMetadata(String sqlOriginal, ArrayList<IBusinessModel> listModels) {
		
		ArrayList<IDataStreamElement> lstDataStream = new ArrayList();
		
		ArrayList<Table> lstTables = new ArrayList<Table>();
		
		try{
			SQLQueryParserManager sqlMgr = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
			QueryStatement aggStmt = null;
			aggStmt = sqlMgr.parseQuery(sqlOriginal).getQueryStatement();
			TableExpression tabl = (TableExpression) StatementHelper.getTablesForStatement((QuerySelectStatement)aggStmt).get(0);
			QuerySelect sel = StatementHelper.getQuerySelectForTableReference(tabl);
			List listSelect = sel.getSelectClause();
			
			
			
			for(int i2 = 0; i2<listModels.size(); i2++) {
				for(int i3 = 0; i3<listModels.get(i2).getBusinessTables().size(); i3++) {
								
					Iterator<IDataStreamElement> itMod = listModels.get(i2).getBusinessTables().get(i3).getColumns(currentGroup.getName()).iterator();
					while(itMod.hasNext()) {
						IDataStreamElement dataStream  = itMod.next();
								
								
						if(!lstTables.contains(listModels.get(i2).getBusinessTables().get(i3).getName())) {
								lstTables.add(new Table(listModels.get(i2).getBusinessTables().get(i3).getName(), new ArrayList<Column>()));
						}
						for(int y = 0; y<lstTables.size(); y++) {			
//								lstTables.get(y).getColList().add(new Column(dataStream.getDefaultMeasureBehavior(),dataStream.getName(),
//										dataStream.getDescription(),dataStream.getFontName(),dataStream.getTextColor(),dataStream.getBackgroundColor(),
//										dataStream.getOriginName(),dataStream.getType(),dataStream.getVisibility(),dataStream.getGrants()));
						}
					}	
				}
			}					
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		removeDuplicates(lstTables);
		for(int i = 0; i<lstTables.size(); i++) {
			for(int y = 0; y<lstTables.get(i).getColList().size(); y++) {
				lstTables.get(i).setColList((ArrayList<Column>) removeDuplicates(lstTables.get(i).getColList()));
			}
		}

		return lstTables;
	}
	
	public List<IDataStreamElement> parseSQL(String sqlOriginal, FmdtDatabaseMetadata metadata, ArrayList<IBusinessModel> lstModels) {
		
		List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		List<IDataStreamElement> lstDataStream = new ArrayList<IDataStreamElement>();
		
		try{
			SQLQueryParserManager sqlMgr = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
			QueryStatement aggStmt = null;
			aggStmt = sqlMgr.parseQuery(sqlOriginal).getQueryStatement();
			TableExpression tabl = (TableExpression) StatementHelper.getTablesForStatement((QuerySelectStatement)aggStmt).get(0);
			QuerySelect sel = StatementHelper.getQuerySelectForTableReference(tabl);	
			List lstSelect = sel.getSelectClause();
			ArrayList<Table> lstTables = metadata.getTables();
			

			//Occurs with a "SELECT *" query
			if(lstSelect.size() == 0) {
				for(int i2 = 0; i2<lstModels.size(); i2++) {
					for(int i3 = 0; i3<lstModels.get(i2).getBusinessTables().size(); i3++) {
						Iterator<IDataStreamElement> itMod = lstModels.get(i2).getBusinessTables().get(i3).getColumns(currentGroup.getName()).iterator();
						while(itMod.hasNext()) {
							IDataStreamElement dataStream  = itMod.next();
							if((dataStream.getOriginName().toUpperCase().startsWith(lstModels.get(i2).getBusinessTables().get(i3).getName().toUpperCase()))) {	
								for(int i4 = 0; i4<metadata.getTables().size(); i4++){
									for(int i5 = 0; i5<metadata.getTables().get(i4).getColList().size(); i5++){
										if(metadata.getTables().get(i4).getName().toUpperCase().equals(tabl.getName().toUpperCase())){
											if(metadata.getTables().get(i4).getColList().get(i5).getName().toUpperCase().equals(dataStream.getName().toUpperCase())){
													lstDataStream.add(dataStream);
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				for(int i = 0; i<sel.getFromClause().size(); i++){
					if(sel.getFromClause().get(i) instanceof TableInDatabaseImpl) {
						TableInDatabaseImpl table = (TableInDatabaseImpl) sel.getFromClause().get(i);
						for(int y = 0; y<lstSelect.size(); y++){
							ResultColumn col = (ResultColumn) lstSelect.get(y);
							IDataStreamElement dataStream = findDataStreamElement(col.getSQL(), lstModels);
							if(dataStream != null){
								if(dataStream.getDataStream().getOriginName().toUpperCase().equals(table.getName().toUpperCase())){
									lstDataStream.add(dataStream);
								}
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		lstDataStream = (List<IDataStreamElement>) removeDuplicates(lstDataStream);

		return lstDataStream;
	}
	
	
	public static ArrayList<?> removeDuplicates(List list)  {  
		ArrayList resultList = new ArrayList();
	    Set uniqueEntries = new HashSet();
	    for (Iterator iter = list.iterator(); iter.hasNext(); ) {  
	      Object element = iter.next(); 
	      if (!uniqueEntries.add(element))
	        iter.remove();
	    }
	    resultList = (ArrayList) list;
	    return resultList;
	}
	
	public void getConditions(IDataStreamElement dataStreamE){
		
		
	}
	
	public ArrayList<HashMap<String, String>> parseWhere(String sql, FmdtDatabaseMetadata metadata){
		
		ArrayList<HashMap<String, String>> lstWhere = new ArrayList<HashMap<String,String>>();
		
		try{
			SQLQueryParserManager sqlMgr = SQLQueryParserManagerProvider.getInstance().getParserManager(null, null);
			QueryStatement aggStmt = null;
			aggStmt = sqlMgr.parseQuery(sql).getQueryStatement();
			TableExpression tabl = (TableExpression) StatementHelper.getTablesForStatement((QuerySelectStatement)aggStmt).get(0);
			QuerySelect sel = StatementHelper.getQuerySelectForTableReference(tabl);
			
			String operator;
			boolean argNull = false;
			boolean argNotNull = false;
			boolean argBetween = false;
			if(sel.getWhereClause() != null){
			String whereSql = sel.getWhereClause().getSQL();
			int i = 0;
			
			if(whereSql.toUpperCase().contains("BETWEEN")){
				argBetween = true;
				ArrayList<String> lstBetween = findBetween(whereSql);
				for(int y = 0; y<lstBetween.size(); y++){
					whereSql = whereSql.replace(lstBetween.get(y), "").trim();
					lstWhere.add(new HashMap<String,String>());
					
					String[] lstArg = lstBetween.get(y).split("BETWEEN");
					lstWhere.get(i).put("op", "BETWEEN");
					lstWhere.get(i).put("arg1", findColumn(lstArg[0], metadata));
					
					lstArg = lstArg[1].split("AND");
					lstWhere.get(i).put("arg2", findColumn(lstArg[0], metadata));
					lstWhere.get(i).put("arg3", findColumn(lstArg[1], metadata));
					i++;
				}				
				if(whereSql.startsWith("AND")){
					whereSql = whereSql.replace("AND", "").trim();
					argBetween = false;
				}
			}
			
			if(whereSql.toUpperCase().contains("AND")){
				String[] andStr = whereSql.toUpperCase().split("AND");
					
				for(int y = 0; y<andStr.length; y++){
					andStr[y] = andStr[y].trim();
					if (andStr[y].toUpperCase().contains(">=")){
						operator = ">=";
					} else if(andStr[y].toUpperCase().contains("<=")){
						operator = "<=";
					} else if(andStr[y].toUpperCase().contains("<")) {
						operator = "<";
					} else if (andStr[y].toUpperCase().contains(">")){
						operator = ">";
					} else if(andStr[y].toUpperCase().contains("IS NOT NULL")){
						argNotNull = true;
						operator = " IS";
					} else if(andStr[y].toUpperCase().contains("IS NULL")){
						argNull = true;
						operator = " IS";
					} else if(andStr[y].toUpperCase().contains("!=")){
						operator = "!=";
					} else if (andStr[y].toUpperCase().contains("NOT LIKE")){
						operator = " NOT LIKE";
					} else if (andStr[y].toUpperCase().contains("LIKE")){
						operator = "LIKE";
					} else {
						operator = "=";
					}
					lstWhere.add(new HashMap<String,String>());
					String[] splitStr = andStr[y].toUpperCase().split(operator);
						lstWhere.get(i).put("arg1", findColumn(splitStr[0], metadata));
						if(splitStr.length > 1) {
							if(argNull){
								lstWhere.get(i).put("arg2", " NULL ");
								argNull = false;
							} else if (argNotNull){
								lstWhere.get(i).put("arg2", " NOT NULL ");
								argNotNull = false;
							} else if(operator.equals("LIKE") || operator.equals(" NOT LIKE")) {
								String arg2 = findColumn(splitStr[1], metadata);
								arg2 = arg2.replaceAll("^'|'$", "");
								lstWhere.get(i).put("arg2", arg2);
							} else {
								lstWhere.get(i).put("arg2", findColumn(splitStr[1], metadata));
							}
						}
						lstWhere.get(i).put("op", operator);
						i++;
					}
				} else if (!argBetween){
					if (whereSql.toUpperCase().contains(">=")){
						operator = ">=";
					} else if(whereSql.toUpperCase().contains("<=")){
						operator = "<=";
					} else if(whereSql.toUpperCase().contains("<")) {
						operator = "<";
					} else if (whereSql.toUpperCase().contains(">")){
						operator = ">";
					} else if(whereSql.toUpperCase().contains("IS NOT NULL")){
						argNotNull = true;
						operator = " IS ";
					} else if(whereSql.toUpperCase().contains("IS NULL")){
						argNull = true;
						operator = " IS ";
					} else if(whereSql.toUpperCase().contains("!=")){
						operator = "!=";
					} else if (whereSql.toUpperCase().contains("NOT LIKE")){
						operator = " NOT LIKE";
					} else if (whereSql.toUpperCase().contains("LIKE")){
						operator = "LIKE";
					} else if (whereSql.toUpperCase().contains("IN")){
						operator = "IN";
					} else if (whereSql.toUpperCase().contains("NOT IN")){
						operator = "NOT IN";
					} else {
						operator = "=";
					}
					
					String[] splitStr = whereSql.toUpperCase().split(operator);
					lstWhere.add(new HashMap<String,String>());
					lstWhere.get(i).put("arg1", findColumn(splitStr[0], metadata));
					if(splitStr.length > 1) {
						if(argNull){
							lstWhere.get(i).put("arg2", "NULL");
							argNull = false;
						} else if (argNotNull){
							lstWhere.get(i).put("arg2", "NOT NULL");
							argNotNull = false;
						} else if(operator.equals("LIKE") || operator.equals(" NOT LIKE")) {
							String arg2 = findColumn(splitStr[1], metadata);
							arg2 = arg2.replaceAll("^'|'$", "");
							lstWhere.get(i).put("arg2", arg2);
						} else {
							lstWhere.get(i).put("arg2", findColumn(splitStr[1], metadata));
						}
					}
					lstWhere.get(i).put("op", operator);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return lstWhere;
	}
	
	public String findColumn(String column, FmdtDatabaseMetadata metadata){
		String result = null;
		column = column.trim();
		for(int i = 0; i<metadata.getTables().size(); i++){
			for(int y = 0; y<metadata.getTables().size(); y++){
				if(metadata.getTables().get(i).getColList().get(y).getOriginName().toUpperCase().endsWith(column.toUpperCase())){
					
					result = metadata.getTables().get(i).getColList().get(y).getOriginName();
				}
			}
		}
		if(result == null){
			result = column;
		}
		return result;
	}
	
	public IDataStreamElement findDataStreamElement(String column, ArrayList<IBusinessModel> metadata){
		IDataStreamElement result = null;
		column = column.trim();
		for(int i = 0; i<metadata.size(); i++){
			for(int y = 0; y<metadata.get(i).getBusinessTables().size(); y++){
				Iterator itModele = metadata.get(i).getBusinessTables().get(y).getColumns(this.currentGroup.getName()).iterator();
				while(itModele.hasNext()){
					IDataStreamElement dataStream = (IDataStreamElement) itModele.next();
					if(dataStream.getOriginName().toUpperCase().endsWith(column.toUpperCase())){
						result = dataStream;
					}
				}
			}
		}
		return result;
	}
	
	public ArrayList<String> findBetween(String whereSql){
		String[] lstWhere = whereSql.split("AND");
		ArrayList<String> lstBetween = new ArrayList<String>();
		for(int i = 0; i<lstWhere.length; i++){
			if(lstWhere[i].contains("BETWEEN")){
				lstBetween.add(lstWhere[i].trim() + " AND " + lstWhere[i+1].trim());
			}
		}
		return lstBetween;
	}
}