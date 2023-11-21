package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.FilterComponent;
import bpm.fd.core.component.FilterRenderer;
import bpm.fd.web.client.I18N.Labels;
import bpm.gwt.commons.client.custom.ColorPanel;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.LabelValueTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class FilterOptionProperties extends CompositeProperties<IComponentOption> {

	private static FilterOptionPropertiesUiBinder uiBinder = GWT.create(FilterOptionPropertiesUiBinder.class);

	interface FilterOptionPropertiesUiBinder extends UiBinder<Widget, FilterOptionProperties> {
	}
	
	@UiField
	HTMLPanel panelDropDown, panelMenu, panelMultipleValues, panelSlider;
	
	@UiField
	ListBoxWithButton<Integer> filterRenderer;
	
	@UiField
	CheckBox submitOnChange, selectFirstValue, initParameterWithFirstValue, required, isVertical, multipleValues, autoRun;
	
	@UiField
	LabelTextBox defaultValue, barColor;
	
	@UiField
	LabelValueTextBox size, delay, menuSize;
	
	@UiField
	ColorPanel sliderColor;

	public FilterOptionProperties(FilterComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		filterRenderer.clear();
		for (Integer type : FilterRenderer.RENDERER_TYPES) {
			filterRenderer.addItem(findName(type), type);
		}
		
		if (component != null) {
			buildContent(component);
		}
		updateUi();
	}
	
	@UiHandler("filterRenderer")
	public void onTypeSelected(ChangeEvent event) {
		updateUi();
	}

	@UiHandler("multipleValues")
	public void onMultipleClick(ClickEvent event) {
		updateUi();
	}
	
	private int getSelectedRenderer() {
		return filterRenderer.getSelectedObject();
	}
	
	private String findName(Integer type) {
		switch (type) {
		case FilterRenderer.DROP_DOWN_LIST_BOX:
			return Labels.lblCnst.DropDownListBox();
		case FilterRenderer.SLIDER:
			return Labels.lblCnst.Slider();
		case FilterRenderer.CHECKBOX:
			return Labels.lblCnst.Checkbox();
		case FilterRenderer.RADIOBUTTON:
			return Labels.lblCnst.RadioButton();
		case FilterRenderer.LIST:
			return Labels.lblCnst.List();
		case FilterRenderer.TEXT_FIELD:
			return Labels.lblCnst.TextField();
		case FilterRenderer.DATE_PIKER:
			return Labels.lblCnst.DatePicker();
		case FilterRenderer.MENU:
			return Labels.lblCnst.Menu();
		case FilterRenderer.DYNAMIC_TEXT:
			return Labels.lblCnst.DynamicText();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}
	
	private void updateUi() {
		int selectedRenderer = getSelectedRenderer();
		
		panelDropDown.setVisible(selectedRenderer == FilterRenderer.DROP_DOWN_LIST_BOX);
		panelMenu.setVisible(selectedRenderer == FilterRenderer.MENU);
		panelMultipleValues.setVisible(selectedRenderer == FilterRenderer.DROP_DOWN_LIST_BOX);
		panelSlider.setVisible(selectedRenderer == FilterRenderer.SLIDER);
		
		panelMultipleValues.setVisible(multipleValues.getValue());
	}

	private void buildContent(FilterComponent filter) {
		if (filter.getRenderer() != null) {
			filterRenderer.setSelectedIndex(filter.getRenderer().getRendererStyle());
		}
		else {
			filterRenderer.setSelectedIndex(0);
		}
		
		submitOnChange.setValue(filter.isSubmitOnChange());
		selectFirstValue.setValue(filter.isSelectFirstValue());
		initParameterWithFirstValue.setValue(filter.isInitParameterWithFirstValue());
		required.setValue(filter.isRequired());
		isVertical.setValue(filter.isVertical());
		multipleValues.setValue(filter.isMultipleValues());
		autoRun.setValue(filter.isAutoRun());
		
		defaultValue.setText(filter.getDefaultValue());
		barColor.setText(filter.getBarColor());

		delay.setText(filter.getDelay());
			
		sliderColor.setColor(filter.getSliderColor());
		
		//TODO: Check type
		size.setText(filter.getSize());
		menuSize.setText(filter.getSize());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		FilterComponent filter = (FilterComponent) component;
		
		try {
			filter.setRenderer(FilterRenderer.getRenderer(getSelectedRenderer()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		filter.setSubmitOnChange(submitOnChange.getValue());
		filter.setSelectFirstValue(selectFirstValue.getValue());
		filter.setInitParameterWithFirstValue(initParameterWithFirstValue.getValue());
		filter.setRequired(required.getValue());
		filter.setVertical(isVertical.getValue());
		filter.setMultipleValues(multipleValues.getValue());
		filter.setAutoRun(autoRun.getValue());
		
		filter.setDefaultValue(defaultValue.getText());
		filter.setBarColor(barColor.getText());
		
		filter.setDelay(delay.getValue());
			
		filter.setSliderColor(sliderColor.getColor());
		
		//TODO: Check type
		filter.setSize(menuSize.getValue());
		filter.setSize(size.getValue());
	}
}
