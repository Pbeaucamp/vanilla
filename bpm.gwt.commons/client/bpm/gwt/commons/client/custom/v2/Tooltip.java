package bpm.gwt.commons.client.custom.v2;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tooltip Panel
 * 
 * Small popup which can display a text
 */
public class Tooltip extends PopupPanel {

	private static TooltipUiBinder uiBinder = GWT.create(TooltipUiBinder.class);

	interface TooltipUiBinder extends UiBinder<Widget, Tooltip> {
	}
	
	@UiField
	HTML tooltip;

	public Tooltip() {
		buildContent();
	}

	public Tooltip(String html) {
		this();
		setHTML(html);
	}
	
	private void buildContent() {
		setWidget(uiBinder.createAndBindUi(this));
		
		setAutoHideEnabled(true);
	}

	public void setHTML(String html) {
		tooltip.setHTML(html);
	}

	public void setWidth(int width) {
		tooltip.setWidth(width + "px");
	}
}
