package bpm.gateway.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.viewer.TreeObject;

class ResourcesContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void dispose() { }

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

	public Object[] getChildren(Object parent) {
		if (parent == ResourceViewHelper.root){
			return ResourceViewHelper.root.getChildren().toArray(new TreeObject[ResourceViewHelper.root.getChildren().size()]);
		}
		else if (parent == ResourceViewHelper.databaseProvider){
			return getServer(Server.DATABASE_TYPE);
		}
		else if (parent == ResourceViewHelper.nosqlProvider){
			return getServer(Server.NOSQL_TYPE);
		}
		else if (parent == ResourceViewHelper.nosqlProvider){
			return getServer(Server.HBASE_TYPE);
		}
		else if (parent ==  ResourceViewHelper.nosqlProvider){
			return getServer(Server.MONGODB_TYPE);
		}
		else if (parent ==  ResourceViewHelper.d4cProvider){
			return getServer(Server.D4C_TYPE);
		}
		else if (parent == ResourceViewHelper.fileProvider){
			return getServer(Server.FILE_TYPE);
		}
		else if (parent == ResourceViewHelper.freemetricsProvider){
			return getServer(Server.FREEMETRICS_TYPE);
		}
		else if (parent == ResourceViewHelper.ldapProvider){
			return getServer(Server.LDAP_TYPE);
		}
		else if (parent instanceof Server){
			return ((Server)parent).getConnections().toArray(new IServerConnection[((Server)parent).getConnections().size()]);
		}
		else if (parent == ResourceViewHelper.variables){
			return getVariables();
		}
		else if (parent == ResourceViewHelper.parameters){
			return getParameters();
		}
		else if (parent == ResourceViewHelper.localVariables){
			return getLocalVariables();
		}
		
		
		return null;
	}

	
	private Object[] getLocalVariables() {
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();
		
		Assert.isTrue(in instanceof GatewayEditorInput);
		List<Variable> l = ((GatewayEditorInput)in).getDocumentGateway().getVariables();
		
		return l.toArray(new Variable[l.size()]);
	}

	private Object[] getParameters() {
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part == null){
			return null;
		}
		
		IEditorInput in = part.getEditorInput();
		
		
		if (in  instanceof GatewayEditorInput){
			List l = ((GatewayEditorInput)in).getDocumentGateway().getParameters();
			return l.toArray(new Parameter[l.size()]);
		}
		
		return null;
	}

	private Object[] getVariables(){
		List l = ResourceManager.getInstance().getVariables(Variable.ENVIRONMENT_VARIABLE);
		return l.toArray(new Variable[l.size()]);
	}
	
	private Object[] getServer(String type){
		List<Server> l = new ArrayList<Server>();
		
		if (type ==  Server.FREEMETRICS_TYPE){
			for(Server s : ResourceManager.getInstance().getServers()){
				if (s.getType() == Server.FREEMETRICS_TYPE ){
					l.add(s);
				}
			}
		}
		
		else if (type ==  Server.DATABASE_TYPE){
			for(Server s : ResourceManager.getInstance().getServers()){
				if (s.getType() == Server.DATABASE_TYPE ){
					l.add(s);
				}
			}
		}
		
		else if (type ==  Server.NOSQL_TYPE){
			for(Server s : ResourceManager.getInstance().getServers()){
				if (s.getType() == Server.CASSANDRA_TYPE){
					l.add(s);
				}
				else if(s.getType() == Server.HBASE_TYPE){
					l.add(s);
				}
				else if(s.getType() == Server.MONGODB_TYPE){
					l.add(s);
				}
			}
		}
		
		else if (type ==  Server.D4C_TYPE){
			for(Server s : ResourceManager.getInstance().getServers()){
				if (s.getType() == Server.D4C_TYPE){
					l.add(s);
				}
			}
		}
		
		else if (type ==  Server.FILE_TYPE){
			for(Server s : ResourceManager.getInstance().getServers()){
				if (s.getType() == Server.FILE_TYPE ){
					l.add(s);
				}
			}
		}
		
		else if (type ==  Server.LDAP_TYPE){
			for(Server s : ResourceManager.getInstance().getServers()){
				if (s.getType() == Server.LDAP_TYPE ){
					l.add(s);
				}
			}
		}
		
		return l.toArray(new Server[l.size()]);
	}
	
	
	public Object getParent(Object element) {
		if (element instanceof DataBaseServer){
			return ResourceViewHelper.databaseProvider;
		}
		else if (element instanceof CassandraServer){
			return ResourceViewHelper.nosqlProvider;
		}
		else if (element instanceof HBaseServer){
			return ResourceViewHelper.nosqlProvider;
		}
		else if (element instanceof MongoDbServer){
			return ResourceViewHelper.nosqlProvider;
		}
		else if (element instanceof D4CServer){
			return ResourceViewHelper.d4cProvider;
		}
		else if (element instanceof FileSystemServer){
			return ResourceViewHelper.fileProvider;
		}
		else if (element instanceof IServerConnection){
			return ((IServerConnection)element).getServer();
		}
		else if (element == ResourceViewHelper.root){
			return null;
		}
		else if (element instanceof Variable){
			return ResourceViewHelper.localVariables;
		}
		
		
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IServerConnection){
			return false;
		}
		if (element instanceof Variable){
			return false;
		}
		
		return true;
	}
	
}