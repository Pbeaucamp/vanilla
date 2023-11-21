package bpm.vanilla.platform.core.beans.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameter extends Resource {

	private static final long serialVersionUID = 130754870418847122L;
	
	public enum TypeParameter {
		SIMPLE(0),
		LOV(1),
		RANGE(2),
		SELECTION(3),
		DB(4);
		
		private int type;

		private static Map<Integer, TypeParameter> map = new HashMap<Integer, TypeParameter>();
		static {
	        for (TypeParameter serverType : TypeParameter.values()) {
	            map.put(serverType.getType(), serverType);
	        }
	    }
		
		private TypeParameter(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static TypeParameter valueOf(int serverType) {
	        return map.get(serverType);
	    }
	}
	
	public enum Filter {
		EQUALNUM(0), SUP(1), INF(2),
		EQUAL(3), CONTAIN(4), NOTCONTAIN(5);

	    private final int filtre;  
	    
	    private static Map<Integer, Filter> map = new HashMap<Integer, Filter>();
		static {
	        for (Filter filtertype : Filter.values()) {
	            map.put(filtertype.getFiltre(), filtertype);
	        }
	    }

	    private Filter(int s) {
	        filtre = s;
	    }

	    public static Filter valueOf(int filtertype) {
	        return map.get(filtertype);
	    }

	    public int getFiltre() {
	       return this.filtre;
	    }
		
	}

	private String question;

	private boolean useListOfValues;
	
	private String defaultValue;
	private String value;
	private List<String> valueRange; //kmo for excel
	private ListOfValues lov;
	private String dataset = null;
	private String column = null;
	
	private int idParentParam = 0;
	private transient Parameter parentParam = null;
	private Filter filter = Filter.EQUAL;
	
	private TypeParameter parameterType = TypeParameter.SIMPLE;
	
	public Parameter() {
		super("", TypeResource.PARAMETER);
	}

	public Parameter(String name) {
		super(name, TypeResource.PARAMETER);
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	
	public boolean useListOfValues() {
		return useListOfValues;
	}

	public String getValue() {
		return value;
	}
	
	public List<String> getValueRange() {
		return valueRange;
	}

	public void setValueString(String value) {
		this.useListOfValues = false;
		this.value = value;
		this.parameterType = TypeParameter.SIMPLE;
	}
	
	public void setValueList(List<String> value) {
		this.useListOfValues = false;
		this.valueRange = value;
		this.parameterType = TypeParameter.RANGE;
	}
	
	public ListOfValues getListOfValues() {
		return lov;
	}

	public void setValueListOfValues(ListOfValues value) {
		this.useListOfValues = true;
		this.lov = value;
		this.parameterType = TypeParameter.LOV;
	}
	
	public void setSelectionListOfValues(ListOfValues value) {
		this.useListOfValues = false;
		this.lov = value;
		this.dataset = null;
		this.column = null;
		this.parameterType = TypeParameter.SELECTION;
	}
	
	public void setSelectionDataset(String dataset, String column) {
		this.useListOfValues = false;
		this.lov = null;
		this.dataset = dataset;
		this.column = column;
		this.parameterType = TypeParameter.SELECTION;
	}
	
	public void setSelectionValue(String value) {
		this.useListOfValues = false;
		this.value = value;
		this.parameterType = TypeParameter.SELECTION;
	}
	
	public void setDataset(String dataset, String column) {
		this.useListOfValues = false;
		this.lov = null;
		this.dataset = dataset;
		this.column = column;
		this.parameterType = TypeParameter.DB;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void updateInfo(String name, String question, boolean useListOfValues, String value, String defaultValue, ListOfValues lov) {
		setName(name);
		this.question = question;
		this.useListOfValues = useListOfValues;
		this.value = value;
		this.defaultValue = defaultValue;
		this.lov = lov;
	}

	public String getParameterName() {
		return "{$P_" + getName() + "}";
	}
	
	@Override
	public List<Variable> getVariables() {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

	public TypeParameter getParameterType() {
		return parameterType;
	}

	public void setParameterType(TypeParameter parameterType) {
		this.parameterType = parameterType;
	}

	
	public String getDataset() {
		return dataset;
	}

	public String getColumn() {
		return column;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public void setColumn(String column) {
		this.column = column;
	}
	
	public int getIdParentParam() {
		return idParentParam;
	}

	public void setIdParentParam(int idParentParam) {
		this.idParentParam = idParentParam;
	}

	public Parameter getParentParam() {
		return parentParam;
	}

	public void setParentParam(Parameter parentParam) {
		this.parentParam = parentParam;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public String getRequest() {
		String val = (parentParam.getValue()!=null)? parentParam.getValue() : parentParam.getDefaultValue();
		String result ="";
		switch(filter){
		case EQUAL:
			result = parentParam.getColumn() + "== '" + val + "'";
			break;
		case CONTAIN:
			result = "grepl('"+ val +"', " + parentParam.getColumn() + ")";
			break;
		case NOTCONTAIN:
			result = "!grepl('"+ val +"', " + parentParam.getColumn() + ")";
			break;
		case EQUALNUM:
			result = parentParam.getColumn() + "== " + val;
			break;
		case INF:
			result = parentParam.getColumn() + "< " + val;
			break;
		case SUP:
			result = parentParam.getColumn() + "> " + val;
			break;
		}
		return result;
	}
	
	 public List<Filter> getNumFilters() {
    	List<Filter> result = new ArrayList<Filter>();
    	result.add(Filter.EQUALNUM);
    	result.add(Filter.INF);
    	result.add(Filter.SUP);
        return result;
    }
	    
    public List<Filter> getQualFilters() {
    	List<Filter> result = new ArrayList<Filter>();
    	result.add(Filter.EQUAL);
    	result.add(Filter.CONTAIN);
    	result.add(Filter.NOTCONTAIN);
        return result;
    }
	
}
