package bpm.architect.web.client.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.dialogs.RuleDialog;
import bpm.architect.web.client.dialogs.SchemaAPIDialog;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.architect.web.client.tree.ClassTree;
import bpm.architect.web.client.tree.ClassTree.IRulesListener;
import bpm.architect.web.client.tree.ClassTreeItem;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dialog.UploadDialog;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.IClassItem;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.OperatorDate;
import bpm.vanilla.platform.core.beans.resources.OperatorListe;
import bpm.vanilla.platform.core.beans.resources.OperatorOperation;
import bpm.vanilla.platform.core.beans.resources.RuleClassColumnNull;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparison;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonBetweenColumn;
import bpm.vanilla.platform.core.beans.resources.RuleColumnComparisonDate;
import bpm.vanilla.platform.core.beans.resources.RuleColumnDateComparison;
import bpm.vanilla.platform.core.beans.resources.RuleColumnOperation;
import bpm.vanilla.platform.core.beans.resources.RuleDBComparison;
import bpm.vanilla.platform.core.beans.resources.RuleExclusionValue;
import bpm.vanilla.platform.core.beans.resources.RulePatternComparison;
import bpm.vanilla.platform.core.beans.resources.RuleValueComparison;

public class RulesPanel extends CompositeWaitPanel implements IRulesListener {
	
	private static final DateTimeFormat df = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static RulesPanelUiBinder uiBinder = GWT.create(RulesPanelUiBinder.class);

	interface RulesPanelUiBinder extends UiBinder<Widget, RulesPanel> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();

		String pager();
	}

	@UiField
	MyStyle style;
	
	@UiField
	Image btnAddSchema, btnAddAPISchema, btnDeleteSchema, btnViewSchema;

	@UiField
	ListBoxWithButton<ClassDefinition> lstClasses;

	@UiField
	SimplePanel panelTree, gridPanel, panelPager;

	private ClassTree classTree;

	private ListDataProvider<ClassRule> dataProvider;
	private ListHandler<ClassRule> sortHandler;
	
	private InfoUser infoUser;

	// private MultiSelectionModel<ClassRule> selectionModel;

	public RulesPanel(InfoUser infoUser) {
		initWidget(uiBinder.createAndBindUi(this));
		this.infoUser = infoUser;

		classTree = new ClassTree(this);
		panelTree.setWidget(classTree);

		gridPanel.setWidget(buildDataGrid());

		loadAvailableClasses(infoUser.getClasses());
	}

	private void loadAvailableClasses(List<ClassDefinition> availableClasses) {
		lstClasses.setList(availableClasses);
		loadRules();
	}

	private void loadRules(List<ClassRule> rules) {
		if (rules == null) {
			rules = new ArrayList<>();
		}
		dataProvider.setList(rules);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<ClassRule> buildDataGrid() {
		sortHandler = new ListHandler<ClassRule>(new ArrayList<ClassRule>());

		TextCell cell = new TextCell();

		Column<ClassRule, String> colName = new Column<ClassRule, String>(cell) {

			@Override
			public String getValue(ClassRule object) {
				return buildDefinition(object);
			}
		};
		colName.setSortable(true);
		sortHandler.setComparator(colName, new Comparator<ClassRule>() {

			@Override
			public int compare(ClassRule o1, ClassRule o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		ButtonImageCell editCell = new ButtonImageCell(Images.INSTANCE.ic_edit_black_18dp(), style.imgGrid());
		Column<ClassRule, String> colEdit = new Column<ClassRule, String>(editCell) {

			@Override
			public String getValue(ClassRule object) {
				return "";
			}
		};
		colEdit.setFieldUpdater(new FieldUpdater<ClassRule, String>() {

			@Override
			public void update(int index, ClassRule object, String value) {
				IClassItem item = object.getParent();

				List<ClassField> fields = new ArrayList<>();
				if (item instanceof ClassDefinition) {
					fields = ((ClassDefinition) item).getFields();
					
					if (fields == null || fields.isEmpty()) {
						return;
					}
				}
				else if (item instanceof ClassField) {
					fields = ((ClassField) item).getParent().getFields();
				}

				RuleDialog dial = new RuleDialog(RulesPanel.this, item, fields, object);
				dial.center();
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<ClassRule, String> colDelete = new Column<ClassRule, String>(deleteCell) {

			@Override
			public String getValue(ClassRule object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<ClassRule, String>() {

			@Override
			public void update(int index, final ClassRule object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.DeleteClassRuleConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteClassRule(object);
						}
					}
				});
				dial.center();
			}
		});

		DataGrid<ClassRule> datagrid = new DataGrid<>(30);
		datagrid.setSize("100%", "100%");
		datagrid.addColumn(colName, Labels.lblCnst.Definition());
		datagrid.addColumn(colEdit);
		datagrid.setColumnWidth(colEdit, "70px");
		datagrid.addColumn(colDelete);
		datagrid.setColumnWidth(colDelete, "70px");

		dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(datagrid);

		// selectionModel = new MultiSelectionModel<ClassRule>();
		// datagrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<ClassRule> createCheckboxManager());
		datagrid.addColumnSortHandler(sortHandler);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(datagrid);

		panelPager.add(pager);

		return datagrid;
	}

	private String buildDefinition(ClassRule object) {
		StringBuffer buf = new StringBuffer();

		switch (object.getType()) {
		case VALUE_COMPARAISON:
			RuleValueComparison ruleVC = (RuleValueComparison) object.getRule();

			buf.append(Labels.lblCnst.TheValueMustBe());
			buf.append(" ");
			buf.append(getOperatorClassicLabel(ruleVC.getFirstOperator()));
			buf.append(" '");
			buf.append(ruleVC.getFirstValue());
			buf.append("'");
			if (ruleVC.hasLastValue()) {
				buf.append(". ");
				buf.append(Labels.lblCnst.TheValueMustBe());
				buf.append(" ");
				buf.append(getOperatorClassicLabel(ruleVC.getLastOperator()));
				buf.append(" '");
				buf.append(ruleVC.getLastValue());
				buf.append("'");
			}
			buf.append(buildExclusionValueDefinition(ruleVC));
			break;
		case COLUMN_COMPARAISON:
			RuleColumnComparison ruleCC = (RuleColumnComparison) object.getRule();

			buf.append(Labels.lblCnst.IfAllOfTheConditionsAreMet());
			if (ruleCC.isEqualToValue()) {
				buf.append(", ");
				buf.append(Labels.lblCnst.TheValueMustBe());
				buf.append(" ");
				buf.append(Labels.lblCnst.EqualTo());
				buf.append(" '");
				buf.append(ruleCC.getMainValue());
				buf.append("'");
			}
			else {
				buf.append(" ");
				buf.append(Labels.lblCnst.SoTheValueMustNotBeEmpty());
			}
			break;
		case COLUMN_DATE_COMPARAISON:
			RuleColumnDateComparison ruleCDC = (RuleColumnDateComparison) object.getRule();

			buf.append(Labels.lblCnst.TheDateMustBe());
			buf.append(" ");
			buf.append(getOperatorDateLabel(ruleCDC.getOperator()));
			buf.append(" '");
			buf.append(ruleCDC.getFieldName());
			buf.append("'");
			buf.append(buildExclusionValueDefinition(ruleCDC));
			break;
		case LISTE_DB_COMPARAISON:
			RuleDBComparison ruleDBC = (RuleDBComparison) object.getRule();

			buf.append(Labels.lblCnst.TheValueMustBe());
			buf.append(" ");
			buf.append(getOperatorListeLabel(ruleDBC.getOperator()));
			buf.append(" ");
			buf.append(Labels.lblCnst.InTheListOfValueOfTheColumn());
			buf.append(" '");
			buf.append(ruleDBC.getColumnName());
			buf.append("' ");
			buf.append(Labels.lblCnst.FromTheDataset());
			buf.append(" '");
			buf.append(ruleDBC.getDatasetName());
			buf.append("' ");
			buf.append(Labels.lblCnst.FromTheDatasource());
			buf.append(" '");
			buf.append(ruleDBC.getDatasourceName());
			buf.append("'");
			break;
		case PATTERN:
			RulePatternComparison rulePC = (RulePatternComparison) object.getRule();

			buf.append(Labels.lblCnst.TheValueMustMatchPattern());
			buf.append(" '");
			buf.append(rulePC.getRegex());
			buf.append("'");
			break;
		case CLASS_COLUMN_NULL:
			RuleClassColumnNull ruleCCN = (RuleClassColumnNull) object.getRule();

			buf.append(Labels.lblCnst.OneColumnAmong());
			if (ruleCCN.getFieldNames() != null) {
				for (String fieldName : ruleCCN.getFieldNames()) {
					buf.append(" '");
					buf.append(fieldName);
					buf.append("'");
				}
			}
			buf.append(" ");
			buf.append(Labels.lblCnst.NeedToBeFilled());
			break;
		case COLUMN_COMPARAISON_DATE:
			RuleColumnComparisonDate ruleCCD = (RuleColumnComparisonDate) object.getRule();

			buf.append(Labels.lblCnst.IfTheDateOf());
			buf.append(" '");
			buf.append(ruleCCD.getFieldName());
			buf.append("' ");
			buf.append(Labels.lblCnst.Is());
			buf.append(" ");
			buf.append(getOperatorDateLabel(ruleCCD.getOperator()));
			buf.append(" ");
			buf.append(df.format(ruleCCD.getValue()));
			buf.append(" ");
			buf.append(Labels.lblCnst.SoTheValueMustNotBeEmpty());
			buf.append(buildExclusionValueDefinition(ruleCCD));
			break;
		case COLUMN_OPERATION:
			RuleColumnOperation ruleCO = (RuleColumnOperation) object.getRule();

			buf.append(Labels.lblCnst.TheValueMustBe());
			buf.append(" ");
			buf.append(ruleCO.getOperator().getlabel());
			buf.append(" ");
			buf.append(Labels.lblCnst.At());
			buf.append(" ");
			buf.append(getOperatorOperationLabel(ruleCO.getOperatorOperation()));
			buf.append(" ");
			if (ruleCO.getFieldNames() != null) {
				for (String fieldName : ruleCO.getFieldNames()) {
					buf.append(" '");
					buf.append(fieldName);
					buf.append("'");
				}
			}
			break;
		case COLUMN_COMPARAISON_BETWEEN_COLUMN:
			RuleColumnComparisonBetweenColumn ruleCCBC = (RuleColumnComparisonBetweenColumn) object.getRule();
			
			buf.append(Labels.lblCnst.TheValueMustBe());
			buf.append(" ");
			buf.append(getOperatorClassicLabel(ruleCCBC.getOperator()));
			buf.append(" ");
			buf.append(Labels.lblCnst.OfTheValueFrom());
			buf.append(" '");
			buf.append(ruleCCBC.getFieldName());
			buf.append("'");
			buf.append(buildExclusionValueDefinition(ruleCCBC));
			break;

		default:
			break;
		}
		return buf.toString();
	}

	private Object buildExclusionValueDefinition(RuleExclusionValue rule) {
		StringBuffer buf = new StringBuffer(" ");
		if (rule.hasExclusionValue()) {
			buf.append("(");
			buf.append(Labels.lblCnst.ExcludeDefaultValue());
			buf.append(" '");
			buf.append(rule.getExclusionValue());
			buf.append("'");
			buf.append(")");
		}
		return buf.toString();
	}

	private String getOperatorClassicLabel(OperatorClassic operator) {
		switch (operator) {
		case INF:
		case INF_OR_EQUAL:
		case EQUAL:
		case NOT_EQUAL:
		case SUP_OR_EQUAL:
		case SUP:
			return operator.getlabel() + " " + Labels.lblCnst.At();
		case IN:
			return Labels.lblCnst.IncludedIn();
		case CONTAINS:
			return Labels.lblCnst.ComposedOf();
		case REGEX:
			return Labels.lblCnst.InAgreementWithThePattern();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}

	private String getOperatorDateLabel(OperatorDate operator) {
		switch (operator) {
		case BEFORE:
			return Labels.lblCnst.Before();
		case EQUAL:
			return Labels.lblCnst.Equal();
		case AFTER:
			return Labels.lblCnst.After();
		case BEFORE_OR_EQUAL:
			return Labels.lblCnst.BeforeOrEqual();
		case AFTER_OR_EQUAL:
			return Labels.lblCnst.AfterOrEqual();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}

	private String getOperatorListeLabel(OperatorListe operator) {
		switch (operator) {
		case IN:
			return Labels.lblCnst.In();
		case OUT:
			return Labels.lblCnst.Out();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}

	private String getOperatorOperationLabel(OperatorOperation operator) {
		switch (operator) {
//		case DIVIDE:
//			return Labels.lblCnst.In();
//		case MINUS:
//			return Labels.lblCnst.Out();
		case MULTIPLY:
			return Labels.lblCnst.TheMultiplication();
		case SUM:
			return Labels.lblCnst.TheSum();

		default:
			break;
		}
		return Labels.lblCnst.Unknown();
	}
	
	@UiHandler("btnAddSchema")
	public void onAddSchema(ClickEvent event) {
		UploadDialog dial = new UploadDialog(Labels.lblCnst.UploadSchema(), CommonConstants.SCHEMA_UPLOAD_SERVLET);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				loadRules();
			}
		});
	}
	
	@UiHandler("btnAddAPISchema")
	public void onAddAPISchema(ClickEvent event) {
		final SchemaAPIDialog dial = new SchemaAPIDialog();
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					loadRules();
				}
			}
		});
	}
	
	@UiHandler("btnDeleteSchema")
	public void onDeleteSchema(ClickEvent event) {
		if (classTree.getSelectedItem() instanceof ClassDefinition) {
			final ClassDefinition classDef = (ClassDefinition) classTree.getSelectedItem();
			
			final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.DeleteSchemaConfirm(), true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						deleteSchema(classDef);
					}
				}
			});
			dial.center();
		}
	}

	private void deleteSchema(ClassDefinition classDef) {
		ArchitectService.Connect.getInstance().deleteSchema(classDef, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				loadRules();
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnViewSchema")
	public void onViewSchema(ClickEvent event) {
		if (classTree.getSelectedItem() instanceof ClassDefinition) {
			final ClassDefinition classDef = (ClassDefinition) classTree.getSelectedItem();

			ArchitectService.Connect.getInstance().loadSchema(classDef, new GwtCallbackWrapper<String>(this, true, true) {

				@Override
				public void onSuccess(String result) {
					String fullUrl = GWT.getHostPageBaseURL() + result;
					ToolsGWT.doRedirect(fullUrl);
				}
			}.getAsyncCallback());
		}
	}

	@UiHandler("lstClasses")
	public void onClassChange(ChangeEvent event) {
		loadRules();
	}

	private void loadRules() {
		ClassDefinition selectedClass = lstClasses.getSelectedObject();
		classTree.loadRules(selectedClass);
		
		updateToolbar();
	}

	private void updateToolbar() {
		ClassDefinition selectedClass = lstClasses.getSelectedObject();
		btnAddSchema.setVisible(selectedClass.getIdentifiant().equals("schema_validation"));
		btnAddAPISchema.setVisible(selectedClass.getIdentifiant().equals("schema_validation"));
		btnDeleteSchema.setVisible(classTree.getSelectedItem() != null && classTree.getSelectedItem() instanceof ClassDefinition);
		btnViewSchema.setVisible(classTree.getSelectedItem() != null && classTree.getSelectedItem() instanceof ClassDefinition);
	}

	@Override
	public void loadRules(ClassTreeItem<?> item) {
		if (item.getItem() instanceof IClassItem) {
			loadRules((IClassItem) item.getItem());
		}
	}

	private void loadRules(IClassItem item) {
		loadRules(item.getRules());
		
		updateToolbar();
	}

	public void refreshSelectedField() {
		if (classTree.getSelectedItem() instanceof IClassItem) {
			classTree.refreshSelectedItemLabel();
			loadRules((IClassItem) classTree.getSelectedItem());
		}
	}

	@UiHandler("btnAddRule")
	public void onAddRule(ClickEvent event) {
		if (classTree.getSelectedItem() instanceof IClassItem) {
			IClassItem item = (IClassItem) classTree.getSelectedItem();

			List<ClassField> fields = new ArrayList<>();
			if (item instanceof ClassDefinition) {
				fields = ((ClassDefinition) item).getFields();
				
				if (fields == null || fields.isEmpty()) {
					return;
				}
			}
			else if (item instanceof ClassField) {
				fields = ((ClassField) item).getParent().getFields();
			}

			RuleDialog dial = new RuleDialog(this, item, fields);
			dial.center();
		}
	}

	private void deleteClassRule(final ClassRule rule) {
		final IClassItem field = rule.getParent();

		ArchitectService.Connect.getInstance().deleteClassRule(rule, new GwtCallbackWrapper<Void>(this, true, true) {

			@Override
			public void onSuccess(Void result) {
				field.removeRule(rule);

				refreshSelectedField();
			}
		}.getAsyncCallback());
	}
	
	public InfoUser getInfoUser() {
		return infoUser;
	}
}
