package bpm.birt.fusionmaps.core.reportitem;

import java.util.Properties;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.extension.ReportItem;

import bpm.vanilla.map.core.design.fusionmap.FusionMapProperties;

public class FusionmapsItem extends ReportItem {
	public static final String EXTENSION_NAME = "VanillaMaps"; 
	public static final String VANILLA_RUNTIME_URL_PROP = "VanillaRuntimeURL"; 
	public static final String SWF_URL_PROP = "SWFURL"; 
	public static final String MAP_ID_PROP = "MapID";
	public static final String MAP_WIDTH_PROP = "MapWidth";
	public static final String MAP_HEIGHT_PROP = "MapHeight";
	public static final String MAP_DATAXML_PROP = "MapDataXML";
	public static final String LIST_MAPXML_PROP = "ListMapXML";
	public static final String IS_DRILLDOWNABLE = "IsDrilldownable";
	public static final String DRILL_DOWN_LINK = "DrillDownLink";
	public static final String CUSTOM_PROPERTY = "CustomProperties";
	

	private ExtendedItemHandle modelHandle;
	
	public FusionmapsItem( ExtendedItemHandle modelHandle ) {
		this.modelHandle = modelHandle;
	}
	
	public ExtendedItemHandle getItemHandle(){
		return modelHandle;
	}
	
	public String getVanillaRuntimeURL( ) {
		return modelHandle.getStringProperty( VANILLA_RUNTIME_URL_PROP );
	}
	
	public void setVanillaRuntimeURL( String value ) throws SemanticException {
		modelHandle.setProperty( VANILLA_RUNTIME_URL_PROP, value );
	}
	
	public String getSwfURL( ) {
		return modelHandle.getStringProperty( SWF_URL_PROP );
	}
	public void setSwfURL( String value ) throws SemanticException {
		modelHandle.setProperty( SWF_URL_PROP, value );
	}
	
	public String getMapID( ) {
		return modelHandle.getStringProperty( MAP_ID_PROP );
	}
	public void setMapID( String value ) throws SemanticException {
		modelHandle.setProperty( MAP_ID_PROP, value );
	}
	
	public int getMapWidth( ) {
		return modelHandle.getIntProperty( MAP_WIDTH_PROP );
	}
	public void setMapWidth (int value ) throws SemanticException {
		modelHandle.setProperty( MAP_WIDTH_PROP, value );
	}

	public int getMapHeight( ) {
		return modelHandle.getIntProperty( MAP_HEIGHT_PROP );
	}
	
	public void setMapHeight( int value ) throws SemanticException {
		modelHandle.setProperty( MAP_HEIGHT_PROP, value );
	}
	
	public String getMapDataXML( ) {
		return modelHandle.getStringProperty( MAP_DATAXML_PROP );
	}
	
	public void setMapDataXML( String value ) throws SemanticException {
		modelHandle.setProperty( MAP_DATAXML_PROP, value );
	}
	
	public String getListMapXML( ) {
		return modelHandle.getStringProperty( LIST_MAPXML_PROP );
	}
	
	public void setListMapXml( String value ) throws SemanticException {
		modelHandle.setProperty( LIST_MAPXML_PROP, value );
	}
	
	public String getDrillDownLink(){
		return modelHandle.getStringProperty(DRILL_DOWN_LINK);
	}
	
	public void setDrillDownLink( String value) throws SemanticException{
		modelHandle.setProperty(DRILL_DOWN_LINK, value);
	}
	
	public boolean isDrillDownable(){
		return modelHandle.getBooleanProperty(IS_DRILLDOWNABLE);
	}
	
	public void setIsDrillDownable(boolean value) throws SemanticException{
		modelHandle.setBooleanProperty(IS_DRILLDOWNABLE, value);
	}
	
	public Properties getCustomProperties(){
		String props = (String)modelHandle.getProperty(CUSTOM_PROPERTY);
		return FusionMapProperties.buildPropertiesFromXml(props);
	}
	
	public void setCustomProperties(Properties props) throws SemanticException{
		String propsXml = FusionMapProperties.buildPropertiesXml(props);
		modelHandle.setProperty(CUSTOM_PROPERTY, propsXml);
	}
}
