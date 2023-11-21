package bpm.gwt.workflow.commons.client.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.resources.properties.VariableResourceProperties;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class VariablesPanel extends ResourcePanel<Variable> {

	public VariablesPanel(IResourceManager resourceManager) {
		super(LabelsCommon.lblCnst.Variables(), Images.INSTANCE.ic_polymer_black_18dp(), LabelsCommon.lblCnst.AddVariable(), resourceManager);
	}

	@Override
	public void loadResources() {
		getResourceManager().loadVariables(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return LabelsCommon.lblCnst.DeleteVariableConfirm();
	}

	@Override
	protected List<ColumnWrapper<Variable>> buildCustomColumns(TextCell cell, ListHandler<Variable> sortHandler) {
		Column<Variable, String> colValue = new Column<Variable, String>(cell) {

			@Override
			public String getValue(Variable object) {
				return object.getValueDisplay();
			}
		};
		
		List<ColumnWrapper<Variable>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<Variable>(colValue, LabelsCommon.lblCnst.Definition(), null));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new VariableResourceProperties(this, getResourceManager(), resource != null ? (Variable) resource : null);
	}
}
