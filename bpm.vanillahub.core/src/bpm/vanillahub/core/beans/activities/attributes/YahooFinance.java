package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.Constants;

public class YahooFinance implements DataServiceAttribute {

	public static final String HELP_URL = "https://code.google.com/p/yahoo-finance-managed/wiki/enumQuoteProperty";
	
	public static final String QUOTES_API_URL = "http://download.finance.yahoo.com/d/quotes.csv?s=";
	public static final String COMPANIES_API_URL = "http://biz.yahoo.com/p/csv/";

	public enum TypeFinance {
		QUOTES(0, "Quotes"), 
		COMPANIES(1, "Companies");

		private int type;
		private String name;

		private static Map<Integer, TypeFinance> map = new HashMap<Integer, TypeFinance>();
		static {
			for (TypeFinance type : TypeFinance.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeFinance(int type, String name) {
			this.type = type;
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public static TypeFinance valueOf(int type) {
			return map.get(type);
		}
	}

	private TypeFinance typeFinance;

	private VariableString ids = new VariableString();
	private VariableString properties = new VariableString();
	private VariableString outputName = new VariableString();

	public YahooFinance() {
	}

	public void setTypeFinance(TypeFinance typeFinance) {
		this.typeFinance = typeFinance;
	}

	public TypeFinance getTypeFinance() {
		return typeFinance;
	}

	public VariableString getIdsVS() {
		return ids;
	}

	public String getIdsDisplay() {
		return ids.getStringForTextbox();
	}

	public void setIds(VariableString ids) {
		this.ids = ids;
	}
	
	public String getIds(List<Parameter> parameters, List<Variable> variables) {
		return ids.getString(parameters, variables);
	}

	public VariableString getPropertiesVS() {
		return properties;
	}

	public String getPropertiesDisplay() {
		return properties.getStringForTextbox();
	}

	public void setProperties(VariableString properties) {
		this.properties = properties;
	}
	
	public String getProperties(List<Parameter> parameters, List<Variable> variables) {
		return properties.getString(parameters, variables);
	}

	public VariableString getOutputNameVS() {
		return outputName;
	}

	public String getOutputNameDisplay() {
		return outputName.getStringForTextbox();
	}

	public void setOutputName(VariableString outputName) {
		this.outputName = outputName;
	}

	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return outputName.getString(parameters, variables) + "." + Constants.CSV;
	}

	@Override
	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables) {
		String idsStr = ids.getString(parameters, variables);
		String propertiesStr = properties.getString(parameters, variables);

		StringBuilder builder = new StringBuilder();
		switch (typeFinance) {
		case COMPANIES:
			builder.append(COMPANIES_API_URL);
			break;
		case QUOTES:
			builder.append(QUOTES_API_URL);
			break;
		}

		builder.append(idsStr);

		if (propertiesStr != null && !propertiesStr.isEmpty()) {
			builder.append("&f=");
			builder.append(propertiesStr);
		}
		builder.append("&e=.csv");

		return builder.toString();
	}

	@Override
	public boolean isValid() {
		return outputName != null && !outputName.getStringForTextbox().isEmpty() && ids != null && !ids.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(ids.getVariables());
		variables.addAll(properties.getVariables());
		variables.addAll(outputName.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(ids.getParameters());
		parameters.addAll(properties.getParameters());
		parameters.addAll(outputName.getParameters());
		return parameters;
	}

}
