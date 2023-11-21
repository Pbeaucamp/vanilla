package bpm.vanilla.repository.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.beans.historique.ReportHisto;
import bpm.vanilla.repository.beans.model.ItemModel;
import bpm.vanilla.repository.services.parsers.ExternalDocumentParser;
import bpm.vanilla.repository.services.parsers.FactoryModelParser;
import bpm.vanilla.repository.services.parsers.IModelParser;

public class ServiceDocumentationImpl implements IDocumentationService {

	private RepositoryRuntimeComponent component;
	private int groupId;
	private int repositoryId;
	private User user;

	private String contextPath;

	private String gedPath;
	private String linkedPath;
	private String extPath;
	private String pendingDocPath;

	public ServiceDocumentationImpl(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user) {
		this.component = repositoryRuntimeComponent;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		init();
	}

	private void init() {
		contextPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");

		if (contextPath == null) {
			Logger.getLogger(this.getClass()).error("Missing property bpm.vanillafiles.path within vanilla-conf file ");
		}

		File f = new File(contextPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		gedPath = contextPath + "/ged_documents";
		f = new File(gedPath);
		if (!f.exists()) {
			f.mkdirs();
		}

		pendingDocPath = contextPath + "/external_documents";
		f = new File(pendingDocPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		extPath = contextPath + "/external_documents";
		f = new File(extPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		linkedPath = contextPath + "/linked_documents/";
		f = new File(linkedPath);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	@Override
	public void addOrUpdateComment(Comment comment, List<Integer> groupIds) throws Exception {
		if(comment.getId() > 0) {
			component.getRepositoryDao(repositoryId).getCommentDao().update(comment, groupIds);
		}
		else {
			comment.setCreationDate(new Date());
			comment.setCreatorId(user.getId());
			component.getRepositoryDao(repositoryId).getCommentDao().save(comment, groupIds);
		}
	}

	@Override
	public void delete(Comment comment) throws Exception {
		component.getRepositoryDao(repositoryId).getCommentDao().delete(comment);
	}

	@Override
	public List<Comment> getComments(int groupId, int objectId, int type) throws Exception {
		return component.getRepositoryDao(repositoryId).getCommentDao().getComment(groupId, objectId, type);
	}

	@Override
	public LinkedDocument attachDocumentToItem(RepositoryItem item, InputStream is, String displayName, String comment, String format, String relativePath) throws Exception {

		relativePath = linkedPath + "ld_" + item.getItemName() + new Object().hashCode() + "." + format;
		FileOutputStream out = new FileOutputStream(relativePath);

		IOWriter.write(is, out, true, true);

		LinkedDocument doc = new LinkedDocument();

		doc.setActive(true);
		doc.setComment(comment);
		doc.setFormat(format);
		doc.setItemId(item.getId());
		doc.setName(displayName);
		doc.setRelativePath(relativePath);
		doc.setVersion(1);

		int id = component.getRepositoryDao(repositoryId).getLinkedDocumentDao().save(doc);
		doc.setId(id);

		return doc;
	}

	@Override
	public List<Integer> getReportHistoricDocumentsId(RepositoryItem item) throws Exception {
		List<ReportHisto> histos = component.getRepositoryDao(repositoryId).getReportHistoDao().getForItemId(item.getId());
		List<Integer> result = new ArrayList<Integer>();
		for (ReportHisto histo : histos) {
			result.add(histo.getGedDocId());
		}
		return result;
	}

	/**
	 * @deprecated Use {@link #importExternalDocument(RepositoryItem)} instead
	 */
	@Override
	public void importExternalDocument(RepositoryItem item, OutputStream outputStream) throws Exception {
		importExternalDocument(item);
	}

	@Override
	public InputStream importExternalDocument(RepositoryItem item) throws Exception {

		ItemModel doc = component.getRepositoryDao(repositoryId).getItemModelDao().getLastVersion(item.getId());

		IModelParser parser = FactoryModelParser.getModelParser(IRepositoryApi.EXTERNAL_DOCUMENT, null, doc.getXml());
		File file = new File(contextPath + "/" + ((ExternalDocumentParser) parser).getRelativePath());

		if (!file.exists()) {
			throw new Exception("Cannot find the file " + file.getName());
		}

		ByteArrayInputStream res = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(file)));

		return res;

	}

	@Override
	public void mapReportItemToReportDocument(RepositoryItem item, int documentId, boolean userPrivate) throws Exception {

		ReportHisto histo = new ReportHisto();
		histo.setCreation(Calendar.getInstance().getTime());
		histo.setDirectoryItemId(item.getId());
		histo.setGedDocId(documentId);
		histo.setGrpId(groupId);
		histo.setUserId(user.getId());

		ReportHisto olderReport = null;

		int nbMaxHisto = 5;
		if (item != null) {
			nbMaxHisto = item.getNbMaxHisto();
		}

		List<ReportHisto> l = component.getRepositoryDao(repositoryId).getReportHistoDao().getForItemIdAndGroupId(item.getId(), groupId, user);

		if (!(l.size() < nbMaxHisto)) {
			for (ReportHisto h : l) {
				if (olderReport == null || h.getCreation().before(olderReport.getCreation())) {
					olderReport = h;
				}
			}
		}

		if (olderReport != null && !user.isSuperUser()) {
			component.getRepositoryDao(repositoryId).getReportHistoDao().delete(olderReport);
		}

		component.getRepositoryDao(repositoryId).getReportHistoDao().save(histo);
	}

	@Override
	public String updateExternalDocument(RepositoryItem item, InputStream datas) throws Exception {
		ItemModel doc = component.getRepositoryDao(repositoryId).getItemModelDao().getLastVersion(item.getId());

		IModelParser parser = FactoryModelParser.getModelParser(IRepositoryApi.EXTERNAL_DOCUMENT, null, doc.getXml());
		File file = new File(contextPath + "/" + ((ExternalDocumentParser) parser).getRelativePath());
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		IOWriter.write(datas, fileOutputStream, true, true);

		return ((ExternalDocumentParser) parser).getRelativePath();
	}

	@Override
	public InputStream importLinkedDocument(int linkedDocumentId) throws Exception {
		String path = component.getRepositoryDao(repositoryId).getLinkedDocumentDao().findByPrimaryKey(linkedDocumentId).getRelativePath();

		ByteArrayInputStream res = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(path)));

		return res;
	}

	@Override
	public void addSecuredCommentObject(SecuredCommentObject secObject) throws Exception {
		component.getRepositoryDao(repositoryId).getCommentDao().save(secObject);
	}

	@Override
	public void addSecuredCommentObjects(List<SecuredCommentObject> secs) throws Exception {
		if(secs != null) {
			for(SecuredCommentObject secObject : secs) {
				component.getRepositoryDao(repositoryId).getCommentDao().save(secObject);
			}
		}
	}

	@Override
	public void deleteComments(int objectId, int type) throws Exception {
		component.getRepositoryDao(repositoryId).getCommentDao().deleteComments(objectId, type);
	}

	@Override
	public List<SecuredCommentObject> getSecuredCommentObjects(int objectId, int type) throws Exception {
		return component.getRepositoryDao(repositoryId).getCommentDao().getSecuredCommentObjects(repositoryId, objectId, type);
	}

	@Override
	public void removeSecuredCommentObject(int groupId, int objectId, int type) throws Exception {
		component.getRepositoryDao(repositoryId).getCommentDao().deleteSecuredCommentObject(groupId, objectId, type);
	}

	@Override
	public boolean canComment(int groupId, int objectId, int type) throws Exception {
		List<SecuredCommentObject> secs = component.getRepositoryDao(repositoryId).getCommentDao().getSecuredCommentObjects(repositoryId, objectId, type);
		if(secs != null) {
			for(SecuredCommentObject sec : secs) {
				if(sec.getGroupId() == groupId) {
					return true;
				}
				else if(groupId == -1) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void removeSecuredCommentObjects(int objectId, int type) throws Exception {
		component.getRepositoryDao(repositoryId).getCommentDao().deleteSecuredCommentObjects(objectId, type);
	}

}
