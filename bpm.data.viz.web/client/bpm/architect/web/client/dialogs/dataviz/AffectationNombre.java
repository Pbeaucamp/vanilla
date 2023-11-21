package bpm.architect.web.client.dialogs.dataviz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.dialogs.dataviz.GroupRulePanel.MyStyle;
import bpm.architect.web.client.images.Images;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRuleAffecter;
import bpm.data.viz.core.preparation.PreparationRuleAffecter.Plage;
import bpm.data.viz.core.preparation.PreparationRuleMinMax;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.DataColumn.FunctionalType;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class AffectationNombre extends Composite implements IRulePanel {

	private static AffectationNombreUiBinder uiBinder = GWT.create(AffectationNombreUiBinder.class);

	interface AffectationNombreUiBinder extends UiBinder<Widget, AffectationNombre> {}
	
	

	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel groupPanel, mappingPanel;
	
	private ListDataProvider<Plage> groupProvider;
	private ListDataProvider<Entry<Serializable, Serializable>> mappingProvider;
	private PreparationRuleAffecter rule;
	
	private DataPreparationResult dataPreparationResult;
	
	public AffectationNombre( PreparationRuleAffecter rule, DataPreparationResult dataPreparationResult) {
		initWidget(uiBinder.createAndBindUi(this));
		this.rule = rule;
		this.dataPreparationResult = dataPreparationResult;
		
		DataGrid<Plage> groupGrid = createGroupGrid();
		groupPanel.add(groupGrid);
		

		
		groupProvider.setList(rule.getGroups());
	
	}





	private DataGrid<Plage> createGroupGrid() {
		DataGrid<Plage> dataGrid = new DataGrid<Plage>();
		dataGrid.setSize("100%", "100%");
		
		Column<Plage, String> colMin = new Column<Plage, String>(new EditTextCell()) {
		
			@Override
			public String getValue(Plage object) {
				return object.getMin().toString();
		
			}
		};
		colMin.setFieldUpdater(new FieldUpdater<Plage, String>() {		
			@Override
			public void update(int index, Plage object, String value) {

				object.setMin(Integer.parseInt(value));

			}
		});
		Column<Plage, String> colMax = new Column<Plage, String>(new EditTextCell()) {
			@Override
			public String getValue(Plage object) {
				return object.getMax().toString();
			}
		};
	
		colMax.setFieldUpdater(new FieldUpdater<Plage, String>() {		
			@Override
			public void update(int index, Plage object, String value) {
				
				object.setMax(Integer.parseInt(value));

			}
		});
		
		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), Labels.lblCnst.DeleteDocument(), style.imgGrid());
		Column<Plage, String> colDelete = new Column<Plage, String>(deleteCell) {

			@Override
			public String getValue(Plage object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Plage, String>() {

			@Override
			public void update(int index, final Plage object, String value) {
				rule.getGroups().remove(object);
				groupProvider.setList(rule.getGroups());
			}
		});
//		
		
		dataGrid.addColumn(colMin, Labels.lblCnst.ValMin());
		dataGrid.addColumn(colMax, Labels.lblCnst.ValMax());
		
		dataGrid.addColumn(colDelete);
		
		groupProvider = new ListDataProvider<>();
		groupProvider.addDataDisplay(dataGrid);
		
		return dataGrid;
	}

	@UiHandler("btnAdd")
	public void onAddGroup(ClickEvent event) {

		Plage p = new Plage();
		rule.getGroups().add(p);

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
		
		mappingPanel.clear();

		//mappingProvider.setList(new ArrayList<>(rule.getMappings().entrySet()));
	}

	
}
