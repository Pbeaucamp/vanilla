package bpm.smart.web.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
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

import com.sun.imageio.plugins.common.InputStreamAdapter;

import bpm.gwt.commons.server.security.CommonSessionHelper;

public class ImportCode extends HttpServlet {

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
					if (fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()).equals("R") || 
							fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()).equals("Rmd")) {
						logger.debug("content type not null " + contentType);
						stream = item.getInputStream();
						
						long sizeInBytes = item.getSize();
						
						logger.debug("Field Name:"+fieldName +",File Name:"+fileName);
						logger.debug("Content Type:"+contentType +",Is In Memory:"+isInMemory+",Size:"+sizeInBytes);			 
						byte[] data = item.get();
						
//						for(int i = 0; i < data.length; i++)
//					    {
//					        res += (char) (data[i] & 0xFF);
//					    }
						res = new String(data, StandardCharsets.UTF_8);
					} 
				}
				resp.setContentType("text/html; charset=UTF-8");
				resp.getWriter().write(res);
				resp.getWriter().flush();
				resp.getWriter().close();
		        
				
				
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
