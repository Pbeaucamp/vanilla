package bpm.gateway.runtime2.transformations.inputs;

import java.util.HashMap;
import java.util.List;

import javax.naming.directory.Attribute;

import org.springframework.ldap.core.DirContextOperations;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.core.transformations.inputs.LdapMultipleMembers;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunLdapMultipleMembers extends RuntimeStep{
	
	private LdapConnection sock;
	private String nodeName;
	private String attributeName;
	private boolean inputUsed = false;
	
	private HashMap<String, Integer> parameterMapping = new HashMap<String, Integer>();
	
	private String values[];
	private int current = -1;
	
	public RunLdapMultipleMembers(LdapMultipleMembers transformation, int bufferSize) {
		super(null, transformation, bufferSize);
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		LdapServer server = (LdapServer)((DataStream)getTransformation()).getServer();
		sock = (LdapConnection)server.getCurrentConnection(adapter);
		
		if (!sock.isOpened()){
			try{
				sock.connect(null);
			}catch(Exception e){
				error("unable to connect to Ldap Server", e);
				throw e;
			}
			
		}
		nodeName = ((LdapMultipleMembers)getTransformation()).getDefinition();
		attributeName = ((LdapMultipleMembers)getTransformation()).getAttributeName();
		
		
		
		
		/*
		 * check if the node name is defined by an input field
		 */
		
		if (!getTransformation().getInputs().isEmpty()){
			int currentIndex = 0;
			List<StreamElement> fields = getTransformation().getInputs().get(0).getDescriptor(getTransformation()).getStreamElements();
			while(nodeName.substring(currentIndex).contains("{$")){
				int start = nodeName.substring(currentIndex).indexOf("{$");
				int end = nodeName.substring(currentIndex).indexOf("}");
				String fieldName = nodeName.substring(start + 1, end);
				
				currentIndex = end + 1; 
				
				
				for(int i = 0; i < fields.size(); i++){
					if(("$" + fields.get(i).name).equals(fieldName)){
						parameterMapping.put("{$" + fields.get(i).name + "}", i);
						inputUsed = true;
					}
				}
			}
			
		}
		if (!inputUsed){
			Object o = sock.getLdapTemplate().lookup(nodeName);
			values = ((DirContextOperations)o).getStringAttributes(attributeName);
		}
		
		
		isInited = true;
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputUsed){
				if (inputEmpty()){
					setEnd();
				}
			}
			else{
				if (inputEmpty() && current >= values.length){
					setEnd();
				}
			}
			
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty() && !inputs.isEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(InterruptedException e){
				
			}
		}
		
		Row row = null;
		if (inputUsed){
			row = readRow();
			
			String node = new String(nodeName);
			
			for(String s : parameterMapping.keySet()){
				node = node.replace(s, row.get(parameterMapping.get(s)).toString());
			}
			Object o = sock.getLdapTemplate().lookup(node);
			values = ((DirContextOperations)o).getStringAttributes(attributeName);
		}
		
		
		if (values != null) {
			for(int i = 0; i < values.length; i++){
				Row newRow = null;
				if (row != null) {
					newRow = RowFactory.createRow(this, row);
				}
				else {
					newRow = RowFactory.createRow(this);
				}
				
				int index = 0;
				for(StreamElement e : getTransformation().getDescriptor(getTransformation()).getStreamElements()){
					if (attributeName.equals(e.name)){
						newRow.set(index, values[++current]);
					}
					index++;
				}
				
				writeRow(newRow);
			}
		}
		
		
		if (!inputUsed){
			setEnd();
		}
		else {
			current = -1;
		}
		
		
		
		
	}

	@Override
	public void releaseResources() {
	
		info(" resources released");
	}

}
