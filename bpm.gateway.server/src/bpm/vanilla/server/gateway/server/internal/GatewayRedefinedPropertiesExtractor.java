package bpm.vanilla.server.gateway.server.internal;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;

public class GatewayRedefinedPropertiesExtractor {
	public static final String P_FULL_URL = "fullUrl";
	public static final String P_HOST = "host";
	public static final String P_PORT = "port";
	public static final String P_LOGIN = "login";
	public static final String P_PASSWORD = "password";
	public static final String P_DRIVER_NAME = "driverName";
	public static final String P_DATABASE_NAME = "database";
	
	static public enum PropertiesObjectType{
		DATABASE_CONNECTION, ALTERNATE_CONNECTION
	}
	
	static public class PropertiesIdentifier{
		PropertiesObjectType type;
		String name;
		
		public PropertiesIdentifier(PropertiesObjectType type, String name){
			this.type = type;
			this.name = name;
		}
		@Override
		public boolean equals(Object obj) {
	
			if (obj == this){
				return true;
			}
			return type == ((PropertiesIdentifier)obj).type && name.equals(((PropertiesIdentifier)obj).name);
		}
	}
	
	/**
	 * message should contain properties with the format 
	 *  : objectType.objectName.propName 
	 *  ex : 
	 *  database.sampleDb.login=root
	 *  database.sampleDb.password=root
	 *  
	 *  this will allow to override dynamically the connections information of a Gateway model
	 * @param message
	 * @return
	 */
	public static HashMap<PropertiesIdentifier, Properties> extract(IGatewayRuntimeConfig runtimeConfig){
		
		HashMap<PropertiesIdentifier, Properties> properties = new HashMap<PropertiesIdentifier, Properties>();
	
		if(runtimeConfig.getAlternateDataSourceConfiguration() != null && runtimeConfig.getAlternateDataSourceConfiguration().getDataSourcesNames() != null) {
			for(String pName : runtimeConfig.getAlternateDataSourceConfiguration().getDataSourcesNames()){
				if (!pName.startsWith(PropertiesObjectType.DATABASE_CONNECTION.name())){
					continue;
				}
				
				String[] split = pName.split("\\.");
				
				PropertiesIdentifier key = findKey(properties.keySet(), split[0], split[1]);
				
				if (properties.get(key) == null){
					properties.put(key, new Properties());
				}
				
				properties.get(key).setProperty(split[2], runtimeConfig.getAlternateDataSourceConfiguration().getConnection(pName));
			}
		}
		
		return properties;
		 
	}

	private static PropertiesIdentifier findKey(Set<PropertiesIdentifier> keySet, String keyTypeName, String objectName) {
		
		PropertiesIdentifier newKey = new PropertiesIdentifier(PropertiesObjectType.valueOf(keyTypeName), objectName);

		
		for(PropertiesIdentifier p : keySet){
			if (p.equals(newKey)){
				return p;
			}
		}
		return newKey;
	}
}
