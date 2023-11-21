package bpm.vanilla.platform.core.beans.data;

public class DatasourceR implements IDatasourceObject {

	private static final long serialVersionUID = 1L;

	private String packageR;
	private String originalDatasetR;

	public String getPackageR() {
		return packageR;
	}

	public void setPackageR(String packageR) {
		this.packageR = packageR;
	}

	public String getOriginalDatasetR() {
		return originalDatasetR;
	}

	public void setOriginalDatasetR(String originalDatasetR) {
		this.originalDatasetR = originalDatasetR;
	}

	@Override
	public boolean equals(Object o) {
		return packageR.equals(((DatasourceR)o).getPackageR()) && originalDatasetR.equals(((DatasourceR)o).getOriginalDatasetR());
	}
	
}
