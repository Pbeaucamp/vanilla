package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;

public class UserRunConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int runId = -1;
	private String name;
	private int idUser;
	private int idRepository;
	private int idItem;
	private List<UserRunConfigurationParameter> parameters;
	private String description;
	
	public UserRunConfiguration() {
		
	}
	
	public UserRunConfiguration(String name, int idUser, int idRepository, int idItem, List<UserRunConfigurationParameter> parameters) {
		this.name = name;
		this.idUser = idUser;
		this.idRepository = idRepository;
		this.idItem = idItem;
		this.parameters = parameters;
	}
	
	public UserRunConfiguration(String name, int idUser, int idRepository, int idItem) {
		this.name = name;
		this.idUser = idUser;
		this.idRepository = idRepository;
		this.idItem = idItem;
	}
	
	public UserRunConfiguration(String name, int idUser, IObjectIdentifier identifier, List<UserRunConfigurationParameter> parameters) {
		this.name = name;
		this.idUser = idUser;
		this.idRepository = identifier.getRepositoryId();
		this.idItem = identifier.getDirectoryItemId();
		this.parameters = parameters;
	}
	
	public UserRunConfiguration(String name, int idUser, IObjectIdentifier identifier) {
		this.name = name;
		this.idUser = idUser;
		this.idRepository = identifier.getRepositoryId();
		this.idItem = identifier.getDirectoryItemId();
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getIdRepository() {
		return idRepository;
	}

	public void setIdRepository(int idRepository) {
		this.idRepository = idRepository;
	}

	public int getIdItem() {
		return idItem;
	}

	public void setIdItem(int idItem) {
		this.idItem = idItem;
	}

	public List<UserRunConfigurationParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<UserRunConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(UserRunConfigurationParameter parameter) {
		if(this.parameters == null) {
			this.parameters = new ArrayList<UserRunConfigurationParameter>();
		}
		this.parameters.add(parameter);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
