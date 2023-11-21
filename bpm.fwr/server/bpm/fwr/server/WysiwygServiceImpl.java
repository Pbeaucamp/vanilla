package bpm.fwr.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.client.services.WysiwygService;
import bpm.fwr.server.security.FwrSession;
import bpm.fwr.server.tools.ServerReportHelper;
import bpm.fwr.shared.models.FusionMapDTO;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.remote.core.design.impl.RemoteFusionMapRegistry;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IReport;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.thoughtworks.xstream.XStream;

public class WysiwygServiceImpl extends RemoteServiceServlet implements WysiwygService {

	private static final long serialVersionUID = 3463919545664327443L;

	private FwrSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FwrSession.class);
	}

	@Override
	public String previewWysiwygReport(FWRReport report) throws ServiceException {
		FwrSession session = getSession();

		try {
			ObjectIdentifier objectId = new ObjectIdentifier(session.getCurrentRepository().getId(), -1);

			XStream xstream = new XStream();
			String reportXML = xstream.toXML(report);
			InputStream in = IOUtils.toInputStream(reportXML, "UTF-8");

			ServerReportHelper server = new ServerReportHelper(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());

			ObjectInputStream reportsStream = new ObjectInputStream();
			String name = new Object().hashCode() + "";

			InputStream is = server.runReport(report.getOutput().toLowerCase(), in, session.getCurrentGroup().getId(), objectId, session.getUser());
			byte currentXMLBytes[] = IOUtils.toByteArray(is);
			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
			reportsStream.addStream(report.getOutput(), byteArrayIs);
			is.close();

			session.addReport(name, reportsStream);
			return name;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = "Failed to run report, reason : " + e.getMessage();
			throw new ServiceException(msg);
		}
	}

	@Override
	public int saveWysiwygReport(FWRReport report, boolean update) throws ServiceException {
		FwrSession session = getSession();

		RepositoryItem item = null;
		if (update) {
			try {
				item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(report.getSaveOptions().getDirectoryItemid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int itemId = -1;
		try {
			XStream xstream = new XStream();
			String xml = xstream.toXML(report);

			if (item == null) {
				SaveOptions so = report.getSaveOptions();
				RepositoryDirectory dir = session.getRepositoryConnection().getRepositoryService().getDirectory(so.getDirectoryId());
				RepositoryItem p = session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FWR_TYPE, -1, dir, so.getName(), so.getComment(), so.getInternalVersion(), so.getPublicVerson(), xml, true);
				itemId = p.getId();

				try {
					Group grp = session.getVanillaApi().getVanillaSecurityManager().getGroupByName(report.getSaveOptions().getGroup());

					SecuredCommentObject secComment = new SecuredCommentObject();
					secComment.setGroupId(grp.getId());
					secComment.setObjectId(itemId);
					secComment.setType(Comment.ITEM);
					session.getRepositoryConnection().getDocumentationService().addSecuredCommentObject(secComment);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				session.getRepositoryConnection().getRepositoryService().updateModel(item, xml);
				itemId = item.getId();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return itemId;
	}

	@Override
	public String saveWysiwygReportAsBirtReport(FWRReport report) throws ServiceException {
		FwrSession session = getSession();
		InputStream is;
		try {
			is = buildRptDesign(session, report);
		} catch (Exception e1) {
			e1.printStackTrace();
			return e1.getMessage();
		}

		String rptDesign = "";
		try {
			rptDesign = IOUtils.toString(is, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return "An error happend: " + e.getMessage();
		}

		if (rptDesign == "") {
			return "An unknown error happend.";
		}

		try {
			RepositoryDirectory dir = session.getRepositoryConnection().getRepositoryService().getDirectory(report.getSaveOptions().getDirectoryId());

			session.getRepositoryConnection().getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE, dir, report.getSaveOptions().getName(), report.getSaveOptions().getComment(), report.getSaveOptions().getInternalVersion(), report.getSaveOptions().getPublicVerson(), rptDesign, true);

		} catch (Exception e) {
			e.printStackTrace();
			return "An error happend: " + e.getMessage();
		}

		return "";
	}

	@Override
	public String downloadReportAsBirt(FWRReport report) throws ServiceException {
		FwrSession session = getSession();
		try(InputStream is = buildRptDesign(session, report)) {
			byte[] buff = new byte[8000];
			int bytesRead = 0;
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			while ((bytesRead = is.read(buff)) != -1) {
				bao.write(buff, 0, bytesRead);
			}

			byte[] data = bao.toByteArray();
			ByteArrayInputStream bin = new ByteArrayInputStream(data);
			try {
				bao.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String name = report.getName() != null && !report.getName().isEmpty() ? clearName(report.getName()) : String.valueOf(new Object().hashCode());
			session.addStream(name, CommonConstants.FORMAT_RPT_DESIGN, bin);
			return name;
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServiceException(e1.getMessage(), e1);
		}
	}

	private String clearName(String name) {
		name = name.replace("'", "");
		String nfdNormalizedString = Normalizer.normalize(name, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9_-]");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	private InputStream buildRptDesign(FwrSession session, FWRReport report) throws Exception {
		ObjectIdentifier objectId = new ObjectIdentifier(session.getCurrentRepository().getId(), -1);

		XStream xstream = new XStream();
		String reportXML = xstream.toXML(report);
		InputStream in;
		try {
			in = IOUtils.toInputStream(reportXML, "UTF-8");
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new Exception("An error happend: " + e1.getMessage());
		}

		ServerReportHelper serverReportHelper = new ServerReportHelper(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());
		try {
			return serverReportHelper.saveAsBirtTask(report.getOutput().toLowerCase(), in, session.getCurrentGroup().getId(), objectId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error happend: " + e.getMessage());
		}
	}

	@Override
	public List<FusionMapDTO> getVanillaMapsAvailables() throws ServiceException {
		FwrSession session = getSession();

		IFusionMapRegistry fusionMapRegistry = new RemoteFusionMapRegistry();
		fusionMapRegistry.configure(session.getVanillaRuntimeUrl());

		List<IFusionMapObject> maps;
		try {
			maps = fusionMapRegistry.getFusionMapObjects();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ServiceException("Unable to load availables VanillaMaps - " + ex.getMessage());
		}

		List<FusionMapDTO> mapsDTO = new ArrayList<FusionMapDTO>();
		if (maps != null) {
			for (IFusionMapObject map : maps) {
				FusionMapDTO mapTmp = new FusionMapDTO();
				mapTmp.setId(map.getId());
				mapTmp.setName(map.getName());
				mapTmp.setDescription(map.getDescription());
				mapTmp.setSwfUrl(map.getSwfFileName());
				mapTmp.setType(map.getType());

				mapsDTO.add(mapTmp);
			}
		}

		return mapsDTO;
	}

	@Override
	public String buildMapHtml(FusionMapDTO selectedMap) throws ServiceException {
		return null;
	}

	@Override
	public List<Template<IReport>> getTemplates() throws ServiceException {
		FwrSession session = getSession();
		try {
			return session.getRepositoryConnection().getRepositoryService().getTemplates(true, TypeTemplate.WEB_REPORT);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get available templates : " + e.getMessage());
		}
	}

	@Override
	public Template<IReport> getTemplate(int templateId) throws ServiceException {
		FwrSession session = getSession();
		try {
			return session.getRepositoryConnection().getRepositoryService().getTemplate(templateId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the selected template : " + e.getMessage());
		}
	}

	@Override
	public void addTemplate(Template<IReport> template) throws ServiceException {
		FwrSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().addTemplate(template);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save this template : " + e.getMessage());
		}
	}

	@Override
	public void deleteTemplate(Template<IReport> template) throws ServiceException {
		FwrSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().deleteTemplate(template);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete this template : " + e.getMessage());
		}
	}
}
