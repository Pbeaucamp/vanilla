package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.ListServer;

/**
 * Check if there is a column in a database
 * @author Charles MARTIN
 *
 */
public class CheckColumnActivity extends AbstractActivity implements IConditionnable,IComment,IServer{

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public String table;
	public String columnName;
	private String comment;
	private String varRefName;
	public String typeOfColumn;
	private static int number = 0;
	
	/**
	 * 
	 * @return the type of the column
	 */
	public String getTypeOfColumn() {
		return typeOfColumn;
	}
	/**
	 * Set the type of the column
	 * @param typeOfColumn
	 */
	public void setTypeOfColumn(String typeOfColumn) {
		this.typeOfColumn = typeOfColumn;
	}
	/**
	 * 
	 * @return the name of the column
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * Set the name of the column
	 * @param columnName
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public DataBaseServer fileserver;
	

	public CheckColumnActivity(){
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
		number++;
	}
	
	public CheckColumnActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
	
	}
	
	
	public String getSuccessVariableSuffix() {
		return "_pathResult";
	}

	public CheckColumnActivity copy() {
		CheckColumnActivity a = new CheckColumnActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		if (varRefName != null)
			a.setVariable(varRefName);
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("checkColumnActivity");
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (table != null){
		e.addElement("path").setText(this.table);
		}
		if (columnName != null){
			e.addElement("columnName").setText(this.columnName);
			}
		if (typeOfColumn != null){
			e.addElement("typeOfColumn").setText(this.typeOfColumn);
			}
		if (varRefName != null) {
			e.addElement("varRefName").setText(varRefName);
		}
		if (fileserver != null){
			e.addElement("fileserver").setText(fileserver.getName());
			}
		
		
		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		if (fileserver == null) {
			buf.append("For activity " + name + ", the database server is not set.\n");
		}
		if (table == null) {
			buf.append("For activity " + name + ", the name of the table is not set.\n");
		}
		if (columnName == null) {
			buf.append("For activity " + name + ", the column name is not set.\n");
		}
		
		
		
		
		return buf.toString();
	}

	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);
		
		return listeVar;
	}
	
	public String getVariable() {
		return varRefName;
	}



	public void setVariable(String varRefName) {
		this.varRefName = varRefName;
	}
	
	/**
	 * Set the name of the table
	 * @param text
	 */
	public void setTable(String text) {
		this.table = text;
		
	}
	/**
	 * 
	 * @return the name of the table
	 */
	public String getTable(){
		return this.table;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String text) {
		this.comment = text;
	}
	
	
	public IResource getServer() {
		return fileserver;
	}
	
	public Class<?> getServerClass() {
		return DataBaseServer.class;
	}
	
	public void setServer(IResource server) {
		this.fileserver = (DataBaseServer) server;
	}
	public void setServer(String text) {

		fileserver = (DataBaseServer) ListServer.getInstance().getServer(text);

	}
	
	public void decreaseNumber() {
		number--;
	}
	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			try {
				if(fileserver.getSchemaName() == null) {
					fileserver.setSchemaName("");
				}
				
				SQLConnection connection = FactorySQLConnection.getInstance().createConnection(
						fileserver.getJdbcDriver(), 
						fileserver.getUrl(), 
						fileserver.getPort(), 
						fileserver.getDataBaseName(), 
						fileserver.getLogin(), 
						fileserver.getPassword(), 
						fileserver.getSchemaName(),false,"");
				
				if(table.contains("{$")){
					table = workflowInstance.parseString(table);
				}
				if(columnName.contains("{$")){
					columnName = workflowInstance.parseString(columnName);
				}
				if(typeOfColumn.contains("{$")){
					typeOfColumn = workflowInstance.parseString(typeOfColumn);
				}
				activityResult = false;
				for(IColumn col : connection.getTable(table).getColumns()){

					if(col.getName().equalsIgnoreCase(table+"."+columnName)){
						if(!typeOfColumn.equalsIgnoreCase("nonremplie")){
							SQLColumn colonne = (SQLColumn)col;
							if(colonne.getSqlType().equalsIgnoreCase(typeOfColumn)){
								activityResult = true;
								break;
							}
						}
						activityResult = true;
						break;
					}
				}
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
			
			
			
			super.finishActivity();
		}
	}

}
