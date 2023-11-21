package bpm.gateway.core.transformations.calcul;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.calculation.RunGeoDispatch;
import bpm.vanilla.platform.core.IRepositoryContext;

public class GeoDispatch extends AbstractTransformation implements Trashable {

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	private String trashName;
	private Transformation trashTransformation;
	
	//if only one column we stock both geoloc in latitude
	private boolean onlyOneColumnGeoloc;
	private Integer latitudeIndex;
	private Integer longitudeIndex;
	
	private Integer placemarkIdIndex;
	private Integer inputReferenceLatitudeIndex;
	private Integer inputReferenceLongitudeIndex;
	private String defaultPlacemarkId;

	public GeoDispatch() {
	}

	@Override
	public Transformation copy() {
		GeoDispatch copy = new GeoDispatch();

		copy.setDescription(description);
		copy.setName("copy of " + name);

		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		return "";
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("geoDispatch");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (getTrashTransformation() != null) {
			e.addElement("trashOuput").setText(trashTransformation.getName());
		}
		
		e.addElement("onlyOneColumnGeoloc").setText("" + isOnlyOneColumnGeoloc());
		if (getLatitudeIndex() != null) {
			e.addElement("latitudeIndex").setText("" + getLatitudeIndex());
		}
		if (getLongitudeIndex() != null) {
			e.addElement("longitudeIndex").setText("" + getLongitudeIndex());
		}
		
		if (getPlacemarkIdIndex() != null) {
			e.addElement("placemarkIdIndex").setText("" + getPlacemarkIdIndex());
		}
		
		if (getInputReferenceLatitudeIndex() != null) {
			e.addElement("inputReferenceLatitudeIndex").setText("" + getInputReferenceLatitudeIndex());
		}
		
		if (getInputReferenceLongitudeIndex() != null) {
			e.addElement("inputReferenceLongitudeIndex").setText("" + getInputReferenceLongitudeIndex());
		}
		
		if (getDefaultPlacemarkId() != null) {
			e.addElement("defaultPlacemarkId").setText("" + getDefaultPlacemarkId());
		}

		if (descriptor != null) {
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunGeoDispatch(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		try {
			if (!isInited()) {
				return;
			}
			for (Transformation t : getInputs()) {
				if (!(t instanceof KMLInput)) {
					for (StreamElement e : t.getDescriptor(this).getStreamElements()) {
						descriptor.addColumn(e.clone(getName(), t.getName()));
					}
				}
			}
			
			if (getPlacemarkIdIndex() != null) {
				for (Transformation t : getInputs()) {
					if (t instanceof KMLInput) {
						StreamElement e = t.getDescriptor(this).getStreamElements().get(getPlacemarkIdIndex());
						descriptor.addColumn(e.clone(getName(), t.getName()));
					}
				}
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
	}

	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null) {
			trashTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return trashTransformation;
	}

	public void setTrashTransformation(Transformation transfo) {
		this.trashTransformation = transfo;
	}

	public void setTrashTransformation(String name) {
		this.trashName = name;
	}
	
	public boolean isOnlyOneColumnGeoloc() {
		return onlyOneColumnGeoloc;
	}
	
	public void setOnlyOneColumnGeoloc(boolean onlyOneColumnGeoloc) {
		this.onlyOneColumnGeoloc = onlyOneColumnGeoloc;
	}

	public void setOnlyOneColumnGeoloc(String onlyOneColumnGeoloc) {
		try {
			this.onlyOneColumnGeoloc = Boolean.parseBoolean(onlyOneColumnGeoloc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Integer getLatitudeIndex() {
		return latitudeIndex;
	}
	
	public void setLatitudeIndex(String fieldValue) {
		try {
			latitudeIndex = Integer.parseInt(fieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setLatitudeIndex(StreamElement e) {
		if (e == null) {
			latitudeIndex = null;
		}

		int i = descriptor.getStreamElements().indexOf(e);
		if (i == -1) {
			latitudeIndex = null;
		}
		else {
			latitudeIndex = i;
		}
	}
	
	public Integer getLongitudeIndex() {
		return longitudeIndex;
	}
	
	public void setLongitudeIndex(String fieldValue) {
		try {
			longitudeIndex = Integer.parseInt(fieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setLongitudeIndex(StreamElement e) {
		if (e == null) {
			longitudeIndex = null;
		}

		int i = descriptor.getStreamElements().indexOf(e);
		if (i == -1) {
			longitudeIndex = null;
		}
		else {
			longitudeIndex = i;
		}
	}
	
	public Integer getPlacemarkIdIndex() {
		return placemarkIdIndex;
	}
	
	public void setPlacemarkIdIndex(Integer placemarkIdIndex) {
		this.placemarkIdIndex = placemarkIdIndex;
	}
	
	public void setPlacemarkIdIndex(String fieldValue) {
		try {
			placemarkIdIndex = Integer.parseInt(fieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Integer getInputReferenceLatitudeIndex() {
		return inputReferenceLatitudeIndex;
	}
	
	public void setInputReferenceLatitudeIndex(Integer inputReferenceLatitudeIndex) {
		this.inputReferenceLatitudeIndex = inputReferenceLatitudeIndex;
	}
	
	public void setInputReferenceLatitudeIndex(String fieldValue) {
		try {
			inputReferenceLatitudeIndex = Integer.parseInt(fieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Integer getInputReferenceLongitudeIndex() {
		return inputReferenceLongitudeIndex;
	}
	
	public void setInputReferenceLongitudeIndex(Integer inputReferenceLongitudeIndex) {
		this.inputReferenceLongitudeIndex = inputReferenceLongitudeIndex;
	}
	
	public void setInputReferenceLongitudeIndex(String fieldValue) {
		try {
			inputReferenceLongitudeIndex = Integer.parseInt(fieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getDefaultPlacemarkId() {
		return defaultPlacemarkId;
	}
	
	public void setDefaultPlacemarkId(String defaultPlacemarkId) {
		this.defaultPlacemarkId = defaultPlacemarkId;
	}

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		boolean b = super.addInput(stream);

		if (b) {
			refreshDescriptor();
		}
		return b;
	}
}
