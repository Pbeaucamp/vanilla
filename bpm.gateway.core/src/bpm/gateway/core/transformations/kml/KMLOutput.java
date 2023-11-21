package bpm.gateway.core.transformations.kml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunKmlOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class KMLOutput extends AbstractTransformation implements DataStream, Kml{

	private KmlObjectType kmlObjectType = KmlObjectType.Point;
	
	public static final int LONGITUDE = 0;
	public static final int LATITUDE = 1;
	public static final int ALTITUDE = 2;
	public static final int NAME = 3;
	public static final int DESCRIPTION = 4;
		
	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();
	
	/**
	 * [0] = longitude
	 * [1] = latitude
	 * [2] = altitude (may be null)
	 */
	private Integer[] inputFieldsCoordinates = new Integer[5];
	
		
	private Server server;
	private String definition = "";
	
	
	private String rootPath;
	private boolean deleteFirst;
	
	
	
	public KMLOutput(){
		StreamElement e = new StreamElement();
		e.className = "java.lang.Double";
		e.name = "Longitude";
		e.typeName = "DOUBLE";
		descriptor.addColumn(e);
		
		e = new StreamElement();
		e.className = "java.lang.Double";
		e.name = "Latitude";
		e.typeName = "DOUBLE";
		descriptor.addColumn(e);
		
		e = new StreamElement();
		e.className = "java.lang.Double";
		e.name = "Altitude";
		e.typeName = "DOUBLE";
		descriptor.addColumn(e);
		
		e = new StreamElement();
		e.className = "java.lang.String";
		e.name = "Name";
		e.typeName = "STRING";
		
		e = new StreamElement();
		e.className = "java.lang.String";
		e.name = "Description";
		e.typeName = "STRING";
		
		descriptor.addColumn(e);
		
		
	}
	
	

	public KmlObjectType getKmlObjectType(){
		return kmlObjectType;
	}
	
	public void setKmlObjectType(KmlObjectType kmlObjectType){
		this.kmlObjectType = kmlObjectType;
	}
	
	public void setKmlObjectType(String kmlObjectTypeName){
		try{
			this.kmlObjectType = KmlObjectType.valueOf(kmlObjectTypeName);
		}catch(Exception ex){
			
		}
	}
	
	public void setCoordinateLongitudeIndex(Integer index){
		inputFieldsCoordinates[LONGITUDE] = index;
	}
	public void setCoordinateLongitudeIndex(String index){
		try{
			inputFieldsCoordinates[LONGITUDE] = Integer.parseInt(index);
		}catch(Exception ex){
			
		}
	}
	public Integer getCoordinateLongitudeIndex(){
		return inputFieldsCoordinates[LONGITUDE];
	}
	
	public void setCoordinateLatitudeIndex(Integer index){
		inputFieldsCoordinates[LATITUDE] = index;
	}
	public void setCoordinateLatitudeIndex(String index){
		try{
			inputFieldsCoordinates[LATITUDE] = Integer.parseInt(index);
		}catch(Exception ex){
			
		}
	}
	public Integer getCoordinateLatitudeIndex(){
		return inputFieldsCoordinates[LATITUDE];
	}
	
	public void setCoordinateAltitudeIndex(Integer index){
		inputFieldsCoordinates[ALTITUDE] = index;
	}
	public void setCoordinateAltitudeIndex(String index){
		try{
			inputFieldsCoordinates[ALTITUDE] = Integer.parseInt(index);
		}catch(Exception ex){
			
		}
	}
	public Integer getCoordinateAltitudeIndex(){
		return inputFieldsCoordinates[ALTITUDE];
	}
	
	public void setNameIndex(Integer index){
		inputFieldsCoordinates[NAME] = index;
	}
	public void setNameIndex(String index){
		try{
			inputFieldsCoordinates[NAME] = Integer.parseInt(index);
		}catch(Exception ex){
			
		}
	}
	public Integer getDescriptionIndex(){
		return inputFieldsCoordinates[DESCRIPTION];
	}
	
	public void setDescriptionIndex(Integer index){
		inputFieldsCoordinates[DESCRIPTION] = index;
	}
	public void setDescriptionIndex(String index){
		try{
			inputFieldsCoordinates[DESCRIPTION] = Integer.parseInt(index);
		}catch(Exception ex){
			
		}
	}
	public Integer getNameIndex(){
		return inputFieldsCoordinates[NAME];
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("kmlOutput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (getServer() != null){
			e.addElement("serverRef").setText(getServer().getName());
		}
		
		if (getDefinition() != null){
			e.addElement("definition").setText(getDefinition());
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		e.addElement("objectType").setText(kmlObjectType.name());
		
		
		if (getCoordinateLongitudeIndex() != null){
			e.addElement("longitudeIndex").setText(inputFieldsCoordinates[LONGITUDE] + "");
		}
		
		if (getCoordinateLatitudeIndex() != null){
			e.addElement("latitudeIndex").setText(inputFieldsCoordinates[LATITUDE] + "");
		}
		
		if (getCoordinateAltitudeIndex() != null){
			e.addElement("altitudeIndex").setText(inputFieldsCoordinates[ALTITUDE] + "");
		}
		
		if (getNameIndex() != null){
			e.addElement("nameIndex").setText(inputFieldsCoordinates[NAME] + "");
		}
		
		if (getDescriptionIndex() != null){
			e.addElement("descriptionIndex").setText(inputFieldsCoordinates[DESCRIPTION] + "");
		}
		
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunKmlOutput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		for(Transformation t : outputs){
			t.refreshDescriptor();
		}
		
	}

	public Transformation copy() {
		
		return null;
	}

	public String getDefinition() {
		return definition;
	}

	public Server getServer() {
		if (server == null){
			return FileSystemServer.getInstance();
		}
		return server;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
		
	}

	public void setServer(Server s) {
		if (s instanceof FileSystemServer){
			this.server = s;
		}
		
	}
	
	public void setServer(String serverName) {
		this.server = ResourceManager.getInstance().getServer(serverName);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition()+ "\n");
		buf.append("Writed Object : " + kmlObjectType.name() + "\n");
		buf.append("Deleted First : " + deleteFirst + "\n");
		buf.append("Root Path : " + rootPath + "\n");
		try{
			buf.append("Altitude Field : " + inputs.get(0).getDescriptor(this).getColumnName(getCoordinateAltitudeIndex())+ "\n");
		}catch(Exception ex){
			buf.append("Altitude Field : " + getCoordinateAltitudeIndex()+ "\n");
		}
		
		try{
			buf.append("Longitude Field : " + inputs.get(0).getDescriptor(this).getColumnName(getCoordinateLongitudeIndex())+ "\n");
		}catch(Exception ex){
			buf.append("Longitude Field : " + getCoordinateLongitudeIndex()+ "\n");
		}
		try{
			buf.append("Latitude Field : " + inputs.get(0).getDescriptor(this).getColumnName(getCoordinateLatitudeIndex())+ "\n");
		}catch(Exception ex){
			buf.append("Latitude Field : " + getCoordinateLatitudeIndex()+ "\n");
		}
		try{
			buf.append("Description Field : " + inputs.get(0).getDescriptor(this).getColumnName(getDescriptionIndex())+ "\n");
		}catch(Exception ex){
			buf.append("Description Field : " + getDescriptionIndex()+ "\n");
		}
		try{
			buf.append("Identifier Field : " + inputs.get(0).getDescriptor(this).getColumnName(getNameIndex())+ "\n");
		}catch(Exception ex){
			buf.append("Identifier Field : " + getNameIndex()+ "\n");
		}
		
		return buf.toString();
	}
}
