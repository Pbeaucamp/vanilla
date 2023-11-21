package bpm.gateway.core.transformations.kml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.server.file.KMLHelper;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.mdm.IMdmContractInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunKmlInput;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryContext;

public class KMLInput extends AbstractTransformation implements DataStream, Kml, IMdmContractInput {

	private StreamDescriptor descriptor = new DefaultStreamDescriptor();

	private KmlObjectType kmlObjectType = KmlObjectType.Point;
	private boolean generateIdPerFoundObject = true;

	private Server server;
	private String filePath;

	// Use for MDM
	private boolean useMdm;

	private Contract selectedContract;

	private int supplierId;
	private int contractId = -1;

	public KMLInput() {
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
				setServer(new MdmFileServer("mdmserver", "", getDocument().getRepositoryContext().getVanillaContext().getVanillaUrl(), getDocument().getRepositoryContext().getVanillaContext().getLogin(), getDocument().getRepositoryContext().getVanillaContext().getPassword(), getDocument().getRepositoryContext().getRepository().getId() + ""));
			}
			super.initDescriptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the generateIdPerFoundObject
	 */
	public boolean isGenerateIdPerFoundObject() {
		return generateIdPerFoundObject;
	}

	/**
	 * @param generateIdPerFoundObject
	 *            the generateIdPerFoundObject to set
	 */
	public void setGenerateIdPerFoundObject(boolean generateIdPerFoundObject) {
		this.generateIdPerFoundObject = generateIdPerFoundObject;
	}

	/**
	 * @param generateIdPerFoundObject
	 *            the generateIdPerFoundObject to set
	 */
	public void setGenerateIdPerFoundObject(String generateIdPerFoundObject) {
		try {
			this.generateIdPerFoundObject = Boolean.parseBoolean(generateIdPerFoundObject);
		} catch (Exception ex) {

		}
	}

	public KmlObjectType getKmlObjectType() {
		return kmlObjectType;
	}

	public void setKmlObjectType(KmlObjectType kmlObjectType) {
		this.kmlObjectType = kmlObjectType;
	}

	public void setKmlObjectType(String kmlObjectTypeName) {
		try {
			this.kmlObjectType = KmlObjectType.valueOf(kmlObjectTypeName);
		} catch (Exception ex) {

		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if (!isInited()){
			setInited();
		}
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("kmlInput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		e.addElement("objectType").setText(kmlObjectType.name());
		e.addElement("generateId").setText(isGenerateIdPerFoundObject() + "");

		if (descriptor != null) {
			e.add(descriptor.getElement());
		}

		e.addElement("useMdm").setText(useMdm() + "");
		
		if (getSelectedContract() != null) {
			e.addElement("supplierId").setText(getSelectedContract().getParent().getId() + "");
		}
		if (getSelectedContract() != null) {
			e.addElement("contractId").setText(getSelectedContract().getId() + "");
		}
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunKmlInput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}

		if (selectedContract != null && selectedContract.getDocId() != null) {
			if (selectedContract.getFileVersions() == null) {
				try {
					selectedContract.setFileVersions(this.getDocument().getMdmHelper().getGedDocument(selectedContract));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String ext = selectedContract.getFileVersions().getCurrentVersion(selectedContract.getVersionId()).getFormat();
			if (ext.equalsIgnoreCase("kml")) {
				try {
					descriptor = KMLHelper.getKmlDescriptor(getName(), this);
				} catch (Exception e) {
					e.printStackTrace();
				}

				for (Transformation t : outputs) {
					t.refreshDescriptor();
				}
			}
		}
		else if (getDefinition() != null && !getDefinition().isEmpty() && !useMdm) {
			try {
				descriptor = KMLHelper.getKmlDescriptor(getName(), this);
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Transformation t : outputs) {
				t.refreshDescriptor();
			}
		}
	}

	public Transformation copy() {
		return null;
	}

	public String getDefinition() {
		return useMdm ? (selectedContract != null ? selectedContract.getId() + "" : contractId + "") : filePath;
	}

	public Server getServer() {
		if (server == null || !useMdm) {
			return FileSystemServer.getInstance();
		}
		return server;
	}

	@Override
	public void setDefinition(String definition) {
		this.filePath = definition;
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
		selectedContract = contract;
		if (contract != null) {
			setContractId(contract.getId());
		}
		refreshDescriptor();
	}

	public Contract getSelectedContract() {
		return selectedContract;
	}

	public void setSupplierId(String id) {
		supplierId = Integer.parseInt(id);
	}

	@Override
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setContractId(String id) {
		contractId = Integer.parseInt(id);
	}
	
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	@Override
	public Integer getContractId() {
		return contractId;
	}

	public void setServer(Server s) {
		if (s instanceof FileSystemServer) {
			this.server = s;
		}

	}

	public void setServer(String serverName) {
		this.server = ResourceManager.getInstance().getServer(serverName);
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");
		buf.append("Extracted Object : " + kmlObjectType.name() + "\n");
		buf.append("AutoGenerated Id : " + generateIdPerFoundObject + "\n");

		return buf.toString();
	}

	@Override
	public void setFileTransfo(AbstractTransformation fileTransfo) {
		// Not used
	}

	@Override
	public Server getFileServer() {
		return null;
	}

	@Override
	public void setFileServer(Server fileServer) {
	}
}
