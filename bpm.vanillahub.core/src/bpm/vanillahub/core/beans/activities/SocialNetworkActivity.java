package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.workflow.commons.beans.TypeActivity;

public class SocialNetworkActivity extends ActivityWithResource<SocialNetworkServer> {

	public enum SocialNetworkType {
		FACEBOOK(0), TWITTER(1), YOUTUBE(2);

		private int type;

		private static Map<Integer, SocialNetworkType> map = new HashMap<Integer, SocialNetworkType>();
		static {
			for (SocialNetworkType serverType : SocialNetworkType.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private SocialNetworkType(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static SocialNetworkType valueOf(int serverType) {
			return map.get(serverType);
		}
	}

	private VariableString outputFile = new VariableString();
	
	private SocialNetworkType type;
	private Integer method;
	
	private VariableString query = new VariableString();
	private String topic;

	public SocialNetworkActivity() {
	}

	public SocialNetworkActivity(String name) {
		super(TypeActivity.SOCIAL, name);
	}
	
	public VariableString getOutputFileVS() {
		return outputFile;
	}
	
	public String getOutputFileDisplay() {
		return outputFile.getStringForTextbox();
	}
	
	public void setOutputFile(VariableString outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputFile(List<Parameter> parameters, List<Variable> variables) {
		return outputFile.getString(parameters, variables);
	}
	
	public SocialNetworkType getSocialNetworkType() {
		return type;
	}
	
	public void setSocialNetworkType(SocialNetworkType type) {
		this.type = type;
	}

	public Integer getMethod() {
		return method;
	}

	public void setMethod(Integer method) {
		this.method = method;
	}
	
	public VariableString getQueryVS() {
		return query;
	}
	
	public String getQueryDisplay() {
		return query.getStringForTextbox();
	}
	
	public void setQuery(VariableString query) {
		this.query = query;
	}

	public String getQuery(List<Parameter> parameters, List<Variable> variables) {
		return query.getString(parameters, variables);
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public boolean isValid() {
		if (type == SocialNetworkType.TWITTER) {
			return method != null;
		}
		else if (type == SocialNetworkType.FACEBOOK) {
			return getResourceId() > 0 &&  method != null;
		}
		else if (type == SocialNetworkType.YOUTUBE) {
			return method != null;
		}
		return method != null;
	}
	
	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(outputFile != null ? outputFile.getVariables() : new ArrayList<Variable>());
		variables.addAll(query != null ? query.getVariables() : new ArrayList<Variable>());
		variables.addAll(super.getVariables(resources));
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(outputFile != null ? outputFile.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(query != null ? query.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(super.getParameters(resources));
		return parameters;
	}

}
