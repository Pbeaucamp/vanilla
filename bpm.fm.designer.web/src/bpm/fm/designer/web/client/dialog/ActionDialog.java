package bpm.fm.designer.web.client.dialog;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricAction;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class ActionDialog extends AbstractDialogBox {

	private static ActionDialogUiBinder uiBinder = GWT.create(ActionDialogUiBinder.class);
	
	interface MyStyle extends CssResource {
		String marginTop();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel, formulaPanel;
	
	@UiField
	TextBox txtName;
	
	@UiField
	TextArea txtDesc, txtFormula;
	
	@UiField
	DateBox txtDateStart, txtDateEnd;

	private Metric metric;
	private MetricAction action;

	interface ActionDialogUiBinder extends UiBinder<Widget, ActionDialog> {
	}

	public ActionDialog(Metric metric) {
		super(Messages.lbl.addMetricAction(), false, true);
		
		this.metric = metric;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		oracle.addAll(MetricAction.formulaElements);
		SuggestBox box = new SuggestBox(oracle, txtFormula);
		box.addStyleName(style.marginTop());
		formulaPanel.add(box);
		
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
	}

	public ActionDialog(Metric metric, MetricAction action) {
		this(metric);
		
		this.action = action;
		txtName.setText(action.getName());
		txtFormula.setText(action.getFormula());
		txtDesc.setText(action.getDescription());
		
		txtDateStart.setValue(action.getStartDate());
		txtDateEnd.setValue(action.getEndDate());
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(action == null) {
				action = new MetricAction();
			}
			action.setDescription(txtDesc.getText());
			action.setName(txtName.getText());
			action.setFormula(txtFormula.getText());
			action.setEndDate(txtDateEnd.getValue());
			action.setStartDate(txtDateStart.getValue());
			action.setMetricId(metric.getId());
			
			MetricService.Connection.getInstance().addMetricAction(action, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					ActionDialog.this.hide();
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ActionDialog.this.hide();
		}
	};
}
