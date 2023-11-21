package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.fd.core.Dashboard;
import bpm.fd.core.xstream.IDashboardManager;
import bpm.fd.jsp.wrapper.helper.WebDashboardManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class WebDashboardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private XStream xstream;
	private IVanillaAPI vanillaApi;
	
	@Override
	public void init() throws ServletException {
		super.init();
		Logger.getLogger(getClass()).info("Initializing WebDashboardServlet...");
		xstream = new XStream();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;

			if (action.getActionType() == null) {
				throw new Exception("XmlAction has no actionType");
			}

			if (!(action.getActionType() instanceof IDashboardManager.ActionType)) {
				throw new Exception("ActionType not a IDashboardManager");
			}

			Date startDate = new Date();

			IDashboardManager.ActionType type = (IDashboardManager.ActionType) action.getActionType();
			
			IRepositoryApi repositoryApi = extractInformationFromRequest(req);
			
			IDashboardManager manager = new WebDashboardManager(repositoryApi);
			
			try {
				switch (type) {
					case PREVIEW_DASHBOARD:
						actionResult = manager.previewDashboard((Dashboard) args.getArguments().get(0));
						break;
					case GET_DEFAULT_CSS:
						actionResult = manager.getDefaultCssFile();
						break;
					case SAVE_DASHBOARD:
						actionResult = manager.saveDashboard((RepositoryDirectory) args.getArguments().get(0), (Dashboard) args.getArguments().get(1), (Boolean) args.getArguments().get(2), (List<Group>) args.getArguments().get(3));
						break;
					case OPEN_DASHBOARD:
						actionResult = manager.openDashboard((Integer) args.getArguments().get(0));
						break;
					}
			} catch (Exception ex) {
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			Date endDate = new Date();

			Logger.getLogger(getClass()).trace("Execution time for : " + type.toString() + " -> " + (endDate.getTime() - startDate.getTime()) + " with arguments -> " + args.toString());

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
				resp.getWriter().close();
			}

		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private IRepositoryApi extractInformationFromRequest(HttpServletRequest request) throws Exception {
		String group = request.getHeader(IRepositoryApi.HTTP_HEADER_GROUP_ID);
		if (group == null){
			throw new Exception("Missing HTTP header " + IRepositoryApi.HTTP_HEADER_GROUP_ID);
		}
		
		String repository = request.getHeader(IRepositoryApi.HTTP_HEADER_REPOSITORY_ID);
		if (repository == null){
			throw new Exception("Missing HTTP header " + IRepositoryApi.HTTP_HEADER_REPOSITORY_ID);
		}
		
		String sessionId = (String)request.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		
		String login = "";
		String password = "";
		
		if (sessionId != null){
			User u = getVanillaRootApi().getVanillaSystemManager().getSession(sessionId).getUser();
			login = u.getLogin();
			password = u.getPassword();
		}
		else {
			String authhead = request.getHeader("Authorization");
			byte[] authentication = authhead.substring(6).getBytes();
			byte[] usernpass = Base64.decodeBase64(authentication);
			String[] datas = new String(usernpass).split(":");
			
			login = datas[0];
			password = datas[1];
		}
		
		Group g = getVanillaRootApi().getVanillaSecurityManager().getGroupById(Integer.parseInt(group));
		Repository r = getVanillaRootApi().getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repository));
		
		return new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(getVanillaRootApi().getVanillaUrl(), login, password), g, r));
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private IVanillaAPI getVanillaRootApi() {
		if(vanillaApi == null) {
			vanillaApi = new RemoteVanillaPlatform(
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN),
					ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
		}
		return vanillaApi;
	}
}
