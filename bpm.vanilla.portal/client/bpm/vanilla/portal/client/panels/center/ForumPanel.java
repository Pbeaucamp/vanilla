package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.ForumItem;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RangeChangeEvent.Handler;
import com.google.gwt.view.client.RowCountChangeEvent;

public class ForumPanel extends Tab implements HasRows {

	private static ForumPanelUiBinder uiBinder = GWT.create(ForumPanelUiBinder.class);

	interface ForumPanelUiBinder extends UiBinder<Widget, ForumPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();
		String pager();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar, panelContent;

	@UiField
	SimplePanel panelPager;
	
	@UiField
	ListBox lstFilter;
	
	@UiField
	Image btnClear;

	@UiField
	TextBox txtSearch;
	
	private ContentDisplayPanel mainPanel;
	LinkedHashMap<RepositoryItem, List<Comment>> allForumItems;
	LinkedHashMap<RepositoryItem, List<Comment>> filteredForumItems;
	List<String> typeNames;
	
	SimplePager pager;
	
	public ForumPanel(ContentDisplayPanel mainPanel) {
		super(mainPanel, ToolsGWT.lblCnst.Forum(), true);
		this.add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		btnClear.setVisible(false);

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());
		initFilter();
		initForumContent();
		
		initJs(this);
	}

	private void initForumContent() {
		showWaitPart(true);
		BiPortalService.Connect.getInstance().getCommentsbyAllItems(new GwtCallbackWrapper<Map<RepositoryItem, List<Comment>>>(ForumPanel.this, true) {
			
			@Override
			public void onSuccess(Map<RepositoryItem, List<Comment>> result) {
				allForumItems = (LinkedHashMap<RepositoryItem, List<Comment>>) result;
				filteredForumItems = allForumItems;
				loadContent();
				
				pager = new SimplePager(TextLocation.CENTER);
				pager.setDisplay(ForumPanel.this);
				pager.addStyleName(style.pager());
				panelPager.add(pager);
				
				showWaitPart(false);
			}
		}.getAsyncCallback());
	}

	private void initFilter() {
		lstFilter.addItem(ToolsGWT.lblCnst.SelectAType());
		BiPortalService.Connect.getInstance().getAllRepositoryTypes(new GwtCallbackWrapper<List<String>>(ForumPanel.this, true) {
			
			@Override
			public void onSuccess(List<String> result) {
				typeNames = result;
				for(String type : result){
					lstFilter.addItem(type);
				}
			}
		}.getAsyncCallback());
	}
	
	protected void loadContent(/*Map<RepositoryItem, List<Comment>> result*/) {
//		panelContent.clear();
//		for(Entry<RepositoryItem, List<Comment>> object : result.entrySet()){
//			panelContent.add(new ForumItem(this, object.getKey(), object.getValue()));
//		}
		setRowCount(getRowCount());
		setVisibleRange(0,20);
	}

	private void filterResult(){
		LinkedHashMap<RepositoryItem, List<Comment>> filterFinal = new LinkedHashMap<RepositoryItem, List<Comment>>();
		LinkedHashMap<RepositoryItem, List<Comment>> filterText = new LinkedHashMap<RepositoryItem, List<Comment>>();
		//filter text
		String search = txtSearch.getText();
		if(search.equals("")){
			filterText = new LinkedHashMap<RepositoryItem, List<Comment>>(allForumItems);
		} else {
			for (Entry<RepositoryItem, List<Comment>> entry : allForumItems.entrySet()) {
				for(Comment com : entry.getValue()){
					if(com.getComment().toLowerCase().contains(search.toLowerCase())){
						filterText.put(entry.getKey(), entry.getValue());
						break;
					}
				}
			}
		}
		
		
		//filter type
		int type = lstFilter.getSelectedIndex() - 1;
		if(type == -1){
			filterFinal = filterText;
		} else {
			for (Entry<RepositoryItem, List<Comment>> entry : filterText.entrySet()) {
				if(entry.getKey().getType() == type){
					filterFinal.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		filteredForumItems = filterFinal;	
		loadContent();
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		if(!txtSearch.getText().isEmpty()) {
			btnClear.setVisible(true);
			
			filterResult();
		}
	}
	
	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		clearSearch();
		filterResult();
	}
	
	private void clearSearch() {
		txtSearch.setText("");
		
		btnClear.setVisible(false);
	}
	
	@UiHandler("lstFilter")
	public void onTypeChange(ChangeEvent event) {
		filterResult();
	}
	
	private final native void initJs(ForumPanel panel) /*-{
		var panel = panel;
			
		$wnd.onresize = function(){ 
			panel.@bpm.vanilla.portal.client.panels.center.ForumPanel::handleResize()();
		};
		
	}-*/;
	
	public  native void onResize() /*-{
		$wnd.setTimeout('replaceEditPanel();', 50);
	}-*/;
	
	public void handleResize() {
		for(int i=0; i< panelContent.getWidgetCount(); i++){
			if(panelContent.getWidget(i) instanceof ForumItem){
				((ForumItem)panelContent.getWidget(i)).onResize();
			}
		}
	}

	@Override
	public HandlerRegistration addRangeChangeHandler(Handler handler) {
		return ForumPanel.this.addHandler(handler, RangeChangeEvent.getType());
	}

	@Override
	public HandlerRegistration addRowCountChangeHandler(com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		return ForumPanel.this.addHandler(handler, RowCountChangeEvent.getType());
	}

	@Override
	public int getRowCount() {
		return filteredForumItems.size();
	}

	@Override
	public Range getVisibleRange() {
		int length = 0;
		int start = 0;
		for(int i=0; i<panelContent.getWidgetCount(); i++){
			if(panelContent.getWidget(i) instanceof ForumItem){
				length ++;
				if(length == 1){ //first
					List<RepositoryItem> lri = new ArrayList<RepositoryItem>(filteredForumItems.keySet());
					start = lri.indexOf(((ForumItem)panelContent.getWidget(i)).getItem());
				}
			}
		}
		return new Range(start, length);
	}

	@Override
	public boolean isRowCountExact() {
		return false;
	}

	@Override
	public void setRowCount(int count) {
		RowCountChangeEvent.fire(ForumPanel.this, count, true);
	}

	@Override
	public void setRowCount(int count, boolean isExact) {}

	@Override
	public void setVisibleRange(int start, int length) {
		panelContent.clear();
		int i = 0;
		for(Entry<RepositoryItem, List<Comment>> object : filteredForumItems.entrySet()){
			if(i >= start && i < start+length){
				panelContent.add(new ForumItem(this, object.getKey(), object.getValue()));
			}
			i++;
		}
		RangeChangeEvent.fire(ForumPanel.this, new Range(start, length));
	}

	@Override
	public void setVisibleRange(Range range) {
		setVisibleRange(range.getStart(), range.getLength());
	}

	public ContentDisplayPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(ContentDisplayPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public List<String> getTypeNames() {
		return typeNames;
	}
}
