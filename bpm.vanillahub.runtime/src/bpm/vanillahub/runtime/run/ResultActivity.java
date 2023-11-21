package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.mail.MailConfig;
import bpm.vanillahub.runtime.run.IResultInformation.TypeResultInformation;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.Result;

public class ResultActivity {

	private WorkflowProgress workflowProgress;

	private String fileName;
	private boolean isVariableFileNameDefined;
	private ByteArrayInputStream inputStream;
	private HashMap<Integer, ActivityLog> logs;

	private ActivityLog currentLog;

	// Options if mail need to be send at the end of the workflow
	private boolean sendMailAtTheEnd;
	private boolean joinLog;
	private boolean sendOnlyIfError;
	private ServerMail serverMail;
	private List<MailConfig> mailConfigs;
	
	private HashMap<TypeResultInformation, IResultInformation> infosComp;
	
	private boolean bigFile;

	public ResultActivity() {
	}

	public ResultActivity(WorkflowProgress workflowProgress, ResultActivity parentResult, String activityName, Integer activityLogIdentifier) {
		this.workflowProgress = workflowProgress;

		if (parentResult != null) {
			this.fileName = parentResult.getFileName();
			this.inputStream = parentResult.getInputStream();
			this.logs = parentResult.getLogs();

			this.sendMailAtTheEnd = parentResult.sendMailAtTheEnd();
			this.joinLog = parentResult.joinLog();
			this.sendOnlyIfError = parentResult.sendOnlyIfError();
			this.serverMail = parentResult.getServerMail();
			this.mailConfigs = parentResult.getMailConfigs();
			
			this.infosComp = parentResult.getInfosComp();
			this.bigFile = parentResult.isBigFile();
		}

		if (logs == null) {
			this.logs = new LinkedHashMap<Integer, ActivityLog>();
		}

		if (logs.get(activityLogIdentifier) != null) {
			this.currentLog = logs.get(activityLogIdentifier);
			this.currentLog.iterateLoop();
		}
		else {
			this.currentLog = new ActivityLog(activityName);
			this.currentLog.setStartDate(new Date());
			setResult(Result.RUNNING);
//			this.currentLog.setResult(Result.RUNNING);
			this.currentLog.iterateLoop();

			if (parentResult != null) {
				this.currentLog.setNumberTotalOfFiles(parentResult.getNumberTotalOfFiles());
			}

			this.logs.put(activityLogIdentifier, currentLog);
		}
	}

	public void setResult(Result result) {
		this.currentLog.setResult(result);
		this.currentLog.setEndDate(result != Result.RUNNING ? new Date() : null);

		updateProgress();
	}

	private void updateProgress() {
		if (workflowProgress != null) {
			workflowProgress.updateProgress(this);
		}
	}

	public void setInputStream(ByteArrayInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public ByteArrayInputStream getInputStream() {
		if (inputStream != null) {
			inputStream.reset();
		}
		return inputStream;
	}

	public void addInfo(String info) {
		this.currentLog.addInfo(info);
	}

	public void addWarning(String warning) {
		this.currentLog.addWarning(warning);
	}

	public void addDebug(String debug) {
		this.currentLog.addDebug(debug);
	}

	public void addError(String error) {
		this.currentLog.addError(error);
	}

	public void setNumberTotalOfFiles(int filesNumber) {
		this.currentLog.setNumberTotalOfFiles(filesNumber);
	}

	public int getNumberTotalOfFiles() {
		return this.currentLog.getNumberTotalOfFiles();
	}

	public void iterateNumberOfFileIgnored() {
		this.currentLog.iterateNumberOfFileIgnored();
	}

	public int getNumberOfFileIgnored() {
		return currentLog.getNumberOfFileIgnored();
	}

	public void iterateNumberOfFileTraited(int batch) {
		this.currentLog.iterateNumberOfFileTraited(batch);
	}

	public int getNumberOfFileTraited() {
		return currentLog.getNumberOfFileTraited();
	}

	public void iterateLoop() {
		this.currentLog.iterateLoop();
	}

	public HashMap<Integer, ActivityLog> getLogs() {
		return logs;
	}

	public Result getResult() {
		return currentLog != null ? currentLog.getResult() : null;
	}

	public InputStream getLogsAsInputStream() {
		StringBuffer buf = new StringBuffer();
		if (logs != null) {
			for (ActivityLog log : logs.values()) {
				buf.append(log.toString(Level.ALL));
			}
		}

		return IOUtils.toInputStream(buf.toString());
	}

	public void setSendMailAtTheEnd(boolean sendMailAtTheEnd, boolean joinLog, boolean sendOnlyIfError, ServerMail serverMail, List<MailConfig> mailConfigs) {
		this.sendMailAtTheEnd = sendMailAtTheEnd;
		this.joinLog = joinLog;
		this.sendOnlyIfError = sendOnlyIfError;
		this.serverMail = serverMail;
		this.mailConfigs = mailConfigs;
	}

	public boolean sendMailAtTheEnd() {
		return sendMailAtTheEnd;
	}

	public boolean joinLog() {
		return joinLog;
	}
	
	public boolean sendOnlyIfError() {
		return sendOnlyIfError;
	}

	public ServerMail getServerMail() {
		return serverMail;
	}

	public List<MailConfig> getMailConfigs() {
		return mailConfigs;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public boolean isVariableFileNameDefined() {
		return isVariableFileNameDefined;
	}
	
	public void setVariableFileNameDefined(boolean isVariableFileNameDefined) {
		this.isVariableFileNameDefined = isVariableFileNameDefined;
	}
	
	public HashMap<TypeResultInformation, IResultInformation> getInfosComp() {
		if (infosComp == null) {
			this.infosComp = new HashMap<TypeResultInformation, IResultInformation>();
		}
		return infosComp;
	}
	
	public void putInfoComp(TypeResultInformation type, IResultInformation infoComp) {
		if (infosComp == null) {
			this.infosComp = new HashMap<TypeResultInformation, IResultInformation>();
		}
		this.infosComp.put(type, infoComp);
	}
	
	public boolean isBigFile() {
		return bigFile;
	}

	public void setBigFile(boolean bigFile) {
		this.bigFile = bigFile;
	}
}
