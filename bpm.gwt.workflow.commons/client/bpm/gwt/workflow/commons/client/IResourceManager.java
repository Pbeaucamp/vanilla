package bpm.gwt.workflow.commons.client;

import java.util.List;

import bpm.gwt.commons.client.loading.IWait;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.resources.Cible;

public interface IResourceManager {

	public List<Variable> getVariables();

	public List<Parameter> getParameters();
	
	public List<Cible> getCibles();
	
	public List<ListOfValues> getListOfValues();
	
	public List<DatabaseServer> getDatabaseServers();
	
	public void loadVariables(IWait waitPanel, IManager<Variable> manager);
	
	public void loadParameters(IWait waitPanel, IManager<Parameter> manager);
	
	public void loadCibles(IWait waitPanel, IManager<Cible> manager);
	
	public void loadListOfValues(IWait waitPanel, IManager<ListOfValues> manager);
	
	public void loadDatabaseServers(IWait waitPanel, IManager<DatabaseServer> manager);

}
