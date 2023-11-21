package bpm.fd.runtime.engine.map.fusionmap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.fusionmap.VanillaMapXmlBuilder;

public class VanillaMapXmlGenerator implements IFlashMapXmlGenerator {
	/**
	 * return the string XML for VanillaMap components
	 * @param colorRanges
	 * @param valueIndex
	 * @param zoneIndex
	 * @param resultSet
	 * @return
	 * @throws Exception
	 */
	public String generateXml(ColorRange[] colorRanges, Integer zoneIndex, Integer valueIndex, IResultSet resultSet, String drillUrl) throws Exception{
		List<ColorRange> l = Arrays.asList(colorRanges);
		VanillaMapXmlBuilder xmlBuilder = new VanillaMapXmlBuilder("", l);
		
		if (resultSet != null){
			try{
				while(resultSet.next()){
					xmlBuilder.addEntity(resultSet.getString(zoneIndex), resultSet.getString(valueIndex));
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
		
		
		
		String s = xmlBuilder.close();
		
		return s;
	}

	@Override
	public String generateXmlForMetrics(ColorRange[] colorRanges, HashMap<String, String> valuesMap) {
		List<ColorRange> l = Arrays.asList(colorRanges);
		VanillaMapXmlBuilder xmlBuilder = new VanillaMapXmlBuilder("", l);
		
		for(String val : valuesMap.keySet()) {
			xmlBuilder.addEntity(val, valuesMap.get(val));
		}
		
		String s = xmlBuilder.close();
		
		return s;
	}
	
}
