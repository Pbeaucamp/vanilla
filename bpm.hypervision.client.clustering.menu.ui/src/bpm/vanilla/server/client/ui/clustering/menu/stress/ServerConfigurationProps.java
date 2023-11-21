package bpm.vanilla.server.client.ui.clustering.menu.stress;

import bpm.vanilla.platform.core.beans.ServerConfigInfo;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;

public class ServerConfigurationProps {

	public Integer numberTasks;
	public Integer numberRepConnection;
	
	public ServerConfigurationProps(Integer numberTasks, Integer numberRepConnection){
		this.numberRepConnection = numberRepConnection;
		this.numberTasks = numberTasks;
	}

	public ServerConfigInfo update(ServerConfigInfo serverConfig) {
		if (serverConfig != null){
			if (numberTasks != null){
				serverConfig.setValue("maximumRunningTasks", "" + numberTasks.intValue()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (numberRepConnection != null){
				serverConfig.setValue("repositoryPoolSize", "" + numberRepConnection.intValue()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return serverConfig;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		if (numberTasks != null){
			b.append(numberTasks + "_tasks_"); //$NON-NLS-1$
		}
		
		if (numberRepConnection != null){
			b.append(numberRepConnection + "_socks_"); //$NON-NLS-1$
		}
		return b.toString();
	}
	
}
