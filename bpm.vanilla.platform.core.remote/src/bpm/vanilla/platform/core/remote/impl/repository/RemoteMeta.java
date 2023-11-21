package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.List;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaForm;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.services.IMetaService;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RemoteMeta implements IMetaService {

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	public RemoteMeta(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Meta> getMetaByForm(int formId) throws Exception {
		XmlAction op = new XmlAction(createArguments(formId), IMetaService.ActionType.GET_META_BY_FORM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Meta>) handleError(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<MetaLink> getMetaLinks(int itemId, TypeMetaLink type, boolean loadResponse) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, type, loadResponse), IMetaService.ActionType.GET_META_LINKS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MetaLink>) handleError(result);
	}

	@Override
	public void manageMetaValues(List<MetaLink> values, ManageAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(values, action), IMetaService.ActionType.MANAGE_VALUES);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getItemsByMeta(TypeMetaLink type, List<MetaValue> values) throws Exception {
		XmlAction op = new XmlAction(createArguments(type, values), IMetaService.ActionType.GET_ITEMS_BY_META);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) handleError(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<MetaForm> getMetaForms() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IMetaService.ActionType.GET_FORMS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<MetaForm>) handleError(result);
	}

	@Override
	public Meta manageMeta(Meta meta, ManageAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(meta, action), IMetaService.ActionType.MANAGE_META);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Meta) handleError(result);
	}

	@Override
	public Meta getMeta(String key) throws Exception {
		XmlAction op = new XmlAction(createArguments(key), IMetaService.ActionType.GET_META_BY_KEY);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Meta) handleError(result);
	}
}
