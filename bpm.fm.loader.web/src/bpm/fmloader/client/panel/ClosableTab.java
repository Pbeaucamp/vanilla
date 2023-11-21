package bpm.fmloader.client.panel;

import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.images.ImageResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ClosableTab extends AbsolutePanel {
	private static final String CSS_IMG_CLOSE = "imgClose";

	private Label lbl;

	private CustomTabLayoutPanel tabPanel;

	private Widget tab;
	
	public ClosableTab(String tabText, CustomTabLayoutPanel tabPanel, Widget tab) {
		super();
		
		this.tabPanel = tabPanel;
		this.tab = tab;
		HorizontalPanel tabHeader = new HorizontalPanel();
		
		lbl = new Label(tabText);
		lbl.setWordWrap(false);
		tabHeader.add(lbl);
		
		Image img = new Image(ImageResources.INSTANCE.close());
		img.addStyleName(CSS_IMG_CLOSE);
		img.addClickHandler(closeClickHandler);
		
		tabHeader.add(new HTML("&nbsp;"));
		tabHeader.add(img);
		tabHeader.setCellHorizontalAlignment(img, HorizontalPanel.ALIGN_RIGHT);
		tabHeader.setCellVerticalAlignment(img, HorizontalPanel.ALIGN_TOP);
		
		this.add(tabHeader);
	}
	
	public void changeTabTitle(String tabTitle){
		this.lbl.setText(tabTitle);
	}
	
	private ClickHandler closeClickHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(Window.confirm(Constantes.LBL.confirmclosetab())) {
				tabPanel.remove(tab);
			}
		}
	};
	
}
