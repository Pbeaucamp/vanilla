package bpm.vanilla.map.core.design.opengis;

import java.util.List;

import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.FMContext;

/**
 * This Class is used to render an OpenGis Map
 * It's in map.core to avoid adding all the Geotools jars in other plugins.
 * @author Marc Lanquetin
 *
 */
public class OpenGisMapRenderer {

	private OpenGisMapObject map;
	private List<ColorRange> colorRanges;
	private IVanillaContext vanillaContext;
	private FMContext fmContext;

	public OpenGisMapRenderer(OpenGisMapObject map, List<ColorRange> colorRanges, IVanillaContext vanillaContext, FMContext fmContext) {
		this.map = map;
		this.colorRanges = colorRanges;
		this.vanillaContext = vanillaContext;
		this.fmContext = fmContext;
	}
	
}
