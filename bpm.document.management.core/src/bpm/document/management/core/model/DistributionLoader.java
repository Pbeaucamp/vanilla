package bpm.document.management.core.model;

import java.io.Serializable;

public class DistributionLoader implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id = 0;
	
	private int distributionId = 0;
	
	private String user = "";
	
	private int targetDirectoryId = 0;
	
	private String associationRule = "";
	
	public DistributionLoader() {
		super();
	}

	

	public DistributionLoader(int id, int distributionId, String user,
			int targetDirectoryId, String associationRule) {
		super();
		this.id = id;
		this.distributionId = distributionId;
		this.user = user;
		this.targetDirectoryId = targetDirectoryId;
		this.associationRule = associationRule;
	}



	public String getUser() {
		return user;
	}



	public void setUser(String user) {
		this.user = user;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	

	public int getDistributionId() {
		return distributionId;
	}


	public void setDistributionId(int distributionId) {
		this.distributionId = distributionId;
	}

	public int getTargetDirectoryId() {
		return targetDirectoryId;
	}

	public void setTargetDirectoryId(int targetDirectoryId) {
		this.targetDirectoryId = targetDirectoryId;
	}

	public String getAssociationRule() {
		return associationRule;
	}

	public void setAssociationRule(String associationRule) {
		this.associationRule = associationRule;
	}
	
}
