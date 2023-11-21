package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DashboardCreationDialog extends AbstractDialogBox {

	private static DashboardCreationDialogUiBinder uiBinder = GWT.create(DashboardCreationDialogUiBinder.class);

	interface DashboardCreationDialogUiBinder extends UiBinder<Widget, DashboardCreationDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	SimplePanel panelContent;

	private MainPanel mainCompParent;

	private ListBox lstViews;
	private Image imgView;

	private List<ItemView> views;
	private String previousSelection;

	public DashboardCreationDialog(final MainPanel mainCompParent, List<ItemView> selectedViews) {
		super(FreeAnalysisWeb.LBL.DashboardCreation(), false, true);
		this.mainCompParent = mainCompParent;
		this.views = selectedViews;

		setWidget(uiBinder.createAndBindUi(this));

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100%");

		Label lblTitle = new Label(FreeAnalysisWeb.LBL.SelectViewForDashboard());

		HorizontalPanel viewsPanel = new HorizontalPanel();

		lstViews = new ListBox(true);
		lstViews.setSize("300px", "256px");
		lstViews.getElement().getStyle().setMarginLeft(5, Unit.PX);

		imgView = new Image();
		imgView.setSize("256px", "256px");

		viewsPanel.add(lstViews);
		viewsPanel.add(imgView);

		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(lblTitle);
		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(viewsPanel);

		mainPanel.setCellHorizontalAlignment(lblTitle, HorizontalPanel.ALIGN_CENTER);
		mainPanel.setCellHorizontalAlignment(lstViews, HorizontalPanel.ALIGN_CENTER);

		panelContent.add(mainPanel);

		fillList();

		lstViews.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String selection = lstViews.getValue(lstViews.getSelectedIndex());

				if (previousSelection == null || !previousSelection.equals(selection)) {
					ItemView view = null;
					for (ItemView v : views) {
						if (v.getName().equals(selection)) {
							view = v;
							break;
						}
					}
					imgView.setUrl("rolodexImages/" + view.getImagePath());
				}

			}
		});

		createButtonBar(FreeAnalysisWeb.LBL.Save(), confirmHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);
	}

	private void fillList() {
		for (ItemView view : views) {
			lstViews.addItem(view.getName(), view.getName());
		}
	}

	private List<ItemView> getSelectedViews() {
		List<ItemView> viewsToSave = new ArrayList<ItemView>();
		for (int i = 0; i < lstViews.getItemCount(); i++) {
			if (lstViews.isItemSelected(i)) {
				for (ItemView view : views) {
					if (view.getName().equals(lstViews.getValue(i))) {
						viewsToSave.add(view);
						break;
					}
				}
			}
		}
		return viewsToSave;
	}

	@UiHandler("imgDashboard")
	public void onDashboardClick(ClickEvent event) {
		List<ItemView> items = getSelectedViews();
		if (items.isEmpty()) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), FreeAnalysisWeb.LBL.SelectOneView());
			return;
		}

		mainCompParent.showWaitPart(true);
		
		FaWebService.Connect.getInstance().createDashboard(mainCompParent.getKeySession(), getSelectedViews(), new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				mainCompParent.showWaitPart(false);
				
				Window.open(GWT.getHostPageBaseURL() + "FdGeneratorServlet?keySession=" + mainCompParent.getKeySession(), "Dashboard", "");
			}

			@Override
			public void onFailure(Throwable caught) {
				mainCompParent.showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, "Error");
			}
		});
	}

	public ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			List<ItemView> items = getSelectedViews();
			if (items.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), FreeAnalysisWeb.LBL.SelectOneView());
				return;
			}
			
			mainCompParent.showWaitPart(true);
			
			FaWebService.Connect.getInstance().getRepositories(mainCompParent.getKeySession(), FaWebService.FD, new AsyncCallback<TreeParentDTO>() {

				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					mainCompParent.showWaitPart(false);
				}

				public void onSuccess(TreeParentDTO result) {
					mainCompParent.showWaitPart(false);

					if (result != null) {
						RepositoryContentDialog c = new RepositoryContentDialog(mainCompParent, result, getSelectedViews());
						c.center();
					}
				}

			});
		}
	};

	public ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			DashboardCreationDialog.this.hide();
		}
	};
}
