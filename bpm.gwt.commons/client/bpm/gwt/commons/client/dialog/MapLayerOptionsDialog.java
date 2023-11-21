package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.map.core.design.MapLayerOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class MapLayerOptionsDialog extends AbstractDialogBox {

	private static MapLayerOptionsDialogUiBinder uiBinder = GWT.create(MapLayerOptionsDialogUiBinder.class);

	interface MapLayerOptionsDialogUiBinder extends UiBinder<Widget, MapLayerOptionsDialog> {
	}

	@UiField
	LabelTextBox lblOpacity;

	private boolean confirm = false;

	public MapLayerOptionsDialog(MapLayerOption option) {
		super(LabelsConstants.lblCnst.Options(), false, true);

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		if (option != null) {
			lblOpacity.setText(option.getOpacity() != null ? String.valueOf(option.getOpacity()) : String.valueOf(MapLayerOption.DEFAULT_OPACITY));
		}
		else {
			lblOpacity.setText(String.valueOf(MapLayerOption.DEFAULT_OPACITY));
		}
	}

	private Integer getOpacity() {
		String opacityAsString = lblOpacity.getText();
		try {
			Integer opacity = Integer.parseInt(opacityAsString);
			return opacity >= 0 && opacity <= 100 ? opacity : null;
		} catch (Exception e) {
			return null;
		}
	}

	public MapLayerOption getOptions() {
		Integer opacity = getOpacity();
		return new MapLayerOption(opacity);
	}

	private boolean isComplete() {
		Integer opacity = getOpacity();
		if (opacity == null) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.OpacityErrorMessage());
			return false;
		}
		
		return true;
	}

	public boolean isConfirm() {
		return confirm;
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (!isComplete()) {
				return;
			}

			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
