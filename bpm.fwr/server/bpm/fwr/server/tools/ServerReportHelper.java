package bpm.fwr.server.tools;

import java.io.InputStream;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;

public class ServerReportHelper {
	private RemoteReportRuntime serverRemote;

	public ServerReportHelper(String url, String login, String password) {
		serverRemote = new RemoteReportRuntime(url, login, password);
	}

	public InputStream runReport(String outputFormat, InputStream reportModel, int groupId, 
			IObjectIdentifier objectId, User user) throws Exception {
		ReportRuntimeConfig config = new ReportRuntimeConfig(objectId, null, groupId);
		config.setOutputFormat(outputFormat);
		
		return serverRemote.runReport(config, reportModel, user, false);
	}

	public InputStream saveAsBirtTask(String outputFormat, InputStream reportModel, int groupId, 
			IObjectIdentifier objectId) throws Exception {
		ReportRuntimeConfig config = new ReportRuntimeConfig(objectId, null, groupId);
		config.setOutputFormat(outputFormat);
		
		return serverRemote.buildRptDesignFromFWR(config, reportModel);
	}

}