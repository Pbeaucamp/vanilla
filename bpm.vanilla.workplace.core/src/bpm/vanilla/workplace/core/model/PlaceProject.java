package bpm.vanilla.workplace.core.model;

import java.util.Date;
import java.util.List;

import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.IProject;
import bpm.vanilla.workplace.core.IUser;

public class PlaceProject implements IProject {

	private Integer id;
	private String name;
	private String version;
	
	private IUser creator;
	private Date creationDate;
	
	private List<IPackage> packages;
	
	public PlaceProject() { }
	
	public PlaceProject(String name, String version, IUser creator, Date creationDate) {
		this.name = name;
		this.version = version;
		this.creator = creator;
		this.creationDate = creationDate;
	}
	
	public PlaceProject(Integer id, String name, String version, IUser creator, Date creationDate) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.creator = creator;
		this.creationDate = creationDate;
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
	public List<IPackage> getPackages() {
		return packages;
	}

	@Override
	public void setPackages(List<IPackage> packages) {
		this.packages = packages;
	}
}
