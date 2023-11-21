package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.util.ConverterBase;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;

public class LineChartDrillableSliceConverter extends ConverterBase<DrillableDot>{

	@Override
	public void convert(DrillableDot o, PathTrackingWriter writer, MarshallingContext mc) {
		writeNode(writer, "value", o.getValue(), false);
		writeNode(writer, "colour", o.getColour(), true);
		writeNode(writer, "dot-size", o.getDotSize(), true);
		writeNode(writer, "halo-size", o.getHaloSize(), true);
		writeNode(writer, "on-click", o.getUrl(), true);
		
	}

	public boolean canConvert(Class c) {
		return DrillableDot.class.isAssignableFrom(c);
	}

}
