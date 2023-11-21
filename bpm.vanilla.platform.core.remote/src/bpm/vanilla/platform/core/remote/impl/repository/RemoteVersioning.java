package bpm.vanilla.platform.core.remote.impl.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Revision;
import bpm.vanilla.platform.core.repository.services.IModelVersionningService;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteVersioning implements IModelVersionningService{
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();

	}
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	public RemoteVersioning(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}
	private Object handleError(String responseMessage) throws Exception{
		if (responseMessage.isEmpty()){
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof VanillaException){
			throw (VanillaException)o;
		}
		return o;
	}
	@Override
	public void checkIn(RepositoryItem item, String comment,
			InputStream modelStream) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(modelStream, bos, true, true);
		byte[] raw64 = Base64.encodeBase64(bos.toByteArray());
		
		XmlAction op = new XmlAction(createArguments(item, comment, raw64), 
				IModelVersionningService.ActionType.CHECK_IN);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
		
	}
	@Override
	public InputStream checkOut(RepositoryItem item) throws Exception {
		
		XmlAction op = new XmlAction(createArguments(item), 
				IModelVersionningService.ActionType.CHECK_OUT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		ByteArrayInputStream raw64 = (ByteArrayInputStream)handleError(result);
		
		return raw64;
	}
	@Override
	public InputStream getRevision(RepositoryItem item, int revisionNumber)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(item, revisionNumber), 
				IModelVersionningService.ActionType.REVISION_MODEL);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (InputStream)handleError(result);	
	}
	@Override
	public List<Revision> getRevisions(RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), 
				IModelVersionningService.ActionType.LIST_REVISIONS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List)handleError(result);

	}
	@Override
	public void revertToRevision(RepositoryItem item, int revisionNumber,
			String comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, revisionNumber, comment), 
				IModelVersionningService.ActionType.REVERT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		handleError(result);
		
	}
	@Override
	public void share(RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), 
				IModelVersionningService.ActionType.SHARE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		handleError(result);
	}
	@Override
	public boolean unlock(RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), 
				IModelVersionningService.ActionType.UNLOCK);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (Boolean)handleError(result);
	}
	@Override
	public void updateRevision(Revision revision) throws Exception {
		XmlAction op = new XmlAction(createArguments(revision), 
				IModelVersionningService.ActionType.UPDATE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		handleError(result);
		
	}
}
