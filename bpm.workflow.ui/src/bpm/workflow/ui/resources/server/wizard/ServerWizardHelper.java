package bpm.workflow.ui.resources.server.wizard;

import java.util.Properties;

import bpm.workflow.runtime.resources.servers.FactoryServer;
import bpm.workflow.runtime.resources.servers.FactoryServerException;
import bpm.workflow.runtime.resources.servers.Server;


/**
 * Helper for the creation of the new server once the informations (properties) are filled
 * @author CHARBONNIER, MARTIN
 *
 */
public class ServerWizardHelper {
	
	protected static Server createServer(ServerWizard wizard) throws FactoryServerException {
		Properties generalProperties = ((ServerTypePage)wizard.getPage(ServerWizard.GENERAL_PAGE_NAME)).getValues();
		

		if (generalProperties.get("type").equals(FactoryServer.MAIL_SERVER+"")) { //$NON-NLS-1$ //$NON-NLS-2$
			Properties repositoryProperties = ((MailPage)wizard.getPage(ServerWizard.MAIL_SERVER_PAGE_NAME)).getValues();
			Properties mainProperties = ((ServerTypePage)wizard.getPage(ServerWizard.GENERAL_PAGE_NAME)).getValues();
			
			for (Object key : mainProperties.keySet()) {
				repositoryProperties.put(key, mainProperties.get(key));
			}
			
			Server server = FactoryServer.getInstance(FactoryServer.MAIL_SERVER, repositoryProperties);
			return server;
		} 
		else if (generalProperties.get("type").equals(FactoryServer.FILE_SERVER+"")) { //$NON-NLS-1$ //$NON-NLS-2$
			Properties repositoryProperties = ((FilesServerPage)wizard.getPage(ServerWizard.FILE_SERVER_PAGE_NAME)).getValues();
			Properties mainProperties = ((ServerTypePage)wizard.getPage(ServerWizard.GENERAL_PAGE_NAME)).getValues();
			
			for (Object key : mainProperties.keySet()) {
				repositoryProperties.put(key, mainProperties.get(key));
			}
			
			Server server = FactoryServer.getInstance(FactoryServer.FILE_SERVER, repositoryProperties);
			return server;
		}

		else if (generalProperties.get("type").equals(FactoryServer.DATABASE_SERVER+"")) { //$NON-NLS-1$ //$NON-NLS-2$
			Properties repositoryProperties = ((DataBasePage)wizard.getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME)).getValues();
			Properties mainProperties = ((ServerTypePage)wizard.getPage(ServerWizard.GENERAL_PAGE_NAME)).getValues();
			
			for (Object key : mainProperties.keySet()) {
				repositoryProperties.put(key, mainProperties.get(key));
			}
			
			Server server = FactoryServer.getInstance(FactoryServer.DATABASE_SERVER, repositoryProperties);
			return server;
		} 
		else if (generalProperties.get("type").equals(FactoryServer.FREEMETRICS_SERVER+"")) { //$NON-NLS-1$ //$NON-NLS-2$
			Properties repositoryProperties = ((FreeMetricsServerPage)wizard.getPage(ServerWizard.FREEMETRICS_SERVER_PAGE_NAME)).getValues();
			Properties mainProperties = ((ServerTypePage)wizard.getPage(ServerWizard.GENERAL_PAGE_NAME)).getValues();
			
			for (Object key : mainProperties.keySet()) {
				repositoryProperties.put(key, mainProperties.get(key));
			}
			
			Server server = FactoryServer.getInstance(FactoryServer.FREEMETRICS_SERVER, repositoryProperties);
			return server;
		} 		
		return null;
	}

}
