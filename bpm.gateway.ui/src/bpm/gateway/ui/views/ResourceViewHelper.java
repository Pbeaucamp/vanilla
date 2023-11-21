package bpm.gateway.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.inputs.LdapInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.viewer.TreeParent;
import bpm.gateway.ui.viewer.TreeStaticObject;


public class ResourceViewHelper {
	
	protected static TreeStaticObject fileProvider, databaseProvider, nosqlProvider, d4cProvider, ldapProvider, freemetricsProvider, variables, parameters, localVariables; 
	protected static TreeParent root;
	
	public static void createTree(TreeViewer viewer){
		
		root = new TreeParent(""); //$NON-NLS-1$
		
		fileProvider = new TreeStaticObject(Messages.ResourceViewHelper_1);
		root.addChild(fileProvider);
		
		databaseProvider = new TreeStaticObject(Messages.ResourceViewHelper_2);
		root.addChild(databaseProvider);
		
		
		nosqlProvider = new TreeStaticObject(Messages.ResourceViewHelper_0);
		root.addChild(nosqlProvider);
		
		d4cProvider = new TreeStaticObject(Messages.ResourceViewHelper_4);
		root.addChild(d4cProvider);
		
		ldapProvider = new TreeStaticObject(Messages.ResourceViewHelper_3);
		root.addChild(ldapProvider);
		
		
		freemetricsProvider = new TreeStaticObject(Messages.ResourceViewHelper_5);
//		root.addChild(freemetricsProvider);

		variables = new TreeStaticObject(Messages.ResourceViewHelper_8);
		root.addChild(variables);
		
		parameters = new TreeStaticObject(Messages.ModelViewHelper_2);
		root.addChild(parameters);
		
		localVariables = new TreeStaticObject(Messages.ModelViewHelper_3);
		root.addChild(localVariables);

		viewer.setInput(root);
		
	}

	
	
	public static Image getImageFor(Object o){
		if (o instanceof DataBaseServer){
			return Activator.getDefault().getImageRegistry().get(IconsNames.database_16);
		}
		else if (o instanceof CassandraServer){
			return Activator.getDefault().getImageRegistry().get(IconsNames.database_16);
		}
		else if (o instanceof LdapInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.ldap_input_16);
		}
		
		else if (o instanceof AbstractFileServer){
			return Activator.getDefault().getImageRegistry().get(IconsNames.table_16);
		}
		else if (o instanceof TreeStaticObject){
			return Activator.getDefault().getImageRegistry().get(IconsNames.table_16);
		}
		else if (o instanceof HBaseServer) {
			return Activator.getDefault().getImageRegistry().get(IconsNames.database_16);
		}
		else if (o instanceof D4CServer) {
			return Activator.getDefault().getImageRegistry().get(IconsNames.d4c_16);
		}
		else if (o instanceof LdapServer){
			return Activator.getDefault().getImageRegistry().get(IconsNames.ldap_server_16);
		}
		else if (o instanceof Variable){
			return Activator.getDefault().getImageRegistry().get(IconsNames.variable_16);
		}
		else if (o instanceof IServerConnection){
			if (((IServerConnection)o).getServer().getCurrentConnection(null) == o){
				return Activator.getDefault().getImageRegistry().get(IconsNames.connected_16);
			}
			else{
				return Activator.getDefault().getImageRegistry().get(IconsNames.disconnected_16);
			}
			
		}
		else if (o instanceof MongoDbServer){
			return Activator.getDefault().getImageRegistry().get(IconsNames.database_16);
		}
		
		else if (o instanceof Parameter){
			return Activator.getDefault().getImageRegistry().get(IconsNames.parameter_16);
		}
		return null;
	}
	
}

