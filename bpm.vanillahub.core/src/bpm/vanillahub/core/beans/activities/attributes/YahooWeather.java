package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.Constants;

public class YahooWeather implements DataServiceAttribute {

	public static final String API_URL = "https://query.yahooapis.com/v1/public/yql?q=";
	public static final String QUERY_TYPE = "{$Type}";
	public static final String QUERY_LOCATION = "{$Location}";
	
	public static final String QUERY = "select " + QUERY_TYPE + " from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"" + QUERY_LOCATION + "\")";
	public static final String FORMAT = "&format=xml";
	public static final String ENV = "&env=";
	public static final String ENV_URL = "store://datatables.org/alltableswithkeys";

	public enum TypeWeather {
		FORECAST(0, "*"), 
		CURRENT_CONDITIONS(1, "item.condition"), 
		WIND(2, "wind");

		private int type;
		private String query;

		private static Map<Integer, TypeWeather> map = new HashMap<Integer, TypeWeather>();
		static {
			for (TypeWeather type : TypeWeather.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeWeather(int type, String query) {
			this.type = type;
			this.query = query;
		}

		public int getType() {
			return type;
		}
		
		public String getQuery() {
			return query;
		}

		public static TypeWeather valueOf(int type) {
			return map.get(type);
		}
	}

	private TypeWeather typeWeather;

	private VariableString outputName = new VariableString();
	private VariableString location = new VariableString();

	public YahooWeather() {
	}
	
	public void setTypeWeather(TypeWeather typeWeather) {
		this.typeWeather = typeWeather;
	}
	
	public TypeWeather getTypeWeather() {
		return typeWeather;
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
		return outputName.getString(parameters, variables) + "." + Constants.XML;
	}


	public VariableString getLocationVS() {
		return location;
	}

	public String getLocationDisplay() {
		return location.getStringForTextbox();
	}

	public void setLocation(VariableString location) {
		this.location = location;
	}

	public String getLocation(List<Parameter> parameters, List<Variable> variables) {
		return location.getString(parameters, variables);
	}

	@Override
	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables) {
		String locationStr = location.getString(parameters, variables);
		String query = QUERY.replace(QUERY_TYPE, typeWeather.getQuery());
		query = query.replace(QUERY_LOCATION, locationStr);

		StringBuilder builder = new StringBuilder(API_URL);
		builder.append(query);
		builder.append(FORMAT);
		builder.append(ENV);
		builder.append(ENV_URL);

		return builder.toString();
	}

	@Override
	public boolean isValid() {
		return outputName != null && !outputName.getStringForTextbox().isEmpty() && location != null && !location.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(location.getVariables());
		variables.addAll(location.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(location.getParameters());
		parameters.addAll(location.getParameters());
		return parameters;
	}

}
