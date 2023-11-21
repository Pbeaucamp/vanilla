package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class APIProperties implements DataServiceAttribute {

	private static final long serialVersionUID = 1L;
	
	public enum TypeSecurity {
		NO_AUTH(0),
		OAUTH20(1),
		API_KEY(2),
		BASIC_AUTH(3);

		private int type;

		private static Map<Integer, TypeSecurity> map = new HashMap<Integer, TypeSecurity>();
		static {
			for (TypeSecurity serverType : TypeSecurity.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private TypeSecurity(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeSecurity valueOf(int serverType) {
			return map.get(serverType);
		}
	}
	
	public enum TypeGrant {
		CLIENT_CREDENTIALS(0);

		private int type;

		private static Map<Integer, TypeGrant> map = new HashMap<Integer, TypeGrant>();
		static {
			for (TypeGrant serverType : TypeGrant.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private TypeGrant(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeGrant valueOf(int serverType) {
			return map.get(serverType);
		}
	}
	
	private VariableString url = new VariableString();
	private VariableString layer = new VariableString();
	private VariableString outputName = new VariableString();
	private boolean unzip;
	
	private TypeSecurity typeSecurity = TypeSecurity.NO_AUTH;
	private TypeGrant typeGrant = TypeGrant.CLIENT_CREDENTIALS;
	
	private ISecurityDataService security;

	public APIProperties() {
	}

	public VariableString getUrlVS() {
		return url;
	}

	public String getUrDisplay() {
		return url.getStringForTextbox();
	}

	public void setUr(VariableString outputName) {
		this.url = outputName;
	}

	public String getUrl(List<Parameter> parameters, List<Variable> variables) {
		return url.getString(parameters, variables);
	}
	
	public VariableString getLayerVS() {
		return layer;
	}
	
	public String getLayerDisplay() {
		return layer.getStringForTextbox();
	}
	
	public void setLayer(VariableString layer) {
		this.layer = layer;
	}
	
	public String getLayer(List<Parameter> parameters, List<Variable> variables) {
		return layer.getString(parameters, variables);
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
	public boolean isValid() {
		return outputName != null && !outputName.getStringForTextbox().isEmpty() && (security == null || security.isValid());
	}
	
	public boolean isUnzip() {
		return unzip;
	}
	
	public void setUnzip(boolean unzip) {
		this.unzip = unzip;
	}
	
	public TypeSecurity getTypeSecurity() {
		return typeSecurity;
	}
	
	public void setTypeSecurity(TypeSecurity typeSecurity) {
		this.typeSecurity = typeSecurity;
	}
	
	public TypeGrant getTypeGrant() {
		return typeGrant;
	}
	
	public void setTypeGrant(TypeGrant typeGrant) {
		this.typeGrant = typeGrant;
	}
	
	public void setSecurity(ISecurityDataService security) {
		this.security = security;
	}
	
	public ISecurityDataService getSecurity() {
		return security;
	}

	@Override
	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return outputName.getString(parameters, variables);
	}

	@Override
	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables) {
		return url.getString(parameters, variables);
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(url != null ? url.getVariables() : new ArrayList<Variable>());
		variables.addAll(outputName != null ? outputName.getVariables() : new ArrayList<Variable>());
		variables.addAll(security != null ? security.getVariables(resources) : new ArrayList<Variable>());
		variables.addAll(layer != null ? layer.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(url != null ? url.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(outputName != null ? outputName.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(security != null ? security.getParameters(resources) : new ArrayList<Parameter>());
		parameters.addAll(layer != null ? layer.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}

}
