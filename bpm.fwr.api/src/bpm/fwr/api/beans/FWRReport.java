package bpm.fwr.api.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.Constants.Locales;
import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.components.ReportComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.api.beans.dataset.JoinDataSet;
import bpm.fwr.api.beans.template.Style;
import bpm.vanilla.platform.core.repository.IReport;

public class FWRReport extends ReportComponent implements IReport {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;

	private String selectedLanguage = "en";

	private HashMap<String, String> titles = new HashMap<String, String>();
	private HashMap<String, String> subtitles = new HashMap<String, String>();

	private String output;

	// Style and margins
	private Style titleStyle;
	private Style subTitleStyle;
	private String templatePath = "";

	private int width;
	private int height;
	private HashMap<String, String> margins = new HashMap<String, String>();

	private Orientation orientation;
	private String pageSize = "A4";

	// Header and footer
	private PageOptions header;
	private PageOptions footer;
	private WysiwygReportHeader wysiwygReportHeader;

	// Save options
	private SaveOptions saveOptions = null;

	private DataSource datasource;
	private String query;
	private boolean isPreview;

	private List<String> filters = new ArrayList<String>();
	private List<FWRFilter> fwrFilters = new ArrayList<FWRFilter>();
	private List<IResource> prompts = new ArrayList<IResource>();
	private List<FwrRelationStrategy> relations = new ArrayList<FwrRelationStrategy>();

	private List<IReportComponent> components = new ArrayList<IReportComponent>();

	private int colCount;
	private int rowCount;

	public FWRReport() {
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Style getTitleStyle() {
		return titleStyle;
	}

	public void setTitleStyle(Style titleStyle) {
		this.titleStyle = titleStyle;
	}

	public Style getSubTitleStyle() {
		return subTitleStyle;
	}

	public void setSubTitleStyle(Style subTitleStyle) {
		this.subTitleStyle = subTitleStyle;
	}

	public HashMap<String, String> getMargins() {
		return margins;
	}

	public void setMargins(HashMap<String, String> margins) {
		this.margins = margins;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<String> filters) {
		this.filters = filters;
	}

	public List<FwrRelationStrategy> getRelations() {
		return relations;
	}

	public void setRelations(List<FwrRelationStrategy> relations) {
		this.relations = relations;
	}

	public PageOptions getHeader() {
		return header;
	}

	public void setHeader(PageOptions header) {
		this.header = header;
	}

	public PageOptions getFooter() {
		return footer;
	}

	public void setFooter(PageOptions footer) {
		this.footer = footer;
	}

	public SaveOptions getSaveOptions() {
		return saveOptions;
	}

	public void setSaveOptions(SaveOptions saveOptions) {
		this.saveOptions = saveOptions;
	}

	public List<FWRFilter> getFwrFilters() {
		return fwrFilters;
	}

	public void setFwrFilters(List<FWRFilter> fwrFilters) {
		this.fwrFilters = fwrFilters;
	}

	public void addFwrFilter(FWRFilter f) {
		if (fwrFilters == null) {
			fwrFilters = new ArrayList<FWRFilter>();
		}
		this.fwrFilters.add(f);

	}

	public void setMarginLeft(String m) {
		this.margins.put("left", m);
	}

	public void setMarginRight(String m) {
		this.margins.put("right", m);
	}

	public void setMarginBottom(String m) {
		this.margins.put("bottom", m);
	}

	public void setMarginTop(String m) {
		this.margins.put("top", m);
	}

	public String getMarginLeft(String m) {
		return (String) this.margins.get("left");
	}

	public String getMarginRight(String m) {
		return (String) this.margins.get("right");
	}

	public String getMarginBottom(String m) {
		return (String) this.margins.get("bottom");
	}

	public String getMarginTop(String m) {
		return (String) this.margins.get("top");
	}

	public void addFilter(String f) {
		if (this.filters == null) {
			this.filters = new ArrayList<String>();
		}
		this.filters.add(f);
	}

	public void addRelation(FwrRelationStrategy relation) {
		if (relations == null) {
			this.relations = new ArrayList<FwrRelationStrategy>();
		}
		this.relations.add(relation);
	}

	public List<IResource> getPrompts() {
		return prompts;
	}

	public void setPrompts(List<IResource> prompts) {
		this.prompts = prompts;
	}

	public void addFwrPromptResource(IResource p) {
		if (this.prompts == null) {
			this.prompts = new ArrayList<IResource>();
		}
		this.prompts.add(p);
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	@Deprecated
	public String getTitle() {
		String s = "";
		if (titles.keySet().contains(Locales.DEFAULT))
			return titles.get(Locales.DEFAULT);
		else {
			for (String _s : titles.keySet()) {
				s = titles.get(_s);
				break;
			}
			return s;
		}
	}

	public String getTitle(String local) {
		return titles.get(local);
	}

	public HashMap<String, String> getTitles() {
		return titles;
	}

	@Deprecated
	public void setTitle(String title) {
		this.addLocaleTitle(Locales.DEFAULT, title);
	}

	public void setTitle(HashMap<String, String> title) {
		this.titles = title;
	}

	@Deprecated
	public String getSubtitle() {
		String s = "";
		if (subtitles.keySet().contains(Locales.DEFAULT))
			return subtitles.get(Locales.DEFAULT);
		else {
			for (String _s : subtitles.keySet()) {
				s = titles.get(_s);
				break;
			}
			return s;
		}
	}

	public HashMap<String, String> getSubtitles() {
		return subtitles;
	}

	public String getSubtitle(String locale) {
		return subtitles.get(locale);
	}

	@Deprecated
	public void setSubtitle(String subtitle) {
		this.addLocaleSubtitle(Locales.DEFAULT, subtitle);
	}

	public void setSubtitle(HashMap<String, String> subtitle) {
		this.subtitles = subtitle;
	}

	public void addLocaleTitle(String locale, String title) {
		titles.put(locale, title);
	}

	public void addLocaleSubtitle(String locale, String subtitle) {
		subtitles.put(locale, subtitle);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<IReportComponent> getComponents() {
		return components;
	}

	public void setComponents(List<IReportComponent> components) {
		this.components = components;
	}

	public void addComponent(IReportComponent component) {
		if (this.components == null) {
			this.components = new ArrayList<IReportComponent>();
		}
		this.components.add(component);
	}

	public void setPreview(String isPreview) {
		this.isPreview = Boolean.parseBoolean(isPreview);
	}

	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

	public boolean isPreview() {
		return isPreview;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public int getColCount() {
		return colCount;
	}

	public void setColCount(int colCount) {
		this.colCount = colCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public List<DataSet> getAllDatasets() {
		List<DataSet> datasets = new ArrayList<DataSet>();
		if (components != null) {
			for (IReportComponent comp : components) {
				DataSet ds = comp.getDataset();
				if(ds != null){
					if(ds instanceof JoinDataSet) {
						datasets.addAll(((JoinDataSet)ds).getChilds());
					}
					datasets.add(ds);
				}
			}
		}
		return datasets;
	}
	
	public void setWysiwygReportHeader(WysiwygReportHeader wysiwygReportHeader) {
		this.wysiwygReportHeader = wysiwygReportHeader;
	}

	public WysiwygReportHeader getWysiwygReportHeader() {
		return wysiwygReportHeader;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

}
