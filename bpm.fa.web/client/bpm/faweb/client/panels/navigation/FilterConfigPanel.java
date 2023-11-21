package bpm.faweb.client.panels.navigation;

import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.dialog.FilterConfigDialog;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.panels.FilterConfigElement;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.FilterConfigDTO;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class FilterConfigPanel extends Composite {

	private static FilterConfigPanelUiBinder uiBinder = GWT.create(FilterConfigPanelUiBinder.class);

	interface FilterConfigPanelUiBinder extends UiBinder<Widget, FilterConfigPanel> {
	}

	@UiField
	Image btnLoad, btnAdd, btnEdit, btnRemove;

	@UiField
	HTMLPanel configPanel;

	private MainPanel mainPanel;
	private FilterConfigElement actualSelection;

	public FilterConfigPanel(MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		btnLoad.setResource(FaWebImage.INSTANCE.Open());
		btnAdd.setResource(FaWebImage.INSTANCE.filterconfigadd());
		btnEdit.setResource(CommonImages.INSTANCE.edit_24());
		btnRemove.setResource(FaWebImage.INSTANCE.filterconfigdel());

		btnLoad.setTitle(FreeAnalysisWeb.LBL.Open());
		btnAdd.setTitle(FreeAnalysisWeb.LBL.Add());
		btnEdit.setTitle(FreeAnalysisWeb.LBL.Edit());
		btnRemove.setTitle(FreeAnalysisWeb.LBL.Remove());

		btnLoad.addClickHandler(handler);
		btnAdd.addClickHandler(handler);
		btnEdit.addClickHandler(handler);
		btnRemove.addClickHandler(handler);
	}

	private ClickHandler handler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(btnLoad)) {
				FilterConfigDTO config = actualSelection.getFilterDto();
				mainPanel.showWaitPart(true);

				FaWebService.Connect.getInstance().loadFilterConfig(mainPanel.getKeySession(), config, new AsyncCallback<InfosReport>() {
					@Override
					public void onSuccess(InfosReport result) {

						if (result.getFailedUnames() != null && result.getFailedUnames().size() > 0) {
							String filters = "";
							for (String s : result.getFailedUnames()) {
								filters += "\n" + s;
							}
							Window.alert("Those filters couldn't be took in account : " + filters);
						}

						mainPanel.getDisplayPanel().getCubeViewerTab().clearFilters();
						mainPanel.getDisplayPanel().getCubeViewerTab().addFilters(result.getWheres());

						mainPanel.setGridFromRCP(result);

						mainPanel.showWaitPart(false);
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

						mainPanel.showWaitPart(false);
					}
				});
			}
			else if (event.getSource().equals(btnAdd)) {
				FilterConfigDialog dial = new FilterConfigDialog(mainPanel, FilterConfigPanel.this, "Create a filter configuration");
				dial.center();
			}
			else if (event.getSource().equals(btnEdit)) {
				FilterConfigDTO config = actualSelection.getFilterDto();
				FilterConfigDialog dial = new FilterConfigDialog(mainPanel, config, FilterConfigPanel.this);
				dial.center();
			}
			else if (event.getSource().equals(btnRemove)) {
				FilterConfigDTO config = actualSelection.getFilterDto();
				FaWebService.Connect.getInstance().deleteFilterConfig(config, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						mainPanel.showWaitPart(false);
					}

					@Override
					public void onSuccess(Void result) {
						configPanel.remove(actualSelection);
					}
				});
			}
		}
	};

	private HTML createConfigPanel(FilterConfigDTO dto, int index) {

		FilterConfigElement html = new FilterConfigElement(dto, index);
		html.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (actualSelection != null) {
					actualSelection.removeSelectedStyle();
				}
				actualSelection = (FilterConfigElement) event.getSource();
				actualSelection.setSelectedStyle();
			}
		});

		return html;
	}

	public void reload() {
		FaWebService.Connect.getInstance().getFilterConfigs(mainPanel.getKeySession(), new AsyncCallback<List<FilterConfigDTO>>() {
			@Override
			public void onSuccess(List<FilterConfigDTO> result) {
				configPanel.clear();
				int i = 0;
				for (FilterConfigDTO dto : result) {
					configPanel.add(createConfigPanel(dto, i));
					i++;
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

}
