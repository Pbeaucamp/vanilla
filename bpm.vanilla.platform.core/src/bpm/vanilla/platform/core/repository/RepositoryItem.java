package bpm.vanilla.platform.core.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.IRepositoryApi;

/**
 * convenient class to wrap all objetcs representing an Object in the repository
 * 
 * @author ludo
 * 
 */
@Entity
@Table (name = "rpy_repository_item")
public class RepositoryItem implements IRepositoryObject {

	private static final long serialVersionUID = 3657218947921578607L;

	public static final String ID = "ITEM_ID";
	public static final String DIR_ID = "ITEM_DIR_ID";
	public static final String TYPE = "ITEM_TYPE";
	public static final String SUBTYPE = "ITEM_SUBTYPE";
	public static final String NAME = "ITEM_NAME";
	public static final String COMMENT = "ITEM_COMMENT";
	public static final String DATE_CREATION = "ITEM_DATE_CREATION";
	public static final String DATE_MODIFICATION = "ITEM_DATE_MODIFICATION";
	public static final String DATE_DELETION = "ITEM_DATE_DELETION";
	public static final String OWNER_ID = "ITEM_OWNER_ID";
	public static final String MODIFIED_BY = "ITEM_MODIFIED_BY";
	public static final String DELETED_BY = "ITEM_DELETED_BY";
	public static final String LOCK_ID = "ITEM_LOCK_ID";
	public static final String FORMATTING_VARIABLE_ID = "ITEM_FORMATTING_VARIABLE_ID";
	public static final String DISPLAY = "ITEM_DISPLAY";
	public static final String VISIBLE = "ITEM_VISIBLE";
	public static final String CREATE_ENTRY = "ITEM_CREATE_ENTRY";
	public static final String ON = "ITEM_ON";
	public static final String AVAILABLE_GED = "ITEM_AVAILABLE_GED";
	public static final String REALTIME_GED = "ITEM_REALTIME_GED";
	public static final String ANDROID_SUPPORTED = "ITEM_ANDROID_SUPPORTED";
	public static final String INTERNAL_VERSION = "ITEM_INTERNAL_VERSION";
	public static final String PUBLIC_VERSION = "ITEM_PUBLIC_VERSION";
	public static final String MODEL_TYPE = "ITEM_MODEL_TYPE";
	public static final String RUNTIME_URL = "ITEM_RUNTIME_URL";
	public static final String DEFAULT_FORMAT = "ITEM_DEFAULT_FORMAT";
	public static final String NB_MAX_HISTO = "ITEM_NB_MAX_HISTO";
	public static final String AVAILABLE_ODATA = "ITEM_AVAILABLE_ODATA";
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "directory_id")
	private int directoryId;

	@Column(name = "type")
	private int type;
	
	@Column(name = "subtype")
	private int subtype = -1;

	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "item_comment")
	private String comment;

	@Column(name = "date_creation")
	private Date dateCreation = new Date();
	
	@Column(name = "date_modification")
	private Date dateModification  = new Date();
	
	@Transient
	private long dateModificationAsLong;
	
	@Column(name = "date_deletion")
	private Date dateDeletion;

	@Column(name = "owner_id")
	private Integer ownerId;
	
	@Column(name = "modified_by")
	private Integer modifiedBy;
	
	@Column(name = "deleted_by")
	private Integer deletedBy;

	@Column(name = "lock_id")
	private Integer lockId;
	
	@Column(name = "formatting_variable_id")
	private Integer formattingVariableId = 0;

	@Column(name = "is_display")
	private boolean display = true;
	
	@Column(name = "is_visible")
	private boolean visible = true;
	
	@Column(name = "create_entry")
	private boolean createEntry = true;
	
	@Column(name = "is_on")
	private boolean on = true;
	
	@Column(name = "is_available_ged")
	private boolean availableGed = true;
	
	@Column(name = "is_realtime_ged")
	private boolean realtimeGed = false;
	
	@Column(name = "is_android_supported")
	private boolean androidSupported = false;
	
	@Transient
	private boolean isCommentable = false;
	
	@Transient
	private boolean canRun = false;

	@Column(name = "internal_version")
	private String internalVersion;
	
	@Column(name = "public_version")
	private String publicVersion;

	@Column(name = "model_type")
	private String modelType;

	@Column(name = "runtime_url")
	private String runtimeUrl = "default";
	
	@Column(name = "default_format")
	private String defaultFormat = "";
	
	@Column(name = "nb_max_histo")
	private int nbMaxHisto;
	
	@Column(name = "is_available_odata")
	private Boolean availableOData = false;

	public RepositoryItem(int id, int directoryId, int type, int subtype, String itemName, String comment, Date dateCreation, 
			Date dateModification, Date dateDeletion, Integer ownerId, 
			Integer modifiedBy, Integer deletedBy, Integer lockId, Integer formattingVariableId, boolean display, boolean visible, 
			boolean createEntry, boolean on, boolean availableGed, boolean realtimeGed, boolean androidSupported, String internalVersion, 
			String publicVersion, String modelType, String runtimeUrl, String defaultFormat, int nbMaxHisto, boolean availableOData) {
		super();
		this.id = id;
		this.directoryId = directoryId;
		this.type = type;
		this.subtype = subtype;
		this.itemName = itemName;
		this.comment = comment;
		this.dateCreation = dateCreation;
		this.dateModification = dateModification;
		this.dateDeletion = dateDeletion;
		this.ownerId = ownerId;
		this.modifiedBy = modifiedBy;
		this.deletedBy = deletedBy;
		this.lockId = lockId;
		this.formattingVariableId = formattingVariableId;
		this.display = display;
		this.visible = visible;
		this.createEntry = createEntry;
		this.on = on;
		this.availableGed = availableGed;
		this.realtimeGed = realtimeGed;
		this.androidSupported = androidSupported;
		this.internalVersion = internalVersion;
		this.publicVersion = publicVersion;
		this.modelType = modelType;
		this.runtimeUrl = runtimeUrl;
		this.defaultFormat = defaultFormat;
		this.nbMaxHisto = nbMaxHisto;
		this.availableOData = availableOData;
	}

	public RepositoryItem() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isAndroidSupported() {
		return androidSupported;
	}

	public void setAndroidSupported(boolean isAndroidSupported) {
		this.androidSupported = isAndroidSupported;
	}

	public int getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(int directoryId) {
		this.directoryId = directoryId;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean isVisible) {
		this.visible = isVisible;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateDeletion() {
		return dateDeletion;
	}

	public void setDateDeletion(Date dateDeletion) {
		this.dateDeletion = dateDeletion;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(Integer deletedBy) {
		this.deletedBy = deletedBy;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getLockId() {
		return lockId;
	}

	public void setLockId(Integer lockId) {
		this.lockId = lockId;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean isOn) {
		this.on = isOn;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Date getDateModification() {
		if (dateModification == null) {
			dateModification = new Date(dateModificationAsLong);
		}
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
		if (dateModification != null) {
			setDateModificationAsLong(dateModification.getTime());
		}
	}

	public Integer getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean isDisplay) {
		this.display = isDisplay;
	}

	public Integer getFormattingVariableId() {
		return formattingVariableId;
	}

	public void setFormattingVariableId(Integer formattingVariableId) {
		this.formattingVariableId = formattingVariableId;
	}

	public boolean isAvailableGed() {
		return availableGed;
	}

	public void setAvailableGed(boolean isAvailableGed) {
		this.availableGed = isAvailableGed;
	}

	public boolean isRealtimeGed() {
		return realtimeGed;
	}

	public void setRealtimeGed(boolean isRealtimeGed) {
		this.realtimeGed = isRealtimeGed;
	}

	public String getInternalVersion() {
		return internalVersion;
	}

	public void setInternalVersion(String internalVersion) {
		this.internalVersion = internalVersion;
	}

	public String getPublicVersion() {
		return publicVersion;
	}

	public void setPublicVersion(String publicVersion) {
		this.publicVersion = publicVersion;
	}
	
	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public void setDateModificationAsLong(long dateModificationAsLong) {
		this.dateModificationAsLong = dateModificationAsLong;
	}

	public long getDateModificationAsLong() {
		return dateModificationAsLong;
	}

	public void setNbMaxHisto(int nbMaxHisto) {
		this.nbMaxHisto = nbMaxHisto;
	}

	public int getNbMaxHisto() {
		return nbMaxHisto;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public boolean isReport() {
		if (getType() == IRepositoryApi.FWR_TYPE || (getType() == IRepositoryApi.CUST_TYPE && (getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE || getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE))) {
			return true;
		}
		return false;
	}

	public boolean isLocked() {
		return getLockId() != null && getLockId().intValue() > 0;
	}

	public void setRuntimeUrl(String runtimeUrl) {
		this.runtimeUrl = runtimeUrl;
	}

	public String getRuntimeUrl() {
		return runtimeUrl;
	}

	public boolean isExecutable() {
		if (getType() == IRepositoryApi.FD_TYPE || getType() == IRepositoryApi.FWR_TYPE || getType() == IRepositoryApi.CUST_TYPE 
				|| getType() == IRepositoryApi.GTW_TYPE || getType() == IRepositoryApi.FASD_TYPE || getType() == IRepositoryApi.FAV_TYPE 
				|| getType() == IRepositoryApi.BIW_TYPE || getType() == IRepositoryApi.EXTERNAL_DOCUMENT || getType() == IRepositoryApi.REPORTS_GROUP
				|| getType() == IRepositoryApi.URL || getType() == IRepositoryApi.KPI_THEME) {
			return true;
		}
		else {
			return false;
		}
	}

	public String getDefaultFormat() {
		return defaultFormat;
	}

	public void setDefaultFormat(String defaultFormat) {
		this.defaultFormat = defaultFormat;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RepositoryItem) {
			return id == ((RepositoryItem) obj).getId();
		}
		return super.equals(obj);
	}

	public boolean isCreateEntry() {
		return createEntry;
	}

	public void setCreateEntry(boolean createEntry) {
		this.createEntry = createEntry;
	}

	public void setCommentable(boolean isCommentable) {
		this.isCommentable = isCommentable;
	}

	public boolean isCommentable() {
		return isCommentable;
	}

	public void setCanRun(boolean canRun) {
		this.canRun = canRun;
	}

	public boolean canRun() {
		return canRun;
	}

	@Override
	public String getName() {
		return itemName;
	}
	
	public boolean isAvailableOData() {
		return availableOData != null ? availableOData : false;
	}
	
	public void setAvailableOData(boolean availableOData) {
		this.availableOData = availableOData;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
}
