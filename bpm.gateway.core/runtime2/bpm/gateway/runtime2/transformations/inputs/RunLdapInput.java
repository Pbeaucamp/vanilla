package bpm.gateway.runtime2.transformations.inputs;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.server.ldap.LdapConnection;
import bpm.gateway.core.server.ldap.LdapServer;
import bpm.gateway.core.transformations.inputs.LdapInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;

public class RunLdapInput extends RuntimeStep {

	private int currentRow = -1;
	private List<String> rowsDn = null;
	private LdapConnection sock;
	private String parentDn;

	private String connectionBaseDn;

	private boolean inputUsed = false;
	private LinkedHashMap<String, String> attributeMapping;
	private HashMap<String, Integer> parameterMapping = new HashMap<String, Integer>();

	private Object adapter;

	private int retryLdapConnection = 0;

	public RunLdapInput(LdapInput transformation, int bufferSize) {
		super(null, transformation, bufferSize);

	}

	@Override
	public void init(Object adapter) throws Exception {
		this.adapter = adapter;

		openLdapConnection();

		// LdapServer server =
		// (LdapServer)((LdapInput)getTransformation()).getServer();
		// sock = (LdapConnection)server.getCurrentConnection(adapter);
		attributeMapping = ((LdapInput) getTransformation()).getAttributeMapping();
		connectionBaseDn = sock.getBase();
		// if (!sock.isOpened()){
		// try{
		// sock.connect(null);
		// }catch(Exception e){
		// error("unable to connect to Ldap Server", e);
		// throw e;
		// }
		//
		// }
		parentDn = ((LdapInput) getTransformation()).getDefinition();

		/*
		 * check if the node name is defined by an input field
		 */

		if (!getTransformation().getInputs().isEmpty()) {
			int currentIndex = 0;
			List<StreamElement> fields = getTransformation().getInputs().get(0).getDescriptor(getTransformation()).getStreamElements();
			while (parentDn.substring(currentIndex).contains("{$")) {
				int start = parentDn.substring(currentIndex).indexOf("{$");
				int end = parentDn.substring(currentIndex).indexOf("}");
				String fieldName = parentDn.substring(start + 1, end);

				currentIndex = end + 1;

				for (int i = 0; i < fields.size(); i++) {
					if (("$" + fields.get(i).name).equals(fieldName)) {
						parameterMapping.put("{$" + fields.get(i).name + "}", i);
						inputUsed = true;
					}
				}
			}

		}

		isInited = true;
		info(" inited");
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()) {
			if (inputUsed) {
				if (inputEmpty()) {
					setEnd();
				}
			}
			// else{
			// if (inputEmpty() && currentRow >= rowsDn.size()){
			// setEnd();
			// }
			// }

		}

		if (isEnd() && inputEmpty()) {
			return;
		}

		if (!isEnd() && inputEmpty() && !inputs.isEmpty()) {
			try {
				Thread.sleep(10);
				return;
			} catch (InterruptedException e) {

			}
		}

		if (!inputUsed) {
			if (currentRow == -1) {
				rowsDn = getListNode(parentDn);
				info(" starting to read on LDAP");
			}
			else if (currentRow >= rowsDn.size()) {
				setEnd();
				return;
			}

			Row row = RowFactory.createRow(this);

			try {
				Attributes att = getAttributes(rowsDn.get(++currentRow) + ", " + parentDn);
				int i = 0;
				for (StreamElement e : getTransformation().getDescriptor(getTransformation()).getStreamElements()) {

					for (String s : attributeMapping.keySet()) {
						if (s.equals(e.name)) {
							try {
								row.set(i, att.get(attributeMapping.get(s)).get());
							} catch (Exception ex) {
								row.set(i, null);

							}
							i++;
							break;
						}
					}

				}

				writeRow(row);
			} catch (Exception e) {
				error(" problem encountered when building a row", e);
				throw e;
			}
			if (currentRow + 1 == rowsDn.size()) {
				setEnd();
			}
		}
		else {
			Row row = readRow();

			String node = new String(parentDn);

			for (String s : parameterMapping.keySet()) {
				node = node.replace(s, row.get(parameterMapping.get(s)).toString());
			}
			node = node.replace("," + connectionBaseDn, "");
			node = node.replace(", " + connectionBaseDn, "");

			rowsDn = getListNode(node);
			for (String rows : rowsDn) {

				Row newrow = RowFactory.createRow(this, row);

				try {
					Attributes attributes = getAttributes(rows + ", " + node);
					int i = 0;
					for (StreamElement e : getTransformation().getDescriptor(getTransformation()).getStreamElements()) {

						for (String s : attributeMapping.keySet()) {
							if (s.equals(e.name)) {
								try {
									Attribute attribute = attributes.get(attributeMapping.get(s));
									newrow.set(i, attribute != null ? attribute.get() : null);
								} catch (Exception ex) {
									newrow.set(i, null);
								}
								break;
							}
						}
						i++;
					}

					writeRow(newrow);
				} catch (Exception e) {
					error(" problem encountered when building a row", e);
					throw e;
				}
			}
		}
	}

	private void openLdapConnection() throws Exception {
		if (retryLdapConnection > 3) {
			error("We tried to restore the connection to the LDAP 5 times but were unable to do it.");
			throw new Exception("We tried to restore the connection to the LDAP 5 times but were unable to do it.");
		}

		retryLdapConnection++;

		try {
			LdapServer server = (LdapServer) ((LdapInput) getTransformation()).getServer();
			sock = (LdapConnection) server.getCurrentConnection(adapter);
			if (!sock.isOpened()) {
				sock.connect(null);
			}
		} catch (Exception e) {
			error("unable to connect to Ldap Server", e);
			openLdapConnection();
		}
	}

	private void closeLdapConnection() {
		if (sock != null && sock.isOpened()) {
			try {
				sock.disconnect();
			} catch (JdbcException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> getListNode(String node) throws Exception {
		try {
			return sock.getListNode(node);
		} catch (Exception e) {
			e.printStackTrace();
			
			error("Error trying to list the node " + node, e);
			
			openLdapConnection();
			return getListNode(node);
		}
	}
	
	private Attributes getAttributes(String dn) throws Exception {
		try {
			return sock.getAttributes(dn);
		} catch (Exception e) {
			e.printStackTrace();
			
			error("Error trying to get attributes for DN " + dn, e);
			
			openLdapConnection();
			return getAttributes(dn);
		}
	}

	@Override
	public void releaseResources() {
		if (rowsDn != null) {
			rowsDn.clear();
		}

		closeLdapConnection();

		info(" resources released");
	}

}
