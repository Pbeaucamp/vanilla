package bpm.vanillahub.runtime.utils;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.runtime.run.IRunner;
import bpm.workflow.commons.resources.Cible;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlControllerImpl extends CrawlController {

	private IRunner runner;
	private Locale locale;
	
	private Pattern regex;
	private String startWith;
	
	private Cible target;
	
	private List<Parameter> parameters;
	private List<Variable> variables;
	
	public CrawlControllerImpl(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer, String regex, String startWith, IRunner runner, Locale locale, Cible target, List<Parameter> parameters, List<Variable> variables) throws Exception {
		super(config, pageFetcher, robotstxtServer);
		this.regex = Pattern.compile(regex);
		this.startWith = startWith;
		this.runner = runner;
		this.locale = locale;
		this.target = target;
		this.parameters = parameters;
		this.variables = variables;
	}
	
	public Pattern getRegex() {
		return regex;
	}
	
	public String getStartWith() {
		return startWith;
	}
	
	public IRunner getRunner() {
		return runner;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public Cible getTarget() {
		return target;
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
}
