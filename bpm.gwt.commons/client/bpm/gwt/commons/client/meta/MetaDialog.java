package bpm.gwt.commons.client.meta;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.VanillaServerInformations;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;

public class MetaDialog extends AbstractDialogBox {

	private static CmisDialogUiBinder uiBinder = GWT.create(CmisDialogUiBinder.class);

	interface CmisDialogUiBinder extends UiBinder<Widget, MetaDialog> {
	}

	@UiField
	HTMLPanel mainPanel;
	
	private VanillaServerInformations server;
	private Integer itemId;
	private TypeMetaLink type;

	private boolean saveOnConfirm;
	private boolean confirm;
	
	private MetasPanel metasPanel;

	public MetaDialog(VanillaServerInformations server, int itemId, TypeMetaLink type, int formId, boolean reloadWithForm, boolean saveOnConfirm) {
		super(LabelsConstants.lblCnst.Meta(), true, true);
		this.server = server;
		this.itemId = itemId;
		this.type = type;
		this.saveOnConfirm = saveOnConfirm;
		
		buildDialog();
		
		this.metasPanel = new MetasPanel(this, server, itemId, type, formId, reloadWithForm);
		mainPanel.add(metasPanel);
	}

	public MetaDialog(VanillaServerInformations server, int formId) {
		super(LabelsConstants.lblCnst.Meta(), true, true);
		this.server = server;
		buildDialog();
		
		this.metasPanel = new MetasPanel(this, server, formId);
		mainPanel.add(metasPanel);
	}
	
	private void buildDialog() {
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (saveOnConfirm && itemId != null && type != null) {
				saveMetas(server, itemId, type, getMetaLinks());
			}
			else {
				confirm = true;
				hide();
			}
		}
	};
	


	private void saveMetas(VanillaServerInformations server, int itemId, TypeMetaLink type, List<MetaLink> metas) {
		// We set the saved informations
		for (MetaLink meta : metas) {
			meta.setItemId(itemId);
			meta.setType(type);
		}
		
		CommonService.Connect.getInstance().manageMetaValues(server, metas, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				confirm = true;
				hide();
			}

		}.getAsyncCallback());
	}

	public boolean isConfirm() {
		return confirm;
	}
	
	public List<MetaLink> getMetaLinks() {
		return metasPanel.getMetaLinks();
	}
}
