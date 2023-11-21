package bpm.fasd.ui.measure.definition.listener;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.fasd.olap.FAModel;

import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;

public class ModifyListenerNotifier implements ModifyListener {

	private MeasureDefinitionDialog dial;
	
	public ModifyListenerNotifier(MeasureDefinitionDialog parent) {
		dial = parent;
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		if(dial != null) {
			dial.updateButtonsState();
		}
	}

	public FAModel getModel() {
		if(dial == null) {
			return null;
		}
		return dial.getFaModel();
	}
}
