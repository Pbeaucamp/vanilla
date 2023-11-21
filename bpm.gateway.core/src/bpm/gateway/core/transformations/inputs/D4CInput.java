package bpm.gateway.core.transformations.inputs;

import java.awt.Point;
import java.util.List;

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
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunCsvInput;
import bpm.gateway.runtime2.transformations.inputs.RunXlsInput;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.utils.D4CHelper;

public class D4CInput extends AbstractTransformation implements DataStream {

	private AbstractTransformation fileTransfo;
	
	private D4CServer server;

	private CkanPackage selectedPackage;
	private CkanResource selectedResource;
	
	private String packageName;
	private String resourceId;

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
			D4CHelper helper = getD4CHelper();
			if (helper != null && selectedPackage == null && packageName != null && !packageName.isEmpty() && resourceId != null && !resourceId.isEmpty()) {
				this.selectedPackage = helper.findCkanPackage(packageName);
				
				if (this.selectedPackage != null) {
					for (CkanResource resource : this.selectedPackage.getResources()) {
						if (resource.getId().equals(resourceId)) {
							this.selectedResource = resource;
							break;
						}
					}
				}
				
				if (fileTransfo != null) {
					refreshDescriptor();
				}
			}
			super.initDescriptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public D4CHelper getD4CHelper() {
		if (server != null) {
			return ((D4CServer) server).getD4CHelper();
		}
		return null;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("d4cinput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		if (packageName != null) {
			e.addElement("packageName").setText(packageName);
		}
		if (resourceId != null) {
			e.addElement("resourceId").setText(resourceId);
		}

		if (fileTransfo != null) {
			e.add(fileTransfo.getElement());
		}

		return e;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void setServer(Server server) {
		this.server = (D4CServer) server;
	}
	
	public final void setServer(String serverName) {
		server = (D4CServer) ResourceManager.getInstance().getServer(serverName);
	}

	@Override
	public void setDefinition(String definition) { }

	@Override
	public String getDefinition() {
		return null;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		if (fileTransfo instanceof FileInputCSV) {
			return new RunCsvInput(repositoryCtx, this, bufferSize);
		}
		else if (fileTransfo instanceof FileInputXLS) {
			return new RunXlsInput(repositoryCtx, this, bufferSize);
		}

		return null;
	}

	@Override
	public void refreshDescriptor() {
		if (selectedResource != null && fileTransfo != null) {
			String ext = selectedResource.getFormat();
			if (ext.equalsIgnoreCase("csv")) {
				try {
					FileCSVHelper.createStreamDescriptor(this, 100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
				try {
					FileXLSHelper.createStreamDescriptor(this);
				} catch (Exception e) {
					e.printStackTrace();
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
	
	public CkanPackage getSelectedPackage() {
		return selectedPackage;
	}
	
	public void setSelectedPackage(CkanPackage selectedPackage) {
		this.selectedPackage = selectedPackage;
		setPackageName(selectedPackage != null ? selectedPackage.getName() : null);
	}

	public CkanResource getSelectedResource() {
		return selectedResource;
	}

	public void setSelectedResource(CkanResource resource) {
		this.selectedResource = resource;
		setResourceId(resource != null ? resource.getId() : null);
		refreshDescriptor();
	}

	public void setFileTransfo(AbstractTransformation fileTransfo) {
		fileTransfo.setName(this.getName() + "_file");
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
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
