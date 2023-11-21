package bpm.birt.wms.map.core.reportitem;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.extension.ReportItem;

public class WMSMapItem extends ReportItem {

	public static final String EXTENSION_NAME = "WMSMapItem";
	
	public static final String P_BASE_LAYER_URL = "baseLayerUrl";
	public static final String P_BASE_LAYER_NAME = "baseLayerName";
	public static final String P_VECTOR_LAYER_URL = "vectorLayerUrl";
	public static final String P_VECTOR_LAYER_NAME = "vectorLayerName";
	public static final String P_BOUNDS = "bounds";
	public static final String P_TILE_ORIGIN = "tileOrigin";
	public static final String P_PROJECTION = "projection";
	
	public static final String P_VALUE_COLUMN = "valueColumn";
	public static final String P_ZONE_ID_COLUMN = "zoneIdColumn";
	
	public static final String P_COLOR_RANGE = "colorRange";

	public static final String P_OPACITY = "opacity";
	public static final String P_WIDTH = "width";
	public static final String P_HEIGHT = "height";

	public static final String P_VANILLARUNTIME = "vanillaRuntime";
	
	private ExtendedItemHandle handle;

	public WMSMapItem(){}
	
	public WMSMapItem(ExtendedItemHandle handle) {
		this.handle = handle;
	}
	
	public String getPropertyString(String prop) {
		return handle.getStringProperty(prop);
	}
	
	@Override
	public Object getProperty(String propName) {
		return handle.getProperty(propName);
	}
	
	@Override
	public void setProperty(String propName, Object value) {
		try {
			handle.setProperty(propName, value);
		} catch(SemanticException e) {
			e.printStackTrace();
		}
	}
	
}
