package bpm.faweb.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.Space;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListSnapshotViewsDialog extends AbstractDialogBox {

	private static ListSnapshotViewsDialogUiBinder uiBinder = GWT.create(ListSnapshotViewsDialogUiBinder.class);

	interface ListSnapshotViewsDialogUiBinder extends UiBinder<Widget, ListSnapshotViewsDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	private MainPanel mainCompPanel;

	private ListBox lstSnap;
	private List<ItemView> views;

	public ListSnapshotViewsDialog(MainPanel mainCompPanel, List<ItemView> viewss) {
		super(FreeAnalysisWeb.LBL.Snapshot(), false, true);
		this.mainCompPanel = mainCompPanel;
		this.views = viewss;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(FreeAnalysisWeb.LBL.Save(), okHandler, FreeAnalysisWeb.LBL.Cancel(), cancelHandler);

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setSize("100%", "100");

		Label lblTitle = new Label(FreeAnalysisWeb.LBL.SelectSnapshotToSave());

		lstSnap = new ListBox(true);
		lstSnap.setSize("350px", "200px");

		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(lblTitle);
		mainPanel.add(new Space("1px", "5px"));
		mainPanel.add(lstSnap);

		mainPanel.setCellHorizontalAlignment(lblTitle, HorizontalPanel.ALIGN_CENTER);
		mainPanel.setCellHorizontalAlignment(lstSnap, HorizontalPanel.ALIGN_CENTER);

		contentPanel.add(mainPanel);

		fillList();
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			final List<ItemView> viewsToSave = new ArrayList<ItemView>();
			for (int i = 0; i < lstSnap.getItemCount(); i++) {
				if (lstSnap.isItemSelected(i)) {
					for (ItemView view : views) {
						if (view.getName().equals(lstSnap.getValue(i))) {
							viewsToSave.add(view);
							break;
						}
					}
				}
			}

			FaWebService.Connect.getInstance().saveSnapshots(mainCompPanel.getKeySession(), viewsToSave, new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {

					List<String> torm = new ArrayList<String>();
					for (ItemView v : viewsToSave) {
						for (String cookName : Cookies.getCookieNames()) {
							String name = "cubeview" + mainCompPanel.getInfosReport().getCubeName() + v.getName();
							if (cookName.equals(name)) {
								torm.add(cookName);
							}
						}
					}
					for (String rm : torm) {
						Cookies.removeCookie(rm);
					}

					ListSnapshotViewsDialog.this.hide();

					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.SnapshotSave(), result);
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ListSnapshotViewsDialog.this.hide();
		}
	};

	private void fillList() {
		for (ItemView view : views) {
			lstSnap.addItem(view.getName(), view.getName());
		}
	}
}
