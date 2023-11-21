package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.run.IRunner;
import bpm.vanillahub.runtime.run.ResultActivity;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class CrawlImpl extends WebCrawler {

	private IRunner runner;
	private Locale locale;

	private Pattern regex;
	private String startWith;

	private Cible target;
	private List<Parameter> parameters;
	private List<Variable> variables;

	private boolean init() {
		if (getMyController() instanceof CrawlControllerImpl) {
			CrawlControllerImpl crawlController = ((CrawlControllerImpl) getMyController());

			this.regex = crawlController.getRegex();
			this.startWith = crawlController.getStartWith();
			this.target = crawlController.getTarget();
			this.runner = crawlController.getRunner();
			this.locale = crawlController.getLocale();
			this.target = crawlController.getTarget();
			this.parameters = crawlController.getParameters();
			this.variables = crawlController.getVariables();

			return true;
		}

		runner.addError(Labels.getLabel(locale, Labels.CrawlInitFailed));
		runner.setResult(Result.ERROR);

		return false;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		if (regex == null && !init()) {
			return false;
		}

		String href = url.getURL().toLowerCase();
		return !regex.matcher(href).matches() && (startWith == null || startWith.isEmpty() || href.startsWith(startWith));
	}

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		runner.addDebug("Url crawl start for '" + url + "'");
		ResultActivity result = runner.getResult();

		String contentDeck = page.getContentType();

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			String pageName = htmlParseData.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".html";
			runner.addDebug("Number of outgoing links '" + links.size() + "'");

			ByteArrayInputStream htmlStream;
			try {
				htmlStream = new ByteArrayInputStream(html.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				runner.addError(Labels.getLabel(locale, Labels.UnableToHandleUrl) + " '" + url + "'");
				runner.setResult(Result.ERROR);
				return;
			}

			try {
				new CibleHelper().manageStream(runner, locale, -1, target, null, result, pageName, htmlStream, parameters, variables, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			String pageName = "page_" + new Object().hashCode();

			ByteArrayInputStream htmlStream = new ByteArrayInputStream(page.getContentData());
			try {
				new CibleHelper().manageStream(runner, locale, -1, target, null, result, pageName, htmlStream, parameters, variables, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
