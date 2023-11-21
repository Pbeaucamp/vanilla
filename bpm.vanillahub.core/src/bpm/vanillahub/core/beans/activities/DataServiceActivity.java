package bpm.vanillahub.core.beans.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.DataServiceAttribute;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class DataServiceActivity extends Activity {
	
	public enum TypeService {
		QUANDL(0),
		EBAY_FINDING(1),
		URL(2),
		API(3),
		WFS(4);

		private int type;

		private static Map<Integer, TypeService> map = new HashMap<Integer, TypeService>();
		static {
			for (TypeService type : TypeService.values()) {
				map.put(type.getType(), type);
			}
		}

		private TypeService(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeService valueOf(int type) {
			return map.get(type);
		}
	}
	
	private TypeService typeService;
	private DataServiceAttribute attribute;
	
	public DataServiceActivity() {
	}

	public DataServiceActivity(String name) {
		super(TypeActivity.DATA_SERVICE, name);
	}
	
	public TypeService getTypeService() {
		return typeService;
	}
	
	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}
	
	public void setAttribute(DataServiceAttribute attribute) {
		this.attribute = attribute;
	}
	
	public DataServiceAttribute getAttribute() {
		return attribute;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return attribute.getVariables(resources);
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return attribute.getParameters(resources);
	}

	@Override
	public boolean isValid() {
		return attribute.isValid();
	}

	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables) {
		return attribute.buildDataUrl(parameters, variables);
	}
	
	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return attribute.getOutputName(parameters, variables);
	}
}
