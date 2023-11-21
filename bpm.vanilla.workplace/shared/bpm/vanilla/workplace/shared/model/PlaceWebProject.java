package bpm.vanilla.workplace.shared.model;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PlaceWebProject implements IsSerializable {

	private int id;
	private int creatorId;
	
	private String name;
	private String version;
	
	private Date creationDate;
	
	private List<PlaceWebPackage> packages;
	
	public PlaceWebProject() { }
	
	public PlaceWebProject(String name, String version, int creatorId, Date creationDate) {
		this.name = name;
		this.version = version;
		this.creatorId = creatorId;
		this.creationDate = creationDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setPackages(List<PlaceWebPackage> packages) {
		this.packages = packages;
	}

	public List<PlaceWebPackage> getPackages() {
		return packages;
	}
}
