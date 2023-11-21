package bpm.gwt.commons.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class MapBullePopup extends Composite {

	private static MapBullePopupUiBinder uiBinder = GWT.create(MapBullePopupUiBinder.class);

	interface MapBullePopupUiBinder extends UiBinder<Widget, MapBullePopup> {
	}
	
	@UiField
	HTMLPanel bullePopup, bullePopupContent;

	public MapBullePopup() {
		initWidget(uiBinder.createAndBindUi(this));

		bullePopup.getElement().setId("bullePopup");
		bullePopup.getElement().setClassName("ol-popup");
		
		bullePopupContent.getElement().setId("bullePopup-content");
	}

}
