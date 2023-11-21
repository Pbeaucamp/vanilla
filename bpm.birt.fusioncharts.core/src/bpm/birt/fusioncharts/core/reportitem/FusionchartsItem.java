package bpm.birt.fusioncharts.core.reportitem;

import java.util.Properties;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.extension.ReportItem;

import bpm.birt.fusioncharts.core.xmldata.FusionChartProperties;

public class FusionchartsItem extends ReportItem {
	public static final String EXTENSION_NAME = "VanillaCharts"; 
	public static final String XML_PROPERTY = "XmlProperty"; 
	public static final String VANILLA_RUNTIME_URL = "VanillaRuntimeUrl"; 
	public static final String CUSTOM_PROPERTY = "CustomProperties";
	

	private ExtendedItemHandle modelHandle;
	
	public FusionchartsItem( ExtendedItemHandle modelHandle ) {
		this.modelHandle = modelHandle;
	}
	
	public ExtendedItemHandle getItemHandle(){
		return modelHandle;
	}
	
	public String getXml( ) {
		return modelHandle.getStringProperty( XML_PROPERTY );
	}
	
	public void setXml( String value ) throws SemanticException {
		modelHandle.setProperty( XML_PROPERTY, value );
	}
	
	public String getVanillaRuntimeUrl( ) {
		return modelHandle.getStringProperty( VANILLA_RUNTIME_URL );
	}
	
	public void setVanillaRuntimeUrl( String value ) throws SemanticException {
		modelHandle.setProperty( VANILLA_RUNTIME_URL, value );
	}
	
	public Properties getCustomProperties(){
		String props = (String)modelHandle.getProperty(CUSTOM_PROPERTY);
		return FusionChartProperties.buildPropertiesFromXml(props);
	}
	
	public void setCustomProperties(Properties props) throws SemanticException{
		String propsXml = FusionChartProperties.buildPropertiesXml(props);
		modelHandle.setProperty(CUSTOM_PROPERTY, propsXml);
	}
}
