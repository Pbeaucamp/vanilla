package bpm.faweb.client.panels.center.grid;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.Image;

public class ColButton extends HTML {

	private boolean isSelected = false;

	public ColButton(final MainPanel mainPanel, String txt, final CellFormatter formatter, final int col, final int max, final int maxRow) {
		super("<center>" + new Image(FaWebImage.INSTANCE.colscalc()) + "</center>");
		this.addStyleName("pointer");
		this.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				if (!isSelected) {
					for (int i = 0; i < maxRow; i++) {
						formatter.setStyleName(i, col, "pair");
					}

					mainPanel.addSelectedCol(col + "");
				}
				else {
					for (int i = 0; i < maxRow; i++) {
						formatter.removeStyleName(i, col, "pair");
					}
					mainPanel.removeSelectedCol(col + "");
				}

				isSelected = !isSelected;
			}

		});
	}

}
