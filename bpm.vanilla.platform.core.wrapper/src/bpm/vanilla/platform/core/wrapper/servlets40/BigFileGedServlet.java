package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.runtime.components.GedIndexManager;
import bpm.vanilla.platform.core.wrapper.VanillaCoreWrapper;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class BigFileGedServlet extends AbstractComponentServlet {

	private static final long serialVersionUID = 1L;

	public BigFileGedServlet(VanillaCoreWrapper vanillaCoreWrapper, IVanillaLogger logger) {
		this.logger = logger;
		this.component = vanillaCoreWrapper;
	}

	@Override
	public void init() throws ServletException {
		logger.info("Initializing GedIndexServlet...");
		super.init();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			
			Object actionResult = null;
			IGedComponent.ActionType type = null;
			try {
				String action = req.getHeader(IGedComponent.PARAM_ACTION);

				if (action.equalsIgnoreCase(IGedComponent.ActionType.CREATE_GED_DOCUMENT.toString())) {
					type = IGedComponent.ActionType.CREATE_GED_DOCUMENT;
					actionResult = createDocument(req, req.getInputStream());
				}
				else if (action.equalsIgnoreCase(IGedComponent.ActionType.ADD_VERSION.toString())) {
					type = IGedComponent.ActionType.ADD_VERSION;
					actionResult = addVersion(req, req.getInputStream());
				}
				else {
					throw new Exception("This method '" + type + "' is not supported.");
				}
				
				try {
					log(type, ((GedIndexManager) component.getGedIndexComponent()).getComponentName(), req);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception ex) {
				throw new ActionException("Operation " + (type != null ? type.name() : " with Unknown Type") + " failed - " + ex.getMessage(), ex);
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
				resp.getWriter().close();
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private GedDocument createDocument(HttpServletRequest req, InputStream inputStream) throws Exception {
		String documentName = URLDecoder.decode(req.getHeader(IGedComponent.PARAM_DOCUMENT_NAME), "UTF-8");
		String format = URLDecoder.decode(req.getHeader(IGedComponent.PARAM_FORMAT), "UTF-8");
		Integer userId = Integer.parseInt(URLDecoder.decode(req.getHeader(IGedComponent.PARAM_USER_ID), "UTF-8"));
		List<Integer> groupIds = (List<Integer>) parseGroupIds(URLDecoder.decode(req.getHeader(IGedComponent.PARAM_GROUP_IDS), "UTF-8"));
		Integer repositoryId = Integer.parseInt(URLDecoder.decode(req.getHeader(IGedComponent.PARAM_REPOSITORY_ID), "UTF-8"));

		return component.getGedIndexComponent().createDocument(documentName, format, userId, groupIds, repositoryId, inputStream);
	}

	private List<Integer> parseGroupIds(String parameter) {
		List<Integer> groupIds = new ArrayList<>();
		if (parameter != null && !parameter.isEmpty()) {
			for (String groupId : parameter.split(";")) {
				groupIds.add(Integer.parseInt(groupId));
			}
		}
		return groupIds;
	}

	private DocumentVersion addVersion(HttpServletRequest req, InputStream inputStream) throws Exception {
		Integer docId = Integer.parseInt(URLDecoder.decode(req.getHeader(IGedComponent.PARAM_DOC_ID), "UTF-8"));
		String format = URLDecoder.decode(req.getHeader(IGedComponent.PARAM_FORMAT), "UTF-8");
		return component.getGedIndexComponent().addVersionToDocumentThroughServlet(docId, format, inputStream);
	}
}
