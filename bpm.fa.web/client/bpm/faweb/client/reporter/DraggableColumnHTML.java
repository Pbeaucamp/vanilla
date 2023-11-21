package bpm.faweb.client.reporter;

import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.reporter.widgets.CrossTabWidget;

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

public class DraggableColumnHTML extends AbsolutePanel implements HasMouseDownHandlers {
	private static final String CSS_HTML_WIDGET_CROSSTAB_ROWS = "htmlCrosstabWidgetRows";
	private static final String DEFAULT_HEIGHT = "defaultHeight";

	private CrossTabWidget crossWidget;
	private HTML htmlContent;

	private DraggableTreeItem column;
	private ColumnType columnType;

	public DraggableColumnHTML(String html, DraggableTreeItem column, ColumnType columnType, CrossTabWidget crossWidget) {
		super();
		this.column = column;
		this.columnType = columnType;
		this.crossWidget = crossWidget;

		htmlContent = new HTML(html);
		htmlContent.addStyleName(CSS_HTML_WIDGET_CROSSTAB_ROWS);
		this.add(htmlContent);
	}

	public void setColumn(DraggableTreeItem column) {
		this.column = column;
	}

	public DraggableTreeItem getColumn() {
		return column;
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public void addStyleNameDefault() {
		this.htmlContent.addStyleName(DEFAULT_HEIGHT);
	}

	public void removeStyleNameDefault() {
		this.htmlContent.removeStyleName(DEFAULT_HEIGHT);
	}

	public CrossTabWidget getCrossWidget() {
		return crossWidget;
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}
}
