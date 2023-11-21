package bpm.workflow.ui.views.property;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;

import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.servers.ServerMail;

/**
 * Mapper for the type of resources server
 * @author CHARBONNIER, MARTIN
 *
 */
public class CustomTypeMapper implements ITypeMapper {

	public Class mapType(Object object) {
		if (object instanceof ServerMail) {
			return ServerMail.class;
		}
		if (object instanceof FileServer) {
			return FileServer.class;
		}
		else if (object instanceof FreemetricServer){
			return FreemetricServer.class;
		}
		else if (object instanceof DataBaseServer) {
			return DataBaseServer.class;
		}
		else if (object instanceof Server){
			return Server.class;
		}
		return null;
	}

}
