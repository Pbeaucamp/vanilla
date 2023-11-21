package bpm.gwt.commons.client.popup;

import java.util.List;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.ReportViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class FormatPopup extends PopupPanel {

	private static FormatUiBinder uiBinder = GWT.create(FormatUiBinder.class);

	interface FormatUiBinder extends UiBinder<Widget, FormatPopup> {
	}
	
	interface MyStyle extends CssResource {
		String labelType();
		String labelTypeEnd();
	}
	
	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;
	
	private IChangeFormat viewer;
	
	public FormatPopup(IChangeFormat viewer, List<String> formats) {
		setWidget(uiBinder.createAndBindUi(this));
		this.viewer = viewer;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		boolean first = true;
		for(String format : formats){
			addFormat(format, first);
			first = false;
		}
		
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}
	
	private void addFormat(String format, boolean first) {
		Label lblTheme = new Label(format);
		lblTheme.addClickHandler(new FormatHandler(format));
		if(first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}
		
		panelMenu.add(lblTheme);
	}
	
	private class FormatHandler implements ClickHandler {
		
		private String format;

		public FormatHandler(String format) {
			this.format = format;
		}
		
		@Override
		public void onClick(ClickEvent event) {
			viewer.changeFormat(format);
			FormatPopup.this.hide();
		}
	}
}
