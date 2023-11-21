package bpm.gwt.workflow.commons.client.workflow.properties;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class VariableItem extends Composite {

	public static final String VARIABLE_ID = "variableId";
	public static final String PARAMETER_ID = "parameterId";

	private static VariableItemUiBinder uiBinder = GWT.create(VariableItemUiBinder.class);

	interface VariableItemUiBinder extends UiBinder<Widget, VariableItem> {
	}

	@UiField
	FocusPanel panelVariable;

	@UiField
	Label lblVariable;

	private Variable variable;
	private Parameter parameter;

	public VariableItem(Variable variable) {
		initWidget(uiBinder.createAndBindUi(this));
		this.variable = variable;

		lblVariable.setText(variable.getName());
		lblVariable.setTitle(variable.getName());

		buildContent();
	}

	public VariableItem(Parameter parameter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parameter = parameter;

		lblVariable.setText(parameter.getName());
		lblVariable.setTitle(parameter.getName());

		buildContent();
	}

	private void buildContent() {
		panelVariable.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		panelVariable.addDragStartHandler(dragStartHandler);
	}

	private DragStartHandler dragStartHandler = new DragStartHandler() {

		@Override
		public void onDragStart(DragStartEvent event) {
			if (variable != null) {
				event.setData(VARIABLE_ID, String.valueOf(variable.getId()));
				event.getDataTransfer().setDragImage(getElement(), 10, 10);
			}
			else if (parameter != null) {
				event.setData(PARAMETER_ID, String.valueOf(parameter.getId()));
				event.getDataTransfer().setDragImage(getElement(), 10, 10);
			}
		}
	};

}
