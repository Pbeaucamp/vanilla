package bpm.map.viewer.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.panel.viewer.MapViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ManageDataObservatory extends AbstractDialogBox {
	private static ManageDataObservatoryUiBinder uiBinder = GWT
			.create(ManageDataObservatoryUiBinder.class);

	interface ManageDataObservatoryUiBinder extends
			UiBinder<Widget, ManageDataObservatory> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	TextBox txtName, txtAuthor;
	
	@UiField
	TextArea txtDescriptif;
	
	@UiField
	Label lblName, lblAuthor, lblDescriptif;

	@UiField
	MyStyle style;

	private MapViewer parent;


	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	public ManageDataObservatory(MapViewer parent) {
		super("Informations observatoire", false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		
		this.lblName.setText(lblCnst.Name());
		this.lblAuthor.setText(lblCnst.Author());
		this.lblDescriptif.setText(lblCnst.Description());
		
		createButtonBar(lblCnst.Ok(), okHandler, lblCnst.Cancel(), cancelHandler);
	}
	
	public String getName() {
		return txtName.getText();
	}

	public void setName(String name) {
		txtName.setText(name);
	}
	
	public String getAuthor() {
		return txtAuthor.getText();
	}

	public void setAuthor(String author) {
		txtAuthor.setText(author);
	}
	
	public String getDescriptif() {
		return txtDescriptif.getText();
	}

	public void setDescriptif(String descriptif) {
		txtDescriptif.setText(descriptif);
	}
	
	@UiHandler("txtName")
	public void onNameChange(ValueChangeEvent<String> event) {
		
	}
	
	@UiHandler("txtAuthor")
	public void onAuthorChange(ValueChangeEvent<String> event) {
		
	}
	
	@UiHandler("txtDescriptif")
	public void onDescriptifChange(ValueChangeEvent<String> event) {
		
	}
	
	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(txtAuthor.getText().equals("") || txtDescriptif.getText().equals("") || txtName.getText().equals("")) {
				return;
			} else {
				ManageDataObservatory.this.hide();
			}
			
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ManageDataObservatory.this.hide();
		}
	};
}
