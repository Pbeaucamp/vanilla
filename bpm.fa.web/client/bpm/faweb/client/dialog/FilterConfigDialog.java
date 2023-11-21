package bpm.faweb.client.dialog;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.panels.navigation.FilterConfigPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.FilterConfigDTO;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FilterConfigDialog extends AbstractDialogBox {

	private static FilterConfigDialogUiBinder uiBinder = GWT.create(FilterConfigDialogUiBinder.class);

	interface FilterConfigDialogUiBinder extends UiBinder<Widget, FilterConfigDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	Label lblName, lblComment, lblSearch, lblSearchResults, lblFilter;

	@UiField
	TextBox txtName, txtComment, txtSearch;

	@UiField
	Image btnSearch;

	@UiField
	ListBox lstSearch, lstFilter;

	@UiField
	Button btnAdd, btnDel;

	private MainPanel mainPanel;
	private FilterConfigPanel configPan;

	public FilterConfigDialog(MainPanel mainPanel, FilterConfigPanel configPan, String dialogName) {
		super(dialogName, false, false);
		this.mainPanel = mainPanel;
		this.configPan = configPan;

		setWidget(uiBinder.createAndBindUi(this));
		
		createButtonBar(FreeAnalysisWeb.LBL.Apply(), confirmHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);

		lblName.setText(FreeAnalysisWeb.LBL.Name());
		lblComment.setText(FreeAnalysisWeb.LBL.Comment());
		lblSearch.setText(FreeAnalysisWeb.LBL.SearchDim());
		lblSearchResults.setText(FreeAnalysisWeb.LBL.SearchDimResult());
		lblFilter.setText(FreeAnalysisWeb.LBL.filter());

		btnSearch.setResource(FaWebImage.INSTANCE.searchsmall());

		btnSearch.setTitle(FreeAnalysisWeb.LBL.SearchDim());

		btnAdd.setText("-->");
		btnDel.setText("<--");

		btnAdd.addClickHandler(handler);
		btnDel.addClickHandler(handler);
		btnSearch.addClickHandler(handler);
	}

	public FilterConfigDialog(MainPanel mainPanel, FilterConfigDTO config, FilterConfigPanel configPan) {
		this(mainPanel, configPan, "Edit the filter configuration");

		txtName.setText(config.getName());
		txtComment.setText(config.getComment());

		for (String filter : config.getFilters()) {
			lstFilter.addItem(filter, filter);
		}
	}
	
	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			mainPanel.showWaitPart(true);

			FilterConfigDTO conf = new FilterConfigDTO();
			conf.setName(txtName.getText());
			conf.setComment(txtComment.getText());
			for (int i = 0; i < lstFilter.getItemCount(); i++) {
				String fil = lstFilter.getValue(i);
				conf.addFilter(fil);
			}

			FaWebService.Connect.getInstance().saveFilterConfig(mainPanel.getKeySession(), conf, true, new AsyncCallback<InfosReport>() {

				@Override
				public void onFailure(Throwable caught) {
					mainPanel.showWaitPart(false);
					configPan.reload();
					FilterConfigDialog.this.hide();
				}

				@Override
				public void onSuccess(InfosReport result) {
					mainPanel.getDisplayPanel().getCubeViewerTab().getFilters().clear();
					mainPanel.getDisplayPanel().getCubeViewerTab().addFilters(result.getWheres());

					mainPanel.setGridFromRCP(result);

					configPan.reload();
					FilterConfigDialog.this.hide();
				}

			});
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			FilterConfigDialog.this.hide();
		}
	};

	private ClickHandler handler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(btnAdd)) {
				for (int i = 0; i < lstSearch.getItemCount(); i++) {
					if (lstSearch.isItemSelected(i)) {
						String fil = lstSearch.getValue(i);
						lstFilter.addItem(fil, fil);
					}
				}
			}
			else if (event.getSource().equals(btnDel)) {
				for (int i = lstFilter.getItemCount() - 1; i > -1; i--) {
					if (lstFilter.isItemSelected(i)) {
						lstFilter.removeItem(i);
					}
				}
			}
			else if (event.getSource().equals(btnSearch)) {
				FaWebService.Connect.getInstance().searchDimensions(mainPanel.getKeySession(), txtSearch.getText(), null, new AsyncCallback<List<String>>() {
					@Override
					public void onSuccess(List<String> result) {
						lstSearch.clear();
						for (String res : result) {
							lstSearch.addItem(res, res);
						}
					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
			}
		}
	};

}
