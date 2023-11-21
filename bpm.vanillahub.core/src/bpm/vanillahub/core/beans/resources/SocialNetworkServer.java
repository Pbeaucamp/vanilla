package bpm.vanillahub.core.beans.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class SocialNetworkServer extends Resource {

	public enum SocialNetworkType {
		FACEBOOK(0);

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

	public enum FacebookMethod {
		SEARCH_PLACES(0);

		private int type;

		private static Map<Integer, FacebookMethod> map = new HashMap<Integer, FacebookMethod>();
		static {
			for (FacebookMethod serverType : FacebookMethod.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private FacebookMethod(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static FacebookMethod valueOf(int serverType) {
			return map.get(serverType);
		}
	}

	public SocialNetworkType type;
	
	private VariableString token = new VariableString();
	
	public SocialNetworkServer() {
		super("", TypeResource.SOCIAL_SERVER);
	}
	
	public SocialNetworkServer(String name) {
		super(name, TypeResource.SOCIAL_SERVER);
	}
	
	public SocialNetworkType getType() {
		return type;
	}
	
	public void setType(SocialNetworkType type) {
		this.type = type;
	}
	
	public VariableString getTokenVS() {
		return token;
	}
	
	public String getTokenDisplay() {
		return token.getStringForTextbox();
	}
	
	public void setToken(VariableString token) {
		this.token = token;
	}

	public String getToken(List<Parameter> parameters, List<Variable> variables) {
		return token.getString(parameters, variables);
	}

	public void updateInfo(String name, SocialNetworkType type, VariableString token) {
		setName(name);
		this.type = type;
		this.token = token;
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		return parameters;
	}

}
