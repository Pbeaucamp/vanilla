package bpm.vanilla.map.model;

import bpm.vanilla.map.core.design.IFactoryModelObject;
import bpm.vanilla.map.core.design.IFactoryModelProvider;
import bpm.vanilla.map.core.design.fusionmap.IFactoryFusionMap;
import bpm.vanilla.map.core.design.kml.IFactoryKml;
import bpm.vanilla.map.model.fusionmap.impl.FactoryFusionMap;
import bpm.vanilla.map.model.impl.FactoryMapModel;
import bpm.vanilla.map.model.kml.impl.FactoryKml;

public class VanillaMapFactoryModelProviderComponent implements IFactoryModelProvider{
	public static final String PLUGIN_ID = "bpm.vanilla.map.model";
	
	
	private IFactoryFusionMap factoryFusionMap = new FactoryFusionMap();
	private IFactoryKml factoryKml = new FactoryKml();
	private IFactoryModelObject factoryMapModel = new FactoryMapModel();
	
	
	
	public VanillaMapFactoryModelProviderComponent(){
		
	}
	
	@Override
	public IFactoryFusionMap getFactoryFusionMap() {
		return factoryFusionMap;
	}

	@Override
	public IFactoryKml getFactoryKml() {
		return factoryKml;
	}

	@Override
	public IFactoryModelObject getFactoryModelProvider() {
		return factoryMapModel;
	}
	
	

}
