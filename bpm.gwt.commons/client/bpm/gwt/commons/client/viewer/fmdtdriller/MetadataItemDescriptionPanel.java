package bpm.gwt.commons.client.viewer.fmdtdriller;

import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.utils.Splitter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MetadataItemDescriptionPanel extends Composite {

	private static MetadataItemDescriptionPanelUiBinder uiBinder = GWT.create(MetadataItemDescriptionPanelUiBinder.class);

	interface MetadataItemDescriptionPanelUiBinder extends UiBinder<Widget, MetadataItemDescriptionPanel> {
	}
	
	interface MyStyle extends CssResource {
		String splitter();
		String displayNone();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelSplitter, panelDescription;
	
	@UiField
	Label lblDescription;
	
	@UiField
	Image imgLeftArrow, imgRightArrow;
	
	private IExpand expandWidget;
	private boolean isExpand = false;

	public MetadataItemDescriptionPanel(IExpand expandWidget, Widget widgetTop, Widget widgetBottom) {
		initWidget(uiBinder.createAndBindUi(this));
		this.expandWidget = expandWidget;
		
		panelSplitter.setWidget(new Splitter(widgetTop, widgetBottom, style.splitter(), true));
		
		panelDescription.addStyleName(style.displayNone());
		panelSplitter.setVisible(false);
	}
	
	public void updateDescription(String description) {
		lblDescription.setText(description);
	}
	
	@UiHandler("panelTitle")
	public void onTitleClick(ClickEvent event) {
		isExpand = !isExpand;
		
		if (isExpand) {
			imgLeftArrow.setResource(CommonImages.INSTANCE.sortDescending());
			imgRightArrow.setResource(CommonImages.INSTANCE.sortDescending());
			
			panelDescription.removeStyleName(style.displayNone());
			panelSplitter.setVisible(true);
		}
		else {
			imgLeftArrow.setResource(CommonImages.INSTANCE.sortAscending());
			imgRightArrow.setResource(CommonImages.INSTANCE.sortAscending());

			panelDescription.addStyleName(style.displayNone());
			panelSplitter.setVisible(false);
		}
		
		expandWidget.expand(isExpand);
	}
}
