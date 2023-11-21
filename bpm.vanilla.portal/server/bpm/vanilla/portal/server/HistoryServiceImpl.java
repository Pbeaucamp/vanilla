package bpm.vanilla.portal.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.helper.DocumentHelper;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.repository.ReportHistoryDTO;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.services.HistoryService;
import bpm.vanilla.portal.server.security.PortalSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class HistoryServiceImpl extends RemoteServiceServlet implements HistoryService {

	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			logger.info("Initing HistoryServiceImpl");
		} catch (Exception e) {
			logger.error("Failed to init HistoryServiceImpl, reason : " + e.getMessage(), e);
			throw new ServletException("Failed to init HistoryServiceImpl, reason : " + e.getMessage(), e);
		}
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}

	@Override
	public DisplayItem getLastView(int itemId) throws ServiceException {

		logger.info("Trying to find latest historic for item with id " + itemId);

		PortalSession session = getSession();
		ReportHistoricComponent histoComponent = session.getHistoricComponent();

		try {
			DocumentVersion latest = null;

			IObjectIdentifier obj = new ObjectIdentifier(session.getCurrentRepository().getId(), itemId);

			List<GedDocument> histos = histoComponent.getReportHistoric(obj, session.getCurrentGroup().getId());
			if (histos != null && !histos.isEmpty()) {
				for (GedDocument histo : histos) {
					if (histo.getDocumentVersions() != null) {
						for (DocumentVersion doc : histo.getDocumentVersions()) {
							if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date())) && latest == null) {
								latest = doc;
							}
							else if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date())) && latest.getModificationDate().before(doc.getModificationDate())) {
								latest = doc;
							}
						}
					}
				}
			}

			if (latest == null) {
				String msg = "No history found for item with id " + itemId;
				logger.info(msg);
				throw new ServiceException(msg);
			}
			else {
				logger.info("Found view, looking up url for item " + latest.getParent().getName());
				return getHistoUrl(latest.getId(), latest.getParent().getName(), latest.getFormat());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e.getMessage());
		}

	}

	@Override
	public List<ReportHistoryDTO> getAllHistory(int itemId, Date from, Date to) throws ServiceException {
		PortalSession session = getSession();
		ReportHistoricComponent histoComponent = session.getHistoricComponent();

		List<ReportHistoryDTO> historic = new ArrayList<ReportHistoryDTO>();

		try {
			RepositoryItem item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);

			IObjectIdentifier obj = new ObjectIdentifier(session.getCurrentRepository().getId(), itemId);

			List<GedDocument> histos = histoComponent.getReportHistoric(obj, session.getCurrentGroup().getId());
			if (histos != null && !histos.isEmpty()) {
				for (GedDocument histo : histos) {
					if (histo.getDocumentVersions() != null) {
						for (DocumentVersion doc : histo.getDocumentVersions()) {
							if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date()))) {
								if (from != null && to != null) {
									if (doc.getModificationDate().after(from) && doc.getModificationDate().before(to)) {
										ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(item, doc);
										historic.add(dto);
									}
								}
								else if (from != null && to == null) {
									if (doc.getModificationDate().after(from)) {
										ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(item, doc);
										historic.add(dto);
									}
								}
								else if (from == null && to != null) {
									if (doc.getModificationDate().before(to)) {
										ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(item, doc);
										historic.add(dto);
									}
								}
								else {
									ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(item, doc);
									historic.add(dto);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			String msg = "Failed to fetch historic " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}

		return historic;
	}

	@Override
	public DisplayItem getHistoricUrl(ReportHistoryDTO itemHistory) throws ServiceException {
		return getHistoUrl(itemHistory.getHistoryId(), itemHistory.getHistoryName(), itemHistory.getHistoryFormat());
	}

	private DisplayItem getHistoUrl(int historyId, String historyName, String historyFormat) throws ServiceException {
		PortalSession session = getSession();
		ReportHistoricComponent histoComponent = session.getHistoricComponent();

		try {

			InputStream is = histoComponent.loadHistorizedDocument(historyId);

			boolean isCommentable = session.getRepositoryConnection().getDocumentationService().canComment(session.getCurrentGroup().getId(), historyId, Comment.DOCUMENT_VERSION);

			byte currentXMLBytes[] = IOUtils.toByteArray(is);
			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
			ObjectInputStream repIS = new ObjectInputStream();
			repIS.addStream(historyFormat, byteArrayIs);

			is.close();

			String fullName = historyName + "_" + new Object().hashCode();
			session.addReport(fullName, repIS);

			DisplayItem item = new DisplayItem();
			item.setKey(fullName);
			item.setOutputFormat(historyFormat);
			item.setCommentable(isCommentable);
			item.setItemId(historyId);
			item.setType(Comment.DOCUMENT_VERSION);

			return item;
		} catch (Exception e) {
			String msg = "Failed to import and show historic : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg, e);
		}
	}

	/**
	 * Given a list of items, fetch history for all of them
	 */
	@Override
	public List<ReportHistoryDTO> getHistoryForItemList(List<Integer> list, Date from, Date to) throws ServiceException {

		PortalSession session = getSession();
		ReportHistoricComponent histoComponent = session.getHistoricComponent();

		List<ReportHistoryDTO> historic = new ArrayList<ReportHistoryDTO>();

		try {
			IObjectIdentifier obj = new ObjectIdentifier(session.getCurrentRepository().getId(), -1);

			List<GedDocument> histos = histoComponent.getReportHistoric(obj, session.getCurrentGroup().getId());
			if (histos != null && !histos.isEmpty()) {
				for (GedDocument histo : histos) {
					if (histo.getDocumentVersions() != null) {
						for (DocumentVersion doc : histo.getDocumentVersions()) {
							if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date()))) {
								if (from != null && to != null) {
									if (doc.getModificationDate().after(from) && doc.getModificationDate().before(to)) {
										ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(null, doc);
										historic.add(dto);
									}
								}
								else if (from != null && to == null) {
									if (doc.getModificationDate().after(from)) {
										ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(null, doc);
										historic.add(dto);
									}
								}
								else if (from == null && to != null) {
									if (doc.getModificationDate().before(to)) {
										ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(null, doc);
										historic.add(dto);
									}
								}
								else {
									ReportHistoryDTO dto = DocumentHelper.createReportHistoryDto(null, doc);
									historic.add(dto);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			String msg = "Failed to fetch historic " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg);
		}

		return historic;

	}
}
