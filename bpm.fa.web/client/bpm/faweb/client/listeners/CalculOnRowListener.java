package bpm.faweb.client.listeners;

import java.util.ArrayList;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.utils.ForCalcul;
import bpm.faweb.shared.infoscube.Calcul;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class CalculOnRowListener implements ClickHandler{
	private MainPanel mainCompParent;
	private int operator;


	public CalculOnRowListener(MainPanel mainCompParent, int type) {
		this.mainCompParent = mainCompParent;
		this.operator = type;
	}
	
	public void onClick(ClickEvent event) {
		
		if (mainCompParent.getSelectedRow().size() >= 2) {
			Calcul calcul = new Calcul();
			calcul.setOrientation(ForCalcul.ON_ROW);
			calcul.setOperator(operator);
			calcul.setFields(mainCompParent.getSelectedRow());
			
			if(mainCompParent.getDisplayPanel().isOverviewVisible()) {
				ForCalcul.associativeCalcul(mainCompParent, calcul, mainCompParent.getDisplayPanel().getOverviewTab().getGridOverview());
				ForCalcul.associativeCalcul(mainCompParent, calcul, mainCompParent.getGrid());
			}
			else {
				ForCalcul.associativeCalcul(mainCompParent, calcul, mainCompParent.getGrid());
			}
			
			mainCompParent.addCalcul(calcul);
			
			mainCompParent.setSelectedRow(new ArrayList<String>());
		}	
	}

}
