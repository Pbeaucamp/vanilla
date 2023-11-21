package bpm.faweb.client.listeners;

import java.util.ArrayList;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.utils.ForCalcul;
import bpm.faweb.shared.infoscube.Calcul;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class CalculOnColListener implements ClickHandler {
	private int operator;
	private MainPanel mainCompParent;

	public CalculOnColListener(MainPanel mainCompParent, int type) {
		this.mainCompParent = mainCompParent;
		this.operator = type;
	}

	public void onClick(ClickEvent event) {

		if (mainCompParent.getSelectedCol().size() >= 2) {
			Calcul calcul = new Calcul();
			calcul.setOrientation(ForCalcul.ON_COL);
			calcul.setOperator(operator);
			calcul.setFields(mainCompParent.getSelectedCol());
			
			if(mainCompParent.getDisplayPanel().isOverviewVisible()) {
				ForCalcul.associativeCalcul(mainCompParent, calcul, mainCompParent.getDisplayPanel().getOverviewTab().getGridOverview());
				ForCalcul.associativeCalcul(mainCompParent, calcul, mainCompParent.getGrid());
			}
			else {
				ForCalcul.associativeCalcul(mainCompParent, calcul, mainCompParent.getGrid());
			}
			
			mainCompParent.addCalcul(calcul);
			
			mainCompParent.setSelectedCol(new ArrayList<String>());
		}	
	}

}
