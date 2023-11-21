package bpm.vanilla.platform.core.remote.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IExternalAccessibilityManager;
import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.PublicUrl.TypeURL;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteExternalAccessibilityManager implements IExternalAccessibilityManager {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static{
		xstream = new XStream();

	}
	public RemoteExternalAccessibilityManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public int addFmdtUrl(FmdtUrl fmdtUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(fmdtUrl), IExternalAccessibilityManager.ActionType.ADD_FMDT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public int addPublicParameter(PublicParameter param) throws Exception {
		XmlAction op = new XmlAction(createArguments(param), IExternalAccessibilityManager.ActionType.ADD_PARAM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public void deleteFmdtUrl(FmdtUrl fmdtUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(fmdtUrl), IExternalAccessibilityManager.ActionType.DEL_FMDT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);


	}

	@Override
	public void deletePublicParameterForPublicUrlId(int publicUrlId)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(publicUrlId), IExternalAccessibilityManager.ActionType.CLEAR_URL_PARAM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);


	}

	@Override
	public void deletePublicUrl(int publicUrlId) throws Exception {
		XmlAction op = new XmlAction(createArguments(publicUrlId), IExternalAccessibilityManager.ActionType.DEL_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);


	}

	@Override
	public FmdtUrl getFmdtUrlByName(String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(name), IExternalAccessibilityManager.ActionType.FIND_FMDT_BY_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (FmdtUrl)xstream.fromXML(xml);
	}

	@Override
	public List<FmdtUrl> getFmdtUrlForItemId(int directoryItemId)throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IExternalAccessibilityManager.ActionType.FIND_FMDT_4ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

//	@Override
//	public List<Group> getGrantedGroupFor(int repositoryId, int directoryItemId)throws Exception {
//		XmlAction op = new XmlAction(createArguments(repositoryId,directoryItemId), IExternalAccessibilityManager.ActionType.GET_GRANTED_GROUPS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List)xstream.fromXML(xml);
//	}

	@Override
	public List<PublicParameter> getParametersForPublicUrl(int publicUrlId)	throws Exception {
		XmlAction op = new XmlAction(createArguments(publicUrlId), IExternalAccessibilityManager.ActionType.FIND_PARAM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public PublicUrl getPublicUrlsByPublicKey(String key) throws Exception {
		XmlAction op = new XmlAction(createArguments(key), IExternalAccessibilityManager.ActionType.FIND_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (PublicUrl)xstream.fromXML(xml);
	}

//	@Override
//	public int grantAccessToAdressable(int groupId, int adressableObjectId)throws Exception {
//		XmlAction op = new XmlAction(createArguments(groupId,adressableObjectId), IExternalAccessibilityManager.ActionType.GRANT_ACCESS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (Integer)xstream.fromXML(xml);
//	}
//
//	@Override
//	public void removeAccessForGroupId(int groupId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(groupId), IExternalAccessibilityManager.ActionType.REMOVE_GROUP_ACCESS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//
//	}

	@Override
	public int savePublicUrl(PublicUrl publicUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(publicUrl), IExternalAccessibilityManager.ActionType.SAVE_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public void updateFmdtUrl(FmdtUrl fmdtUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(fmdtUrl), IExternalAccessibilityManager.ActionType.UPDATE_FMDT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public PublicUrl getPublicUrlById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IExternalAccessibilityManager.ActionType.GET_URL_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (PublicUrl)xstream.fromXML(xml);
	}

	@Override
	public List<PublicUrl> getPublicUrlsByItemIdRepositoryId(int itemId, int repId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, repId), IExternalAccessibilityManager.ActionType.LIST_URL_4ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List)xstream.fromXML(xml);
	}

	@Override
	public Double[][] launchRFonctions(List<List<Double>> varX, List<List<Double>> varY, String function, int nbcluster) throws Exception {
		XmlAction op = new XmlAction(createArguments(varX, varY, function, nbcluster), IExternalAccessibilityManager.ActionType.CALL_CLASSIFICATION_KMEAN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Double[][])xstream.fromXML(xml);
	}
	
	@Override
	public  String launchRDecisionTree(List<List<Double>> varX, List<String> varY, Double train, List<String> names) throws Exception {
		XmlAction op = new XmlAction(createArguments(varX, varY, train, names), IExternalAccessibilityManager.ActionType.CALL_DECISIONTREE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String)xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PublicUrl> getPublicUrls(int itemId, int repId, TypeURL typeUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, repId, typeUrl), IExternalAccessibilityManager.ActionType.GET_PUBLIC_URLS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return xml != null && !xml.isEmpty() ? (List)xstream.fromXML(xml) : new ArrayList<>();
	}

}
