package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.services.CubeServices;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.FaWebFilterHTML;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.infoscube.ItemCube;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchDialog extends AbstractDialogBox {

	private static SearchDialogUiBinder uiBinder = GWT.create(SearchDialogUiBinder.class);

	interface SearchDialogUiBinder extends UiBinder<Widget, SearchDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	private MainPanel parentPanel;
	
	private Image imgSearch;

	private ListBox listDimensions = new ListBox(true);
	private TextBox txtSearch = new TextBox();

	private HorizontalPanel resWaitPanel;

	public SearchDialog(final MainPanel parentPanel) {
		super(FreeAnalysisWeb.LBL.SearchDim(), false, false);
		this.parentPanel = parentPanel;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(FreeAnalysisWeb.LBL.filter(), confirmHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);
		
		VerticalPanel mainPanel = new VerticalPanel();

		imgSearch = new Image(FaWebImage.INSTANCE.searchsmall());
		imgSearch.setTitle("Search");
		imgSearch.addStyleName("pointer");

		Label lblSearch = new Label(FreeAnalysisWeb.LBL.SearchDim());
		Label lblResults = new Label(FreeAnalysisWeb.LBL.SearchDimResult());
		lblSearch.addStyleName("chartLabel");
		lblResults.addStyleName("chartLabel");
		
		resWaitPanel = new HorizontalPanel();
		resWaitPanel.setHeight("25px");
		resWaitPanel.add(lblResults);
		resWaitPanel.add(new Space("10px", "1px"));

		HorizontalPanel searchPanel = new HorizontalPanel();
		searchPanel.add(txtSearch);
		searchPanel.add(imgSearch);
		searchPanel.setWidth("100%");
		searchPanel.setCellWidth(txtSearch, "90%");
		txtSearch.setWidth("100%");
		searchPanel.setCellWidth(imgSearch, "10%");
		searchPanel.setCellHorizontalAlignment(imgSearch, HorizontalPanel.ALIGN_CENTER);

		listDimensions.setSize("100%", "160px");

		mainPanel.add(lblSearch);
		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(searchPanel);
		mainPanel.add(new Space("1px", "10px"));
		// mainPanel.add(lblResults);
		mainPanel.add(resWaitPanel);
		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(listDimensions);

		mainPanel.setSize("400px", "300px");

		contentPanel.add(mainPanel);
		imgSearch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				listDimensions.clear();
				if (!txtSearch.getText().equalsIgnoreCase("")) {
					FaWebService.Connect.getInstance().searchDimensions(parentPanel.getKeySession(), txtSearch.getText(), null, new AsyncCallback<List<String>>() {

						@Override
						public void onSuccess(List<String> result) {
							for (String elem : result) {
								listDimensions.addItem(elem, elem);
							}
							if (result.size() == 0) {
								listDimensions.addItem(FreeAnalysisWeb.LBL.NoResultFound());
							}
							try {
								resWaitPanel.remove(2);
							} catch (Exception e) {
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							try {
								resWaitPanel.remove(2);
							} catch (Exception e) {
							}
						}
					});
				}
			}
		});
	}
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			SearchDialog.this.hide();
		}
	};
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			List<String> filters = new ArrayList<String>();
			for (int i = 0; i < listDimensions.getItemCount(); i++) {
				if (listDimensions.isItemSelected(i)) {
					filters.add(listDimensions.getValue(i));
				}
			}
			try {
				boolean addFilterOk = true;
				String errorMess = "";
				List<String> filtersSelected = filters;
				List<ArrayList<ItemCube>> items = parentPanel.getInfosReport().getGrid().getItems();

				for (ArrayList<ItemCube> itemsLine : items) {
					for (ItemCube item : itemsLine) {
						if (!item.getType().equalsIgnoreCase("ItemNull") && item.getUname() != null) {

							for (String filter : filtersSelected) {
								String[] itemDim = item.getUname().split("\\.");
								String[] filterDim = filter.split("\\.");

//								if (itemDim[0].equalsIgnoreCase(filterDim[0])) {
//									errorMess = FreeAnalysisWeb.LBL.AddFilterDimExistError();
//									addFilterOk = false;
//								}
//
//								else {
									List<FaWebFilterHTML> addedFilters = parentPanel.getDisplayPanel().getCubeViewerTab().getFilters();

									for (FaWebFilterHTML addedFaWebFilter : addedFilters) {
										String addedFilter = addedFaWebFilter.getFilter();
//
//										String[] addedFilterDim = addedFilter.split("\\.");
//
//										if (filterDim.length != addedFilterDim.length) {
//											errorMess = FreeAnalysisWeb.LBL.AddFilterDimLevelError();
//											addFilterOk = false;
//										}
//
//										else {

											if (filter.equalsIgnoreCase(addedFilter)) {
												errorMess = FreeAnalysisWeb.LBL.AddFilterAlreadyExistError();
												addFilterOk = false;

											}
//										}
//									}
								}
							}
						}
					}

				}

				if (addFilterOk) {
					CubeServices.filter(filters, parentPanel, parentPanel.getDisplayPanel().getCubeViewerTab().getFilterPanel());
					// FaWebMainComposite.getInstance().setFilters(filters);
					parentPanel.getSelectedFilters().addAll(filters);
					parentPanel.getNavigationPanel().clearItemSelected();
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), errorMess);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			

			SearchDialog.this.hide();
		}
	};
}
