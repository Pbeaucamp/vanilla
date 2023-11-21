package bpm.fd.web.client.panels.properties;

import java.util.List;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.CubeElement;
import bpm.fd.core.component.CubeViewComponent;
import bpm.fd.web.client.dialogs.DimensionMeasureDialog;
import bpm.fd.web.client.services.DashboardService;
import bpm.gwt.commons.client.custom.CustomButton;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class CubeViewOptionsProperties extends CompositeProperties<IComponentOption> {

	private static MapOptionsPropertiesUiBinder uiBinder = GWT.create(MapOptionsPropertiesUiBinder.class);

	interface MapOptionsPropertiesUiBinder extends UiBinder<Widget, CubeViewOptionsProperties> {
	}

	@UiField
	CustomCheckbox isInteractive, showDimensions, showCubeFunctions;

	@UiField
	LabelTextBox lblElements;

	@UiField
	CustomButton btnSelect;

	private IWait waitPanel;
	private List<CubeElement> elements;

	public CubeViewOptionsProperties(IWait waitPanel, CubeViewComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;

		if (component != null) {
			isInteractive.setValue(component.isInteractive());
			showDimensions.setValue(component.isShowDimensions());
			showCubeFunctions.setValue(component.isShowCubeFunctions());
		}

		updateUi();

		refreshOptions(component);
	}

	@Override
	public void refreshOptions(IComponentOption component) {
		if (component instanceof CubeViewComponent) {
			CubeViewComponent cubeViewComponent = (CubeViewComponent) component;
			if (cubeViewComponent.getItemId() > 0) {

				waitPanel.showWaitPart(true);
				DashboardService.Connect.getInstance().getCubeDimensionAndMeasures(cubeViewComponent.getItemId(), cubeViewComponent.getElements(), new GwtCallbackWrapper<List<CubeElement>>(waitPanel, true) {
					@Override
					public void onSuccess(List<CubeElement> elements) {
						setElements(elements);
					}

				}.getAsyncCallback());
			}
		}
	}

	@UiHandler("isInteractive")
	public void onInteractiveClick(ClickEvent event) {
		updateUi();
	}

	@UiHandler("btnSelect")
	public void onSelectClick(ClickEvent event) {
		DimensionMeasureDialog dial = new DimensionMeasureDialog(this, elements);
		dial.center();
	}

	private void updateUi() {
		boolean interactive = isInteractive.getValue();
		showDimensions.setEnabled(interactive);
		showCubeFunctions.setEnabled(interactive);
		btnSelect.setEnabled(interactive);
	}

	public void setElements(List<CubeElement> elements) {
		this.elements = elements;

		lblElements.setText(buildText(elements));
	}

	private String buildText(List<CubeElement> elements) {
		StringBuilder buf = new StringBuilder();
		if (elements != null) {
			boolean first = true;
			for (CubeElement el : elements) {
				if (el.isVisible()) {
					if (first) {
						buf.append(el.getName());
						first = false;
					}
					else {
						buf.append(", " + el.getName());
					}
				}
			}
		}
		return buf.toString();
	}

	@Override
	public void buildProperties(IComponentOption component) {
		CubeViewComponent cubeView = (CubeViewComponent) component;

		cubeView.setInteractive(isInteractive.getValue());
		cubeView.setShowDimensions(showDimensions.getValue());
		cubeView.setShowCubeFunctions(showCubeFunctions.getValue());

		cubeView.setElements(elements);
	}

}
