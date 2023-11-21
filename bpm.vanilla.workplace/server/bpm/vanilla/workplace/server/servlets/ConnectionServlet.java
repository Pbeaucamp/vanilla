package bpm.vanilla.workplace.server.servlets;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IProject;
import bpm.vanilla.workplace.core.IUser;
import bpm.vanilla.workplace.core.PlaceConstants;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.PlacePackage;
import bpm.vanilla.workplace.core.model.PlaceProject;
import bpm.vanilla.workplace.core.model.PlaceUser;
import bpm.vanilla.workplace.core.services.VanillaPlaceService;
import bpm.vanilla.workplace.core.xstream.IXmlActionType;
import bpm.vanilla.workplace.core.xstream.XmlAction;
import bpm.vanilla.workplace.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.workplace.server.runtime.PlaceProjectRuntime;
import bpm.vanilla.workplace.server.runtime.PlaceUserRuntime;
import bpm.vanilla.workplace.shared.exceptions.ServiceException;

import com.thoughtworks.xstream.XStream;

public class ConnectionServlet extends HttpServlet{
	private static final String VALIDATE_USER_SUCCESS = "You are now registered. You can connect to Vanilla Place from ES";
	private static final String VALIDATE_USER_FAILED = "The register operation failed. Please, try again later.";

	private static final long serialVersionUID = 7702059323883547957L;
	
	private Logger logger = Logger.getLogger(ConnectionServlet.class);
	private XStream xstream;
	private PlaceUserRuntime workplaceUserRuntime;
	private PlaceProjectRuntime workplaceProjectRuntime;
	
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Initializing ExternalAccessibilityServlet...");
		
		try {
			workplaceUserRuntime = new PlaceUserRuntime();
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new ServletException(e.getMessage());
		}
		
		try {
			workplaceProjectRuntime = new PlaceProjectRuntime();
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new ServletException(e.getMessage());
		}
		
		xstream = new XStream();
		xstream.alias("action", XmlAction.class);
		xstream.alias("actionType", IXmlActionType.class, VanillaPlaceService.ActionType.class);
		xstream.alias("argumentsHolder", XmlArgumentsHolder.class);
		xstream.alias("user", PlaceUser.class);
		xstream.alias("project", PlaceProject.class);
		xstream.alias("package", PlacePackage.class);
		xstream.alias("directory", PlaceImportDirectory.class);
		xstream.alias("item", PlaceImportItem.class);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String login = req.getParameter(PlaceConstants.LOGIN);
		String hashCode = req.getParameter(PlaceConstants.HASH_PARAMETER);
		if(hashCode != null && !hashCode.isEmpty()){
			boolean valid = workplaceUserRuntime.validateUser(login, hashCode);
			if(valid){
				resp.getWriter().write(VALIDATE_USER_SUCCESS);
			}
			else {
				resp.getWriter().write(VALIDATE_USER_FAILED);
			}
		}
		resp.getWriter().close();
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

			if (!(action.getActionType() instanceof VanillaPlaceService.ActionType)) {
				throw new Exception("ActionType not a IRepositoryManager");
			}

			VanillaPlaceService.ActionType type = (VanillaPlaceService.ActionType) action.getActionType();
			try {
				switch (type) {
				case CREATE_USER:
					actionResult = workplaceUserRuntime.createUserAndSendMail(getServletContext().getRealPath(File.separator),
							req.getRequestURL().toString(), (IUser)args.getArguments().get((0)), req);
					break;
				case CONNECT:
					String login = (String)args.getArguments().get(0);
					String password = (String)args.getArguments().get(1);
					actionResult = workplaceUserRuntime.authentifyUser(login, password, req);
					break;
				case GET_PACKAGES:
					int userId = (Integer)args.getArguments().get(0);
					actionResult = workplaceProjectRuntime.getProjectForUser(getServletContext().getRealPath(File.separator), userId);
					break;
				case EXPORT_PACKAGE:
					int userId2 = (Integer)args.getArguments().get(0);
					IProject project = (IProject)args.getArguments().get(1);
					IPackage vanillaPackage = (IPackage)args.getArguments().get(2);
					byte[] encoded = (byte[])args.getArguments().get(3);
					actionResult = workplaceProjectRuntime.createPackageWithProject(getServletContext().getRealPath(File.separator),
							userId2, project, vanillaPackage, new ByteArrayInputStream(Base64.decodeBase64(encoded)));
					break;
				case IMPORT_PACKAGE:
					userId = (Integer)args.getArguments().get(0);
					IPackage pack = (IPackage)args.getArguments().get(1);
					actionResult = workplaceProjectRuntime.getPackageStream(getServletContext().getRealPath(File.separator), 
							pack, userId);
					break;
				}
			} catch (Exception ex) {
				logger.error(ex);
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
}
