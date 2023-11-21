package bpm.gwt.commons.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

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

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.core.xstream.RemoteSmartManager;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class UploadFile extends HttpServlet {

	private static final long serialVersionUID = -2003349227760840976L;

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		logger.debug("-- Begin upload --");
		if (isMultipart) {

			String res = "";
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				List<FileItem> items = upload.parseRequest(request);
				InputStream stream = null;
				for (FileItem item : items) {
					String fieldName = item.getFieldName();
					String fileName = item.getName();
					String contentType = item.getContentType();
					boolean isInMemory = item.isInMemory();
					logger.debug("testing item " + fieldName);
					if ((fileName.substring(fileName.lastIndexOf(".") + 1).equals("csv")) || (fileName.substring(fileName.lastIndexOf(".") + 1).equals("xls")) || (fileName.substring(fileName.lastIndexOf(".") + 1).equals("xlsx"))) {
						logger.debug("content type not null " + contentType);
						stream = item.getInputStream();

						long sizeInBytes = item.getSize();

						logger.debug("Field Name:" + fieldName + ",File Name:" + fileName);
						logger.debug("Content Type:" + contentType + ",Is In Memory:" + isInMemory + ",Size:" + sizeInBytes);
						byte[] data = item.get();

						CommonSession session = getSession(request);

						String vanillaUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
//						String sessionId = session.getServerSessionId();
						RemoteAdminManager adm = new RemoteAdminManager(vanillaUrl, null, Locale.getDefault());
						String sessionId = adm.connect(session.getUser());
						ISmartManager manager = new RemoteSmartManager(vanillaUrl, sessionId, getLocale(request));
						
						String uploadres = manager.uploadFile(fileName, stream);
						uploadres.replace("<string>", "");
						uploadres.replace("</string>", "");
						logger.debug("File name:" + fileName);
						if (uploadres.split(";")[0].equals("success")) {
							logger.debug("File Uploaded Successfully!");
						}
						else {
							logger.debug("File Uploaded aborted!");
						}
						fileName = uploadres.split(";")[1];
						logger.debug("File Name:" + fileName);

						res = fileName;
					}
					else {
						// res = "wrong file type";
					}
				}
				resp.setContentType("text/html; charset=UTF-8");
				resp.getWriter().write(res);

			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private CommonSession getSession(HttpServletRequest req) throws ServiceException {
		return CommonSessionHelper.getCurrentSession(req, CommonSession.class);
	}

	private Locale getLocale(HttpServletRequest req) {
		return req != null ? req.getLocale() : null;
	}

}
