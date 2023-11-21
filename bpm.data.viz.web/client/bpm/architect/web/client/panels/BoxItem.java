package bpm.architect.web.client.panels;

import bpm.data.viz.core.preparation.PreparationRule;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class BoxItem extends FocusPanel {

	private static BoxItemUiBinder uiBinder = GWT.create(BoxItemUiBinder.class);

	interface BoxItemUiBinder extends UiBinder<Widget, BoxItem> {
	}

	interface MyStyle extends CssResource {

	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel box;

	@UiField
	Label lblName;


	private int left;
	private int top;
	private PreparationRule rule;

	private BoxItem previous;
	private BoxItem next;

	public BoxItem() {
		setWidget(uiBinder.createAndBindUi(this));
		this.top = 115;
		this.left = 25;
	}
	
	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public BoxItem getPrevious() {
		return previous;
	}
	
	public void setPrevious(BoxItem previous) {
		this.previous = previous;
	}

	public BoxItem getNext() {
		return next;
	}
	
	public void setNext(BoxItem next) {
		this.next = next;
	}
	
	public PreparationRule getRule() {
		return rule;
	}

	public void setRule(PreparationRule rule) {
		this.rule = rule;
	}
	
	public void setColor(String rule) {
		box.getElement().getStyle().clearBackgroundColor();
		if(rule == LabelsConstants.lblCnst.RuleCatText()) {
			box.getElement().getStyle().setBackgroundColor("#F0F8FF");
		}
		else if(rule == LabelsConstants.lblCnst.RuleCatNumber()) {
			box.getElement().getStyle().setBackgroundColor("#98FB98");
		}
		else if(rule == LabelsConstants.lblCnst.RuleCatDate()) {
			box.getElement().getStyle().setBackgroundColor("#FFFFE0");
		}
		else if(rule == LabelsConstants.lblCnst.RuleCatCalc()) {
			box.getElement().getStyle().setBackgroundColor("#DCDCDC");
		}
		else if(rule == LabelsConstants.lblCnst.RuleCatAdvanced()) {
			box.getElement().getStyle().setBackgroundColor("#E6E6FA");
		}
		else if(rule == "Libre Office") {
			box.getElement().getStyle().setBackgroundColor("#FFD7B5");
		}
		else {
			box.getElement().getStyle().setBackgroundColor("#FFFFFF");
		}
		
	}
}
