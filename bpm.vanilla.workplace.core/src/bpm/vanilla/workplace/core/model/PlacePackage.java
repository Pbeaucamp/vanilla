package bpm.vanilla.workplace.core.model;

import java.util.Date;

import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IPackageType;
import bpm.vanilla.workplace.core.IUser;

public class PlacePackage implements IPackage {

	private Integer id;
	private String name;
	private String version;
	private String vanillaVersion;
	
	private IPackageType type;
	
	private IUser creator;
	private Date creationDate;
	
	private Integer projectId;
	
	private String path;
	
	private boolean isValid;
	private boolean isCertified;
	
	private String documentationUrl;
	private String siteWebUrl;
	private String prerequisUrl;
	
	private PlaceImportDirectory rootDirectory;
	
//	private List<PlaceImportItem> importItems = new ArrayList<PlaceImportItem>();
	private VanillaPackage pack;
	
	public PlacePackage() { }
	
	public PlacePackage(String name, String version, String vanillaVersion, IPackageType type, 
			IUser creator, Date creationDate, boolean isValid, boolean isCertified, 
			String documentationUrl, String siteWebUrl, String prerequisUrl) {
		this.name = name;
		this.version = version;
		this.vanillaVersion = vanillaVersion;
		this.type = type;
		this.creator = creator;
		this.creationDate = creationDate;
		this.isValid = isValid;
		this.isCertified = isCertified;
		this.documentationUrl = documentationUrl;
		this.siteWebUrl = siteWebUrl;
		this.prerequisUrl = prerequisUrl;
	}
	
	public PlacePackage(Integer id, String name, String version, String vanillaVersion, 
			IPackageType type, IUser creator, Date creationDate, int projectId, String path,
			boolean isValid, boolean isCertified, String documentationUrl, String siteWebUrl,
			String prerequisUrl) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.vanillaVersion = vanillaVersion;
		this.type = type;
		this.creator = creator;
		this.creationDate = creationDate;
		this.projectId = projectId;
		this.path = path;
		this.isValid = isValid;
		this.isCertified = isCertified;
		this.documentationUrl = documentationUrl;
		this.siteWebUrl = siteWebUrl;
		this.prerequisUrl = prerequisUrl;
	}
	
	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public IUser getCreator() {
		return creator;
	}

	public void setCreator(PlaceUser creator) {
		this.creator = creator;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String getVanillaVersion() {
		return vanillaVersion;
	}

	public void setVanillaVersion(String vanillaVersion) {
		this.vanillaVersion = vanillaVersion;
	}

	@Override
	public IPackageType getType() {
		return type;
	}

	public void setType(IPackageType type) {
		this.type = type;
	}

	@Override
	public Integer getProjectId() {
		return projectId;
	}

	@Override
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public boolean isValid() {
		return isValid;
	}

	public void setCertified(boolean isCertified) {
		this.isCertified = isCertified;
	}

	@Override
	public boolean isCertified() {
		return isCertified;
	}

	@Override
	public void setRootDirectory(PlaceImportDirectory rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	@Override
	public PlaceImportDirectory getRootDirectory() {
		return rootDirectory;
	}

//	@Override
//	public void addImportItem(PlaceImportItem importItem) {
//		this.importItems.add(importItem);
//	}
//
//	@Override
//	public List<PlaceImportItem> getImportItems() {
//		return importItems;
//	}
//
//	@Override
//	public void setImportItems(List<PlaceImportItem> importItems) {
//		this.importItems = importItems;
//	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	@Override
	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setSiteWebUrl(String siteWebUrl) {
		this.siteWebUrl = siteWebUrl;
	}

	@Override
	public String getSiteWebUrl() {
		return siteWebUrl;
	}

	public void setPrerequisUrl(String prerequisUrl) {
		this.prerequisUrl = prerequisUrl;
	}

	@Override
	public String getPrerequisUrl() {
		return prerequisUrl;
	}

	@Override
	public VanillaPackage getPackage() {
		return pack;
	}

	@Override
	public void setPackage(VanillaPackage pack) {
		this.pack = pack;
	}
}
