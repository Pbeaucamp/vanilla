package bpm.faweb.client.panels.center.grid;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.ItemCube;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;

public class FaWebLabelElement extends Label {

	private MainPanel mainPanel;

	public static boolean mousup = true;
	public static boolean dragging = false;

	private int row;
	private int col;
	private static int startRow = -1;
	private static int startCol = -1;

	public FaWebLabelElement(MainPanel mainPanel, String label, int i, int j) {
		super(label);
		this.mainPanel = mainPanel;
		this.row = i;
		this.col = j;
		this.setWordWrap(false);

		disableContextMenu(getElement());
		setselectedhighlight(getElement());

		sinkEvents(Event.ONMOUSEOUT | Event.ONMOUSEOVER | Event.ONMOUSEUP | Event.ONDBLCLICK | Event.BUTTON_RIGHT | Event.BUTTON_LEFT | Event.ONMOUSEDOWN | Event.ONMOUSEMOVE);
	}

	public void onBrowserEvent(Event event) {
		int mouseX;

		switch (DOM.eventGetType(event)) {
		case Event.ONDBLCLICK:
			mainPanel.showWaitPart(true);
			if (isDrillable()) {
				mainPanel.drill(row, col, null);
			}
			else {
				MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "You're not allowed to drill here");
				mainPanel.showWaitPart(false);
			}
			break;

		case Event.ONMOUSEOVER:
			this.addStyleName("ItemElementOver");
			break;

		case Event.ONMOUSEOUT:
			this.removeStyleName("ItemElementOver");
			if (!mousup) {
				dragging = true;
			}
			if (dragging && startRow == -1) {
				startRow = row;
				startCol = col;
			}
			if (dragging) {
				mainPanel.addStyleName("dragging");
			}
			Timer t = new Timer() {
				public void run() {

					if (mousup) {
						dragging = false;
						mainPanel.removeStyleName("dragging");
					}
				}
			};
			t.schedule(10);
			break;

		case Event.ONMOUSEDOWN:
			mousup = false;

			break;

		case Event.ONMOUSEMOVE:
			mouseX = DOM.eventGetClientX(event);

			if (dragging && startRow != -1) {
				int width = this.getOffsetWidth();
				int startX = this.getAbsoluteLeft();

				if (mouseX < startX + width / 2) {
					mainPanel.setBefore(true);
					mainPanel.removeStyleName("dragging");
					this.addStyleName("ItemExtremite");
				}
				else if (mouseX > startX - width / 2) {
					mainPanel.setBefore(false);
					mainPanel.removeStyleName("dragging");
					this.addStyleName("ItemExtremite");
				}
				else {
					this.removeStyleName("ItemExtremite");
					mainPanel.addStyleName("dragging");
				}
			}

			break;

		case Event.ONMOUSEUP:
			mousup = true;

			switch (DOM.eventGetButton(event)) {

			case Event.BUTTON_LEFT:
				if (dragging) {
					mainPanel.showWaitPart(true);
					if (movePossible()) {
						mainPanel.move(startRow, startCol, row, col, mainPanel.isBefore());
					}
					else {
						this.removeStyleName("ItemElementOver");

						MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "You're not allowed to drop it here");
						mainPanel.showWaitPart(false);
					}
					dragging = false;
					startRow = -1;
					startCol = -1;
					mainPanel.removeStyleName("dragging");
				}
				dragging = false;
				break;
			}
			break;
		}
	}

	public boolean movePossible() {
		boolean isPossible = false;

		int iFirst = mainPanel.getInfosReport().getIFirst();
		int jFirst = mainPanel.getInfosReport().getJFirst();

		if (startCol < jFirst) {
			if (startCol == 0) {
				if (jFirst == 1) {
					isPossible = false;
				}
				else {
					GridCube gc = mainPanel.getInfosReport().getGrid();
					for (int j = 1; j < jFirst; j++) {
						ItemCube ic = (ItemCube) gc.getIJ(startRow, j);
						String itemType = ic.getType();
						if (itemType.equalsIgnoreCase("ItemElement")) {
							isPossible = true;
						}
					}
				}
			}
			else {
				GridCube gc = mainPanel.getInfosReport().getGrid();
				for (int j = 0; j < jFirst; j++) {
					if (j != startCol) {
						ItemCube ic = (ItemCube) gc.getIJ(startRow, j);
						String itemType = ic.getType();
						if (itemType.equalsIgnoreCase("ItemElement")) {
							isPossible = true;
						}
					}
				}
			}
		}
		else {
			if (startRow == 0) {
				if (iFirst == 1) {
					isPossible = false;
				}
				else {
					GridCube gc = mainPanel.getInfosReport().getGrid();
					for (int i = 1; i < iFirst; i++) {
						ItemCube ic = (ItemCube) gc.getIJ(i, startCol);
						String itemType = ic.getType();
						if (itemType.equalsIgnoreCase("ItemElement")) {
							isPossible = true;
						}
					}
				}
			}
			else {
				GridCube gc = mainPanel.getInfosReport().getGrid();
				for (int i = 0; i < iFirst; i++) {
					if (i != startRow) {
						ItemCube ic = (ItemCube) gc.getIJ(i, startCol);
						String itemType = ic.getType();
						if (itemType.equalsIgnoreCase("ItemElement")) {
							isPossible = true;
						}
					}
				}
			}
		}

		return isPossible;
	}

	public boolean isDrillable() {
		boolean drillable = true;

		int jFirst = mainPanel.getInfosReport().getJFirst();
		int iFirst = mainPanel.getInfosReport().getIFirst();
		ItemCube ic1;
		ItemCube ic2;
		if (col < jFirst && col < jFirst - 2) {
			ic1 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row, col + 1);
			ic2 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row, col + 2);
			if (!ic1.getType().equalsIgnoreCase("ItemElement") && !ic2.getType().equalsIgnoreCase("ItemElement")) {
				if (mainPanel.getInfosReport().getGrid().getIJ(row + 2, col + 2) != null) {
					ItemCube ic3 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row + 1, col + 1);
					if (ic3.getType().equalsIgnoreCase("ItemElement")) {
						ItemCube ic4 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row + 2, col + 2);
						if (ic4.getType().equalsIgnoreCase("ItemElement")) {
							drillable = false;
						}
					}
				}
			}
		}
		else if (row < iFirst - 2) {
			ic1 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row + 1, col);
			ic2 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row + 2, col);
			if (!ic1.getType().equalsIgnoreCase("ItemElement") && !ic2.getType().equalsIgnoreCase("ItemElement")) {
				if (mainPanel.getInfosReport().getGrid().getIJ(row + 2, col + 2) != null) {
					ItemCube ic3 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row + 1, col + 1);
					if (ic3.getType().equalsIgnoreCase("ItemElement")) {
						ItemCube ic4 = (ItemCube) mainPanel.getInfosReport().getGrid().getIJ(row + 2, col + 2);
						if (ic4.getType().equalsIgnoreCase("ItemElement")) {
							drillable = false;
						}
					}
				}
			}
		}
		return drillable;
	}

	private native void disableContextMenu(Element elem) /*-{
		elem.oncontextmenu = function(a, b) {
			return false
		};
	}-*/;

	private native void setselectedhighlight(Element elem) /*-{
		elem.onselectstart = function(a, b) {
			return false
		};
	}-*/;

}
