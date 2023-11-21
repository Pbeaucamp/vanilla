package bpm.vanillahub.web.server.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

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
		String fileName = req.getParameter("fileName");
		if(loadLogin && fileName != null && !fileName.isEmpty()){
    		logger.info("Loading Login File");
        	
        	FileInputStream is = new FileInputStream(loginCssPath.endsWith("/") ? loginCssPath + fileName : loginCssPath + "/" + fileName);
    	    
    	    byte buffer[]=new byte[512*1024]; 
    		int nbLecture;
    	
    		while((nbLecture = is.read(buffer)) != -1 ){
    			out.write(buffer, 0, nbLecture);
    		}
    		is.close();
    		out.close();
		}
		else {
			logger.info("Loading theme");
			
			String cssFileName = req.getParameter("cssFileName");
			cssFileName = cssFileName  + ".css";
			
			logger.info("Loading css file = " + cssFileName);

        	FileInputStream is = new FileInputStream(loginCssPath.endsWith("/") ? loginCssPath + cssFileName : loginCssPath + "/" + cssFileName);

		    byte buffer[]=new byte[512*1024]; 
			int nbLecture;
		
			while((nbLecture = is.read(buffer)) != -1 ){
				out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		}
	}
}  