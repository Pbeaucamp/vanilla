package bpm.architect.web.client.panels.rules;

import java.util.List;

import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonChild;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ColumnComparisonSubPanel extends Composite {

	private static ColumnComparisonSubPanelUiBinder uiBinder = GWT.create(ColumnComparisonSubPanelUiBinder.class);

	interface ColumnComparisonSubPanelUiBinder extends UiBinder<Widget, ColumnComparisonSubPanel> {
	}

	@UiField
	ListBoxWithButton<ClassField> lstColumns;

	@UiField
	ListBoxWithButton<OperatorClassic> lstOperators;

	@UiField
	LabelTextBox txtValue;

	@UiField
	Label lblIn;
	
	private ColumnComparisonPanel parent;

	public ColumnComparisonSubPanel(ColumnComparisonPanel parent, RuleColumnComparisonChild child, List<ClassField> fields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		
		lstColumns.setList(fields);
		lstOperators.setList(OperatorClassic.values());

		if (child != null) {
			for (int i = 0; i < fields.size(); i++) {
				if (fields.get(i).getName().equals(child.getFieldName()))
					lstColumns.setSelectedIndex(i);
			}
			lstOperators.setSelectedObject(child.getOperator());
			txtValue.setText(child.getValue());
		}

		updateUi();
	}

	@UiHandler("btnDelete")
	public void onDeleteComparison(ClickEvent event) {
		parent.removeComparison(this);
	}

	@UiHandler("lstOperators")
	public void onOperatorChange(ChangeEvent event) {
		updateUi();
	}

	private void updateUi() {
		OperatorClassic op = lstOperators.getSelectedObject();
		lblIn.setVisible(op != null && op == OperatorClassic.IN);
	}

	public RuleColumnComparisonChild getChild() {
		ClassField field = lstColumns.getSelectedObject();
		OperatorClassic op = lstOperators.getSelectedObject();
		String value = txtValue.getText();
		
		if (value == null || value.isEmpty()) {
			return null;
		}
		
		return new RuleColumnComparisonChild(field.getName(), op, value);
	}
}
