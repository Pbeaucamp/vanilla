package bpm.faweb.client.panels.center.grid;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.Image;

public class RowButton extends HTML {

	private boolean isSelected = false;

	public RowButton(final MainPanel mainPanel, String txt, final CellFormatter formatter, final int row, final int max, final int colMax) {
		super("<center>" + new Image(FaWebImage.INSTANCE.rowcalc()) + "</center>");
		this.addStyleName("pointer");
		this.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!isSelected) {
					for (int j = 0; j < colMax; j++) {
						formatter.addStyleName(row, j, "pair");
					}
					mainPanel.addSelectedRow(row + "");

					isSelected = !isSelected;
				}
				else {
					for (int j = 0; j < colMax; j++) {
						formatter.removeStyleName(row, j, "pair");
					}
					mainPanel.removeSelectedRow(row + "");

					isSelected = !isSelected;
				}

			}

		});
	}

}
