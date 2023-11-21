package bpm.vanilla.api.dto;

import org.springframework.web.multipart.MultipartFile;

public class UploadFile {

	private int type;
	
	private int integrationId;
	private int contractId;
	private int documentId;
	private String format;
	
	private MultipartFile file;
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getIntegrationId() {
		return integrationId;
	}

	public void setIntegrationId(int integrationId) {
		this.integrationId = integrationId;
	}

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public int getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}

	public MultipartFile getFile() {
		return file;
	}
	
	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
