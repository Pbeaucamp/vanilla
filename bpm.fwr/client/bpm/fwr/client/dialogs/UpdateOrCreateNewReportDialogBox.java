package bpm.fwr.client.dialogs;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.images.WysiwygImage;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UpdateOrCreateNewReportDialogBox extends AbstractDialogBox {

	private static UpdateOrCreateNewReportDialogBoxUiBinder uiBinder = GWT.create(UpdateOrCreateNewReportDialogBoxUiBinder.class);

	interface UpdateOrCreateNewReportDialogBoxUiBinder extends UiBinder<Widget, UpdateOrCreateNewReportDialogBox> {
	}
	
	interface MyStyle extends CssResource {
		String img();
		String lblNew();
		String lblUpdate();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel contentPanel;

	private Image btnSave, btnUpdate;
	
	public UpdateOrCreateNewReportDialogBox() {
		super(Bpm_fwr.LBLW.SaveOrUpdate(), false, false);
		
		setWidget(uiBinder.createAndBindUi(this));

		AbsolutePanel rootPanel = new AbsolutePanel();

		Label lblSaveAsFwr = new Label(Bpm_fwr.LBLW.CreateNew());
		lblSaveAsFwr.addStyleName(style.lblNew());

		Label lblSaveAsBirt = new Label(Bpm_fwr.LBLW.Update());
		lblSaveAsBirt.addStyleName(style.lblUpdate());

		btnSave = new Image(WysiwygImage.INSTANCE.SaveAsFWR());
		btnSave.setTitle(Bpm_fwr.LBLW.CreateNew());
		btnSave.addStyleName(style.img());
		btnSave.addClickHandler(btnClickHandler);

		btnUpdate = new Image(WysiwygImage.INSTANCE.update());
		btnUpdate.setTitle(Bpm_fwr.LBLW.Update());
		btnUpdate.addStyleName(style.img());
		btnUpdate.addClickHandler(btnClickHandler);

		HorizontalPanel lblPanel = new HorizontalPanel();
		lblPanel.add(lblSaveAsFwr);
		lblPanel.add(lblSaveAsBirt);

		HorizontalPanel imgPanel = new HorizontalPanel();
		imgPanel.add(btnSave);
		imgPanel.add(btnUpdate);

		rootPanel.add(imgPanel);
		rootPanel.add(lblPanel);

		contentPanel.add(rootPanel);
	}

	private ClickHandler btnClickHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(btnSave)) {
				finish(false, null, null);
				UpdateOrCreateNewReportDialogBox.this.hide();
			}
			else if (event.getSource().equals(btnUpdate)) {
				finish(true, null, null);
				UpdateOrCreateNewReportDialogBox.this.hide();
			}
		}
	};
}