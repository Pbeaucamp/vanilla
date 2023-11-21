package bpm.vanillahub.core.beans.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.workflow.commons.beans.TypeActivity;

public class ActionActivity extends ActivityWithResource<Source> {

	public enum TypeAction {
		UNZIP(0), COPY(1), MOVE(2), DELETE(3);

		private int type;

		private static Map<Integer, TypeAction> map = new HashMap<Integer, TypeAction>();
		static {
			for (TypeAction actionType : TypeAction.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeAction(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeAction valueOf(int actionType) {
			return map.get(actionType);
		}
	}

	private TypeAction type;

	private Source target;

	public ActionActivity() {
	}

	public ActionActivity(String name) {
		super(TypeActivity.ACTION, name);
		this.type = TypeAction.UNZIP;
	}

	public TypeAction getTypeAction() {
		return type;
	}

	public void setTypeAction(TypeAction type) {
		this.type = type;
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0;
	}

	public void setTarget(Source target) {
		this.target = target;
	}
	
	public int getTargetId() {
		return target != null ? target.getId() : -1;
	}

	public Source getTarget(List<Source> resources) {
		return (Source) (target != null ? findResource(target, resources) : null);
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = super.getVariables(resources);
		if (target != null) {
			variables.addAll(target.getVariables());
		}
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = super.getParameters(resources);
		if (target != null) {
			parameters.addAll(target.getParameters());
		}
		return parameters;
	}
}
