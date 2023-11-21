package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.components.CrossComponent;
import bpm.fwr.api.beans.components.GridComponent;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;

public class ReportGridWidget extends ReportWidget {

	private List<List<ReportWidget>> table;

	public ReportGridWidget() {
		super();
	}
	
	
	public ReportGridWidget(int id,String type,List<List<ReportWidget>> table) {
		super(id,type);
		this.table = table;
	}

	
	public ReportGridWidget(String gridWidgetStr) throws Exception{
		JSONObject gridWidgetJson = new JSONObject(gridWidgetStr);
		this.id = gridWidgetJson.getInt("id");
		this.type = gridWidgetJson.getString("type");
		
		JSONArray tableArrayJson = gridWidgetJson.getJSONArray("table");
		this.table = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		
		for(int i=0;i<tableArrayJson.length();i++) {
			JSONArray rowArrayJson = tableArrayJson.getJSONArray(i);
			List<ReportWidget> row = new ArrayList<>();
			
			for(int j=0;j<rowArrayJson.length();j++) {
				JSONObject widgetJson = rowArrayJson.getJSONObject(j);
				widgetJson.remove("rowIndex");
				widgetJson.remove("colIndex");
				
				String widgetType = widgetJson.getString("type");
				
				
				switch(widgetType)
				{
				case "Image":
					row.add(mapper.readValue(widgetJson.toString(), ReportImageWidget.class));
					break;
				case "List":
					row.add(new ReportListWidget(widgetJson.toString()));
					break;
				case "Crosstab":
					row.add(new ReportCrosstabWidget(widgetJson.toString()));
					break;		
				case "None":
					row.add(mapper.readValue(widgetJson.toString(), ReportNullWidget.class));
					break;	
				default:
					break;
				}
			}
			
			this.table.add(row);
		}
	}


	public List<List<ReportWidget>> getTable() {
		return table;
	}

	
	public void createReportGrid(Repository repository,Group group,User user,FWRReport fwrReport,int row) {
		for(List<ReportWidget> gridRow : this.table) {
			int col = 0;
			for(ReportWidget widget : gridRow) {
				if(widget instanceof ReportImageWidget) {
					ReportImageWidget imageWidget = (ReportImageWidget) widget;
					fwrReport.addComponent(imageWidget.createImageComponent(row,col));
				}
				else if(widget instanceof ReportListWidget) {
					ReportListWidget listWidget = (ReportListWidget) widget;
					GridComponent listComponent = listWidget.createListComponent(fwrReport,repository,group,user,row,col);
					fwrReport.addComponent(listComponent);
					fwrReport.getPrompts().addAll(listComponent.getPrompts());
					fwrReport.getFwrFilters().addAll(listComponent.getFwrFilters());
					fwrReport.getFilters().addAll(listComponent.getFilters());
				}
				else if(widget instanceof ReportCrosstabWidget) {
					ReportCrosstabWidget crosstabWidget = (ReportCrosstabWidget) widget;
					CrossComponent crosstabComponent = crosstabWidget.createCrosstabComponent(fwrReport,repository, group, user, row, col);
					fwrReport.addComponent(crosstabComponent);
					fwrReport.getPrompts().addAll(crosstabComponent.getPrompts());
					fwrReport.getFwrFilters().addAll(crosstabComponent.getFwrFilters());
					fwrReport.getFilters().addAll(crosstabComponent.getFilters());
				}
				col++;
			}
			row++;
		}
	}
}
