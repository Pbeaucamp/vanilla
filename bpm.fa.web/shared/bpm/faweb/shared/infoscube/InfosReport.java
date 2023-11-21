package bpm.faweb.shared.infoscube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.faweb.client.projection.Projection;
import bpm.faweb.shared.DrillDTO;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.ParameterDTO;
import bpm.faweb.shared.SortElement;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfosReport implements IsSerializable {

	private GridCube grid;
	private GridValue values;
	private String reportName = null;
	private List<String> measuresDisplay;
	private int jFirst;
	private int iFirst;
	private boolean inCol = true;
	private List<ItemDim> dims;
	private List<ItemMesGroup> measuresGroup = new ArrayList<ItemMesGroup>();
	private boolean dimchange = false;
	private List<ItemOlapMember> newmembers;
	private String membrerdrill;
	private String cubeName;
	private ChartInfos chartInfos;
	private MapOptions mapOptions;
	private String reportTitle;
	private List<ParameterDTO> parameters = new ArrayList<ParameterDTO>();
	private List<String> wheres = new ArrayList<String>();

	private boolean isProjection = false;
	private Projection actualProjection;
	private boolean isProjectionAllowed;

	private List<String> failedUnames;

	private List<DrillDTO> drills;
	
	private List<SortElement> sortElements = new ArrayList<SortElement>();
	
	private boolean isView;
	
	private HashMap<Integer, Integer> customSizes = new HashMap<Integer, Integer>();
	private boolean sizeLoaded;
	private int fasdId;
	
	public InfosReport() {
		
	}

	public InfosReport(int id) {
		this.fasdId = id;
	}

	public int getFasdId() {
		return fasdId;
	}

	public void setFasdId(int fasdId) {
		this.fasdId = fasdId;
	}

	public boolean isSizeLoaded() {
		return sizeLoaded;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String n) {
		this.reportName = n;
	}

	public GridCube getGrid() {
		return grid;
	}

	public void setGrid(GridCube grid) {
		this.grid = grid;
	}

	public List<String> getMeasuresDisplay() {
		return measuresDisplay;
	}

	public void setMeasuresDisplay(List<String> measuresDisplay) {
		this.measuresDisplay = measuresDisplay;
	}

	public void addMeasureDisplay(String measureDisplay) {
		this.measuresDisplay.add(measureDisplay);
	}

	public int getJFirst() {
		return jFirst;
	}

	public void setJFirst(int first) {
		jFirst = first;
	}

	public int getIFirst() {
		return iFirst;
	}

	public void setIFirst(int first) {
		iFirst = first;
	}

	public boolean isInCol() {
		return inCol;
	}

	public void setInCol(boolean inCol) {
		this.inCol = inCol;
	}

	public List<ItemDim> getDims() {
		return dims;
	}

	public void setDims(List<ItemDim> dims) {
		this.dims = dims;
	}

	public List<ItemMesGroup> getMeasuresGroup() {
		return measuresGroup;
	}

	public void setMeasuresGroup(List<ItemMesGroup> measuresGroup) {
		this.measuresGroup = measuresGroup;
	}

	public List<ItemMes> getMeasures() {
		List<ItemMes> meas = new ArrayList<ItemMes>();
		for (ItemMesGroup meaGr : measuresGroup) {
			meas.addAll(meaGr.getChilds());
		}
		return meas;
	}

	public GridValue getValues() {
		return values;
	}

	public void setValues(GridValue values) {
		this.values = values;
	}

	public boolean isDimchange() {
		return dimchange;
	}

	public void setDimchange(boolean dimchange) {
		this.dimchange = dimchange;
	}

	public List<ItemOlapMember> getNewmembers() {
		return newmembers;
	}

	public void setNewmembers(List<ItemOlapMember> newmembers) {
		this.newmembers = newmembers;
	}

	public String getMembrerdrill() {
		return membrerdrill;
	}

	public void setMembrerdrill(String membrerdrill) {
		this.membrerdrill = membrerdrill;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}

	public ChartInfos getChartInfos() {
		return chartInfos;
	}

	public void setChartInfos(ChartInfos chartInfos) {
		this.chartInfos = chartInfos;
	}
	
	public MapOptions getMapOptions() {
		return mapOptions;
	}
	
	public void setMapOptions(MapOptions mapOptions) {
		this.mapOptions = mapOptions;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setParameters(List<ParameterDTO> parameters) {
		this.parameters = parameters;
	}

	public List<ParameterDTO> getParameters() {
		return parameters;
	}

	public void setWheres(List<String> wheres) {
		this.wheres = wheres;
	}

	public List<String> getWheres() {
		return wheres;
	}

	public void setProjection(boolean isProjection) {
		this.isProjection = isProjection;
	}

	public boolean isProjection() {
		return isProjection;
	}

	// public void setProjectionCube(GridCube projectionCube) {
	// this.projectionCube = projectionCube;
	// }
	//
	// public GridCube getProjectionCube() {
	// return projectionCube;
	// }

	public void setActualProjection(Projection actualProjection) {
		this.actualProjection = actualProjection;
	}

	public Projection getActualProjection() {
		return actualProjection;
	}

	public void setProjectionAllowed(boolean isProjectionAllowed) {
		this.isProjectionAllowed = isProjectionAllowed;
	}

	public boolean isProjectionAllowed() {
		return isProjectionAllowed;
	}

	public void setFailedUnames(List<String> failedUnames) {
		this.failedUnames = failedUnames;
	}

	public List<String> getFailedUnames() {
		return failedUnames;
	}

	public void addFailedUname(String failedUnames) {
		if (this.failedUnames == null) {
			this.failedUnames = new ArrayList<String>();
		}
		this.failedUnames.add(failedUnames);
	}

	public void setDrills(List<DrillDTO> drills) {
		this.drills = drills;
	}

	public List<DrillDTO> getDrills() {
		return drills;
	}

	public List<SortElement> getSortElements() {
		return sortElements;
	}

	public void setSortElements(List<SortElement> sortElements) {
		this.sortElements = sortElements;
	}

	public boolean isView() {
		return isView;
	}

	public void setView(boolean isView) {
		this.isView = isView;
	}

	public HashMap<Integer, Integer> getCustomSizes() {
		return customSizes;
	}

	public void setCustomSizes(HashMap<Integer, Integer> customSizes) {
		this.customSizes = customSizes;
	}

	public void setSizeLoaded(boolean b) {
		this.sizeLoaded = b;
	}


	// public List getLmeasures() {
	// return lmeasures;
	// }

	// public void setLmeasures(List lmeasures) {
	// this.lmeasures = lmeasures;
	// }

	// public GridThrough getGThrough() {
	// return gThrough;
	// }
	//
	// public void setGThrough(GridThrough through) {
	// gThrough = through;
	// }

}
