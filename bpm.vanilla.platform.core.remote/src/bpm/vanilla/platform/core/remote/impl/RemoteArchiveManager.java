package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import bpm.vanilla.platform.core.IArchiveManager;
import bpm.vanilla.platform.core.beans.ArchiveType;
import bpm.vanilla.platform.core.beans.ArchiveTypeItem;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteArchiveManager implements IArchiveManager {
	
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;

	public RemoteArchiveManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	static {
		xstream = new XStream();
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public ArchiveType addArchiveType(ArchiveType type) throws Exception {
		XmlAction action = new XmlAction(createArguments(type), IArchiveManager.ActionType.ADD_ARCHIVE_TYPE);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		return (ArchiveType) xstream.fromXML(xml);
	}

	@Override
	public void deleteArchiveType(ArchiveType type) throws Exception {
		XmlAction action = new XmlAction(createArguments(type), IArchiveManager.ActionType.DELETE_ARCHIVE_TYPE);
		httpCommunicator.executeAction(action, xstream.toXML(action), false);
	}

	@Override
	public ArchiveType updateArchiveType(ArchiveType type) throws Exception {
		XmlAction action = new XmlAction(createArguments(type), IArchiveManager.ActionType.UPDATE_ARCHIVE_TYPE);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		return (ArchiveType) xstream.fromXML(xml);
	}

	@Override
	public List<ArchiveType> getArchiveTypes() throws Exception {
		XmlAction action = new XmlAction(createArguments(), IArchiveManager.ActionType.GET_ARCHIVE_TYPES);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		return (List<ArchiveType>) xstream.fromXML(xml);
	}

	@Override
	public ArchiveTypeItem linkItemArchiveType(int archiveTypeId, int itemId, int repositoryId, boolean isDirectory) throws Exception {
		XmlAction action = new XmlAction(createArguments(archiveTypeId, itemId, repositoryId, isDirectory), IArchiveManager.ActionType.LINK_ITEM_ARCHIVE_TYPE);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		return (ArchiveTypeItem) xstream.fromXML(xml);
	}

	@Override
	public ArchiveTypeItem getArchiveTypeByItem(int itemId, int repositoryId, boolean isDirectory) throws Exception {
		XmlAction action = new XmlAction(createArguments(itemId, repositoryId, isDirectory), IArchiveManager.ActionType.GET_LINK_BY_ITEM);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		if(xml == null || xml.isEmpty()) {
			return null;
		}
		return (ArchiveTypeItem) xstream.fromXML(xml);
	}

	@Override
	public List<ArchiveTypeItem> getArchiveTypeByArchive(int archiveTypeId) throws Exception {
		XmlAction action = new XmlAction(createArguments(archiveTypeId), IArchiveManager.ActionType.GET_LINK_BY_ARCHIVE);
		String xml = httpCommunicator.executeAction(action, xstream.toXML(action), false);
		return (List<ArchiveTypeItem>) xstream.fromXML(xml);
	}
}
