package bpm.faweb.client.projection.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.DraggableGridItem;
import bpm.faweb.client.panels.center.ICubeViewer;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.client.panels.center.grid.FaWebLabelValue;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.ItemCube;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class DialogCompareProjection extends AbstractDialogBox {

	private static DialogCompareProjectionUiBinder uiBinder = GWT.create(DialogCompareProjectionUiBinder.class);

	interface DialogCompareProjectionUiBinder extends UiBinder<Widget, DialogCompareProjection> {
	}
	
	@UiField
	HTMLPanel contentPanel;

	@UiField
	Label lblOrder, lblCalcul, lblRappel;
	
	@UiField
	Button btnLoadData;
	
	@UiField
	HTMLPanel gridPanel;
	
	@UiField
	CaptionPanel panelFormula;
	
	@UiField
	RadioButton rbCalculMinus, rbCalculPercent, rbProjOrig, rbOrigProj;
	
	private MainPanel mainPanel;
	private ICubeViewer cubeViewer;
	private CubeView projection, original;
	
	public DialogCompareProjection(MainPanel mainPanel, ICubeViewer cubeViewer, CubeView projection, CubeView original) {
		super(FreeAnalysisWeb.LBL.ProjectionDifferences(), false, true);
		this.mainPanel = mainPanel;
		this.cubeViewer = cubeViewer;
		this.projection = projection;
		this.original = original;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		createButton(LabelsConstants.lblCnst.Close(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DialogCompareProjection.this.hide();
			}
		});
		
		panelFormula.setCaptionText(FreeAnalysisWeb.LBL.ProjectionFormulaTitle());
		lblCalcul.setText(FreeAnalysisWeb.LBL.ProjectionFormulaCalculType() + "  : ");
		lblOrder.setText(FreeAnalysisWeb.LBL.ProjectionFormulaOrder() + " : ");
		
		rbCalculMinus.setText(FreeAnalysisWeb.LBL.ProjectionFormulaMinus());
		rbCalculPercent.setText(FreeAnalysisWeb.LBL.ProjectionFormulaPercent());
		rbProjOrig.setText("Projection -> Original");
		rbOrigProj.setText("Original -> Projection");
		
		rbCalculMinus.setValue(true);
		rbProjOrig.setValue(true);
		
		lblRappel.setText(FreeAnalysisWeb.LBL.ProjectionRappelFormula() + " : " + "Projection - Original");
		
		rbCalculMinus.addClickHandler(handler);
		rbCalculPercent.addClickHandler(handler);
		rbProjOrig.addClickHandler(handler);
		rbOrigProj.addClickHandler(handler);
		
		btnLoadData.setText(FreeAnalysisWeb.LBL.ProjectionDiffLoad());
		
		btnLoadData.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				gridPanel.clear();
				gridPanel.add(createCompareGrid());
			}
		});
	}
	
	private ClickHandler handler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			
			String rappel = FreeAnalysisWeb.LBL.ProjectionRappelFormula() + " : ";
			
			if(event.getSource().equals(rbCalculMinus)) {
				if(rbOrigProj.getValue()) {
					rappel += "Original - Projection";
				}
				else {
					rappel += "Projection - Original";
				}
			}
			else if(event.getSource().equals(rbCalculPercent)) {
				if(rbOrigProj.getValue()) {
					rappel += "Original / Projection * 100";
				}
				else {
					rappel += "Projection / Original * 100";
				}
			}
			else if(event.getSource().equals(rbOrigProj)) {
				if(rbCalculMinus.getValue()) {
					rappel += "Original - Projection";
				}
				else {
					rappel += "Original / Projection * 100";
				}
			}	
			else if(event.getSource().equals(rbProjOrig)) {
				if(rbCalculMinus.getValue()) {
					rappel += "Projection - Original";
				}
				else {
					rappel += "Projection / Original * 100";
				}
			}
			
			lblRappel.setText(rappel);
		}
	};

	private Widget createCompareGrid() {
		
		CubeView left = null;
		CubeView right = null;
		
		//FIXME : need a test to know the order
		if(rbProjOrig.getValue()) {
			left = projection;
			right = original;
		}
		else {
			right = projection;
			left = original;
		}
		
		GridCube cube = new GridCube();
		
		List<ArrayList<ItemCube>> res = new ArrayList<ArrayList<ItemCube>>();
		
		for(int i = 0 ; i < left.getRowCount() ; i++) {
			ArrayList<ItemCube> line = new ArrayList<ItemCube>();
			for(int j = 0 ; j < left.getCellCount(i); j++) {
				
				ItemCube it = new ItemCube();
				
				Widget lw = left.getWidget(i, j);
				if(lw instanceof DraggableGridItem) {
					DraggableGridItem dg = (DraggableGridItem) lw;
					it.setType("ItemElement");
					it.setLabel(dg.getName());
					it.setUname(dg.getUname());
					
				}
				else if(lw instanceof FaWebLabelValue) {
					FaWebLabelValue vla = (FaWebLabelValue) lw;
					it.setType("ItemValue");
					
					float d = vla.getVal();
					float dd = ((FaWebLabelValue)right.getWidget(i, j)).getVal();
					
					float r =  0;
					if(rbCalculMinus.getValue()) {
						r = d-dd;
						it.setLabel(NumberFormat.getDecimalFormat().format(r));
					}
					else {
						r = d / dd * 100;
						it.setLabel(NumberFormat.getDecimalFormat().format(r) + " %");
					}
					
					
				}
				else {
//					HTML itn = (HTML) lw;
					it.setType("ItemNull");
					it.setLabel("");
				}
				
				line.add(it);
			}
			res.add(line);
		}
		
		cube.setItems(res);
		
		return new CubeView(mainPanel, cubeViewer, cube, false, false);
	}

}
