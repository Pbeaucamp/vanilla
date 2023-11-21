package bpm.vanilla.portal.client.panels.center;

import java.util.List;

import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.vanilla.portal.client.panels.center.search.ResultPanel;
import bpm.vanilla.portal.client.panels.center.search.SearchPanel;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchManagerPanel extends Tab {

	private static SearchManagerPanelUiBinder uiBinder = GWT.create(SearchManagerPanelUiBinder.class);

	interface SearchManagerPanelUiBinder extends UiBinder<Widget, SearchManagerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelSearch, panelResult;
	
	@UiField
	Image btnLeft, btnRight;

	private SearchPanel pageSearch;
	private ResultPanel pageResult;
	
	private int windowClientWidth;

	public SearchManagerPanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.GedTab(), true);
		this.add(uiBinder.createAndBindUi(this));

		pageSearch = new SearchPanel(this);
		panelSearch.setWidget(pageSearch);
		
		pageResult = new ResultPanel(this);
		panelResult.setWidget(pageResult);

		windowClientWidth = Window.getClientWidth() + 50;
		setLeftToPanel(0, windowClientWidth);
		
		btnLeft.setVisible(false);
		
		this.addStyleName(style.mainPanel());
	}
	
	private void setLeftToPanel(int searchLeft, int resultLeft) {
		panelSearch.getElement().getStyle().setLeft(searchLeft, Unit.PX);
		panelResult.getElement().getStyle().setLeft(resultLeft, Unit.PX);
	}
	
	public void loadResult(List<DocumentVersionDTO> documents){
		pageResult.setDocuments(documents);
		changeCurrentPage(true);
	}
	
	private void changeCurrentPage(final boolean goToResult){
		if(goToResult){
			pageResult.onPageLoad();
			btnLeft.setVisible(true);
			btnRight.setVisible(false);
			
			setLeftToPanel(-windowClientWidth, 0);
		}
		else {
			btnLeft.setVisible(false);
			btnRight.setVisible(true);
			
			setLeftToPanel(0, windowClientWidth);
		}
	}
	
	@UiHandler("btnLeft")
	public void onSearchClick(ClickEvent event) {
		changeCurrentPage(false);
	}
	
	@UiHandler("btnRight")
	public void onResultClick(ClickEvent event) {
		changeCurrentPage(true);
	}
}
