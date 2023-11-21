package bpm.architect.web.client.dialogs;

import java.util.ArrayList;
import java.util.List;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.panels.IRulePanel;
import bpm.architect.web.client.panels.RulesPanel;
import bpm.architect.web.client.panels.rules.ClassColumnNullPanel;
import bpm.architect.web.client.panels.rules.ColumnComparisonBetweenColumnPanel;
import bpm.architect.web.client.panels.rules.ColumnComparisonDatePanel;
import bpm.architect.web.client.panels.rules.ColumnComparisonPanel;
import bpm.architect.web.client.panels.rules.ColumnDateComparisonPanel;
import bpm.architect.web.client.panels.rules.ColumnOperationPanel;
import bpm.architect.web.client.panels.rules.ListDBComparisonPanel;
import bpm.architect.web.client.panels.rules.PatternPanel;
import bpm.architect.web.client.panels.rules.ValueComparisonPanel;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassField.TypeField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.IClassItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RuleDialog extends AbstractDialogBox {

	private static RepositorySaveDialogUiBinder uiBinder = GWT.create(RepositorySaveDialogUiBinder.class);

	interface RepositorySaveDialogUiBinder extends UiBinder<Widget, RuleDialog> {
	}

	@UiField
	ListBoxWithButton<String> lstType;

	@UiField
	SimplePanel panelRule;

	private RulesPanel parent;
	private IRulePanel rulePanel;

	private IClassItem fieldParent;

	private ClassRule classRule;
	
	private List<ClassField> fields;
	private List<ClassField> fieldsDate;

	public RuleDialog(RulesPanel parent, IClassItem fieldParent, List<ClassField> fields) {
		super(Labels.lblCnst.RuleDefinition(), false, true);
		this.parent = parent;
		this.fieldParent = fieldParent;
		this.fields = fields;
		this.fieldsDate = buildFieldsDate(fields);

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		if (fieldParent instanceof ClassField) {
			ClassField classField = (ClassField) fieldParent;
			
			lstType.addItem(Labels.lblCnst.RuleValueComparison(), TypeRule.VALUE_COMPARAISON.getType());
	
			if (fields != null && !fields.isEmpty()) {
				lstType.addItem(Labels.lblCnst.RuleColumnComparison(), TypeRule.COLUMN_COMPARAISON_BETWEEN_COLUMN.getType());
				lstType.addItem(Labels.lblCnst.RuleColumnComparisonWithCondition(), TypeRule.COLUMN_COMPARAISON.getType());
			}
			
			if (fields != null && !fields.isEmpty()) {
				lstType.addItem(Labels.lblCnst.RuleColumnComparisonDate(), TypeRule.COLUMN_COMPARAISON_DATE.getType());
			}
			
			if (fields != null && !fields.isEmpty()) {
				lstType.addItem(Labels.lblCnst.RuleColumnOperation(), TypeRule.COLUMN_OPERATION.getType());
			}
	
			if (classField.getType() == TypeField.DATE && !fieldsDate.isEmpty()) {
				lstType.addItem(Labels.lblCnst.RuleColumnDateComparison(), TypeRule.COLUMN_DATE_COMPARAISON.getType());
			}
	
			if (classField.getType() == TypeField.ENUM || classField.getType() == TypeField.NUMERIC || classField.getType() == TypeField.STRING) {
				lstType.addItem(Labels.lblCnst.RuleListeDBComparison(), TypeRule.LISTE_DB_COMPARAISON.getType());
				lstType.addItem(Labels.lblCnst.RulePattern(), TypeRule.PATTERN.getType());
			}
	
			loadTypeRulePanel(TypeRule.VALUE_COMPARAISON, null);
		}
		else if (fieldParent instanceof ClassDefinition) {
			lstType.addItem(Labels.lblCnst.RuleClassColumnNull(), TypeRule.CLASS_COLUMN_NULL.getType());
	
			loadTypeRulePanel(TypeRule.CLASS_COLUMN_NULL, null);
		}
	}

	public RuleDialog(RulesPanel parent, IClassItem fieldParent, List<ClassField> fields, ClassRule classRule) {
		this(parent, fieldParent, fields);
		this.classRule = classRule;

		for (int i = 0; i < lstType.getItemCount(); i++) {
			int selectedType = lstType.getItemAsInteger(i);
			if (selectedType == classRule.getType().getType()) {
				lstType.setSelectedIndex(i);
				break;
			}
		}
		loadTypeRulePanel(classRule.getType(), classRule);
	}

	private List<ClassField> buildFieldsDate(List<ClassField> fields) {
		List<ClassField> fieldsDate = new ArrayList<>();
		if (fields != null) {
			for (ClassField field : fields) {
				if (field.getType() == TypeField.DATE) {
					fieldsDate.add(field);
				}
			}
		}
		return fieldsDate;
	}

	@UiHandler("lstType")
	public void onTypeChange(ChangeEvent event) {
		int selectedType = lstType.getSelectedItemAsInteger();
		TypeRule typeRule = TypeRule.valueOf(selectedType);
		loadTypeRulePanel(typeRule, classRule);
	}

	private void loadTypeRulePanel(TypeRule typeRule, ClassRule rule) {
		switch (typeRule) {
		case VALUE_COMPARAISON:
			rulePanel = new ValueComparisonPanel(rule, (ClassField) fieldParent);
			break;
		case COLUMN_COMPARAISON:
			rulePanel = new ColumnComparisonPanel(rule, (ClassField) fieldParent, fields);
			break;
		case COLUMN_DATE_COMPARAISON:
			rulePanel = new ColumnDateComparisonPanel(rule, (ClassField) fieldParent, fieldsDate);
			break;
		case LISTE_DB_COMPARAISON:
			rulePanel = new ListDBComparisonPanel(this, parent.getInfoUser().getUser(), rule, (ClassField) fieldParent);
			break;
		case PATTERN:
			rulePanel = new PatternPanel(rule, (ClassField) fieldParent);
			break;
		case CLASS_COLUMN_NULL:
			rulePanel = new ClassColumnNullPanel(rule, fields);
			break;
		case COLUMN_OPERATION:
			rulePanel = new ColumnOperationPanel(rule, fields);
			break;
		case COLUMN_COMPARAISON_DATE:
			rulePanel = new ColumnComparisonDatePanel(rule, (ClassField) fieldParent, fieldsDate);
			break;
		case COLUMN_COMPARAISON_BETWEEN_COLUMN:
			rulePanel = new ColumnComparisonBetweenColumnPanel(rule, (ClassField) fieldParent, fields);
			break;

		default:
			break;
		}

		panelRule.setWidget(rulePanel);
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (rulePanel == null) {
				return;
			}

			final ClassRule classRule = rulePanel.getClassRule();
			if (classRule != null) {
				classRule.setMainClassIdentifiant(fieldParent.getMainClassIdentifiant());
				classRule.setParentPath(fieldParent.getPath());

				ArchitectService.Connect.getInstance().saveOrUpdateClassRule(classRule, new GwtCallbackWrapper<ClassRule>(RuleDialog.this, true, true) {

					@Override
					public void onSuccess(ClassRule result) {
						fieldParent.removeRule(classRule);
						fieldParent.addRule(result);

						parent.refreshSelectedField();
						hide();
					}
				}.getAsyncCallback());
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
