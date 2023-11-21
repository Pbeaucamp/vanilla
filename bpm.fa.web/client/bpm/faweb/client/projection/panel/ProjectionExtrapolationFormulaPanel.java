package bpm.faweb.client.projection.panel;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.ProjectionMeasure;
import bpm.faweb.client.projection.ProjectionMeasureCondition;
import bpm.faweb.client.projection.dialog.DialogExtrapolationStartEndMembers;
import bpm.faweb.shared.infoscube.ItemMes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProjectionExtrapolationFormulaPanel extends Composite {

	private static ProjectionExtrapolationFormulaPanelUiBinder uiBinder = GWT.create(ProjectionExtrapolationFormulaPanelUiBinder.class);

	interface ProjectionExtrapolationFormulaPanelUiBinder extends UiBinder<Widget, ProjectionExtrapolationFormulaPanel> {
	}

	private ItemMes measure;
	
	
//	@UiField
//	Label lblMeasure;
	
//	@UiField
//	ListBox txtExtrapolationType;
	
	@UiField
	Button btnMemberStartEnd;
	
	@UiField
	RadioButton rbNone, rbLinear, rbLagrange;
	
	@UiField
	Label lblStart, lblEnd;
	
	@UiField
	TextBox txtStart, txtEnd;
	
	private ProjectionMeasure projMes;
	
	public ProjectionExtrapolationFormulaPanel(final MainPanel mainPanel, ItemMes measure) {
		initWidget(uiBinder.createAndBindUi(this));
		this.measure = measure;
		
//		lblMeasure.setText(measure.getName() + " : ");
		btnMemberStartEnd.setText(FreeAnalysisWeb.LBL.AddDateFilters());
		lblStart.setText(FreeAnalysisWeb.LBL.ProjectionFilterStart());
		lblEnd.setText(FreeAnalysisWeb.LBL.ProjectionFilterEnd());
		
		txtStart.setEnabled(false);
		txtEnd.setEnabled(false);
		
		rbNone.setText(Projection.EXTRAPOLATION_TYPES_LBL[Projection.EXTRAPOLATION_NONE_INDEX]);
		rbNone.setFormValue(Projection.EXTRAPOLATION_TYPES[Projection.EXTRAPOLATION_NONE_INDEX]);
		
		rbLinear.setText(Projection.EXTRAPOLATION_TYPES_LBL[Projection.EXTRAPOLATION_LINEAR_INDEX]);
		rbLinear.setFormValue(Projection.EXTRAPOLATION_TYPES[Projection.EXTRAPOLATION_LINEAR_INDEX]);
		
		rbLagrange.setText(Projection.EXTRAPOLATION_TYPES_LBL[Projection.EXTRAPOLATION_LAGRANGE_INDEX]);
		rbLagrange.setFormValue(Projection.EXTRAPOLATION_TYPES[Projection.EXTRAPOLATION_LAGRANGE_INDEX]);
		
		btnMemberStartEnd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DialogExtrapolationStartEndMembers dial = new DialogExtrapolationStartEndMembers(mainPanel, getProjectionMeasure());
				dial.center();
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						txtEnd.setText("");
						txtStart.setText("");
						if(projMes.getConditions() != null && projMes.getConditions().size() > 0) {
							for(ProjectionMeasureCondition cond : projMes.getConditions()) {
								String uname = cond.getMemberUnames().get(0);
								
								int start = mainPanel.getTimeHiera().getUname().length() + 1;
								
								uname = uname.substring(start, uname.length());
								
								if(cond.getFormula().equals(Projection.EXTRAPOLATION_END_MEMBER)) {
									txtEnd.setText(uname);
								}
								else {
									txtStart.setText(uname);
								}
							}
						}
						
					}
				});
			}
		});

	}

	
	public String getType() {
		if(rbNone.getValue()) {
			return Projection.EXTRAPOLATION_NONE;
		}
		else if(rbLagrange.getValue()) {
			return Projection.EXTRAPOLATION_LAGRANGE;
		}
		else if(rbLinear.getValue()) {
			return Projection.EXTRAPOLATION_LINEAR;
		}
		return Projection.EXTRAPOLATION_NONE;
	}

	public ProjectionMeasure getProjectionMeasure() {
		if(projMes == null) {
			projMes = new ProjectionMeasure();
		}
		
		projMes.setUname(measure.getUname());
		projMes.setFormula(getType());
		
		return projMes;
	}

	public void initProjectionMeasureInformations(Projection actualProjection) {
		for(ProjectionMeasure m : actualProjection.getMeasureFormulas()) {
			if(m.getUname().equals(measure.getUname())) {
//				for(int i = 0 ; i < txtExtrapolationType.getItemCount() ; i++) {
//					String type = txtExtrapolationType.getValue(i);
//					if(type.equals(m.getFormula())) {
//						txtExtrapolationType.setSelectedIndex(i);
//						break;
//					}
//				}
			}
		}
		
	}

	
	
}
