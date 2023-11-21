package bpm.workflow.ui.wizards.biwmanagement;

import java.util.HashMap;

import bpm.vanilla.platform.core.IRepositoryApi;

public interface IRepositoryWizard {

	public HashMap<String, IRepositoryApi> getRepositoryConnections();

	public bpm.vanilla.repository.ui.wizards.page.PageConnectionDefinition getPageEdit(boolean isNew);

	public void load();

	public void setRepositoryConnection(IRepositoryApi connection);

	public void setModel();

	public Object getPageRepository();

	public Object getModel();

	public IRepositoryApi getRepositoryConnection();
}
