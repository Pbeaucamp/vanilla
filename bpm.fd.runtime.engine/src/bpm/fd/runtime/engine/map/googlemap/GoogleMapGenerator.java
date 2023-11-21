package bpm.fd.runtime.engine.map.googlemap;

import java.util.Arrays;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.google.GoogleMapXmlBuilder;

public class GoogleMapGenerator {
	
	private IResultSet resultSet;
	private Integer labelIndex;
	private Integer valueIndex;
	private Integer longitudeIndex;
	private Integer latitudeIndex;
	private List<ColorRange> colors;
	private String divId;
	
	private Double[] center;
	
	public GoogleMapGenerator(IResultSet resultSet, String mapID, Integer gmapColumnLatitude, 
			Integer gmapColumnLongitude, Integer gmapColumnValue, Integer gmapColumnLabel, ColorRange[] colorRanges) {
		this.resultSet = resultSet;
		this.labelIndex = gmapColumnLabel;
		this.valueIndex = gmapColumnValue;
		this.longitudeIndex = gmapColumnLongitude;
		this.latitudeIndex = gmapColumnLatitude;
		this.colors = Arrays.asList(colorRanges);
		this.divId = mapID;
		
	}
	
	
	public Double[] getCenter(){
		if (center == null){
			return new Double[]{0.,0.};
		}
		return center;
	}
	
	public String generate(){
		GoogleMapXmlBuilder xmlBuilder = new GoogleMapXmlBuilder(divId);
		
		if (resultSet != null){
			try{
				while(resultSet.next()){
					xmlBuilder.addEntity(
							this.resultSet.getString(latitudeIndex), 
							this.resultSet.getString(longitudeIndex), 
							this.resultSet.getString(valueIndex), 
							this.resultSet.getString(labelIndex), 
							colors);
					
					if (center == null){
						try{
							center = new Double[]{Double.parseDouble(this.resultSet.getString(latitudeIndex)), Double.parseDouble(this.resultSet.getString(longitudeIndex))};
						}catch(Exception ex){
							
						}
					}
					else{
						
					}
				}
				
				resultSet.close();
			}catch(Exception ex){
				ex.printStackTrace();
				try {
					resultSet.close();
				} catch (OdaException e) {
					
					e.printStackTrace();
				}
			}
		}
		
		
		
		return xmlBuilder.close();
	}
}
