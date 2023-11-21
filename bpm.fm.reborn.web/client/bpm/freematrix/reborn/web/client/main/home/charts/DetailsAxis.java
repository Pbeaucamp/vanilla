package bpm.freematrix.reborn.web.client.main.home.charts;

import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DetailsAxis extends Composite  {

	private static DetailsAxisUiBinder uiBinder = GWT
			.create(DetailsAxisUiBinder.class);

	interface DetailsAxisUiBinder extends UiBinder<Widget, DetailsAxis> {
	}
	
	@UiField Label lblAxisName;
	@UiField HTMLPanel childPanel;
	
	private Label lblChild;
	
	public DetailsAxis(FactTableAxis f) {
		initWidget(uiBinder.createAndBindUi(this));
		lblAxisName.setText(f.getAxis().getName());
		
		for(Level l : f.getAxis().getChildren()){
			lblChild = new Label(l.getName());
			lblChild.setStyleName("lblLabel");
			childPanel.add(lblChild);
		}
	}

	

}
