package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.SaveOptions;
import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.api.beans.components.CrossComponent;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.components.ImageComponent;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;


public class ReportSheet {

	private int id;
	private String name;
	private String description;
	private int width;
	private int height;
	private Orientation orientation;
	private HashMap<String, String> margins;
	private String pageSize;
	private List<ReportWidget> components;
	private SaveOptions saveOptions;
	
	public static class ReportWidgetType{
		public final static String LIST = "List";
		public final static String CROSSTAB = "Crosstab";
		public final static String IMAGE = "Image";
		public final static String TABLE = "Table";
		public final static String NONE = "None";
	}
	
	public ReportSheet(String reportSheetStr) throws Exception {
		JSONObject json = new JSONObject(reportSheetStr);
		
		id = json.getInt("id");
		name = json.getString("name");
		description = json.getString("description");
		width = json.getInt("width");
		height = json.getInt("height");
		pageSize = json.getString("pageSize");
		
		switch(json.getString("orientation")) {
		case "Portrait":
			orientation = Orientation.PORTRAIT;
			break;
		case "Landscape":
			orientation = Orientation.LANDSCAPE;
			break;
		}
		
		JSONObject marginsJson = json.getJSONObject("margins");
		margins = new HashMap<>();
		margins.put("top", String.valueOf(marginsJson.getInt("top")));
		margins.put("left", String.valueOf(marginsJson.getInt("left")));
		margins.put("right", String.valueOf(marginsJson.getInt("right")));
		margins.put("bottom", String.valueOf(marginsJson.getInt("bottom")));
		
		JSONArray componentsJson = json.getJSONArray("components");
		components = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		for(int i=0;i<componentsJson.length();i++) {
			JSONObject componentJson = componentsJson.getJSONObject(i);
			String componentType = componentJson.getString("type");
			
			switch(componentType)
			{
			case ReportWidgetType.IMAGE:
				components.add(mapper.readValue(componentJson.toString(), ReportImageWidget.class));
				break;
			case ReportWidgetType.LIST:
				components.add(new ReportListWidget(componentJson.toString()));
				break;
			case ReportWidgetType.CROSSTAB:
				components.add(new ReportCrosstabWidget(componentJson.toString()));
				break;
			case ReportWidgetType.TABLE:
				components.add(new ReportGridWidget(componentJson.toString()));
				break;
			case ReportWidgetType.NONE:
				components.add(mapper.readValue(componentJson.toString(), ReportNullWidget.class));
				break;	
			default:
				break;
			}
		}
		
		this.saveOptions = null;
		if(json.has("saveOptions")) {
			JSONObject saveOptionsJson = json.getJSONObject("saveOptions");
			this.saveOptions = mapper.readValue(saveOptionsJson.toString(),SaveOptions.class);
		}
	}
	
	
	public ReportSheet(FWRReport fwrReport,int id) {
		this.id = id;
		this.name = fwrReport.getName();
		this.description = fwrReport.getDescription();
		this.width = fwrReport.getWidth();
		this.height = fwrReport.getHeight();
		this.orientation = fwrReport.getOrientation();
		this.margins = fwrReport.getMargins();
		this.pageSize = fwrReport.getPageSize();
		this.saveOptions = fwrReport.getSaveOptions();
		
		this.components = new ArrayList<>();
		int ind = 0;
		for(IReportComponent component : fwrReport.getComponents()) {
			if(component instanceof ImageComponent) {
				this.components.add(new ReportImageWidget((ImageComponent) component,ind));
			}
			else if(component instanceof GridComponent) {
				this.components.add(new ReportListWidget((GridComponent) component,ind));
			}
			else if(component instanceof CrossComponent) {
				this.components.add(new ReportCrosstabWidget((CrossComponent) component,ind));
			}
			ind++;
		}
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public List<ReportWidget> getComponents() {
		return components;
	}
	public Orientation getOrientation() {
		return orientation;
	}

	public HashMap<String, String> getMargins() {
		return margins;
	}

	public String getPageSize() {
		return pageSize;
	}
	
	public SaveOptions getSaveOptions() {
		return saveOptions;
	}


	public FWRReport generateFWRReport(Repository repository,Group group,User user) {
		FWRReport fwrReport = new FWRReport();
		
		fwrReport.setName(name);
		fwrReport.setDescription(description);
		fwrReport.setWidth(width);
		fwrReport.setHeight(height);
		fwrReport.setPageSize(pageSize);
		fwrReport.setOrientation(orientation);
		fwrReport.setMargins(margins);
		fwrReport.setSaveOptions(saveOptions);
		
		int row = 0;
		for(ReportWidget widget : components) {
			if(widget instanceof ReportImageWidget) {
				ReportImageWidget imageWidget = (ReportImageWidget) widget;
				fwrReport.addComponent(imageWidget.createImageComponent(row,0));
				row++;
			}
			else if(widget instanceof ReportListWidget) {
				ReportListWidget listWidget = (ReportListWidget) widget;
				GridComponent listComponent = listWidget.createListComponent(fwrReport,repository,group,user,row,0);
				fwrReport.addComponent(listComponent);
				row++;
			}
			else if(widget instanceof ReportCrosstabWidget) {
				ReportCrosstabWidget crosstabWidget = (ReportCrosstabWidget) widget;
				CrossComponent crosstabComponent = crosstabWidget.createCrosstabComponent(fwrReport,repository, group, user, row, 0);
				fwrReport.addComponent(crosstabComponent);
				row++;
			}
			else if(widget instanceof ReportGridWidget) {
				ReportGridWidget gridWidget = (ReportGridWidget) widget;
				gridWidget.createReportGrid(repository, group, user, fwrReport, row);
			}
		}
		
		return fwrReport;
	}
	
	

	

	
}
