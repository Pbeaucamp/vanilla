package bpm.map.viewer.web.client.wizard;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexMapAxis;
import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.panel.DesignerPanel;

import com.google.gwt.core.client.GWT;

public class AddComplexMapWizard extends GwtWizard implements IWait {
	
private DesignerPanel designerPanel;
	
	private IGwtPage currentPage;
	
	private AddMapComponentsPage definitionPage;
	private AddMapParamsPage paramPage;
	
	private boolean edit = false;
	private ComplexMap selectedMap;
    //private List<Axis> axisList;
   // private List<Metric> metricList;
    
    public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);
	
	public AddComplexMapWizard(DesignerPanel designerPanel) {
		super(lblCnst.AddANewMap());
		this.edit = false;
		this.designerPanel = designerPanel;

		definitionPage = new AddMapComponentsPage(this, 0);
		
		setCurrentPage(definitionPage);
	}
	
	public AddComplexMapWizard(DesignerPanel MapDesignerPanel, ComplexMap selectedMap) {
		super(lblCnst.EditMap());
		this.edit = true;
		this.designerPanel = MapDesignerPanel;
		this.selectedMap = selectedMap;

		definitionPage = new AddMapComponentsPage(this, 0);
		definitionPage.setName(selectedMap.getName());
		definitionPage.setMetrics(selectedMap.getMetrics());
		definitionPage.setAxis(selectedMap.getAxis());
		definitionPage.loadValues();
		
		//paramPage = new AddMapParamsPage(this, 1);
		

		setCurrentPage(definitionPage);
	}

	@Override
	public boolean canFinish() {
		if (paramPage == null)
			return false;
		else
			return definitionPage.isComplete() && paramPage.isComplete();
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if (page instanceof AddMapComponentsPage)
			setContentPanel((AddMapComponentsPage) page);
		else if (page instanceof AddMapParamsPage)
			setContentPanel((AddMapParamsPage) page);
		currentPage = page;
		updateBtn();
		
	}

	@Override
	protected void onClickFinish() {
		paramPage.saveComplexLevel();
		paramPage.saveComplexMetric();
		ComplexMap map = getCurrentComplexMap();
		designerPanel.addMap(map);
		
		AddComplexMapWizard.this.hide();
	}

	@Override
	protected void onNextClick() {
		if (currentPage instanceof AddMapComponentsPage) {
			
			if(paramPage == null) {
				paramPage = new AddMapParamsPage(this, 1);
			}
			setCurrentPage(paramPage);
			
		}
	}

	@Override
	protected void onBackClick() {
		if (currentPage instanceof AddMapParamsPage) {
			setCurrentPage(definitionPage);
		}
	}
	

	public ComplexMap getCurrentComplexMap() {
		
		List<Metric> metrics = definitionPage.getMetrics();
		List<Axis> axis = definitionPage.getAxis();
		List<ComplexMapMetric> cpxmetrics = new ArrayList<ComplexMapMetric>();
		List<ComplexMapAxis> cpxaxis = new ArrayList<ComplexMapAxis>();
		List<ComplexMapLevel> cpxLevels = new ArrayList<ComplexMapLevel>();
		
		
		String name = definitionPage.getName().trim();

   
	    if(edit) {
	    	for(Axis axe: axis){
				if(selectedMap.getAxis().contains(axe)){
					for(ComplexMapAxis cpx : selectedMap.getComplexAxes()){
						if(cpx.getIdAxis() == axe.getId()){
							cpxaxis.add(cpx);
							break;
						}
							
					}
				} else {
					cpxaxis.add(new ComplexMapAxis(axe));
				}
			}
	    	
	    	if (currentPage instanceof AddMapComponentsPage) {
				for(Metric met: metrics){
					if(selectedMap.getMetrics().contains(met)){
						for(ComplexMapMetric cpx : selectedMap.getComplexMetrics()){
							if(cpx.getIdMetric() == met.getId()){
								cpxmetrics.add(cpx);
								break;
							}
								
						}
						
					} else {
						cpxmetrics.add(new ComplexMapMetric(met));
					}
				}
				
				for(Axis axe: axis){
					if(selectedMap.getAxis().contains(axe)){
						for(ComplexMapLevel cpx : selectedMap.getComplexLevels()){
							if(cpx.getIdAxis() == axe.getId()){
								cpxLevels.add(cpx);
							}
								
						}
					} else {
						List<Level> res = axe.getChildren();
						for(Level lev : res){
							ComplexMapLevel cpx = new ComplexMapLevel(lev);
							cpx.setIdAxis(axe.getId());
							cpxLevels.add(cpx);
							
						};
					}
				}
				
			} else {
				cpxmetrics = paramPage.getComplexMetricList();
				cpxLevels = paramPage.getComplexLevelList();
			}
	    	
	    	for(ComplexMapMetric m : cpxmetrics){
	    		m.setIdMap(selectedMap.getId());
	    	}
	    	for(ComplexMapAxis a : cpxaxis){
	    		a.setIdMap(selectedMap.getId());
	    	}
	    	for(ComplexMapLevel a : cpxLevels){
	    		a.setIdMap(selectedMap.getId());
	    	}
	    	
	    	selectedMap.setComplexAxes(cpxaxis);
	    	selectedMap.setComplexMetrics(cpxmetrics);
			selectedMap.setName(name);
			selectedMap.setComplexLevels(cpxLevels);
			

			return selectedMap;
		}
		else {
			for(Axis a : axis){
				cpxaxis.add(new ComplexMapAxis(a));
				//ajouter les autres variables
			}
			
			if (currentPage instanceof AddMapComponentsPage) {
				for(Metric m : metrics){
					cpxmetrics.add(new ComplexMapMetric(m));
					//ajouter les autres variables
				}
				
				for(Axis a : axis){
					List<Level> res = a.getChildren();
					for(Level lev : res){
						ComplexMapLevel cpx = new ComplexMapLevel(lev);
						cpx.setIdAxis(a.getId());
						cpxLevels.add(cpx);
						
					}
					
				}
			} else {
				cpxmetrics = paramPage.getComplexMetricList();
				cpxLevels = paramPage.getComplexLevelList();
			}
			
			
			ComplexMap map = new ComplexMap(name, cpxmetrics, cpxaxis, cpxLevels);

			return map;
		}
		
		
	}
	
}
