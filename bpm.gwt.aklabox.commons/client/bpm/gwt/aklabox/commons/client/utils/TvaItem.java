package bpm.gwt.aklabox.commons.client.utils;

import java.util.Arrays;

import bpm.document.management.core.model.Chorus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TvaItem extends Composite {

	private static CellTableItemUiBinder uiBinder = GWT.create(CellTableItemUiBinder.class);

	interface CellTableItemUiBinder extends UiBinder<Widget, TvaItem> {
	}

	@UiField
	Label lblTTC, btnRemove;
	@UiField
	ListBox lstTvas;
	@UiField
	TextBox txtMontantTva, txtHT;
	
	private String rowtva;
	private Double taux;
	private Double tva;
	private Double ht;
	private Double ttc;
	private TvaInterface manager;
	
	public TvaItem(TvaInterface manager, String rowtva) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rowtva = rowtva;
		this.manager = manager;
		if(rowtva != null){
			taux = Double.parseDouble(rowtva.split("\\$")[0]);
			ht = Double.parseDouble(rowtva.split("\\$")[1]);
			tva = Double.parseDouble(rowtva.split("\\$")[2]);
			ttc = Double.parseDouble(rowtva.split("\\$")[3]);
		} else {
			taux = 0.0;
			tva = 0.0;
			ht = 0.0;
			ttc = 0.0;
		}
		
		initListTaux();
		
		updateValues();
		
		/*txtHT.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(txtHT.getText().isEmpty()){
					onChangeHT(null);
					txtMontantTva.setFocus(true);
				}
			}
		});*/
	}
	
	private void updateValues(){
		txtMontantTva.setText(String.valueOf(tva));
		txtHT.setText(String.valueOf(ht));
		lblTTC.setText(String.valueOf(ttc));
	}

	public TvaItem(TvaInterface manager) {
		this(manager, null);
	}
	
	private void initListTaux() {
		for(String t : Chorus.P_CPP_TVAS){
			lstTvas.addItem(t);
			if(taux == Double.parseDouble(t)){
				lstTvas.setSelectedIndex(Arrays.asList(Chorus.P_CPP_TVAS).indexOf(t));
			}
		}
		
	}
	
	@UiHandler("btnRemove")
	void onRemoveCell(ClickEvent e){
		this.removeFromParent();
		manager.onTvaChange();
	}
	
	@UiHandler("txtMontantTva")
	public void onChangeTvaMontant(ChangeEvent e){
		try {
			tva = Double.parseDouble(txtMontantTva.getText());
			
		} catch (Exception e2) {
			tva = 0.0;
		}
		ht = (double) ((double)Math.round((tva / taux * 100) * 100) / 100);
		ttc = (double) ((double)Math.round((ht + tva) * 100) / 100);
		updateValues();
		manager.onTvaChange();
	}
	
	@UiHandler("txtHT")
	public void onChangeHT(ChangeEvent e){
		try {
			ht = Double.parseDouble(txtHT.getText());
		} catch (Exception e2) {
			ht = 0.0;
		}
		tva = (double) ((double)Math.round((ht * taux / 100) * 100) / 100);
		ttc = (double) ((double)Math.round((ht + tva) * 100) / 100);
		updateValues();
		manager.onTvaChange();
	}
	
	@UiHandler("lstTvas")
	public void onChangeTvaTaux(ChangeEvent e){
		try {
			taux = Double.parseDouble(lstTvas.getValue(lstTvas.getSelectedIndex()));
			ht = (double) ((double)Math.round((tva / taux * 100) * 100) / 100);
			ttc = (double) ((double)Math.round((ht + tva) * 100) / 100);
			updateValues();
			manager.onTvaChange();
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	public String getRowTva(){
		return taux + "$" + ht + "$" + tva + "$" + ttc;
	}

	public Double getTva() {
		return tva;
	}

	public Double getHt() {
		return ht;
	}

	public Double getTtc() {
		return ttc;
	}
	
	
}
