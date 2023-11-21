package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Enterprise implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeUser {
		ADMIN, MANAGER, VALIDATOR, DEFAULT_VALIDATOR, SIGNER, READER, DEFAULT_READER, MAILER, USER, ARCHIVE
	}

	private int enterpriseId = 0;
	private String enterpriseName = "";
	private String enterpriseDescription = "";
	private boolean canReadWrite = false;
	private Date creationDate = new Date();

	private String logo;
	private String stamp;
	private boolean displayDate;
	private String background = "";

//	private String validators = "";
//	private String readers = "";
//	private String mailList = "";
//	private String administrators = "";
//	private String signatories = "";
//	private String managers = "";

	private FileType typeSelected;

	private int readingDelay;
//	private String defaultReaders;
	private int validationDelay;
//	private String defaultValidators;

	private String patternModel = "";

	private String formatBarCode = "";

	// Can only have one root child
	// private List<Tree> childs;
	private int folderRootId;
	private Tree folderRoot;

	// For markets
	private int instructionFolderId;
	private int activeFolderId;
	private int signFolderId;
	private int treatedFolderId;

	//Hierarchy for markets state
	private Integer initialMarketHierarchy;
	private Integer activeMarketHierarchy;
	private Integer finalMarketHierarchy;
	
	private List<User> admins;
	
	private Boolean archive;

//	public String getReaders() {
//		return readers;
//	}
//
//	public void setReaders(String readers) {
//		this.readers = readers;
//	}
//
//	public String getAdministrators() {
//		return administrators;
//	}
//
//	public void setAdministrators(String administrators) {
//		this.administrators = administrators;
//	}

	public int getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(int enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getEnterpriseDescription() {
		return enterpriseDescription;
	}

	public void setEnterpriseDescription(String enterpriseDescription) {
		this.enterpriseDescription = enterpriseDescription;
	}

	public boolean isCanReadWrite() {
		return canReadWrite;
	}

	public void setCanReadWrite(boolean canReadWrite) {
		this.canReadWrite = canReadWrite;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getBackground() {
		if (background == null) {
			background = "";
		}
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

//	public String getValidators() {
//		return validators;
//	}
//
//	public void setValidators(String validators) {
//		this.validators = validators;
//	}
//
//	public String getMailList() {
//		return mailList;
//	}
//
//	public void setMailList(String mailList) {
//		this.mailList = mailList;
//	}

	public FileType getTypeSelected() {
		return typeSelected;
	}

	public void setTypeSelected(FileType typeSelected) {
		this.typeSelected = typeSelected;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public int getReadingDelay() {
		return readingDelay;
	}

	public void setReadingDelay(int readingDelay) {
		this.readingDelay = readingDelay;
	}

//	public String getDefaultReaders() {
//		return defaultReaders;
//	}
//
//	public void setDefaultReaders(String defaultReaders) {
//		this.defaultReaders = defaultReaders;
//	}

	public int getValidationDelay() {
		return validationDelay;
	}

	public void setValidationDelay(int validationDelay) {
		this.validationDelay = validationDelay;
	}

//	public String getDefaultValidators() {
//		return defaultValidators;
//	}
//
//	public void setDefaultValidators(String defaultValidators) {
//		this.defaultValidators = defaultValidators;
//	}

	public String getPatternModel() {
		return patternModel;
	}

	public void setPatternModel(String patternModel) {
		this.patternModel = patternModel;
	}

	public boolean isDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}

//	public String getSignatories() {
//		return signatories;
//	}
//
//	public void setSignatories(String signatories) {
//		this.signatories = signatories;
//	}

	public String getFormatBarCode() {
		return formatBarCode;
	}

	public void setFormatBarCode(String formatBarCode) {
		this.formatBarCode = formatBarCode;
	}

//	public String getManagers() {
//		return managers;
//	}
//
//	public void setManagers(String managers) {
//		this.managers = managers;
//	}

	// public List<Tree> getChilds() {
	// return childs;
	// }
	//
	// public void setChilds(List<Tree> childs) {
	// if (childs != null) {
	// for (Tree tree : childs) {
	// tree.setEnterpriseParent(this);
	// }
	// }
	// this.childs = childs;
	// }

	public int getFolderRootId() {
		return folderRootId;
	}

	public void setFolderRootId(int folderRootId) {
		this.folderRootId = folderRootId;
	}

	public Tree getFolderRoot() {
		return folderRoot;
	}

	public void setFolderRoot(Tree folderRoot) {
		if (folderRoot != null) {
			folderRoot.setEnterpriseParent(this);
		}
		this.folderRoot = folderRoot;
	}

	public int getInstructionFolderId() {
		return instructionFolderId;
	}

	public void setInstructionFolderId(int instructionFolderId) {
		this.instructionFolderId = instructionFolderId;
	}

	public int getActiveFolderId() {
		return activeFolderId;
	}

	public void setActiveFolderId(int activeFolderId) {
		this.activeFolderId = activeFolderId;
	}

	public int getSignFolderId() {
		return signFolderId;
	}

	public void setSignFolderId(int signFolderId) {
		this.signFolderId = signFolderId;
	}
	
	public Integer getInitialMarketHierarchy() {
		return initialMarketHierarchy;
	}
	
	public void setInitialMarketHierarchy(Integer initialMarketHierarchy) {
		this.initialMarketHierarchy = initialMarketHierarchy;
	}
	
	public Integer getActiveMarketHierarchy() {
		return activeMarketHierarchy;
	}
	
	public void setActiveMarketHierarchy(Integer activeMarketHierarchy) {
		this.activeMarketHierarchy = activeMarketHierarchy;
	}
	
	public Integer getFinalMarketHierarchy() {
		return finalMarketHierarchy;
	}
	
	public void setFinalMarketHierarchy(Integer finalMarketHierarchy) {
		this.finalMarketHierarchy = finalMarketHierarchy;
	}
	
	public List<User> getAdmins() {
		return admins;
	}
	
	public void setAdmins(List<User> admins) {
		this.admins = admins;
	}

	public boolean isAdmin(User currentUser) {
		if (admins != null) {
			for (User user : admins) {
				if (user.getUserId() == currentUser.getUserId()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return enterpriseName != null ? enterpriseName : "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + enterpriseId;
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
		Enterprise other = (Enterprise) obj;
		if (enterpriseId != other.enterpriseId)
			return false;
		return true;
	}

	public int getTreatedFolderId() {
		return treatedFolderId;
	}

	public void setTreatedFolderId(int treatedFolderId) {
		this.treatedFolderId = treatedFolderId;
	}

	public Boolean isArchive() {
		return archive;
	}

	public void setArchive(Boolean archive) {
		this.archive = archive;
	}
}
