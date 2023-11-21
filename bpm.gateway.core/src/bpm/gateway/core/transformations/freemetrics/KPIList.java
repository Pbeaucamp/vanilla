package bpm.gateway.core.transformations.freemetrics;

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
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.kpi.RunKPIList;
import bpm.vanilla.platform.core.IRepositoryContext;

public class KPIList extends AbstractTransformation implements FreemetricsKPI{

	private DefaultStreamDescriptor descriptor ;
	
	private FreemetricServer server;
	private List<Integer> listSelectedAssocId = new ArrayList<Integer>();

	private String metricNameFieldName = "metricName";
	private String applicationNameFieldName = "applicationName";
	private String associationIdFieldName = "associationId";
	
	@Override
	public boolean addInput(Transformation stream) throws Exception {
		
		throw new Exception("KPI List cannot have Input");
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		if (descriptor == null){
			descriptor = new DefaultStreamDescriptor();
			
			
			StreamElement met = new StreamElement();
			met.className = "java.lang.String";
			met.transfoName = getName();
			met.originTransfo = getName();
			met.name = getMetricNameFieldName();
			
			descriptor.addColumn(met);
			
			StreamElement app = new StreamElement();
			app.className = "java.lang.String";
			app.transfoName = getName();
			app.originTransfo = getName();
			app.name = getApplicationNameFieldName();
			
			descriptor.addColumn(app);
			
			StreamElement assocId = new StreamElement();
			assocId.className = "java.lang.Integer";
			assocId.transfoName = getName();
			assocId.originTransfo = getName();
			assocId.name = getAssociationIdFieldName();
			
			descriptor.addColumn(assocId);
			
			
		}
		return descriptor;
	}
	public final String getAssociationIdFieldName() {
		return associationIdFieldName;
	}

	public final void setAssociationIdFieldName(String associationIdFieldName) {
		this.associationIdFieldName = associationIdFieldName;
		refreshDescriptor();
	}
	
	
	public final String getMetricNameFieldName() {
		return metricNameFieldName;
	}

	public final void setMetricNameFieldName(String metricNameFieldName) {
		this.metricNameFieldName = metricNameFieldName;
		refreshDescriptor();
	}

	public final String getApplicationNameFieldName() {
		return applicationNameFieldName;
	}

	public final void setApplicationNameFieldName(String applicationNameFieldName) {
		this.applicationNameFieldName = applicationNameFieldName;
		refreshDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("kpiList");
		
		
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
	
		
		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");

		if (metricNameFieldName != null){
			e.addElement("metricNameFieldName").setText(metricNameFieldName);
		}
		
		if (applicationNameFieldName != null){
			e.addElement("applicationNameFieldName").setText(applicationNameFieldName);
		}
		
		if (associationIdFieldName != null){
			e.addElement("associationIdFieldName").setText(associationIdFieldName);
		}
		
		for(Integer i : listSelectedAssocId){
			e.addElement("associationMetAppId").setText(i + "");
		}

		
		
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new KPIListRuntime(this, runtimeEngine);
//	}
	
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunKPIList(this, bufferSize);
	}
	
	
	public List<Integer> getAssocIds(){
		return listSelectedAssocId;
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		try{
			getDescriptor(this).getStreamElements().get(0).name = new String(getMetricNameFieldName());
			getDescriptor(this).getStreamElements().get(1).name = new String(getApplicationNameFieldName());
			getDescriptor(this).getStreamElements().get(2).name = new String(getAssociationIdFieldName());
			
			for(Transformation t : getOutputs()){
				t.refreshDescriptor();
			}
		}catch(Exception e){
			
		}
		
	}

	public Transformation copy() {
		KPIList copy = new KPIList();
		
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setServer(getServer());

		return copy;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = (FreemetricServer)server;
		
	}

	public void setServer(String serverName) {
		this.server = (FreemetricServer)ResourceManager.getInstance().getServer(serverName);
		
	}

	
	public void addAssociationId(String i){
		try{
			addAssociationId(Integer.parseInt(i));
		}catch(Exception e){
			
		}
	}
	
	public void addAssociationId(Integer i){
		for(Integer v : listSelectedAssocId){
			if (v.equals(i)){
				return;
			}
		}
			
		listSelectedAssocId.add(i);
	}
	
	public void removeAssociationId(Integer i){
		for(Integer v : listSelectedAssocId){
			if (v.equals(i)){
				listSelectedAssocId.remove(i);
				return;
			}
		}
		
	}

	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FreeMetrics Server : "+ getServer().getName() + "\n");
		buf.append("ApplicationFieldName : " + applicationNameFieldName + "\n");
		buf.append("AssociationIdFieldName : " + associationIdFieldName + "\n");
		buf.append("MetricFieldName : " + metricNameFieldName + "\n");
		buf.append("Selected Association Ids : \n");
		
		boolean first = true;
		for(Integer i : listSelectedAssocId){
			if (first){
				first = false;
			}
			else{
				buf.append(", ");
			}
			buf.append(i);
		}
		
		return buf.toString();
	}
}
