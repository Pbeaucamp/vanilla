package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.util.ConverterBase;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;

public class PieChartDrillableSliceConverter extends ConverterBase<DrillableSlice>{

	@Override
	public void convert(DrillableSlice o, PathTrackingWriter writer, MarshallingContext mc) {
		writeNode(writer, "value", o.getValue(), false);
		writeNode(writer, "label", o.getLabel(), true);
		writeNode(writer, "tip", o.getTip(), true);
		writeNode(writer, "highlight", o.getHighlight(), false);
		writeNode(writer, "text", o.getText(), true);
		writeNode(writer, "on-click", o.getUrl(), true);
		
	}

	public boolean canConvert(Class c) {
		return DrillableSlice.class.isAssignableFrom(c);
	}

}
