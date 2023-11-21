package bpm.vanilla.portal.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonConfiguration;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.portal.client.services.ActionsService;
import bpm.vanilla.portal.server.security.PortalSession;
import bpm.vanilla.portal.shared.repository.LinkedDocumentDTO;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ActionsServiceImpl extends RemoteServiceServlet implements ActionsService {
	
	private Logger logger = Logger.getLogger(getClass());

	private CommonConfiguration portalConfig;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		logger.info("ActionsServiceImpl initing...");
		try {
			portalConfig = CommonConfiguration.getInstance();
			
		} catch (Exception e) {
			logger.error("Failed to init ActionsService, reason : " + e.getMessage(), e);
			throw new ServletException("Failed to init ActionsService, reason : " + e.getMessage(), e);
		}
		
		logger.info("ActionsServiceImpl finished initing.");
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7186775290496824L;

	@Override
	public String checkRunnable(int itemId) throws ServiceException {
		PortalSession session = getSession();
		
		IRepositoryApi sock = session.getRepositoryConnection();
		try {
			
			boolean test = sock.getAdminService().canRun(itemId, session.getCurrentGroup().getId());
			return test + "";
		}
		catch (Exception e) {
			e.printStackTrace();
			return "<error>" + e.getMessage() + "</error>";
		} 
	}
	
	@Override
	public List<LinkedDocumentDTO> getLinkedDocuments(int itemId) throws ServiceException {
		List<LinkedDocumentDTO> res = new ArrayList<LinkedDocumentDTO>();
		
		PortalSession session = getSession();
		
		try {
			
			
			List<LinkedDocument> linked = session.getRepositoryConnection().getRepositoryService().getLinkedDocumentsForGroup(itemId, session.getCurrentGroup().getId());
			//XXX ere, problem here, see mantis #933
			//List<LinkedDocument> linked = admin.getLinkedDocument((DirectoryItem)itm/*, currGroup*/);
			
			for(LinkedDocument l : linked) {
				LinkedDocumentDTO dto = new LinkedDocumentDTO();
				dto.setName(l.getName());
				dto.setId(l.getId());
				dto.setFormat(l.getFormat());
				dto.setComment(l.getComment());
				dto.setRelativePath(l.getRelativePath());
				res.add(dto);
			}
		} catch (Exception e) {
			logger.error("Failed to retrieve LinkedDocuments for item with id " + itemId 
					+ ", reason " + e.getMessage(), e);
			return res;
		}
		
		logger.info("Retrieved " + res.size() + "LinkedDocument(s) for item with id " + itemId);
		
		return res;	
	}
	
	@Override
	public DisplayItem getLinkedDocumentUrl(int itemId, int linkDocId) throws ServiceException {
		try {
			PortalSession session = getSession();
			
			List<LinkedDocument> linkedDocs = session.getRepositoryConnection().getRepositoryService().getLinkedDocumentsForGroup(itemId, session.getCurrentGroup().getId());
		
			LinkedDocument selectedDoc = null;
			for(LinkedDocument doc : linkedDocs){
				if(doc.getId() == linkDocId){
					selectedDoc = doc;
					break;
				}
			}
			
			if(selectedDoc != null){
				InputStream is = session.getRepositoryConnection().getDocumentationService().importLinkedDocument(selectedDoc.getId());
				byte currentXMLBytes[] = IOUtils.toByteArray(is);
				ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
				ObjectInputStream obj = new ObjectInputStream();
				obj.addStream(selectedDoc.getFormat(), byteArrayIs);
				
				is.close();
				
				String reportName = selectedDoc.getName() + "_" + new Object().hashCode();
				session.addReport(reportName, obj);
				
				DisplayItem item = new DisplayItem();
				item.setKey(reportName);
				item.setOutputFormat(selectedDoc.getFormat());
				item.setCommentable(false);
				
				return item;
			}
			else {
				throw new ServiceException("The selected document couldn't be retrieve for an unknown reason.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the linked document url.", e);
		}
	}
	
}
