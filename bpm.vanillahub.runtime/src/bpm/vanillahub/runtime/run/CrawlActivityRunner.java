package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.beans.activities.CrawlActivity;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.utils.CrawlControllerImpl;
import bpm.vanillahub.runtime.utils.CrawlHelper;
import bpm.vanillahub.runtime.utils.CrawlImpl;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.resources.Cible;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class CrawlActivityRunner extends ActivityRunner<CrawlActivity> {

	private List<Cible> targets;
	
	public CrawlActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, CrawlActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<Cible> targets) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.targets = targets;
	}

	@Override
	protected void run(Locale locale) {
		String outputFile = activity.getOutputFile(parameters, variables);

		String url = activity.getUrl(parameters, variables);
		String baliseStart = activity.getBaliseStart(parameters, variables);
		String baliseEnd = activity.getBaliseEnd(parameters, variables);
		
		
		ByteArrayInputStream bis = null;
		try {
			int depth = -1;
			try {
				depth = Integer.parseInt(activity.getDepth(parameters, variables));
			} catch(Exception e) {
				throw new Exception("Bad format for depth. Should be numeric.");
			}
			
			switch (activity.getTypeCrawl()) {
			case EXTRACT:
				bis = crawl(url, baliseStart, baliseEnd, "", locale);

				if (bis != null) {
					result.setFileName(outputFile);
					result.setInputStream(bis);
					
					result.setResult(Result.SUCCESS);
				}
				else if (result.getResult() == null || result.getResult() != Result.ERROR) {
					addError(Labels.getLabel(locale, Labels.CrawlReturnsNoData));

					result.setResult(Result.ERROR);
				}

				clearResources();
				
				return;
			case NUTCH:
				String nutchUrl = activity.getNutchUrl(parameters, variables);
				
				addInfo("Nutch URL " + nutchUrl);
				nutch(nutchUrl, url, depth);
				
				setResult(Result.SUCCESS);

				clearResources();
				
				return;
				
			case CRAWL4J:
				Cible cible = (Cible) activity.getResource(targets);
				String regex = activity.getRegex(parameters, variables);
				String startWith = activity.getStartWith(parameters, variables);
				
				crawl4j(url, cible, regex, startWith, depth, locale);
				
				if (result.getResult() != Result.ERROR) {
					setResult(Result.SUCCESS);
				}

				clearResources();
				
				return;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToCrawl));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			clearResources();
			
			return;
		}
	}

	private ByteArrayInputStream crawl(String url, String baliseStart, String baliseEnd, String regex, Locale locale) throws IOException {
		String extraction = CrawlHelper.extractReference(url, baliseStart, baliseEnd, regex);

		// FOR NOW: To change
		String itemTitle = "price";

		StringBuffer buf = new StringBuffer();
		buf.append("<root>\n");
		buf.append("    <extracts>\n");
		buf.append(buildItem(itemTitle, extraction));
		buf.append("    </extracts>\n");
		buf.append("</root>");

		return new ByteArrayInputStream(buf.toString().getBytes());
	}

	private void nutch(String nutchUrl, String url, int depth) throws Exception {
		CrawlHelper.launchNutchWorkflow(nutchUrl, url, depth);
	}

	private void crawl4j(String url, Cible cible, String regex, String startWith, int depth, Locale locale) throws Exception {
		String crawlStorageFolder = Constants.CRAWL_STORAGE_FOLDER;
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(depth);

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlControllerImpl controller = new CrawlControllerImpl(config, pageFetcher, robotstxtServer, regex, startWith, this, locale, cible, parameters, variables);
        
        controller.addSeed(url);

        controller.start(CrawlImpl.class, numberOfCrawlers);
	}

	private String buildItem(String itemTitle, String extraction) {
		return "        <" + itemTitle + ">" + extraction + "</" + itemTitle + ">\n";
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(null);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

	@Override
	protected void clearResources() {
	}
}
