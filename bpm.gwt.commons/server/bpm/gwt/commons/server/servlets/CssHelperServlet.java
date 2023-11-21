package bpm.gwt.commons.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class CssHelperServlet extends HttpServlet {

	private Logger logger = Logger.getLogger(this.getClass());

	private static final long serialVersionUID = 6453269541206128338L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/css");

		OutputStream out = resp.getOutputStream();

		String loginCssPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_WEBAPPS_LOGIN_CSS);

		boolean loadLogin = req.getParameter("loadLoginCss") != null ? Boolean.parseBoolean(req.getParameter("loadLoginCss")) : false;
		boolean isDisconnected = req.getParameter("disconnected") != null ? Boolean.parseBoolean(req.getParameter("disconnected")) : false;
		String fileName = req.getParameter("fileName");
		if (loadLogin && fileName != null && !fileName.isEmpty()) {
			logger.info("Loading Login File");

			FileInputStream is = new FileInputStream(loginCssPath.endsWith("/") ? loginCssPath + fileName : loginCssPath + "/" + fileName);

			byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = is.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		}
		else if (isDisconnected) {
			String cssFileName = req.getParameter("cssFileName");

			logger.info("Loading css file = " + cssFileName);

			FileInputStream is = new FileInputStream(getServletContext().getRealPath(File.separator) + "css/" + cssFileName);

			byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = is.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		}
		else {
			CommonSession session;
			try {
				session = getSession(req);
			} catch (ServiceException e1) {
				e1.printStackTrace();
				throw new ServletException("Unable to get Session : " + e1.getMessage());
			}

			logger.info("Getting theme for user " + session.getUser().getLogin());

			String cssFileName = req.getParameter("cssFileName");
			if (cssFileName != null && !cssFileName.isEmpty()) {
				try {
					if (session.isConnectedToVanilla()) {
						logger.info("Updating theme for user " + session.getUser().getLogin() + " with theme " + cssFileName);
						CommonConfiguration.getInstance().changeThemeForUser(session.getUser(), cssFileName);
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error during update theme for user " + session.getUser().getLogin() + " with theme " + cssFileName, e);
				}
			}
			else if (session.getUser().getVanillaTheme() == null || session.getUser().getVanillaTheme().isEmpty()) {
				cssFileName = "vanilla_theme_default";
				try {
					logger.info("Updating theme for user " + session.getUser().getLogin() + " with theme " + cssFileName);
					CommonConfiguration.getInstance().changeThemeForUser(session.getUser(), cssFileName);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error during update theme for user " + session.getUser().getLogin() + " with theme " + cssFileName, e);
				}
			}
			else {
				cssFileName = session.getUser().getVanillaTheme();
			}
			cssFileName = cssFileName + ".css";

			logger.info("Loading css file = " + cssFileName);

			FileInputStream is = new FileInputStream(loginCssPath.endsWith("/") ? loginCssPath + cssFileName : loginCssPath + "/" + cssFileName);
			// FileInputStream is = new
			// FileInputStream(getServletContext().getRealPath(File.separator) +
			// "css/" + cssFileName);

			byte buffer[] = new byte[512 * 1024];
			int nbLecture;

			while ((nbLecture = is.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		}
	}

	private CommonSession getSession(HttpServletRequest req) throws ServiceException {
		return CommonSessionHelper.getCurrentSession(req, CommonSession.class);
	}
}