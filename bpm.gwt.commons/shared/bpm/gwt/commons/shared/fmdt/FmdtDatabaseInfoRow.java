package bpm.gwt.commons.shared.fmdt;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FmdtDatabaseInfoRow extends FmdtRow implements IsSerializable {

	private List<String> columnType;

	public List<String> getColumnType() {
		if(columnType == null) {
			columnType = new ArrayList<String>();
		}
		return columnType;
	}

	public void setColumnType(List<String> columnType) {
		this.columnType = columnType;
	}
	
}
