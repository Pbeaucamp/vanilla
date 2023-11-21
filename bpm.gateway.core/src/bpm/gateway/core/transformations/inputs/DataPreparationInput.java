package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.vanilla.DataPreparationHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunDataPreparationInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class DataPreparationInput extends AbstractTransformation {

	private AbstractTransformation fileTransfo;
	private DataPreparation selectedDataPrep;
	private DefaultStreamDescriptor descriptor;
	private String dataprepid;

	@Override
	public Transformation copy() {
		return null;
	}

	@Override
	public String getAutoDocumentationDetails() {
		return null;
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public void initDescriptor() {
		// refreshDescriptor();
		// super.initDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("datapreparationinput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (selectedDataPrep != null) {
			e.addElement("dataPrepId").setText(selectedDataPrep.getId() + "");
		}

		if (fileTransfo != null) {
			e.add(fileTransfo.getElement());
		}

		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunDataPreparationInput(this, bufferSize, selectedDataPrep);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			try {
				descriptor = DataPreparationHelper.createStreamDescriptor(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}
	}

	public DataPreparation getSelectedDataPrep() {
		return selectedDataPrep;
	}

	public void setSelectedDataPrep(DataPreparation selectedDataPrep) {
		this.selectedDataPrep = selectedDataPrep;
		refreshDescriptor();
	}

	public Transformation getFileTransfo() {
		return this.fileTransfo;
	}

	public void setDataPrepId(String id) {
		dataprepid = id;
	}

	public String getDataprepid() {
		return dataprepid;
	}
}
