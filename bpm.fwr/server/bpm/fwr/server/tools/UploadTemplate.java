package bpm.fwr.server.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

public class UploadTemplate extends HttpServlet{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6352046582561357568L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		
		response.setContentType("text/plain");
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		List items = null;
		try{
			items = upload.parseRequest(request);
			Iterator it = items.iterator();
			
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField()) {
					String name = "";
					if(item.getName().contains("\\")){
						name = item.getName().substring(item.getName().lastIndexOf("\\")+1);	
					}else{
						name = item.getName().substring(item.getName().lastIndexOf("/")+1);	
					}

					InputStream is = item.getInputStream();
					String type = name.substring(name.lastIndexOf('.')+1);
					
					ServletContext context = getServletContext();
					String location = context.getRealPath(File.separator);
					
					String path = location + File.separator + ".." + File.separator + "templates" + File.separator + name;
					
					File f = new File(path);
					FileWriter fw = null;
					try {
						fw = new FileWriter(f);
					} catch (Exception e) {
						e.printStackTrace();
					}
					fw.write(IOUtils.toString(is));
					fw.close();
				
				}
			}
		}
		catch (FileUploadException e){
			return ;
		} catch (IOException e) {
			e.printStackTrace();
		}	

		
		
		
		if (items == null) {
			try {
				response.getWriter().write("NO-SCRIPT-DATA");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

	}


}
