package bpm.vanilla.workplace.core.datasource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table (name = "rpy_relational_datasources_repository")
@PrimaryKeyJoinColumn(name="dbId")
public class ModelDatasourceRepository extends AbstractDatasource {

	@Column(name = "`user`")
	private String user;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "repositoryUrl")
	private String repositoryUrl;
	
	@Column(name = "vanillaRuntimeUrl")
	private String vanillaRuntimeUrl;
	
	@Column(name = "dirId")
	private String dirId;
	
	@Column(name = "repositoryId")
	private String repositoryId;
	
	@Column(name = "groupName")
	private String groupName;
	
	@Column(name = "groupId")
	private String groupId;
	
	@Column(name = "connectionName")
	private String connectionName;

	@Column(name = "businessPackage")
	private String businessPackage;
	
	@Column(name = "businessModel")
	private String businessModel;

	@Column(name = "cubeName")
	private String cubeName;

	public ModelDatasourceRepository() {
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setDirId(String dirId) {
		this.dirId = dirId;
	}

	public String getDirId() {
		return dirId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl) {
		this.vanillaRuntimeUrl = vanillaRuntimeUrl;
	}

	public String getVanillaRuntimeUrl() {
		return vanillaRuntimeUrl;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public void setBusinessPackage(String businessPackage) {
		this.businessPackage = businessPackage;
	}

	public String getBusinessPackage() {
		return businessPackage;
	}

	public void setBusinessModel(String businessModel) {
		this.businessModel = businessModel;
	}

	public String getBusinessModel() {
		return businessModel;
	}
}
