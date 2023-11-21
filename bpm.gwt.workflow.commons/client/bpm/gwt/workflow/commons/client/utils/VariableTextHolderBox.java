package bpm.gwt.workflow.commons.client.utils;

import java.util.List;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.workflow.commons.client.workflow.properties.VariableItem;
import bpm.gwt.workflow.commons.client.workflow.properties.parameters.HasParameterWidget;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class VariableTextHolderBox extends TextHolderBox implements HasParameterWidget {

	private VariableString variableString;

	private List<Variable> variables;
	private List<Parameter> parameters;

	public VariableTextHolderBox(VariableString variableString, String placeHolder, String style, List<Variable> variables, List<Parameter> parameters) {
		this.variableString = variableString != null ? variableString : new VariableString();
		this.variables = variables;
		this.parameters = parameters;

		if (style != null) {
			addStyleName(style);
		}
		setPlaceHolder(placeHolder);
		setTitle(placeHolder);

		setText(variableString != null ? variableString.getStringForTextbox() : "");

		addDragOverHandler(dragOverHandler);
		addDragLeaveHandler(dragLeaveHandler);
		addDropHandler(dropHandler);

		addValueChangeHandler(txtChangeHandler);
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	public VariableString getVariableString() {
		return variableString;
	}

	private Variable findVariable(int variableId) {
		for (Variable var : variables) {
			if (var.getId() == variableId) {
				return var;
			}
		}
		return null;
	}

	private Parameter findParameter(int parameterId) {
		for (Parameter param : parameters) {
			if (param.getId() == parameterId) {
				return param;
			}
		}
		return null;
	}

	private ValueChangeHandler<String> txtChangeHandler = new ValueChangeHandler<String>() {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			VariableHelper.setVariableStringValue(variableString, event.getValue(), parameters, variables);
		}
	};

	private DragOverHandler dragOverHandler = new DragOverHandler() {

		@Override
		public void onDragOver(DragOverEvent event) {
		}
	};

	private DragLeaveHandler dragLeaveHandler = new DragLeaveHandler() {

		@Override
		public void onDragLeave(DragLeaveEvent event) {
		}
	};

	private DropHandler dropHandler = new DropHandler() {

		@Override
		public void onDrop(DropEvent event) {
			String variableData = event.getDataTransfer().getData(VariableItem.VARIABLE_ID);
			if (!variableData.isEmpty()) {
				Variable variable = findVariable(Integer.parseInt(variableData));
				if (variable != null) {
					String value = getText();

					variableString.addVariable(value, variable);
					setText(variableString.getStringForTextbox());
				}
			}

			String parameterData = event.getDataTransfer().getData(VariableItem.PARAMETER_ID);
			if (!parameterData.isEmpty()) {
				Parameter parameter = findParameter(Integer.parseInt(parameterData));
				if (parameter != null) {
					String value = getText();

					variableString.addParameter(value, parameter);
					setText(variableString.getStringForTextbox());
				}
			}
		}
	};

	@Override
	public Object getParameterValue() {
		return getVariableString();
	}
}
