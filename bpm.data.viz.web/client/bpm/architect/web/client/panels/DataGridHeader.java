package bpm.architect.web.client.panels;

import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class DataGridHeader extends HTMLPanel{

	public DataGridHeader(DataColumn col) {
		super("");
		Label lbl = new Label(col.getColumnLabel());
		lbl.setWidth("80%");
		lbl.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		add(lbl);
		Button btn = new Button("...");
		btn.setWidth("20%");
		btn.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		add(btn);
		
		btn.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	
	
}
