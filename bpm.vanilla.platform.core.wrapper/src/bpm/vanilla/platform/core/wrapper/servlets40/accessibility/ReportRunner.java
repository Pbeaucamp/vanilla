package bpm.vanilla.platform.core.wrapper.servlets40.accessibility;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.constant.Formats;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameterCreator;
import bpm.vanilla.platform.core.components.IVanillaComponentIdentifier;
import bpm.vanilla.platform.core.components.VanillaComponentType;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.utils.IOWriter;

public class ReportRunner implements IPublicUrlRunner {

	private IVanillaComponentProvider component;
	private User user;

	public ReportRunner(IVanillaComponentProvider component, User user) {
		this.component = component;
		this.user = user;
	}

	@Override
	public String runObject(HttpServletResponse resp, PublicUrl publicUrl, IObjectIdentifier identifier, HashMap<String, String> parameters) throws Exception {

		// create RunConfig
		List<VanillaGroupParameter> vanillaParameters = VanillaParameterCreator.createVanillaGroupParameters(parameters);

		String format = publicUrl.getOutputFormat();
		
		ReportRuntimeConfig conf = new ReportRuntimeConfig(identifier, vanillaParameters, publicUrl.getGroupId());
		conf.setOutputFormat(format);
		InputStream stream = null;
		try {
			List<IVanillaComponentIdentifier> ids = component.getVanillaListenerComponent().getRegisteredComponents(VanillaComponentType.COMPONENT_REPORTING, false);

			if (ids.isEmpty()) {
				throw new VanillaException("There is no Reporting Component currently available within the VanillaPlatform");
			}

			RemoteReportRuntime remote = new RemoteReportRuntime(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), user.getLogin(), user.getPassword());

			try {
				Logger.getLogger(getClass()).info("Launching report ....");
				stream = remote.runReport(conf, user);
				Logger.getLogger(getClass()).info("Report successfully executed");
			} catch (Exception ex) {
				Logger.getLogger(getClass()).error("Failed to execute Report - " + ex.getMessage(), ex);
				throw new Exception("Failed to execute Report - " + ex.getMessage(), ex);
			}
			
			if (format != null) {
				String mime = "";
				for (Formats f : Formats.values()) {
					if (f.getExtension().equals(format)) {
						mime = f.getMime();
						break;
					}
				}
				if (!mime.equals("")) {
					resp.setContentType(mime);
				}
			}

			resp.setHeader("Content-disposition", "filename=" + new Object().hashCode() + "." + format);
			
			IOWriter.write(stream, resp.getOutputStream(), true, true);
			
			return "";

		} catch (Exception e) {
			Logger.getLogger(getClass()).error(e.getMessage(), e);
			throw e;
		}
	}

}
