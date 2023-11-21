package bpm.gateway.core.transformations.mdm;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamElement;
import bpm.mdm.model.supplier.Contract;

public interface IMdmContract {
	
	public boolean useMdm();
	
	public void setUseMdm(boolean useMdm);

	public void setSupplierId(String id);

	public void setContractId(String id);
	
	public void setContract(Contract contract);
	
	public Contract getSelectedContract();
	
	public Integer getSupplierId();
	
	public Integer getContractId();
	
	public void setFileTransfo(AbstractTransformation fileTransfo);
	
	public void initDescriptor();

	public Server getFileServer();
	
	public void setFileServer(Server fileServer);
}
