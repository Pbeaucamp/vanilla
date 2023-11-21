package bpm.metadata.layer.logical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.metadata.MetaData;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.sql.SQLConnection;

public abstract class AbstractDataSource implements IDataSource {
	protected HashMap<String, IDataStream> tables = new LinkedHashMap<String, IDataStream>();
	protected List<Relation> relations = new ArrayList<Relation>();
	
	protected MetaData model;
	
	protected int type = -1;
	protected String name;
//	protected IConnection connection = null;
	protected List<IConnection> connections = new ArrayList<IConnection>();
	protected HashMap<IConnection, List<String>> groupsConnection = new HashMap<IConnection, List<String>>();
	
	
	private List<IConnection> connectionToNotSave = new ArrayList<IConnection>();
	
	protected AbstractDataSource(){}
	
	protected AbstractDataSource(IConnection connection, int type){
		this.connections.add(connection);
		this.type = type;
	}
	

	public void setDefaultConnection(IConnection connection) {
		if (connections.contains(connection)){
			Collections.swap(connections, connections.indexOf(connection), 0);
		}
		
	}
	
	public List<IDataStream> getDataStreams() {
		List<IDataStream> list = new ArrayList<IDataStream>();
		for(IDataStream d : tables.values()){
			list.add(d);
		}
		
		try {
			Collections.sort(list, new Comparator<IDataStream>() {
				@Override
				public int compare(IDataStream o1, IDataStream o2) {
					return o1.getName().compareTo(o2.getName());
				}	
			});
		} catch (Exception e) {
		}
		
		return list;
	}

	public String getName() {
		return name;
	}


	public int getType() {
		return type;
		
	}


	public List<Relation> remove(IDataStream stream) {
		tables.remove(stream.getName());
		List<Relation> toRemove = new ArrayList<Relation>();
		
		for(Relation r : relations){
			if (r.getLeftTable() == stream || r.getRightTable() == stream){
				toRemove.add(r);
			}
		}
		
		relations.removeAll(toRemove);
		
		return toRemove;
	}
	

	public void add(IDataStream stream){
		if (stream == null){
			return;
		}
		tables.put(stream.getName(), stream);
		stream.setDataSource(this);
	}
	
	public abstract IDataStream add(ITable table); 


	public void removeAll() {
		tables.clear();
		
	}
	
	public IDataStream getDataStreamNamed(String name){
		return tables.get(name.replace(" ", ""));
	}


	public void addRelation(Relation relation) {
		if (relation == null){
			return;
		}
		

		if (!relations.contains(relation)){
			relations.add(relation);
		}
		
	}


	public List<Relation> getRelations() {
		return relations;
	}


	public void removeRelation(Relation relation) {
		relations.remove(relation);
	}
	
	public IConnection getConnection(){
		return connections.get(0);
	}

	public int getIndiceConnection(IConnection c) throws Exception{
		int i = -1;
		for(IConnection cc : connections){
			i++;
			if (cc == c){
				return i;
			}
			
		}
		throw new Exception("This Connection dont belong to the DataSource");
	}
	
	public void setConnection(int i, IConnection connection){
		IConnection old = this.connections.remove(i);
		this.connections.add(i, connection);
		
		List<String> groups = groupsConnection.get(old);
		groupsConnection.remove(old);
		groupsConnection.put(connection, groups);
		
	}
	
	public void addAlternateConnection(IConnection connection) throws Exception{
		//check if the structure contains at least the same used columns
//		connection.connect(); 
		
		if (connection == null){
			return;
		}
		for(IDataStream t : tables.values()){
			for(IDataStreamElement c : t.getElements()){
				if ( ! (c instanceof ICalculatedElement)){
					ITable table = connection.getTable(c.getOrigin().getTable().getName());
					
					if (table == null){
						throw new Exception("The table " + c.getOrigin().getTable().getName() + " is not accesible from this connection. The connection cannot be used.");
					}
					
					IColumn col = table.getElementNamed(c.getOrigin().getName());
					
					if (col == null){
						throw new Exception("The Column " + c.getOrigin().getName() + " is not accesible from this connection. The connection cannot be used.");
					}
				}
				
				
			}
		}
		
		
		
		
		for(IConnection c : connections){
			if (c.getName().equals(connection.getName())){
				throw new Exception("The Connection Name is already used for this DataSource");
			}
		}
		
		this.connections.add(connection);
		this.groupsConnection.put(connection, new ArrayList<String>());
	}
	
	public void setName(String name){
		if (model != null){
			model.removeDataSource(this);
		}
		
		this.name = name;
		if (model != null){
			model.addDataSource(this);
		}
	}
	
	public MetaData getMetaDataModel(){
		return model;
	}
	
	public void setMetaDataModel(MetaData model){
		this.model = model;
	}
	
	
	/**
	 * return all the relation for the given tables
	 * @param dataStreams
	 * @return
	 */
	public List<Relation> getRelations(List<IDataStream> dataStreams){
		List<Relation> result = new ArrayList<Relation>();
		
		for(Relation r : relations){
			if (dataStreams.contains(r.getLeftTable()) && dataStreams.contains(r.getRightTable())){
				result.add(r);
			}
		}
		
		return result;
	}
	
	/**
	 * create a table from the given query
	 * @param query
	 */
	public abstract IDataStream addTableFromQuery(String name, String query) throws Exception;

	public List<IConnection> getConnections() {
		return connections;
	}
	
	public List<IConnection> getConnections(String groupname) {
		if (groupname == null || groupname.equals("none")){
			return getConnections();
		}
		List<IConnection> l = new ArrayList<IConnection>();
		
		for(IConnection c : connections){
			List<String> _l = groupsConnection.get(c);
			if (_l == null){
				continue;
			}
			for(String s : _l){
				if (s.equals(groupname) && !l.contains(c)){
					l.add(c);
					break;
				}
			}
		}
		
		return l;
	}
	
	
	public List<String> getConnectionNames(){
		List<String> l = new ArrayList<String>();
		
		for(IConnection c : connections){
			l.add(c.getName());
		}
		
		return l;
	}
	
	public List<String> getConnectionNames(String groupName){
		List<String> l = new ArrayList<String>();
		
		for(IConnection c : getConnections(groupName) ){
			l.add(c.getName());
		}
		
		return l;
	}

	public void securizeConnection(String conName, List<String> groupName){
		for(String s : groupName){
			securizeConnection(conName, s, true);
		}
	}
	public void securizeConnection(String conName, String groupName, boolean isSecured){
		if (!isSecured){
			for(IConnection c : groupsConnection.keySet()){
				if (c.getName().equals(conName)){
					for(String s : groupsConnection.get(c)){
						if (s.equals(groupName)){
							groupsConnection.get(c).remove(s);
							return;
						}
					}
				}
			}
		}else{
			if (groupsConnection.isEmpty()){
				for(IConnection c : getConnections()){
					groupsConnection.put(c, new ArrayList<String>());
				}
			}
			
			for(IConnection c : groupsConnection.keySet()){
				if (c.getName().equals(conName)){
					for(String s : groupsConnection.get(c)){
						if (s.equals(groupName)){
							return;
						}
					}
					groupsConnection.get(c).add(groupName);
					return;
				}
			}
			
			
		}
		
	}
	
	public boolean isGranted(SQLConnection sock, String groupName) {
		if (groupsConnection.get(sock) == null){
			return false;
		}
		for(String s : groupsConnection.get(sock)){
			if (s.equals(groupName)){
				return true;
			}
		}
		
		
		return false;
	}
	
	public void addAlternateConnection(IConnection connection,	boolean saveConnectionInModel) {
		connections.add(connection);
		if (!saveConnectionInModel){
			connectionToNotSave.add(connection);
		}
		
	}
	
	public void removeConnection(IConnection connection) {
		connections.remove(connection);
		
	}
	
	protected List<IConnection> getNonSaveableConnections(){
		return connectionToNotSave;
	}
	
}
