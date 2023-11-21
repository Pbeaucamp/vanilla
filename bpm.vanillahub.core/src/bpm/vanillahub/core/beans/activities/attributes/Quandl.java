package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.Constants;

public class Quandl implements DataServiceAttribute {

	public static final String HELP_URL = "https://blog.quandl.com/getting-started-with-the-quandl-api";
	private static final String QUANDL_API_URL = "https://www.quandl.com/api/v3/datasets/";

	public enum FormatOutput {
		XML(0, "XML"), CSV(1, "CSV"), JSON(2, "JSON");

		private int type;
		private String format;

		private static Map<Integer, FormatOutput> map = new HashMap<Integer, FormatOutput>();
		static {
			for (FormatOutput formatOutput : FormatOutput.values()) {
				map.put(formatOutput.getType(), formatOutput);
			}
		}

		private FormatOutput(int type, String format) {
			this.type = type;
			this.format = format;
		}

		public int getType() {
			return type;
		}

		public String getFormat() {
			return format;
		}

		public static FormatOutput valueOf(int formatOutput) {
			return map.get(formatOutput);
		}
	}

	private VariableString apiKey = new VariableString();
	private VariableString base = new VariableString();
	private VariableString indicator = new VariableString();
	private VariableString outputName = new VariableString();

	private FormatOutput formatOutput;

	private List<QuandlParameter> parameters;

	public Quandl() {
	}

	public VariableString getApiKeyVS() {
		return apiKey;
	}

	public String getApiKeyDisplay() {
		return apiKey.getStringForTextbox();
	}

	public void setApiKey(VariableString apiKey) {
		this.apiKey = apiKey;
	}

	public VariableString getBaseVS() {
		return base;
	}

	public String getBaseDisplay() {
		return base.getStringForTextbox();
	}

	public void setBase(VariableString base) {
		this.base = base;
	}

	public VariableString getIndicatorVS() {
		return indicator;
	}

	public String getIndicatorDisplay() {
		return indicator.getStringForTextbox();
	}

	public void setIndicator(VariableString indicator) {
		this.indicator = indicator;
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

	@Override
	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return outputName.getString(parameters, variables) + "." + getFormat();
	}

	public void setFormatOutput(FormatOutput formatOutput) {
		this.formatOutput = formatOutput;
	}
	
	public FormatOutput getFormatOutput() {
		return formatOutput;
	}

	public List<QuandlParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<QuandlParameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(QuandlParameter parameter) {
		if (parameters == null) {
			this.parameters = new ArrayList<>();
		}
		this.parameters.add(parameter);
	}

	@Override
	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables) {
		String apiKeyStr = apiKey.getString(parameters, variables);
		String baseStr = base.getString(parameters, variables);
		String indicatorStr = indicator.getString(parameters, variables);
		String formatStr = getFormat();

		StringBuilder builder = new StringBuilder(QUANDL_API_URL);
		builder.append(baseStr);
		builder.append("/");
		builder.append(indicatorStr);
		builder.append("." + formatStr);

		boolean first = true;
		if (!apiKeyStr.isEmpty()) {
			first = false;
			builder.append("?");
			builder.append("api_key=" + apiKeyStr);
		}

		if (this.parameters != null && !this.parameters.isEmpty()) {
			for (QuandlParameter param : this.parameters) {
				if (first) {
					first = false;
					builder.append("?");
				}
				else {
					builder.append("&");
				}
				builder.append(param.getValue(parameters, variables));
			}
		}
		return builder.toString();
	}

	private String getFormat() {
		if (formatOutput != null) {
			switch (formatOutput) {
			case CSV:
				return Constants.CSV;
			case XML:
				return Constants.XML;
			case JSON:
				return Constants.JSON;
			default:
				break;
			}
		}

		return Constants.XML;
	}

	@Override
	public boolean isValid() {
		return outputName != null && !outputName.getStringForTextbox().isEmpty() && base != null && !base.getStringForTextbox().isEmpty() && indicator != null && !indicator.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(base.getVariables());
		variables.addAll(indicator.getVariables());
		variables.addAll(outputName.getVariables());
		if (parameters != null) {
			for (QuandlParameter param : parameters) {
				variables.addAll(param.getVariables());
			}
		}
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(base.getParameters());
		parameters.addAll(indicator.getParameters());
		parameters.addAll(outputName.getParameters());
		if (this.parameters != null) {
			for (QuandlParameter param : this.parameters) {
				parameters.addAll(param.getParameters());
			}
		}
		return parameters;
	}

}
