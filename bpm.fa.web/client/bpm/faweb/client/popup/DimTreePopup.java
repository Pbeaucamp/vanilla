package bpm.faweb.client.popup;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.CubeServices;
import bpm.faweb.client.utils.FaWebFilterHTML;
import bpm.faweb.shared.infoscube.ItemCube;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DimTreePopup extends PopupPanel {

	private static DimTreePopupUiBinder uiBinder = GWT.create(DimTreePopupUiBinder.class);

	interface DimTreePopupUiBinder extends UiBinder<Widget, DimTreePopup> {
	}

	@UiField
	HTMLPanel panelMenu;
	
	@UiField
	Label btnAddFilter, btnRemoveFilter;
	
	private MainPanel mainPanel;
	
	private String uname;

	public DimTreePopup(final MainPanel mainPanel, String uname) {
		setWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.uname = uname;
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);

		boolean modify = false;
		for(FaWebFilterHTML filterHtml : mainPanel.getDisplayPanel().getCubeViewerTab().getFilters()) {
			String fil = filterHtml.getFilter();
			if(uname.equals(fil)) {
				modify = true;
				break;
			}
		}
		
		//adding items to the menu
		if(modify) {
			btnAddFilter.removeFromParent();
		}
		else {
			btnRemoveFilter.removeFromParent();
		}
	}
	
	@UiHandler("btnAddFilter")
	public void onAddFilterClick(ClickEvent event) {
		addFilter();
		
		DimTreePopup.this.hide();
	}
	
	@UiHandler("btnRemoveFilter")
	public void onRemoveFilterClick(ClickEvent event) {
		String source = DimTreePopup.this.uname;
		CubeServices.removefilter(source, mainPanel);

		int i=0;
		for(FaWebFilterHTML filterHtml : mainPanel.getDisplayPanel().getCubeViewerTab().getFilters()) {
			String filter = filterHtml.getFilter();
			if(source.equals(filter)) {
				mainPanel.getDisplayPanel().getCubeViewerTab().removeFilter(i);
				break;
			}
			i++;
		}
		
		DimTreePopup.this.hide();
	}

	protected void addFilter() {
		try{
			boolean addFilterOk = true;
			String errorMess = "";
			List<String> filtersSelected = new ArrayList<String>();
			filtersSelected.add(DimTreePopup.this.uname);
			List<ArrayList<ItemCube>> items = mainPanel.getInfosReport().getGrid().getItems();
			
			for(ArrayList<ItemCube> itemsLine : items) {
				for(ItemCube item : itemsLine) {
					if(!item.getType().equalsIgnoreCase("ItemNull") && item.getUname() != null) {
						
						for(String filter : filtersSelected) {
							String[] itemDim = item.getUname().split("\\.");
							String[] filterDim = filter.split("\\.");
							
							if(itemDim[0].equalsIgnoreCase(filterDim[0])) {
								errorMess = FreeAnalysisWeb.LBL.AddFilterDimExistError();
								addFilterOk = false;
							}
							
							else {
								List<FaWebFilterHTML> addedFiltersHtml = mainPanel.getDisplayPanel().getCubeViewerTab().getFilters();
								
								for(FaWebFilterHTML addedFilterHtml : addedFiltersHtml) {
									String addedFilter = addedFilterHtml.getFilter();
									String[] addedFilterDim = addedFilter.split("\\.");
									
									if(filterDim.length != addedFilterDim.length) {
										errorMess = FreeAnalysisWeb.LBL.AddFilterDimLevelError();
										addFilterOk = false;
									}
									
									else {
										
										if(filter.equalsIgnoreCase(addedFilter)) {
											errorMess = FreeAnalysisWeb.LBL.AddFilterAlreadyExistError();
											addFilterOk = false;
											
										}
									}
								}
							}
						}
					}
				}
				
			}
			
			if(addFilterOk) {
				CubeServices.filter(filtersSelected, mainPanel, mainPanel.getDisplayPanel().getCubeViewerTab().getFilterPanel());
				
				mainPanel.getDisplayPanel().getCubeViewerTab().addFilters(filtersSelected);
				
				mainPanel.getSelectedFilters().addAll(filtersSelected);
				mainPanel.getNavigationPanel().clearItemSelected();
			}
			else {
				mainPanel.getNavigationPanel().clearItemSelected();
				
				MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), errorMess);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}				
	}
	
}
