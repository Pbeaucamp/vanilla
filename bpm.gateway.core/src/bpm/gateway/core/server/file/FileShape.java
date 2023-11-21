package bpm.gateway.core.server.file;

import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.transformations.mdm.IMdmContractInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * Abstract File for SHAPE Files
 * @author SVI
 *
 */
public abstract class FileShape extends AbstractTransformation implements DataStream, IMdmContractInput {

	private Server server = FileSystemServer.getInstance();
	private String filePath;
	protected DefaultStreamDescriptor descriptor = new DefaultStreamDescriptor();

	// Use for MDM
	private boolean useMdm;

	private Contract selectedContract;

	private int supplierId;
	private int contractId = -1;
	
	@Override
	public String getDefinition() {
		return useMdm ? (selectedContract != null ? selectedContract.getId() + "" : contractId + "") : filePath;
	}

	@Override
	public void setDefinition(String definition) {
		this.filePath = definition;
	}

	@Override
	public Server getServer() {
		if (server == null){
			return FileSystemServer.getInstance();
		}
		return server;
	}

	@Override
	public void setServer(Server s) {
		this.server = s;
	}

	/**
	 * Used by the digester to retrieve the server
	 * @param serverName
	 */
	public void setServer(String serverName){
		for(Server s : ResourceManager.getInstance().getServers(AbstractFileServer.class)){
			if (s.getName().equals(serverName)){
				setServer(s);
				return;
			}
		}
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

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	protected void setDescriptor(DefaultStreamDescriptor desc){
		this.descriptor = desc;
//		refreshDescriptor();
		this.fireChangedProperty();
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

	public abstract Element getElement();

	public abstract RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize);

}
