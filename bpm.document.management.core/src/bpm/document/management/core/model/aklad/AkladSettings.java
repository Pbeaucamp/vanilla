package bpm.document.management.core.model.aklad;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AkladSettings implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum DocType {
		COURRIER(1), DOCUMENT(2);
		
		private int type;
		private static Map<Integer, DocType> map = new HashMap<Integer, DocType>();
		static {
			for (DocType type : DocType.values()) {
				map.put(type.getType(), type);
			}
		}

		private DocType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
		
		public static DocType valueOf(int type) {
			return map.get(type);
		}
	}

	public enum ExportType {
		ARCHIVE(1), AKLABOX(2);

		private int type;
		private static Map<Integer, ExportType> map = new HashMap<Integer, ExportType>();
		static {
			for (ExportType type : ExportType.values()) {
				map.put(type.getType(), type);
			}
		}
		
		private ExportType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
		
		public static ExportType valueOf(int type) {
			return map.get(type);
		}
	}
	
	private int id;
	private int userId;
	private int selectedServerId = 0;
	private DocType docType = null;
	private ExportType exportType = null;
	private int folderTargetId = 0;
	private int radFolderId = 0;
	private String defaultLang = "fra";
	
	public AkladSettings() {
		super();
	}

	public AkladSettings(int userId, int selectedServerId, DocType docType,
			ExportType exportType, int folderTargetId, int radFolderId,
			String defaultLang) {
		super();
		this.userId = userId;
		this.selectedServerId = selectedServerId;
		this.docType = docType;
		this.exportType = exportType;
		this.folderTargetId = folderTargetId;
		this.radFolderId = radFolderId;
		this.defaultLang = defaultLang;
	}
	
	public AkladSettings(AkladSettings settings) {
		super();
		this.id = settings.getId();
		this.userId = settings.getUserId();
		this.selectedServerId = settings.getSelectedServerId();
		this.docType = settings.getDocTypeType();
		this.exportType = settings.getExportTypeType();
		this.folderTargetId = settings.getFolderTargetId();
		this.radFolderId = settings.getRadFolderId();
		this.defaultLang = settings.getDefaultLang();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSelectedServerId() {
		return selectedServerId;
	}

	public void setSelectedServerId(int selectedServerId) {
		this.selectedServerId = selectedServerId;
	}

	public DocType getDocTypeType() {
		return docType;
	}

	public void setDocType(DocType docType) {
		this.docType = docType;
	}

	public ExportType getExportTypeType() {
		return exportType;
	}

	public void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}

	public int getFolderTargetId() {
		return folderTargetId;
	}

	public void setFolderTargetId(int folderTargetId) {
		this.folderTargetId = folderTargetId;
	}

	public int getRadFolderId() {
		return radFolderId;
	}

	public void setRadFolderId(int radFolderId) {
		this.radFolderId = radFolderId;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}

	public int getDocType() {
		if(docType == null) {
			return 0;
		}
		return docType.getType();
	}
	
	public void setDocType(int docType) {
		this.docType = DocType.valueOf(docType);
	}
	
	public int getExportType() {
		if(exportType == null) {
			return 0;
		}
		return exportType.getType();
	}
	
	public void setExportType(int exportType) {
		this.exportType = ExportType.valueOf(exportType);
	}
}
