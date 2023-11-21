package bpm.workflow.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.WorkfowModelParameter;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.servers.ServerMail;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.viewer.TreeObject;

/**
 * Provider for the resources view
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class ResourcesContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getChildren(Object parent) {
		if(parent == ResourceViewHelper.root) {
			return ResourceViewHelper.root.getChildren().toArray(new TreeObject[ResourceViewHelper.root.getChildren().size()]);

		}
		else if(parent == ResourceViewHelper.freemetricsProvider) {
			return getServer(FreemetricServer.class);
		}
		else if(parent == ResourceViewHelper.databaseProvider) {
			return getServer(DataBaseServer.class);
		}
		else if(parent == ResourceViewHelper.fileProvider) {
			return getServer(FileServer.class);
		}
		else if(parent == ResourceViewHelper.mailProvider) {
			return getServer(ServerMail.class);
		}
		else if(parent == ResourceViewHelper.variables) {
			return getVariable(Variable.class);
		}
		else if(parent == ResourceViewHelper.parameters) {
			return getParameters();
		}

		return null;
	}

	private Object[] getParameters() {
		WorkflowModel m = (WorkflowModel) Activator.getDefault().getCurrentModel();
		if(m != null) {
			return m.getParameters().toArray(new Object[m.getParameters().size()]);
		}
		return null;
	}

	private Object[] getVariable(Class<Variable> class1) {
		List l = new ArrayList(ListVariable.getInstance().getVariables());
		return l.toArray(new Object[l.size()]);

	}

	private Object[] getServer(Class<?> serverClass) {
		List<Server> servers = ListServer.getInstance().getServers(serverClass);
		return servers.toArray();
	}

	public Object getParent(Object element) {

		if(element instanceof FreemetricServer) {
			return ResourceViewHelper.freemetricsProvider;
		}
		if(element instanceof FileServer) {
			return ResourceViewHelper.fileProvider;
		}
		else if(element instanceof DataBaseServer) {
			return ResourceViewHelper.databaseProvider;
		}
		else if(element instanceof ServerMail) {
			return ResourceViewHelper.mailProvider;
		}
		else if(element instanceof Variable) {
			return ResourceViewHelper.variables;
		}
		else if(element instanceof WorkfowModelParameter) {
			return ResourceViewHelper.parameters;
		}

		else if(element == ResourceViewHelper.root) {
			return null;
		}

		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof Server) {
			return false;
		}

		return true;
	}

}