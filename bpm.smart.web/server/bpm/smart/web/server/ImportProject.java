package bpm.smart.web.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.UserSession;
import bpm.smart.web.server.security.SmartWebSession;

import com.thoughtworks.xstream.XStream;

public class ImportProject extends HttpServlet {

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
			String alternativeName = null;
			String projectName = "";
			boolean nameExists = false;
			// Parse the request
			try {
				List<FileItem> items = upload.parseRequest(request);
				InputStream stream = null;
				SmartWebSession session = CommonSessionHelper.getCurrentSession(request, SmartWebSession.class);
				int userId = session.getUser().getId();
				for (FileItem item : items) {
					if(item.isFormField()){
						String name = item.getFieldName();
					    String value = item.getString();
					    if(value.equals("")){
					    	alternativeName = null;
					    } else {
					    	alternativeName = value;
					    }
					}
				}
				for (FileItem item : items) {
					if(item.isFormField()){
						
					}
					else {
						String fieldName = item.getFieldName();
			            String fileName = item.getName();
			            String contentType = item.getContentType();
			            boolean isInMemory = item.isInMemory();
			            
			            resp.setContentType("text/html; charset=UTF-8");
			            
						logger.debug("testing item " + fieldName);
						if (fileName.substring(fileName.lastIndexOf(".")+1, fileName.length()).equals("vanillaair")) {
							logger.debug("content type not null " + contentType);
							stream = item.getInputStream();
							
							long sizeInBytes = item.getSize();
							
							logger.debug("Field Name:"+fieldName +",File Name:"+fileName);
							logger.debug("Content Type:"+contentType +",Is In Memory:"+isInMemory+",Size:"+sizeInBytes);		
							
							
							
							
							byte[] data = item.get();
							
							ZipInputStream zis = new ZipInputStream(item.getInputStream());
							ZipEntry entry;
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							byte[] buffer = new byte[2048];
							
							AirProject project = null;
							List<RScript> scripts = new ArrayList<RScript>();
							List<RScriptModel> scriptsModels = new ArrayList<RScriptModel>();
							String iconfileName, iconcontentType;
							InputStream iconstream;
							
							
							
							while ((entry = zis.getNextEntry()) != null)
							 {
							    if(entry.getName().equals("descriptor.xml")){
							    	int len = 0;
							    	bos = new ByteArrayOutputStream();
				                    while ((len = zis.read(buffer)) > 0)
				                    {
				                        bos.write(buffer, 0, len);
				                    }
				                    String xml = new String(bos.toByteArray());
				                    project = (AirProject) new XStream().fromXML(xml);
				                    if(alternativeName == null){
				                    	projectName = project.getName();
				                    } else {
				                    	projectName = alternativeName;
				                    }
				                    
				                    nameExists = session.getManager().checkProjectNameExistence(projectName);
				                    if(nameExists){
				                    	bos.close();
				                    	break;
				                    }
				                    bos.close();
							    }

							}
							if(nameExists){
								resp.getWriter().write("existentName;"+projectName);
							} else {
								session.getManager().saveAirProjectWithElements(item.getInputStream(), alternativeName, userId);
								resp.getWriter().write("success;"+projectName);
							}
						}
						else {
							resp.getWriter().write("errorType;");
						}
						
					}
					
				}
				
				
		        
				
				
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
