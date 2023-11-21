package bpm.faweb.client.panels.center.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.DraggableGridItem;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.dnd.ResizablePanel;
import bpm.faweb.client.panels.center.ICubeViewer;
import bpm.faweb.client.utils.DateFunctionCalculator;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.ItemCube;
import bpm.faweb.shared.infoscube.ItemDim;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class CubeView extends FlexTable {

	private MainPanel mainPanel;
	
	private GridCube cube;
	private int maxLenght = 0;
	
	private List<Integer> lengths = new ArrayList<Integer>();
	
	private int dateItemI;
	private int dateItemJ;

	public CubeView(MainPanel mainPanel, ICubeViewer cubeViewer, GridCube cube, boolean isCalcul, boolean isProjection) {
		super();
		this.mainPanel = mainPanel;
		this.cube = cube;

		mainPanel.getAllMeasureAdded().clear();

		refreshDragCtrl();

		findIJFirst();
		
		//date functions
		if(this.mainPanel.dateFunction > 0) {
			ItemCube dateItem = findDateItem();
			
			if(dateItem != null && DateFunctionCalculator.canCalculateDate(dateItem, this.mainPanel.getInfosReport(), dateItemI, dateItemJ)) {
			
				if(this.mainPanel.dateFunction == DateFunctionCalculator.DIFFERENCE_BETWEEN_DATE) {
					DateFunctionCalculator.differenceBetweenDate(dateItem, this.mainPanel.getInfosReport(), dateItemI, dateItemJ);
				}
				else if(this.mainPanel.dateFunction == DateFunctionCalculator.YEAR_TO_DATE) {
					DateFunctionCalculator.yearToDate(dateItem, this.mainPanel.getInfosReport(), this.mainPanel.dateFunctionMonth, dateItemI, dateItemJ);
				}
				else if(this.mainPanel.dateFunction == DateFunctionCalculator.YEAR_TO_YEAR) {
					DateFunctionCalculator.yearToYear(dateItem, this.mainPanel.getInfosReport(), this.mainPanel.dateFunctionMonth, dateItemI, dateItemJ);
				}
				cube = this.mainPanel.getInfosReport().getGrid();
				cube.setDateFunctionCalculated(true);
			}
		}

		
		findIJFirst();
		
		this.addStyleName("cubeView");
		this.getElement().getStyle().setTableLayout(TableLayout.FIXED);
		this.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		this.getElement().getStyle().setMarginLeft(5, Unit.PX);
		this.getElement().getStyle().setMarginBottom(5, Unit.PX);
		this.getElement().getStyle().setMarginRight(5, Unit.PX);

		this.setCellSpacing(0);
		this.setCellPadding(0);

		// Lists for total rows and cols
		List<Integer> totalRows = new ArrayList<Integer>();
		List<Integer> totalCols = new ArrayList<Integer>();

		for(int i = 0 ; i < cube.getItems().get(0).size(); i++) {
			lengths.add(14);
		}
		maxLenght = 14;
		
		// An hashmap who contains previous row itemElement (for Spanning)
		HashMap<Integer, Integer> precRowItem = new HashMap<Integer, Integer>();
		for (int i = 0; i < cube.getItems().size(); i++) {
			// the previous column itemElement (for Spanning)
			int precColItem = -1;
			for (int j = 0; j < cube.getItems().get(i).size(); j++) {
				boolean isLastRow = false;
				boolean isLastCol = false;

				if (i == cube.getNbOfRow() - 1) {
					isLastRow = true;
				}
				if (j == cube.getNbOfCol() - 1) {
					isLastCol = true;
				}

				ItemCube curr = cube.getItems().get(i).get(j);
//				if (curr.getLabel().length() > maxLenght) {
//					maxLenght = curr.getLabel().length();
//				}
				if(curr.getLabel().length() > lengths.get(j)) {
					lengths.set(j, curr.getLabel().length());
				}

				// if ItemElement
				if (curr.getType().equalsIgnoreCase("ItemElement") && !curr.getLabel().equalsIgnoreCase("")) {
					DraggableGridItem lbl = null;

					if (curr.getUname().contains("[Measures]")) {
						mainPanel.putMeasureToAllMeasuresAdded(curr.getUname(), curr.getLabel());
					}

					// Geolocalizable dimension
					boolean isGeolocalisable = false;

					int pointIndex = curr.getUname().indexOf(".");
					String selectedDimensionUname = curr.getUname().substring(0, pointIndex);
					int beginIndex = selectedDimensionUname.indexOf("[") + "[".length();
					String selectedName = selectedDimensionUname.substring(beginIndex);

					selectedName = selectedName.replace("]", "").replace("[", "");

					for (ItemDim dim : mainPanel.getInfosReport().getDims()) {
						String dimensionUname = dim.getUname();
						int begin = dimensionUname.indexOf("[") + "[".length();
						int end = dimensionUname.lastIndexOf("]");
						String name = dimensionUname.substring(begin, end);

						if (selectedName.equals(name)) {
							isGeolocalisable = dim.isGeolocalisable();
							break;
						}
					}
					if (cube.getPersonalNames().containsKey(curr.getUname())) {
						String label = cube.getPersonalNames().get(curr.getUname());
						if (isGeolocalisable) {
							String firstLetter = label.substring(0, 1);
							String labelWithUnderline = "<u>" + firstLetter + "</u>" + label.substring(1);
							lbl = new DraggableGridItem(mainPanel, labelWithUnderline, curr.getUname(), i, j, curr.getLabel(), isProjection, this);
						}
						else {
							lbl = new DraggableGridItem(mainPanel, label, curr.getUname(), i, j, curr.getLabel(), isProjection, this);
						}
					}
					else {
						String label = curr.getLabel();
						if (isGeolocalisable) {
							String firstLetter = label.substring(0, 1);
							String labelWithUnderline = "<u>" + firstLetter + "</u>" + label.substring(1);
							lbl = new DraggableGridItem(mainPanel, labelWithUnderline, curr.getUname(), i, j, curr.getLabel(), isProjection, this);
						}
						else {
							lbl = new DraggableGridItem(mainPanel, label, curr.getUname(), i, j, curr.getLabel(), isProjection, this);
						}
					}

					// keep the precedent ItemElement for rowspan/colspan
					if (i < mainPanel.getInfosReport().getIFirst()) {
						precColItem = j;
						lbl.addStyleName("gridItemBorder");
						DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
						DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");
					}
					else {
						precRowItem.put(j, i);
						lbl.addStyleName("gridItemBorder");
						DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
						DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");
					}

					addEndStyle(isLastRow, isLastCol, lbl);

					isItemDrillable(i, j, lbl);

					this.setWidget(i, j, lbl);
				}

				// if ItemValue
				else if (curr.getType().equalsIgnoreCase("ItemValue")) {
					if (cubeViewer.isOn()) {
						FaWebLabelValue lbl = null;
						if (curr.getLabel().equalsIgnoreCase(" ") || curr.getLabel().equalsIgnoreCase("")) {
							lbl = new FaWebLabelValue(mainPanel, "&nbsp;", i, j, curr.getValue(), this);
						}
						else {

							lbl = new FaWebLabelValue(mainPanel, curr.getLabel(), i, j, curr.getValue(), this);
						}
						lbl.addStyleName("gridItemBorder");
						DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
						DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");
						addEndStyle(isLastRow, isLastCol, lbl);
						this.setWidget(i, j, lbl);
					}
				}

				// if ItemProperty
				else if (curr.getType().equalsIgnoreCase("ItemProperties")) {
					ResizablePanel lbl = new ResizablePanel(mainPanel, this, i, j);
					lbl.html.setHTML(curr.getLabel());
//					HTML lbl = new HTML(curr.getLabel());
					lbl.html.setTitle(curr.getLabel());

					DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
					DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

					lbl.getElement().getStyle().setBackgroundColor("#FFFFFF");
					lbl.getElement().getStyle().setFontSize(11, Unit.PX);
					lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
					lbl.getElement().getStyle().setOverflow(Overflow.HIDDEN);
					lbl.getElement().getStyle().setCursor(Cursor.POINTER);
					lbl.getElement().getStyle().setTextOverflow(TextOverflow.ELLIPSIS);
					lbl.getElement().getStyle().setPropertyPx("maxWidth", 7 * 14 - 4);
					DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(lbl.getElement(), "whiteSpace", "nowrap");
					DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

					lbl.addStyleName("gridItemBorder");
					lbl.addStyleName("gridItemValue");

					addEndStyle(isLastRow, isLastCol, lbl);
					this.setWidget(i, j, lbl);
				}

				// if ItemNull
				else {
					ResizablePanel lbl = new ResizablePanel(mainPanel, this, i, j);
					lbl.html.setHTML("&nbsp;");
//					HTML lbl = new HTML("&nbsp;");
					addEndStyle(isLastRow, isLastCol, lbl);
					this.setWidget(i, j, lbl);
					if (isSpanning(i, j, precColItem, precRowItem)) {
						if (i < mainPanel.getInfosReport().getIFirst()) {

							DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");

							lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
							lbl.getElement().getStyle().setFontSize(11, Unit.PX);
							lbl.getElement().getStyle().setTableLayout(TableLayout.FIXED);
							lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
							lbl.getElement().getStyle().setOverflow(Overflow.HIDDEN);
							DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
							DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
							DOM.setStyleAttribute(lbl.getElement(), "whiteSpace", "nowrap");
							DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

							lbl.addStyleName("rightGridSpanItemBorder");
							lbl.addStyleName("draggableGridItem");
						}
						else {
							DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

							lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
							lbl.getElement().getStyle().setFontSize(11, Unit.PX);
							lbl.getElement().getStyle().setTableLayout(TableLayout.FIXED);
							lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
							lbl.getElement().getStyle().setOverflow(Overflow.HIDDEN);
							DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
							DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
							DOM.setStyleAttribute(lbl.getElement(), "whiteSpace", "nowrap");
							DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

							lbl.addStyleName("leftGridSpanItemBorder");
							lbl.addStyleName("draggableGridItem");
						}
					}

					else {
						// if its a total element
						if (isTotal(i, j, lbl)) {
							if (i < mainPanel.getInfosReport().getIFirst()) {
								totalCols.add(j);
							}
							else {
								totalRows.add(i);
							}

							// items.add(lbl);
						}
						else {
							DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
							DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

							lbl.getElement().getStyle().setFontSize(11, Unit.PX);
							DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
							DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

							lbl.addStyleName("gridItemBorder");
							lbl.addStyleName("gridItemNull");
						}
					}
				}

			}
		}

		// if(maxLenght > 14) {
//		maxLenght = 14;
		// }

		DOM.setStyleAttribute(this.getElement(), "height", cube.getItems().size() * 30 + "px");
//		DOM.setStyleAttribute(this.getElement(), "width", cube.getItems().get(0).size() * 7 * maxLenght + "px");

		// add size and styles to cells
		for (int i = 0; i < cube.getItems().size(); i++) {
			boolean isTotalRow = false;
			if (totalRows.contains(i)) {
				isTotalRow = true;
			}
			for (int j = 0; j < cube.getItems().get(i).size(); j++) {
				try {
					this.getWidget(i, j).getElement().getStyle().setPropertyPx("width", 7 * maxLenght - 2);
					this.getWidget(i, j).getElement().getStyle().setPropertyPx("minWidth", 7 * maxLenght - 2);
					if (this.getWidget(i, j) instanceof DraggableGridItem) {
						if (((DraggableGridItem) this.getWidget(i, j)).isLast()) {
							this.getWidget(i, j).getElement().getStyle().setPropertyPx("maxWidth", 7 * maxLenght - 4);
							this.getWidget(i, j).getElement().getStyle().setPropertyPx("minWidth", 7 * maxLenght - 4);
						}
						else {
							this.getWidget(i, j).getElement().getStyle().setPropertyPx("maxWidth", 7 * maxLenght - 2);
						}
						this.getCellFormatter().getElement(i, j).getStyle().setPropertyPx("maxWidth", 7 * maxLenght);
					}
					else if (this.getWidget(i, j).getStyleName().contains("lastColItemBorder") && !this.getWidget(i, j).getStyleName().contains("rightGridSpanItemBorder")) {
						this.getWidget(i, j).getElement().getStyle().setPropertyPx("maxWidth", 7 * maxLenght - 4);
						this.getWidget(i, j).getElement().getStyle().setPropertyPx("minWidth", 7 * maxLenght - 4);
					}
					this.getWidget(i, j).setHeight("30px");
					this.getCellFormatter().getElement(i, j).getStyle().setPropertyPx("width", 7 * maxLenght);
					this.getCellFormatter().getElement(i, j).getStyle().setPropertyPx("minWidth", 7 * maxLenght);
					this.getCellFormatter().addStyleName(i, j, "cubeView");
					if (isTotalRow && j >= mainPanel.getInfosReport().getJFirst()) {
						this.getCellFormatter().getElement(i, j).getStyle().setFontWeight(FontWeight.BOLD);
						this.getCellFormatter().addStyleName(i, j, "gridItemValueBold");
					}
					if (totalCols.contains(j) && i >= mainPanel.getInfosReport().getIFirst()) {
						this.getCellFormatter().getElement(i, j).getStyle().setFontWeight(FontWeight.BOLD);
						this.getCellFormatter().addStyleName(i, j, "gridItemValueBold");
					}
					
					if(this.getWidget(i, j).getElement().getStyle().getProperty("borderLeft") == null || this.getWidget(i, j).getElement().getStyle().getProperty("borderLeft").isEmpty()) {
						this.getWidget(i, j).getElement().getStyle().setPropertyPx("width", 7 * maxLenght);
					}
					
				} catch (Exception e) {
				}
			}
		}

		// If its a calculGrid, we add row and col buttons.
		if (isCalcul) {
			DOM.setStyleAttribute(this.getElement(), "height", (cube.getItems().size() + 1) * 30 + "px");
			DOM.setStyleAttribute(this.getElement(), "width", (cube.getItems().get(0).size() + 1) * 7 * maxLenght + "px");
			this.insertRow(0);
			ResizablePanel lbl = new ResizablePanel(mainPanel, this, 0, 0);
			lbl.html.setHTML("&nbsp;");
//			HTML lbl = new HTML("&nbsp;");

			DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
			DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

			lbl.getElement().getStyle().setFontSize(11, Unit.PX);
			DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
			DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

			lbl.addStyleName("gridItemBorder");
			lbl.addStyleName("gridItemNull");

			this.setWidget(0, 0, lbl);
			this.getWidget(0, 0).setHeight("30px");
			this.getWidget(0, 0).setWidth(7 * maxLenght + "px");
			this.getCellFormatter().addStyleName(0, 0, "cubeView");

			for (int i = 1; i < cube.getItems().size() + 1; i++) {
				this.insertCell(i, 0);
				if (i > mainPanel.getInfosReport().getIFirst()) {
					RowButton btn = new RowButton(mainPanel, "r" + i, this.getCellFormatter(), i, cube.getItems().size() + 1, cube.getLigne(0).size() + 1);

					DOM.setStyleAttribute(btn.getElement(), "borderTop", "2px groove");
					DOM.setStyleAttribute(btn.getElement(), "borderLeft", "2px groove");

					btn.getElement().getStyle().setFontSize(11, Unit.PX);
					DOM.setStyleAttribute(btn.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(btn.getElement(), "display", "table-cell");

					btn.addStyleName("gridItemBorder");
					btn.addStyleName("gridItemNull");

					this.setWidget(i, 0, btn);
					this.getWidget(i, 0).setHeight("30px");
					this.getWidget(i, 0).setWidth(7 * maxLenght + "px");
					this.getCellFormatter().setHeight(i, 0, "30px");
					this.getCellFormatter().setWidth(i, 0, 7 * maxLenght + "px");
				}
				else {
					ResizablePanel label = new ResizablePanel(mainPanel, this, i, 0);
					label.html.setHTML("&nbsp;");
//					HTML label = new HTML("&nbsp;");

					DOM.setStyleAttribute(label.getElement(), "borderTop", "2px groove");
					DOM.setStyleAttribute(label.getElement(), "borderLeft", "2px groove");

					label.getElement().getStyle().setFontSize(11, Unit.PX);
					DOM.setStyleAttribute(label.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(label.getElement(), "display", "table-cell");

					label.addStyleName("gridItemBorder");
					label.addStyleName("gridItemNull");
					this.setWidget(i, 0, label);
					this.getWidget(i, 0).setHeight("30px");
					this.getWidget(i, 0).setWidth(7 * maxLenght + "px");
				}

				this.getCellFormatter().addStyleName(i, 0, "cubeView");
				if (i == cube.getItems().size()) {
					DOM.setStyleAttribute(this.getWidget(i, 0).getElement(), "borderBottom", "2px groove");
					this.getWidget(i, 0).addStyleName("lastRowItemBorder");
				}
			}
			for (int j = 1; j < cube.getLigne(0).size() + 1; j++) {
				if (j > mainPanel.getInfosReport().getJFirst()) {
					ColButton btn = new ColButton(mainPanel, "c" + j, this.getCellFormatter(), j, cube.getLigne(0).size() + 1, cube.getItems().size() + 1);

					DOM.setStyleAttribute(btn.getElement(), "borderTop", "2px groove");
					DOM.setStyleAttribute(btn.getElement(), "borderLeft", "2px groove");

					btn.getElement().getStyle().setFontSize(11, Unit.PX);
					DOM.setStyleAttribute(btn.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(btn.getElement(), "display", "table-cell");

					btn.addStyleName("gridItemBorder");
					btn.addStyleName("gridItemNull");

					this.setWidget(0, j, btn);
					this.getWidget(0, j).setHeight("30px");
					this.getWidget(0, j).setWidth(7 * maxLenght + "px");
					this.getCellFormatter().setHeight(0, j, "30px");
					this.getCellFormatter().setWidth(0, j, 7 * maxLenght + "px");
				}
				else {
					ResizablePanel label = new ResizablePanel(mainPanel, this, 0, j);
					label.html.setHTML("&nbsp;");

					DOM.setStyleAttribute(label.getElement(), "borderTop", "2px groove");
					DOM.setStyleAttribute(label.getElement(), "borderLeft", "2px groove");

					label.getElement().getStyle().setFontSize(11, Unit.PX);
					DOM.setStyleAttribute(label.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(label.getElement(), "display", "table-cell");

					label.addStyleName("gridItemBorder");
					label.addStyleName("gridItemNull");
					this.setWidget(0, j, label);
					this.getWidget(0, j).setHeight("30px");
					this.getWidget(0, j).setWidth(7 * maxLenght + "px");
				}

				this.getCellFormatter().getElement(0, j).getStyle().setTableLayout(TableLayout.FIXED);
				this.getCellFormatter().getElement(0, j).getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
				this.getCellFormatter().addStyleName(0, j, "cubeView");
				if (j == cube.getLigne(0).size()) {
					DOM.setStyleAttribute(this.getWidget(0, j).getElement(), "borderRight", "2px groove");
					this.getWidget(0, j).addStyleName("lastColItemBorder");
				}
			}
		}
		
		//put the size
		try {
			if(mainPanel.getInfosReport().isSizeLoaded()) {
				for(int i : mainPanel.getInfosReport().getCustomSizes().keySet()) {
					lengths.set(i, mainPanel.getInfosReport().getCustomSizes().get(i)/7);
				}
				mainPanel.getInfosReport().setSizeLoaded(false);
			}
			for(int col = 0 ; col < lengths.size() ; col++) {
				for(int row = 0 ; row < cube.getItems().size() ; row++) {
					Widget w = this.getWidget(row, col);
					if(w instanceof ResizablePanel) {
						((ResizablePanel)w).resize(lengths.get(col) * 7);
					}
				}
			}
		} catch(Exception e) {
		}
		

	}

	private ItemCube findDateItem() {
		int iFirst = mainPanel.getInfosReport().getIFirst();
		int jFirst = mainPanel.getInfosReport().getJFirst();
		
		ItemCube w =  mainPanel.getInfosReport().getGrid().getIJ(iFirst, 0);
		if(w.getType().equals("ItemElement")) {
			boolean isDateAllMember = false;
			String itemUname = w.getUname();
			for(ItemDim dim : MainPanel.getInstance().getInfosReport().getDims()) {
				if(itemUname.startsWith("[" + dim.getName())) {
					if(dim.isDate()) {
						isDateAllMember = true;
					}
					break;
				}
			}
			if(isDateAllMember) {
				dateItemI = iFirst;
				dateItemJ = 0;
				return w;
			}
		}
		
		w =  mainPanel.getInfosReport().getGrid().getIJ(0, jFirst);
		if(w.getType().equals("ItemElement")) {
			boolean isDateAllMember = false;
			String itemUname = w.getUname();
			for(ItemDim dim : MainPanel.getInstance().getInfosReport().getDims()) {
				if(itemUname.startsWith("[" + dim.getName())) {
					if(dim.isDate()) {
						isDateAllMember = true;
					}
					break;
				}
			}
			if(isDateAllMember) {
				dateItemI = 0;
				dateItemJ = jFirst;
				return w;
			}
		}
	
		
		
		return null;
	}

	/**
	 * Find the first value (for the undo/redo method)
	 */
	private void findIJFirst() {
		boolean find = false;
		for (int i = 0; i < cube.getItems().size(); i++) {
			for (int j = 0; j < cube.getLigne(i).size(); j++) {
				if (((ItemCube) cube.getIJ(i, j)).getType().equalsIgnoreCase("ItemValue")) {
					mainPanel.getInfosReport().setIFirst(i);
					mainPanel.getInfosReport().setJFirst(j);
					find = true;
					break;
				}
			}
			if (find) {
				break;
			}
		}
	}

	/**
	 * Look if the ItemElement is drillable
	 * 
	 * @param i
	 * @param j
	 * @param lbl
	 */
	private void isItemDrillable(int i, int j, DraggableGridItem lbl) {
		lbl.setDrillable(true);
	}

	/**
	 * Look if its a total cell and apply styles
	 * 
	 * @param i
	 * @param j
	 * @param lbl
	 * @return
	 */
	private boolean isTotal(int i, int j, ResizablePanel lbl) {
		boolean isTotal = false;
		if (i < mainPanel.getInfosReport().getIFirst() && j >= mainPanel.getInfosReport().getJFirst()) {
			isTotal = true;
			try {
				Widget w = this.getWidget(i - 1, j);
				if (w instanceof DraggableGridItem) {
					lbl.html.setHTML("&nbsp;&nbsp;");

					DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
					DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

					lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
					lbl.getElement().getStyle().setFontSize(11, Unit.PX);
					lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
					DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
					DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

					lbl.addStyleName("gridItemBorder");
					lbl.addStyleName("gridTotalItem");
					// lbl.addStyleName(FaWebMainComposite.skin.getTotalItem());
					// setElementStyle(lbl,
					// FaWebMainComposite.skin.getTotalItem());
					return true;
				}
				else {
					for (int row = i - 1; row > -1; row--) {
						if (((ResizablePanel) this.getWidget(row, j)).html.getHTML().equals("&nbsp;&nbsp;")) {

							DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

							lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
							lbl.getElement().getStyle().setFontSize(11, Unit.PX);
							lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
							DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
							DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
							DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

							lbl.addStyleName("leftGridSpanItemBorder");
							lbl.addStyleName("gridTotalItem");
							return true;
						}
					}

					lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
					lbl.getElement().getStyle().setFontSize(11, Unit.PX);
					lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
					DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
					DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

					lbl.addStyleName("gridTotalItem");
					for (int col = j - 1; col > -1; col--) {
						if (((ResizablePanel) this.getWidget(i, col)).html.getHTML().equals("&nbsp;&nbsp;")) {
							DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");

							lbl.addStyleName("rightGridSpanItemBorder");
						}
					}
				}
				if (!lbl.getStyleName().contains("rightGridSpanItemBorder")) {
					DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");

					lbl.addStyleName("rightGridSpanItemBorder");
				}
			} catch (Exception e) {

			}
		}
		else if (j < mainPanel.getInfosReport().getJFirst() && i >= mainPanel.getInfosReport().getIFirst()) {
			isTotal = true;
			try {
				Widget w = this.getWidget(i, j - 1);
				if (w instanceof DraggableGridItem) {
					lbl.html.setHTML("&nbsp;&nbsp;");

					DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");
					DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

					lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
					lbl.getElement().getStyle().setFontSize(11, Unit.PX);
					lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
					DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
					DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

					lbl.addStyleName("gridItemBorder");
					lbl.addStyleName("gridTotalItem");
					return true;
				}
				else {
					for (int col = j - 1; col > -1; col--) {
						if (((ResizablePanel) this.getWidget(i, col)).html.getHTML().equals("&nbsp;&nbsp;")) {
							DOM.setStyleAttribute(lbl.getElement(), "borderTop", "2px groove");

							lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
							lbl.getElement().getStyle().setFontSize(11, Unit.PX);
							lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
							DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
							DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
							DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

							lbl.addStyleName("rightGridSpanItemBorder");
							lbl.addStyleName("gridTotalItem");
							return true;
						}
					}

					lbl.getElement().getStyle().setBackgroundColor("#C3DAF9");
					lbl.getElement().getStyle().setFontSize(11, Unit.PX);
					lbl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
					DOM.setStyleAttribute(lbl.getElement(), "textAlign", "center");
					DOM.setStyleAttribute(lbl.getElement(), "margin", "auto");
					DOM.setStyleAttribute(lbl.getElement(), "display", "table-cell");

					lbl.addStyleName("gridTotalItem");
					for (int row = i - 1; row > -1; row--) {
						if (((ResizablePanel) this.getWidget(row, j)).html.getHTML().equals("&nbsp;&nbsp;")) {
							DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

							lbl.addStyleName("leftGridSpanItemBorder");
						}
					}
				}
				if (!lbl.getStyleName().contains("leftGridSpanItemBorder")) {
					DOM.setStyleAttribute(lbl.getElement(), "borderLeft", "2px groove");

					lbl.addStyleName("leftGridSpanItemBorder");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isTotal;
	}

	private void refreshDragCtrl() {
		FaWebDragController dragCtrl = mainPanel.getDragController();
		dragCtrl.removeAllDropControllers();
	}

	private void addEndStyle(boolean isLastRow, boolean isLastCol, Widget lbl) {
		if (isLastCol) {
			lbl.addStyleName("lastColItemBorder");
			DOM.setStyleAttribute(lbl.getElement(), "borderRight", "2px groove");
		}
		if (isLastRow) {
			lbl.addStyleName("lastRowItemBorder");
			DOM.setStyleAttribute(lbl.getElement(), "borderBottom", "2px groove");
		}
		if (lbl instanceof DraggableGridItem) {
			DraggableGridItem item = (DraggableGridItem) lbl;
			if (isLastCol) {
				item.setLast(true);
			}
		}
	}

	private boolean isSpanning(int i, int j, int precColItem, HashMap<Integer, Integer> precRowItem) {
		boolean spanning = false;

		// If first element return false
		if (i < mainPanel.getInfosReport().getIFirst() && j < mainPanel.getInfosReport().getJFirst()) {
			return false;
		}

		// For a column element
		if (i < mainPanel.getInfosReport().getIFirst()) {
			try {
				if (j > precColItem && precColItem != -1) {
					spanning = true;
					if (((ItemCube) cube.getIJ(i, precColItem)).getUname().contains("[Measures]")) {
						return true;
					}
				}
				// For a child item
				Widget o = (this.getWidget(i - 1, j));
				if (o instanceof DraggableGridItem) {
					spanning = false;
				}
				else {
					if (o.getStyleName().contains("gridItemNull")) {
						spanning = false;
					}
					else if (o.getStyleName().contains("gridTotalItem")) {
						spanning = false;
					}
				}
			} catch (Exception e) {
				spanning = true;
			}
		}
		else if (j < mainPanel.getInfosReport().getJFirst()) {
			try {
				// For a row element
				if (precRowItem.containsKey(j)) {
					if (i > precRowItem.get(j)) {
						spanning = true;
						if (((ItemCube) cube.getIJ(precRowItem.get(j), j)).getUname().contains("[Measures]")) {
							return true;
						}
					}
				}
				// For a child item
				Widget o = (this.getWidget(i, j - 1));
				if (o instanceof DraggableGridItem) {
					spanning = false;
				}
				else {
					if (o.getStyleName().contains("gridItemNull")) {
						spanning = false;
					}
					else if (o.getStyleName().contains("gridTotalItem")) {
						spanning = false;
					}
				}
			} catch (Exception e) {
				spanning = true;
			}
		}
		else {
			return false;
		}
		return spanning;
	}

	public int getMaxLenght() {
		return maxLenght;
	}

	public boolean isDateOnRow() {
		return dateItemJ == 0;
	}

	// public void applyNewSkin() {
	// for(HTML item : items) {
	// if(item instanceof DraggableGridItem) {
	// setElementStyle(item, FaWebMainComposite.skin.getItem());
	// }
	// else {
	// setElementStyle(item, FaWebMainComposite.skin.getTotalItem());
	// }
	// }
	// }

	// public static void setElementStyle(HTML element, HashMap<String, String>
	// properties) {
	// for(Object key : properties.keySet()) {
	// DOM.setStyleAttribute(element.getElement(), key.toString(),
	// properties.get(key.toString()));
	// }
	// }
}
