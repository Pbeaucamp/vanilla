package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.beans.activities.SocialNetworkActivity;
import bpm.vanillahub.core.beans.activities.SocialNetworkActivity.SocialNetworkType;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.utils.FacebookManager;
import bpm.vanillahub.runtime.utils.TwitterManager;
import bpm.vanillahub.runtime.utils.YoutubeManager;
import bpm.workflow.commons.beans.Result;

public class SocialNetworkRunner extends ActivityRunner<SocialNetworkActivity> {

	private List<SocialNetworkServer> socialServers;

	public SocialNetworkRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, SocialNetworkActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<SocialNetworkServer> socialServers) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.socialServers = socialServers;
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(socialServers);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(socialServers);
	}

	@Override
	protected void clearResources() {
	}

	@Override
	protected void run(Locale locale) {
		String outputFile = activity.getOutputFile(parameters, variables);
		SocialNetworkType type = activity.getSocialNetworkType();

		int method = activity.getMethod();
		String query = activity.getQuery(parameters, variables);

		ByteArrayInputStream bis = null;
		try {
			switch (type) {
			case FACEBOOK:
				SocialNetworkServer server = (SocialNetworkServer) activity.getResource(socialServers);
				bis = facebook(locale, server, method, query);
				break;
			case TWITTER:
				bis = twitter(method, query);
				break;
			case YOUTUBE:
				server = (SocialNetworkServer) activity.getResource(socialServers);
				String topic = activity.getTopic();
				
				bis = youtube(locale, server, method, topic, query);
				break;

			default:
				break;
			}

			if (bis != null) {
				result.setFileName(outputFile);
				result.setInputStream(bis);

				result.setResult(Result.SUCCESS);
			}
			else if (result.getResult() == null || result.getResult() != Result.ERROR) {
				addError(Labels.getLabel(locale, Labels.SocialNetworkReturnsNoData));

				result.setResult(Result.ERROR);
			}

			clearResources();
		} catch (Exception e) {
			e.printStackTrace();

			addError(Labels.getLabel(locale, Labels.UnableToSatisfyTheRequest));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			clearResources();

			return;
		}
	}

	private ByteArrayInputStream facebook(Locale locale, SocialNetworkServer server, int method, String query) throws Exception {
		String token = server.getToken(parameters, variables);
		if (token == null || token.isEmpty()) {
			throw new Exception(Labels.getLabel(locale, Labels.TokenIsNeeded));
		}
		
		FacebookManager manager = new FacebookManager(token);
		return manager.doAction(method, query);
	}

	private ByteArrayInputStream twitter(int method, String query) throws Exception {
		TwitterManager manager = TwitterManager.getInstance();
		return manager.doAction(method, query);
	}

	private ByteArrayInputStream youtube(Locale locale, SocialNetworkServer server, int method, String topic, String query) throws Exception {
		String token = server.getToken(parameters, variables);
		if (token == null || token.isEmpty()) {
			throw new Exception(Labels.getLabel(locale, Labels.TokenIsNeeded));
		}
		
		YoutubeManager manager = new YoutubeManager();
		return manager.doAction(token, method, topic, query, Constants.YOUTUBE_DEFAULT_NB_VIDEOS);
	}

}
