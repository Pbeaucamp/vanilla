package bpm.vanilla.workplace.server.servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

import bpm.vanilla.workplace.server.runtime.PlaceProjectRuntime;
import bpm.vanilla.workplace.server.security.WorkplaceSession;
import bpm.vanilla.workplace.server.security.WorkplaceSessionHelper;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;

public class IndexPackageServlet extends HttpServlet {

	private static final long serialVersionUID = -2003349227760840976L;

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		logger.debug("-- Begin package creation --");
		if (isMultipart) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			try {
				List<FileItem> items = upload.parseRequest(request);
				InputStream stream = null;
				for (FileItem item : items) {
					logger.debug("testing item " + item.getFieldName());
					if (item.getContentType() != null) {
						logger.debug("content type not null " + item.getContentType());
						stream = item.getInputStream();
					}
				}
				
				WorkplaceSession session = WorkplaceSessionHelper.getCurrentSession(request);
				PlaceWebPackage newPackage = session.getPackageInformations();

				PlaceProjectRuntime projRuntime = new PlaceProjectRuntime();
				projRuntime.createPackage(getServletContext().getRealPath(File.separator),
						newPackage.getCreatorId(), newPackage.getProjectId(), newPackage, stream);
				
				logger.info("Package created with success.");
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
