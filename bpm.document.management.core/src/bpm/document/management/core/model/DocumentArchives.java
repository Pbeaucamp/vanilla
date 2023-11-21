package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class DocumentArchives implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7463689130294228777L;
	private int idDocArchive;
	private Date archiveDate;
	private String archiveType;
	private String archiveName;
	private int idDocument;
	private int idArchiving;
	
	private Documents document;

	public DocumentArchives() {
		super();
	}

	public DocumentArchives(Date archiveDate, String archiveType, String archiveName, int idDocument, int idArchiving) {
		super();
		this.archiveDate = archiveDate;
		this.archiveType = archiveType;
		this.archiveName = archiveName;
		this.idDocument = idDocument;
		this.idArchiving = idArchiving;
	}

	public int getIdDocArchive() {
		return idDocArchive;
	}

	public void setIdDocArchive(int idDocArchive) {
		this.idDocArchive = idDocArchive;
	}

	public Date getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(Date archiveDate) {
		this.archiveDate = archiveDate;
	}

	public String getArchiveType() {
		return archiveType;
	}

	public void setArchiveType(String archiveType) {
		this.archiveType = archiveType;
	}

	public String getArchiveName() {
		return archiveName;
	}

	public void setArchiveName(String archiveName) {
		this.archiveName = archiveName;
	}

	public int getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(int idDocument) {
		this.idDocument = idDocument;
	}

	public Documents getDocument() {
		return document;
	}

	public void setDocument(Documents document) {
		this.document = document;
	}

	public int getIdArchiving() {
		return idArchiving;
	}

	public void setIdArchiving(int idArchiving) {
		this.idArchiving = idArchiving;
	}
	
}
