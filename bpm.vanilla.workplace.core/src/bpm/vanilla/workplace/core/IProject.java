package bpm.vanilla.workplace.core;

import java.util.Date;
import java.util.List;

public interface IProject {
	
	public Integer getId();
	
	public String getName();
	
	public String getVersion();
	
	public IUser getCreator();
	
	public Date getCreationDate();
	
	public List<IPackage> getPackages();
	
	public void setPackages(List<IPackage> packages);
}
