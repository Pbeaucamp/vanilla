package bpm.mdm.model.supplier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.resources.ContractType;

public class Contract implements Serializable {
	


	private static final long serialVersionUID = 1L;
	
	public static final int ID = 0;
	public static final int NAME = 1;
	public static final int EXTERNAL_SOURCE = 2;
	public static final int EXTERNAL_ID = 3;
	
	private Integer id;
	private String name;
	private Date creationDate = new Date();
	private Integer userId;
	
	private String externalId;
	private String externalSource;
	
	private ContractType type = ContractType.DOCUMENT;
	private GedDocument fileVersions;
	private Integer docId;
	
	private Integer directoryId;
	private MdmDirectory directory;
	
	private Supplier parent;
	private Integer versionId;

	private List<DocumentItem> items;
	
	private Integer datasourceId;
	private Integer datasetId;
	
	//True if the contract has DocumentItem of type input
	//Not saved in DB
	private boolean hasInput;
	
	public Contract(int id, String name, String externalId, String externalSource) {
		this(id, ContractType.DOCUMENT, name, externalId, externalSource);
	}
	
	public Contract(int id, ContractType type, String name, String externalId, String externalSource) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.externalId = externalId;
		this.externalSource = externalSource;
	}
	
	public Contract(){}
	
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
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getUserId() {
		return userId;
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
	
	public void setFileVersions(GedDocument fileVersions) {
		this.fileVersions = fileVersions;
		this.docId = fileVersions.getId();
	}

	/**
	 * Careful ! This is not loaded by default.
	 * You need to use the docId to get back the right GedDocument.
	 * @return
	 */
	public GedDocument getFileVersions() {
		return fileVersions;
	}

	public void setParent(Supplier parent) {
		this.parent = parent;
	}

	public Supplier getParent() {
		return parent;
	}
	
	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}
	
	public Integer getVersionId() {
		return versionId;
	}
	
	public ContractType getType() {
		return type;
	}
	
	public void setType(ContractType type) {
		this.type = type;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	
	public Integer getDirectoryId() {
		return directoryId;
	}
	
	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}
	
	public MdmDirectory getDirectory() {
		return directory;
	}
	
	public void setDirectory(MdmDirectory directory) {
		this.directory = directory;
		setDirectoryId(directory != null ? directory.getId() : null);
	}
	
	public List<DocumentItem> getItems() {
		return items;
	}
	
	public void setItems(List<DocumentItem> items) {
		this.items = items;
	}
	
	public void addItem(DocumentItem item) {
		if (items == null) {
			this.items = new ArrayList<DocumentItem>();
		}
		this.items.add(item);
	}
	
	public Integer getDatasourceId() {
		return datasourceId;
	}
	
	public void setDatasourceId(Integer datasourceId) {
		this.datasourceId = datasourceId;
	}
	
	public Integer getDatasetId() {
		return datasetId;
	}
	
	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}
	
	public boolean hasInput() {
		return hasInput;
	}
	
	public void setHasInput(boolean hasInput) {
		this.hasInput = hasInput;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
