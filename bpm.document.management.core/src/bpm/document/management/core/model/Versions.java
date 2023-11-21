package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class Versions implements Serializable {

	private static final long serialVersionUID = 1L;

	private int versionId = 0;
	private int docId = 0;
	private String docName = "";
	private String docRelPath = "";
	private String stampPath = "";
	private int versionNumber = 0;
	private String modifiedBy = "";
	private Date versionDate = new Date();
	private String versionComment = "";
	private int versionFileSize = 0;
	private boolean hadoopFile = false;
	private Boolean hide = false;
	private boolean major = true;
	
	private String hash;

	public Versions() {
		super();
	}

	public Versions(int docId, String docName, String docRelPath, int versionNumber, String modifiedBy, Date versionDate, String versionComment, int versionFileSize, boolean hadoopFile) {
		super();
		this.docId = docId;
		this.docName = docName;
		this.docRelPath = docRelPath;
		this.versionNumber = versionNumber;
		this.modifiedBy = modifiedBy;
		this.versionDate = versionDate;
		this.versionComment = versionComment;
		this.versionFileSize = versionFileSize;
		this.hadoopFile = hadoopFile;
	}

	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public Date getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getDocRelPath() {
		return stampPath != null && !stampPath.isEmpty() ? stampPath : docRelPath;
	}

	public void setDocRelPath(String docRelPath) {
		this.docRelPath = docRelPath;
	}
	
	public String getStampPath() {
		return stampPath;
	}
	
	public void setStampPath(String stampPath) {
		this.stampPath = stampPath;
	}

	public int getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getVersionComment() {
		return versionComment;
	}

	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	public int getVersionFileSize() {
		return versionFileSize;
	}

	public void setVersionFileSize(int versionFileSize) {
		this.versionFileSize = versionFileSize;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public boolean isHadoopFile() {
		return hadoopFile;
	}

	public void setHadoopFile(boolean hadoopFile) {
		this.hadoopFile = hadoopFile;
	}

	public Boolean isHide() {
		return hide;
	}

	public void setHide(Boolean hide) {
		this.hide = hide;
	}
	
	public boolean isMajor() {
		return major;
	}
	
	public void setMajor(boolean major) {
		this.major = major;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
