package bpm.fd.web.client.panels.properties;

import java.util.HashMap;
import java.util.Map;

import bpm.fd.api.core.model.components.definition.chart.RChartNature;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.web.client.I18N.Labels;
import bpm.fd.web.client.panels.properties.ChartTypeProperties.TypeChart;
import bpm.fd.web.client.panels.properties.RChartDataProperties.RChartDataPropertiesUiBinder;
import bpm.fd.web.client.utils.RChartTypeHelper;
import bpm.gwt.commons.client.custom.ListBoxWithButton;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class RChartTypeProperties extends Composite {
	
	
	public enum TypeRChart{
		
		POINT(0),LINE(1),BAR_V(2),BAR_H(3), HIST(4) , PIE(5), BOXPLOT(6),HEATMAP(7),TREEMAP(8), CORRELATION(9),ACP(10);
		
		private int type;
		
		private static Map<Integer,TypeRChart> map = new HashMap<Integer,TypeRChart>();
		static{
			
			for( TypeRChart serverType: TypeRChart.values() ){
				map.put(serverType.getType(), serverType);
			}
		}
		
		private TypeRChart(int type){
			this.type = type; 
		}
		
		public int getType(){
			return type;
		}
		
		public static TypeRChart valueOf(int type){
			return map.get(type);
		}
		
	}
	
	
	private static RChartDataPropertiesUiBinder uiBinder = GWT.create( RChartDataPropertiesUiBinder.class );
	
	interface RChartDataPropertiesUiBinder  extends UiBinder<Widget, RChartTypeProperties>{	
	}
	
	@UiField
	ListBoxWithButton listBoxTypesChart ;
	
	@UiField
	Image imgChart;
	
	private RChartComponent component;
	private PropertiesPanel propertiesPanel;
	
	public RChartTypeProperties(RChartComponent component , PropertiesPanel propertiesPanel ){
		
		initWidget(uiBinder.createAndBindUi(this) );
		
		this.component = component; 
		this.propertiesPanel = propertiesPanel ;
		
		for( TypeRChart type : TypeRChart.values() ){
			listBoxTypesChart.addItem( findName(type) , String.valueOf(type.getType()) );
		}
		
		if( component !=null && component.getNature() != null ){
			buidContent( component.getNature() ) ;
		}
		
	}
	
	@UiHandler("listBoxTypesChart")
	public void onChangeChart(ChangeEvent event ){
		this.updateUi();
	}
	

	private void buidContent(RChartNature nature) {
		// TODO Auto-generated method stub
		TypeRChart type =  RChartTypeHelper.findType(nature);
		this.listBoxTypesChart.setSelectedIndex( type.getType() );
		
		updateUi();
	}

	private void updateUi() {
		// TODO Auto-generated method stub
		TypeRChart typeChart = getSelectedTypeChart();
		
		DataResource img = RChartTypeHelper.findImage(typeChart);
		this.imgChart.setUrl( img.getSafeUri() );
		
		component.setNature( RChartTypeHelper.getRChartNature((RChartComponent) component, typeChart)) ;
		
		if( propertiesPanel != null ){
			propertiesPanel.refreshOptions(component);
		}	
	}

	public TypeRChart getSelectedTypeChart() {
		// TODO Auto-generated method stub
		int type = Integer.parseInt( this.listBoxTypesChart.getSelectedItem() );
		return  TypeRChart.valueOf(type);
	}

	private String findName(TypeRChart type) {
		// TODO Auto-generated method stub
		
		switch(type){
		case POINT:
			return Labels.lblCnst.POINT();
		case LINE:
			return Labels.lblCnst.LINE();
		case BAR_V:
			return Labels.lblCnst.BAR_V();
		case BAR_H:
			return Labels.lblCnst.BAR_H();
		case HIST:
			return Labels.lblCnst.HIST();
		case PIE:
			return Labels.lblCnst.Pie();
		case BOXPLOT:
			return Labels.lblCnst.Boxplot();
		case HEATMAP:
			return Labels.lblCnst.Heatmap();
		case TREEMAP:
			return Labels.lblCnst.Treemap();
		case ACP:
			return Labels.lblCnst.Acp();
		case CORRELATION:
			return Labels.lblCnst.Correlation();
			
		
		default:
			break;
		}
		
		return Labels.lblCnst.Unknown();
	}
	
	

}
