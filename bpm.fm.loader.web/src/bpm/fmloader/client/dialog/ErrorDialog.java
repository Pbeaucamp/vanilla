package bpm.fmloader.client.dialog;

import bpm.fmloader.client.constante.Constantes;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorDialog extends DialogBox {

	private PushButton btnOk;
	private VerticalPanel mainPanel = new VerticalPanel();
	private Label lbl;
	
	public ErrorDialog(String errorText) {
		super();
	
		btnOk = new PushButton(Constantes.images.apply().createImage(),Constantes.images.apply().createImage());
		btnOk.setTitle(Constantes.LBL.btnApply());
		btnOk.setSize("32px", "32px");
		
		lbl = new Label(errorText);
		
		mainPanel.setSize("300px", "200px");
		mainPanel.add(lbl);
		mainPanel.add(btnOk);
		
		this.setWidget(mainPanel);
		this.setSize("300px", "200px");
		
		btnOk.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				ErrorDialog.this.hide();
			}
		});
	}

	
	
}
