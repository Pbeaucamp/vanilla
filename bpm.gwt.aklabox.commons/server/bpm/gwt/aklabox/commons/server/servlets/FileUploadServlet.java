package bpm.gwt.aklabox.commons.server.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import bpm.aklabox.workflow.core.IAklaflowConstant;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private long FILE_SIZE_LIMIT = 20 * 1024 * 1024; // 20 MiB
	private final Logger logger = Logger.getLogger("UploadServlet");

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
			fileUpload.setSizeMax(FILE_SIZE_LIMIT);

			List<FileItem> items = fileUpload.parseRequest(req);
			String filePath = "";
			for (FileItem item : items) {
				String name =  item.getName();
				
				//XXX For this piece of crap called "Internet Explorer"
				name = name.replace("\\", "/");
				if(name.contains("/")) {
					name = name.substring(name.lastIndexOf("/") + 1, name.length());
				}
				if (item.isFormField()) {
					logger.log(Level.INFO, "Received form field:");
					logger.log(Level.INFO, "Name: " + item.getFieldName());
					logger.log(Level.INFO, "Value: " + item.getString());
				}
				else {
					logger.log(Level.INFO, "Received file:");
					logger.log(Level.INFO, "Name: " + name);
					logger.log(Level.INFO, "Size: " + item.getSize());
				}

				if (!item.isFormField()) {
					if (item.getSize() > FILE_SIZE_LIMIT) {
						resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "File size exceeds limit");

						return;
					}
					
					File f = new File(IAklaflowConstant.CELL_DESTINATION + name);
//					File f = new File("D:/KMO/Eclipses/eclipse-ws/webapps/aklabox_files/Documents/Images/" + item.getName());
					filePath = f.getPath();
					item.write(f);
					if (!item.isInMemory())
						item.delete();
				}
				resp.getWriter().print(filePath);
				
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Throwing servlet exception for unhandled exception", e);
			throw new ServletException(e);
		}
	}

}