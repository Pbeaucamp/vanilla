package bpm.gwt.aklabox.commons.client.utils;

import bpm.aklabox.workflow.core.model.resources.FormCell;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CropCellLayer extends Composite {

	private static CropCellLayerUiBinder uiBinder = GWT.create(CropCellLayerUiBinder.class);

	interface CropCellLayerUiBinder extends UiBinder<Widget, CropCellLayer> {
	}
	
	interface MyStyle extends CssResource {
		
	}

	@UiField MyStyle style;
	@UiField Label lblName;
	@UiField HTMLPanel innerPanel;

	private FormCell cell;
	private int[] taille;

	public CropCellLayer(FormCell cell, int[] taille) {
		initWidget(uiBinder.createAndBindUi(this));

		this.cell = cell;
		this.taille = taille;
		
		lblName.setVisible(false);
		this.getElement().getStyle().setTop(((double)cell.getyAxis()/taille[1])*100, Unit.PCT);
		this.getElement().getStyle().setLeft(((double)cell.getxAxis()/taille[0])*100, Unit.PCT);
		
		this.getElement().getStyle().setHeight(((double)cell.getHeight()/taille[1])*100, Unit.PCT);
		this.getElement().getStyle().setWidth(((double)cell.getWidth()/taille[0])*100, Unit.PCT);
		
		
		lblName.setText(cell.getName());
		innerPanel.addDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				lblName.setVisible(true);
			}
		}, MouseOverEvent.getType());
		
		innerPanel.addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				lblName.setVisible(false);
			}
		}, MouseOutEvent.getType());
	}

	
}