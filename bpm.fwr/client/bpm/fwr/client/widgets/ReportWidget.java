package bpm.fwr.client.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.draggable.HasBin;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.WidgetType;

import com.google.gwt.user.client.ui.AbsolutePanel;

public abstract class ReportWidget extends AbsolutePanel implements HasBin{
	private static final String CSS_ENTETE_REPORT_WIDGET = "headReportWidget";
	
	private List<IResource> prompts = new ArrayList<IResource>();
	private List<FWRFilter> filters = new ArrayList<FWRFilter>();
	private List<FwrRelationStrategy> relations = new ArrayList<FwrRelationStrategy>();
	
	private WidgetType type;
	private ReportSheet reportSheetParent;
	
	private boolean showHeader = true;
	
	private GridCell selectedCell;
	
	public ReportWidget(ReportSheet reportSheetParent, WidgetType type, int width) {
		this.reportSheetParent = reportSheetParent;
		this.type = type;
		
		this.setWidth(100 + "%");
		this.setStyleName(CSS_ENTETE_REPORT_WIDGET);
	}
	
	@Override
	protected void onDetach() {
		if(getParent() instanceof ReportSheet){
			((ReportSheet)getParent()).removeWidget(this);
		}
		super.onDetach();
	}
	
	public List<IResource> getPrompts(){
		return prompts;
	}
	
	public void addPromptResource(IResource prompt){
		this.prompts.add(prompt);
	}
	
	public void addFilter(FWRFilter filter){
		this.filters.add(filter);
	}
	
	public void removePromptResource(IResource prompt){
		this.prompts.remove(prompt);
	}
	
	public int indexOfResource(IResource prompt){
		return this.prompts.indexOf(prompt);
	}
	
	public void removeFilter(FWRFilter filter){
		this.filters.remove(filter);
	}
	
	public int indexOfResource(FWRFilter filter){
		return this.filters.indexOf(filter);
	}
	
	public List<FWRFilter> getFilters(){
		return filters;
	}
	
	public boolean hasFilter(){
		return filters != null && !filters.isEmpty() ? true : false;
	}
	
	public boolean hasPrompt(){
		return prompts != null && !prompts.isEmpty() ? true : false;
	}
	
	public void addRelation(FwrRelationStrategy relation){
		this.relations.add(relation);
	}
	
	public void removeRelation(FwrRelationStrategy relation){
		this.relations.remove(relation);
	}
	
	public int indexOfResource(FwrRelationStrategy relation){
		return this.relations.indexOf(relation);
	}
	
	public List<FwrRelationStrategy> getRelations(){
		return relations;
	}
	
	public boolean hasRelations(){
		return relations != null && !relations.isEmpty() ? true : false;
	}
	
	public WidgetType getType(){
		return type;
	}

	public ReportSheet getReportSheetParent() {
		return reportSheetParent;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public boolean showHeader() {
		return showHeader;
	}
	
	public abstract int getWidgetHeight();

	public void setSelectedCell(GridCell selectedCell) {
		this.selectedCell = selectedCell;
	}

	public GridCell getSelectedCell() {
		return selectedCell;
	}
}
