package bpm.fd.web.client.panels.properties;

import bpm.fd.core.DashboardComponent;
import bpm.fd.core.IComponentUrl;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.ChartOption;
import bpm.fd.core.component.D4CComponent;
import bpm.fd.core.component.DivComponent;
import bpm.fd.core.component.FlourishComponent;
import bpm.fd.core.component.LabelComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.core.component.RChartOption;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.custom.LabelRichTextArea;
import bpm.gwt.commons.client.custom.LabelTextArea;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.dialog.D4CItemManager;
import bpm.vanilla.platform.core.beans.resources.D4CItem;
import bpm.vanilla.platform.core.beans.resources.D4CItemMap;
import bpm.vanilla.platform.core.beans.resources.D4CItemTable;
import bpm.vanilla.platform.core.beans.resources.D4CItemVisualization;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class GeneralProperties extends CompositeProperties<DashboardComponent> {

	private static GeneralPropertiesUiBinder uiBinder = GWT.create(GeneralPropertiesUiBinder.class);

	interface GeneralPropertiesUiBinder extends UiBinder<Widget, GeneralProperties> {
	}	
	
	@UiField
	LabelTextBox txtTitle, txtOption, txtCss;
	
	@UiField
	HTMLPanel panelOption;
	
	@UiField
	LabelRichTextArea txtRichTitle;
	
	@UiField
	LabelTextArea txtDescription;
	
	@UiField
	Button btnOption;
	
	public GeneralProperties(DashboardComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		txtTitle.setText(component.getTitle());
		txtRichTitle.setText(component.getTitle());
		txtCss.setText(component.getCssClass());
		txtDescription.setText(component.getComment());
		
		if (component instanceof ChartComponent) {
			ChartOption option = ((ChartComponent) component).getOption();
			
			txtOption.setPlaceHolder(Labels.lblCnst.Subtitle());
			txtOption.setText(option != null ? option.getSubCaption() : "");
			panelOption.setVisible(true);
			txtOption.setVisible(true);
		}
		else if (component instanceof LabelComponent) {
			txtTitle.setVisible(false);
			txtRichTitle.setVisible(true);
		}
		else if (component instanceof IComponentUrl) {
			txtOption.setPlaceHolder(Labels.lblCnst.URLAndParam());
			txtOption.setText(((IComponentUrl) component).getUrl());
			txtOption.setVisible(true);
			panelOption.setVisible(true);
			
			if (component instanceof D4CComponent) {
				btnOption.setVisible(true);
			}
			else if (component instanceof FlourishComponent) {
				txtOption.setPlaceHolder(Labels.lblCnst.FlourishIdAndParam());
			}
		}
		else if (component instanceof DivComponent) {
			txtTitle.setVisible(false);
		}
	}

	@Override
	public void buildProperties(DashboardComponent component) {
		String title = txtTitle.getText();
		String richTitle = txtRichTitle.getText();
		String complement = txtOption.getText();
		String cssClass = txtCss.getText();
		String description = txtDescription.getText();
		
		if (component instanceof LabelComponent) {
			component.setTitle(richTitle);
		}
		else {
			component.setTitle(title);
		}
		component.setCssClass(cssClass);
		component.setComment(description);
		
		if (component instanceof ChartComponent) {
			ChartOption option = ((ChartComponent) component).getOption();
			if (option == null) {
				option = new ChartOption();
				((ChartComponent) component).setOption(option);
			}
			option.setSubCaption(complement);
		}
		else if (component instanceof IComponentUrl) {
			((IComponentUrl) component).setUrl(complement);
		}
		else if (component instanceof RChartComponent) {
			RChartOption option = ((RChartComponent) component).getOption();
			if (option == null) {
				option = new RChartOption();
				((RChartComponent) component).setOption(option);
			}
			
		}
	}
	
	@UiHandler("btnOption")
	public void onBtnClick(ClickEvent event) {
		final D4CItemManager dial = new D4CItemManager();
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					D4CItem item = dial.getSelectedItem();
					if (item instanceof D4CItemVisualization) {
						txtOption.setText(((D4CItemVisualization) item).getUrl());
					}
					else if (item instanceof D4CItemTable) {
						txtOption.setText(((D4CItemTable) item).getUrl());
					}
					else if (item instanceof D4CItemMap) {
						txtOption.setText(((D4CItemMap) item).getUrl());
					}
				}
			}
		});
	}
}