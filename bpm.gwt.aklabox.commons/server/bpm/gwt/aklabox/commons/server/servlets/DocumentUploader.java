package bpm.gwt.aklabox.commons.server.servlets;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import bpm.document.management.core.IDocumentManager;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.TypeProcess;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.gwt.aklabox.commons.server.security.CommonSessionHelper;

public class DocumentUploader extends UploadAction {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		super.service(request, response);
	}

	/**
	 * Override executeAction to save the received files in a custom place and delete this items from session.
	 */
	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		CommonSession session = getSession();

		String version = request.getParameter(AklaboxConstant.UPLOADER_VERSION);
		String result = "";

		// This servlet is now used for all document creation. We use
		// the parameter Version=V2 to use the new method
		if (version != null && version.equals(AklaboxConstant.UPLOADER_V2)) {
			// System.out.println("TIME_TO_DELETE Time 1" + new Date());
			// logger.info("TIME_TO_DELETE Time 1" + new Date());
			//
			// logger.info("Saving files ...");

			TypeProcess process = null;

			int parentId = -1;
			int itemId = -1;
			boolean isMajor = false;

			for (FileItem item : sessionFiles) {
				if (item.isFormField()) {
					if (item.getFieldName().equals(IDocumentManager.PARAM_ACTION)) {
						process = TypeProcess.valueOf(Integer.parseInt(item.getString()));
					}
					else if (item.getFieldName().equals(IDocumentManager.PARAM_PARENT_ID)) {
						parentId = Integer.parseInt(item.getString());
					}
					else if (item.getFieldName().equals(IDocumentManager.PARAM_ITEM_ID)) {
						itemId = Integer.parseInt(item.getString());
					}
					else if (item.getFieldName().equals(IDocumentManager.PARAM_VERSION_MAJOR)) {
						isMajor = Boolean.parseBoolean(item.getString());
					}
				}
			}

			for (FileItem item : sessionFiles) {
				if (!item.isFormField()) {
					// logger.info(" File Name: " + item.getName());
					String name = item.getName();

					logger.info("Upload new file '" + name + "'");
					System.out.println("Upload new file '" + name + "'");

					// XXX For this piece of crap called "Internet Explorer"
					name = name.replace("\\", "/");
					if (name.contains("/")) {
						name = name.substring(name.lastIndexOf("/") + 1, name.length());
					}

					try {
						String detectedCharset = charset(name, new String[] { "ISO-8859-1", "UTF-8" });
						if (!detectedCharset.equalsIgnoreCase("utf-8")) {
							byte[] bytes = name.getBytes(StandardCharsets.ISO_8859_1);
							name = new String(bytes, StandardCharsets.UTF_8);
						}
					} catch (UnsupportedEncodingException e1) {
					}

					String fileName = Normalizer.normalize(name, Normalizer.Form.NFD);
					fileName = fileName.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

					logger.info("File name is '" + fileName + "'");
					System.out.println("File name is '" + fileName + "'");
					try {
						double fileSize = ((double) item.getSize()) / 1024.0;

						InputStream fileStream = item.getInputStream();

						// System.out.println("TIME_TO_DELETE Time 2" + new Date());
						// logger.info("TIME_TO_DELETE Time 2" + new Date());

						Documents doc = null;
						if (process == TypeProcess.UPLOAD_DOCUMENTS || process == TypeProcess.UPLOAD_ONE_DOCUMENT) {
							doc = session.getDocumentService().uploadDocument(parentId, fileName, fileSize, fileStream);
						}
						else if (process == TypeProcess.CHECKIN) {
							doc = session.getDocumentService().uploadNewVersion(itemId, fileName, fileSize, isMajor, fileStream);
						}

						// System.out.println("TIME_TO_DELETE Time 3" + new Date());
						// logger.info("TIME_TO_DELETE Time 3" + new Date());

						result = String.valueOf(doc.getId());
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(" File: " + item.getName() + " failed to upload.");
						throw new UploadActionException(e.getMessage());
					}
				}
			}
		}
		else {
			// logger.info("Saving files ...");
			for (FileItem item : sessionFiles) {
				if (!item.isFormField()) {
					try {

						String name = item.getName();

						// XXX For this piece of crap called "Internet Explorer"
						name = name.replace("\\", "/");
						if (name.contains("/")) {
							name = name.substring(name.lastIndexOf("/") + 1, name.length());
						}

						try {
							String detectedCharset = charset(name, new String[] { "ISO-8859-1", "UTF-8" });
							if (!detectedCharset.equalsIgnoreCase("utf-8")) {
								byte[] bytes = name.getBytes(StandardCharsets.ISO_8859_1);
								name = new String(bytes, StandardCharsets.UTF_8);
							}
						} catch (UnsupportedEncodingException e1) {
						}

						String fileName = Normalizer.normalize(name, Normalizer.Form.NFD);
						fileName = fileName.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

						double fileSize = ((double) item.getSize()) / 1024.0;

						// logger.info(" File Name: " + name);
						InputStream fileStream = item.getInputStream();
						String filePath = session.getDocumentService().uploadFile(fileName, fileSize, fileStream);

						return filePath + ";" + name;
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(" File: " + item.getName() + " failed to upload.");
						throw new UploadActionException(e.getMessage());
					}
				}
			}
		}

		removeSessionFileItems(request);

		return result;
	}

	public String convert(String value, String fromEncoding, String toEncoding) throws UnsupportedEncodingException {
		return new String(value.getBytes(fromEncoding), toEncoding);
	}

	public String charset(String value, String charsets[]) throws UnsupportedEncodingException {
		String probe = StandardCharsets.UTF_8.name();
		for (String c : charsets) {
			Charset charset = Charset.forName(c);
			if (charset != null) {
				if (value.equals(convert(convert(value, charset.name(), probe), probe, charset.name()))) {
					return c;
				}
			}
		}
		return StandardCharsets.UTF_8.name();
	}

	// private String createFile(CommonSession session, FileItem item,
	// HttpServletRequest request) throws Exception {
	// int fileSize = (int) item.getSize() / 1024;
	//
	// Documents doc = new Documents();
	// doc = saveFile(doc, "Upload", item.getName(), fileSize);
	//
	// File file = new File(doc.getFilePath());
	// item.write(file);
	//
	// new DocsController().createThumbnails(doc, "AklaBox", session, true);
	//
	// String customPath = doc.getCustomPath() != null ? doc.getCustomPath() :
	// "";
	// logger.info(" File saved as" + doc.getFilePath());
	// if (!customPath.isEmpty()) {
	// logger.info(" Custom path " + customPath);
	// }
	//
	// if (request.getParameter("RETURN_DOC") != null) {
	// return doc.getThumbImage() + ";" + doc.getFilePath() + ";" +
	// doc.getType() + ";" + doc.getFileExtension() + ";" + doc.getFileName() +
	// ";" + doc.getName() + ";" + doc.getFileSize() + "::";
	// }
	//
	// String message = fileSize + ";" + doc.getThumbImage() + ";" +
	// doc.getFilePath() + ";" + doc.getType() + ";" + doc.getFileExtension() +
	// ";" + new String(item.getName().getBytes("ISO-8859-1"), "UTF-8") + ";" +
	// customPath;
	//
	// check doublons
	// for (Documents d : session.getAklaboxService().getAllDocs()) {
	// if (d.getFileSize() == fileSize && !d.isDeleted()) {
	// message += ";" + d.getId();
	// }
	// }
	//
	// message += "::";
	//
	// return message;
	// }
	//
	// public Documents saveFile(Documents doc, String uploadType, String
	// itemName, int fileSize) throws Exception {
	//
	// String fileName = Utils.deAccent(itemName.replace("." +
	// FilenameUtils.getExtension(itemName), ""));
	// fileName = fileName + System.currentTimeMillis() + "." +
	// FilenameUtils.getExtension(itemName);
	//
	// doc.setFileSize(fileSize);
	//
	// if (new DocumentUtils().isDxfDocument(itemName)) {
	// doc.setFilePath(aklaboxFiles + "Documents/Office/" + fileName);
	// doc.setType(DocumentUtils.DXF);
	// }
	// else if (new DocumentUtils().isAudio(itemName)) {
	// doc.setThumbImage(aklaboxFiles + "images/type_audio.png");
	// doc.setFilePath(aklaboxFiles + "Documents/Audio/" + fileName.replace(" ",
	// "_"));
	// doc.setType(DocumentUtils.AUDIO);
	// }
	// else if (new DocumentUtils().isVideo(itemName)) {
	// doc.setFilePath(aklaboxFiles + "Documents/Videos/" +
	// fileName.replace(" ", "_"));
	// doc.setThumbImage(doc.getFilePath() + ".png");
	// doc.setType(DocumentUtils.VIDEO);
	// }
	// else if (new DocumentUtils().isOffice(itemName)) {
	// doc.setThumbImage(aklaboxFiles + "Documents/Office/Thumbnails/" +
	// fileName + ".jpg");
	// doc.setFilePath(aklaboxFiles + "Documents/Office/" + fileName);
	// doc.setType(DocumentUtils.OFFICE);
	// }
	// else if (new DocumentUtils().isVanilla(itemName)) {
	// doc.setThumbImage(aklaboxFiles + "images/type_audio.png");
	// doc.setFilePath(aklaboxFiles + "Documents/Vanilla/" + fileName);
	// doc.setType(DocumentUtils.VANILLA);
	// }
	// else if (new DocumentUtils().isZip(itemName)) {
	// doc.setThumbImage(aklaboxFiles + "images/type_zip.png");
	// doc.setFilePath(aklaboxFiles + "Documents/Zip/" + fileName);
	// doc.setType(DocumentUtils.ZIP);
	// }
	// else if (new DocumentUtils().isTextFile(itemName)) {
	// doc.setThumbImage(aklaboxFiles + "images/type_text.png");
	// doc.setFilePath(aklaboxFiles + "Documents/Text/" + fileName);
	// doc.setType(DocumentUtils.TEXT);
	// }
	// else if (new DocumentUtils().isXaklFile(itemName)) {
	// doc.setThumbImage(aklaboxFiles + "images/type_xakl.png");
	// doc.setFilePath(aklaboxFiles + "Documents/XaklFiles/" + fileName);
	// doc.setType(DocumentUtils.XAKL);
	// }
	// else {
	// System.err.println("createThumbImage2 " + doc.getName());
	// doc.setThumbImage(aklaboxFiles + "images/type_others.png");
	// doc.setFilePath(aklaboxFiles + "Documents/Others/" + fileName);
	// doc.setType(DocumentUtils.OTHERS);
	// }
	//
	// doc.setFileExtension(FilenameUtils.getExtension(doc.getFilePath()));
	// doc.setFileName(itemName);
	// doc.setName(itemName);
	// doc.setFileSize(fileSize);
	//
	// return doc;
	// }

	private CommonSession getSession() throws UploadActionException {
		try {
			return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UploadActionException(e.getMessage());
		}
	}

}
