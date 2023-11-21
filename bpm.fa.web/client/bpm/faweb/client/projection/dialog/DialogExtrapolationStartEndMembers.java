package bpm.faweb.client.projection.dialog;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.ProjectionMeasure;
import bpm.faweb.client.projection.ProjectionMeasureCondition;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.faweb.shared.infoscube.ItemHier;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class DialogExtrapolationStartEndMembers extends AbstractDialogBox {

	private static DialogExtrapolationStartEndMembersUiBinder uiBinder = GWT.create(DialogExtrapolationStartEndMembersUiBinder.class);

	interface DialogExtrapolationStartEndMembersUiBinder extends UiBinder<Widget, DialogExtrapolationStartEndMembers> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	RadioButton rbNone, rbFilter;

	@UiField
	CheckBox cbStart, cbEnd;

	@UiField
	ListBox lstStart, lstEnd;

	@UiField
	HTMLPanel filterPanel;
	
	private MainPanel mainPanel;

	private ProjectionMeasure measure;

	public DialogExtrapolationStartEndMembers(MainPanel mainPanel, ProjectionMeasure measure) {
		super(FreeAnalysisWeb.LBL.FilterTimeMembers(), true, true);
		this.mainPanel = mainPanel;
		
		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(FreeAnalysisWeb.LBL.Apply(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		this.measure = measure;

		rbNone.setText(FreeAnalysisWeb.LBL.ProjectionFilterNone());
		rbNone.addClickHandler(handler);
		rbFilter.setText(FreeAnalysisWeb.LBL.ProjectionFilter());
		rbFilter.addClickHandler(handler);

		cbStart.setText(FreeAnalysisWeb.LBL.ProjectionFilterStart());
		cbStart.addClickHandler(handler);
		cbEnd.setText(FreeAnalysisWeb.LBL.ProjectionFilterEnd());
		cbEnd.addClickHandler(handler);

		fillMemberLists();

	}

	private void fillMemberLists() {

		ItemHier timeHiera = null;
		for (ItemDim dim : mainPanel.getInfosReport().getDims()) {
			if (dim.isDate()) {
				timeHiera = dim.getChilds().get(0);
			}
		}

		mainPanel.setTimeHiera(timeHiera);

		ItemHier level = timeHiera.getChilds().get(timeHiera.getChilds().size() - 1);

		FaWebService.Connect.getInstance().searchDimensions(mainPanel.getKeySession(), timeHiera.getUname(), level.getUname(), new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {

				for (String r : result) {

					int start = mainPanel.getTimeHiera().getUname().length() + 1;

					String rr = r.substring(start, r.length());

					lstStart.addItem(rr, r);
					lstEnd.addItem(rr, r);
				}

				initConditions();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Problem while retriving date members");
			}
		});
	}

	private void initConditions() {
		if (measure.getConditions() == null || measure.getConditions().size() == 0) {
			rbNone.setValue(true);
			DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), rbNone);
		}
		else {
			for (ProjectionMeasureCondition cond : measure.getConditions()) {
				if (cond.getFormula().equals(Projection.EXTRAPOLATION_END_MEMBER)) {
					cbEnd.setValue(true);
					String mbUname = cond.getMemberUnames().get(0);
					for (int i = 0; i < lstEnd.getItemCount(); i++) {
						String uname = lstEnd.getValue(i);
						if (mbUname.equals(uname)) {
							lstEnd.setSelectedIndex(i);
							break;
						}
					}
				}
				else {
					cbStart.setValue(true);
					String mbUname = cond.getMemberUnames().get(0);
					for (int i = 0; i < lstStart.getItemCount(); i++) {
						String uname = lstStart.getValue(i);
						if (mbUname.equals(uname)) {
							lstStart.setSelectedIndex(i);
							break;
						}
					}
				}
			}
			rbFilter.setValue(true);
			DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), rbFilter);
		}
	}

	private ClickHandler handler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource().equals(rbNone)) {
				cbStart.setEnabled(false);
				cbEnd.setEnabled(false);
				lstStart.setEnabled(false);
				lstEnd.setEnabled(false);
			}
			else if (event.getSource().equals(rbFilter)) {
				cbStart.setEnabled(true);
				cbEnd.setEnabled(true);
				enableLstBox(cbStart.getValue(), cbStart.getValue());
			}
			else if (event.getSource().equals(cbStart)) {
				enableLstBox(cbStart.getValue(), cbStart.getValue());
			}
			else if (event.getSource().equals(cbEnd)) {
				enableLstBox(cbStart.getValue(), cbStart.getValue());
			}
		}
	};
	
	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			// FIXME : create the conditions

			if (measure.getConditions() != null) {
				measure.getConditions().clear();
			}

			if (rbFilter.getValue()) {
				if (cbStart.getValue()) {
					String uname = lstStart.getValue(lstStart.getSelectedIndex());
					ProjectionMeasureCondition cond = new ProjectionMeasureCondition();
					cond.setFormula(Projection.EXTRAPOLATION_START_MEMBER);
					cond.addMemberUname(uname);
					measure.addCondition(cond);
				}
				if (cbEnd.getValue()) {
					String uname = lstEnd.getValue(lstEnd.getSelectedIndex());
					ProjectionMeasureCondition cond = new ProjectionMeasureCondition();
					cond.setFormula(Projection.EXTRAPOLATION_END_MEMBER);
					cond.addMemberUname(uname);
					measure.addCondition(cond);
				}
			}

			DialogExtrapolationStartEndMembers.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			DialogExtrapolationStartEndMembers.this.hide();
		}
	};

	private void enableLstBox(boolean start, boolean end) {
		lstStart.setEnabled(start);
		lstEnd.setEnabled(end);
	}
}
