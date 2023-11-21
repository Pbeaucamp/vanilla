package bpm.vanilla.map.core.design;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;

public interface IVanillaMapDaoProvider {
	public IMapDefinitionService getDefinitionService() throws Exception;
	public IFusionMapRegistry getFusionMapRegistry()throws Exception;
	public IKmlRegistry getKmlRegistry()throws Exception;
	public IOpenGisMapService getOpenGisMapService() throws Exception;
}
