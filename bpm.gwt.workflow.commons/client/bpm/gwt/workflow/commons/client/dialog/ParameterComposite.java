package bpm.gwt.workflow.commons.client.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class ParameterComposite extends Composite {

	private static ParameterCompositeUiBinder uiBinder = GWT.create(ParameterCompositeUiBinder.class);

	interface ParameterCompositeUiBinder extends UiBinder<Widget, ParameterComposite> {
	}

	@UiField
	Label lblQuestion, lblRange;

	@UiField
	TextHolderBox txtValue;

	@UiField
	ListBox lstLovs, lstValue;

	@UiField
	RadioButton radioTxt, radioLst;

	private Parameter parameter;
	private List<ListOfValues> lovs;
	private ParametersPanel parentPanel;

	private boolean useLovs;
	private Timer t;

	public ParameterComposite(Parameter parameter, List<ListOfValues> lovs, ParametersPanel parentPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parameter = parameter;
		this.lovs = lovs;
		this.useLovs = parameter.useListOfValues();
		this.parentPanel = parentPanel;
		
		radioLst.setName("radioTextorList"+parameter.getName());
		radioTxt.setName("radioTextorList"+parameter.getName());

		lblQuestion.setText(parameter.getQuestion());
		txtValue.setText(parameter.getDefaultValue());
		lstValue.setVisible(false);
		lblRange.setVisible(false);

		if (lovs != null && !lovs.isEmpty() && useLovs) {

			lstLovs.clear();
			for (ListOfValues lov : lovs) {
				lstLovs.addItem(lov.getName(), String.valueOf(lov.getId()));
			}

			for(int i=0; i<lstLovs.getItemCount(); i++){
				if(lstLovs.getValue(i).equals(parameter.getDefaultValue())){
					lstLovs.setSelectedIndex(i);
					break;
				}
			}
			updateUi(useLovs);
			//hide textbox and choice for now
			radioTxt.setVisible(false);
			radioLst.setVisible(false);
			txtValue.setVisible(false);
		}
		else {
			radioTxt.setVisible(false);
			radioLst.setVisible(false);
			lstLovs.setVisible(false);
		}
		
		switch (parameter.getParameterType()) {
		case SIMPLE:
			txtValue.setVisible(true);
			lstValue.setVisible(false);
			lblRange.setVisible(false);
			break;
		case LOV:
			break;
		case RANGE:
			txtValue.setVisible(true);
			lstValue.setVisible(false);
			lblRange.setVisible(true);
			break;
		case SELECTION:
			if(parameter.getListOfValues() != null){
				for(String item : parameter.getListOfValues().getValues()){
					lstValue.addItem(item);
					if(item.equals(parameter.getDefaultValue())){
						lstValue.setSelectedIndex(lstValue.getItemCount()-1);
					}
				}
			} else { //from dataset
				t = new Timer() {
					
					@Override
					public void run() {
						if(ParameterComposite.this.parentPanel.isCanRunR()){
							ParameterComposite.this.parentPanel.setCanRunR(false);
							loadDatasetValues();
						}
						
					}
				};
				t.scheduleRepeating(500);
			}
			
			txtValue.setVisible(false);
			lstValue.setVisible(true);
			lblRange.setVisible(false);
			break;
		default:
			break;

		}
	}

	public Parameter getParameterWithValue() throws Exception {
		if (useLovs) {
			ListOfValues lov = getSelectedLov();
			if (lov == null) {
				throw new Exception(LabelsCommon.lblCnst.AllParameterNeedToBeSet());
			}
			parameter.setValueListOfValues(lov);
		}
		else {
			if(parameter.getParameterType().equals(TypeParameter.SIMPLE)){
				if (txtValue.getText() == null || txtValue.getText().isEmpty()) {
					throw new Exception(LabelsCommon.lblCnst.AllParameterNeedToBeSet());
				}
				parameter.setValueString(txtValue.getText());
			} else if(parameter.getParameterType().equals(TypeParameter.RANGE)){
				if (txtValue.getText() == null || txtValue.getText().isEmpty()) {
					throw new Exception(LabelsCommon.lblCnst.AllParameterNeedToBeSet());
				}
				parameter.setValueList(Arrays.asList(txtValue.getText().split(";")));
			} else if(parameter.getParameterType().equals(TypeParameter.SELECTION)){
				parameter.setSelectionValue(lstValue.getValue(lstValue.getSelectedIndex()));
			}
			
		}
		return parameter;
	}

	private ListOfValues getSelectedLov() {
		int id = Integer.parseInt(lstLovs.getValue(lstLovs.getSelectedIndex()));
		for (ListOfValues lov : lovs) {
			if (lov.getId() == id) {
				return lov;
			}
		}
		return null;
	}

	private void updateUi(boolean useLovs) {
		this.useLovs = useLovs;
//		txtValue.setEnabled(!useLovs);
//		lstLovs.setEnabled(useLovs);
//		radioLst.setValue(useLovs);
//		radioTxt.setValue(!useLovs);
	}

	@UiHandler("radioTxt")
	public void onTextClick(ClickEvent event) {
		updateUi(false);
	}

	@UiHandler("radioLst")
	public void onListClick(ClickEvent event) {
		updateUi(true);
	}
	
	private void loadDatasetValues() {
		lstValue.clear();
		String script = "";
		if(parameter.getIdParentParam() == 0){
			script = "manual_result<-" + parameter.getDataset() + "$" + parameter.getColumn();
		} else {
			script = "library(dplyr)\n";
			script += "manual_result <- filter(" + parameter.getDataset() + ", " + parameter.getRequest() + ")"+ "$" + parameter.getColumn();
		}
		

		RScriptModel model = new RScriptModel();
		model.setScript(script);
		model.setOutputs(new String[] { "manual_result" });
		parentPanel.showWaitPart(true);
		WorkflowsService.Connect.getInstance().executeScript(model, new AsyncCallback<RScriptModel>() {
			@Override
			public void onSuccess(RScriptModel result) {
				parentPanel.showWaitPart(false);
				parentPanel.setCanRunR(true);
				t.cancel();
				String values = result.getOutputVarstoString().get(0);
				List<String> distincts = Arrays.asList(values.trim().split("\\t"));

				HashSet<String> temp = new HashSet<String>(distincts.subList(0, distincts.size()-1));
				distincts = new ArrayList<String>(temp);
				Collections.sort(distincts);
				for(String item : distincts){
					lstValue.addItem(item);
					if(item.equals(parameter.getDefaultValue())){
						lstValue.setSelectedIndex(lstValue.getItemCount()-1);
					}
				}
			}
			@Override
			public void onFailure(Throwable caught) {
				parentPanel.showWaitPart(false);
				parentPanel.setCanRunR(true);
				t.cancel();
			}
		});

	}
	
	public Parameter getParameter() {
		return parameter;
	}

	@UiHandler("lstValue")
	public void onChangeValue(ChangeEvent event) {
		
		try {
			for(ParameterComposite p : parentPanel.paramHasChild(parameter)){
				p.getParameter().setParentParam(getParameterWithValue());
				p.loadDatasetValues();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageHelper.openMessageError(LabelsCommon.lblCnst.Error(), e);
		}
	}
}
