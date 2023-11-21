package bpm.fwr.client.widgets;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.action.ActionChangeBirtComment;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.dialogs.TextDialogBox;
import bpm.fwr.client.dialogs.TextDialogBox.TypeDialog;
import bpm.fwr.client.images.WysiwygImage;
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
import com.google.gwt.user.client.ui.Image;

public class BirtCommentWidget extends ReportWidget implements HasMouseDownHandlers {
	private static final String DEFAULT_VANILLA_URL = "http://localhost:7171/VanillaRuntime";
	private static final String DEFAULT_TITLE = "BIRT Comment";
	private static final String DEFAULT_HEIGHT = "defaultHeight";
	private static final String DEFAULT_COMMENT_DISPLAYED = "1";

	private ReportSheet reportSheetParent;
	private ReportPanel reportPanelParent;
	private GridWidget gridWidget;
	private Image img;
	private String title, commentDisplayed, name;
	private BirtCommentWidget comment;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommentDisplayed() {
		return commentDisplayed;
	}

	public void setCommentDisplayed(String commentDisplayed) {
		this.commentDisplayed = commentDisplayed;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BirtCommentWidget(ReportSheet reportSheetParent, WidgetType type, int width) {
		super(reportSheetParent, WidgetType.BIRTCOMMENT, width);
		this.reportSheetParent = reportSheetParent;

		this.commentDisplayed = DEFAULT_COMMENT_DISPLAYED;

		img = new Image(WysiwygImage.INSTANCE.commentBig());

		img.addDoubleClickHandler(doubleClickHandler);
		this.add(img);
		comment = this;
	}

	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {

		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			showDialogText();
		}
	};

	public String getCommentTitle() {
		if (title == DEFAULT_TITLE)
			return "";
		else
			return title;

	}

	public void showDialogText() {
		TextDialogBox dial = null;

		String titleBirt = getCommentTitle();

		dial = new TextDialogBox(Bpm_fwr.LBLW.BirtComment(), Bpm_fwr.LBLW.TextFill(), titleBirt, comment, TypeDialog.BIRT);
		dial.addFinishListener(finishListener);
		dial.center();
	}

	private void setBirtComment(BirtCommentWidget comment) {
		this.comment = comment;
	}

	private FinishListener finishListener = new FinishListener() {

		@Override
		public void onFinish(Object result, Object source, String result1) {
			title = result.toString();
			commentDisplayed = result1;
			comment.setTitle(title);
			comment.setCommentDisplayed(commentDisplayed);
			ActionChangeBirtComment action = new ActionChangeBirtComment(ActionType.CHANGE_BIRT_COMMENT, comment, DEFAULT_VANILLA_URL);
			if (reportSheetParent != null) {
				reportSheetParent.getPanelParent().addActionToUndoAndClearRedo(action);
			}
			else {
				reportPanelParent.addActionToUndoAndClearRedo(action);
			}

			setBirtComment(comment);
		}

	};

	@Override
	public int getWidgetHeight() {
		return 0;
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
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

	public void changeBirtComment(BirtCommentWidget birtComment1, ActionType redo) {
		if (redo != null) {
			this.title = birtComment1.getTitle();
			this.commentDisplayed = birtComment1.getCommentDisplayed();
		}

	}
}
