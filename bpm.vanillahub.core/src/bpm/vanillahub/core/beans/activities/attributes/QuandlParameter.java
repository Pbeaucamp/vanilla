package bpm.vanillahub.core.beans.activities.attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class QuandlParameter implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//Sort order (value can be asc/desc)
	private static final String SORT_ORDER = "order";
	
	//Exclude headers (value can be true/false)
	private static final String EXCLUDE_HEADER = "exclude_column_names";
	
	//Data truncation (value = n) get n latest rows
	private static final String ROWS = "rows";
	
	//Specific date range (value need to have the following format yyyy-MM-dd)
	private static final String DATE_START = "start_date";
	private static final String DATE_END = "end_date";
	
	//Specific columns (value = n) get the column at index n
	private static final String SPECIFIC_COLUMN = "column_index";
	
	//Frequency (value can be none/daily/weekly/monthly/quarterly/annual
	public static final String FREQUENCY = "collapse";
	
	//Calculations (value can be none/diff/rdiff/cumul/normalize)
	public static final String CALCULATIONS = "transform";
	
	public enum TypeParam {
		SORT_ORDER(0),
		EXCLUDE_HEADER(1),
		ROWS(2),
		DATE_START(3),
		DATE_END(4),
		SPECIFIC_COLUMN(5),
		FREQUENCY(6),
		CALCULATIONS(7);
		
		private int type;

		private static Map<Integer, TypeParam> map = new HashMap<Integer, TypeParam>();
		static {
	        for (TypeParam typeParam : TypeParam.values()) {
	            map.put(typeParam.getType(), typeParam);
	        }
	    }
		
		private TypeParam(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static TypeParam valueOf(int typeParam) {
	        return map.get(typeParam);
	    }
	}
	
	private TypeParam typeParam;
	private VariableString value;
	
	public TypeParam getTypeParam() {
		return typeParam;
	}
	
	public void setTypeParam(TypeParam typeParam) {
		this.typeParam = typeParam;
	}

	public VariableString getValue() {
		return value;
	}
	
	public void setValue(VariableString value) {
		this.value = value;
	}

	public String getValue(List<Parameter> parameters, List<Variable> variables) {
		StringBuilder builder = new StringBuilder();
		
		switch (typeParam) {
		case SORT_ORDER:
			builder.append(SORT_ORDER + "=" + value.getString(parameters, variables));
			break;
		case EXCLUDE_HEADER:
			builder.append(EXCLUDE_HEADER + "=" + value.getString(parameters, variables));
			break;
		case ROWS:
			builder.append(ROWS + "=" + value.getString(parameters, variables));
			break;
		case DATE_START:
			builder.append(DATE_START + "=" + value.getString(parameters, variables));
			break;
		case DATE_END:
			builder.append(DATE_END + "=" + value.getString(parameters, variables));
			break;
		case SPECIFIC_COLUMN:
			builder.append(SPECIFIC_COLUMN + "=" + value.getString(parameters, variables));
			break;
		case FREQUENCY:
			builder.append(FREQUENCY + "=" + value.getString(parameters, variables));
			break;
		case CALCULATIONS:
			builder.append(CALCULATIONS + "=" + value.getString(parameters, variables));
			break;

		default:
			break;
		}
		
		return builder.toString();
	}

	public List<Variable> getVariables() {
		return value != null ? value.getVariables() : new ArrayList<Variable>();
	}

	public List<Parameter> getParameters() {
		return value != null ? value.getParameters() : new ArrayList<Parameter>();
	}
}
