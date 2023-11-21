package bpm.data.viz.core.preparation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.data.DataColumn;
public class PreparationRule implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum RuleType {
		UPPER_CASE("UPPER_CASE", true), LOWER_CASE("LOWER_CASE", true), RECODE("RECODE", true), SORT("SORT", false), ADD_CHAR("ADD_CHAR", true), NORMALIZE("NORMALIZE", true), 
		ROUND("ROUND", true), FORMAT_NUMBER("FORMAT_NUMBER", true), MIN("MIN", true), MAX("MAX", true),
		DATE_TO_AGE("DATE_TO_AGE", true), GROUP("GROUP", true), CALC("CALC", true), DEDOUBLON("DEDOUBLON", false), FILTER("FILTER", false), AFFECTER("AFFECTER", true), LIBREOFFICE("LIBRE_OFFICE", false);
		
		private String name;
		private boolean canCreateColumn;
		
		private RuleType(String name, boolean canCreateColumn) {
			this.name = name;
			this.canCreateColumn = canCreateColumn;
		}
		
		public String getLabel() {
			return this.name;
		}
		
		public boolean canCreateColumn() {
			return canCreateColumn;
		}
	}
	protected int id;
	
	protected RuleType type;
	
	protected boolean enabled;
	
	protected List<DataColumn> columns = new ArrayList<>();
	protected int rowNumber = -1;
	protected boolean newColumn;
	protected String newColumnName;
	protected boolean multiColumn;
	
	public PreparationRule() {
		
	}
	
	public PreparationRule(RuleType type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RuleType getType() {
		return type;
	}

	public void setType(RuleType type) {
		this.type = type;
	}

	public DataColumn getColumn() {
		if(columns != null && !columns.isEmpty()) {
			return columns.get(0);
		}
		return null;
	}
	
	public List<DataColumn> getColumns() {
		return columns;
	}

	public void setColumn(DataColumn column) {
		this.columns = new ArrayList<>();
		this.columns.add(column);
	}
	
	public void setColumns(List<DataColumn> column) {
		this.columns = column;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public boolean isNewColumn() {
		return newColumn;
	}

	public void setNewColumn(boolean newColumn) {
		this.newColumn = newColumn;
	}

	public boolean isMultiColumn() {
		switch(type) {
			case DEDOUBLON:
				return true;

			default:
				return multiColumn;
		}
	}

	public void setMultiColumn(boolean multiColumn) {
		this.multiColumn = multiColumn;
	}

	public String getNewColumnName() {
		return newColumnName;
	}

	public void setNewColumnName(String newColumnName) {
		this.newColumnName = newColumnName;
	}
	
	

}
