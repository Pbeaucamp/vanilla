package bpm.gateway.ui.views;

import java.util.Properties;

public class RuntimeConfig {
	public static final int REMOTE_RUN = 0;
	public static final int LOCAL_RUN = 1;
	
	private int type = REMOTE_RUN;
	private Properties parameters;
	private int logLevel;
//	private GatewayServerConnection sock;
	
	public int getType(){
		return type;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public void setParameters(Properties p){
		this.parameters = p;
	}
	
	public Properties getParameters(){
		return parameters;
	}

	public void setLogLevel(int lvl){
		this.logLevel = lvl;
	}
	
	public int getLogLevel() {
		return logLevel;
	}
	
//	public void setGatewayServer(String serverName) throws Exception{
//		Server s = ResourceManager.getInstance().getServer(serverName);
//		
//		
//		if (s == null || !(s instanceof GatewayServer)){
//			sock = null;
//			throw new Exception(Messages.RuntimeConfig_0);
//		}
//		
//		GatewayServer gs = (GatewayServer)s;
//		
//		sock = new GatewayServerConnection();
//		sock.setUrl(gs.getUrl());
//		sock.setLogin(gs.getLogin());
//		sock.setPassword(gs.getPassword());
//		
//		
//		
//		
//	}
//	
//	public GatewayServerConnection getSocket(){
//		return sock;
//	}
}
