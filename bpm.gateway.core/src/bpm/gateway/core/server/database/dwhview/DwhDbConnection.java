package bpm.gateway.core.server.database.dwhview;

import org.dom4j.Element;

import bpm.gateway.core.server.database.DBColumn;
import bpm.gateway.core.server.database.DBSchema;
import bpm.gateway.core.server.database.DBTable;
import bpm.gateway.core.server.database.DataBaseConnection;

public class DwhDbConnection extends DataBaseConnection{
	private DBSchema schema;
	
	
	public DBSchema getSchema(){
		return schema;
	}
	
	
	public void setSchema(DBSchema schema){
		this.schema = schema;
	}
	
	@Override
	public Element getElement() {
		
		Element e =  super.getElement();
		e.setName("dwhDatabaseConnection");
		
		Element s = e.addElement("schema");
		if (schema.getName() != null && !"null".equals(schema.getName())){
			s.addElement("name").setText(schema.getName());
			
		}
		s.addElement("noSchema").setText(schema.isNoSchema() + "");
		for(DBTable t : schema.getTables()){
			Element eT = s.addElement("table");
			eT.addElement("name").setText(t.getName());
			
			for(DBColumn c : t.getAllColumns()){
				Element eC = eT.addElement("column");
				eC.addElement("name").setText(c.getName());
				eC.addElement("primaryKey").setText(c.isPrimaryKey() + "");
				
			}
		}
		return e;
	}
}
