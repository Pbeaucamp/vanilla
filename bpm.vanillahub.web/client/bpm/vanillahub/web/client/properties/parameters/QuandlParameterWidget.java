package bpm.vanillahub.web.client.properties.parameters;

import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.activities.attributes.QuandlParameter;
import bpm.vanillahub.core.beans.activities.attributes.QuandlParameter.TypeParam;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class QuandlParameterWidget extends Composite implements ValueChangeHandler<String> {

	private static QuandlParameterWidgetUiBinder uiBinder = GWT.create(QuandlParameterWidgetUiBinder.class);

	interface QuandlParameterWidgetUiBinder extends UiBinder<Widget, QuandlParameterWidget> {
	}

	interface MyStyle extends CssResource {
		String txt();
	}

	@UiField
	MyStyle style;

	@UiField
	ListBox lstTypeParam;

	@UiField
	SimplePanel panelTxtValue;

	@UiField
	Label lblHelp;

	@UiField
	Image btnDelete;

	private QuandlParametersPanel panelParent;

	private QuandlParameter parameter;
	private VariableTextHolderBox txtValue;

	public QuandlParameterWidget(QuandlParametersPanel panelParent, ResourceManager resourceManager, QuandlParameter parameter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.panelParent = panelParent;
		this.parameter = parameter;

		txtValue = new VariableTextHolderBox(parameter.getValue(), Labels.lblCnst.Value(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		txtValue.addValueChangeHandler(this);

		panelTxtValue.setWidget(txtValue);

		fillParamTypes(parameter);
	}

	private void fillParamTypes(QuandlParameter parameter) {
		int selectedIndex = 0;

		int i = 0;
		for (TypeParam type : TypeParam.values()) {
			lstTypeParam.addItem(getParamLabel(type), String.valueOf(type.getType()));
			if (parameter.getTypeParam() != null && parameter.getTypeParam() == type) {
				selectedIndex = i;
			}

			i++;
		}

		lstTypeParam.setSelectedIndex(selectedIndex);

		lblHelp.setText(getParamDescription(TypeParam.values()[selectedIndex]));
	}

	@UiHandler("lstTypeParam")
	public void onTypeChange(ChangeEvent event) {
		TypeParam type = TypeParam.valueOf(Integer.parseInt(lstTypeParam.getValue(lstTypeParam.getSelectedIndex())));
		lblHelp.setText(getParamDescription(type));

		txtValue.setText("");
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		panelParent.refreshGeneratedUrl();
	}

	@UiHandler("btnDelete")
	public void onDelete(ClickEvent event) {
		panelParent.removeParameter(this);
	}

	private String getParamLabel(TypeParam type) {
		switch (type) {
		case SORT_ORDER:
			return Labels.lblCnst.SortOrder();
		case EXCLUDE_HEADER:
			return Labels.lblCnst.ExcludeHeader();
		case ROWS:
			return Labels.lblCnst.Rows();
		case DATE_START:
			return Labels.lblCnst.DateStart();
		case DATE_END:
			return Labels.lblCnst.DateEnd();
		case SPECIFIC_COLUMN:
			return Labels.lblCnst.SpecificColumn();
		case FREQUENCY:
			return Labels.lblCnst.Frequency();
		case CALCULATIONS:
			return Labels.lblCnst.Calculations();
		default:
			break;
		}
		return LabelsCommon.lblCnst.Unknown();
	}

	private String getParamDescription(TypeParam type) {
		switch (type) {
		case SORT_ORDER:
			return Labels.lblCnst.SortOrderDescription();
		case EXCLUDE_HEADER:
			return Labels.lblCnst.ExcludeHeaderDescription();
		case ROWS:
			return Labels.lblCnst.RowsDescription();
		case DATE_START:
			return Labels.lblCnst.DateStartDescription();
		case DATE_END:
			return Labels.lblCnst.DateEndDescription();
		case SPECIFIC_COLUMN:
			return Labels.lblCnst.SpecificColumnDescription();
		case FREQUENCY:
			return Labels.lblCnst.FrequencyDescription();
		case CALCULATIONS:
			return Labels.lblCnst.CalculationsDescription();
		default:
			break;
		}
		return LabelsCommon.lblCnst.Unknown();
	}

	public QuandlParameter getParameter() {
		TypeParam type = TypeParam.valueOf(Integer.parseInt(lstTypeParam.getValue(lstTypeParam.getSelectedIndex())));
		VariableString value = txtValue.getVariableString();

		parameter.setTypeParam(type);
		parameter.setValue(value);
		return parameter;
	}
}
