package bpm.fd.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.core.IComponentData;
import bpm.fd.core.component.RChartComponent;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RChartDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static RChartDataPropertiesUiBinder uiBinder = GWT.create(RChartDataPropertiesUiBinder.class);

	interface RChartDataPropertiesUiBinder extends UiBinder<Widget, RChartDataProperties> {
	}

	// Classe correspondant a l'onglet Data ou donnnes de la fenetre du composant R charrt
	
	@UiField
	ListBoxWithButton groupField, axeXField , axeYField,listOfColumn ;//groupFieldLabel, subGroupField, orderType;

	private Dataset selectedDataset;
	private RChartComponent component;
	
	public RChartDataProperties(RChartComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		this.component = component;
		
		if(this.component.getNature().getNature() != RChartNature.CORRELATION ){
			this.listOfColumn.setVisible(false);
		}
	}
	
	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		this.setSelectedDataset(dataset);
		this.groupField.setListWithEmptyItem( dataset.getMetacolumns() );
		this.axeXField.setListWithEmptyItem( dataset.getMetacolumns() );
		this.axeYField.setListWithEmptyItem( dataset.getMetacolumns() );

		
		if( component.getDataset() != null && component.getDataset().getId() == dataset.getId() ){
			this.groupField.setSelectedIndex( component.getGroupFieldIndex()+1);
			this.axeXField.setSelectedIndex(component.getAxeXField()+1);
			this.axeYField.setSelectedIndex(component.getAxeYField()+1);
		}
		
		if(this.component.getNature().getNature() == RChartNature.TREEMAP ){
			this.axeXField.setLabel("Area");
			this.axeYField.setLabel("Fill");
			this.groupField.setVisible(false);
		}
		if(this.component.getNature().getNature() == RChartNature.PIE ){
			this.axeYField.setVisible(false);
		}
		if(this.component.getNature().getNature() == RChartNature.CORRELATION ){
			this.groupField.setVisible(false);
			this.axeXField.setVisible(false);
			this.axeYField.setVisible(false);
			this.listOfColumn.setVisible(true);
			this.listOfColumn.setMultiple(true);
			//this.listOfColumn.setList( dataset.getMetacolumns() );
			this.listOfColumn.setList( this.getColumnName(dataset) );
			//getColumnName(dataset)
		}
	}

	@Override
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}

	@Override
	public void buildProperties(IComponentData component) {
		// TODO Auto-generated method stub
		RChartComponent comp = (RChartComponent) component;

		comp.setGroupFieldIndex(this.groupField.getSelectedIndex() );
		comp.setAxeXField(this.axeXField.getSelectedIndex() );
		comp.setAxeYField(this.axeYField.getSelectedIndex() );
		comp.setGroupFieldLabel(this.groupField.getSelectedItem());
		comp.setAxeXFieldLabel(this.axeXField.getSelectedItem());
		comp.setAxeYFieldLabel( this.axeYField.getSelectedItem());

		if(this.component.getNature().getNature() == RChartNature.CORRELATION ){
			comp.setSelectedColumList( this.listOfColumn.getSelectedItems() );
			//comp.setSelectedColumName(  );
		}
		
//		System.out.println("*****axe x selected: "+this.axeXField.getSelectedItem());
//		System.out.println("*****axe y selected: "+this.axeYField.getSelectedItem());
		//System.out.println("*****Group selected: "+this.groupField.getSelectedItem());
	}


	public List<String> getColumnName(Dataset dataset) 	{
		
		List<DataColumn> metacolumns = dataset.getMetacolumns() ;
		List<String> names = new ArrayList();
		
		for(int i=0; i<metacolumns.size(); i++){
			names.add( metacolumns.get(i).getColumnName() ) ;
		}
		return names;
	}
	public Dataset getSelectedDataset() {
		return selectedDataset;
	}


	public void setSelectedDataset(Dataset selectedDataset) {
		this.selectedDataset = selectedDataset;
	}

}
