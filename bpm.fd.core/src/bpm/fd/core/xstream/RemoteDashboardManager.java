package bpm.fd.core.xstream;

import java.util.List;

import bpm.fd.core.Dashboard;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteDashboardManager implements IDashboardManager {

	private static XStream xstream;

	static {
		xstream = new XStream();
	}

	private HttpCommunicator httpCommunicator;

	public RemoteDashboardManager(IRepositoryApi repositoryConnection) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(repositoryConnection);
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public String previewDashboard(Dashboard dashboard) throws Exception {
		XmlAction op = new XmlAction(createArguments(dashboard), IDashboardManager.ActionType.PREVIEW_DASHBOARD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}

	@Override
	public String getDefaultCssFile() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IDashboardManager.ActionType.GET_DEFAULT_CSS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}

	@Override
	public Integer saveDashboard(RepositoryDirectory target, Dashboard dashboard, boolean update, List<Group> groups) throws Exception {
		XmlAction op = new XmlAction(createArguments(target, dashboard, update, groups), IDashboardManager.ActionType.SAVE_DASHBOARD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer)xstream.fromXML(xml);
	}

	@Override
	public Dashboard openDashboard(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IDashboardManager.ActionType.OPEN_DASHBOARD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Dashboard)xstream.fromXML(xml);
	}
	
	
}
