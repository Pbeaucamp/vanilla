package bpm.gateway.core.transformations.outputs;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.mdm.IMdmContract;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.outputs.RunXLSOutput;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryContext;

public class FileOutputXLS extends FileXLS implements IMdmContract {

	private Server fileServer = FileSystemServer.getInstance();

	private boolean append = false;
	private boolean includeHeader = true;

	private boolean delete = false;

	// Use for MDM
	private boolean useMdm;

	private Contract selectedContract;

	private int supplierId;
	private int contractId = -1;

	public boolean addInput(Transformation stream) throws Exception {
		boolean result = super.addInput(stream);
		if (result == false) {
			return result;
		}

		StreamDescriptor desc = stream.getDescriptor(this);
		for (StreamElement e : desc.getStreamElements()) {
			descriptor.addColumn(e.clone(getName(), stream.getName()));
		}
		return result;
	}

	public Element getElement() {
		Element e = DocumentHelper.createElement("fileOutputXLS");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("delete").setText("" + delete);
		e.addElement("sheetName").setText("" + getSheetName());

		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}
		e.addElement("encoding").setText(getEncoding());
		e.addElement("append").setText(append + "");
		e.addElement("includeHeader").setText(includeHeader + "");
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		e.addElement("useMdm").setText(useMdm() + "");

		if (getSelectedContract() != null) {
			e.addElement("supplierId").setText(getSelectedContract().getParent().getId() + "");
		}
		if (getSelectedContract() != null) {
			e.addElement("contractId").setText(getSelectedContract().getId() + "");
		}

		if (descriptor != null) {
			e.add(descriptor.getElement());
		}

		return e;
	}
	
	@Override
	public void initDescriptor() {
		try {
			if (selectedContract == null && contractId > -1) {
				LOOK: for (Supplier sup : getDocument().getMdmHelper().getMdmSuppliers()) {
					if (sup.getId() == supplierId) {
						for (Contract c : sup.getContracts()) {
							if (c.getId() == contractId) {
								selectedContract = c;
								break LOOK;
							}
						}
					}
				}
			}
			if (useMdm) {
				setFileServer(new MdmFileServer("mdmserver", "", getDocument().getRepositoryContext().getVanillaContext().getVanillaUrl(), getDocument().getRepositoryContext().getVanillaContext().getLogin(), getDocument().getRepositoryContext().getVanillaContext().getPassword(), getDocument().getRepositoryContext().getRepository().getId() + ""));
			}
			super.initDescriptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunXLSOutput(this, bufferSize);
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

	public void removeOutput(Transformation transfo) {

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
			} catch (NumberFormatException e) {

				e.printStackTrace();
			} catch (ServerException e) {

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
		FileOutputXLS copy = new FileOutputXLS();

		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());
		copy.setFileServer(getFileServer());
		copy.setContract(getSelectedContract());
		copy.setDelete(getDelete());
		copy.setSheetName(getSheetName());
		copy.setSupplierId(getSupplierId());

		return copy;
	}

	@Override
	public String getDefinition() {
		return useMdm ? (selectedContract != null ? selectedContract.getId()  + "" : contractId + "") : super.getDefinition();
	}

	public Server getFileServer() {
		if (fileServer == null || !useMdm) {
			return FileSystemServer.getInstance();
		}
		return fileServer;
	}
	
	public void setFileServer(Server fileServer) {
		this.fileServer = fileServer;
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public void setAppend(String append) {
		this.append = Boolean.parseBoolean(append);
	}

	public boolean isIncludeHeader() {
		return includeHeader;
	}

	public void setIncludeHeader(String includeHeader) {
		this.append = Boolean.parseBoolean(includeHeader);
	}

	public void setIncludeHeader(boolean includeHeader) {
		this.includeHeader = includeHeader;
	}

	public boolean getDelete() {
		return delete;
	}

	public void setDelete(boolean selection) {
		this.delete = selection;
	}

	public void setDelete(String selection) {
		this.delete = Boolean.parseBoolean(selection);
	}
	
	public boolean useMdm() {
		return useMdm;
	}
	
	public void setUseMdm(boolean useMdm) {
		this.useMdm = useMdm;
	}
	
	public void setUseMdm(String useMdm) {
		this.useMdm = Boolean.valueOf(useMdm);
	}

	public void setContract(Contract contract) {
		this.selectedContract = contract;
		if (contract != null) {
			setContractId(contract.getId());
		}
	}

	public Contract getSelectedContract() {
		return selectedContract;
	}
	
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int id) {
		this.supplierId = id;
	}

	public void setSupplierId(String id) {
		supplierId = Integer.parseInt(id);
	}
	
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	public void setContractId(String id) {
		contractId = Integer.parseInt(id);
	}
	
	@Override
	public Integer getContractId() {
		return contractId;
	}

	@Override
	public void setFileTransfo(AbstractTransformation fileTransfo) { }

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");
		buf.append("Write header : " + includeHeader + "\n");
		buf.append("Encoding : " + getEncoding() + "\n");
		buf.append("Append : " + isAppend() + "\n");
		buf.append("Delete First : " + getDelete() + "\n");
		buf.append("SheetName : " + getSheetName() + "\n");

		return buf.toString();
	}
}
