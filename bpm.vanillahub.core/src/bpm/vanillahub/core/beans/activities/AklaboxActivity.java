package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.activities.attributes.AklaboxDispatch;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.workflow.commons.beans.TypeActivity;

public class AklaboxActivity extends ActivityWithResource<AklaboxServer> {

	private static final long serialVersionUID = 1L;

	public enum AklaboxApp {
		AKLABOX(0), 
		AKLAD(1);

		private int type;

		private static Map<Integer, AklaboxApp> map = new HashMap<Integer, AklaboxApp>();
		static {
			for (AklaboxApp aklaboxApp : AklaboxApp.values()) {
				map.put(aklaboxApp.getType(), aklaboxApp);
			}
		}

		private AklaboxApp(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static AklaboxApp valueOf(int aklaboxApp) {
			return map.get(aklaboxApp);
		}
	}
	
	private AklaboxApp app;

	private Integer itemId;
	private String itemName;
	
	private VariableString projectName = new VariableString();
	private boolean runOcr;
	private String lang;
	
	private List<AklaboxDispatch> rules;
	
	public AklaboxActivity() {
		super();
	}

	public AklaboxActivity(String name) {
		super(TypeActivity.AKLABOX, name);
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0 && app != null;
	}
	
	public AklaboxApp getApp() {
		return app;
	}
	
	public void setApp(AklaboxApp app) {
		this.app = app;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}	
	
	public VariableString getProjectNameVS() {
		return projectName;
	}
	
	public String getProjectNameDisplay() {
		return projectName.getStringForTextbox();
	}
	
	public void setProjectName(VariableString projectName) {
		this.projectName = projectName;
	}

	public String getProjectName(List<Parameter> parameters, List<Variable> variables) {
		return projectName.getString(parameters, variables);
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return projectName.getVariables();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return projectName.getParameters();
	}
	
	public boolean isRunOcr() {
		return runOcr;
	}
	
	public void setRunOcr(boolean runOcr) {
		this.runOcr = runOcr;
	}
	
	public String getLang() {
		return lang;
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public List<AklaboxDispatch> getRules() {
		return rules;
	}
	
	public void addRule(AklaboxDispatch rule) {
		if (rules == null) {
			this.rules = new ArrayList<>();
		}
		this.rules.add(rule);
	}
	
	public void removeRule(AklaboxDispatch rule) {
		if (rules != null) {
			this.rules.remove(rule);
		}
	}
}
