package bpm.es.clustering.ui.gef;

import java.util.List;

import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;

public class GefModel {
	private List<Server> clients;
	private List<VanillaPlatformModule> modules;
	private VanillaPlatformModule defaultModule;
	private List<Repository> repositories;
	/**
	 * @param clients
	 * @param modules
	 * @param defaultModule
	 * @param repositories
	 */
	public GefModel(List<Server> clients, List<VanillaPlatformModule> modules,
			VanillaPlatformModule defaultModule,
			List<Repository> repositories) {
		super();
		this.clients = clients;
		this.modules = modules;
		this.defaultModule = defaultModule;
		this.repositories = repositories;
	}
	/**
	 * @return the clients
	 */
	public List<Server> getClients() {
		return clients;
	}
	/**
	 * @return the modules
	 */
	public List<VanillaPlatformModule> getModules() {
		return modules;
	}
	/**
	 * @return the defaultModule
	 */
	public VanillaPlatformModule getDefaultModule() {
		return defaultModule;
	}
	/**
	 * @return the repositories
	 */
	public List<Repository> getRepositories() {
		return repositories;
	}
	
	
	
	
}
