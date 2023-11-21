package bpm.gateway.core.transformations;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.sequence.RunSequence;
import bpm.vanilla.platform.core.IRepositoryContext;

public class Sequence extends AbstractTransformation {

	private boolean isFromValue = false;
	private long minValue = 1;
	private Integer minFieldIndex;

	private long maxValue = 100000;
	private int step = 1;
	private String fieldName = "id";

	private DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	public boolean isFromValue() {
		return isFromValue;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		refreshDescriptor();
	}

	public long getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = Long.parseLong(minValue);
		this.isFromValue = true;
	}

	public long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(long maxValue) {
		this.maxValue = maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = Long.parseLong(maxValue);
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void setStep(String step) {
		this.step = Integer.parseInt(step);
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("sequence");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		// e.addElement("minValue").setText(minValue + "");
		if (getMinField() != null) {
			e.addElement("minFieldIndex").setText("" + getMinField());
		}
		e.addElement("maxValue").setText(maxValue + "");
		e.addElement("step").setText(step + "");
		e.addElement("fieldName").setText(fieldName);

		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSequence(this, bufferSize);
	}

	@Override
	public boolean addInput(Transformation stream) throws Exception {
		if (getInputs().size() > 0 && !getInputs().contains(stream)) {
			throw new Exception("The Filter Transformations can only have one Input");
		}
		boolean b = super.addInput(stream);

		if (b) {
			refreshDescriptor();
		}
		return b;
	}

	@Override
	public void refreshDescriptor() {
		try {
			descriptor = new DefaultStreamDescriptor();
			if (!isInited()) {
				return;
			}
			StreamElement col = new StreamElement();
			col.name = fieldName;
			col.className = "java.lang.Long";
			col.originTransfo = this.getName();
			col.transfoName = this.getName();
			col.typeName = "LONG";

			for (Transformation t : getInputs()) {
				for (StreamElement e : t.getDescriptor(this).getStreamElements()) {
					descriptor.addColumn(e.clone(getName(), t.getName()));
				}
			}

			descriptor.addColumn(col);

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}

	}

	public Transformation copy() {
		Sequence seq = new Sequence();
		seq.setFieldName(getFieldName());
		seq.setMaxValue(getMaxValue());
		// seq.setMinValue(getMinValue());
		seq.setMinField(getMinField());
		seq.setStep(getStep());
		seq.setName("copy of " + getName());
		return seq;
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("Generated Sequence Field Name :" + fieldName + "\n");
		buf.append("Step between two id :" + step + "\n");
		// buf.append("Minimum generated value :" + minValue + "\n");
		try {
			buf.append("Minimum generated value field : " + getInputs().get(0).getDescriptor(this).getColumnName(minFieldIndex) + "\n");
		} catch (Exception ex) {
			buf.append("Minimum generated value field index : " + minFieldIndex + "\n");
		}
		buf.append("Maximum generated value :" + maxValue + "\n");

		return buf.toString();
	}

	public void setMinField(String minFieldValue) {
		try {
			this.minFieldIndex = Integer.parseInt(minFieldValue);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setMinField(Integer minField) {
		this.minFieldIndex = minField;
	}

	public void setMinField(StreamElement e) {
		int i = descriptor.getStreamElements().indexOf(e);
		if (i == -1) {
			this.minFieldIndex = null;
		}
		else {
			this.minFieldIndex = i;
		}
	}

	public Integer getMinField() {
		return minFieldIndex;
	}
}
