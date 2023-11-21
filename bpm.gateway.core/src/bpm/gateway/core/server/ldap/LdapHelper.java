package bpm.gateway.core.server.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;

import org.springframework.ldap.core.NameAwareAttribute;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.inputs.LdapInput;

public class LdapHelper {

//	private static final String OBJECT_CLASS = "objectClass";
	private static final int MAX_OBJECT_TEST = 20;

	public static DefaultStreamDescriptor getDescriptor(DataStream transfo) throws ServerException {
		LdapServer server = (LdapServer) transfo.getServer();
		LdapConnection sock = (LdapConnection) server.getCurrentConnection(null);

		if (!sock.isOpened()) {
			sock.connect(transfo.getDocument());
		}

		String parentDn = transfo.getDefinition();

		List<String> rowsDn = sock.getListNode(parentDn);

		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
//		//We add the column coming from inputs
//		for(Transformation t : transfo.getInputs()){
//			try{
//				for(StreamElement e : t.getDescriptor(transfo).getStreamElements()){
//					
//					StreamElement _e = e.clone(transfo.getName(), t.getName());
//					desc.addColumn(_e);
//				}
//			}catch(Exception ex){ }
//		}
		
		if (!rowsDn.isEmpty()) {

			List<String> existingAttributes = new ArrayList<String>();

			// We try to see if there is multiple type of objects
			for (int i = 0; i < rowsDn.size() && i < MAX_OBJECT_TEST; i++) {
				Attributes att = sock.getAttributes(rowsDn.get(i) + ", " + parentDn);// "regId=0000022,ou=personne,dc=basse-normandie-sante,dc=fr"
				// Attribute itemClass = att.get(OBJECT_CLASS);
				// String objectClass = null;
				// if (itemClass instanceof NameAwareAttribute) {
				// objectClass = ((NameAwareAttribute) itemClass).getAll();
				// }
				// else if (itemClass instanceof BasicAttribute) {
				// objectClass = ((BasicAttribute) itemClass).getID();
				// }

				// if (objectClass == null || !classes.contains(objectClass)) {
				NamingEnumeration<?> en = att.getAll();
				try {
					while (en.hasMore()) {

						Object item = en.nextElement();

						String attributeName = null;
						if (item instanceof NameAwareAttribute) {
							attributeName = ((NameAwareAttribute) item).getID();
						}
						else if (item instanceof BasicAttribute) {
							attributeName = ((BasicAttribute) item).getID();
						}

						if (attributeName != null && !attributeName.isEmpty() && !existingAttributes.contains(attributeName)) {
							StreamElement e = new StreamElement();
							e.className = "java.lang.String";
							e.transfoName = transfo.getName();
							e.originTransfo = transfo.getName();
							e.name = attributeName;

							desc.addColumn(e);
							existingAttributes.add(attributeName);
						}
					}

				} catch (Exception e) {
				}

				// classes.add(objectClass);
			}
		}

		return desc;

	}

	public static List<List<Object>> getValues(LdapInput os, int firstRow, int maxRows) throws Exception {
		int readedRow = 0;
		int currentRow = 0;

		LdapServer server = (LdapServer) os.getServer();
		LdapConnection sock = (LdapConnection) server.getCurrentConnection(null);

		if (!sock.isOpened()) {
			sock.connect(os.getDocument());
		}

		String parentDn = os.getDefinition();

		List<String> rowsDn = sock.getListNode(parentDn);

		List<List<Object>> values = new ArrayList<List<Object>>();
		for (int i = 0; i < rowsDn.size() && currentRow < maxRows; i++) {
			readedRow++;

			if (readedRow <= firstRow) {
				continue;
			}

			List<Object> l = new ArrayList<Object>();

			try {
				Attributes att = sock.getAttributes(rowsDn.get(i) + ", " + parentDn);
				for (StreamElement e : os.getDescriptor(null).getStreamElements()) {
					try {

						l.add(att.get(e.name).get());
					} catch (Exception ex) {
						l.add(null);

					}

				}

				values.add(l);
				currentRow++;
			} catch (Exception e) {

			}

		}
		sock.disconnect();

		return values;
	}
}
