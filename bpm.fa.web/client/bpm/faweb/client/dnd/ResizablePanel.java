package bpm.faweb.client.dnd;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.panels.center.grid.CubeView;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;

public class ResizablePanel extends HTMLPanel {

	protected int i = 0;
	protected int j = 0;
	public HTML html;

	protected int actualWidth;
	protected int itemLeft;

	public HTML dragRight;
	protected boolean isDragging = false;
	protected CubeView parent;

	public ResizablePanel(final MainPanel mainPanel, CubeView view, int ii, int jj) {
		super("");
		i = ii;
		j = jj;
		this.parent = view;
		html = new HTML();

		html.getElement().getStyle().setWidth(98, Unit.PCT);
		html.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		html.getElement().getStyle().setLineHeight(30, Unit.PX);
		html.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

		this.add(html);

		dragRight = new HTML();
		dragRight.getElement().getStyle().setWidth(2, Unit.PCT);
		dragRight.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		dragRight.getElement().getStyle().setHeight(30, Unit.PX);
		dragRight.getElement().getStyle().setCursor(Cursor.COL_RESIZE);
		dragRight.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		this.add(dragRight);

		dragRight.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				isDragging = true;
				itemLeft = ResizablePanel.this.getElement().getAbsoluteLeft();
				Event.setCapture(dragRight.getElement());
				event.preventDefault();
			}
		});
		dragRight.addMouseUpHandler(new MouseUpHandler() {

			@Override
			public void onMouseUp(MouseUpEvent event) {
				isDragging = false;
				Event.releaseCapture(dragRight.getElement());
				event.preventDefault();
			}
		});
		dragRight.addMouseMoveHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if(isDragging) {
					if(itemLeft == 0) {
						itemLeft = ResizablePanel.this.getElement().getAbsoluteLeft();
					}
					int newWidth = event.getClientX() - itemLeft;
					if(newWidth <= 10) {
						newWidth = 10;
					}

					resize(newWidth);

					event.preventDefault();
				}
			}
		});

	}

	public void resize(int newWidth) {
		for(int row = 0; row < parent.getRowCount(); row++) {
			try {
				((ResizablePanel) parent.getWidget(row, j)).resizeWidth(row, newWidth);
			} catch(Exception e) {

				try {
					parent.getWidget(row, j).getElement().getStyle().setPropertyPx("width", newWidth - 2);
					parent.getWidget(row, j).getElement().getStyle().setPropertyPx("minWidth", newWidth - 2);
					if(parent.getWidget(row, j) instanceof DraggableGridItem) {
						if(((DraggableGridItem) parent.getWidget(row, j)).isLast()) {
							parent.getWidget(row, j).getElement().getStyle().setPropertyPx("maxWidth", newWidth - 4);
							parent.getWidget(row, j).getElement().getStyle().setPropertyPx("minWidth", newWidth - 4);
						}
						else {
							parent.getWidget(row, j).getElement().getStyle().setPropertyPx("maxWidth", newWidth - 2);
						}
						parent.getCellFormatter().getElement(row, j).getStyle().setPropertyPx("maxWidth", newWidth);
					}
					else if(parent.getWidget(row, j).getStyleName().contains("lastColItemBorder") && !parent.getWidget(row, j).getStyleName().contains("rightGridSpanItemBorder")) {
						parent.getWidget(row, j).getElement().getStyle().setPropertyPx("maxWidth", newWidth - 4);
						parent.getWidget(row, j).getElement().getStyle().setPropertyPx("minWidth", newWidth - 4);
					}
					parent.getWidget(row, j).setHeight("30px");
					parent.getCellFormatter().getElement(row, j).getStyle().setPropertyPx("width", newWidth);
					parent.getCellFormatter().getElement(row, j).getStyle().setPropertyPx("minWidth", newWidth);

					if(parent.getWidget(row, j).getElement().getStyle().getProperty("borderLeft") == null || parent.getWidget(row, j).getElement().getStyle().getProperty("borderLeft").isEmpty()) {
						parent.getWidget(row, j).getElement().getStyle().setPropertyPx("width", newWidth);
					}

				} catch(Exception ex) {}
			}
		}

		//TODO try to set the custom width to infosReport
		MainPanel.getInstance().getInfosReport().getCustomSizes().put(j, newWidth);
	}

	public void resizeWidth(int row, int newWidth) {
		try {
			ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("width", newWidth - 2);
			ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("minWidth", newWidth - 2);
			if(ResizablePanel.this.parent.getWidget(row, j) instanceof DraggableGridItem) {
				if(((DraggableGridItem) ResizablePanel.this.parent.getWidget(row, j)).isLast()) {
					ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("maxWidth", newWidth - 4);
					ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("minWidth", newWidth - 4);
				}
				else {
					ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("maxWidth", newWidth - 2);
				}
				ResizablePanel.this.parent.getCellFormatter().getElement(row, j).getStyle().setPropertyPx("maxWidth", newWidth);
			}
			else if(ResizablePanel.this.parent.getWidget(row, j).getStyleName().contains("lastColItemBorder") && !ResizablePanel.this.parent.getWidget(row, j).getStyleName().contains("rightGridSpanItemBorder")) {
				ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("maxWidth", newWidth - 4);
				ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("minWidth", newWidth - 4);
			}
			ResizablePanel.this.parent.getWidget(row, j).setHeight("30px");
			ResizablePanel.this.parent.getCellFormatter().getElement(row, j).getStyle().setPropertyPx("width", newWidth);
			ResizablePanel.this.parent.getCellFormatter().getElement(row, j).getStyle().setPropertyPx("minWidth", newWidth);

			if(ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().getProperty("borderLeft") == null || ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().getProperty("borderLeft").isEmpty()) {
				ResizablePanel.this.parent.getWidget(row, j).getElement().getStyle().setPropertyPx("width", newWidth);
			}

		} catch(Exception e) {}
	}

}
