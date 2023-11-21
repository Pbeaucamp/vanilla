package bpm.metadata.layer.logical.sql;

import java.util.List;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;


public class SQLDataSource extends AbstractDataSource {
	
//	private PathFinder path = new PathFinder(this);
	
	
	/**
	 * do not use!!!!
	 */
	public SQLDataSource(){}
	
	protected SQLDataSource(IConnection connection) {
		super(connection, IDataSource.SQL);
	}

	@Override
	public IDataStream add(ITable table) {
		if (table instanceof SQLTable){
			SQLDataStream s = new SQLDataStream((SQLTable) table);
			
			int i = 0;
			boolean namechanged = false;
			while(getDataStreamNamed(s.getName()) != null){
				if (!namechanged){
					namechanged = true;
					s.setName(s.getName() + "_" + (++i));
				}
				else{
					s.setName(s.getName().substring(0, s.getName().indexOf("_") + 1) + (++i));
				}
				
			}
			
			s.setDataSource(this);
			add(s);
			
			return s;
		}
		return null;
		
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("    <sqlDataSource>\n");
		buf.append("        <name>" + name + "</name>\n");
		
		
		
		List<IConnection> toSkip = getNonSaveableConnections();
		
		for(IConnection c: connections){
			if (!toSkip.contains(c)){
				buf.append(c.getXml());
			}
			
		}
		for(IConnection c : groupsConnection.keySet()){
			if (toSkip.contains(c)){
				continue;
			}
			if (!groupsConnection.get(c).isEmpty()){
				buf.append("        <grant>\n");
				buf.append("            <connectionName>" + c.getName() + "</connectionName>\n");
				
				buf.append("            <grantedFor>\n");
				for(String s : groupsConnection.get(c)){
					buf.append("                <group>" + s + "</group>\n");
				}
				buf.append("            </grantedFor>\n");
				buf.append("        </grant>\n");
			}
			
		}
		
		for(IDataStream t : tables.values()){
			buf.append(t.getXml());
		}
		
		for(Relation r : relations){
			buf.append(r.getXml());
		}
		
		buf.append("    </sqlDataSource>\n");
		return buf.toString();
	}

	public List<List<String>> executeQuery(String connectionName, String query, int maxRows) throws Exception {
		
		for(IConnection c : connections){
			if (c.getName().equals(connectionName)){
				SQLConnection con = (SQLConnection) c;
				return con.executeQuery(query, maxRows, null);
			}
		}
		
		
		throw new Exception("Connection " + connectionName + " not found in dataSource " + getName());
		
	}

	@Override
	public Integer countQuery(String connectionName, String query, int maxRows) throws Exception {
		for(IConnection c : connections){
			if (c.getName().equals(connectionName)){
				SQLConnection con = (SQLConnection) c;
				return con.countQuery(query, maxRows);
			}
		}
		
		
		throw new Exception("Connection " + connectionName + " not found in dataSource " + getName());
		
	}
	
	public List<List<String>> executeQuery(String connectionName, String query, boolean[] flags, int maxRows) throws Exception {
		for(IConnection c : connections){
			if (c.getName().equals(connectionName)){
				SQLConnection con = (SQLConnection) c;
				return con.executeQuery(query, maxRows, flags);
			}
		}
		
		
		throw new Exception("Connection " + connectionName + " not found in dataSource " + getName());

	}


	@Override
	public IDataStream addTableFromQuery(String name, String query) throws Exception {
		ITable table = null;
		for(IConnection c : connections){//SQLConnection con = (SQLConnection)connections.get(0);
			table = ((SQLConnection)c).createTableFromQuery(/*name,*/ query);
		}
		
		
		IDataStream ds = add(table);
		((SQLDataStream)ds).setSql(query);
		ds.setOriginName(name);
		ds.setName(name);
		return ds;
	}




	@Override
	public void addRelation(Relation relation) {
		super.addRelation(relation);
		
//		Path t = new Path();
//		t.addRelationship(relation);
//		path.addPath(t);
//
//		
//		List<Path> toAdd = new ArrayList<Path>();
//		for(Path p : path.getPaths()){
//			if (path.isRelationAddableToPath(p, relation)){
//				//p.addRelationship(relation);
//				Path _t = new Path();
//				for(Relation r : p.getRelations()){
//					_t.addRelationship(r);
//				}
//				_t.addRelationship(relation);
//				
//				toAdd.add(_t);
//			}
//
//		}
//		
//		for(Path pp : toAdd){
//			path.addPath(pp);
//		}
//		path.getPaths().addAll(toAdd);
		
		
	}

//	public Path getShortest(List<IDataStream> list) {
//		
//		return path.getBest(list);
//	}
	

}
