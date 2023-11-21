package bpm.vanilla.map.model.fusionmap.impl;

import bpm.vanilla.map.core.design.fusionmap.IFactoryFusionMap;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;

public class FactoryFusionMap implements IFactoryFusionMap{

	@Override
	public IFusionMapObject createFusionMapObject() {
		return new FusionMapObject();
	}

	@Override
	public IFusionMapSpecificationEntity createFusionMapSpecificationEntity() {
		return new FusionMapSpecificationEntity();
	}

}
