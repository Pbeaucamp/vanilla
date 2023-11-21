package bpm.fwr.client.draggable.widgets;

import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.dialogs.TextDialogBox;
import bpm.fwr.client.dialogs.TextDialogBox.TypeDialog;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.utils.WidgetType;
import bpm.gwt.commons.client.listeners.FinishListener;

import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class DraggableImageHTML extends AbsolutePanel implements HasMouseDownHandlers {
	private static final String CSS_HTML_WIDGET_IMAGE_MENU_OPTIONS = "htmlWidgetImageMenuOptions";
	private static final String CSS_HTML_WIDGET_COLUMN = "htmlWidgetColumn";

	private static final String CSS_HTML_WIDGET_CROSSTAB_ROWS = "htmlCrosstabWidgetRows";

	private static final String DEFAULT_HEIGHT = "defaultHeight";

	private WidgetType type;
	private Image image;
	private HTML htmlContent;
	protected Image imgMenuOptions;

	/**
	 * 
	 * This create an object DraggableImageHTML only with a text
	 * 
	 * @param html
	 * @param type
	 */
	public DraggableImageHTML(String html, WidgetType type, boolean isNotCrossTab) {
		super();
		this.type = type;

		htmlContent = new HTML(html);
		this.add(htmlContent);

		if (!isNotCrossTab) {
			htmlContent.addStyleName(CSS_HTML_WIDGET_CROSSTAB_ROWS);
		}
		else {
			htmlContent.addStyleName(CSS_HTML_WIDGET_COLUMN);

			imgMenuOptions = new Image(WysiwygImage.INSTANCE.arrowDown());
			imgMenuOptions.setStyleName(CSS_HTML_WIDGET_IMAGE_MENU_OPTIONS);
			this.add(imgMenuOptions);
		}
	}

	public void setType(WidgetType type) {
		this.type = type;
	}

	public WidgetType getType() {
		return type;
	}

	public String getImage() {
		return image.getUrl();
	}

	public void setText(String html) {
		this.htmlContent.setHTML(html);
	}

	public void addStyleNameDefault() {
		this.htmlContent.addStyleName(DEFAULT_HEIGHT);
	}

	public void removeStyleNameDefault() {
		this.htmlContent.removeStyleName(DEFAULT_HEIGHT);
	}

	protected void addDoubleClickHandler(DoubleClickHandler doubleClickHandler) {
		this.htmlContent.addDoubleClickHandler(doubleClickHandler);
	}

	protected String getText() {
		return htmlContent.getHTML();
	}

	public void setTextAlignRigh() {
		DOM.setStyleAttribute(htmlContent.getElement(), "textAlign", "right");
	}

	public void setTextAlignLeft() {
		DOM.setStyleAttribute(htmlContent.getElement(), "textAlign", "left");
	}

	public void setTextAlignCenter() {
		DOM.setStyleAttribute(htmlContent.getElement(), "textAlign", "center");
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public void showDialogText(FinishListener finishListener) {
		TextDialogBox dial = new TextDialogBox(Bpm_fwr.LBLW.Label(), Bpm_fwr.LBLW.TextFill(), htmlContent.getHTML(), htmlContent, TypeDialog.NORMAL);
		dial.addFinishListener(finishListener);
		dial.center();
	}
}
