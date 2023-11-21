package bpm.faweb.client.dialog;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.panels.center.DrillThroughContainer;
import bpm.faweb.shared.drill.DrillthroughFilter;
import bpm.faweb.shared.drill.DrillthroughFilter.FilterType;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.shared.analysis.DrillInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddFilterDialog extends AbstractDialogBox {

	private static AddPromptsDialogUiBinder uiBinder = GWT.create(AddPromptsDialogUiBinder.class);

	interface AddPromptsDialogUiBinder extends UiBinder<Widget, AddFilterDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	ListBox lstTypes, lstColumns;

	@UiField
	TextBox txtValue;

	private DrillThroughContainer drillThroughContainer;

	public AddFilterDialog(DrillThroughContainer drillThroughContainer, DrillInformations drillInfo) {
		super(FreeAnalysisWeb.LBL.addFilter(), false, false);
		this.drillThroughContainer = drillThroughContainer;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		for (int i = 0; i < drillInfo.getColumns().size(); i++) {
			lstColumns.addItem(drillInfo.getColumns().get(i), String.valueOf(i));
		}

		for (FilterType type : FilterType.values()) {
			lstTypes.addItem(getFilterName(type), String.valueOf(type.getType()));
		}
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			int columnIndex = Integer.parseInt(lstColumns.getValue(lstColumns.getSelectedIndex()));
			String columnName = lstColumns.getItemText(lstColumns.getSelectedIndex());
			int type = Integer.parseInt(lstTypes.getValue(lstTypes.getSelectedIndex()));
			String value = txtValue.getText();

			DrillthroughFilter filter = new DrillthroughFilter(columnIndex, columnName, getFilterType(type), value);
			drillThroughContainer.addFilter(filter);

			AddFilterDialog.this.hide();
		}
	};
	
	private FilterType getFilterType(int typeIndex) {
		for(FilterType type : FilterType.values()) {
			if(type.getType() == typeIndex) {
				return type;
			}
		}
		return null;
	}
	
	private String getFilterName(FilterType type) {
		switch (type) {
		case BETWEEN:
			return FreeAnalysisWeb.LBL.Between();
		case CONTAINS:
			return FreeAnalysisWeb.LBL.Contains();
		case DOES_NOT_CONTAINS:
			return FreeAnalysisWeb.LBL.NotContains();
		case ENDS_WITH:
			return FreeAnalysisWeb.LBL.EndWith();
		case GREATER_THAN:
			return FreeAnalysisWeb.LBL.GreaterThan();
		case GREATER_THAN_OR_EQUAL_TO:
			return FreeAnalysisWeb.LBL.GreaterOrEqual();
		case IS_EMPTY:
			return FreeAnalysisWeb.LBL.Empty();
		case LESS_THAN:
			return FreeAnalysisWeb.LBL.LessThan();
		case LESS_THAN_OR_EQUAL_TO:
			return FreeAnalysisWeb.LBL.LessOrEqual();
		case NOT_BETWEEN:
			return FreeAnalysisWeb.LBL.NotBetween();
		case NOT_IS_EMPTY:
			return FreeAnalysisWeb.LBL.NotEmpty();
		case STARTS_WITH:
			return FreeAnalysisWeb.LBL.StartWith();

		default:
			break;
		}
		
		return "";
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			AddFilterDialog.this.hide();
		}
	};
}
