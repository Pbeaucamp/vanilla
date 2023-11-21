package bpm.gwt.workflow.commons.client.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ParametersPanel extends CompositeWaitPanel {

	private static ParametersPanelUiBinder uiBinder = GWT.create(ParametersPanelUiBinder.class);

	interface ParametersPanelUiBinder extends UiBinder<Widget, ParametersPanel> {
	}
	
	@UiField
	HTMLPanel contentPanel;
	
	private List<ParameterComposite> params = new ArrayList<ParameterComposite>();
	private boolean canRunR;
	//private List<Parameter> parameters;
	
	public ParametersPanel(List<Parameter> parameters, List<ListOfValues> lovs) {
		initWidget(uiBinder.createAndBindUi(this));
		//this.parameters = parameters;

		Collections.sort(parameters, new Comparator<Parameter>() {
			@Override
			public int compare(Parameter arg0, Parameter arg1) {
				if(arg0.getIdParentParam() !=0 && arg1.getIdParentParam() !=0){
					return 0;
				} else if(arg0.getIdParentParam() !=0) {
					return 1;
				} else if(arg1.getIdParentParam() !=0) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		canRunR = true;
		for(Parameter param : parameters) {
			if (param.getParameterType() != TypeParameter.DB) {
				ParameterComposite paramComp = new ParameterComposite(param, lovs, this);
				contentPanel.add(paramComp);
				params.add(paramComp);
			}
			else {
				ParameterComposite paramComp = new ParameterComposite(param, lovs, this);
				params.add(paramComp);
			}
		}
	}
	
	public List<Parameter> getParameters() throws Exception {
		List<Parameter> parameters = new ArrayList<Parameter>();
		for(ParameterComposite param : params) {
			parameters.add(param.getParameterWithValue());
		}
		return parameters;
	}

	public List<Parameter> getLOVParameters() throws Exception {
		List<Parameter> parameters = new ArrayList<Parameter>();
		for(ParameterComposite paramComp : params) {
			Parameter param = paramComp.getParameterWithValue();
			if(param.useListOfValues()){
				parameters.add(param);
			}
		}
		return parameters;
	}

	public List<ParameterComposite> paramHasChild(Parameter param) {
		List<ParameterComposite> result = new ArrayList<>();
		for(ParameterComposite p : params){
			if(p.getParameter().getParentParam() != null && p.getParameter().getParentParam().getId() == param.getId()){
				result.add(p);
			}
		}
		return result;
	}

	public boolean isCanRunR() {
		return canRunR;
	}

	public void setCanRunR(boolean canRunR) {
		this.canRunR = canRunR;
	}
	
	
}
