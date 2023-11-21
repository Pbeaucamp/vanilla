package bpm.faweb.server.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fasd.olap.Drill;
import org.fasd.olap.DrillCube;
import org.fasd.olap.DrillReport;
import org.fasd.olap.DrillReportParameter;

import bpm.fa.api.item.ItemElement;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPResult;
import bpm.faweb.shared.DrillDTO;
import bpm.faweb.shared.DrillParameterDTO;
import bpm.faweb.shared.infoscube.ChartInfos;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class InfosOlap {
	private OLAPCube cube;
	private OLAPResult res;
	private InfosReport infosReport;
	private List<String> measures = new ArrayList<String>();
	private boolean firstValue = true;
	private String cubeName;
	private RepositoryItem selectedFasd;
	private HashMap<String, ItemElement> itemsByUname = new HashMap<String, ItemElement>();
	private HashMap<String, ItemElement> filtersByUname = new HashMap<String, ItemElement>();
	private ChartInfos chartInfos;
	private int viewId;

	public InfosOlap() {
		chartInfos = new ChartInfos();
		infosReport = new InfosReport();
	}
	
	public ChartInfos getChartInfos() {
		return chartInfos;
	}

	public void setChartInfos(ChartInfos chartInfos) {
		this.chartInfos = chartInfos;
	}

	public boolean isFirstValue() {
		return firstValue;
	}

	public void setFirstValue(boolean firstValue) {
		this.firstValue = firstValue;
	}

	public String getCubeName() {
		return cubeName;
	}

	public void setCubeName(String cubeName) {
		this.cubeName = cubeName;
	}


	public InfosReport getInfosReport() {
		return infosReport;
	}

	public void setInfosReport(InfosReport infosReport) {
		this.infosReport = infosReport;
	}

	public OLAPCube getCube() {
		return cube;
	}

	public void setCube(OLAPCube cube) {
		this.cube = cube;
		try {
			infosReport.setDrills(createDTODrills(cube.getDrills()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private List<DrillDTO> createDTODrills(List<Drill> drills) {
		
		List<DrillDTO> res = new ArrayList<DrillDTO>();
		if(drills != null) { 
			for(Drill drill: drills) {
				DrillDTO dto = null;
				if(drill instanceof DrillCube) {
					dto = new DrillDTO(drill.getDrillName(), ((DrillCube)drill).getFasdId(), ((DrillCube)drill).getCubeName());
				}
				else {
					dto = new DrillDTO(drill.getDrillName(), ((DrillReport)drill).getItemId(), createDrillParameters(((DrillReport)drill).getParameters()));
				}
				res.add(dto);
			}
		}
		
		return res;
	}

	private List<DrillParameterDTO> createDrillParameters(List<DrillReportParameter> parameters) {
		List<DrillParameterDTO> res = new ArrayList<DrillParameterDTO>();
		if(parameters != null) { 
			for(DrillReportParameter drill: parameters) {
				DrillParameterDTO dto = new DrillParameterDTO();
				dto.setDimension(drill.getDimension());
				dto.setLevel(drill.getLevel());
				dto.setName(drill.getName());
				res.add(dto);
			}
		}
		
		return res;
	}

	public OLAPResult getRes() {
		return res;
	}

	public void setRes(OLAPResult res) {
		this.res = res;
	}
	
	public List<String> getMeasures() {
		return measures;
	}

	public void setMeasures(List<String> measures) {
		this.measures = measures;
	}

	public RepositoryItem getSelectedFasd() {
		return selectedFasd;
	}


	public void setSelectedFasd(RepositoryItem selectedFasd) {
		this.selectedFasd = selectedFasd;
		infosReport.setFasdId(selectedFasd.getId());
	}

	public HashMap<String, ItemElement> getItemsByUname() {
		return itemsByUname;
	}

	public void setItemsByUname(HashMap<String, ItemElement> itemsByUname) {
		this.itemsByUname = itemsByUname;
	}

	public HashMap<String, ItemElement> getFiltersByUname() {
		return filtersByUname;
	}

	public void setFiltersByUname(HashMap<String, ItemElement> filtersByUname) {
		this.filtersByUname = filtersByUname;
	}
	
	public void setProjectionAllowed(boolean projectionAllowed) {
		infosReport.setProjectionAllowed(projectionAllowed);
	}

	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
		infosReport.setView(true);
	}
}

