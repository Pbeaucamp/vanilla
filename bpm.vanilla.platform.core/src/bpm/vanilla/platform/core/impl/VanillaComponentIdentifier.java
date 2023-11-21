package bpm.vanilla.platform.core.impl;

import java.util.Properties;

import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;

public class VanillaComponentIdentifier implements IVanillaComponentIdentifier{
	private Properties prop;
	
	public VanillaComponentIdentifier() {
	}
	
	public VanillaComponentIdentifier(String nature, String protocole, String ip, String port, String contextPath,
			String id, String status){

		prop = new Properties();
		
		prop.setProperty(P_COMPONENT_NATURE, nature);
		prop.setProperty(P_COMPONENT_PROTOCOLE, protocole);
		prop.setProperty(P_COMPONENT_IP, ip);
		prop.setProperty(P_COMPONENT_PORT, port);
		prop.setProperty(P_COMPONENT_CONTEXT_NAME, contextPath);
		prop.setProperty(P_COMPONENT_ID, id);
		prop.setProperty(P_COMPONENT_STATUS, status);
		
	}
	
	@Override
	public Properties getProperties() {
		return prop;
	}

	@Override
	public String getComponentProtocole() {
		return prop.getProperty(P_COMPONENT_PROTOCOLE);
	}

	@Override
	public String getComponentId() {
		return prop.getProperty(P_COMPONENT_ID);
	}

	@Override
	public String getComponentIp() {
		return prop.getProperty(P_COMPONENT_IP);
	}

	@Override
	public String getComponentNature() {
		return prop.getProperty(P_COMPONENT_NATURE);
	}

	@Override
	public String getComponentPort() {
		return prop.getProperty(P_COMPONENT_PORT);
	}
	
	@Override
	public String getComponentContextName() {
		return prop.getProperty(P_COMPONENT_CONTEXT_NAME);
	}
	
	@Override
	public String getComponentStatus() {
		return prop.getProperty(P_COMPONENT_STATUS);
	}

	@Override
	public String getComponentUrl() {
		StringBuilder b = new StringBuilder();
		b.append(getComponentProtocole());
		b.append(getComponentIp());
		b.append(":");
		b.append(getComponentPort());
		if (!getComponentContextName().startsWith("/") && !getComponentContextName().isEmpty()){
			b.append("/");
		}
		
		b.append(getComponentContextName());
		
		return b.toString();
	}
	
	@Override
	public void setComponentStatus(String status) {
		prop.setProperty(P_COMPONENT_STATUS, status);	
	}

//	@Override
//	public String[] getComponentSupportedEvent() {
//		if (prop.getProperty(P_EVENT_SUPPORTED).isEmpty()){
//			return new String[0];
//		}
//		return prop.getProperty(P_EVENT_SUPPORTED).split(";");
//	}

}
