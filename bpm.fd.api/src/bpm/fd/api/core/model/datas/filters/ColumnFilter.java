package bpm.fd.api.core.model.datas.filters;

import java.io.Serializable;

public class ColumnFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value;
	private Integer columnIndex;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(Integer columnIndex) {
		this.columnIndex = columnIndex;
	}

}
