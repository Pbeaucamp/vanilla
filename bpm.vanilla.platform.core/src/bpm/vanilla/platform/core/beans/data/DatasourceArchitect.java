package bpm.vanilla.platform.core.beans.data;

public class DatasourceArchitect implements IDatasourceObject {

	private static final long serialVersionUID = 1L;

	private String url;
	private String user;
	private String password;

	private int contractId;
	private boolean hasInput;
	private int supplierId;
	private String separator;

	public boolean isOtherRepository() {
		return url != null && !url.isEmpty();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getContractId() {
		return contractId;
	}

	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public boolean hasInput() {
		return hasInput;
	}
	
	public void setHasInput(boolean hasInput) {
		this.hasInput = hasInput;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		DatasourceArchitect architect = (DatasourceArchitect) o;
		return contractId == architect.getContractId() && supplierId == architect.getSupplierId() && url.equals(architect.getUrl());
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

}
