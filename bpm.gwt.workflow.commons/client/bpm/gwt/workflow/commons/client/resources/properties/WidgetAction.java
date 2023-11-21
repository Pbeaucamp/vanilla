package bpm.gwt.workflow.commons.client.resources.properties;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.utils.VariableTextHolderBox;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.resources.attributes.FTPAction;
import bpm.workflow.commons.resources.attributes.FTPAction.TypeAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetAction extends Composite {

	private static WidgetActionUiBinder uiBinder = GWT.create(WidgetActionUiBinder.class);

	interface WidgetActionUiBinder extends UiBinder<Widget, WidgetAction> {
	}

	interface MyStyle extends CssResource {
		String txt();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelValue;

	@UiField
	ListBox lstActions;
	
	private VariableTextHolderBox txtValue;
	
	private FTPActionsPanel panelParent;
	
	public WidgetAction(FTPActionsPanel panelParent, IResourceManager resourceManager, FTPAction action) {
		initWidget(uiBinder.createAndBindUi(this));
		this.panelParent = panelParent;

		int i = 0;
		int selectedIndex = -1;
		for (TypeAction type : TypeAction.values()) {
			lstActions.addItem(getTypeLabel(type), String.valueOf(type.getType()));

			if (action != null && action.getType() == type) {
				selectedIndex = i;
			}

			i++;
		}

		if (selectedIndex != -1) {
			lstActions.setSelectedIndex(selectedIndex);
		}

		txtValue = new VariableTextHolderBox(action != null ? action.getValueVS() : null, LabelsCommon.lblCnst.DestinationPath(), style.txt(), resourceManager.getVariables(), resourceManager.getParameters());
		panelValue.setWidget(txtValue);
	}

	private String getTypeLabel(TypeAction type) {
		switch (type) {
		case DELETE:
			return LabelsCommon.lblCnst.Delete();
		case MOVE:
			return LabelsCommon.lblCnst.Move();

		default:
			break;
		}
		return LabelsCommon.lblCnst.Unknown();
	}

	public FTPAction getAction() {
		int typeValue = Integer.parseInt(lstActions.getValue(lstActions.getSelectedIndex()));
		TypeAction selectedType = TypeAction.valueOf(typeValue);
		VariableString value = txtValue.getVariableString();
		return new FTPAction(selectedType, value);
	}
	
	@UiHandler("lstActions")
	public void onActionChange(ChangeEvent event) {
		int typeValue = Integer.parseInt(lstActions.getValue(lstActions.getSelectedIndex()));
		TypeAction selectedType = TypeAction.valueOf(typeValue);
		
		updateUi(selectedType);
	}
	
	@UiHandler("btnDelete")
	public void onDeleteEvent(ClickEvent event) {
		panelParent.removeAction(this);
	}

	private void updateUi(TypeAction type) {
		switch (type) {
		case DELETE:
			txtValue.setEnabled(false);
			break;
		case MOVE:
			txtValue.setEnabled(true);
			break;

		default:
			break;
		}
	}
}
