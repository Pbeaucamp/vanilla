package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.service.ServiceTransformationDefinition;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IVanillaWebServiceComponent {

	public static enum ActionType implements IXmlActionType {
		ADD_WEBSERVICE_DEFINITION(Level.INFO), UPDATE_WEBSERVICE_DEFINITION(Level.INFO), DELETE_WEBSERVICE_DEFINITION(Level.INFO), 
		GET_WEBSERVICE_DEFINITION(Level.DEBUG), GET_WEBSERVICE_ALLDEFINTIONS(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public void addWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception;
	
	public void updateWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception;
	
	public void deleteWebServiceDefinition(ServiceTransformationDefinition definition) throws Exception;
	
	public void deleteWebServiceDefinition(int definitionId) throws Exception;
	
	public ServiceTransformationDefinition getWebServiceDefinition(int defintionId) throws Exception;
	
	public List<ServiceTransformationDefinition> getWebServiceDefinitions() throws Exception;
	
}
