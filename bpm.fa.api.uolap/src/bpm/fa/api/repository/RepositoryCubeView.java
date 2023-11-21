package bpm.fa.api.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.OLAPChart;
import bpm.fa.api.olap.Topx;

public class RepositoryCubeView {
	private ArrayList<String> columns = new ArrayList<String>();
	private ArrayList<String> rows = new ArrayList<String>();
	private ArrayList<String> wheres = new ArrayList<String>();
	private List<Topx> topx = new ArrayList<Topx>();
	private HashMap<String, String> personalNames = new HashMap<String, String>();
	private HashMap<String, Boolean> percentMeasures = new HashMap<String, Boolean>();
	private String reportTitle;
	private boolean isShowTotals = true;
	private List<Parameter> parameters = new ArrayList<Parameter>();
	private int rowCount;
	private int colCount;
	private boolean showEmpty;
	private boolean showProps;
	private String base64img;
	private OLAPChart chart;

	private HashMap<String, String> sortElements = new HashMap<String, String>();

	public ArrayList<String> getRows() {
		return rows;
	}

	public ArrayList<String> getCols() {
		return columns;
	}

	public ArrayList<String> getWheres() {
		return wheres;
	}

	public void addCol(String col) {
		columns.add(col);
	}

	public void addRow(String row) {
		rows.add(row);
	}

	public void addWhere(String where) {
		wheres.add(where);
	}

	public String getXML() {
		String xml = null;

		xml = "            <view>\n";
		for (String s : this.getCols()) {
			xml += "                <col>" + s + "</col>\n";
		}
		for (String s : this.getRows()) {
			xml += "                <row>" + s + "</row>\n";
		}
		for (String s : this.getWheres()) {
			xml += "                <where>" + s + "</where>\n";
		}
		try {
			for (String uname : sortElements.keySet()) {
				xml += "                <sortelement><uname>" + uname + "</uname><type>" + sortElements.get(uname) + "</type></sortelement>\n";
			}
		} catch (Exception e) {
		}
		for (Topx t : topx) {
			xml += "                <topx><element>" + t.getElementName() + "</element><target>" + t.getElementTarget() + "</target><count>" + t.getCount() + "</count></topx>\n";
		}
		for (String uname : personalNames.keySet()) {
			xml += "                <personalname><uname>" + uname + "</uname><personal>" + personalNames.get(uname) + "</personal></personalname>\n";
		}
		for (String percent : percentMeasures.keySet()) {
			xml += "                <percent><measure>" + percent + "</measure><showmeasure>" + percentMeasures.get(percent) + "</showmeasure></percent>\n";
		}
		if (reportTitle != null) {
			xml += "					<reporttitle>" + reportTitle + "</reporttitle>\n";
		}
		xml += "				<showtotals>" + isShowTotals + "</showtotals>\n";
		xml += "				<parameters>\n";
		for (Parameter param : parameters) {
			xml += "				<parameter>\n";
			xml += "						<name>" + param.getName() + "</name>\n";
			xml += "						<level>" + param.getLevel() + "</level>\n";
			if (param.getValue() != null) {
				xml += "						<value>" + param.getValue() + "</value>\n";
			}
			else {
				xml += "						<value></value>\n";
			}
			xml += "				</parameter>\n";
		}
		xml += "				</parameters>\n";
		xml += "				<rowcount>" + rowCount + "</rowcount>";
		xml += "				<colcount>" + colCount + "</colcount>";
		xml += "				<showempty>" + showEmpty + "</showempty>";
		xml += "				<showprops>" + showProps + "</showprops>";
		if(base64img != null) {
			xml += "				<base64img>" + base64img + "</base64img>\n";
		}
		if(chart != null) {
			xml += "			   " + chart.loadXML();
		}
		xml += "            </view>\n";

		return xml;
	}

	public void setTopx(List<Topx> topx) {
		this.topx = topx;
	}

	public List<Topx> getTopx() {
		return topx;
	}

	public void addTopx(String element, String target, String count) {
		Topx t = new Topx(element, target, Integer.parseInt(count), true);
		topx.add(t);
	}

	public void setPersonalNames(HashMap<String, String> personalNames) {
		this.personalNames = personalNames;
	}

	public HashMap<String, String> getPersonalNames() {
		return personalNames;
	}

	public void addPersonalName(String uname, String personal) {
		personalNames.put(uname, personal);
	}

	public void setPercentMeasures(HashMap<String, Boolean> percentMeasures) {
		this.percentMeasures = percentMeasures;
	}

	public HashMap<String, Boolean> getPercentMeasures() {
		return percentMeasures;
	}

	public void addPercentMeasure(String measureName, String showMeasure) {
		if (showMeasure.equalsIgnoreCase("true")) {
			percentMeasures.put(measureName, true);
		}
		else {
			percentMeasures.put(measureName, false);
		}

	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setShowTotals(boolean isShowTotals) {
		this.isShowTotals = isShowTotals;
	}

	public boolean isShowTotals() {
		return isShowTotals;
	}

	public void isShowTotals(String showTotals) {
		if (showTotals.equalsIgnoreCase("true")) {
			isShowTotals = true;
		}
		else {
			isShowTotals = false;
		}
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	public void addParameter(String name, String value, String level) {
		Parameter param = new Parameter();
		param.setName(name);
		param.setLevel(level);
		param.setValue(value);
		this.parameters.add(param);
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setColCount(int colCount) {
		this.colCount = colCount;
	}

	public int getColCount() {
		return colCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = Integer.parseInt(rowCount);
	}

	public void setColCount(String colCount) {
		this.colCount = Integer.parseInt(colCount);
	}

	public void setShowProps(boolean showProps) {
		this.showProps = showProps;
	}

	public void setShowProps(String showProps) {
		this.showProps = Boolean.parseBoolean(showProps);
	}

	public boolean isShowProps() {
		return showProps;
	}

	public void setShowEmpty(boolean showEmpty) {
		this.showEmpty = showEmpty;
	}

	public void setShowEmpty(String showEmpty) {
		this.showEmpty = Boolean.parseBoolean(showEmpty);
	}

	public boolean isShowEmpty() {
		return showEmpty;
	}

	public HashMap<String, String> getSortElements() {
		return sortElements;
	}

	public void setSortElements(HashMap<String, String> sortElements) {
		this.sortElements = sortElements;
	}

	public void addSortElement(String uname, String type) {
		this.sortElements.put(uname, type);
	}

	public String getBase64img() {
		return base64img;
	}

	public void setBase64img(String base64img) {
		this.base64img = base64img;
	}

	public OLAPChart getChart() {
		return chart;
	}

	public void setChart(OLAPChart chart) {
		this.chart = chart;
	}

	
	

}
