package bpm.android.vanilla.remote;

import java.io.InputStream;
import java.util.List;

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.core.beans.AndroidCubeView;
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.AndroidVanillaContext;
import bpm.android.vanilla.core.beans.Parameter;
import bpm.android.vanilla.core.beans.cube.HierarchyAndCol;
import bpm.android.vanilla.core.beans.cube.MeasureAndCol;
import bpm.android.vanilla.core.beans.report.AndroidDocumentDefinition;
import bpm.android.vanilla.core.beans.report.IRunItem;
import bpm.android.vanilla.core.beans.report.ReportRunItem;
import bpm.android.vanilla.core.xstream.XmlAction;
import bpm.android.vanilla.core.xstream.XmlArgumentsHolder;
import bpm.android.vanilla.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;

public class RemoteAndroidRepository implements IAndroidRepositoryManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteAndroidRepository(AndroidVanillaContext ctx, String sessionId) {
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
	public List<AndroidItem> getRepositoryContent() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAndroidRepositoryManager.ActionType.GET_REPOSITORY_CONTENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AndroidItem>) xstream.fromXML(xml);
	}

	@Override
	public AndroidCube loadCubeView(AndroidCube selectedCube, AndroidCubeView androidView) throws Exception {
		XmlAction op = new XmlAction(createArguments(selectedCube, androidView), IAndroidRepositoryManager.ActionType.LOAD_CUBE_VIEW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidCube) xstream.fromXML(xml);
	}

	@Override
	public AndroidCube loadCube(AndroidCube selectedCube, boolean isIphone) throws Exception {
		XmlAction op = new XmlAction(createArguments(selectedCube, isIphone), IAndroidRepositoryManager.ActionType.LOAD_CUBE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidCube) xstream.fromXML(xml);
	}

	@Override
	public AndroidCube changeDimensions(AndroidCube cube, List<HierarchyAndCol> selectedHiers) throws Exception {
		XmlAction op = new XmlAction(createArguments(cube, selectedHiers), IAndroidRepositoryManager.ActionType.CHANGE_DIMENSIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidCube) xstream.fromXML(xml);
	}

	@Override
	public AndroidCube changeMeasures(AndroidCube cube, List<MeasureAndCol> selectedMeas) throws Exception {
		XmlAction op = new XmlAction(createArguments(cube, selectedMeas), IAndroidRepositoryManager.ActionType.CHANGE_MEASURES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidCube) xstream.fromXML(xml);
	}

	@Override
	public String swapCube() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAndroidRepositoryManager.ActionType.SWAP_CUBE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public AndroidCube initCube(AndroidCube cube) throws Exception {
		XmlAction op = new XmlAction(createArguments(cube), IAndroidRepositoryManager.ActionType.INIT_CUBE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AndroidCube) xstream.fromXML(xml);
	}

	@Override
	public String hideNull() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAndroidRepositoryManager.ActionType.HIDE_NULL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public IRunItem getDashboardUrl(AndroidItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), IAndroidRepositoryManager.ActionType.LOAD_DASHBOARD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IRunItem) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Parameter> getParameterValues(AndroidItem item, Parameter selectedParameter) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, selectedParameter), IAndroidRepositoryManager.ActionType.EXECUTE_ITEM_PARAMETER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Parameter>) xstream.fromXML(xml);
	}

	@Override
	public ReportRunItem getItemLastView(AndroidItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), IAndroidRepositoryManager.ActionType.GET_VIEW_ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ReportRunItem) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AndroidDocumentDefinition> searchInGed(String search) throws Exception {
		XmlAction op = new XmlAction(createArguments(search), IAndroidRepositoryManager.ActionType.SEARCH_IN_GED);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AndroidDocumentDefinition>) xstream.fromXML(xml);
	}

	@Override
	public String loadItemInGed(AndroidDocumentDefinition definition) throws Exception {
		XmlAction op = new XmlAction(createArguments(definition), IAndroidRepositoryManager.ActionType.LOAD_ITEM_IN_GED);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public ReportRunItem runItem(AndroidItem selectedItem, String outputFormat) throws Exception {
		XmlAction op = new XmlAction(createArguments(selectedItem, outputFormat), IAndroidRepositoryManager.ActionType.RUN_ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ReportRunItem) xstream.fromXML(xml);
	}

	@Override
	public String drillCube(String drillUniqueName) throws Exception {
		XmlAction op = new XmlAction(createArguments(drillUniqueName), IAndroidRepositoryManager.ActionType.DRILL_CUBE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public InputStream generateDiscoPackage(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IAndroidRepositoryManager.ActionType.GENERATE_PACKAGE);
		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op));
	}
}
