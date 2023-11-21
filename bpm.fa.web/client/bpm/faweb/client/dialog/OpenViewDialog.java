package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.history.ModificationHistory;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.ParameterDTO;
import bpm.faweb.shared.infoscube.ChartInfos;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomDatagrid;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class OpenViewDialog extends AbstractDialogBox {

	private static OpenViewDialogUiBinder uiBinder = GWT.create(OpenViewDialogUiBinder.class);

	interface OpenViewDialogUiBinder extends UiBinder<Widget, OpenViewDialog> {
	}

	interface MyStyle extends CssResource {
		String imgView();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	@UiField
	TextBox txtSearch;

	@UiField
	Image imgView, btnClear;

	private CustomDatagrid<ItemView> gridCube;
	private SingleSelectionModel<ItemView> selectionModel;

	private MainPanel mainCompPanel;
	private List<ItemView> views;

	public OpenViewDialog(MainPanel mainCompPanel, List<ItemView> views) {
		super(FreeAnalysisWeb.LBL.OpenView(), false, true);
		this.mainCompPanel = mainCompPanel;
		this.views = views;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		selectionModel = new SingleSelectionModel<ItemView>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		gridCube = new CustomDatagrid<ItemView>(selectionModel, 200, FreeAnalysisWeb.LBL.NoView(), FreeAnalysisWeb.LBL.SelView());

		panelGrid.setWidget(gridCube);

		fillViews(views, "");
		clearSearch();
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		if (!txtSearch.getText().isEmpty()) {
			btnClear.setVisible(true);

			fillViews(views, txtSearch.getText());
		}
	}

	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		clearSearch();

		fillViews(views, txtSearch.getText());
	}

	private void clearSearch() {
		txtSearch.setText("");

		btnClear.setVisible(false);
	}

	private void fillViews(List<ItemView> views, String search) {
		List<ItemView> viewsToDisplay = new ArrayList<ItemView>();
		if (search != null && !search.isEmpty()) {
			if (views != null) {
				for (ItemView view : views) {
					if (view.getName().toLowerCase().contains(search.toLowerCase())) {
						viewsToDisplay.add(view);
					}
				}
			}
		}
		else {
			if (views != null) {
				viewsToDisplay.addAll(views);
			}
		}

		gridCube.loadItems(viewsToDisplay);
	}

	private Handler selectionChangeHandler = new Handler() {
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			ItemView view = selectionModel.getSelectedObject();

			imgView.setUrl(GWT.getHostPageBaseURL() + "rolodexImages" + view.getImagePath());
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final ItemView view = selectionModel.getSelectedObject();

			if (view == null) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), FreeAnalysisWeb.LBL.NeedSelectView());
				return;
			}

			mainCompPanel.getInfosReport().setReportName(view.getName());
			mainCompPanel.showWaitPart(true);

			FaWebService.Connect.getInstance().getParametersForView(mainCompPanel.getKeySession(), view.getName(), "", 0, false, new AsyncCallback<HashMap<ParameterDTO, List<String>>>() {

				@Override
				public void onSuccess(HashMap<ParameterDTO, List<String>> result) {
					mainCompPanel.showWaitPart(false);
					final HashMap<String, String> params = new HashMap<String, String>();
					if (result != null && result.size() > 0) {
						final List<ParameterDTO> temp = new ArrayList<ParameterDTO>();
						temp.addAll(result.keySet());
						for (ParameterDTO param : result.keySet()) {
							final PromptDialog dial = new PromptDialog(mainCompPanel, param, params, result);
							dial.center();
							dial.addCloseHandler(new CloseHandler<PopupPanel>() {
								public void onClose(CloseEvent<PopupPanel> event) {
									temp.remove(dial.getParameter());
									if (temp.isEmpty()) {
										open(view.getKey(), params, mainCompPanel);
									}
								}
							});
						}

					}
					else {
						open(view.getKey(), params, mainCompPanel);
					}

					hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					mainCompPanel.showWaitPart(false);
				}
			});
		}
	};

	public void open(String viewKey, HashMap<String, String> parameters, final MainPanel mainCompParent) {
		mainCompParent.showWaitPart(true);
		
		FaWebService.Connect.getInstance().loadSavedView(mainCompPanel.getKeySession(), viewKey, parameters, new AsyncCallback<InfosReport>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				mainCompParent.showWaitPart(false);
			}

			public void onSuccess(InfosReport result) {
				mainCompParent.showWaitPart(false);
				
				try {
					mainCompParent.setMh(new ModificationHistory(mainCompParent));
				} catch (Exception e) {
					e.printStackTrace();
				}

				mainCompParent.setGridFromRCP(result);

				ChartInfos chart = result.getChartInfos();
				if (chart != null) {
					final List<String> groups = chart.getChartGroups();
					final List<String> datas = chart.getChartDatas();
					final List<String> filters = chart.getChartFilters();
					final String measureRecup = chart.getMeasure();

					final String title = chart.getTitle();
					final String chartType = chart.getType();
					final String graphe = chart.getRenderer();

					if ((groups != null && !groups.isEmpty()) || (datas != null && !datas.isEmpty())) {
						FaWebService.Connect.getInstance().executeQuery(mainCompPanel.getKeySession(), groups, datas, filters, measureRecup, new AsyncCallback<List<GroupChart>>() {

							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							public void onSuccess(List<GroupChart> groupsChart) {
								if (groupsChart != null && !groupsChart.isEmpty()) {
									mainCompParent.openChartAfterLoad(title, chartType, graphe, datas, groups, filters, measureRecup, groupsChart);
								}
							}
						});
					}
				}

				mainCompParent.getDisplayPanel().getCubeViewerTab().clearFilters();

				if (mainCompParent.getInfosReport().getParameters() != null && mainCompParent.getInfosReport().getParameters().size() > 0) {
					List<String> params = new ArrayList<String>();
					for (ParameterDTO param : mainCompParent.getInfosReport().getParameters()) {
						params.add(param.getValue());
					}
					mainCompParent.getDisplayPanel().getCubeViewerTab().addParams(params);
				}

				if (mainCompParent.getInfosReport().getWheres() != null && mainCompParent.getInfosReport().getWheres().size() > 0) {
					mainCompParent.getDisplayPanel().getCubeViewerTab().addParams(mainCompParent.getInfosReport().getWheres());
				}
				
				MapOptions mapOptions = result.getMapOptions();
				if (mapOptions != null) {
					mainCompParent.setMapOptions(mapOptions);
					
					String uname = mapOptions.getUname();
					String element = mapOptions.getElement();
					
					mainCompParent.getDisplayPanel().chooseMap(uname, element, true);
				}
			}
		});
	}
}
