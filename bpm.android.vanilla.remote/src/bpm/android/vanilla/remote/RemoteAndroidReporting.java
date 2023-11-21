package bpm.android.vanilla.remote;

import java.util.List;

import bpm.android.vanilla.core.IAndroidReportingManager;
import bpm.android.vanilla.core.beans.AndroidRepository;
import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.core.beans.metadata.AndroidMetadata;
import bpm.android.vanilla.core.xstream.XmlAction;
import bpm.android.vanilla.core.xstream.XmlArgumentsHolder;
import bpm.android.vanilla.remote.internal.HttpCommunicator;
import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.dataset.FwrPrompt;

import com.thoughtworks.xstream.XStream;

public class RemoteAndroidReporting implements IAndroidReportingManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteAndroidReporting(AndroidVanillaContext ctx, String sessionId) {
		httpCommunicator.init(ctx.getVanillaRuntimeUrl(), ctx.getLogin(), ctx.getPassword(), sessionId);
	}
	
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<AndroidMetadata> getAllMetadata() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAndroidReportingManager.ActionType.GET_ALL_METADATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AndroidMetadata>)xstream.fromXML(xml);
	}

	@Override
	public AndroidMetadata loadMetadata(int metadataId) throws Exception {
		XmlAction op = new XmlAction(createArguments(metadataId), IAndroidReportingManager.ActionType.LOAD_METADATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidMetadata)xstream.fromXML(xml);
	}

	@Override
	public FWRReport loadReport(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IAndroidReportingManager.ActionType.LOAD_REPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FWRReport)xstream.fromXML(xml);
	}

	@Override
	public String previewReport(FWRReport report) throws Exception {
		XmlAction op = new XmlAction(createArguments(report), IAndroidReportingManager.ActionType.PREVIEW_REPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}

	@Override
	public void saveReport(FWRReport report) throws Exception {
		XmlAction op = new XmlAction(createArguments(report), IAndroidReportingManager.ActionType.SAVE_REPORT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public AndroidRepository getRepositoryContent(AndroidRepository repository) throws Exception {
		XmlAction op = new XmlAction(createArguments(repository), IAndroidReportingManager.ActionType.LOAD_REPOSITORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidRepository)xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FwrPrompt> getPromptsResponse(List<FwrPrompt> prompts) throws Exception {
		XmlAction op = new XmlAction(createArguments(prompts), IAndroidReportingManager.ActionType.GET_PROMPTS_RESPONSE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FwrPrompt>)xstream.fromXML(xml);
	}
}
