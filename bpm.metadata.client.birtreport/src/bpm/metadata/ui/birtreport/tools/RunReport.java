package bpm.metadata.ui.birtreport.tools;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import metadataclient.Activator;

import org.apache.commons.io.IOUtils;

import bpm.fwr.api.beans.FWRReport;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;

import com.thoughtworks.xstream.XStream;

public class RunReport {

	private String outputFormat = "html";
	private String html;

	public RunReport(FWRReport report) throws Exception {

		RemoteReportRuntime remote = new RemoteReportRuntime(ConnectionHelper.getInstance().getVanillaRuntimeUrl(), ConnectionHelper.getInstance().getLogin(), ConnectionHelper.getInstance().getPassword());

		XStream xstream = new XStream();
		String modelXml = xstream.toXML(report);
		
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(modelXml.getBytes("UTF-8"));
		} catch (Exception ex) {
			throw new Exception("Error when generating FWR model - " + ex.getMessage(), ex);
		}
		ReportRuntimeConfig conf = new ReportRuntimeConfig();
		conf.setOutputFormat(outputFormat);
		conf.setVanillaGroupId(ConnectionHelper.getInstance().getSock().getContext().getGroup().getId());
		conf.setObjectIdentifier(new ObjectIdentifier(Activator.getDefault().getRepositoryContext().getRepository().getId(), -1));
		
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ConnectionHelper.getInstance().getVanillaRuntimeUrl(), ConnectionHelper.getInstance().getLogin(), ConnectionHelper.getInstance().getPassword());
		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(ConnectionHelper.getInstance().getLogin());
		
		String req = "";
		try {

			InputStream is = remote.runReport(conf, bis, user, false);
			req = IOUtils.toString(is, "UTF-8");
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.html = req;
	}

	public String getHtml() {
		return html;
	}

}
