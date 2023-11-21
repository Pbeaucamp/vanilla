package bpm.vanilla.platform.core.beans.resources;

public class ContractIntegrationInformations extends AbstractD4CIntegrationInformations {

	private static final long serialVersionUID = 1L;

	private int id;

	private String nameService;
	private String item;
	private String outputName;

	private int etlId;
	private String etlName;
	
	public ContractIntegrationInformations() {
	}

	public ContractIntegrationInformations(Integer contractId, ContractType type, String nameService, String item, String outputName, String targetOrganisation, String targetDatasetName) {
		this.nameService = nameService;
		this.item = item;
		this.outputName = outputName;
		setContractId(contractId);
		setType(type);
		setTargetOrganisation(targetOrganisation);
		setTargetDatasetName(targetDatasetName);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNameService() {
		return nameService;
	}

	public void setNameService(String nameService) {
		this.nameService = nameService;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public int getEtlId() {
		return etlId;
	}

	public void setEtlId(int etlId) {
		this.etlId = etlId;
	}

	public String getEtlName() {
		return etlName;
	}

	public void setEtlName(String etlName) {
		this.etlName = etlName;
	}

}