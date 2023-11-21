package bpm.vanillahub.runtime.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.MailActivity;
import bpm.vanillahub.core.beans.activities.attributes.Email;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.mail.MailConfig;

public class MailActivityRunner extends ActivityRunner<MailActivity> {

	private List<ServerMail> serverMails;
	
	public MailActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, MailActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<ServerMail> serverMails) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.serverMails = serverMails;
	}

	@Override
	public void run(Locale locale) {
		String htmlText = activity.getMessage();
		ServerMail serverMail = (ServerMail) activity.getResource(serverMails);

		List<MailConfig> mailConfigs = new ArrayList<MailConfig>();

		for (User user : activity.getUsers()) {
			if (user.getBusinessMail() != null || !user.getBusinessMail().isEmpty()) {
				addInfo(Labels.getLabel(locale, Labels.AddUser) + " '" + user.getName() + "' " + Labels.getLabel(locale, Labels.ForMailSend) + " '" + user.getBusinessMail() + "'");
				mailConfigs.add(new MailConfig(user.getBusinessMail(), "Vanilla Hub - " + workflowName, htmlText, workflowName, user.getName(), launcherName));
			}
			else {
				addWarning("'" + user.getName() + "' " + Labels.getLabel(locale, Labels.UserIgnored));
			}
		}

		for (Email email : activity.getEmails()) {
			addInfo(Labels.getLabel(locale, Labels.AddMail) + " '" + email + "' " + Labels.getLabel(locale, Labels.ForMailSend));
			mailConfigs.add(new MailConfig(email.getEmail(), "Vanilla Hub - " + workflowName, htmlText, workflowName, email.getEmail(), launcherName));
		}

		addInfo(Labels.getLabel(locale, Labels.EmailSendAtTheEnd));
		result.setSendMailAtTheEnd(true, activity.isJoinLog(), activity.sendOnlyIfError(), serverMail, mailConfigs);
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(serverMails);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(serverMails);
	}

	@Override
	protected void clearResources() { }
}
