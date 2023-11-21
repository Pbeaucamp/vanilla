package bpm.vanilla.platform.core.beans.fmdt;

import java.util.ArrayList;
import java.util.List;

public class FmdtTableStruct extends FmdtData {

	private static final long serialVersionUID = 1L;

	private List<FmdtColumn> columns = null;
	private List<FmdtTableStruct> subTables = new ArrayList<FmdtTableStruct>();
	public FmdtTableStruct() {
		super();
	}

	public FmdtTableStruct(String name, String label, String description, List<FmdtColumn> columns) {
		super(name, label, description);
		this.columns = columns;
	}

	public List<FmdtColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<FmdtColumn> columns) {
		this.columns = columns;
	}

	public List<FmdtTableStruct> getSubTables() {
		return subTables;
	}

	public void setSubTables(List<FmdtTableStruct> subTables) {
		this.subTables = subTables;
	}

}
