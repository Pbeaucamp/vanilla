package bpm.gwt.aklabox.commons.client.workflows;

import bpm.aklabox.workflow.core.IAklaflowConstant;
import bpm.gwt.aklabox.commons.shared.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LogMessage extends Composite {

	private static LogMessageUiBinder uiBinder = GWT.create(LogMessageUiBinder.class);

	interface LogMessageUiBinder extends UiBinder<Widget, LogMessage> {
	}

	@UiField
	FocusPanel logMessage;
	@UiField
	HTMLPanel logError, logWarning, logInfo, logSuccess;

	private Log log;

	public LogMessage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public LogMessage(Log log) {
		initWidget(uiBinder.createAndBindUi(this));
		this.log = log;

		logError.removeFromParent();
		logWarning.removeFromParent();
		logInfo.removeFromParent();
		logSuccess.removeFromParent();

		Label message = new Label();

		if (log.getDate() != null) {
			message = new Label(DateTimeFormat.getMediumDateTimeFormat().format(log.getDate()) + ": " + log.getMessage());
		}
		else {
			message = new Label(log.getMessage());
		}

		message.addStyleName("inline");
		message.getElement().setAttribute("style", "width: 95%;");
		if (log.getType().equals(IAklaflowConstant.WARN)) {
			logWarning.add(message);
			logMessage.add(logWarning);
		}
		else if (log.getType().equals(IAklaflowConstant.ERROR)) {
			logError.add(message);
			logMessage.add(logError);
		}
		else if (log.getType().equals(IAklaflowConstant.INFO)) {
			logInfo.add(message);
			logMessage.add(logInfo);
		}
		else if (log.getType().equals(IAklaflowConstant.SUCCESS)) {
			logSuccess.add(message);
			logMessage.add(logSuccess);
		}
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

}
