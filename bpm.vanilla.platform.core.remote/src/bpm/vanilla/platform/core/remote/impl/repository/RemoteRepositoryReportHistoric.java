package bpm.vanilla.platform.core.remote.impl.repository;

import java.util.List;

import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteRepositoryReportHistoric implements IRepositoryReportHistoricService {
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

	public RemoteRepositoryReportHistoric(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
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

	@Override
	public void createHistoricAccess(int directoryItemId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId, groupId), IRepositoryReportHistoricService.ActionType.CREATE_ACCESS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void deleteHistoricEntry(int gedDocumentEntryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(gedDocumentEntryId), IRepositoryReportHistoricService.ActionType.DEL_HISTO_ENTRY);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public List<Integer> getAuthorizedGroupId(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryReportHistoricService.ActionType.LIST_GRANTED_GROUPS_ID);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) handleError(result);
	}

	@Override
	public List<Integer> getHistorizedDocumentIdFor(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryReportHistoricService.ActionType.LIST_DOCS_4_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) handleError(result);
	}

	@Override
	public List<Integer> getHistorizedDocumentIdFor(int directoryItemId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId, groupId), IRepositoryReportHistoricService.ActionType.LIST_DOCS_4_ITEM_GROUP);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) handleError(result);
	}

	@Override
	public List<Integer> getHistorizedDocumentIdForGroup(Integer groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId), IRepositoryReportHistoricService.ActionType.LIST_DOCS_4_GROUP);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) handleError(result);
	}

	@Override
	public List<Integer> removeHistoricAccess(int directoryItemId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId, groupId), IRepositoryReportHistoricService.ActionType.REMOVE_ACCESS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) handleError(result);
	}

	@Override
	public void addOrUpdateReportBackground(ReportBackground report) throws Exception {
		XmlAction op = new XmlAction(createArguments(report), IRepositoryReportHistoricService.ActionType.ADD_OR_UPDATE_REPORT_BACKGROUND);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteReportBackground(ReportBackground report) throws Exception {
		XmlAction op = new XmlAction(createArguments(report), IRepositoryReportHistoricService.ActionType.DEL_REPORT_BACKGROUND);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ReportBackground> getReportBackgrounds(int userId, int limit) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, limit), IRepositoryReportHistoricService.ActionType.GET_REPORT_BACKGROUNDS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ReportBackground>) handleError(result);
	}

	@Override
	public ReportBackground getReportBackground(int reportBackgroundId) throws Exception {
		XmlAction op = new XmlAction(createArguments(reportBackgroundId), IRepositoryReportHistoricService.ActionType.GET_REPORT_BACKGROUND);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ReportBackground) handleError(result);
	}
}
