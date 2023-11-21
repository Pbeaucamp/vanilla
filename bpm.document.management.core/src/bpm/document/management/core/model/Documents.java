package bpm.document.management.core.model;

import java.util.Date;
import java.util.List;

public class Documents implements IObject, IAuthor {
	
	public static final String[] LISTE_PATTERN = {"{DOCUMENT_NAME}",
		"{CREATION_DATE}","{UPLOAD_DATE}","{LAST_MODIFIED}","{TYPE}","{FILE_EXTENSION}","{USER_ID}",
		"{ANNOTATION}","{LAST_MODIFIED_BY}","{AUTHOR_NAME}","{VALIDATION_DATE}","{VALIDATION_STATUS}"
	};
	
	public static final int XAKL_CLASSIC = 0;
	public static final int XAKL_SEARCH = 1;
	public static final int XAKL_PESV2 = 2;

	private static final long serialVersionUID = 1L;

	private int id = 0;
	
	private String name = "";
	private String thumbImage = "";
	private String filePath = "";
	private String fileExtension = "";
	private String fileName = "";
	private String originalPath = "";
	private String stampPath = "";
	private String pdfPath = "";

	//Use to pass any url (not saved in DB)
	private String customPath;
	
	private String treeItemType="";
	private String nature;
	
	private String documentsClassementPattern = "";
	private String barCode = "";

	private int fileSize = 0;
	
	private Date creationDate = new Date();
	private Date uploadDate = new Date();
	private Date lastModified = new Date();

	private int userId = 0;
	private String authorName = "";
	private String lastModifiedBy = "";

	private String annotation = "";
	private String uniqueCode;
	private String type = "";
	private int likes = 0;
	private int viewNumber = 0;
	
	private boolean deleted = false;
	private boolean finished = false;
	private boolean android = false;
	private boolean scanned = false;
	private boolean xakl = false;
	private boolean indexedAccess=true;
	private boolean analyzeZip=true;
	private boolean activateDoc=true;
	private boolean notified=false;
	private boolean archived = false;
	private boolean signed;
	
	private boolean encrypt = false;
	private String encryptPassword = "";

	private boolean validatedDocument = false;
	private String validationStatus = "";
	private String userValidator = "";
	private Date validationDate = new Date();

	private boolean checkoutStatus = false;
	private Date endCheckoutDate;
	
	private String TreeType = "Documents";

	private Integer typeSelectedId;
	private FileType typeSelected = new FileType();
	
	private String origin = "Aklabox";
	private String originReference = "NONE";
	
	private String ocrResult="";
	private Integer formId = 0;
	private List<Tags> tags;

	private int idSubStatus = 0;
	
	private int linkFolderId;
	
	//Used for when you do a search
	private Float score;
	private Float maxScore;
	
	//Used for mail management (if false display button in toolbar to assign task)
	private boolean hasTask = true;

	private Integer parentId = 0;
	private Tree parent;
	//Not saved in DB
	private boolean isFromEnterprise;
	
	private PermissionItem permissionItem;
	
	//Used for public sharing because everything is hardcoded for the public sharing date so yeah guess what it's not working...
//	FolderShare fs = new FolderShare();
//	fs.setDocId(doc.getId());
//	fs.setEmail(owner.getEmail());
//	fs.setSharedpassword("NONE");
//	fs.setPreEmptionDate("Limitless");
//	fs.setSharedConsult(true);
//	fs.setRequestEmail(false);
//
//	component.getDao().saveFoldersShare(fs);

//	private int itemTypeId = 0;
//	private String securityStatus = "";

	//Yeah with that it will sure take my preEmptionDate no problem 
//	private String peremptionDate;
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getFilePath() {
		return stampPath != null && !stampPath.isEmpty() ? stampPath : filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getPdfPath() {
		return stampPath != null && !stampPath.isEmpty() ? stampPath : (pdfPath != null && !pdfPath.isEmpty() ? pdfPath : (getFileExtension().toLowerCase().contains("pdf") ? getFilePath() : ""));
	}
	
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

//	public int getItemTypeId() {
//		return itemTypeId;
//	}
//
//	public void setItemTypeId(int itemTypeId) {
//		this.itemTypeId = itemTypeId;
//	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

//	public String getSecurityStatus() {
//		return securityStatus;
//	}
//
//	public void setSecurityStatus(String securityStatus) {
//		this.securityStatus = securityStatus;
//	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getTreeType() {
		return TreeType;
	}

	public void setTreeType(String TreeType) {
		this.TreeType = TreeType;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public boolean getCheckoutStatus() {
		return checkoutStatus;
	}

	public void setCheckoutStatus(boolean checkoutStatus) {
		this.checkoutStatus = checkoutStatus;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}
	
	public String getStampPath() {
		return stampPath;
	}
	
	public void setStampPath(String stampPath) {
		this.stampPath = stampPath;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getThumbImage() {
		return thumbImage;
	}

	public void setThumbImage(String thumbImage) {
		this.thumbImage = thumbImage;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public String getEncryptPassword() {
		return encryptPassword;
	}

	public void setEncryptPassword(String encryptPassword) {
		this.encryptPassword = encryptPassword;
	}

	public boolean isAndroid() {
		return android;
	}

	public void setAndroid(boolean android) {
		this.android = android;
	}

	public boolean isScanned() {
		return scanned;
	}

	public void setScanned(boolean scanned) {
		this.scanned = scanned;
	}

	public boolean isXakl() {
		return xakl;
	}

	public void setXakl(boolean xakl) {
		this.xakl = xakl;
	}

	public int getViewNumber() {
		return viewNumber;
	}

	public void setViewNumber(int viewNumber) {
		this.viewNumber = viewNumber;
	}

	public String getUserValidator() {
		if (userValidator == null)
			return "aklabox@aklabox.com";
		else
			return userValidator;
	}

	public void setUserValidator(String userValidator) {
		this.userValidator = userValidator;
	}

	public String getValidationStatus() {
		if (validationStatus == null)
			return "YES";
		else
			return validationStatus;
	}

	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}

	public boolean isValidatedDocument() {
		return validatedDocument;
	}

	public void setValidatedDocument(boolean validatedDocument) {
		this.validatedDocument = validatedDocument;
	}

	public String getOrigin() {
		if (origin == null) {
			return "Aklabox";
		} else {
			return origin;
		}
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getOriginReference() {
		if (originReference == null) {
			return "NONE";
		} else {
			return originReference;
		}
	}

	public void setOriginReference(String originReference) {
		this.originReference = originReference;
	}

	public Date getValidationDate() {
		if (validationDate == null) {
			return new Date();
		} else {
			return validationDate;
		}
	}
	public void setValidationDate(Date validationDate) {
		this.validationDate = validationDate;
	}
	public boolean isNotified() {
		return notified;
	}
	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public boolean isIndexedAccess() {
		return indexedAccess;
	}
	public void setIndexedAccess(boolean indexedAccess) {
		this.indexedAccess = indexedAccess;
	}

	public boolean isAnalyzeZip() {
		return analyzeZip;
	}

	public void setAnalyzeZip(boolean analyzeZip) {
		this.analyzeZip = analyzeZip;
	}

	public boolean isActivateDoc() {
		return activateDoc;
	}

	public void setActivateDoc(boolean activateDoc) {
		this.activateDoc = activateDoc;
	}

	public String getTreeItemType() {
		return treeItemType;
	}

	public void setTreeItemType(String treeItemType) {
		this.treeItemType = treeItemType;
	}

	public String getOcrResult() {
		if (ocrResult == null) {
			return "";			
		}else{
			return ocrResult;			
		}
	}

	public void setOcrResult(String ocrResult) {
		this.ocrResult = ocrResult;
	}

	public Integer getFormId() {
		return formId;
	}

	public void setFormId(Integer formId) {
		this.formId = formId;
	}

//	public String getPeremptionDate() {
//		return peremptionDate;
//	}
//
//	public void setPeremptionDate(String peremptionDate) {
//		this.peremptionDate = peremptionDate;
//	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public void setEndCheckoutDate(Date endCheckoutDate) {
		this.endCheckoutDate = endCheckoutDate;
	}
	
	public Date getEndCheckoutDate() {
		return endCheckoutDate;
	}
	
	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	public int getLinkFolderId() {
		return linkFolderId;
	}
	
	public void setLinkFolderId(int linkFolderId) {
		this.linkFolderId = linkFolderId;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public String getDocumentsClassementPattern() {
		return documentsClassementPattern;
	}

	public void setDocumentsClassementPattern(String documentsClassementPattern) {
		this.documentsClassementPattern = documentsClassementPattern;
	}
	
	public int getIdSubStatus() {
		return idSubStatus;
	}

	public void setIdSubStatus(int idSubStatus) {
		this.idSubStatus = idSubStatus;
	}

	public void setSearchScore(Float score) {
		this.score = score;
	}
	
	public Float getSearchScore() {
		return score;
	}

	public void setSearchMaxScore(Float maxScore) {
		this.maxScore = maxScore;
	}
	
	public Float getMaxScore() {
		return maxScore;
	}

	public Float calculateScore() {
		Float calculateScore = null;
		if (maxScore != null && maxScore > 0 && score != null) {
			calculateScore = score * 5 / maxScore;
		}
		return calculateScore;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	
	public boolean hasTask() {
		return hasTask;
	}
	
	public void setHasTask(boolean hasTask) {
		this.hasTask = hasTask;
	}
	
	public String getCustomPath() {
		return customPath;
	}
	
	public void setCustomPath(String customPath) {
		this.customPath = customPath;
	}
	
	public Integer getTypeSelectedId() {
		return typeSelectedId;
	}
	
	public void setTypeSelectedId(Integer typeSelectedId) {
		this.typeSelectedId = typeSelectedId;
	}

	public FileType getTypeSelected() {
		return typeSelected;
	}

	public void setTypeSelected(FileType typeSelected) {
		this.typeSelected = typeSelected;
		if (typeSelected != null) {
			this.typeSelectedId = typeSelected.getTypeId();
		}
	}
	
	@Override
	public Tree getParent() {
		return parent;
	}
	
	@Override
	public void setParent(Tree parent) {
		this.parent = parent;
	}
	
	@Override
	public Enterprise getEnterpriseParent() {
		return parent != null ? parent.getEnterpriseParent() : null;
	}
	
	@Override
	public PermissionItem getPermissionItem() {
		return permissionItem;
	}
	
	@Override
	public void setPermissionItem(PermissionItem permissionItem) {
		this.permissionItem = permissionItem;
	}
	
	@Override
	public boolean isFromEnterprise() {
		return isFromEnterprise;
	}
	
	@Override
	public void setFromEnterprise(boolean isFromEnterprise) {
		this.isFromEnterprise = isFromEnterprise;
	}
	
	@Override
	public boolean isShare() {
		return permissionItem != null && permissionItem.getShareStatus() != null;
	}
	
	@Override
	public String toString() {
		return name != null ? name : "";
	}
	
	public String getFilePathWithoutFormat() {
		return filePath != null && filePath.lastIndexOf(".") > 0 ? filePath.substring(0, filePath.lastIndexOf(".")) : filePath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Documents other = (Documents) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}