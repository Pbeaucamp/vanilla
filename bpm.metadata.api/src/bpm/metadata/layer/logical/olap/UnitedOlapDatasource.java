package bpm.metadata.layer.logical.olap;

import java.util.List;

import bpm.metadata.layer.business.UnitedOlapBusinessPackage;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.ITable;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.layer.physical.olap.UnitedOlapTable;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.vanilla.platform.core.IVanillaContext;

public class UnitedOlapDatasource extends AbstractDataSource {

	
	
	/**
	 * Forbidden, just here for the digester to work
	 * @see public UnitedOlapDatasource(UnitedOlapConnection connection) instead
	 */
	public UnitedOlapDatasource() {
		
	}
	
	public UnitedOlapDatasource(UnitedOlapConnection connection) {
		super(connection, AbstractDataSource.OLAP);
	}
	
	@Override
	public IDataStream add(ITable table) {
		UnitedOlapDataStream dataStream = new UnitedOlapDataStream((UnitedOlapTable)table);
		add(dataStream);
		return dataStream;
	}

	@Override
	public IDataStream addTableFromQuery(String name, String query) throws Exception {
		
		return null;
	}

	@Override
	public List<List<String>> executeQuery(String connectionName, String query, int maxRows) throws Exception {
		return getConnection().executeQuery(query, maxRows, null);
	}

	@Override
	public Integer countQuery(String connectionName, String query, int maxRows) throws Exception {
		return getConnection().countQuery(query, maxRows);
	}

	@Override
	public List<List<String>> executeQuery(String connectionName, String query, boolean[] flags, int maxRows) throws Exception {
		return executeQuery(connectionName, query, maxRows);
	}

	@Override
	public String getXml() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("	<unitedOlapDatasource>\n");
		
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
		
		buf.append("	</unitedOlapDatasource>\n");
		
		return buf.toString();
	}

	public List<List<String>> executeQuery(String connectionName, UnitedOlapQuery query, int maxRows, IVanillaContext vanillaCtx, List<List<String>> promptsValues, UnitedOlapBusinessPackage unitedOlapBusinessPackage) throws Exception {
		return ((UnitedOlapConnection)getConnection()).executeQuery(query, maxRows, vanillaCtx, promptsValues, unitedOlapBusinessPackage);
	}

	@Override
	public List<IConnection> getConnections(String groupname) {
		List<IConnection> cons = super.getConnections(groupname);
		if(cons == null || cons.size() == 0) {
			return getConnections();
		}
		return cons; 
	}
}
