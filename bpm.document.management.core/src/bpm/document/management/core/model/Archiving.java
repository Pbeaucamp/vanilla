package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Archiving implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum METHOD_ARCHIVING {
		FILE_TYPE("filetype"), ENTERPRISE("enterprise"), FOLDER("folder"), DOCUMENT("document");

		private String name = "";

		// Constructeur
		METHOD_ARCHIVING(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		public static METHOD_ARCHIVING fromString(String text) {
			if (text != null) {
				for (METHOD_ARCHIVING f : METHOD_ARCHIVING.values()) {
					if (text.equalsIgnoreCase(f.name)) {
						return f;
					}
				}
			}
			return null;
		}
	};

	private int archiveId;
	private String name;
	private String destination;
	private int nb_years;
	private int nb_days;
	private Boolean automatic = false;
	private List<FileType> listTypes = new ArrayList<FileType>();
	private String archive_type;
	private Boolean save = true;
	// voir enum METHOD_ARCHIVING
	private String methodArchive = "";
	private int folderId;
	private int enterpriseId;
	private Boolean oldVersions;
	

	private Enterprise enterprise;
	private IObject item;

	public int getArchiveId() {
		return archiveId;
	}

	public void setArchiveId(int archiveId) {
		this.archiveId = archiveId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getNb_years() {
		return nb_years;
	}

	public void setNb_years(int nb_Years) {
		this.nb_years = nb_Years;
	}

	public int getNb_days() {
		return nb_days;
	}

	public void setNb_days(int nb_Days) {
		this.nb_days = nb_Days;
	}

	public Boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(Boolean automatic) {
		this.automatic = automatic;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (archiveId == ((Archiving) obj).getArchiveId()) {
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	public List<FileType> getListTypes() {
		return listTypes;
	}

	public void setListTypes(List<FileType> listTypes) {
		this.listTypes = (listTypes != null) ? new ArrayList<FileType>(listTypes) : new ArrayList<FileType>();
	}

	public void addType(FileType types) {
		this.listTypes.add(types);
	}

	public void removeType(FileType type) {
		this.listTypes.remove(type);
	}

	public String getArchive_type() {
		return archive_type;
	}

	public void setArchive_type(String archiveType) {
		this.archive_type = archiveType;
	}

	public Boolean getSave() {
		return save;
	}

	public void setSave(Boolean save) {
		this.save = save;
	}

	public String getMethodArchive() {
		return methodArchive;
	}

	public void setMethodArchive(String methodArchive) {
		this.methodArchive = methodArchive;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public int getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(int enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
	
	public IObject getItem() {
		return item;
	}
	
	public void setItem(IObject item) {
		this.item = item;
	}
	
	public Boolean getOldVersions() {
		return oldVersions;
	}

	public void setOldVersions(Boolean oldVersions) {
		this.oldVersions = oldVersions;
	}

}
