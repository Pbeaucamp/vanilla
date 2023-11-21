package bpm.gateway.core.transformations.inputs;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.Trashable;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunCsvInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileInputCSV extends FileCSV implements Trashable {
	private boolean skipFirstRow = true;
	/**
	 * step used as trash to collect rows that have not been inserted for some
	 * reason
	 */
	private Transformation errorHandlerTransformation;
	private String trashName;

	public Element getElement() {
		Element e = DocumentHelper.createElement("fileInputCSV");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("skipFirstRow").setText(skipFirstRow + "");
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}
		
		e.addElement("fromUrl").setText(String.valueOf(fromUrl));

		e.addElement("separator").setText(getSeparator() + "");
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("encoding").setText(getEncoding());

		if (descriptor != null) {
			e.add(descriptor.getElement());
		}
		if (getTrashTransformation() != null) {
			e.addElement("trashOuput").setText(errorHandlerTransformation.getName());
		}
		
		if (isJson()) {
			e.addElement("isJson").setText(isJson() + "");
			e.addElement("jsonRootItem").setText(getJsonRootItem());
			e.addElement("jsonDepth").setText(getJsonDepth() + "");
		}
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunCsvInput(null, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}

	}

	@Override
	public void initDescriptor() {
		try {
			FileCSVHelper.createStreamDescriptor(this, 100);

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.initDescriptor();
	}

	public Transformation copy() {
		FileInputCSV copy = new FileInputCSV();

		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());
		copy.setSkipFirstRow(skipFirstRow);
		copy.setFromUrl(fromUrl);
		copy.setSeparator(getSeparator());
		copy.setJson(isJson());
		copy.setJsonRootItem(getJsonRootItem());
		copy.setJsonDepth(getJsonDepth());

		return copy;
	}

	public boolean isSkipFirstRow() {
		return skipFirstRow;
	}

	public void setSkipFirstRow(boolean skipFirstRow) {
		this.skipFirstRow = skipFirstRow;
	}

	public void setSkipFirstRow(String skipFirstRow) {
		this.skipFirstRow = Boolean.parseBoolean(skipFirstRow);
	}

	public Transformation getTrashTransformation() {
		if (trashName != null && getDocument() != null) {
			errorHandlerTransformation = getDocument().getTransformation(trashName);
			trashName = null;
		}
		return errorHandlerTransformation;
	}

	public void setTrashTransformation(Transformation transfo) {
		this.errorHandlerTransformation = transfo;

	}

	public void setTrashTransformation(String name) {
		this.trashName = name;
	}

	@Override
	public void addOutput(Transformation stream) {
		super.addOutput(stream);
		if (outputs.contains(stream)) {
			if (trashName != null && trashName.equals(stream.getName())) {
				setTrashTransformation(stream);
				trashName = null;
			}
		}
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");
		buf.append("SkipFirstRow : " + isSkipFirstRow() + "\n");
		buf.append("Encoding: " + getEncoding() + "\n");
		buf.append("From Url: " + fromUrl + "\n");

		if (getTrashTransformation() != null) {
			buf.append("ErrorHandler: " + getTrashTransformation().getName() + "\n");
		}
		return buf.toString();
	}

	public void setFromUrl(boolean fromUrl) {
		this.fromUrl = fromUrl;
	}

	public void setFromUrl(String fromUrl) {
		this.fromUrl = Boolean.parseBoolean(fromUrl);
	}

	public boolean isFromUrl() {
		return fromUrl;
	}
	
	public boolean isJson() {
		return isJson;
	}
	
	public void setJson(boolean isJson) {
		this.isJson = isJson;
	}

	public void setIsJson(String isJson) {
		this.isJson = Boolean.parseBoolean(isJson);
	}
	
	public String getJsonRootItem() {
		return jsonRootItem;
	}
	
	public void setJsonRootItem(String jsonRootItem) {
		this.jsonRootItem = jsonRootItem;
	}
	
	public int getJsonDepth() {
		return jsonDepth;
	}
	
	public void setJsonDepth(int jsonDepth) {
		this.jsonDepth = jsonDepth;
	}
	
	public void setJsonDepth(String jsonDepth) {
		this.jsonDepth = Integer.parseInt(jsonDepth);
	}
}
