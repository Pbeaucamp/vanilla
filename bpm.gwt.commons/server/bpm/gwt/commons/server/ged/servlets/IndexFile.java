package bpm.gwt.commons.server.ged.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.beans.ged.constant.RuntimeFields;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class IndexFile extends HttpServlet {

	private static final long serialVersionUID = -2003349227760840976L;

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		String checkin = request.getParameter(CommonConstants.CHECKIN);
		String documentId = request.getParameter(CommonConstants.DOCUMENT_ID);
		String userId = request.getParameter(CommonConstants.USER_ID);
		String index = request.getParameter(CommonConstants.INDEX);

		resp.setContentType("text/html; charset=UTF-8");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		logger.debug("-- Begin index file on server UI --");
		if (isMultipart) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

//			File extFile = null;
			// Parse the request
			try {
				String name = "";
				InputStream stream = null;
				String format = null;
				if (request.getParameter(CommonConstants.IS_IN_SESSION) == null || !request.getParameter(CommonConstants.IS_IN_SESSION).equals("true")) {
					List<FileItem> items = upload.parseRequest(request);

					for (FileItem item : items) {
						logger.debug("testing item " + item.getFieldName());
						if (item.getContentType() != null) {
							logger.debug("content type not null " + item.getContentType());
							stream = item.getInputStream();
							name = item.getName();
							for (Formats f : Formats.values()) {
								if (f.getMime().equalsIgnoreCase(item.getContentType())) {
									logger.debug("find mimetype " + f.getExtension() + " for item " + item.getFieldName());
									// format = f.getExtension();
									break;
								}
							}
							if (format == null) {
								format = name.substring(name.lastIndexOf(".") + 1, name.length());
							}
						}
					}
				}
				else { // if file stream is in common session KMO 23/05/2016
					format = request.getParameter(CommonConstants.STREAM_HASHMAP_FORMAT);
					name = request.getParameter(CommonConstants.STREAM_HASHMAP_NAME) + "." + format;
					stream = getSession(request).getStream(request.getParameter(CommonConstants.STREAM_HASHMAP_NAME), format);
				}

				CommonSession session = getSession(request);

				if (checkin != null && checkin.equalsIgnoreCase("true")) {
					logger.info("Checkin document...");

					if (documentId == null || Integer.parseInt(documentId) <= 0) {
						throw new Exception("The Document cannot be found. We are not created a new version. Checkin impossible");
					}

					if (userId == null || Integer.parseInt(userId) <= 0) {
						throw new Exception("The User is not allowed to checkin the document.");
					}

					boolean canIndex = false;
					if (index != null && index.equalsIgnoreCase("true")) {
						canIndex = true;
					}

					if (stream == null) {
						throw new ServiceException("A problem happened during the upload of the file.");
					}

					session.getGedComponent().checkin(Integer.parseInt(documentId), Integer.parseInt(userId), format, stream, canIndex);

					logger.info("New version of document created successfully");
				}
				else {
					GedInformations gedInfos = session.getGedInformations();
//					String path = getServletContext().getRealPath(File.separator);
//					if (path == null)
//						path = getServletContext().getRealPath("") + "/"; // protection KMO 23/05/2016
					ByteArrayOutputStream out = new ByteArrayOutputStream();
//					extFile = new File(path + "tmp/" + name);
					try {
//						OutputStream out = new FileOutputStream(extFile);
						byte buf[] = new byte[1024];
						int len;
						while ((len = stream.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
//						out.close();
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// create comProps
					IGedComponent gedComponent = session.getGedComponent();

					List<Definition> defs = gedComponent.getFieldDefinitions(false);

					ComProperties comProps = new ComProperties();

					Definition author = findFieldByName(RuntimeFields.AUTHOR.getName(), defs);
					comProps.setProperty(author, session.getUser().getLogin());

					Definition t = findFieldByName(RuntimeFields.TITLE.getName(), defs);
					comProps.setProperty(t, gedInfos.getTitle());

					int repoId = 0;
					if (session.getCurrentRepository() != null) {
						repoId = session.getCurrentRepository().getId();
					}
					else {
						repoId = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID));
					}

					GedIndexRuntimeConfig config = new GedIndexRuntimeConfig(comProps, session.getUser().getId(), session.getCurrentGroup().getId(), gedInfos.getGroupIds(), repoId, format, gedInfos.getDocumentDefinitionId(), -1);
					config.setPeremptionDate(gedInfos.getPeremptionDate());

//					InputStream fileStream = new FileInputStream(extFile);
					InputStream fileStream = new ByteArrayInputStream(out.toByteArray());
					int result = gedComponent.index(config, fileStream);
					fileStream.close();
					logger.info("Result of indexation is " + result);
				}

				resp.getWriter().write("success");
			} catch (FileUploadException e) {
				e.printStackTrace();
				resp.getWriter().write(e.getCause().getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				resp.getWriter().write(e.getCause().getMessage());
			} finally {
//				if (extFile != null) {
//					logger.info("Delete created files");
//					extFile.delete();
//				}
			}
		}
	}

	private Definition findFieldByName(String fieldName, List<Definition> defs) {
		for (Definition def : defs) {
			if (def.getName().equals(fieldName)) {
				return def;
			}
		}

		return null;
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
