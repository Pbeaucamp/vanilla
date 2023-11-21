package bpm.document.management.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tree implements IObject {
	private static final long serialVersionUID = 1L;

	public static final String TYPE_CLASSIC = "Classic";
	public static final String TYPE_MAIL = "Mail";
	public static final String TYPE_ROOT_MAIL = "Root Mail";

	public enum FOLDER_STATUS {
		STUDY, INSTRUCTION, WAIT, FINAL
	}

	private int id = 0;
	private String name = "";
	private int userId = 0;
	private String TreeType = "Folders";
	private String folderStatus = "";
	private Boolean isDeleted = false;
	private Boolean documentIndexed = true;
	private Boolean thumbnailCreated = true;
	private boolean notified = false;
	private String uniqueCode;

	private String validationStatus = "";
	private String validatedBy = "";
	private boolean activateFolder = true;
	private String treeItemType = "";
	private String backgroundImage = "";
	private Date creationDate = new Date();
	private boolean autoStamp = false;
	private boolean immediateValidation = false;
	private FileType fileTypeSelected;// = new FileType();

	private String folderType = TYPE_CLASSIC;

	// Used for public sharing because it's stored on the shareMail object...
	private String peremptionDate;
	private long administrativeUseDuration;

	private int associatedWorkflow = 0;
	private int workflowFolderStatus = 0;
	private int workflowProcessStatus = 0;

	private boolean canSign = true;
	private boolean validateWithSign = false;

	private boolean validateComments = false;

	private String background;
	
	private Integer parentId = 0;
	private Tree parent;
	private Enterprise enterpriseParent;
	//Not saved in DB
	private boolean isFromEnterprise;

	private List<IObject> children = new ArrayList<IObject>();
	
	private PermissionItem permissionItem;
	private List<FolderHierarchy> hierarchies;

	public Tree() {
	}

	public Tree(int id) {
		super();
		this.setId(id);
	}

	public Tree(String name, int userId) {
		super();
		this.name = name;
		this.userId = userId;
//		this.folderStatus = folderStatus;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getUserId() {
		return userId;
	}

	public String getTreeType() {
		return TreeType;
	}

	public void setTreeType(String TreeType) {
		this.TreeType = TreeType;
	}

	public String getFolderStatus() {
		return folderStatus;
	}

	public void setFolderStatus(String folderStatus) {
		this.folderStatus = folderStatus;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
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

	public String getValidatedBy() {
		if (validatedBy == null)
			return "aklabox@aklabox.com";
		else
			return validatedBy;
	}

	public void setValidatedBy(String validatedBy) {
		this.validatedBy = validatedBy;
	}

	public Boolean getDocumentIndexed() {
		return documentIndexed;
	}

	public void setDocumentIndexed(Boolean documentIndexed) {
		this.documentIndexed = documentIndexed;
	}

	public Boolean getThumbnailCreated() {
		return thumbnailCreated;
	}

	public void setThumbnailCreated(Boolean thumbnailCreated) {
		this.thumbnailCreated = thumbnailCreated;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}

	public String getUniqueCode() {
		if (uniqueCode == null) {
			String name = getName() + System.currentTimeMillis();
			long l = 0;
			for (int i = 0; i < name.length(); i++)
				l = l * 127 + name.charAt(i);
			return String.valueOf(Math.abs(l));
		}
		else {
			return uniqueCode;
		}
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public boolean isActivateFolder() {
		return activateFolder;
	}

	public void setActivateFolder(boolean activateFolder) {
		this.activateFolder = activateFolder;
	}

	public String getTreeItemType() {
		return treeItemType;
	}

	public void setTreeItemType(String treeItemType) {
		this.treeItemType = treeItemType;
	}

	@Override
	public int getFileSize() {
		return 0;
	}

	@Override
	public String getAuthorName() {
		return null;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getPeremptionDate() {
		return peremptionDate;
	}

	public void setPeremptionDate(String peremptionDate) {
		this.peremptionDate = peremptionDate;
	}

	public long getAdministrativeUseDuration() {
		return administrativeUseDuration;
	}

	public void setAdministrativeUseDuration(long administrativeUseDuration) {
		this.administrativeUseDuration = administrativeUseDuration;
	}

	public String getFolderType() {
		if (folderType == null) {
			folderType = TYPE_CLASSIC;
		}
		return folderType;
	}

	public void setFolderType(String folderType) {
		this.folderType = folderType;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	public boolean isAutoStamp() {
		return autoStamp;
	}

	public void setAutoStamp(boolean autoStamp) {
		this.autoStamp = autoStamp;
	}

	public boolean isImmediateValidation() {
		return immediateValidation;
	}

	public void setImmediateValidation(boolean immediateValidation) {
		this.immediateValidation = immediateValidation;
	}

	public int getAssociatedWorkflow() {
		return associatedWorkflow;
	}

	public void setAssociatedWorkflow(int associatedWorkflow) {
		this.associatedWorkflow = associatedWorkflow;
	}

	public int getWorkflowFolderStatus() {
		return workflowFolderStatus;
	}

	public void setWorkflowFolderStatus(int workflowFolderStatus) {
		this.workflowFolderStatus = workflowFolderStatus;
	}

	public int getWorkflowProcessStatus() {
		return workflowProcessStatus;
	}

	public void setWorkflowProcessStatus(int workflowProcessStatus) {
		this.workflowProcessStatus = workflowProcessStatus;
	}

	public FileType getFileTypeSelected() {
		return fileTypeSelected;
	}

	public void setFileTypeSelected(FileType fileTypeSelected) {
		this.fileTypeSelected = fileTypeSelected;
	}

	public boolean isCanSign() {
		return canSign;
	}

	public void setCanSign(boolean canSign) {
		this.canSign = canSign;
	}

	public boolean isValidateWithSign() {
		return validateWithSign;
	}

	public void setValidateWithSign(boolean validateWithSign) {
		this.validateWithSign = validateWithSign;
	}

	public boolean isValidateComments() {
		return validateComments;
	}

	public void setValidateComments(boolean validateComments) {
		this.validateComments = validateComments;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public List<IObject> getChildren() {
		return children;
	}

	public void setChildren(List<IObject> children) {
		if (children != null) {
			for (IObject child : children) {
				child.setParent(this);
			}
		}
		this.children = children;
	}

	public void addChild(IObject item) {
		if (children == null) {
			children = new ArrayList<IObject>();
		}
		item.setParent(this);
		children.add(item);
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
		return enterpriseParent != null ? enterpriseParent : parent != null ? parent.getEnterpriseParent() : null;
	}
	
	public void setEnterpriseParent(Enterprise enterpriseParent) {
		this.enterpriseParent = enterpriseParent;
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
	
	public String getFullPath() {
		return parent != null ? parent.getFullPath() + " / " + name : "/ " + name;
	}

	public List<FolderHierarchy> getHierarchies() {
		return hierarchies;
	}

	public void setHierarchies(List<FolderHierarchy> hierarchies) {
		this.hierarchies = hierarchies;
	}
}