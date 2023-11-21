package bpm.architect.web.client.panels;

import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.data.viz.core.preparation.LinkItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MapPopup extends Composite {

	private static MapPopupUiBinder uiBinder = GWT.create(MapPopupUiBinder.class);

	interface MapPopupUiBinder extends UiBinder<Widget, MapPopup> {
	}
	
	@UiField
	HTMLPanel popup, popupCloser, popupContent, pagination, previous, pager, next;

	public MapPopup(List<LinkItem> linkedItems) {
		initWidget(uiBinder.createAndBindUi(this));
		
		popup.getElement().setId("popup");
		popup.getElement().setClassName("ol-popup");

		popupCloser.getElement().setId("popup-closer");
		popupCloser.getElement().setClassName("ol-popup-closer");

		popupContent.getElement().setId("popup-content");

		pagination.getElement().setId("pagination");
		pagination.addStyleName("ol-popup-pagination");
		previous.getElement().setId("previous");
		pager.getElement().setId("pager");
		next.getElement().setId("next");

		Label lblItems = new Label(Labels.lblCnst.ItemsLinked());
		lblItems.addStyleName("linkItemsTitle");

		HTMLPanel panelItems = new HTMLPanel("");
		panelItems.addStyleName("linkItemsPanel");
		panelItems.add(lblItems);

		if (linkedItems != null) {
			for (int i = 0; i < linkedItems.size(); i++) {
				Anchor lien = new Anchor();
				lien.getElement().setId("lien" + i);
				lien.getElement().getStyle().setMarginLeft(15, Unit.PX);
				panelItems.add(lien);
			}
		}
		
		popup.add(panelItems);
	}

}
