package bpm.architect.web.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import bpm.architect.web.server.security.ArchitectSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import gwtupload.shared.UConsts;

public class UploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	private Hashtable<String, File> receivedFiles = new Hashtable<String, File>();
	private Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
	
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		ArchitectSession session;
		try {
			session = getSession(request);
		} catch (ServletException e1) {
			e1.printStackTrace();
			throw new UploadActionException("Session no longer valid.");
		}
		
		String response = "";
		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				try {
					session.setPendingNewVersion(item.getInputStream());
//					File file = session.getResourceManager().addFile(getLocale(), item.getName());
//					item.write(file);

					// / Save a list with the received files
//					receivedFiles.put(item.getFieldName(), file);
					receivedContentTypes.put(item.getFieldName(), item.getContentType());

					// / Send a customized message to the client.
					response += "File added as new version.";
				} catch (Exception e) {
					throw new UploadActionException(e);
				}
			}
		}

		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);

		// / Send your customized message to the client.
		return response;
	}
	
//	private Locale getLocale() {
//		return getThreadLocalRequest() != null ? getThreadLocalRequest().getLocale() : null;
//	}

	/**
	 * Get the content of an uploaded file.
	 */
	@Override
	public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fieldName = request.getParameter(UConsts.PARAM_SHOW);
		File f = receivedFiles.get(fieldName);
		if (f != null) {
			response.setContentType(receivedContentTypes.get(fieldName));
			FileInputStream is = new FileInputStream(f);
			copyFromInputStreamToOutputStream(is, response.getOutputStream());
		}
		else {
			renderXmlResponse(request, response, XML_ERROR_ITEM_NOT_FOUND);
		}
	}

	/**
	 * Remove a file when the user sends a delete request.
	 */
	@Override
	public void removeItem(HttpServletRequest request, String fieldName) throws UploadActionException {
		File file = receivedFiles.get(fieldName);
		receivedFiles.remove(fieldName);
		receivedContentTypes.remove(fieldName);
		if (file != null) {
			file.delete();
		}
	}
	
	private ArchitectSession getSession(HttpServletRequest req) throws ServletException {
		ArchitectSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, ArchitectSession.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException("Unable to get application's session: " + e1.getMessage());
		}
		
		return session;
	}
}