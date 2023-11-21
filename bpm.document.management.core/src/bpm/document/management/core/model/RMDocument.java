package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class RMDocument implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	private int rmFolderId;
	private int docId;
	private int docClassId;
	private int docTypeId;
	private int docSubTypeId;
	private String docDetail;
	private String docUsage;
	private Date lockDate;
	private Date suspendDate;
	private Date transferDate;
	private Date destroyDate;
	
	public RMDocument() {
		// TODO Auto-generated constructor stub
	}
	
	public RMDocument(int docClassId, int docTypeId, int docSubTypeId, String docDetail, String docUsage) {
		super();
		this.docClassId = docClassId;
		this.docTypeId = docTypeId;
		this.docSubTypeId = docSubTypeId;
		this.docDetail = docDetail;
		this.docUsage = docUsage;
	}

	public RMDocument(int rmFolderId, int docId, int docClassId, int docTypeId, int docSubTypeId, String docDetail, String docUsage, Date lockDate, Date suspendDate, Date transferDate, Date destroyDate) {
		super();
		this.rmFolderId = rmFolderId;
		this.docId = docId;
		this.docClassId = docClassId;
		this.docTypeId = docTypeId;
		this.docSubTypeId = docSubTypeId;
		this.docDetail = docDetail;
		this.docUsage = docUsage;
		this.lockDate = lockDate;
		this.suspendDate = suspendDate;
		this.transferDate = transferDate;
		this.destroyDate = destroyDate;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRmFolderId() {
		return rmFolderId;
	}

	public void setRmFolderId(int rmFolderId) {
		this.rmFolderId = rmFolderId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getDocClassId() {
		return docClassId;
	}

	public void setDocClassId(int docClassId) {
		this.docClassId = docClassId;
	}

	public int getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(int docTypeId) {
		this.docTypeId = docTypeId;
	}

	public int getDocSubTypeId() {
		return docSubTypeId;
	}

	public void setDocSubTypeId(int docSubTypeId) {
		this.docSubTypeId = docSubTypeId;
	}

	public String getDocDetail() {
		return docDetail;
	}

	public void setDocDetail(String docDetail) {
		this.docDetail = docDetail;
	}

	public String getDocUsage() {
		return docUsage;
	}

	public void setDocUsage(String docUsage) {
		this.docUsage = docUsage;
	}

	public Date getLockDate() {
		return lockDate;
	}

	public void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}

	public Date getSuspendDate() {
		return suspendDate;
	}

	public void setSuspendDate(Date suspendDate) {
		this.suspendDate = suspendDate;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public Date getDestroyDate() {
		return destroyDate;
	}

	public void setDestroyDate(Date destroyDate) {
		this.destroyDate = destroyDate;
	}
	
	
}
