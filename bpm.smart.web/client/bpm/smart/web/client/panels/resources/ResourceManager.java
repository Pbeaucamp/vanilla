package bpm.smart.web.client.panels.resources;

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
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.workflow.commons.resources.Cible;

public class ResourceManager implements IResourceManager {

	private List<Variable> variables;
	private List<Parameter> parameters;
	private List<Cible> cibles;
	private List<ListOfValues> listOfValues;

	@Override
	public List<Variable> getVariables() {
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	@Override
	public List<Cible> getCibles() {
		return cibles;
	}
	
	@Override
	public List<ListOfValues> getListOfValues() {
		return listOfValues;
	}
	
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

				for(Parameter p : parameters){
					if(p.getIdParentParam() != 0){
						for(Parameter p2 : parameters){
							if(p.getIdParentParam() == p2.getId()){
								p.setParentParam(p2);
							}
						}
					}
				}
				if (manager != null) {
					manager.loadResources(parameters);
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void loadCibles(IWait waitPanel, final IManager<Cible> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.CIBLE, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				cibles = (List<Cible>) result;

				if (manager != null) {
					manager.loadResources(cibles);
				}
			}
		}.getAsyncCallback());
	}
	
	@Override
	public void loadListOfValues(IWait waitPanel, final IManager<ListOfValues> manager) {
		waitPanel.showWaitPart(true);

		WorkflowsService.Connect.getInstance().getResources(TypeResource.LOV, new GwtCallbackWrapper<List<? extends Resource>>(waitPanel, true) {

			@Override
			@SuppressWarnings("unchecked")
			public void onSuccess(List<? extends Resource> result) {
				listOfValues = (List<ListOfValues>) result;

				if (manager != null) {
					manager.loadResources(listOfValues);
				}
			}
		}.getAsyncCallback());
	}

	@Override
	public void loadDatabaseServers(IWait waitPanel, IManager<DatabaseServer> manager) { }

	@Override
	public List<DatabaseServer> getDatabaseServers() {
		return null;
	}
}
