package bpm.architect.web.client.panels;

import java.util.List;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.resources.Cible;

public class ResourceManager implements IResourceManager {

	private List<Variable> variables;
	private List<Parameter> parameters;
	private List<DatabaseServer> databaseServers;

	@Override
	public List<Cible> getCibles() {
		return null;
	}

	@Override
	public List<Variable> getVariables() {
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	public List<DatabaseServer> getDatabaseServers() {
		return databaseServers;
	}
	
	@Override
	public List<ListOfValues> getListOfValues() {
		return null;
	}

	@Override
	public void loadCibles(IWait waitPanel, final IManager<Cible> manager) {}

	@Override
	public void loadVariables(IWait waitPanel, final IManager<Variable> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.VARIABLE, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				variables = (List<Variable>) result;

				if (manager != null) {
					manager.loadResources(variables);
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void loadParameters(IWait waitPanel, final IManager<Parameter> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.PARAMETER, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				parameters = (List<Parameter>) result;

				if (manager != null) {
					manager.loadResources(parameters);
				}
			}
		}.getAsyncCallback());
	}

	public void loadDatabaseServers(IWait waitPanel, final IManager<DatabaseServer> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.DATABASE_SERVER, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				databaseServers = (List<DatabaseServer>) result;

				if (manager != null) {
					manager.loadResources(databaseServers);
				}
			}
		}.getAsyncCallback());
	}

	public void loadListOfValues(IWait waitPanel, final IManager<ListOfValues> manager) {}
}
