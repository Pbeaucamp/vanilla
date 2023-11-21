package bpm.gwt.commons.server.servlets;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.beans.VanillaImage;

public class UploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		CommonSession session;
		try {
			session = getSession(request);
		} catch (ServletException e1) {
			e1.printStackTrace();
			throw new UploadActionException("Session is not valid");
		}

		String response = "";
		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					IOUtils.copy(item.getInputStream(), baos);
					
					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					
					if(ImageIO.read(bais) == null) {
						ByteArrayInputStream result = new ByteArrayInputStream(baos.toByteArray());
						session.setPendingNewVersion(result);
					}
					else {
						ByteArrayInputStream result = new ByteArrayInputStream(baos.toByteArray());
						String extension = item.getName().substring(item.getName().lastIndexOf(".") + 1, item.getName().length());
						String name = item.getName().split("-")[0];
						
						if (name == null || name.isEmpty()) {
							name = item.getName();
						}
						else {
							name = name + "." + extension;
						}
						
						VanillaImage image = session.getVanillaApi().getImageManager().uploadImage(result, name);
						response = String.valueOf(image.getId());
					}
					
					

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

	private CommonSession getSession(HttpServletRequest req) throws ServletException {
		CommonSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, CommonSession.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException("Unable to get application's session: " + e1.getMessage());
		}

		return session;
	}
}