package bpm.document.management.core.model.aklademat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Documents;

public class PastellFile implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum FileType {
		FICHIER_FACTURE(0, Chorus.P_CPP_FICHIER_FACTURE),
		FICHIER_FACTURE_PDF(1, Chorus.P_CPP_FICHIER_FACTURE_PDF),
		FACTURE_PJ_01(2, Chorus.P_CPP_FACTURE_PJ_01),
		FACTURE_PJ_02(3, Chorus.P_CPP_FACTURE_PJ_02);

		private int type;
		private String name;

		private static Map<Integer, FileType> map = new HashMap<Integer, FileType>();
		static {
			for (FileType type : FileType.values()) {
				map.put(type.getType(), type);
			}
		}

		private FileType(int type, String name) {
			this.type = type;
			this.name = name;
		}

		public int getType() {
			return type;
		}
		
		public String getName() {
			return name;
		}

		public static FileType valueOf(int type) {
			return map.get(type);
		}
	}
	
	private FileType type;
	
	private String fileName;
	private String filePath;
	private int index;
	
	/*Transient - Set to null when saved*/
	private Documents document;
	private int documentId;
	
	public PastellFile() { }
	
	public PastellFile(FileType type, String fileName, int index) {
		this.type = type;
		this.fileName = fileName;
		this.index = index;
	}
	
	public PastellFile(FileType type, String fileName, String filePath, int index) {
		this.type = type;
		this.fileName = fileName;
		this.filePath = filePath;
		this.index = index;
	}
	
	public FileType getType() {
		return type;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Documents getDocument() {
		return document;
	}
	
	public void setDocument(Documents document) {
		this.document = document;
		if (document != null) {
			this.documentId = document.getId();
		}
	}
	
	public int getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
}
