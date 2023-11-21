package bpm.vanilla.map.core.design;

import bpm.vanilla.map.core.design.fusionmap.IFactoryFusionMap;
import bpm.vanilla.map.core.design.kml.IFactoryKml;

public interface IFactoryModelProvider {
	public IFactoryModelObject getFactoryModelProvider();
	public IFactoryFusionMap getFactoryFusionMap();
	public IFactoryKml getFactoryKml();
}
