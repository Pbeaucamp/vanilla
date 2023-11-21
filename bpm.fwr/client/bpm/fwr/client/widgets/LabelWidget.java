package bpm.fwr.client.widgets;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.action.ActionChangeTextLabel;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.TextDialogBox;
import bpm.fwr.client.dialogs.TextDialogBox.TypeDialog;
import bpm.fwr.client.panels.ReportPanel;
import bpm.fwr.client.panels.ReportSheet;
import bpm.fwr.client.utils.WidgetType;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;

public class LabelWidget extends ReportWidget implements HasMouseDownHandlers {
	private static final String DEFAULT_HEIGHT = "defaultHeight";

	private ReportSheet reportSheetParent;
	private ReportPanel reportPanelParent;
	private GridWidget gridWidget;
	private HTML label;

	public LabelWidget(ReportSheet reportSheetParent, int width, GridWidget gridWidget) {
		super(reportSheetParent, WidgetType.LABEL, width);
		this.reportSheetParent = reportSheetParent;
		this.gridWidget = gridWidget;

		label = new HTML();
		label.addDoubleClickHandler(doubleClickHandler);
		this.add(label);
	}

	public LabelWidget(ReportPanel reportPanelParent, int width, String html) {
		super(null, WidgetType.LABEL, width);
		this.reportPanelParent = reportPanelParent;

		label = new HTML(html);
		label.addDoubleClickHandler(doubleClickHandler);
		this.add(label);
	}

	public void showDialogText() {
		TextDialogBox dial = new TextDialogBox(Bpm_fwr.LBLW.Label(), Bpm_fwr.LBLW.TextFill(), label.getHTML(), label, TypeDialog.EDITABLE);
		dial.addFinishListener(finishListener);
		dial.center();
	}

	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {

		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			showDialogText();
		}
	};

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			ActionChangeTextLabel action = new ActionChangeTextLabel(ActionType.CHANGE_IMG_URL, LabelWidget.this, getText(), (String) result);
			if (reportSheetParent != null) {
				reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
			}
			else {
				reportPanelParent.addActionToUndoAndClearRedo(action);
			}

			String txt = (String) result;
			if (txt.isEmpty()) {
				label.addStyleName(DEFAULT_HEIGHT);
			}
			else {
				label.removeStyleName(DEFAULT_HEIGHT);
			}
			label.setHTML(txt);
		}
	};

	public String getText() {
		return label.getHTML();
	}

	public void setText(String text) {
		this.label.setHTML(text);
	}

	public void setText(String text, String newText, ActionType actionType) {
		this.label.setHTML(text);

		if (actionType == ActionType.REDO) {
			ActionChangeTextLabel action = new ActionChangeTextLabel(ActionType.CHANGE_IMG_URL, this, newText, text);
			if (reportSheetParent != null) {
				reportSheetParent.getPanelParent().replaceLastActionToUndo(action);
			}
			else {
				reportPanelParent.replaceLastActionToUndo(action);
			}
		}
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	@Override
	public int getWidgetHeight() {
		return getOffsetHeight();
	}

	@Override
	public void widgetToTrash(Object widget) {
		if (gridWidget != null) {
			gridWidget.widgetToTrash(widget);
		}
		else {
			reportSheetParent.widgetToTrash(widget);
		}
	}
}
