package bpm.document.management.manager.remote;

import java.util.List;

import bpm.document.management.manager.core.IVdmAdminManager;
import bpm.document.management.manager.core.model.AklaBoxInstance;
import bpm.document.management.manager.core.model.AklaBoxLicense;
import bpm.document.management.manager.core.model.AklaBoxNotifications;
import bpm.document.management.manager.core.model.VdmAdminContext;
import bpm.document.management.manager.core.xstream.XmlAction;
import bpm.document.management.manager.core.xstream.XmlArgumentsHolder;
import bpm.document.management.manager.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;

public class RemoteVdmAdminManager implements IVdmAdminManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteVdmAdminManager(VdmAdminContext ctx) {
		httpCommunicator.init(ctx.getVdmUrl(), ctx.getMail(), ctx.getPassword(), null);
	}
	
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public void saveAklaBoxInstance(AklaBoxInstance instance) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IVdmAdminManager.ActionType.SAVE_AKLABOX_INSTANCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteAklaBoxInstance(AklaBoxInstance instance)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IVdmAdminManager.ActionType.DELETE_AKLABOX_INSTANCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateAklaBoxInstance(AklaBoxInstance instance)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IVdmAdminManager.ActionType.UPDATE_AKLABOX_INSTANCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<AklaBoxInstance> getAllAklaBoxInstance() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmAdminManager.ActionType.GET_ALL_AKLABOX_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AklaBoxInstance>)xstream.fromXML(xml);
	}

	@Override
	public AklaBoxLicense saveAklaBoxLicense(AklaBoxLicense license) throws Exception {
		XmlAction op = new XmlAction(createArguments(license), IVdmAdminManager.ActionType.SAVE_AKLABOX_LICENSE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AklaBoxLicense)xstream.fromXML(xml);
	}

	@Override
	public void updateAklaBoxLicense(AklaBoxLicense license) throws Exception {
		XmlAction op = new XmlAction(createArguments(license), IVdmAdminManager.ActionType.UPDATE_AKLABOX_LICENSE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteAklaBoxLicense(AklaBoxLicense license) throws Exception {
		XmlAction op = new XmlAction(createArguments(license), IVdmAdminManager.ActionType.DELETE_AKLABOX_LICENSE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public AklaBoxLicense getAklaBoxLicense(AklaBoxInstance instance)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IVdmAdminManager.ActionType.GET_AKLABOX_LICENSE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AklaBoxLicense)xstream.fromXML(xml);
	}

	@Override
	public void saveNotification(AklaBoxNotifications noti) throws Exception {
		XmlAction op = new XmlAction(createArguments(noti), IVdmAdminManager.ActionType.SAVE_AKLABOX_NOTIFICATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateNotification(AklaBoxNotifications noti) throws Exception {
		XmlAction op = new XmlAction(createArguments(noti), IVdmAdminManager.ActionType.UPDATE_AKLABOX_NOTIFICATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteNotification(AklaBoxNotifications noti) throws Exception {
		XmlAction op = new XmlAction(createArguments(noti), IVdmAdminManager.ActionType.DELETE_AKLABOX_NOTIFICATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<AklaBoxNotifications> getAllNotifications() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmAdminManager.ActionType.GET_ALL_AKLABOX_NOTIFICATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AklaBoxNotifications>)xstream.fromXML(xml);
	}
}