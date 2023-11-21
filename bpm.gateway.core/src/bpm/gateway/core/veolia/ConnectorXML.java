package bpm.gateway.core.veolia;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.mdm.IMdmContractInput;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;

public abstract class ConnectorXML extends AbstractTransformation implements DataStream, IMdmContractInput {

	private DataBaseServer server;
	private Server fileServer = FileSystemServer.getInstance();
	
	private String filePath;
	private String encoding = "UTF-8";
	
	//Use for MDM
	private boolean useMdm;

	private Contract selectedContract;
	
	private int supplierId;
	private int contractId = -1;

	private boolean isInput = true;
	private boolean isODS = false;

	private String beginDate;
	private String endDate;
	private String query;
	private String query2;

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return new DefaultStreamDescriptor();
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
	public void refreshDescriptor() {
		if (!isInited()) {
			return;
		}
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

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void setServer(Server server) {
		this.server = (DataBaseServer) server;
	}

	public final void setServer(String serverName) {
		if (owner != null && owner.getResourceManager() != null && owner.getResourceManager().getServer(serverName) != null) {
			server = (DataBaseServer) owner.getResourceManager().getServer(serverName);
		}
		else {
			server = (DataBaseServer) ResourceManager.getInstance().getServer(serverName);
		}
	}

	@Override
	public String getDefinition() {
		return useMdm ? (selectedContract != null ? selectedContract.getId()  + "" : contractId + "") : filePath;
	}

	@Override
	public void setDefinition(String definition) {
		this.filePath = definition;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
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
	
	@Override
	public Integer getContractId() {
		return contractId;
	}

	public boolean isInput() {
		return isInput;
	}

	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}

	public void setInput(String isInput) {
		this.isInput = Boolean.parseBoolean(isInput);
	}
	
	public boolean isODS() {
		return isODS;
	}
	
	public void setODS(boolean isODS) {
		this.isODS = isODS;
	}
	
	public void setODS(String isODS) {
		this.isODS = Boolean.parseBoolean(isODS);
	}
	
	public String getBeginDate() {
		return beginDate;
	}
	
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getQuery2() {
		return query2;
	}
	
	public void setQuery2(String query2) {
		this.query2 = query2;
	}

	public boolean isDelete() {
		return false;
	}

	@Override
	public void setFileTransfo(AbstractTransformation fileTransfo) {
		//Not used
	}
}
