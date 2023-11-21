package bpm.fmloader.client.command;

import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.dialog.CompteurValueInformationsDialog;
import bpm.fmloader.client.dialog.TablePopup;
import bpm.fmloader.client.dialog.ValueInformationsDialog;
import bpm.fmloader.client.dto.IndicatorValuesDTO;
import bpm.fmloader.client.dto.ValuesDTO;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.table.value.ValueCellCompteur;
import bpm.fmloader.client.table.value.ValueCellIndicateur;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ShowInformationsCommand implements Command {

	private Object cell;
	private TablePopup popup;
	
	public ShowInformationsCommand(Object cell, TablePopup popup) {
		this.cell = cell;
		this.popup = popup;
	}
	
	public void execute() {
		
		//TODO
		
//		final String filename = "evochart" + new Object().hashCode() + ".jsp";
//		if(cell instanceof ValueCellIndicateur) {
//			Constantes.DATAS_SERVICES.getValueInformations((IndicatorValuesDTO) ((ValueCellIndicateur) cell).getValue(), filename, GWT.getHostPageBaseURL(), InfosUser.getInstance().getSelectedDate(), new AsyncCallback<IndicatorValuesDTO>() {
//				public void onSuccess(IndicatorValuesDTO result) {
//					ShowInformationsCommand.this.popup.hide();
//					ValueInformationsDialog dial = new ValueInformationsDialog((IndicatorValuesDTO) ((ValueCellIndicateur) cell).getValue(), result, filename);
//					dial.setGlassEnabled(true);
//					dial.center();
//					dial.createFrame();
//				}
//				
//				public void onFailure(Throwable caught) {
//					
//				}
//			});
//		}
//		else {
//			Constantes.DATAS_SERVICES.getCompteurValueInformations((ValuesDTO) ((ValueCellCompteur) cell).getValue(), filename, GWT.getHostPageBaseURL(), InfosUser.getInstance().getSelectedDate(), new AsyncCallback<ValuesDTO>() {
//				public void onSuccess(ValuesDTO result) {
//					ShowInformationsCommand.this.popup.hide();
//					CompteurValueInformationsDialog dial = new CompteurValueInformationsDialog((ValuesDTO) ((ValueCellCompteur) cell).getValue(), result, filename);
//					dial.setGlassEnabled(true);
//					dial.center();
//					dial.createFrame();
//				}
//				
//				public void onFailure(Throwable caught) {
//					
//				}
//			});
//		}
	}

}
