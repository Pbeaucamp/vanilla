package bpm.metadata.jdbc.driver.remote;

import java.io.Serializable;

import bpm.metadata.jdbc.driver.FmdtDatabaseMetadata;

public class ActionJDBC implements Serializable{
	
	private int type;
	private String user;
	private String password;
	private String groupName;
	private int repositoryId;
	private int metadataId; 
	private String modele;
	private String pack;
	private QueryJDBC query;
	private FmdtDatabaseMetadata metadata;
	
	
	public ActionJDBC(int type, String user, String password,
			String groupName, int repositoryId, int metadataId, String modele, String pack, QueryJDBC query, FmdtDatabaseMetadata metadata){
		this.type = type;
		this.user = user;
		this.password = password;
		this.groupName = groupName;
		this.repositoryId = repositoryId;
		this.metadataId = metadataId;
		this.modele = modele;
		this.pack = pack;
		this.query = query;
		this.metadata = metadata;
		
	}
	
	public void setType(int type) {
		this.type = type;
	}




	public int getType() {
		return type;
	}


	public String getUser() {
		return user;
	}


	public String getPassword() {
		return password;
	}


	public String getGroupName() {
		return groupName;
	}


	public int getMetadataId() {
		return metadataId;
	}


	public String getModele() {
		return modele;
	}


	public String getPack() {
		return pack;
	}


	public QueryJDBC getQuery() {
		return query;
	}


	public int getRepositoryId() {
		return repositoryId;
	}

	public FmdtDatabaseMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(FmdtDatabaseMetadata metadata) {
		this.metadata = metadata;
	}
	
}
