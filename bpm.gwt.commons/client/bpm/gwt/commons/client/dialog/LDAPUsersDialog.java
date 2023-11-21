package bpm.gwt.commons.client.dialog;


import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class LDAPUsersDialog extends AbstractDialogBox {

	private static LDAPUsersDialogUiBinder uiBinder = GWT.create(LDAPUsersDialogUiBinder.class);

	interface LDAPUsersDialogUiBinder extends UiBinder<Widget, LDAPUsersDialog> {
	}

	interface MyStyle extends CssResource {
		String panelButton();

		String btn();
	}

	@UiField
	MyStyle style;

	@UiField
	CaptionPanel captionPanel;

	@UiField
	LabelTextBox txtUrl, txtPass, txtBase;

	@UiField
	ScrollPanel scrollPanel;

	@UiField
	HTMLPanel radioButtons, addPanelButton, removePanelButton;

	@UiField
	ListBox lstBases;
	
	public LDAPUsersDialog() {
		super(LabelsConstants.lblCnst.LDAPUsers(), false, true);
		setWidget(uiBinder.createAndBindUi(this));


		captionPanel = new CaptionPanel();
		scrollPanel = new ScrollPanel(captionPanel);

		RadioButton aklabox = new RadioButton("radioGrp","Aklabox");
		RadioButton vanilla = new RadioButton("radioGrp","Vanilla");
		aklabox.setChecked(true);

		radioButtons.add(aklabox);
		radioButtons.add(vanilla);

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		addBtn();
		removeBtn();
		removePanelButton.setVisible(false);
		
		lstBases.setVisibleItemCount(5);
		lstBases.setMultipleSelect(true);
	}


	public void removeBtn() {
		Button btn = new Button("Remove");
		btn.addStyleName(style.btn());
		btn.addClickHandler(deleteHandler);
		
		removePanelButton.add(btn);
		removePanelButton.setStyleName(style.panelButton());
	}
	public void addBtn() {
		Button btn = new Button("Add");
		btn.addStyleName(style.btn());
		btn.addClickHandler(addHandler);

		addPanelButton.add(btn);
		addPanelButton.setStyleName(style.panelButton());
	}
	private ClickHandler deleteHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			
			for(int i=0; i<lstBases.getItemCount(); i++) {
				if(lstBases.isItemSelected(i)) {
					lstBases.removeItem(i);
				}
			}
			
			if(lstBases.getItemCount()<=0) {
				removePanelButton.setVisible(false);
			}
		}
	};
	
	private ClickHandler addHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if(!txtBase.getText().equals("")){
				lstBases.addItem(txtBase.getText());
				txtBase.setText("");
				removePanelButton.setVisible(true);
			}
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {

		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

}
