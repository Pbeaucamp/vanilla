package bpm.faweb.shared.drill;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DrillthroughFilter implements IsSerializable {
	
	private static final String CONTAINS_NAME = "Contains";
	private static final String DOES_NOT_CONTAINS_NAME = "Does Not Contain";
	private static final String STARTS_WITH_NAME = "Starts With";
	private static final String ENDS_WITH_NAME = "Ends With";
	private static final String GREATER_THAN_NAME = "Greater Than";
	private static final String LESS_THAN_NAME = "Less Than";
	private static final String GREATER_THAN_OR_EQUAL_NAME = "Greater Than or Equal";
	private static final String LESS_THAN_OR_EQUAL_NAME = "Less Than or Equal";
	private static final String BETWEEN_NAME = "Between";
	private static final String NOT_BETWEEN_NAME = "Not Between";
	private static final String IS_EMPTY_NAME = "Is Empty";
	private static final String IS_NOT_EMPTY_NAME = "Is not Empty";
	
	public enum FilterType {
		CONTAINS(CONTAINS_NAME, 0),
		DOES_NOT_CONTAINS(DOES_NOT_CONTAINS_NAME,1),
		STARTS_WITH(STARTS_WITH_NAME,2),
		ENDS_WITH(ENDS_WITH_NAME,3),
		GREATER_THAN(GREATER_THAN_NAME,4),
		LESS_THAN(LESS_THAN_NAME,5),
		GREATER_THAN_OR_EQUAL_TO(GREATER_THAN_OR_EQUAL_NAME,6),
		LESS_THAN_OR_EQUAL_TO(LESS_THAN_OR_EQUAL_NAME,7),
		BETWEEN(BETWEEN_NAME,8),
		NOT_BETWEEN(NOT_BETWEEN_NAME,9),
		IS_EMPTY(IS_EMPTY_NAME,10),
		NOT_IS_EMPTY(IS_NOT_EMPTY_NAME,11);
		
		private String name;
		private int type;
		
		private FilterType(String name, int type) {
			this.name = name;
			this.type = type;
		}
		
		public String getName() {
			return name;
		}
		
		public int getType() {
			return type;
		}
	}

	public static final String[] FILTER_OPTIONS = new String[] { CONTAINS_NAME, DOES_NOT_CONTAINS_NAME, STARTS_WITH_NAME, ENDS_WITH_NAME, GREATER_THAN_NAME, LESS_THAN_NAME, GREATER_THAN_OR_EQUAL_NAME, LESS_THAN_OR_EQUAL_NAME, BETWEEN_NAME, NOT_BETWEEN_NAME, IS_EMPTY_NAME, IS_NOT_EMPTY_NAME };

	private int columnIndex;
	private String columnName;
	private FilterType type;
	private String value;
	
	public DrillthroughFilter() { }
	
	public DrillthroughFilter(int columnIndex, String columnName, FilterType type, String value) {
		this.columnIndex = columnIndex;
		this.columnName = columnName;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return type.getName() + " : " + value + "[" + columnName + "]";
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public FilterType getType() {
		return type;
	}

	public void setType(FilterType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}
