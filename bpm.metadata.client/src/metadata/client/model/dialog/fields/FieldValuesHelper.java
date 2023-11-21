package metadata.client.model.dialog.fields;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;
import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.physical.sql.SQLColumn;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.layer.physical.sql.SQLTable;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.vanilla.platform.core.IVanillaContext;

public class FieldValuesHelper {

	
	public static int countRows(IDataStream str) throws Exception{
		AbstractBusinessTable t = null;
		IQuery q = null;
		if(str instanceof UnitedOlapDataStream) {
			t = new UnitedOlapBusinessTable(str.getName());
			q = new UnitedOlapQuery();

		}
		else {
			t = new SQLBusinessTable(str.getName());
			q = new QuerySql();
			((QuerySql)q).setGroupName("none");
			
		}
		for(IDataStreamElement c : str.getElements()){
			t.addColumn(c);
			if (q instanceof QuerySql){
				((QuerySql)q).getSelect().add(c);
			}
			else{
				((UnitedOlapQuery)q).getSelect().add(c);
			}
		}
		BusinessModel m = new BusinessModel();
		BusinessPackage p = new BusinessPackage();
		m.addBusinessPackage(p);
		try{
			Activator.getDefault().getModel().addBusinessModel(m);
			m.addBusinessTable(t);
			p.addBusinessTable(t);
			
			
			if (q instanceof QuerySql){
				String qStr = p.getQuery(null, Activator.getDefault().getVanillaContext(), q, new ArrayList());
				VanillaJdbcConnection c = null;
				VanillaPreparedStatement stmt = null;
				ResultSet rs = null;
				
				try{
					c = ((SQLConnection)str.getOrigin().getConnection()).getJdbcConnection();
					stmt = c.createStatement();
					qStr = qStr.replace("`", "\"");
					rs = stmt.executeQuery("select count(*) from (" + qStr + ") T");
					if (rs.next()){
						return rs.getInt(1);
					}
					else{
						return 0;
					}
				}finally{
					if (rs != null){
						rs.close();
					}
					if (stmt != null){
						stmt.close();
					}
					if (c != null){
						ConnectionManager.getInstance().returnJdbcConnection(c);
					}
				}
				
				
				
				
			}
			else{
				throw new Exception("Only supported on SQL");
			}
			
			
			
			
			
			
		}finally{
			Activator.getDefault().getModel().removeBusinessModel(m);
		}
	}
	
	
	public static List<List<Object>> getRows(Object origin, Object field)throws Exception{
		
		VanillaJdbcConnection jdbcConnection = null;
		StringBuffer query = new StringBuffer();
		
		
		
		if (origin instanceof SQLTable && field instanceof SQLColumn){
			
			jdbcConnection = ((SQLConnection)((SQLTable)origin).getConnection()).getJdbcConnection();
			query.append("select ");
			query.append(((SQLColumn)field).getName() + " , count(*) from ");
			query.append(((SQLTable)origin).getName());
			query.append( " group by " + ((SQLColumn)field).getName());
		}
		else if (origin instanceof IDataStream && field instanceof IDataStreamElement){
			
			if(origin instanceof UnitedOlapDataStream) {
				UnitedOlapBusinessTable table = new UnitedOlapBusinessTable(((UnitedOlapDataStream) origin).getName());
				table.addColumn((IDataStreamElement) field);
				
				IVanillaContext ctx = null;
				
				try{
					ctx = Activator.getDefault().getVanillaContext();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
				List<List<String>> values = table.executeQuery(ctx, 0);
				
				int i = 0;
				for(IDataStreamElement e : ((UnitedOlapDataStream)origin).getElements()) {
					if(e.getName().equals(((IDataStreamElement)field).getName())) {
						break;
					}
					i++;
				}
				List<List<Object>> rows = new ArrayList<List<Object>>();
				String previous = values.get(0).get(i);
				int count = 0;
				for(List<String> ls : values) {
					String s = ls.get(i);
					if(s.equals(previous)) {
						count++;
					}
					else {
						List<Object> row = new ArrayList<Object>();
						row.add(previous);
						row.add(count);
						rows.add(row);
						count = 1;
						previous = s;
					}
				}
				
				List<Object> row = new ArrayList<Object>();
				row.add(previous);
				row.add(count);
				rows.add(row);
				
				return rows;
			}
			
			else {
			
				jdbcConnection = ((SQLConnection)((SQLDataSource)((IDataStream)origin).getDataSource()).getConnections().get(0)).getJdbcConnection();
				query.append("select ");
				if (field instanceof ICalculatedElement){
					query.append(((ICalculatedElement)field).getFormula() + " as \"" +  ((ICalculatedElement)field).getName() + "\" , count(*) from ");
					
					query.append(((IDataStream)origin).getOrigin().getName() + " `" + ((IDataStream)origin).getName() + "`");
					query.append( " group by " + ((IDataStreamElement)field).getName() + "");
				}
				else{
					query.append(((SQLColumn)((IDataStreamElement)field).getOrigin()).getShortName() +   " , count(*) from ");
					if (((SQLTable)((IDataStreamElement)field).getOrigin().getTable()).isQuery()){
						query.append("(" + ((SQLDataStream)((IDataStreamElement)field).getDataStream()).getSql() + ") `" + ((IDataStreamElement)field).getDataStream().getName() + "`");
					}
					else{
						query.append(((SQLTable)((IDataStreamElement)field).getOrigin().getTable()).getName() + " `" + ((IDataStreamElement)field).getDataStream().getName() + "`");
					}
					
					query.append( " group by `"  + ((IDataStreamElement)field).getDataStream().getName()+ "`."+ ((IDataStreamElement)field).getOrigin().getShortName() + "");
					
				}
			}
			
			
		}
		else if (origin instanceof SQLBusinessTable && field instanceof IDataStreamElement){
			List<String> l = new ArrayList<String>();
			for(IDataStreamElement e : ((SQLBusinessTable)origin).getColumns()){
				l.add(e.getName());
			}
			
			String q = 	((SQLBusinessTable)origin).getQueryFor(l);
			jdbcConnection = ((SQLBusinessTable)origin).getDefaultConnection().getJdbcConnection();
			
			query.append("select `T`.`" + ((IDataStreamElement)field).getOuputName() + "`, count(*) ");
			query.append(" from (");
			query.append(q);
			query.append(")  `T`");
			query.append(" group by `T`.`"+ ((IDataStreamElement)field).getOuputName() + "`");
			
		}
		
		if (jdbcConnection != null && !query.toString().equals("")){
			
			String _f = query.toString().replace("`", "\"");
			
			try{
				VanillaPreparedStatement stmt = jdbcConnection.createStatement();
				
				
				ResultSet rs = stmt.executeQuery(_f);
				List<List<Object>> result = new ArrayList<List<Object>>();
				while(rs.next()){
					List<Object> l = new ArrayList<Object>();
					l.add(rs.getString(1));
					l.add(rs.getInt(2));
					result.add(l);
				}
				rs.close();
				stmt.close();
				ConnectionManager.getInstance().returnJdbcConnection(jdbcConnection);
				return result;
			}catch(Exception ex){
				ex.printStackTrace();
				throw new Exception(ex.getMessage() + "\n" + _f,ex);
			}
			
			
		}
		
		if(origin instanceof UnitedOlapBusinessTable) {
			List<List<String>> values = ((UnitedOlapBusinessTable)origin).executeQuery(Activator.getDefault().getVanillaContext(), 0);
			
			int i = 0;
			for(IDataStreamElement e : ((UnitedOlapBusinessTable)origin).getColumns()) {
				if(e.getName().equals(((IDataStreamElement)field).getName())) {
					break;
				}
				i++;
			}
			List<List<Object>> rows = new ArrayList<List<Object>>();
			String previous = values.get(0).get(i);
			int count = 0;
			for(List<String> ls : values) {
				String s = ls.get(i);
				if(s.equals(previous)) {
					count++;
				}
				else {
					List<Object> row = new ArrayList<Object>();
					row.add(previous);
					row.add(count);
					rows.add(row);
					count = 1;
					previous = s;
				}
			}
			
			List<Object> row = new ArrayList<Object>();
			row.add(previous);
			row.add(count);
			rows.add(row);
			
			return rows;
		}
		
		throw new Exception("Distinct Values not supported on "+ origin.getClass().getSimpleName() + " steps" );
	}
}
