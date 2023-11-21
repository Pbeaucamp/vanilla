package bpm.fd.web.client.wizard;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.popup.PropertiesPopup;
import bpm.fd.web.client.widgets.DashboardWidget;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TemplatePropertiesWizard extends AbstractDialogBox {

	private static RepositoryDialogUiBinder uiBinder = GWT.create(RepositoryDialogUiBinder.class);

	interface RepositoryDialogUiBinder extends UiBinder<Widget, TemplatePropertiesWizard> {
	}

	@UiField
	SimplePanel contentPanel;
	
	private Button btnPrevious, btnNext;
	
	private List<WidgetProperties> widgets = new ArrayList<>();
	private DashboardWidget selectedWidget;
	private int displayIndex;

	public TemplatePropertiesWizard(List<DashboardWidget> widgets) {
		super(Labels.lblCnst.DashboardDefinition(), true, true);

		setWidget(uiBinder.createAndBindUi(this));
		setGlassEnabled(false);

		createButton(LabelsConstants.lblCnst.Cancel(), cancelHandler);
		createButton(LabelsConstants.lblCnst.Confirmation(), confirmHandler);
		btnNext = createButton(LabelsConstants.lblCnst.Next(), nextPropertiesHandler);
		btnPrevious = createButton(LabelsConstants.lblCnst.Back(), previousPropertiesHandler);

		for (DashboardWidget widget : widgets) {
			this.widgets.add(new WidgetProperties(widget));
		}
		displayProperties(0);
	}

	private void displayProperties(int displayIndex) {
		this.displayIndex = displayIndex;
		
		if (selectedWidget != null) {
			selectedWidget.deselect();
		}
		
		WidgetProperties widgetProp = widgets.get(displayIndex);
		this.selectedWidget = widgetProp.getWidget();
		if (widgetProp.getPropertiesPanel() == null) {
			widgetProp.setPropertiesPanel(widgetProp.getWidget().getPropertiesPanel());
		}
		
		selectedWidget.select();
		
		contentPanel.setWidget(widgetProp.getPropertiesPanel());
		
		updateUi();
	}
	
	private void updateUi() {
		btnPrevious.setEnabled(displayIndex > 0);
		btnNext.setEnabled(displayIndex < widgets.size() - 1);
	}

	private ClickHandler previousPropertiesHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			displayProperties(displayIndex - 1);
		}
	};

	private ClickHandler nextPropertiesHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			displayProperties(displayIndex + 1);
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (selectedWidget != null) {
				selectedWidget.deselect();
			}
			
			for (WidgetProperties widgetProp : widgets) {
				PropertiesPopup properties = widgetProp.getPropertiesPanel();
				properties.getPropertiesPanel().confirm();
				
				DashboardWidget widget = widgetProp.getWidget();
				widget.refreshUiFromProperties();
			}
			
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
	
	private class WidgetProperties {
		
		private DashboardWidget widget;
		private PropertiesPopup propertiesPanel;
		
		public WidgetProperties(DashboardWidget widget) {
			this.widget = widget;
		}
		
		public DashboardWidget getWidget() {
			return widget;
		}
		
		public PropertiesPopup getPropertiesPanel() {
			return propertiesPanel;
		}
		
		public void setPropertiesPanel(PropertiesPopup propertiesPanel) {
			this.propertiesPanel = propertiesPanel;
		}
	}
}
