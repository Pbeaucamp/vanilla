package bpm.fd.runtime.engine.chart.ofc.generator;

import jofc2.OFC;
import jofc2.util.ConverterBase;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.path.PathTrackingWriter;

public class HorizontalBarChartDrillableSliceConverter extends ConverterBase<DrillableHorizontalBar>{
	static{
		 OFC.getInstance().doAlias(DrillableHorizontalBar.class);
		 OFC.getInstance().doRegisterConverter(DrillableHorizontalBar.class);
	}
	@Override
	public void convert(DrillableHorizontalBar b, PathTrackingWriter writer, MarshallingContext mc) {
		writeNode(writer, "right", b.getRight(), false);
		writeNode(writer, "left", b.getLeft(), false);
		writeNode(writer, "on-click", b.getUrl(), true);
		
	}

	public boolean canConvert(Class c) {
		return DrillableHorizontalBar.class.isAssignableFrom(c);
	}

}
