package bpm.fwr.client.draggable.widgets;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.client.action.ActionChangeDraggableColumnHTMLLabel;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.utils.ColumnType;
import bpm.fwr.client.utils.WidgetType;
import bpm.fwr.client.widgets.ReportWidget;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;

public class DraggableColumnHTML extends DraggableImageHTML{

	private Column column;
	private ReportWidget reportWidgetParent;
	private ColumnType columnType;
	private boolean custom = false;

	public DraggableColumnHTML(String html, Column column, ColumnType columnType, WidgetType type, ReportWidget reportWidgetParent, 
			boolean withMenuOptions) {
		super(html, type, withMenuOptions);
		this.column = column;
		this.columnType = columnType;
		this.reportWidgetParent = reportWidgetParent;
		
		if(columnType == ColumnType.GROUP || columnType == ColumnType.DETAIL){
			addDoubleClickHandler(doubleClickHandler);
		}
	}
	
	public void addClickHandlerToMenuOptions(ClickHandler clickHandler){
		if(imgMenuOptions != null){
			imgMenuOptions.addClickHandler(clickHandler);
		}
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Column getColumn() {
		return column;
	}

	public ReportWidget getReportWidgetParent() {
		return reportWidgetParent;
	}

	public void setReportWidgetParent(ReportWidget reportWidgetParent) {
		this.reportWidgetParent = reportWidgetParent;
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

	public ColumnType getColumnType() {
		return columnType;
	}
	
	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {
		
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			showDialogText(finishListener);
		}
	};
	
	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			ActionChangeDraggableColumnHTMLLabel action = 
				new ActionChangeDraggableColumnHTMLLabel(ActionType.CHANGE_TEXT, DraggableColumnHTML.this, 
					getText(), (String)result);
			reportWidgetParent.getReportSheetParent().getPanelParent().addActionToUndoAndClearRedo(action);
			
			String txt = (String)result;
			if(txt.isEmpty()){
				addStyleNameDefault();
			}
			else {
				removeStyleNameDefault();
			}
			
			setText(txt);
			getColumn().setCustomColumnName(txt, true);
			setCustom(true);
			
		}
	};
	
	public void setText(String text, String newText, ActionType actionType){
		setText(text);
		
		if(actionType == ActionType.REDO){
			ActionChangeDraggableColumnHTMLLabel action = 
				new ActionChangeDraggableColumnHTMLLabel(ActionType.CHANGE_IMG_URL, this, newText, text);
			reportWidgetParent.getReportSheetParent().getPanelParent().replaceLastActionToUndo(action);
		}
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public boolean isCustom() {
		return custom;
	}
}
