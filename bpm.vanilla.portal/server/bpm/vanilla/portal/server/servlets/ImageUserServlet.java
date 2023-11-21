package bpm.vanilla.portal.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.portal.server.helper.InputStreamDatasource;

public class ImageUserServlet extends HttpServlet {

	private static final long serialVersionUID = -2003349227760840976L;

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		logger.debug("-- Begin adding image to user--");
		if (isMultipart) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			int userId = Integer.parseInt(request.getParameter("userId"));
			// Parse the request
			try {
				List<FileItem> items = upload.parseRequest(request);
				String name = "";
				InputStream stream = null;
				String format = null;
				for (FileItem item : items) {
					logger.debug("testing item " + item.getFieldName());
					if (item.getContentType() != null) {
						logger.debug("content type not null " + item.getContentType());
						stream = item.getInputStream();
						name = item.getName();
						for (Formats f : Formats.values()) {
							if (f.getMime().equalsIgnoreCase(item.getContentType())) {
								logger.debug("find format " + f.getExtension() + " for item " + item.getFieldName());
								format = f.getExtension();
								break;
							}
						}
						if (format == null) {
							format = name.substring(name.lastIndexOf(".") + 1, name.length());
						}
					}
				}
				
				if(format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")){
					
					DataSource ds = new InputStreamDatasource(stream, format);
					DataHandler dataHandler = new DataHandler(ds);
					
					CommonConfiguration.getInstance().addImageForUser(userId, dataHandler, format);
					
					logger.info("Image added with success");
				}
				else {
					logger.warn("Image not added because not a png, jpg or jpeg file.");
				}
			} catch (FileUploadException e) {
				logger.warn("Image not added because not a png, jpg or jpeg file.");
			} catch (Exception e) {
				logger.warn("Image not added because not a png, jpg or jpeg file.");
			}
		}
	}
}
