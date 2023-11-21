package bpm.faweb.client.projection.panel;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.ProjectionMeasure;
import bpm.faweb.client.projection.dialog.ProjectionMeasureFormulaDialog;
import bpm.faweb.shared.infoscube.ItemMes;
import bpm.gwt.commons.client.images.CommonImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProjectionMeasureFormulaPanel extends Composite {

	private static ProjectionMeasureFormulaPanelUiBinder uiBinder = GWT.create(ProjectionMeasureFormulaPanelUiBinder.class);

	interface ProjectionMeasureFormulaPanelUiBinder extends UiBinder<Widget, ProjectionMeasureFormulaPanel> {
	}

//	@UiField
//	Label lblMeasure;
	
	@UiField
	TextBox txtNewType;
	
	@UiField
	Image imgEditFormula;
	
//	@UiField
//	ListBox lstType;
	
	@UiField
	HorizontalPanel mainPanel;
	
	private MainPanel mainCompPanel;
	
	private ItemMes measure;
	private ProjectionMeasure projectionMeasure;
	
	public ProjectionMeasureFormulaPanel(MainPanel mainCompPanel, ItemMes mes) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainCompPanel = mainCompPanel;
		this.measure = mes;
			
		imgEditFormula.setResource(CommonImages.INSTANCE.edit_24());
		imgEditFormula.addClickHandler(clickHandler);
		txtNewType.setVisible(true);
		imgEditFormula.setVisible(true);
	}

	public String getMeasureUName() {
		return measure.getUname();
	}
	
	
	public void setProjectionMeasure(ProjectionMeasure projectionMeasure) {
		this.projectionMeasure = projectionMeasure;
		txtNewType.setText((projectionMeasure.getConditions().size()) + " conditions");
	}

	public ProjectionMeasure getProjectionMeasure() {
		return projectionMeasure;
	}


	private ClickHandler clickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(imgEditFormula)) {
				ProjectionMeasureFormulaDialog dial = new ProjectionMeasureFormulaDialog(mainCompPanel, measure, ProjectionMeasureFormulaPanel.this);
				dial.center();
			}
		}
	};

	public void initProjectionMeasureInformations(Projection actualProjection) {
		for(ProjectionMeasure me : actualProjection.getMeasureFormulas()) {
			if(me.getUname().equals(measure.getUname())) {
				this.projectionMeasure = me;
				txtNewType.setText((projectionMeasure.getConditions().size()) + " conditions");
			}
		}
	}
	
	
}
