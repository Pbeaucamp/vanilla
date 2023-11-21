package bpm.workflow.runtime.model.activities;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.CDATA;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.databases.DataBaseHelper;
import bpm.workflow.runtime.databases.JdbcConnectionProvider;
import bpm.workflow.runtime.databases.StreamElement;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IDataBaseServer;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;

/**
 * Execute a SQL Query
 * @author CHARBONNIER, MARTIN
 *
 */
public class SqlActivity extends AbstractActivity implements IDataBaseServer,IComment,IConditionnable {
	
	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	
	private String comment;
	private DataBaseServer server;
	private String query;
	private static int number = 0;
	
	/**
	 * This property containt the mapping between the fields define in the query and the variable to fill
	 * key are used to store sql fields
	 * value are used to store variable
	 */
	private HashMap<String, Variable> mapping = new LinkedHashMap<String, Variable>();
	private String iterationCond = "no";
	
	
	/**
	 * 
	 * @return if you have to loop on the resultset ("yes" or "no")
	 */
	public String getIterationCond() {
		return iterationCond;
	}



	/**
	 * Set if you have to loop on the resultset ("yes" or "no")
	 * @param iterationCond
	 */
	public void setIterationCond(String iterationCond) {
		this.iterationCond = iterationCond;
	}

	public SqlActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_endIteration");
		varSucceed.setType(0);
		
		number++;
	}
	
	public SqlActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_endIteration");
		varSucceed.setType(0);
	}
	
	public String getSuccessVariableSuffix() {
		return "_endIteration";
	}



	/**
	 * 
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Set the query
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	public void setServer(String name){
		server = (DataBaseServer) ListServer.getInstance().getServer(name);
	}

	public IResource getServer() {
		return server;
	}

	public Class<?> getServerClass() {
		return DataBaseServer.class;
	}

	public void setServer(IResource server) {
		this.server = (DataBaseServer) server;
	}

	public IActivity copy() {
		SqlActivity a = new SqlActivity();
		a.setName("copy of " + name);
		if (server != null)
			a.setServer(server);
		if (query != null)
			a.setQuery(query);
		for (String sqlfield : mapping.keySet()) {
			a.linkFieldToVariable(sqlfield, mapping.get(sqlfield));
		}
		
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("sqlActivity");
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (server != null){
			e.addElement("serverRefName").setText(server.getName());
		}
		if (query != null) {
			CDATA c = DocumentHelper.createCDATA(query);
			e.addElement("query").add(c);
		}
		
		if (!mapping.isEmpty()) {
			Element links = e.addElement("links");
			for (String sqlfield : mapping.keySet()) {
				Element link = links.addElement("link");
				link.addElement("sqlfield").setText(sqlfield);
				link.addElement("variable").setText(mapping.get(sqlfield).getName());
			}
		}
		

		
		return e;
	}


	
	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);
				
		return listeVar;
	}
	
	
	/**
	 * Create a link between a SQL's field and a variable
	 * @param sqlfield
	 * @param var
	 */
	public void linkFieldToVariable(String sqlfield, Variable var) {
		this.mapping.put(sqlfield, var);
	}

	/**
	 * Create a link between a SQL's field and a variable
	 * Used in digester
	 * @param sqlfield
	 * @param variablename
	 */
	public void linkFieldToVariable(String sqlfield, String variablename) {
		if (ListVariable.getInstance().getVariable(variablename) == null){
			Variable v = new Variable();
			v.setName(variablename);
			v.setType(Variable.STRING);
			this.mapping.put(sqlfield, v);
		}
		else{
			this.mapping.put(sqlfield, ListVariable.getInstance().getVariable(variablename));
		}
		
	}
	
	public void deleteLink(String sqlfield) {
		this.mapping.remove(sqlfield);
	}
	
	public Variable getVariableFillByField(String sqlfield) {
		return mapping.get(sqlfield);
	}
	
	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (server == null) {
			buf.append("For activity " + name + ", the server is not set.\n");
		}
		if (query == null || query.equals("")) {
			buf.append("For activity " + name + ", the query is not set.\n");
		}
			
		return buf.toString();
	}


	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	public HashMap<String, Variable> getMapping() {
		return mapping;
	}
	
	public void setMapping(HashMap<String, Variable> mapping) {
		this.mapping = mapping;
	}
	
	public void decreaseNumber() {
		number--;
	}



	public void updateMapping() throws Exception {
		List<StreamElement> fields = DataBaseHelper.getDescriptor((DataBaseServer) this.getServer(), this.getQuery());
		List<String> names = new ArrayList<String>();
		
		for(StreamElement elem : fields) {
			names.add(elem.name);
		}
		
		for(StreamElement elem : fields) {
			if(!mapping.containsKey(elem.name)) {
				String nameToUse = null;
				for(String name : mapping.keySet()) {
					if(!names.contains(name)) {
						nameToUse=name;
						break;
					}
				}
				if(nameToUse != null) {
					Variable v = mapping.get(nameToUse);
					mapping.remove(nameToUse);
					mapping.put(elem.name, v);
				}
			}
		}
		
	}

	private int currentRow = 0;

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			try {
				String parsedQuery = query;
				if(query.contains("{$")){
					parsedQuery = workflowInstance.parseString(parsedQuery);
				}
				
				VanillaJdbcConnection connection = JdbcConnectionProvider.createSqlConnection(server.getJdbcDriver(), server.getUrl(), server.getPort(), server.getDataBaseName(), server.getLogin(), server.getPassword());
				
				VanillaPreparedStatement stmt = null;
				ResultSet rs = null;
					
				stmt = connection.createStatement(parsedQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				stmt.setFetchSize(5000);
				
				if (parsedQuery.toLowerCase().startsWith("insert ") || 
						parsedQuery.toLowerCase().startsWith("update ") || 
						parsedQuery.toLowerCase().startsWith("delete ")|| 
						parsedQuery.toLowerCase().startsWith("alter ")) {
					//fuck pgsql
					stmt.close();
					stmt = connection.prepareQuery(parsedQuery);
					stmt.executeUpdate();
					activityResult = true;
				}
				else if(parsedQuery.toLowerCase().startsWith("declare ") || 
						parsedQuery.toLowerCase().startsWith("do ")) {
					CallableStatement cs = connection.getJdbcConnection().prepareCall(parsedQuery);
					cs.execute();
					cs.close();
				}
				else {
					rs = stmt.executeQuery(parsedQuery);
					if (rs.absolute(currentRow + 1)) {
						for (String s : mapping.keySet()) {

							List<String> valeur = new ArrayList<String>();
							String key = s.trim();
							String varname = mapping.get(s).getName();

							valeur.add(rs.getString(key));

							workflowInstance.getOrCreateVariable(varname)
									.setValues(valeur);
						}

						activityResult = false;
						currentRow++;
					} else {
						activityResult = true;
					}
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if (stmt != null){
					try {
						stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if (connection != null){
					try {
						ConnectionManager.getInstance().returnJdbcConnection(connection);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch(Exception e) {
				activityResult = true;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
			
			super.finishActivity();
		}
		
	}
	
	

}
