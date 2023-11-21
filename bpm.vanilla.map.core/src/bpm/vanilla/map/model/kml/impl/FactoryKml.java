package bpm.vanilla.map.model.kml.impl;

import bpm.vanilla.map.core.design.kml.IFactoryKml;
import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.kml.IKmlSpecificationEntity;

public class FactoryKml implements IFactoryKml {

	@Override
	public IKmlObject createKmlObject() {
		return new KmlObject();
	}

	@Override
	public IKmlSpecificationEntity createKmlSpecificationEntity() {
		return new KmlSpecificationEntity();
	}
}
