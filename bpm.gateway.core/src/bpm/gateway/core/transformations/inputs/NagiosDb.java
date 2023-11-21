package bpm.gateway.core.transformations.inputs;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DataStream;
import bpm.gateway.core.Server;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunDataBaseInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class NagiosDb extends AbstractTransformation implements DataStream {
	
	public static final String[] CATEGORY_NAME = {
		"Comments",
		"Contacts",
		"Groups",
		"Contacts and Groups",
		"Flapping History"
	};
	
	public static final String[] CATEGORY_REQUEST = {
		"SELECT commenthistory_id, comment_time, author_name, comment_data, deletion_time, instance_name, instance_description, name1, name2, objecttype_id, is_active FROM nagios_commenthistory his, nagios_objects obj, nagios_instances ins WHERE his.instance_id = ins.instance_id AND his.object_id = obj.object_id;",
		"SELECT contact_id, alias, email_address, name1, name2, is_active FROM nagios_contacts cont, nagios_objects obj WHERE cont.contact_object_id = obj.object_id",
		"SELECT contactgroup_id, alias, name1, name2, instance_name, instance_description FROM nagios_contactgroups gr, nagios_instances ins, nagios_objects obj WHERE gr.instance_id = ins.instance_id AND gr.contactgroup_object_id = obj.object_id",
		"SELECT nagios_contacts.contact_id, nagios_contacts.alias, nagios_contacts.email_address, nagios_objects.name1, nagios_objects.name2, nagios_objects.is_active, nagios_contactgroups.alias FROM nagios_contacts left join nagios_contactgroup_members on nagios_contacts.contact_id = nagios_contactgroup_members.contactgroup_member_id left join nagios_contactgroups on nagios_contactgroup_members.contactgroup_id = nagios_contactgroups.contactgroup_id inner join nagios_objects on nagios_contacts.contact_object_id = nagios_objects.object_id",
		"SELECT flappinghistory_id, event_time, percent_state_change, name1, name2, instance_name, instance_description FROM nagios_flappinghistory fla, nagios_instances ins, nagios_objects obj WHERE fla.instance_id = ins.instance_id AND fla.object_id = obj.object_id"
	};
	
	private NagiosRequest request;

	private DataBaseServer server;
	
	private String definitionSql = "";
	private StreamDescriptor descriptor;

	//those field are use to avoid refreshing 
	//the descriptor 
	transient private String lastLoaderSql;
	transient private Server lastLoadedServer;
	
	private List<NagiosRequest> requests;
	
	public NagiosDb(){
		this.requests = buildNagiosRequest();
	}

	public NagiosRequest getNagiosRequest() {
		return request;
	}

	public void setNagiosRequest(NagiosRequest request) {
		this.request = request;
		if(request != null){
			setDefinition(request.getRequest());
		}
		else {
			setDefinition(null);
		}
	}

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("nagiosDb");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		if (getContainer() != null){
			e.addElement("container-ref").setText(getContainer());
		}
		
		
		if (server != null){
			e.addElement("serverRef").setText(server.getName());
		}
		
		Element outputVars = e.addElement("outputVariables");
		for(Variable v : getOututVariables().keySet()){
			Integer el = getOututVariables().get(v);
			if (el != null && descriptor != null && el < descriptor.getColumnCount()){
				Element g = outputVars.addElement("variableInit");
				g.addElement("variableName").setText(v.getName());
				g.addElement("fieldIndex").setText(el.intValue() + "");
				
			}
		}
		
		if (request != null){
			e.addElement("request").setText(request.getName());
		}
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunDataBaseInput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		if (definitionSql != lastLoaderSql || server != lastLoadedServer){
			try {
				descriptor = DataBaseHelper.getDescriptor(this);
				lastLoaderSql = definitionSql;
				lastLoadedServer = server;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for(Transformation  t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	@Override
	public String getDefinition() {
		return definitionSql;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public void setDefinition(String definition) {
		this.definitionSql = definition;
		try {
			refreshDescriptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setServer(Server server) {
		this.server = (DataBaseServer)server;
	}
	
	public void setServer(String serverName) {
		this.server = (DataBaseServer)ResourceManager.getInstance().getServer(serverName);
	}
	
	public void setRequest(String requestName){
		this.request = findRequest(requestName);
		if(request != null){
			this.definitionSql = request.getRequest();
		}
	}
	
	public NagiosRequest getRequest(){
		return request;
	}
	
	public  void setName(String name){
		if (name.equals(getName())){
			return;
		}
		super.setName(name);
		
		if (definitionSql == null || definitionSql.equals("")){
			return;
		}
		else{
			for(Transformation t : outputs){
				t.refreshDescriptor();
			}
		}
	}

	@Override
	public Transformation copy() {
		NagiosDb copy = new NagiosDb();
		copy.setServer(server);
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		
		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("SqlServer : \n" + server.getName()+ "\n");
		buf.append("Sql : \n" + definitionSql+ "\n\n");
		return buf.toString();
	}
	
	private NagiosRequest findRequest(String requestName){
		if(requests != null){
			for(NagiosRequest req : requests){
				if(req.getName().equals(requestName)){
					return req;
				}
			}
		}
		return null;
	}
	
	public static List<NagiosRequest> buildNagiosRequest(){
		List<NagiosRequest> requests = new ArrayList<NagiosRequest>();
		
		for(int i=0; i<NagiosDb.CATEGORY_NAME.length; i++){
			NagiosRequest request = new NagiosRequest();
			request.setName(NagiosDb.CATEGORY_NAME[i]);
			request.setRequest(NagiosDb.CATEGORY_REQUEST[i]);
			requests.add(request);
		}
		
		return requests;
	}
}
