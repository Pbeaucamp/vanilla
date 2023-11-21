package bpm.es.web.client.dialogs;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.images.Images;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.shared.InfoUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LastConnectionDialog extends AbstractDialogBox {
	
	private DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");

	private static UserDialogUiBinder uiBinder = GWT.create(UserDialogUiBinder.class);

	interface UserDialogUiBinder extends UiBinder<Widget, LastConnectionDialog> {
	}
	
	@UiField
	Image imgUser;
	
	@UiField
	HTML lblUser;
	
	@UiField
	Label lblLastConnection;
	
	public LastConnectionDialog(InfoUser infoUser) {
		super(Labels.lblCnst.Welcome(), true, true);

		setWidget(uiBinder.createAndBindUi(this));

		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
		
		imgUser.setResource(Images.INSTANCE.img_profile_small());
		lblUser.setHTML(Labels.lblCnst.Welcome() + ", <b>" + infoUser.getUser().getSurname() + " " + infoUser.getUser().getName() + "</b>");
		
		lblLastConnection.setText(infoUser.getUserLastConnectionDate() != null ? dtf.format(infoUser.getUserLastConnectionDate()) : Labels.lblCnst.Unknown());
	}

	private ClickHandler closeHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
