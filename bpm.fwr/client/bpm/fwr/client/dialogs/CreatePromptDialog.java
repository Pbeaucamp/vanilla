package bpm.fwr.client.dialogs;

import bpm.fwr.api.beans.dataset.DynamicPrompt;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.action.ActionType;
import bpm.fwr.client.panels.ReportPanel;
import bpm.fwr.client.panels.ReportPanel.DropTargetType;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CreatePromptDialog extends AbstractDialogBox {
	private static final int LIST_BOX = 0;
	private static final int LIST_BOX_MULTIPLE = 1;
	private static final int TEXT_BOX = 2;

	private static final String EQUAL = "=";
	private static final String INFERIOR = "<";
	private static final String INFERIOR_OR_EQUAL = "<=";
	private static final String SUPERIOR = ">";
	private static final String SUPERIOR_OR_EQUAL = ">=";
	private static final String NOT_EQUAL = "!=";
	private static final String DIFFERENT = "<>";
	private static final String IN = "IN";
	private static final String LIKE = "LIKE";

	private static final String[] OPERATORS = { EQUAL, INFERIOR, INFERIOR_OR_EQUAL, SUPERIOR, SUPERIOR_OR_EQUAL, NOT_EQUAL, DIFFERENT, IN, LIKE };

	private static CreatePromptDialogBoxUiBinder uiBinder = GWT.create(CreatePromptDialogBoxUiBinder.class);

	interface CreatePromptDialogBoxUiBinder extends UiBinder<Widget, CreatePromptDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	HTMLPanel panelName, panelOperator;

	@UiField
	TextBox txtName;

	@UiField
	ListBox lstOperator, lstType;

	private ReportPanel reportPanel;
	private IResource resource;
	private DropTargetType dropTargetType;
	private ActionType actionType;

	private boolean isDynamic;

	public CreatePromptDialog(ReportPanel reportPanel, IResource resource, DropTargetType dropTargetType, ActionType actionType, boolean isDynamic) {
		super(Bpm_fwr.LBLW.DefinePrompt(), false, true);
		this.reportPanel = reportPanel;
		this.resource = resource;
		this.dropTargetType = dropTargetType;
		this.actionType = actionType;
		this.isDynamic = isDynamic;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		int index = 0;
		int selectedIndex = -1;
		for (String operator : OPERATORS) {
			lstOperator.addItem(operator);

			if (resource != null && resource instanceof FwrPrompt && ((FwrPrompt) resource).getOperator() != null && ((FwrPrompt) resource).getOperator().equals(operator)) {
				selectedIndex = index;
			}
			if (resource != null && resource instanceof DynamicPrompt && ((DynamicPrompt) resource).getOperator() != null && ((DynamicPrompt) resource).getOperator().equals(operator)) {
				selectedIndex = index;
			}
			index++;
		}

		if (selectedIndex != -1) {
			lstOperator.setSelectedIndex(selectedIndex);
		}

		if (!isDynamic) {
			panelName.setVisible(false);
			lstOperator.setEnabled(false);
		}
		txtName.setText(resource.getName()!=null ? resource.getName():  "");
		updateLstType();
	}

	@UiHandler("lstOperator")
	public void onOperatorChange(ChangeEvent event) {
		updateLstType();
	}

	private void updateLstType() {
		String operator = lstOperator.getValue(lstOperator.getSelectedIndex());
		boolean multiple = operator.equals(IN);

		lstType.clear();
		lstType.addItem(Bpm_fwr.LBLW.ListBox(), String.valueOf(LIST_BOX));
		if (multiple) {
			lstType.addItem(Bpm_fwr.LBLW.ListBoxMultiple(), String.valueOf(LIST_BOX_MULTIPLE));
		}
		lstType.addItem(Bpm_fwr.LBLW.TextBox(), String.valueOf(TEXT_BOX));
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			String name = resource.getName()!=null ? resource.getName():  "";
			String operator = lstOperator.getValue(lstOperator.getSelectedIndex());
			if (isDynamic) {
				name = txtName.getText() + "_" + new Object().hashCode();
				operator = lstOperator.getValue(lstOperator.getSelectedIndex());
			}

			int type = Integer.parseInt(lstType.getValue(lstType.getSelectedIndex()));

			if (resource instanceof DynamicPrompt) {
				DynamicPrompt dyn = (DynamicPrompt) resource;

				dyn.setName(name);
				dyn.setOperator(operator);

				if (type == LIST_BOX) {
					dyn.setType(VanillaParameter.LIST_BOX);
					dyn.setParamType(VanillaParameter.PARAM_TYPE_SIMPLE);
				}
				else if (type == LIST_BOX_MULTIPLE) {
					dyn.setType(VanillaParameter.LIST_BOX);
					dyn.setParamType(VanillaParameter.PARAM_TYPE_MULTI);
				}
				else if (type == TEXT_BOX) {
					dyn.setType(VanillaParameter.TEXT_BOX);
					dyn.setParamType(VanillaParameter.PARAM_TYPE_SIMPLE);
				}
			}
			else if (resource instanceof FwrPrompt) {
				FwrPrompt prt = (FwrPrompt) resource;

				if (type == LIST_BOX) {
					prt.setType(VanillaParameter.LIST_BOX);
					prt.setParamType(VanillaParameter.PARAM_TYPE_SIMPLE);
				}
				else if (type == LIST_BOX_MULTIPLE) {
					prt.setType(VanillaParameter.LIST_BOX);
					prt.setParamType(VanillaParameter.PARAM_TYPE_MULTI);
				}
				else if (type == TEXT_BOX) {
					prt.setType(VanillaParameter.TEXT_BOX);
					prt.setParamType(VanillaParameter.PARAM_TYPE_SIMPLE);
				}
			}

			reportPanel.manageWidget(resource, dropTargetType, actionType, false);

			CreatePromptDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			CreatePromptDialog.this.hide();
		}
	};
}
