package bpm.vanilla.platform.core.beans.data;

public class DatasourceCsv implements IDatasourceObject {

	private static final long serialVersionUID = 4541113647173029720L;


	private String filePath;
	private String separator = ";";
	private boolean hasHeader = true;
	private String sourceType;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getSeparator() {
		if(separator == null) {
			separator = ";";
		}
 		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public boolean getHasHeader() {
		return hasHeader;
	}

	public void setHasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public boolean equals(Object o) {
		return filePath.equals(((DatasourceCsv)o).getFilePath()) && hasHeader == ((DatasourceCsv)o).getHasHeader() && separator.equals(((DatasourceCsv)o).getSeparator());
	}

}
