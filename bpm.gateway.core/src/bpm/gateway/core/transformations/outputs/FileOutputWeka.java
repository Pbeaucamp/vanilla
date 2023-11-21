package bpm.gateway.core.transformations.outputs;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunWekaOutput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileOutputWeka extends FileCSV {

	private char separator = ',';
	private boolean delete;
	
	@Override
	public char getSeparator() {
		return separator;
	}
	
	@Override
	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public boolean addInput(Transformation stream) throws Exception {

		boolean result = super.addInput(stream);
		if (result == false) {
			return result;
		}

		if (isInited()) {
			refreshDescriptor();
		}

		return result;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("fileOutputWeka");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("delete").setText("" + delete);
		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}
		e.addElement("encoding").setText(getEncoding());
		e.addElement("separator").setText(getSeparator() + "");
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (descriptor != null) {
			e.add(descriptor.getElement());
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunWekaOutput(this, bufferSize);
	}

	public void removeInput(Transformation transfo) {

		if (!inputs.contains(transfo)) {
			return;
		}

		// we need to update the descriptor too
		int index = inputs.indexOf(transfo);

		int start = 0;

		for (int i = 0; i < index; i++) {
			try {
				start += (inputs.get(i)).getDescriptor(this).getColumnCount();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		List<Integer> toDelete = new ArrayList<Integer>();

		try {
			for (int i = start; i < start + (transfo).getDescriptor(this).getColumnCount(); i++) {
				toDelete.add(i);
			}

			int count = 0;
			for (int i = 0; i < toDelete.size(); i++) {
				descriptor.removeColumn(i - count);
				count++;
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}

		super.removeInput(transfo);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

		descriptor = new DefaultStreamDescriptor();

		for (Transformation t : inputs) {
			try {
				for (StreamElement e : t.getDescriptor(this).getStreamElements()) {
					descriptor.addColumn(getName(), e.tableName, e.name, e.type, e.className, t.getName(), e.isNullable, e.typeName, e.defaultValue, e.isPrimaryKey);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}
	}

	public void setServer(String serverName) {
		for (Server s : ResourceManager.getInstance().getServers(AbstractFileServer.class)) {
			if (s.getName().equals(serverName)) {
				setServer(s);
				return;
			}
		}
	}

	public Transformation copy() {
		FileOutputWeka copy = new FileOutputWeka();

		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());

		return copy;
	}

	public boolean getDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public void setDelete(String delete) {
		this.delete = Boolean.parseBoolean(delete);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");
		buf.append("Encoding : " + getEncoding() + "\n");
		buf.append("Delete First : " + getDelete() + "\n");

		return buf.toString();
	}
}
