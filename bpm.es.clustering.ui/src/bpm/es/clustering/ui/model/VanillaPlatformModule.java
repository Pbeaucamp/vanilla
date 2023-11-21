package bpm.es.clustering.ui.model;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.beans.tasks.ServerType;
import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGatewayComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;

public class VanillaPlatformModule extends Server {

	private List<IVanillaServerManager> remotes = new ArrayList<IVanillaServerManager>();
	private Integer serverId;

	private List<Server> components = new ArrayList<Server>();

	public VanillaPlatformModule(List<Server> components) {
		this.components = components;
		setUrl(components.get(0).getUrl());
	}

	public Integer getServerId() {
		return serverId;
	}

	public IVanillaServerManager getRemoteClient(ServerType type) {
		for (IVanillaServerManager sc : remotes) {
			if ((type == ServerType.GATEWAY && sc instanceof GatewayComponent) || (type == ServerType.REPORTING && sc instanceof GatewayComponent)) {
				return sc;
			}
		}
		return null;
	}

	public void load() {
		remotes = new ArrayList<IVanillaServerManager>();
		
		IVanillaContext ctx = Activator.getDefault().getVanillaContext();

		for (Server s : components) {
			ServerType type = null;
			IVanillaServerManager sc = null;
			if (s.getComponentNature().equals(VanillaComponentType.COMPONENT_GATEWAY)) {
				type = ServerType.GATEWAY;
				sc = new RemoteGatewayComponent(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword(), getUrl());
			}
			else if (s.getComponentNature().equals(VanillaComponentType.COMPONENT_REPORTING)) {
				type = ServerType.REPORTING;
				sc = new RemoteReportRuntime(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword(), getUrl());
			}
			if (s.getComponentStatus().equals(Status.STARTED.getStatus()) && type != null) {
				try {
					sc.isStarted();
					remotes.add(sc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 
	 * @param host
	 * @return the list of Runtime Server Type that are running on the given url
	 */
	public List<IVanillaServerManager> getRegisteredRuntimeServers() {
		return remotes;
	}

	public List<Server> getRegisteredModules() {
		return components;
	}

}
