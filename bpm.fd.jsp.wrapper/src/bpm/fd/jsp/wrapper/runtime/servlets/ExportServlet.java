package bpm.fd.jsp.wrapper.runtime.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class ExportServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String osName = System.getProperty("os.name");
		
		String destFile = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "/temp";
		File f = new File(destFile);
		f.mkdir();
		
		String fileName = "ExportDashboard" + new Object().hashCode() + ".pdf";
		
		destFile = destFile + "/" + fileName;
		
		if(osName.toLowerCase().contains("windows")) {
			Runtime rt = Runtime.getRuntime() ;
			String resourcesFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanilla.platform.resourcesFolder");
			String wkhtmltopdf = resourcesFolder + "/wkhtmltopdf/" + "wkhtmltopdf.exe";
			File wkhtmltopdfFile = new File(wkhtmltopdf);
			Process p = rt.exec(wkhtmltopdfFile.getAbsolutePath() + " " + req.getQueryString() + "&_isExport_=true " + destFile);
			
			String errors = IOUtils.toString(p.getErrorStream(), "UTF-8");
			System.out.println(errors);
			
		}
		else {
			//TODO do it for linux
		}
		
		FileInputStream is = new FileInputStream(destFile);
		
		resp.setHeader("Content-disposition", "attachment; filename=" + fileName);
		
		ServletOutputStream out = resp.getOutputStream();

		byte buffer[] = new byte[512 * 1024];
		int nbLecture;
		while ((nbLecture = is.read(buffer)) != -1) {
			out.write(buffer, 0, nbLecture);
		}
		is.close();
		out.close();
		
	}
}
