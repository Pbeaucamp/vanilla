package bpm.fd.runtime.engine.map.fusionmap;

import java.util.HashMap;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public interface IFlashMapXmlGenerator {

	public String generateXmlForMetrics(ColorRange[] colorRanges, HashMap<String, String> valuesMap);
	
}
