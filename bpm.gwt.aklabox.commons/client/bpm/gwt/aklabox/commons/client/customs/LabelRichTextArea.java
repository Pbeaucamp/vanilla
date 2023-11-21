package bpm.gwt.aklabox.commons.client.customs;

import bpm.gwt.aklabox.commons.client.utils.text.RichTextToolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LabelRichTextArea extends Composite {
	
	public static String ua = GWT.isClient() ? Window.Navigator.getUserAgent().toLowerCase() : "JVM";
	private static final String RICHTEXTAREA_NAME = "RichTextArea";

	private static LabelTextAreaUiBinder uiBinder = GWT.create(LabelTextAreaUiBinder.class);

	interface LabelTextAreaUiBinder extends UiBinder<Widget, LabelRichTextArea> {
	}

	interface MyStyle extends CssResource {
		String resize();

		String toolbar();
	}

	@UiField
	MyStyle style;

	@UiField
	Label label;

	@UiField
	SimplePanel panelToolbar;

	@UiField
	RichTextArea textarea;
	
	private boolean isIE11;

	public LabelRichTextArea() {
		initWidget(uiBinder.createAndBindUi(this));
		buildContent(true);
	}

	public LabelRichTextArea(boolean showRichToolbar) {
		initWidget(uiBinder.createAndBindUi(this));
		buildContent(showRichToolbar);
	}

	private void buildContent(boolean showRichToolbar) {
		textarea.getElement().setAttribute("spellcheck", "false");
		textarea.getElement().setAttribute("id", RICHTEXTAREA_NAME);

		if (showRichToolbar) {
			RichTextToolbar toolbar = new RichTextToolbar(textarea);
			toolbar.addStyleName(style.toolbar());
			panelToolbar.setWidget(toolbar);
		}
		else {
			panelToolbar.setVisible(false);
		}
		
		isIE11 = isIE11();
	}
	
	private boolean isIE11() {
		RegExp re  = RegExp.compile("[t|T]rident/.*rv:([0-9]{1,}[\\.0-9]{0,})");
	    return re.exec(ua) != null;
	}

	public void setText(String text) {
		textarea.setHTML(text);
	}

	public String getHTML() {
		return textarea.getHTML();
	}

	public String getText() {
		return textarea.getText();
	}

	public void clear() {
		textarea.setText("");
	}

	public void setEnabled(boolean enabled) {
		textarea.setEnabled(enabled);
	}

	public void setPlaceHolder(String placeHolder) {
		label.setText(placeHolder);
	}

	public void setResize(boolean resize) {
		if (!resize) {
			textarea.addStyleName(style.resize());
		}
	}

	public void setHeight(String height) {
		textarea.setHeight(height);
	}

	public Point getCursorTopAndLeft() {
		String div = "<span id='cursorPosition' visible='false'></span>";
		textarea.getFormatter().insertHTML(div);

		IFrameElement ife = IFrameElement.as(textarea.getElement());
		Document doc = ife.getContentDocument();
		Element el = doc.getElementById("cursorPosition");

		Point coordinate = new Point(el.getAbsoluteLeft(), el.getAbsoluteTop());
		el.removeFromParent();
		return coordinate;
	}

	public void insertHTML(String html) {
		if (!isIE11) {
			textarea.getFormatter().insertHTML(html);
		}
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return textarea.addKeyUpHandler(handler);
	}

	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return textarea.addDomHandler(handler, BlurEvent.getType());
	}

	public native int getCursorTextArea() /*-{
		var elem = $doc.getElementById("RichTextArea");
	    var node = elem.contentWindow.document.body;
	    var range = elem.contentWindow.getSelection().getRangeAt(0);
	
	    var treeWalker = $doc.createTreeWalker(node, NodeFilter.SHOW_ALL, function(node) {
	        var nodeRange = $doc.createRange();
	        nodeRange.selectNodeContents(node);
	        if(node.tagName == 'BR' || node.nodeType == 3){
	            return nodeRange.compareBoundaryPoints(Range.END_TO_END, range) < 0 ? NodeFilter.FILTER_ACCEPT
	                : NodeFilter.FILTER_SKIP;
	        } else {
	            return NodeFilter.FILTER_SKIP;
	        }
	    }, false);
	
	    var charCount = 0;
	    while (treeWalker.nextNode()) {
	        if(treeWalker.currentNode.nodeType == 3){
	        	charCount += treeWalker.currentNode.length;
	        } else {
	        	charCount += 1;
	        }
	    }
	
	    if (range.startContainer.nodeType == 3) {
	        charCount += range.startOffset;
	    }
		//$wnd.alert(charCount);
	    return charCount;
	}-*/;
}
