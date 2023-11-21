package bpm.gwt.workflow.commons.client.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.resources.properties.ParameterResourceProperties;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;

public class ParametersPanel extends ResourcePanel<Parameter> {

	private IResourceManager resourceManager;
	public ParametersPanel(IResourceManager resourceManager) {
		super(LabelsCommon.lblCnst.Parameters(), Images.INSTANCE.ic_tune_black_18dp(), LabelsCommon.lblCnst.AddParameter(), resourceManager);
		this.resourceManager = resourceManager;
	}

	@Override
	public void loadResources() {
		getResourceManager().loadParameters(this, this);
	}

	@Override
	protected String getDeleteConfirmMessage() {
		return LabelsCommon.lblCnst.DeleteParameterConfirm();
	}

	@Override
	protected List<ColumnWrapper<Parameter>> buildCustomColumns(TextCell cell, ListHandler<Parameter> sortHandler) {

		Column<Parameter, String> colQuestion = new Column<Parameter, String>(cell) {

			@Override
			public String getValue(Parameter object) {
				return object.getQuestion();
			}
		};

		Column<Parameter, String> colDefaultValue = new Column<Parameter, String>(cell) {

			@Override
			public String getValue(Parameter object) {
				String defaultValue = object.getDefaultValue() != null && !object.getDefaultValue().isEmpty() ? object.getDefaultValue() : LabelsCommon.lblCnst.NotDefined();
				if(object.getParameterType().equals(TypeParameter.LOV) && resourceManager.getListOfValues() != null){
					for(ListOfValues lov : resourceManager.getListOfValues()){
						if(defaultValue.equals(lov.getId()+"")){
							defaultValue = lov.getName();
							break;
						}
					}
				}
				return LabelsCommon.lblCnst.DefaultValue() + ": " + defaultValue;
			}
		};
		
		Column<Parameter, String> colType = new Column<Parameter, String>(cell) {

			@Override
			public String getValue(Parameter object) {
				switch (object.getParameterType()) {
				case SIMPLE:
					return LabelsCommon.lblCnst.SimpleValue();
				case LOV:
					return LabelsCommon.lblCnst.ListOfValues();
				case RANGE:
					return LabelsCommon.lblCnst.RangeValue();
				case SELECTION:
					return LabelsCommon.lblCnst.SelectionList();
				case DB:
					return LabelsCommon.lblCnst.Database();
				default:
					return "";
				}
				
			}
		};

		List<ColumnWrapper<Parameter>> columns = new ArrayList<>();
		columns.add(new ColumnWrapper<Parameter>(colQuestion, LabelsCommon.lblCnst.Question(), null));
		columns.add(new ColumnWrapper<Parameter>(colDefaultValue, LabelsCommon.lblCnst.DefaultValue(), "250px"));
		columns.add(new ColumnWrapper<Parameter>(colType, LabelsCommon.lblCnst.Type(), "250px"));
		return columns;
	}

	@Override
	protected PropertiesPanel<Resource> buildPropertiesPanel(Resource resource) {
		return new ParameterResourceProperties(this, getResourceManager(), resource != null ? (Parameter) resource : null);
	}

}
