package bpm.fd.runtime.engine.map.fusionmap;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fd.api.core.model.components.definition.maps.MapDrillInfo;
import bpm.fd.api.core.model.components.definition.maps.MapDrillInfo.TargetType;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.fusionmap.FusionMapXmlBuilder;

public class FusionMapXmlGenerator implements IFlashMapXmlGenerator {

	private boolean isShowLabels;
	
	public FusionMapXmlGenerator(boolean isShowLabels) {
		this.isShowLabels = isShowLabels;
	}
	
	/**
	 * return the string XML for fusionMap components
	 * @param colorRanges
	 * @param valueIndex
	 * @param zoneIndex
	 * @param resultSet
	 * @return
	 * @throws Exception
	 */
	public String generateXml(ColorRange[] colorRanges, 
			Integer zoneIndex, 
			Integer valueIndex, 
			IResultSet resultSet,
			MapDrillInfo drillInfo) throws Exception{
		List<ColorRange> l = Arrays.asList(colorRanges);

		FusionMapXmlBuilder xmlBuilder = new FusionMapXmlBuilder("", l,isShowLabels){
			@Override
			protected String link(String linkName, String link) {
				StringBuilder builder = new StringBuilder(linkName);
				builder.insert(0, " ");

				builder.append("=\\\"");
				builder.append(link);
				builder.append("\\\"");
				return builder.toString();
			}
		};
		
		if (resultSet != null){
			try{
				while(resultSet.next()){
					
					
					if (drillInfo == null || !drillInfo.isDrillable()){
						xmlBuilder.addEntity(resultSet.getString(zoneIndex), resultSet.getString(valueIndex));	
					}
					else{
						String v = drillInfo.isSendCategory() ? resultSet.getString(zoneIndex) : resultSet.getString(valueIndex);
						
						try{
							String link =" setParameter('" + 
								drillInfo.getComponentMap().getName() + "', '" + URLEncoder.encode(v, "UTF-8") +"');";
							
							if (drillInfo.getTarget() == TargetType.PopupPage){
								link += " popupModelPage('" + URLEncoder.encode(drillInfo.getModelPage().getName(), "UTF-8") + "','" + drillInfo.getWidth() + "','" + drillInfo.getHeight()	+"');";
							}
							else if (drillInfo.getTarget() == TargetType.FolderPage){
								link +=" setParameter('folder','" + URLEncoder.encode(drillInfo.getFolderPageName(), "UTF-8") + "');";
							}
							
							xmlBuilder.addEntity(
									resultSet.getString(zoneIndex), 
									resultSet.getString(valueIndex), 
									"JavaScript:" + link);
						}catch(Throwable t){
							t.printStackTrace();
							xmlBuilder.addEntity(resultSet.getString(zoneIndex), resultSet.getString(valueIndex));
						}
						
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
		
		
		
		String s = xmlBuilder.close();
		
		return s;//.replace("\"", "\\\"");
	}

	@Override
	public String generateXmlForMetrics(ColorRange[] colorRanges, HashMap<String, String> valuesMap) {
		List<ColorRange> l = Arrays.asList(colorRanges);
		FusionMapXmlBuilder xmlBuilder = new FusionMapXmlBuilder("", l, isShowLabels);
		
		for(String val : valuesMap.keySet()) {
			xmlBuilder.addEntity(val, valuesMap.get(val));
		}
		
		String s = xmlBuilder.close();
		return s;
	}
	
}
