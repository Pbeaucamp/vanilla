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

public class ChooseSaveAsFwrOrBirtDialogBox extends AbstractDialogBox {

	private static ChooseSaveAsFwrOrBirtDialogBoxUiBinder uiBinder = GWT.create(ChooseSaveAsFwrOrBirtDialogBoxUiBinder.class);

	interface ChooseSaveAsFwrOrBirtDialogBoxUiBinder extends UiBinder<Widget, ChooseSaveAsFwrOrBirtDialogBox> {
	}
	
	interface MyStyle extends CssResource {
		String img();
		String lblFwr();
		String lblBirt();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	private Image btnSaveAsFwr, btnSaveAsBirt;
	
	public ChooseSaveAsFwrOrBirtDialogBox() {
		super(Bpm_fwr.LBLW.ChooseSaveAs(), false, false);
		
		setWidget(uiBinder.createAndBindUi(this));

		AbsolutePanel rootPanel = new AbsolutePanel();
		
		Label lblSaveAsFwr = new Label(Bpm_fwr.LBLW.SaveAsFwr());
		lblSaveAsFwr.addStyleName(style.lblFwr());
		
		Label lblSaveAsBirt = new Label(Bpm_fwr.LBLW.SaveAsBirt());
		lblSaveAsBirt.addStyleName(style.lblBirt());
		
		btnSaveAsFwr = new Image(WysiwygImage.INSTANCE.SaveAsFWR());
		btnSaveAsFwr.setTitle(Bpm_fwr.LBLW.SaveAsFwr());
		btnSaveAsFwr.addStyleName(style.img());
		btnSaveAsFwr.addClickHandler(btnClickHandler);
		
		btnSaveAsBirt = new Image(WysiwygImage.INSTANCE.SaveAsBirt());
		btnSaveAsBirt.setTitle(Bpm_fwr.LBLW.SaveAsBirt());
		btnSaveAsBirt.addStyleName(style.img());
		btnSaveAsBirt.addClickHandler(btnClickHandler);
		
		
		HorizontalPanel lblPanel = new HorizontalPanel();
		lblPanel.add(lblSaveAsFwr);
		lblPanel.add(lblSaveAsBirt);
		
		HorizontalPanel imgPanel = new HorizontalPanel();
		imgPanel.add(btnSaveAsFwr);
		imgPanel.add(btnSaveAsBirt);

		rootPanel.add(imgPanel);
		rootPanel.add(lblPanel);
		
		contentPanel.add(rootPanel);
	}
	
	private ClickHandler btnClickHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(btnSaveAsFwr)){
				finish(false, null, null);
				ChooseSaveAsFwrOrBirtDialogBox.this.hide();
			}
			else if(event.getSource().equals(btnSaveAsBirt)){
				finish(true, null, null);
				ChooseSaveAsFwrOrBirtDialogBox.this.hide();
			}
		}
	};
}