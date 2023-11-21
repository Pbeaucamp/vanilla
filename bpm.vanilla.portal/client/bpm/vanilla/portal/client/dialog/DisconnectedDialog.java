package bpm.vanilla.portal.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DisconnectedDialog extends AbstractDialogBox {

	private static DisconnectedDialogUiBinder uiBinder = GWT.create(DisconnectedDialogUiBinder.class);

	interface DisconnectedDialogUiBinder extends UiBinder<Widget, DisconnectedDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	TextBox txtName, txtLimitRows;

	private List<RepositoryItem> items;

	public DisconnectedDialog(PortailRepositoryDirectory directory) {
		super(ToolsGWT.lblCnst.Disconnected(), false, true);
		this.items = checkItemsToPack(directory.getItems());
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(ToolsGWT.lblCnst.Ok(), okHandler, ToolsGWT.lblCnst.Cancel(), cancelHandler);
	}

	public DisconnectedDialog(PortailRepositoryItem item) {
		super(ToolsGWT.lblCnst.Disconnected(), false, true);
		
		List<IRepositoryObject> items = new ArrayList<IRepositoryObject>();
		items.add(item);
		this.items = checkItemsToPack(items);
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(ToolsGWT.lblCnst.Ok(), okHandler, ToolsGWT.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			DisconnectedDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String packageName = txtName.getText();
			int limitRows = 1000;
			try {
				limitRows = Integer.parseInt(txtLimitRows.getText());
			} catch (Exception e) {
			}

			if (packageName != null && !packageName.isEmpty()) {

				biPortal.get().showWaitPart(true);

				BiPortalService.Connect.getInstance().createDisconnectedPackage(packageName, limitRows, items, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						DisconnectedDialog.this.hide();
						
						biPortal.get().showWaitPart(false);
						
						ExceptionManager.getInstance().handleException(caught, "The package cannot be created.");
					}

					@Override
					public void onSuccess(String result) {
						DisconnectedDialog.this.hide();
						
						biPortal.get().showWaitPart(false);
						
						String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET 
								+ "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + result + "&" + CommonConstants.REPORT_OUTPUT + "=" + "zip";
						ToolsGWT.doRedirect(fullUrl);
					}
				});
			}
			else {
				MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.NameMustBeFill());
			}
		}
	};

	private List<RepositoryItem> checkItemsToPack(List<IRepositoryObject> items) {
		List<RepositoryItem> itemsToPack = new ArrayList<RepositoryItem>();
		for (IRepositoryObject obj : items) {
			if (obj instanceof PortailRepositoryItem) {
				PortailRepositoryItem item = (PortailRepositoryItem) obj;
				if ((item.getType() == IRepositoryApi.CUST_TYPE && item.getSubType() == IRepositoryApi.BIRT_REPORT_SUBTYPE) 
						|| item.getType() == IRepositoryApi.FD_TYPE 
						|| item.getType() == IRepositoryApi.FD_DICO_TYPE 
						|| item.getType() == IRepositoryApi.FASD_TYPE) {
					itemsToPack.add(item.getItem());
				}
			}
		}
		return itemsToPack;
	}
}
