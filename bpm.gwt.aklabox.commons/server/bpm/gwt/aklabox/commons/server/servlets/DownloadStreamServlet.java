package bpm.gwt.aklabox.commons.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.aklademat.PastellFile.FileType;
import bpm.document.management.core.utils.Formats;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.gwt.aklabox.commons.server.security.CommonSessionHelper;
import bpm.gwt.aklabox.commons.shared.CommonConstants;

public class DownloadStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -7156481441321370835L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String name = req.getParameter(CommonConstants.STREAM_HASHMAP_NAME);
		String documentId = req.getParameter(CommonConstants.STREAM_DOCUMENT_ID);
		String format = req.getParameter(CommonConstants.STREAM_HASHMAP_FORMAT);
		String checkout = req.getParameter(CommonConstants.STREAM_CHECKOUT);
		String typeDocument = req.getParameter(CommonConstants.STREAM_TYPE_DOCUMENT);

		InputStream is = null;
		try {
			CommonSession session = getSession(req);

			if (name != null) {
				is = session.getStream(name, format);
			}
			else if (documentId != null) {
				if (typeDocument == null || Integer.parseInt(typeDocument) == CommonConstants.STREAM_TYPE_DOCUMENT_CLASSIC) {
					int docId = Integer.parseInt(documentId);
					Documents doc = session.getAklaboxService().getDocById(docId);
					name = doc.getFileName();
					
					if (doc.getFileExtension() != null) {
						format = doc.getFileExtension();
					}
					
					is = new FileInputStream(new File(doc.getFilePath()));
				}
				else if (Integer.parseInt(typeDocument) == CommonConstants.STREAM_TYPE_AKLADEMAT_ENTITY_RECEIPT) {
					//Not used for now
//					AkladematDocumentEntity entity = session.getAkladematService().getAkladematEntity(docId);
//					name = entity.getName() + "_accuse_reception";
//					
//					is = new FileInputStream(new File(entity.getPathReceiptDocument()));
				}
				else if (Integer.parseInt(typeDocument) == CommonConstants.STREAM_TYPE_AKLADEMAT_ENTITY_RETURN) {
					//Not used for now
//					AkladematDocumentEntity entity = session.getAkladematService().getAkladematEntity(docId);
//					name = entity.getName() + "_document_retour";
//
//					is = new FileInputStream(new File(entity.getPathReturnDocument()));
				}
				else if (Integer.parseInt(typeDocument) == CommonConstants.STREAM_TYPE_AKLADEMAT_ENTITY) {
					FileType fileType = FileType.valueOf(Integer.parseInt(req.getParameter(CommonConstants.STREAM_TYPE_FILE)));
					String fileName = req.getParameter(CommonConstants.STREAM_FILE_NAME);
					int index = Integer.parseInt(req.getParameter(CommonConstants.STREAM_FILE_INDEX));
					
					name = extractName(fileName);
					format = extractFormat(fileName);
					
					is = session.getAkladematService().getFileStream(documentId, fileType, index);
				}
			}
			else {
				throw new Exception("Unable to get document.");
			}
			
			if (format != null) {
				String mime = "";
				for (Formats f : Formats.values()) {
					if (f.getExtension().equals(format)) {
						mime = f.getMime();
						break;
					}
				}
				if (!mime.equals("")) {
					resp.setContentType(mime);
				}
			}
			
			if (checkout == null || !checkout.equalsIgnoreCase("true")) {
				resp.setHeader("Content-disposition", "attachment; filename=" + name + "." + format);
			}
			else {
				resp.setHeader("Content-disposition", "filename=" + name + "." + format);
			}

			ServletOutputStream out = resp.getOutputStream();

			byte buffer[] = new byte[512 * 1024];
			int nbLecture;
			while ((nbLecture = is.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			is.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();

			InputStream errorStream = createHTMLErrorResponse(e);

			ServletOutputStream out = resp.getOutputStream();
			byte buffer[] = new byte[512 * 1024];
			int nbLecture;
			while ((nbLecture = errorStream.read(buffer)) != -1) {
				out.write(buffer, 0, nbLecture);
			}
			errorStream.close();
			out.close();
		} finally {
			if (is != null) {
				is.reset();
			}
		}
	}

	private String extractName(String fileName) {
		if (fileName.contains(".")) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
		return null;
	}

	private String extractFormat(String fileName) {
		if (fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		}
		return null;
	}

	private InputStream createHTMLErrorResponse(Throwable caught) {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>");
		buf.append("	<head></head>");
		buf.append("	<body>");
		buf.append("		<h1>Informations</h1>");
		buf.append("		<p>This document is not available at this time. Sorry for the inconveniance. <br/> The following informations can help you understand the problem :</p>");
		buf.append("		<p style=\"margin: 15px; background-color: #E6E6E6; padding: 20px; font-size: 12px;\">" + ExceptionUtils.getStackTrace(caught).replace("\n", "<br/>") + "</p>");
		buf.append("	</body>");
		buf.append("</html>");
		return IOUtils.toInputStream(buf.toString());
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
