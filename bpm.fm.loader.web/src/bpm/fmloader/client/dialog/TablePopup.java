package bpm.fmloader.client.dialog;

import bpm.fmloader.client.command.ShowInformationsCommand;
import bpm.fmloader.client.constante.Constantes;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class TablePopup extends PopupPanel {
	
	public TablePopup(int x, int y, Object cell) {
		MenuBar menu = new MenuBar(true);
		
		MenuItem itemCreate = new MenuItem(Constantes.LBL.ValueInformations(), new ShowInformationsCommand(cell, this));
		itemCreate.addStyleName("popupTable-item");
		
		menu.addItem(itemCreate);
		
		menu.setVisible(true);
  		this.setStyleName("popupTable");
  		this.setWidget(menu);
  		this.show();
  		this.setPopupPosition(x, y);  
	}
	
}
