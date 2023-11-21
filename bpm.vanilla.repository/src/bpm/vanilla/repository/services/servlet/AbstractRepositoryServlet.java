package bpm.vanilla.repository.services.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;

import com.thoughtworks.xstream.XStream;

public abstract class AbstractRepositoryServlet extends HttpServlet{
	protected static XStream xstream;
	
	static{
		xstream = new XStream();
		xstream.alias("action", XmlAction.class);
		xstream.alias("argumentsHolder", XmlArgumentsHolder.class);
	}
	
	protected RepositoryRuntimeComponent component;

	
	public AbstractRepositoryServlet(RepositoryRuntimeComponent component) {
		this.component = component;
	}
	
	protected int extractGroupId(HttpServletRequest req) throws Exception{
		String group = req.getHeader(IRepositoryApi.HTTP_HEADER_GROUP_ID);
		if (group == null){
			throw new Exception("Missing IRepositoryApi.HTTP_HEADER_GROUP_ID header");
		}
		
		return Integer.parseInt(group);
	}
	
	protected int extractRepositoryId(HttpServletRequest req) throws Exception{
		String rep = req.getHeader(IRepositoryApi.HTTP_HEADER_REPOSITORY_ID);
		if (rep == null){
			throw new Exception("Missing IRepositoryApi.HTTP_HEADER_REPOSITORY_ID header");
		}
		
		return Integer.parseInt(rep);
	}
	
	protected User extractUser(HttpServletRequest req) throws Exception{
		String sessionId = req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		if (sessionId == null){
			sessionId = (String)req.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		}
		
		VanillaSession session = component.getVanillaRootApi().getVanillaSystemManager().getSession(sessionId);
		return session.getUser();
	}
	
}
