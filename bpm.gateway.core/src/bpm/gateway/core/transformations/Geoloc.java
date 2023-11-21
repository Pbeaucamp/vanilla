package bpm.gateway.core.transformations;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.tools.APIBanField;
import bpm.gateway.core.tools.AdresseGeoLocHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.tools.RuntimeGeoloc;
import bpm.vanilla.platform.core.IRepositoryContext;

public class Geoloc extends AbstractTransformation {

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	private boolean onlyOneColumnAdress;
	private Integer libelleIndex;
	private Integer postalCodeIndex;
	private Integer score;
	
	private boolean onlyOneColumnOutput = false;
	private String firstColunmName = "Latitude";
	private String secondColunmName = "Longitude";
	
	private List<String> fields = new ArrayList<String>();

	// Put later
	// private Transformation trashOutput;
	// private String trashName;

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("geoloc");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		e.addElement("onlyOneColumnAdress").setText("" + isOnlyOneColumnAdress());
		if (getLibelleIndex() != null) {
			e.addElement("libelleIndex").setText("" + getLibelleIndex());
		}
		if (getPostalCodeIndex() != null) {
			e.addElement("postalCodeIndex").setText("" + getPostalCodeIndex());
		}
		if (getScore() != null) {
			e.addElement("score").setText("" + getScore());
		}
		
		e.addElement("onlyOneColumnOutput").setText("" + isOnlyOneColumnOutput());
		e.addElement("firstColunmName").setText(getFirstColunmName());
		e.addElement("secondColunmName").setText(getSecondColunmName());
//		e.addElement("scoreColumnName").setText(getScoreColumnName());

//		Element e_a = e.addElement("fields");
		if (fields != null) {
			for(String item : fields){
				e.addElement("field").setText(item);
			}
		}

		// if (getTrashTransformation() != null) {
		// e.addElement("trashOuput").setText(trashOutput.getName());
		// }

		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RuntimeGeoloc(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		descriptor = new DefaultStreamDescriptor();
		if (!isInited()) {
			return;
		}
		try {
			for (Transformation t : getInputs()) {
				for (StreamElement e : t.getDescriptor(this).getStreamElements()) {
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}
		} catch (Exception e) {

		}

		if (inputs.size() > 0) {
			StreamElement e = new StreamElement();
//			e.name = getScoreColumnName();
//			e.transfoName = getName();
//			e.originTransfo = getName();
//			e.type = java.sql.Types.FLOAT;
//			e.typeName = "Float";
//			e.className = Double.class.getName();
//
//			descriptor.addColumn(e);

			e = new StreamElement();
			e.name = getFirstColunmName();
			e.transfoName = getName();
			e.originTransfo = getName();
			if (isOnlyOneColumnOutput()) {
				e.type = java.sql.Types.VARCHAR;
				e.className = String.class.getName();
			}
			else {
				e.type = java.sql.Types.FLOAT;
				e.typeName = "Float";
				e.className = Double.class.getName();
			}

			descriptor.addColumn(e);

			if (!isOnlyOneColumnOutput()) {
				e = new StreamElement();
				e.name = getSecondColunmName();
				e.transfoName = getName();
				e.originTransfo = getName();
				e.type = java.sql.Types.FLOAT;
				e.typeName = "Float";
				e.className = Double.class.getName();
	
				descriptor.addColumn(e);
			}
			
			if (fields != null) {
				for (String item : fields) {
					APIBanField field = AdresseGeoLocHelper.getField(item);
					
					if (field != null) {
						e = AdresseGeoLocHelper.buildColumn(getName(), field);
						descriptor.addColumn(e);
					}
				}
			}
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}
	}

	public boolean isOnlyOneColumnAdress() {
		return onlyOneColumnAdress;
	}

	public void setOnlyOneColumnAdress(String onlyOneColumnAdress) {
		try {
			this.onlyOneColumnAdress = Boolean.parseBoolean(onlyOneColumnAdress);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setOnlyOneColumnAdress(boolean onlyOneColumnAdress) {
		this.onlyOneColumnAdress = onlyOneColumnAdress;
	}

	public Integer getLibelleIndex() {
		return libelleIndex;
	}

	public void setLibelleIndex(String fieldValue) {
		try {
			libelleIndex = Integer.parseInt(fieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setLibelleIndex(StreamElement e) {
		if (e == null) {
			libelleIndex = null;
		}

		int i = descriptor.getStreamElements().indexOf(e);
		if (i == -1) {
			libelleIndex = null;
		}
		else {
			libelleIndex = i;
		}
	}

	public Integer getPostalCodeIndex() {
		return postalCodeIndex;
	}

	public void setPostalCodeIndex(String fieldValue) {
		try {
			postalCodeIndex = Integer.parseInt(fieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setPostalCodeIndex(StreamElement e) {
		int i = descriptor.getStreamElements().indexOf(e);
		if (i == -1) {
			postalCodeIndex = null;
		}
		else {
			postalCodeIndex = i;
		}
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public void setScore(String score) {
		try {
			this.score = Integer.parseInt(score);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean isOnlyOneColumnOutput() {
		return onlyOneColumnOutput;
	}
	
	public void setOnlyOneColumnOutput(String onlyOneColumnOutput) {
		try {
			this.onlyOneColumnOutput = Boolean.parseBoolean(onlyOneColumnOutput);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void setOnlyOneColumnOutput(boolean onlyOneColumnOutput) {
		this.onlyOneColumnOutput = onlyOneColumnOutput;
	}

	public String getFirstColunmName() {
		return firstColunmName;
	}
	
	public void setFirstColunmName(String firstColunmName) {
		this.firstColunmName = firstColunmName;
	}
	
	public String getSecondColunmName() {
		return secondColunmName;
	}
	
	public void setSecondColunmName(String secondColunmName) {
		this.secondColunmName = secondColunmName;
	}
	
	public void addField(String newField){
		if (fields == null) {
			this.fields = new ArrayList<String>();
		}
		
		for(String field : fields){
			if (newField.equals(field)){
				this.fields.remove(field);
				break;
			}
		}
		this.fields.add(newField);
		
		refreshDescriptor();
		fireChangedProperty();
	}

	public void removeField(String oldField){
		boolean b = false;
		if (fields != null) {
			for(String field : fields){
				if (oldField.equals(field)){
					this.fields.remove(field);
					b = true;
					break;
				}
			}
		}
		
		if (b) {
			refreshDescriptor();
			fireChangedProperty();
		}
	}

	public boolean isFieldSelected(StreamElement element) {
		if (fields != null) {
			for(String field : this.fields){
				if (element.name != null && element.name.equals(field)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public List<String> getFields() {
		return new ArrayList<String>(fields);
	}
	
	// public Transformation getTrashTransformation() {
	// if (trashName != null && getDocument() != null) {
	// trashOutput = getDocument().getTransformation(trashName);
	// trashName = null;
	// }
	// return trashOutput;
	// }
	//
	// public void setTrashTransformation(Transformation transfo) {
	// trashOutput = transfo;
	// }
	//
	// public void setTrashTransformation(String name) {
	// this.trashName = name;
	// }

	public Transformation copy() {
		Geoloc copy = new Geoloc();

		copy.setDescription(description);
		copy.setName("copy of " + name);

		return copy;
	}

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)) {
			throw new Exception("The Geoloc transformation can only have one Input");
		}
		boolean b = super.addInput(stream);

		if (b) {
			refreshDescriptor();
		}
		return b;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();

		return buf.toString();
	}
}
