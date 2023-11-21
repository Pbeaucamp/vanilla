package bpm.data.viz.core.preparation;

import java.io.Serializable;

public class ExportPreparationInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private DataPreparation dataPreparation;
	private String exportType;
	private String separator = ";";

	public ExportPreparationInfo() {

	}

	public ExportPreparationInfo(String name, DataPreparation dataPreparation, String exportType, String separator) {
		this.name = name;
		this.dataPreparation = dataPreparation;
		this.exportType = exportType;
		this.separator = separator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataPreparation getDataPreparation() {
		return dataPreparation;
	}

	public void setDataPreparation(DataPreparation dataPreparation) {
		this.dataPreparation = dataPreparation;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

}
