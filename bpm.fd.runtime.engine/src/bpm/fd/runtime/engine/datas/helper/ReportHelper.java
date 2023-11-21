package bpm.fd.runtime.engine.datas.helper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;

public class ReportHelper {

	public static String generateHTML(User user, int groupId, int repositoryId, int itemId, HashMap<String, String> params) throws Exception {

		RemoteReportRuntime remote = new RemoteReportRuntime(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), user.getLogin(), user.getPassword());

		ReportRuntimeConfig conf = new ReportRuntimeConfig();
		conf.setObjectIdentifier(new ObjectIdentifier(repositoryId, itemId));
		conf.setOutputFormat("html");
		conf.setVanillaGroupId(groupId);

		VanillaGroupParameter dumy = new VanillaGroupParameter();
		for (String s : params.keySet()) {
			VanillaParameter p = new VanillaParameter();
			p.setName(s);
			p.addValue(params.get(s), params.get(s));
			p.setSelectedValues(new ArrayList<String>(p.getValues().keySet()));
			dumy.addParameter(p);
		}

		conf.setParameters(new ArrayList<VanillaGroupParameter>());
		conf.getParametersValues().add(dumy);

		InputStream is = null;

		try {
			is = remote.runReport(conf, user);
		} catch (Exception ex) {
			throw new Exception("Error when running report - " + ex.getMessage(), ex);
		}

		ByteArrayOutputStream output1 = new ByteArrayOutputStream();

		// copy input to output1
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = is.read(buf)) >= 0) {
			output1.write(buf, 0, sz);
		}
		return new String(output1.toByteArray());

	}
}
