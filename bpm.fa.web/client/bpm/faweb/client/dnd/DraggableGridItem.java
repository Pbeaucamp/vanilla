package bpm.faweb.client.dnd;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.gwt.commons.client.utils.MessageHelper;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.DOM;

public class DraggableGridItem extends ResizablePanel {

	private String name;
	private String uname;
	private boolean isDrillable = false;
	private boolean isLast = false;
	private boolean isProjection;

	private MainPanel mainPanel;
	private CubeView parent;

	public DraggableGridItem(final MainPanel mainPanel, String label, String uname, int ii, int jj, String name, boolean isProjection, CubeView view) {
		super(mainPanel, view, ii, jj);

		this.parent = view;
		
		html.setHTML("&nbsp;" + label + "&nbsp;");
		
		html.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				mainPanel.showTabWaitPart(true);
				if (isDrillable()) {
					if(mainPanel.dateFunction == 2 || mainPanel.dateFunction == 3) {
						if(mainPanel.getGrid().isDateOnRow()) {
							mainPanel.drill(DraggableGridItem.this.i + 1, DraggableGridItem.this.j + 1, DraggableGridItem.this.isProjection ? mainPanel.getActualProjection() : null);
						}
						else {
							mainPanel.drill(DraggableGridItem.this.i + 1, DraggableGridItem.this.j, DraggableGridItem.this.isProjection ? mainPanel.getActualProjection() : null);
						}
					}
					else {
						mainPanel.drill(DraggableGridItem.this.i, DraggableGridItem.this.j, DraggableGridItem.this.isProjection ? mainPanel.getActualProjection() : null);
					}
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "You're not allowed to drill here");

					mainPanel.showTabWaitPart(false);
				}
			}
		});
		
		html.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mainPanel.getDisplayPanel().setDisabledMap(isGelocalisable(), DraggableGridItem.this); 
			}
		});

		html.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
					mainPanel.hideGridPopup();
					mainPanel.showNewGridPopup(DraggableGridItem.this.getUname(), DraggableGridItem.this, event.getClientX(), event.getClientY());
				}
			}
		});
		
		int indexOffUnderline = label.indexOf("<u>");
		int indexOffEndUnderline = label.indexOf("</u>");
		if (indexOffUnderline != -1) {
			String firstLetter = label.substring(indexOffUnderline + "<u>".length(), indexOffEndUnderline);
			String restOfLabel = label.substring(indexOffEndUnderline + "</u>".length());
			label = firstLetter + restOfLabel;
		}
		this.setUname(uname);
		this.i = ii;
		this.j = jj;
		this.setName(name);
		this.setTitle(label);
		this.isProjection = isProjection;
		this.mainPanel = mainPanel;

		html.getElement().getStyle().setBackgroundColor("#C3DAF9");
		dragRight.getElement().getStyle().setBackgroundColor("#C3DAF9");
		html.getElement().getStyle().setFontSize(11, Unit.PX);
		html.getElement().getStyle().setTableLayout(TableLayout.FIXED);
		html.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
//		this.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		html.getElement().getStyle().setTextOverflow(TextOverflow.ELLIPSIS);
		DOM.setStyleAttribute(html.getElement(), "textAlign", "center");
		DOM.setStyleAttribute(html.getElement(), "margin", "auto");
		DOM.setStyleAttribute(html.getElement(), "whiteSpace", "nowrap");
		html.addStyleName("draggableGridItem");

		mainPanel.getDragController().makeDraggable(this);
		mainPanel.getDragController().registerDropController(new GridItemDropController(mainPanel, this, i, j));


		
	}

	private boolean isGelocalisable() {
		boolean isGeolocalisable = false;

		int pointIndex = DraggableGridItem.this.getUname().indexOf(".");
		String selectedDimensionUname = DraggableGridItem.this.getUname().substring(0, pointIndex);
		int beginIndex = selectedDimensionUname.indexOf("[") + "[".length();
		String selectedName = selectedDimensionUname.substring(beginIndex);

		selectedName = selectedName.replace("]", "").replace("[", "");

		for (ItemDim itemDim : mainPanel.getInfosReport().getDims()) {
			String dimensionUname = itemDim.getUname();
			int begin = dimensionUname.indexOf("[") + "[".length();
			int end = dimensionUname.lastIndexOf("]");
			String name = dimensionUname.substring(begin, end);
			if (selectedName.equals(name)) {
				isGeolocalisable = itemDim.isGeolocalisable();
				break;
			}
		}
		return isGeolocalisable;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public void setDrillable(boolean isDrillable) {
		this.isDrillable = isDrillable;
	}

	public boolean isDrillable() {
		return isDrillable;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUname() {
		return uname;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static native void disableContextMenu(Element e) /*-{
		e.oncontextmenu = function() {
			return false;
		};
	}-*/;

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	public boolean isLast() {
		return isLast;
	}

	public boolean isProjection() {
		return isProjection;
	}

	public void setText(String text) {
		html.setText(text);
	}

}
