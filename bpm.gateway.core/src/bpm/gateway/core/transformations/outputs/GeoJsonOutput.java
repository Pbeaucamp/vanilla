package bpm.gateway.core.transformations.outputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunGeoJsonOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class GeoJsonOutput extends AbstractTransformation {

	public static final String TYPE_POINT = "Point";
	public static final String TYPE_LINESTRING = "LineString";
	public static final String TYPE_POLYGON = "Polygon";
	public static final String TYPE_MULTIPOINT = "MultiPoint";
	public static final String TYPE_MULTILINESTRING = "MultiLineString";
	public static final String TYPE_MULTIPOLYGON = "MultiPolygon";

	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	private String filePath;
	private String latitudeColumn;
	private String longitudeColumn;
	private String geometryType = TYPE_POINT;

	@Override
	public Transformation copy() {
		GeoJsonOutput copy = new GeoJsonOutput();
		copy.setFilePath(filePath);
		copy.setLatitudeColumn(latitudeColumn);
		copy.setLongitudeColumn(longitudeColumn);
		copy.setGeometryType(geometryType);
		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + filePath + "\n");
		buf.append("latitudeColumn : " + latitudeColumn + "\n");
		buf.append("longitudeColumn : " + longitudeColumn+ "\n");
		buf.append("geometryType : " + geometryType);
		return buf.toString();
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("geojsonoutput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("filepath").setText(filePath);
		e.addElement("latitude").setText(latitudeColumn);
		e.addElement("longitude").setText(longitudeColumn);
		e.addElement("geotype").setText(geometryType);
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunGeoJsonOutput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		// TODO Auto-generated method stub

	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getLatitudeColumn() {
		return latitudeColumn;
	}

	public void setLatitudeColumn(String latitudeColumn) {
		this.latitudeColumn = latitudeColumn;
	}

	public String getLongitudeColumn() {
		return longitudeColumn;
	}

	public void setLongitudeColumn(String longitudeColumn) {
		this.longitudeColumn = longitudeColumn;
	}

	public String getGeometryType() {
		return geometryType;
	}

	public void setGeometryType(String geometryType) {
		this.geometryType = geometryType;
	}

}
