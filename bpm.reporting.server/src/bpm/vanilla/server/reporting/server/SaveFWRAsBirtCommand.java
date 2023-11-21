package bpm.vanilla.server.reporting.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

import bpm.fwr.api.FwrReportManager;
import bpm.fwr.api.beans.FWRReport;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.server.commons.server.Server;

import com.thoughtworks.xstream.XStream;

public class SaveFWRAsBirtCommand {

	private Server server;
	
	private String reportXML;
	private String login;
	private String password;
	
	private int groupId;
	private int repId;
	
	public SaveFWRAsBirtCommand(Server server, String login, String password, IReportRuntimeConfig reportConfig, InputStream model){
		this.server = server;
		this.login = login;
		this.password = password;
		this.repId = reportConfig.getObjectIdentifier().getRepositoryId();
		this.groupId = reportConfig.getVanillaGroupId();

		try {
			this.reportXML = IOUtils.toString(model, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			this.reportXML = "";
		}
	}
	
	public InputStream buildRPTDesign() throws Exception {
		XStream xstream = new XStream();
		FWRReport model = (FWRReport) xstream.fromXML(reportXML);

		BaseVanillaContext vCtx = new BaseVanillaContext(server.getConfig().getVanillaUrl(), login, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vCtx);
		
		Repository repository = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repId);
		Group group = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
		
		BaseRepositoryContext repCtx = new BaseRepositoryContext(vCtx, group, repository);

		IRepositoryApi sock = new RemoteRepositoryApi(repCtx);

		EngineConfig config = new EngineConfig();
		IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		IReportEngine birtEngine = factory.createReportEngine(config);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			FwrReportManager fwrManager = new FwrReportManager(birtEngine, model);
			InputStream is = fwrManager.buildRPTDesignFromFWR(sock);
			
			IOWriter.write(is, bos, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ByteArrayInputStream(bos.toByteArray());
	}
}
