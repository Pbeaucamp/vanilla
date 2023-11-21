package bpm.vanilla.map.core.design.kml;

public interface IKmlManipulator {
	/**
	 * load the kml file from the kmlObjectId, then using coloringDatas 
	 * to set the color for each placemark within the KML
	 * it return the URL of the generated Kml
	 * 
	 * @param kmlObjectId
	 * @param coloringDatas
	 * @return
	 * @throws Exception
	 */
	public String generateKml(String originalKmlFileUrl, KmlColoringDatas coloringDatas) throws Exception;
}
