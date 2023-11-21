package bpm.gateway.core.transformations.mdm;

import java.awt.Point;
import java.sql.Types;
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
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunCsvInput;
import bpm.gateway.runtime2.transformations.inputs.RunKmlInput;
import bpm.gateway.runtime2.transformations.inputs.RunXlsInput;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.Meta.TypeMeta;

public class MdmContractFileInput extends AbstractTransformation implements IMdmContractInput {

	private AbstractTransformation fileTransfo;

	private Contract selectedContract;
	
	private int supplierId;
	private int contractId = -1;
	
	private List<Meta> meta = new ArrayList<Meta>();

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if (fileTransfo != null) {
			return fileTransfo.getDescriptor(this);
		}
		else {
			return new DefaultStreamDescriptor();
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
				if (fileTransfo != null) {
					if (fileTransfo instanceof FileCSV) {
						((FileCSV) fileTransfo).setServer(new MdmFileServer("mdmserver", "", getDocument().getRepositoryContext().getVanillaContext().getVanillaUrl(), getDocument().getRepositoryContext().getVanillaContext().getLogin(), getDocument().getRepositoryContext().getVanillaContext().getPassword(), getDocument().getRepositoryContext().getRepository().getId() + ""));
						refreshDescriptor();
					}
					else if (fileTransfo instanceof FileXLS) {
						((FileXLS) fileTransfo).setServer(new MdmFileServer("mdmserver", "", getDocument().getRepositoryContext().getVanillaContext().getVanillaUrl(), getDocument().getRepositoryContext().getVanillaContext().getLogin(), getDocument().getRepositoryContext().getVanillaContext().getPassword(), getDocument().getRepositoryContext().getRepository().getId() + ""));
						refreshDescriptor();
					}
					else if (fileTransfo instanceof KMLInput) {
						((KMLInput) fileTransfo).setServer(new MdmFileServer("mdmserver", "", getDocument().getRepositoryContext().getVanillaContext().getVanillaUrl(), getDocument().getRepositoryContext().getVanillaContext().getLogin(), getDocument().getRepositoryContext().getVanillaContext().getPassword(), getDocument().getRepositoryContext().getRepository().getId() + ""));
					}
				}
			}
			super.initDescriptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("mdmfileinput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (selectedContract != null) {
			e.addElement("supplierId").setText(selectedContract.getParent().getId() + "");
		}
		if (selectedContract != null) {
			e.addElement("contractId").setText(selectedContract.getId() + "");
		}

		if (fileTransfo != null) {
			e.add(fileTransfo.getElement());
		}
		
		if (meta != null) {
			
			for(Meta met : meta){
				Element e_a = e.addElement("metadata");
				e_a.addElement("metaKey").setText(met.getKey());
				e_a.addElement("metaLabel").setText(met.getLabel());
				e_a.addElement("metaType").setText(met.getType().toString());
			}
		}

		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {

		if (fileTransfo instanceof FileInputCSV) {
			return new RunCsvInput(repositoryCtx, this, bufferSize);
		}
		else if (fileTransfo instanceof FileInputXLS) {
			return new RunXlsInput(repositoryCtx, this, bufferSize);
		}
		else if (fileTransfo instanceof KMLInput) {
			return new RunKmlInput(this, bufferSize);
		}
		return null;
	}

	@Override
	public void refreshDescriptor() {
		if (selectedContract != null && fileTransfo != null) {
			if (selectedContract.getDocId() != null) {
				if (selectedContract.getFileVersions() == null) {
					try {
						selectedContract.setFileVersions(this.getDocument().getMdmHelper().getGedDocument(selectedContract));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				String ext = selectedContract.getFileVersions().getCurrentVersion(selectedContract.getVersionId()).getFormat();
				if (ext.equalsIgnoreCase("csv") || ext.equalsIgnoreCase("json") || ext.equalsIgnoreCase("geojson")) {
					try {
						FileCSVHelper.createStreamDescriptor((FileInputCSV) fileTransfo, 100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
					try {
						FileXLSHelper.createStreamDescriptor((FileInputXLS) fileTransfo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
				for(Meta met : getMeta()){
					try {
						((DefaultStreamDescriptor) getDescriptor(this)).addColumn(MdmHelper.buildColumn(getName(), met));
					} catch (ServerException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		for (Transformation t : getOutputs()) {
			t.refreshDescriptor();
		}
	}

	@Override
	public Transformation copy() {
		return null;
	}

	@Override
	public String getAutoDocumentationDetails() {
		return null;
	}

	public void setContract(Contract contract) {
		selectedContract = contract;
		refreshDescriptor();
	}

	public Contract getSelectedContract() {
		return selectedContract;
	}

	public void setFileTransfo(AbstractTransformation fileTransfo) {
		if (fileTransfo != null) {
			fileTransfo.setName(this.getName() + "_file");
		}
		this.fileTransfo = fileTransfo;
	}

	public AbstractTransformation getFileTransfo() {
		return fileTransfo;
	}

	@Override
	public void addOutput(Transformation stream) {

		if (fileTransfo != null) {
			fileTransfo.addOutput(stream);
		}

		super.addOutput(stream);
	}

	@Override
	public void addOutput(Transformation stream, List<Point> outputBendPoints) {
		if (fileTransfo != null) {
			fileTransfo.addOutput(stream);
		}
		super.addOutput(stream, outputBendPoints);
	}

	public void setSupplierId(String id) {
		supplierId = Integer.parseInt(id);
	}
	
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setContractId(String id) {
		contractId = Integer.parseInt(id);
	}
	
	public Integer getContractId() {
		return contractId;
	}


	public void addMeta(String key, String label, String type){
		Meta meta = new Meta();
		meta.setKey(key);
		meta.setLabel(label);
		meta.setType(TypeMeta.valueOf(type));
		
		addMeta(meta);
	}
	
	public void addMeta(Meta newMeta){
		if (meta == null) {
			this.meta = new ArrayList<Meta>();
		}
		
		for(Meta met : this.meta){
			if (newMeta.getKey() != null && met.getKey() != null && newMeta.getKey().equals(met.getKey())){
				this.meta.remove(met);
				break;
			}
		}
		meta.add(newMeta);
		
		refreshDescriptor();
		fireChangedProperty();
	}

	public void removeMeta(Meta met){
		if (meta == null) {
			this.meta = new ArrayList<Meta>();
		}
		
		Meta metaToRemove = null;
		for (Meta meta : meta) {
			if (meta.getKey().equals(met.getKey())) {
				metaToRemove = meta;
				break;
			}
		}
		boolean b = metaToRemove != null ? meta.remove(metaToRemove) : false;
		
		if (b) {
			refreshDescriptor();
			fireChangedProperty();
		}
	}

	public boolean isMetaSelected(StreamElement element) {
		if (meta == null) {
			this.meta = new ArrayList<Meta>();
		}
		
		for(Meta met : this.meta){
			if (element.name != null && met.getLabel() != null && element.name.equals(met.getLabel())){
				return true;
			}
		}
		
		return false;
	}
	
	public List<Meta> getMeta() {
		return new ArrayList<Meta>(meta);
	}

	@Override
	public boolean useMdm() {
		return true;
	}

	@Override
	public void setUseMdm(boolean useMdm) { }

	@Override
	public Server getFileServer() {
		return null;
	}

	@Override
	public void setFileServer(Server fileServer) { }
}
