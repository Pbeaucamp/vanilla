package bpm.architect.web.client.panels.rules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.panels.IRulePanel;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.RuleClassColumnNull;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class ClassColumnNullPanel extends Composite implements IRulePanel {

	private static ColumnComparisonPanelUiBinder uiBinder = GWT.create(ColumnComparisonPanelUiBinder.class);

	interface ColumnComparisonPanelUiBinder extends UiBinder<Widget, ClassColumnNullPanel> {
	}

	@UiField
	DefinitionRulePanel panelDefinition;

	@UiField
	SimplePanel gridPanel;

	private ClassRule rule;

	private ListDataProvider<ClassField> dataProvider;
	private ListHandler<ClassField> sortHandler;
	private MultiSelectionModel<ClassField> selectionModel;
	
	private List<ClassField> fields;

	public ClassColumnNullPanel(ClassRule rule, List<ClassField> fields) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		this.fields = fields;

		gridPanel.setWidget(buildDataGrid());
		loadFields(fields);

		if (rule != null && rule.getType() == TypeRule.CLASS_COLUMN_NULL) {
			panelDefinition.setDefinition(rule.getName(), rule.getDescription(), rule.isEnabled());
			
			RuleClassColumnNull ruleDef = (RuleClassColumnNull) rule.getRule();

			if (ruleDef != null && ruleDef.getFieldNames() != null) {
				for (String fieldName : ruleDef.getFieldNames()) {

					for (ClassField field : fields) {
						if (field.getName().equals(fieldName))
							selectionModel.setSelected(field, true);
					}
				}
			}
		}
	}
	
	private void loadFields(List<ClassField> fields) {
		if (fields == null) {
			fields = new ArrayList<>();
		}
		dataProvider.setList(fields);
		sortHandler.setList(dataProvider.getList());
	}
	
	private DataGrid<ClassField> buildDataGrid() {
		sortHandler = new ListHandler<ClassField>(new ArrayList<ClassField>());
		
		TextCell cell = new TextCell();
		Column<ClassField, Boolean> colCheck = new Column<ClassField, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(ClassField object) {
				return selectionModel.isSelected(object);
			}
		};

		Column<ClassField, String> colName = new Column<ClassField, String>(cell) {

			@Override
			public String getValue(ClassField object) {
				return object.getName();
			}
		};
		colName.setSortable(true);
		sortHandler.setComparator(colName, new Comparator<ClassField>() {

			@Override
			public int compare(ClassField o1, ClassField o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		DataGrid<ClassField> datagrid = new DataGrid<>(30);
		datagrid.setSize("100%", "100%");
		datagrid.addColumn(colCheck);
		datagrid.setColumnWidth(colCheck, "100px");
		datagrid.addColumn(colName, Labels.lblCnst.Name());

		dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(datagrid);
		
		selectionModel = new MultiSelectionModel<ClassField>();
		datagrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<ClassField> createCheckboxManager());
		datagrid.addColumnSortHandler(sortHandler);

		return datagrid;
	}

	@Override
	public ClassRule getClassRule() {
		String name = panelDefinition.getName();
		String description = panelDefinition.getDescription();
		boolean enabled = panelDefinition.isEnabled();
		
		RuleClassColumnNull ruleDef = new RuleClassColumnNull();

		List<String> selectedFields = new ArrayList<>();
		for (ClassField field : fields) {
			if (selectionModel.isSelected(field)) {
				selectedFields.add(field.getName());
			}
		}

		if (selectedFields.isEmpty()) {
			// TODO: Message
			return null;
		}

		ruleDef.setFieldNames(selectedFields);

		if (rule == null) {
			rule = new ClassRule();
		}
		rule.setName(name);
		rule.setDescription(description);
		rule.setEnabled(enabled);
		rule.setType(TypeRule.CLASS_COLUMN_NULL);
		rule.setRule(ruleDef);

		return rule;
	}
}
