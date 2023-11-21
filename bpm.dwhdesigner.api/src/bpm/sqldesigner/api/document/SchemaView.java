package bpm.sqldesigner.api.document;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;

public class SchemaView extends Node{
	
	
//	
//	private String connectionHost;
//	private String connectionPort;
//	private String connectionDatabaseName;
//	private String connectionLogin;
//	private String connectionPassword;
	
	
	private float scale = 1.0f;
	
	
	private String name = "newSchemaView";
	
	private HashMap<Table, Table> tables = new HashMap<Table, Table>();
	
	private Schema schema;
	
	public SchemaView(){}
	public SchemaView(Schema schema) {
		this.schema = schema;
		for(Object o : schema.getChildren()){
			if (o instanceof Table){
				tables.put((Table)o, new Table((Table)o));
				
				final Node n = (Node)o;
				n.addPropertyChangeListener(new PropertyChangeListener() {
					
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)){
							tables.get(n).setLayout(n.getLayout()[0], n.getLayout()[1], n.getLayout()[2], n.getLayout()[3]);
						}
						
					}
				});
				
			}
//			else if (o instanceof SQLProcedure){
//				this.schema.addProcedure((SQLProcedure)o);
//			}
//			else if (o instanceof SQLView){
//				this.schema.addView((SQLView)o);
//			}
		}
	}
	
	public SchemaView(Catalog catalog) {
		this(catalog.getSchema("null"));
	}

	/**
	 * @return the scale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	
	
	@Override
	public DatabaseCluster getCluster() {
		return getSchema().getCluster();
	}

//	public void addTable(Table table){
//		tables.add(table);
//		
//	}
	
//	public void removeTable(Table table){
//		tables.remove(table.getName());
//	}
	
	@Override
		public Object[] getChildren() {
			
			return tables.keySet().toArray(new Object[tables.size()]);
		}

//	/**
//	 * @return the connectionHost
//	 */
//	public String getConnectionHost() {
//		return connectionHost;
//	}
//
//	/**
//	 * @param connectionHost the connectionHost to set
//	 */
//	public void setConnectionHost(String connectionHost) {
//		this.connectionHost = connectionHost;
//	}
//
//	/**
//	 * @return the connectionPort
//	 */
//	public String getConnectionPort() {
//		return connectionPort;
//	}
//
//	/**
//	 * @param connectionPort the connectionPort to set
//	 */
//	public void setConnectionPort(String connectionPort) {
//		this.connectionPort = connectionPort;
//	}
//
//	/**
//	 * @return the connectionDatabaseName
//	 */
//	public String getConnectionDatabaseName() {
//		return connectionDatabaseName;
//	}
//
//	/**
//	 * @param connectionDatabaseName the connectionDatabaseName to set
//	 */
//	public void setConnectionDatabaseName(String connectionDatabaseName) {
//		this.connectionDatabaseName = connectionDatabaseName;
//	}
//
//	/**
//	 * @return the connectionLogin
//	 */
//	public String getConnectionLogin() {
//		return connectionLogin;
//	}
//
//	/**
//	 * @param connectionLogin the connectionLogin to set
//	 */
//	public void setConnectionLogin(String connectionLogin) {
//		this.connectionLogin = connectionLogin;
//	}
//
//	/**
//	 * @return the connectionPassword
//	 */
//	public String getConnectionPassword() {
//		return connectionPassword;
//	}
//
//	/**
//	 * @param connectionPassword the connectionPassword to set
//	 */
//	public void setConnectionPassword(String connectionPassword) {
//		this.connectionPassword = connectionPassword;
//	}

	/**
	 * @return the tables
	 */
	public List<Table> getTables() {
		return new ArrayList<Table>(tables.keySet());
	}
	
	public String getName() {
		
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	public Schema getSchema() {
		return schema;
	}
}

