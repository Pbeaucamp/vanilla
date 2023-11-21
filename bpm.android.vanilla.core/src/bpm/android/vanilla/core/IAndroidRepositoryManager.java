package bpm.android.vanilla.core;

import java.io.InputStream;
import java.util.List;

import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.core.beans.AndroidCubeView;
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.Parameter;
import bpm.android.vanilla.core.beans.cube.HierarchyAndCol;
import bpm.android.vanilla.core.beans.cube.MeasureAndCol;
import bpm.android.vanilla.core.beans.report.AndroidDocumentDefinition;
import bpm.android.vanilla.core.beans.report.IRunItem;
import bpm.android.vanilla.core.xstream.IXmlActionType;

public interface IAndroidRepositoryManager {
	
	public enum ActionType implements IXmlActionType {
		GET_REPOSITORY_CONTENT,
		LOAD_CUBE_VIEW,
		LOAD_CUBE,
		CHANGE_DIMENSIONS,
		CHANGE_MEASURES,
		SWAP_CUBE,
		INIT_CUBE,
		HIDE_NULL,
		DRILL_CUBE,
		EXECUTE_ITEM_PARAMETER,
		GET_VIEW_ITEM,
		LOAD_DASHBOARD,
		LOAD_ITEM_IN_GED,
		SEARCH_IN_GED,
		RUN_ITEM,
		GENERATE_PACKAGE
	}
	
	public List<AndroidItem> getRepositoryContent() throws Exception;
	
	public AndroidCube loadCubeView(AndroidCube selectedCube, AndroidCubeView androidView) throws Exception;
	
	public AndroidCube loadCube(AndroidCube selectedCube, boolean isIphone) throws Exception;
	
	public AndroidCube changeDimensions(AndroidCube cube, List<HierarchyAndCol> selectedHiers) throws Exception;
	
	public AndroidCube changeMeasures(AndroidCube cube, List<MeasureAndCol> selectedMeas) throws Exception;
	
	public String swapCube() throws Exception;
	
	public AndroidCube initCube(AndroidCube cube) throws Exception;
	
	public String hideNull() throws Exception;
	
	public String drillCube(String drillUniqueName) throws Exception;
	
	public IRunItem getDashboardUrl(AndroidItem item) throws Exception;
	
	public List<Parameter> getParameterValues(AndroidItem item, Parameter selectedParameter) throws Exception;
	
	public IRunItem getItemLastView(AndroidItem item) throws Exception;
	
	public List<AndroidDocumentDefinition> searchInGed(String search) throws Exception;
	
	public String loadItemInGed(AndroidDocumentDefinition definition) throws Exception;
	
	public IRunItem runItem(AndroidItem selectedItem, String outputFormat) throws Exception;
	
	public InputStream generateDiscoPackage(int directoryItemId) throws Exception;
}
