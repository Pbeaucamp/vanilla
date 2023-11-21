package bpm.document.management.core.model;

import java.io.Serializable;

public class CopyDocuments implements Serializable {

	private static final long serialVersionUID = 7002558177534277891L;
	
	public static final String TYPE_DOCUMENT = "Document";
	public static final String TYPE_FOLDER = "Folder";
	

	private int id;
	//docId=original
	private int docId;
	private int copyDocId;
	private int fromFolder;
	private int toFolder;
	private String type;

	public CopyDocuments() {
		// TODO Auto-generated constructor stub
	}

	public CopyDocuments(int docId, int fromFolder, int toFolder, String type) {
		super();
		this.docId = docId;
		this.fromFolder = fromFolder;
		this.toFolder = toFolder;
		this.type = type;
	}
	
	public CopyDocuments(int docId, int copyDocId, int fromFolder, int toFolder) {
		super();
		this.docId = docId;
		this.fromFolder = fromFolder;
		this.toFolder = toFolder;
		this.copyDocId = copyDocId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getFromFolder() {
		return fromFolder;
	}

	public void setFromFolder(int fromFolder) {
		this.fromFolder = fromFolder;
	}

	public int getToFolder() {
		return toFolder;
	}

	public void setToFolder(int toFolder) {
		this.toFolder = toFolder;
	}

	public int getCopyDocId() {
		return copyDocId;
	}

	public void setCopyDocId(int copyDocId) {
		this.copyDocId = copyDocId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
