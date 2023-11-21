package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.util.ConverterBase;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;

public class BarChartDrillableSliceConverter extends ConverterBase<DrillableBar>{

	@Override
	public void convert(DrillableBar b, PathTrackingWriter writer, MarshallingContext mc) {
		writeNode(writer, "top", b.getTop(), false);
		writeNode(writer, "bottom", b.getBottom(), true);
		writeNode(writer, "colour", b.getColour(), true);
		writeNode(writer, "tip", b.getTooltip(), true);
		writeNode(writer, "on-click", b.getUrl(), true);
		
	}

	public boolean canConvert(Class c) {
		return DrillableBar.class.isAssignableFrom(c);
	}

}
