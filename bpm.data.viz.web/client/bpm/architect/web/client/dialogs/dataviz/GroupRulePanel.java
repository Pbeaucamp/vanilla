package bpm.architect.web.client.dialogs.dataviz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleGroup;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class GroupRulePanel extends Composite implements IRulePanel {

	private static GroupRulePanelUiBinder uiBinder = GWT.create(GroupRulePanelUiBinder.class);

	interface GroupRulePanelUiBinder extends UiBinder<Widget, GroupRulePanel> {}

	interface MyStyle extends CssResource {
		String imgGrid();
	}

	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel groupPanel, mappingPanel;
	
	private PreparationRuleGroup rule;
	
	private ListDataProvider<Serializable> groupProvider;
	private ListDataProvider<Entry<Serializable, Serializable>> mappingProvider;

	private DataPreparationResult dataPreparationResult;
	
	public GroupRulePanel(PreparationRuleGroup rule, DataPreparationResult dataPreparationResult) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		this.dataPreparationResult = dataPreparationResult;
		
		DataGrid<Serializable> groupGrid = createGroupGrid();
		groupPanel.add(groupGrid);
		
		DataGrid<Entry<Serializable, Serializable>> mappingGrid = createMappingGrid();
		mappingPanel.add(mappingGrid);
		
		groupProvider.setList(rule.getGroups());
		mappingProvider.setList(new ArrayList<>(rule.getMappings().entrySet()));
	}

	private DataGrid<Entry<Serializable, Serializable>> createMappingGrid() {
		DataGrid<Entry<Serializable, Serializable>> dataGrid = new DataGrid<Entry<Serializable, Serializable>>();
		dataGrid.setSize("100%", "100%");
		
		Column<Entry<Serializable, Serializable>, String> colVal = new Column<Entry<Serializable, Serializable>, String>(new TextCell()) {
			@Override
			public String getValue(Entry<Serializable, Serializable> object) {
				return object.getKey().toString();
			}
		};
		List<String> list = rule.getGroupsAsString();
		list.add(0, "");
		Column<Entry<Serializable, Serializable>, String> colGroup = new Column<Entry<Serializable, Serializable>, String>(new SelectionCell(list)) {
			@Override
			public String getValue(Entry<Serializable, Serializable> object) {
				try {
					return object.getValue().toString();
				} catch(Exception e) {
					return "";
				}
			}
		};
		colGroup.setFieldUpdater(new FieldUpdater<Entry<Serializable,Serializable>, String>() {
			@Override
			public void update(int index, Entry<Serializable, Serializable> object, String value) {
				rule.getMappings().put(object.getKey(), value);
			}
		});
		
		dataGrid.addColumn(colVal, Labels.lblCnst.Value());
		dataGrid.addColumn(colGroup, LabelsConstants.lblCnst.Group());
		
		mappingProvider = new ListDataProvider<>();
		mappingProvider.addDataDisplay(dataGrid);
		
		return dataGrid;
	}



	private DataGrid<Serializable> createGroupGrid() {
		DataGrid<Serializable> dataGrid = new DataGrid<Serializable>();
		dataGrid.setSize("100%", "100%");
		
		Column<Serializable, String> colName = new Column<Serializable, String>(new EditTextCell()) {
			@Override
			public String getValue(Serializable object) {
				return object.toString();
			}
		};
		colName.setFieldUpdater(new FieldUpdater<Serializable, String>() {		
			@Override
			public void update(int index, Serializable object, String value) {
				rule.getGroups().remove(index);
				rule.getGroups().add(index, value);
				groupProvider.setList(rule.getGroups());
			}
		});
		
		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteDocument(), style.imgGrid());
		Column<Serializable, String> colDelete = new Column<Serializable, String>(deleteCell) {

			@Override
			public String getValue(Serializable object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Serializable, String>() {

			@Override
			public void update(int index, final Serializable object, String value) {
				rule.getGroups().remove(object);
				groupProvider.setList(rule.getGroups());
			}
		});
		
		dataGrid.addColumn(colName, Labels.lblCnst.Name());
		dataGrid.addColumn(colDelete);
		
		groupProvider = new ListDataProvider<>();
		groupProvider.addDataDisplay(dataGrid);
		
		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAddGroup(ClickEvent event) {
		rule.getGroups().add("Nouveau groupe");
		groupProvider.setList(rule.getGroups());
	}

	@Override
	public PreparationRule getRule() {
		return rule;
	}

	@Override
	public void changeColumnSelection(DataColumn column) {
		HashMap<Serializable, Serializable> set = new HashMap<Serializable, Serializable>();
		for(Map<DataColumn, Serializable> line : dataPreparationResult.getValues()) {
			set.put(line.get(column), null);
		}
		
		rule.setMappings(set);
		DataGrid<Entry<Serializable, Serializable>> mappingGrid = createMappingGrid();
		mappingPanel.clear();
		mappingPanel.add(mappingGrid);
		mappingProvider.setList(new ArrayList<>(rule.getMappings().entrySet()));
	}

}
