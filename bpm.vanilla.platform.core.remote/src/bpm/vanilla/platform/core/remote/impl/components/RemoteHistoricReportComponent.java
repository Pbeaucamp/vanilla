package bpm.vanilla.platform.core.remote.impl.components;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteHistoricReportComponent implements ReportHistoricComponent {
	
	private static XStream xstream;
	
	static {
		xstream = new XStream();
	}
	
	private HttpCommunicator httpCommunicator;
	
	public RemoteHistoricReportComponent(String vanillaUrl, String login, String password) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(vanillaUrl, login, password);
	}
	
	public RemoteHistoricReportComponent(IVanillaContext ctx) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}
	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public List<GedDocument> getReportHistoric(IObjectIdentifier identifier, int groupId)throws Exception {
		XmlAction op = new XmlAction(createArguments(identifier,groupId), ReportHistoricComponent.ActionType.List);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
		if(xml == null || xml.isEmpty()){
			return null;
		}
		else {
			return (List<GedDocument>)xstream.fromXML(xml);
		}
	}

	@Override
	public GedDocument historize(HistorizationConfig conf,	InputStream datas) throws Exception {
		
		/*
		 * encode the datas in base 64
		 * TODO: do not know why i used InputStream instead of dataHandler
		 */
		byte[] datas64 = null;
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOWriter.write(datas, bos, true, true);
			
			datas64 = Base64.encodeBase64(bos.toByteArray());
		}catch(Exception ex){
			throw new Exception("Could not encode the provided datas stream " + ex.getMessage(), ex);
		}
		
		
		XmlAction op = new XmlAction(createArguments(conf, datas64), ReportHistoricComponent.ActionType.MassHistorize);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		GedDocument res = (GedDocument)xstream.fromXML(xml);
		return res;

	}

	@Override
	public InputStream loadHistorizedDocument(Integer gedHistoricId) throws Exception {
		XmlAction op = new XmlAction(createArguments(gedHistoricId), ReportHistoricComponent.ActionType.Load);
		return httpCommunicator.executeActionAsStream(VanillaConstants.VANILLA_HISTORIC_REPORT_SERVLET, xstream.toXML(op));
	}
	
	@Override
	public InputStream loadHistorizedDocument(DocumentVersion gedHistoric) throws Exception {
		return loadHistorizedDocument(gedHistoric.getId());
	}

	@Override
	public void deleteHistoricEntry(List<GedDocument> entries, int repId) throws Exception {
		XmlAction op = new XmlAction(createArguments(entries,repId), ReportHistoricComponent.ActionType.Delete);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
		
	}

	@Override
	public List<Integer> getGroupsAuthorizedByItemId(IObjectIdentifier identifier) throws Exception {		
		XmlAction op = new XmlAction(createArguments(identifier), ReportHistoricComponent.ActionType.Get_Access);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) xstream.fromXML(xml);
	}

	@Override
	public void grantHistoricAccess(int groupId, int directoryItem, int repositoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, directoryItem, repositoryId), ReportHistoricComponent.ActionType.Grant_Access);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);	
	}

	@Override
	public void removeHistoricAccess(int groupId, int directoryItem, int repositoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, directoryItem, repositoryId), ReportHistoricComponent.ActionType.Remove_Access);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);	
	}

	@Override
	public Integer historizeReport(HistoricRuntimeConfiguration conf, InputStream is) throws Exception {
		byte[] datas64 = null;
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOWriter.write(is, bos, true, true);
			
			datas64 = Base64.encodeBase64(bos.toByteArray());
		}catch(Exception ex){
			throw new Exception("Could not encode the provided datas stream " + ex.getMessage(), ex);
		}
		
		XmlAction op = new XmlAction(createArguments(conf, datas64), ReportHistoricComponent.ActionType.Historize);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void removeDocumentVersion(DocumentVersion version) throws Exception {
		XmlAction op = new XmlAction(createArguments(version), ReportHistoricComponent.ActionType.REMOVE_DOCUMENT_VERSION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);	
	}
}
