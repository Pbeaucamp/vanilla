package bpm.mdm.model.supplier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Supplier implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int ID = 0;
	public static final int NAME = 1;
	public static final int EXTERNAL_SOURCE = 2;
	public static final int EXTERNAL_ID = 3;
	
	public static final int DOC_ID = 0;
	public static final int DOC_NAME = 1;
	public static final int DOC_FORMAT = 2;
	public static final int DOC_VERSION = 3;
	public static final int DOC_DATE = 4;
	
	private Integer id;
	private String name;
	
	private String externalId;
	private String externalSource;
	
	private List<Contract> contracts;
	
	public Supplier() {
		
	}
	
	public Supplier(Integer id, String name, String externalId, String externalSource) {
		super();
		this.id = id;
		this.name = name;
		this.externalId = externalId;
		this.externalSource = externalSource;
	}



	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getExternalId() {
		return externalId;
	}
	
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public String getExternalSource() {
		return externalSource;
	}
	
	public void setExternalSource(String externalSource) {
		this.externalSource = externalSource;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public List<Contract> getContracts() {
		if(contracts == null) {
			contracts = new ArrayList<Contract>();
		}
		return contracts;
	}
	
	public void addContract(Contract contract) {
		if(contracts == null) {
			contracts = new ArrayList<Contract>();
		}
		contracts.add(contract);
		
		contract.setParent(this);
	}
	
	@Override
	public String toString() {
		return name;
	}

	public void updateContract(Contract newContract) {
		if (contracts == null) {
			contracts = new ArrayList<Contract>();
		}
		
		Contract existingContract = null;
		for (Contract contract : contracts) {
			if (contract.getId() == newContract.getId()) {
				existingContract = contract;
				break;
			}
		}
		
		if (existingContract != null) {
			contracts.remove(existingContract);
		}
		contracts.add(newContract);
	}
}
