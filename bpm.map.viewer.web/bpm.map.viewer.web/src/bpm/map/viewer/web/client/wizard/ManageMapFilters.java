package bpm.map.viewer.web.client.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.panel.viewer.MapViewer;
import bpm.map.viewer.web.client.services.MapViewerService;
import bpm.map.viewer.web.client.utils.FilteredTreeItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ManageMapFilters extends AbstractDialogBox {
	private static ManageMapFiltersUiBinder uiBinder = GWT
			.create(ManageMapFiltersUiBinder.class);

	interface ManageMapFiltersUiBinder extends
			UiBinder<Widget, ManageMapFilters> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	CheckBox cbxAll, cbxAny;
	
	@UiField
	Tree treeFilters;

	@UiField
	MyStyle style;

	private MapViewer parent;
	private AxisInfo tempInfo;
	private List<LevelMember> filteredElements = new ArrayList<LevelMember>();

	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public ManageMapFilters(MapViewer parent, ClickHandler okHandler) {
		super("Edition des filtres", false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.filteredElements = parent.getFilterLevelMembers();
		
		fillTreeFilters();
		
		createButtonBar(lblCnst.Ok(), okHandler, lblCnst.Cancel(), cancelHandler);
	}
	
	public void fillTreeFilters() {
		List<Axis> axis = parent.getSelectedMap().getAxis();
		if(axis != null){
			Collections.sort(axis, new Comparator<Axis>() {
				@Override
				public int compare(Axis m1, Axis m2) {
					return m1.getName().compareToIgnoreCase(m2.getName());
				}
			});
		}
		
		treeFilters.clear();
		for (Axis axe : axis) {
			TreeItem item = new TreeItem();
			item.setHTML(axe.getName());
			treeFilters.addItem(item);
			
			loadAxisInfo(axe.getId());
			
//			for(LevelMember level : tempInfo.getMembers()){
//				FilteredTreeItem branch = new FilteredTreeItem(this, level, false);
//				item.addItem(branch);
//			}
		}
	}

	private void loadAxisInfo(int id) {
		showWaitPart(true);
		
		MapViewerService.Connect.getInstance().getAxisInfo(id , new AsyncCallback<AxisInfo>() {
	
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
	
			@Override
			public void onSuccess(AxisInfo result) {
				
				showWaitPart(false);
				tempInfo = result;
				List<LevelMember> members = tempInfo.getMembers();
				if(members != null){
					Collections.sort(members, new Comparator<LevelMember>() {
						@Override
						public int compare(LevelMember m1, LevelMember m2) {
							return m1.getLabel().compareToIgnoreCase(m2.getLabel());
						}
					});
				}
				for(LevelMember level : members){
					FilteredTreeItem branch = new FilteredTreeItem(ManageMapFilters.this, level, false);
					if(filteredElements.contains(level)){
						branch.setCheckBoxState(true);
					}
					for(int i=0; i<treeFilters.getItemCount(); i++){
						if(treeFilters.getItem(i).getText().equals(result.getAxis().getName())){
							treeFilters.getItem(i).addItem(branch);
						}	
					}
					completeItems(branch);
				}
				
			}

			
		});
	}
	
	
	private void completeItems(FilteredTreeItem item) {
		List<LevelMember> children = item.getLevelMember().getChildren();
		if(children != null){
			Collections.sort(children, new Comparator<LevelMember>() {
				@Override
				public int compare(LevelMember m1, LevelMember m2) {
					return m1.getLabel().compareToIgnoreCase(m2.getLabel());
				}
			});
		}
		
		for(LevelMember level : children){
			FilteredTreeItem branch = new FilteredTreeItem(ManageMapFilters.this, level, false);
			if(filteredElements.contains(level)){
				branch.setCheckBoxState(true);
			}
			item.addItem(branch);
			
			completeItems(branch);
		}
		
	}
	
	public void onFilterSelection(boolean checked, FilteredTreeItem item){
		cbxAll.setValue(false);
		cbxAny.setValue(false);
		LevelMember lmember = item.getLevelMember();
		if(checked){
			filteredElements.add(lmember);
			selectChildren(item);
		}else {
			filteredElements.remove(lmember);
			unselectChildren(item);
		}
	}
	
	@UiHandler("cbxAll")
	public void onAllClick(ClickEvent event) {
		selectAll();
		cbxAny.setValue(false);
	}

	@UiHandler("cbxAny")
	public void onAnyClick(ClickEvent event) {
		unselectAll();
		cbxAll.setValue(false);
	}
	
	public void selectAll(){
		for(int i=0; i<treeFilters.getItemCount(); i++){
			TreeItem axe = treeFilters.getItem(i);
			for(int j=0; j<axe.getChildCount(); j++){
				FilteredTreeItem item = (FilteredTreeItem) axe.getChild(j);
				item.setCheckBoxState(true);
				filteredElements.add(item.getLevelMember());
				selectChildren(item);
			}
		}
	}
	
	public void unselectAll(){
		for(int i=0; i<treeFilters.getItemCount(); i++){
			TreeItem axe = treeFilters.getItem(i);
			for(int j=0; j<axe.getChildCount(); j++){
				FilteredTreeItem item = (FilteredTreeItem) axe.getChild(j);
				item.setCheckBoxState(false);
				filteredElements.remove(item.getLevelMember());
				unselectChildren(item);
			}
		}
	}
	
	public void selectChildren(FilteredTreeItem item){
		for(int i=0; i<item.getChildCount(); i++){
			
			FilteredTreeItem child = (FilteredTreeItem) item.getChild(i);
			child.setCheckBoxState(true);
			filteredElements.add(child.getLevelMember());
			selectChildren(child);
		}
	}
	
	public void unselectChildren(FilteredTreeItem item){
		for(int i=0; i<item.getChildCount(); i++){
			
			FilteredTreeItem child = (FilteredTreeItem) item.getChild(i);
			child.setCheckBoxState(false);
			filteredElements.remove(child.getLevelMember());
			unselectChildren(child);
		}
	}
	
//	private ClickHandler okHandler = new ClickHandler() { //non utilise
//		@Override
//		public void onClick(ClickEvent event) {
//			
//			parent.updateFilters(filteredElements);
//			ManageMapFilters.this.hide();
//		}
//	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ManageMapFilters.this.hide();
		}
	};

	public List<LevelMember> getFilteredElements() {
		return filteredElements;
	}
	
}
