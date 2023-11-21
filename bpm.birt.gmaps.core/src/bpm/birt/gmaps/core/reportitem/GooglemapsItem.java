package bpm.birt.gmaps.core.reportitem;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.extension.ReportItem;

public class GooglemapsItem extends ReportItem {
	public static final String EXTENSION_NAME = "VanillaGMaps";
	public static final String GMAP_ID_PROP = "GMapID";
//	public static final String GMAP_CENTER_LATITUDE = "GMapCenterLatitude";
//	public static final String GMAP_CENTER_LONGITUDE = "GMapCenterLongitude";
//	public static final String GMAP_ZOOM = "GMapZoom";
	public static final String GMAP_WIDTH_PROP = "GMapWidth";
	public static final String GMAP_HEIGHT_PROP = "GMapHeight";
	public static final String GMAP_COLUMN_LATITUDE = "GMapColumnLatitude";
	public static final String GMAP_COLUMN_LONGITUDE = "GMapColumnLongitude";
	public static final String GMAP_COLUMN_LABEL = "GMapColumnLabel";
	public static final String GMAP_COLUMN_VALUE = "GMapColumnValue";
//	public static final String GMAP_COLOR_RANGES = "GMapColorRanges";
	
	
	private ExtendedItemHandle modelHandle;
	
	GooglemapsItem( ExtendedItemHandle modelHandle ) {
		this.modelHandle = modelHandle;
	}
	
	public String getMapID( ) {
		return modelHandle.getStringProperty( GMAP_ID_PROP );
	}
	
	public void setMapID( String value ) throws SemanticException {
		modelHandle.setProperty( GMAP_ID_PROP, value );
	}
	
	
	
	
	
//	public String getCenterLatitude(){
//		return modelHandle.getStringProperty(GMAP_CENTER_LATITUDE);
//	}
//	
//	public void setCenterLatitude(String value) throws SemanticException{
//		modelHandle.setProperty(GMAP_CENTER_LATITUDE, value);
//	}
//	
//	public String getCenterLongitude(){
//		return modelHandle.getStringProperty(GMAP_CENTER_LONGITUDE);
//	}
//	
//	public void setCenterLongitude(String value) throws SemanticException{
//		modelHandle.setProperty(GMAP_CENTER_LONGITUDE, value);
//	}
//	
//	public int getZoom(){
//		return modelHandle.getIntProperty(GMAP_ZOOM);
//	}
//	
//	public void setZoom(int value) throws SemanticException{
//		modelHandle.setProperty(GMAP_ZOOM, value);
//	}
	
	public int getMapWidth( ) {
		return modelHandle.getIntProperty( GMAP_WIDTH_PROP );
	}
	public void setMapWidth (int value ) throws SemanticException {
		modelHandle.setProperty( GMAP_WIDTH_PROP, value );
	}

	public int getMapHeight( ) {
		return modelHandle.getIntProperty( GMAP_HEIGHT_PROP );
	}
	
	public void setMapHeight( int value ) throws SemanticException {
		modelHandle.setProperty( GMAP_HEIGHT_PROP, value );
	}
	
	
	public String getMapColumnLabel( ) {
		return modelHandle.getStringProperty( GMAP_COLUMN_LABEL );
	}
	
	public void setMapColumnLabel( String value ) throws SemanticException {
		modelHandle.setProperty( GMAP_COLUMN_LABEL, value );
	}
	
	public String getMapColumnLatitude( ) {
		return modelHandle.getStringProperty( GMAP_COLUMN_LATITUDE );
	}
	
	public void setMapColumnLatitude( String value ) throws SemanticException {
		modelHandle.setProperty( GMAP_COLUMN_LATITUDE, value );
	}
	
	public String getMapColumnLongitude( ) {
		return modelHandle.getStringProperty( GMAP_COLUMN_LONGITUDE );
	}
	
	public void setMapColumnLongitude( String value ) throws SemanticException {
		modelHandle.setProperty( GMAP_COLUMN_LONGITUDE, value );
	}
	
	public String getMapColumnValue( ) {
		return modelHandle.getStringProperty( GMAP_COLUMN_VALUE );
	}
	
	public void setMapColumnValue( String value ) throws SemanticException {
		modelHandle.setProperty( GMAP_COLUMN_VALUE, value );
	}
	
//	public void setMapColorRanges( String value ) throws SemanticException {
//		modelHandle.setProperty( GMAP_COLOR_RANGES, value );
//	}
//	
//	public String getMapColorRanges( ) {
//		return modelHandle.getStringProperty( GMAP_COLOR_RANGES );
//	}
}
