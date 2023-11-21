package bpm.vanillahub.runtime.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanillahub.core.IHubResourceManager;
import bpm.vanillahub.core.IHubResourceManager.ActionType;
import bpm.vanillahub.runtime.ComponentVanillaHub;
import bpm.vanillahub.runtime.security.HubSession;
import bpm.workflow.commons.remote.internal.RemoteConstants;

import com.thoughtworks.xstream.XStream;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ComponentVanillaHub component;
	protected XStream xstream;

	public ResourceServlet(ComponentVanillaHub component) throws ServletException {
		this.component = component;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String sessionId = req.getHeader(RemoteConstants.HTTP_HEADER_SESSION_ID);
			HubSession session = component.getSessionHolder().getSession(sessionId);
			
			if(session == null){
				throw new Exception("The session is not initialized. Please try to connect to a vanilla instance.");
			}
			
			IHubResourceManager manager = session.getResourceManager();
			
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a IAndroidRepositoryManager");
			}
			
			ActionType type = (ActionType)action.getActionType();

			Object actionResult = null;
			try{
				switch (type) {
				case MANAGE_USER:
					actionResult = manager.manageUser((User) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
					break;
				case REMOVE_USER:
					manager.removeUser((User) args.getArguments().get(0));
					break;
				case GET_USERS:
					actionResult = manager.getUsers();
					break;
				case MANAGE_RESOURCE:
					actionResult = manager.manageResource((Resource) args.getArguments().get(0), (Boolean) args.getArguments().get(1));
					break;
				case REMOVE_RESOURCE:
					manager.removeResource((Resource) args.getArguments().get(0));
					break;
				case GET_CRAWL_REFERENCE:
					actionResult = manager.getCrawlReference((String) args.getArguments().get(0), (String) args.getArguments().get(1), (Integer) args.getArguments().get(2), (Integer) args.getArguments().get(3));
					break;
				case GET_DRIVERS:
					actionResult = manager.getJdbcDrivers();
					break;
				case GET_RESOURCES:
					actionResult = manager.getResources((TypeResource) args.getArguments().get(0));
					break;
				case GET_WEB_SERVICE_METHODS:
					actionResult = manager.getWebServiceMethods((VariableString) args.getArguments().get(0));
					break;
				case TEST_CONNECTION:
					actionResult = manager.testConnection((DatabaseServer) args.getArguments().get(0));
					break;
				case VALID_SCRIPT:
					actionResult = manager.validScript((Variable) args.getArguments().get(0));
					break;
				case ADD_FILE:
					manager.addFile((String) args.getArguments().get(0), createStream((byte[]) args.getArguments().get(1)));
					break;
				case GET_CKAN_PACKAGE:
					actionResult = manager.getCkanDatasets((String) args.getArguments().get(0));
					break;
				case DUPLICATE_RESOURCE:
					actionResult = manager.duplicateResource((Integer) args.getArguments().get(0), (String) args.getArguments().get(1));
					break;
				}
			}catch(Exception ex){
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception e) {
			component.getLogger().error(e.getMessage(), e);
		
			resp.getWriter().write("<error>" + e.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
	
	private InputStream createStream(byte[] raw64) throws Exception {
		return new ByteArrayInputStream(Base64.decodeBase64(raw64));
	}
}
