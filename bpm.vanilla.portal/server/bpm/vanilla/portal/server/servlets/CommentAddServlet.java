package bpm.vanilla.portal.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
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
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.portal.server.security.PortalSession;

public class CommentAddServlet extends HttpServlet {

	private static final long serialVersionUID = -6600687158883925528L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			Integer extId = null;
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			
			PortalSession session = getSession(request);
			
			String comment = "";
			String type = "";
			int itemId = 0;
			Integer parentId = null;
			
			String commentId = null;
			int userId = 0;
			int docId = 0;
			
			if (isMultipart) {
				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();
	
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
				
				// Parse the request
				try {
					List<FileItem> items = upload.parseRequest(request);
					String name = "";
					InputStream stream = null;
					String format = null;
					for (FileItem item : items) {
						if(item.isFormField()) {
							String fieldName = item.getFieldName();
							if(fieldName.equals("comment")) {
								comment = item.getString();
							}
							else if(fieldName.equals("userid")) {
								userId = Integer.parseInt(item.getString());
							}
							else if(fieldName.equals("objectid")) {
								itemId = Integer.parseInt(item.getString());
							}
							else if(fieldName.equals("parentid")) {
								parentId = Integer.parseInt(item.getString());
							}
							else if(fieldName.equals("type")) {
								type = item.getString();
							}
							else if(fieldName.equals("editid")) {
								commentId = item.getString();
							}
							else if(fieldName.equals("docid")) {
								docId = Integer.parseInt(item.getString());
							}
						}
						else {
							logger.debug("testing item " + item.getFieldName());
							if (item.getContentType() != null) {
								logger.debug("content type not null " + item.getContentType());
								stream = item.getInputStream();
								name = item.getName();
								if (format == null) {
									format = name.substring(name.lastIndexOf(".") + 1, name.length());
								}
							}
						}
					}
					if(format != null && !format.equals("")) {
						RepositoryDirectory target = new Repository(session.getRepositoryConnection()).getRootDirectories().get(0);
						
						String filename = name + new Object().hashCode() + "." + format;
						File file = new File(filename);
						
						OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));
						out.write(IOUtils.toString(stream));
						out.flush();
						out.close();
						
						extId = session.getRepositoryConnection().getRepositoryService().addExternalDocumentWithDisplay(
								target, name,
								"", "", "", 
								new FileInputStream(file), false, format).getId();
						
					}
					
				} catch (FileUploadException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
//			IVanillaAPI manager = session.getVanillaApi();
//			
//			Commentaire com = null;
//			if(commentId != null) {
//				com = manager.getContributionManager().getCommentaire(Integer.parseInt(commentId));
//			}
//			else {
//				com = new Commentaire();
//			}
//			com.setCommentaire(comment);
//			com.setExternalDocId(extId);
//			com.setIdUser(userId);
//			com.setDateAjout(new Date());
//			com.setParentId(parentId);
//			com.setType(type);
//			com.setDocId(docId);
//			
//			
//			CommentableObject obj = manager.getContributionManager().getCommentableObjectsByIdObjectAndIdRepositoryAndIdHisto(itemId, session.getCurrentRepository().getId(), docId);
//			
//			manager.getContributionManager().addCommentaire(com, obj.getIdCommentableObject());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private PortalSession getSession(HttpServletRequest req) throws ServletException {
		PortalSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, PortalSession.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException("Unable to get application's session: " + e1.getMessage());
		}
		
		return session;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
}
