package bpm.vanilla.server.gateway.pool;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.gateway.core.AbrstractDigesterTransformation;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.GatewayDigester;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.pool.PoolableModelFactory;
import bpm.vanilla.server.commons.pool.VanillaItemKey;
import bpm.vanilla.server.gateway.server.GatewayServer;
import bpm.vanilla.server.gateway.server.GatewayServerConfig;

public class GatewayPoolableFactory extends PoolableModelFactory {
	private GatewayServer server;
	
	public GatewayPoolableFactory(GatewayServer server, Logger logger) {
		super(logger);
		this.server = server;
	}

	@Override
	protected PoolableModel createPoolableModel(RepositoryItem item, String modelXml, final VanillaItemKey itemKey) throws Exception {
		PoolableModel model = null;
		if (!item.isOn()){
			throw new VanillaException("The Gateway Model has been turned off from Entreprise Services.");
		}
		if (item.getType() == IRepositoryApi.GTW_TYPE){
			model = new PoolableModel<DocumentGateway>(item, modelXml, itemKey) {

				@Override
				protected void buildModel() throws Exception {
					try{
						server.getServerLogger().info("Set VAR_BIGATEWAY_HOME with " + ((GatewayServerConfig)server.getConfig()).getHomeFolder());
						ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue(((GatewayServerConfig)server.getConfig()).getHomeFolder() + "/");
						ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP).setValue(((GatewayServerConfig)server.getConfig()).getTempFolder()+ "/");

					}catch(Throwable t){
						t.printStackTrace();
					}
					try{
												
						server.getServerLogger().info("Start Parsing Big model");
						GatewayDigester dig = new GatewayDigester(IOUtils.toInputStream(getXml(), "UTF-8"), new ArrayList<AbrstractDigesterTransformation>());
						setModel(dig.getDocumentWithoutClean(itemKey.getRepositoryContext()));
						server.getServerLogger().info("BIG model parsed");
						
						ResourceManager mgr = getModel().getResourceManager();
						
						// define the commons variable 
						mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_BIGATEWAY_HOME).setValue(server.getConfig().getPropertyValue(GatewayServerConfig.GATEWAY_HOME) + "/");
						
						//add the variable TEMP folder
						Variable GATEWAY_TEMP = mgr.getVariable(Variable.ENVIRONMENT_VARIABLE, ResourceManager.VAR_GATEWAY_TEMP);
						GATEWAY_TEMP.setValue(server.getConfig().getPropertyValue(GatewayServerConfig.GATEWAY_TEMP) + "/"); //$NON-NLS-1$
						
					
						File f = new File(GATEWAY_TEMP.getValueAsString());
						if (!f.exists()){

							f.mkdirs();
							
						}
						
						
					}catch(Exception ex){
						logger.error("Error when building Gateway model from its xml", ex);
						throw ex;
					}
					
					
				}
				
			};
		}
		return model;
	}

}
