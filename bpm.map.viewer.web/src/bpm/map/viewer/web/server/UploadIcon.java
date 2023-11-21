package bpm.map.viewer.web.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
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

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class UploadIcon extends HttpServlet {

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
					if ((contentType.equals("image/jpg")) || (contentType.equals("image/png"))) {
						logger.debug("content type not null " + contentType);
						stream = item.getInputStream();
						
						long sizeInBytes = item.getSize();
						
						logger.debug("Field Name:"+fieldName +",File Name:"+fileName);
						logger.debug("Content Type:"+contentType +",Is In Memory:"+isInMemory+",Size:"+sizeInBytes);			 
						byte[] data = item.get();
						
						String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES);
						fileName = basePath + "/KpiMap_Icons/"+ fileName;
						logger.debug("File name:" +fileName);			
						FileOutputStream fileOutSt = new FileOutputStream(fileName);
						fileOutSt.write(data);
						fileOutSt.close();
						logger.debug("File Uploaded Successfully!");
						
						res = fileName;
					}
				}
				resp.getWriter().write(res);
				
		        
				
				
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
