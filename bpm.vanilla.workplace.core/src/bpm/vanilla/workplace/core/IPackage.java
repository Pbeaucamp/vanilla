package bpm.vanilla.workplace.core;

import java.util.Date;

import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public interface IPackage {
	
	public Integer getId();
	
	public String getName();
	
	public String getVersion();
	
	public IUser getCreator();
	
	public Date getCreationDate();
	
	public String getVanillaVersion();
	
	public IPackageType getType();
	
	public Integer getProjectId();
	
	public void setProjectId(int projectId);
	
	public String getPath();
	
	public void setPath(String path);
	
	public boolean isValid();
	
	public boolean isCertified();
	
	public String getDocumentationUrl();
	
	public String getSiteWebUrl();
	
	public String getPrerequisUrl();
	
	public PlaceImportDirectory getRootDirectory();
	
	public void setRootDirectory(PlaceImportDirectory rootDirectory);
	
	public void setPackage(VanillaPackage pack);
	
	public VanillaPackage getPackage();
	
}
