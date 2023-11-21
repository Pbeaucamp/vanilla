package org.fasd.datasource;

import java.util.ArrayList;
import java.util.List;

import org.fasd.export.IExportable;



public class DataSource implements IExportable{
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	protected String id;
	
	protected String name = "";
	private List<DataSourceConnection> drivers = new ArrayList<DataSourceConnection>();
	private DataSourceConnection defaultConnection;
	
	
	private String defaultConnectionId;
	
	protected ArrayList<DataObject> dataObjects = new ArrayList<DataObject>();
	
	public DataSource(String name, DataSourceConnection driver) {
		this.name = name;
		drivers.add(driver);
		this.defaultConnection = driver;
		counter ++;
		id = "t" + String.valueOf(counter);
	}
	
	public DataSource() {
		counter ++;
		id = "t" + String.valueOf(counter);
		//for parsing purposes
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}
	
	public DataSourceConnection getDriver() {
		if (defaultConnectionId != null){
			for(DataSourceConnection c : getDrivers()){
				if (c.getId().equals(defaultConnectionId)){
					setDriver(c);
					defaultConnectionId = null;
					break;
				}
			}
		}
		else if (defaultConnection == null && getDrivers().size() > 0){
			defaultConnection = getDrivers().get(0);
		}
		return defaultConnection;
	}
	
	public List<DataSourceConnection> getDrivers(){
		return new ArrayList<DataSourceConnection>(drivers);
	}
	
	public void setDefaultDriverId(String driverId){
		this.defaultConnectionId = driverId;
	}
	
	public boolean addConnection(DataSourceConnection connection){
		if (connection != null && !drivers.contains(connection)){
			drivers.add(connection);
			connection.setParent(this);
			return true;
		}
		return false;
	}
	
	public boolean removeConnection(DataSourceConnection connection){
		return drivers.remove(connection);
	}
	
	public void setDSName(String name) {
		this.name = name;
	}
	
	public void setDriver(DataSourceConnection driver) {
		this.defaultConnection = driver;
		driver.setParent(this);
		addConnection(driver);
	}
	
	public ArrayList<DataObject> getDataObjects() {
		return dataObjects;
	}
	
	public void addDataObject(DataObject tab) {
		dataObjects.add(tab);
		tab.setDataSource(this);
	}
	
		
	public String getDSName() {
		return name;
	}
	
	public String getFAXML() {
		String tmp = "";
		
		tmp += "        <datasource-item>\n";
		tmp += "            <name>" + name + "</name>\n";
		tmp += "            <id>" + id + "</id>\n";
		
		tmp += "            <defaultConnectionId>" + getDriver().getId() + "</defaultConnectionId>\n";
		for(DataSourceConnection c : getDrivers()){
			tmp += c.getFAXML();
		}
		
		
		for (int i=0; i < dataObjects.size(); i++) {
			tmp += dataObjects.get(i).getFAXML();
		}
		
		tmp += "        </datasource-item>\n";
		
		return tmp;
	}

	public String getXML() {
		
		return null;
	}

	public boolean containsDataObjectNamed(String tableName) {
		for(DataObject o : dataObjects){
			if (o.getName().equals(tableName))
				return true;
		}
		return false;
	}

	public void removeDataObject(DataObject data) {
		dataObjects.remove(data);
		
	}

	public DataObject findDataObjectNamed(String tableName) {
		for(DataObject o : dataObjects){
			if (o.getName().equalsIgnoreCase(tableName))
				return o;
		}
		return null;
	}

	public void clear() {
		dataObjects.clear();
		
	}

	public DataObject findDataObject(String id) {
		for(DataObject o : dataObjects){
			if (o.getId().equals(id))
				return o;
		}
		return null;
	}

	public DataObjectItem findItemNamed(String origin) {
		for(DataObject o : dataObjects){
			for(DataObjectItem it : o.getColumns())
				if (it.getOrigin().equals(origin))
					return it;
		}
		return null;
	}

	
	public void refreshAggregateTables(){
		for(DataObject d : dataObjects){
			try {
				d.fillTableInDataBase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
