package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.resources.Cible;

public class CrawlActivity extends ActivityWithResource<Cible> {

	public enum TypeCrawl {
		EXTRACT(0), NUTCH(1), CRAWL4J(2);

		private int type;

		private static Map<Integer, TypeCrawl> map = new HashMap<Integer, TypeCrawl>();
		static {
			for (TypeCrawl actionType : TypeCrawl.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeCrawl(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeCrawl valueOf(int actionType) {
			return map.get(actionType);
		}
	}
	
	private TypeCrawl type = TypeCrawl.EXTRACT;
	
	private VariableString url = new VariableString();
	
	private VariableString outputFile = new VariableString();
	private VariableString baliseStart = new VariableString();
	private VariableString baliseEnd = new VariableString();

	private VariableString nutchUrl = new VariableString();
	private VariableString depth = new VariableString();
	
	private VariableString regex = new VariableString();
	private VariableString startWith = new VariableString();
	
	public CrawlActivity() { }
	
	public CrawlActivity(String name) {
		super(TypeActivity.CRAWL, name);
	}

	public TypeCrawl getTypeCrawl() {
		return type;
	}

	public void setTypeCrawl(TypeCrawl type) {
		this.type = type;
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

	public VariableString getUrlVS() {
		return url;
	}
	
	public String getUrlDisplay() {
		return url.getStringForTextbox();
	}

	public String getUrl(List<Parameter> parameters, List<Variable> variables) {
		return url.getString(parameters, variables);
	}

	public void setUrl(VariableString url) {
		this.url = url;
	}

	public VariableString getNutchUrlVS() {
		return nutchUrl;
	}
	
	public String getNutchUrlDisplay() {
		return nutchUrl.getStringForTextbox();
	}

	public String getNutchUrl(List<Parameter> parameters, List<Variable> variables) {
		return nutchUrl.getString(parameters, variables);
	}

	public void setNutchUrl(VariableString nutchUrl) {
		this.nutchUrl = nutchUrl;
	}

	public VariableString getBaliseStartVS() {
		return baliseStart;
	}
	
	public String getBaliseStartDisplay() {
		return baliseStart.getStringForTextbox();
	}

	public String getBaliseStart(List<Parameter> parameters, List<Variable> variables) {
		return baliseStart.getString(parameters, variables);
	}

	public void setBaliseStart(VariableString baliseStart) {
		this.baliseStart = baliseStart;
	}

	public VariableString getBaliseEndVS() {
		return baliseEnd;
	}
	
	public String getBaliseEndDisplay() {
		return baliseEnd.getStringForTextbox();
	}

	public String getBaliseEnd(List<Parameter> parameters, List<Variable> variables) {
		return baliseEnd.getString(parameters, variables);
	}

	public void setBaliseEnd(VariableString baliseEnd) {
		this.baliseEnd = baliseEnd;
	}

	public VariableString getDepthVS() {
		return depth;
	}
	
	public String getDepthDisplay() {
		return depth.getStringForTextbox();
	}

	public String getDepth(List<Parameter> parameters, List<Variable> variables) {
		return depth.getString(parameters, variables);
	}

	public void setDepth(VariableString depth) {
		this.depth = depth;
	}

	public VariableString getRegexVS() {
		return regex;
	}
	
	public String getRegexDisplay() {
		return regex.getStringForTextbox();
	}

	public String getRegex(List<Parameter> parameters, List<Variable> variables) {
		return regex.getString(parameters, variables);
	}

	public void setRegex(VariableString regex) {
		this.regex = regex;
	}

	public VariableString getStartWithVS() {
		return startWith;
	}
	
	public String getStartWithDisplay() {
		return startWith.getStringForTextbox();
	}

	public String getStartWith(List<Parameter> parameters, List<Variable> variables) {
		return startWith.getString(parameters, variables);
	}

	public void setStartWith(VariableString startWith) {
		this.startWith = startWith;
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(url != null ? url.getVariables() : new ArrayList<Variable>());
		variables.addAll(depth != null ? depth.getVariables() : new ArrayList<Variable>());
		variables.addAll(baliseStart != null ? baliseStart.getVariables() : new ArrayList<Variable>());
		variables.addAll(baliseEnd != null ? baliseEnd.getVariables() : new ArrayList<Variable>());
		variables.addAll(regex != null ? regex.getVariables() : new ArrayList<Variable>());
		variables.addAll(startWith != null ? startWith.getVariables() : new ArrayList<Variable>());
		variables.addAll(super.getVariables(resources));
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(url != null ? url.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(depth != null ? depth.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(baliseStart != null ? baliseStart.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(baliseEnd != null ? baliseEnd.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(regex != null ? regex.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(startWith != null ? startWith.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(super.getParameters(resources));
		return parameters;
	}
}
