package bpm.gateway.ui.views.property;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;

import bpm.gateway.core.forms.Form;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.database.nosql.hbase.HBaseConnection;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbConnection;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;

public class CustomTypeMapper implements ITypeMapper {

	public Class mapType(Object object) {
		if (object instanceof Variable){
			return Variable.class;
		}
		if (object instanceof DataBaseConnection){
			return DataBaseConnection.class;
			
		}
		if (object instanceof LdapConnection){
			return LdapConnection.class;
		}
		
		if (object instanceof Form){
			return Form.class;
		}
		
		if (object instanceof Parameter){
			return Parameter.class;
		}
		
		if(object instanceof CassandraConnection){
			return CassandraConnection.class;
		}
		
		if(object instanceof HBaseConnection){
			return HBaseConnection.class;
		}
		
		if(object instanceof MongoDbConnection){
			return MongoDbConnection.class;
		}
		
		return null;
	}

}
