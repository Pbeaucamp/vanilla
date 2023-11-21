package bpm.vanilla.server.client.commandline.profiing;

import bpm.vanilla.server.client.communicators.ServerConfigInfo;

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
				serverConfig.setValue("maximumRunningTasks", "" + numberTasks.intValue());
			}
			if (numberRepConnection != null){
				serverConfig.setValue("repositoryPoolSize", "" + numberRepConnection.intValue());
			}
		}
		return serverConfig;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		if (numberTasks != null){
			b.append(numberTasks + "_tasks_");
		}
		
		if (numberRepConnection != null){
			b.append(numberRepConnection + "_socks_");
		}
		return b.toString();
	}
	
}
