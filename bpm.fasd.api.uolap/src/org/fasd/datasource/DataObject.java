package org.fasd.datasource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import org.fasd.olap.ServerConnection;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;


public class DataObject {
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	
	public static String[] TYPES = {"dimension", "fact", "label","closure", "aggregate"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
	private int positionX = 10;
	private int positionY = 10;
	private String id;
	
	protected String name;
	
	protected DataSource dataSource;
	private String serverId = ""; //$NON-NLS-1$
	private ServerConnection server;
	protected String dataObjectType = ""; //$NON-NLS-1$
	private String selectStatement = ""; //$NON-NLS-1$
	private String transName = ""; //$NON-NLS-1$
	private String fileName = ""; //$NON-NLS-1$
	private String desc = ""; //$NON-NLS-1$
	private String creationStatement = null;
	private String fillingStatement = null;
	
	private boolean isInline = false;
	private DataInline datas;
	
	private boolean isView = false;
	
	
	
	protected ArrayList<DataObjectItem> items = new ArrayList<DataObjectItem>();
	
	public DataObject(String name) {
		this.name = name;
		counter++;
		id = "c" + String.valueOf(counter); //$NON-NLS-1$
	}
	
	public DataObject() {
		//parsing purposes
		counter++;
		id = "c" + String.valueOf(counter); //$NON-NLS-1$
	}
	
	/**
	 * @return the isView
	 */
	public boolean isView() {
		return isView;
	}

	/**
	 * @param isView the isView to set
	 */
	public void setView(boolean isView) {
		this.isView = isView;
	}
	
	public void setView(String isView) {
		this.isView = Boolean.parseBoolean(isView);
	}

	public void setItems(ArrayList<DataObjectItem> items) {
		this.items = items;
	}
	
	public void addDataObjectItem(DataObjectItem item) {
		for(DataObjectItem i : items){
			if (i.getOrigin().equals(item.getOrigin())){
				return;
			}
		}
		item.setParent(this);
		items.add(item);
	}
	
	public ArrayList<DataObjectItem> getColumns() {
		return items;
	}
	
		
	public String getId() {
		return id;
	}
	
	public String getFAXML() {
		String tmp = ""; //$NON-NLS-1$
		
		tmp += "            <dataobject-item>\n"; //$NON-NLS-1$
		tmp += "            	<id>" + getId() + "</id>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <name>" + name + "</name>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <data-source-id>" + dataSource.getId() + "</data-source-id>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <server-id>" + serverId + "</server-id>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <data-object-type>" + dataObjectType + "</data-object-type>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <positionX>" + positionX + "</positionX>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <positionY>" + positionY + "</positionY>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		
		String statementFormated = selectStatement.replace('<', '#');
		statementFormated = statementFormated.replace('>', '#');
		tmp += "                <isView>" + isView() + "</isView>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <select-statment-definition>" + statementFormated + "</select-statment-definition>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <transformation-name>" + transName + "</transformation-name>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <file-name>" + fileName + "</file-name>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <description>" + desc + "</description>\n"; //$NON-NLS-1$ //$NON-NLS-2$
		tmp += "                <isInline>" + isInline + "</isInline>\n";		 //$NON-NLS-1$ //$NON-NLS-2$
		if (creationStatement != null){
			tmp += "                <creationStatement>" + creationStatement + "</creationStatement>\n";
		}
		if (fillingStatement != null){
			tmp += "                <fillingStatement>" + fillingStatement + "</fillingStatement>\n";
		}
		
		
		for (int i=0; i < items.size(); i++) {
			tmp += items.get(i).getFAXML();
		}
		
		//inlinetable
		if (isInline){
			tmp += datas.getFAXML();
		}
		
		tmp += "            </dataobject-item>\n"; //$NON-NLS-1$
		
		return tmp;
	}

	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}

	public String getDataObjectType() {
		return dataObjectType;
	}

	public void setDataObjectType(String dataObjectType) {
		this.dataObjectType = dataObjectType;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSelectStatement() {
		return selectStatement;
	}

	public void setSelectStatement(String selectStatement) {
		StringTokenizer st = new StringTokenizer(selectStatement, "#"); //$NON-NLS-1$
		String result = ""; //$NON-NLS-1$
		int i = 0;
		while(st.hasMoreElements()){
			result += st.nextToken();
			if (i % 2 == 0 && st.hasMoreElements()){
				result += "<"; //$NON-NLS-1$
				i++;
			}
			else if (i % 2 == 1){
				result += ">"; //$NON-NLS-1$
				i++;
			}

		}
		
		this.selectStatement = result;
	}
	
	public ServerConnection getServer() {
		return server;
	}

	public void setServer(ServerConnection server) {
		this.server = server;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getTransName() {
		return transName;
	}

	public void setTransName(String transName) {
		this.transName = transName;
	}

	public String getPhysicalName() {
		try{
			return selectStatement.substring(selectStatement.indexOf(" FROM ") + 6, selectStatement.length()); //$NON-NLS-1$
		}
		catch(Exception e ){
			return name;
		}
		
	}
	
	public String getShortName(){
		if (getDataSource() != null && selectStatement.contains("."))
			return selectStatement.substring(selectStatement.lastIndexOf(".") + 1, selectStatement.length());
		
		else 
			return getPhysicalName();
	   }

	public boolean isClosure() {
		return dataObjectType.equals("closure"); //$NON-NLS-1$
	}

	public DataObjectItem findItemNamed(String primaryKey) {
		for(DataObjectItem i : items){
			if (i.getName().equals(primaryKey))
				return i;
		}
		return null;
	}

	public void removeItem(DataObjectItem i) {
		items.remove(i);
		
	}

	public void buildSelectStatement() {
		String buf = "SELECT "; //$NON-NLS-1$
		boolean first = true;
		for(DataObjectItem i : items){
			if (first){
				first = ! first;
				buf += i.getOrigin();
			}
			else{
				buf += ", " + i.getOrigin(); //$NON-NLS-1$
			}
		}
		buf += " FROM " + getPhysicalName(); //$NON-NLS-1$
		selectStatement = buf;
	}

	public boolean isInline() {
		return isInline;
	}

	public void setInline(boolean isInline) {
		this.isInline = isInline;
	}
	public void setInline(String isInline) {
		this.isInline = Boolean.parseBoolean(isInline);
	}
	
	public void setDatas(DataInline datas){
		this.datas = datas;
		datas.setDataObject(this);
	}
	
	public DataInline getDatas(){
		return datas;
	}

	/**
	 * the query that create the table
	 * @return
	 */
	public String getCreationStatement() {
		return creationStatement;
	}

	public void setCreationStatement(String creationStatement) {
		this.creationStatement = creationStatement;
	}

	/**
	 * the query to fill the table
	 * this should contain a truncate to clear the table
	 * @return
	 */
	public String getFillingStatement() {
		return fillingStatement;
	}

	public void setFillingStatement(String fillingStatement) {
		this.fillingStatement = fillingStatement;
	}
	
	/**
	 * create the table in the DataBase by executing the creationStatement query
	 *
	 */
	public void createTableInDataBase() throws Exception{
		if (creationStatement == null){
			throw new Exception("The creationStatement is not set");
		}

		VanillaJdbcConnection c = null;
		VanillaPreparedStatement stmt = null;
		try {
			dataSource.getDriver().connectAll();
			c = dataSource.getDriver().getConnection().getConnection();
			stmt = c.createStatement();
			stmt.executeQuery(creationStatement);
			
		} catch (SQLException e1) {
			throw e1;
		}
		finally{
			try {
				if (stmt != null)
					stmt.close();
				if (c != null)
					ConnectionManager.getInstance().returnJdbcConnection(c);
			} catch (SQLException e1) {
				throw e1;
			}
		}
		
	}
	
	/**
	 * fill the table in the DataBase by executing the fillingStatement query
	 *
	 */
	public void fillTableInDataBase() throws Exception{
		
		if (fillingStatement == null)
			throw new Exception("This statement for refreshing datas is not set");
		
		VanillaJdbcConnection c = null;
		VanillaPreparedStatement stmt = null;
		try {
			dataSource.getDriver().connectAll();
			c = dataSource.getDriver().getConnection().getConnection();
			stmt = c.createStatement();
			
			System.out.println("execute SQL : truncate "+ getPhysicalName());
			stmt.executeQuery("delete from " + getPhysicalName());
			System.out.println("execute SQL : " + fillingStatement);
			stmt.executeQuery(fillingStatement);
//			if (!ok){
//				System.out.println("execute SQL : rollback");
//				stmt.execute("rollback");
//			}
			
		} catch (SQLException e1) {
			throw e1;
		} catch (Exception e1) {
			throw e1;
		}
		finally{
			try {
				if (stmt != null)
					stmt.close();
				if (c != null)
					ConnectionManager.getInstance().returnJdbcConnection(c);
			} catch (SQLException e1) {
				throw e1;
			}
		}
	}
	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	
	public void setPositionX(String positionX) {
		try{
			this.positionX = Integer.parseInt(positionX);
		}catch(NumberFormatException e){
			
		}
		
	}
	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}
	public void setPositionY(String positionY) {
		try{
			this.positionY = Integer.parseInt(positionY);
		}catch(NumberFormatException e){
			
		}
		
	}
}
