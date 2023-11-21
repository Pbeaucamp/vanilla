package bpm.gwt.commons.server.helper;

import java.util.Date;

import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.gwt.commons.shared.repository.ReportHistoryDTO;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DocumentHelper {
	
	public static String getFormat(String newFileName) {
		int index = newFileName.lastIndexOf(".") + 1;
		return newFileName.substring(index);
	}
	
	public static DocumentDefinitionDTO transformGedDocumentToDto(GedDocument doc){
		DocumentDefinitionDTO newDoc = new DocumentDefinitionDTO();
		newDoc.setCreationDate(doc.getCreationDate());
		newDoc.setCreatorId(doc.getCreatedBy());
		newDoc.setId(doc.getId());
		newDoc.setName(doc.getName());
		newDoc.setScore(doc.getAccessCounter());
		newDoc.setGranted(doc.isGranted());
		newDoc.setMdm(doc.isMdmAttached());
		return newDoc;
	}
	
	public static DocumentVersionDTO transformDocumentVersionToDto(DocumentVersion docVersion, DocumentDefinitionDTO documentParent, String key){
		DocumentVersionDTO newVersion = new DocumentVersionDTO();
		newVersion.setId(docVersion.getId());
		newVersion.setFormat(docVersion.getFormat());
		newVersion.setSummary(docVersion.getSummary());
		newVersion.setCreationDate(docVersion.getModificationDate());
		newVersion.setName(documentParent.getName() + " - V" + docVersion.getVersion());
		newVersion.setVersion("V " + docVersion.getVersion());
		newVersion.setDocumentParent(documentParent);
		newVersion.setKey(key);
		return newVersion;
	}

	public static ReportHistoryDTO createReportHistoryDto(RepositoryItem item, DocumentVersion doc) {
		ReportHistoryDTO dto = new ReportHistoryDTO();
		dto.setModelId(item != null ? item.getId() : 0);
		dto.setModelName(item != null ? item.getItemName() : "");
		dto.setHistoryId(doc.getId());
		dto.setHistoryName(doc.getParent().getName() + " - V" + doc.getVersion());
		dto.setHistoryFormat(doc.getFormat());
		dto.setHistorySummary(doc.getSummary());
		dto.setHasBeenAccessed(false);
		dto.setCreationDate(doc.getModificationDate());
		dto.setLastAccessDate(new Date());
		dto.setDocumentId(doc.getDocumentId());
		
		
		dto.setAccessCounter(0);
		
		String username = "unknown";
		dto.setLastUserName(username);
		
		return dto;
	}
}
