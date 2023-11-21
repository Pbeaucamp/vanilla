package bpm.fd.repository.ui.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.SAXException;

import bpm.fd.repository.ui.Messages;
import bpm.repository.api.axis.model.AxisRepositoryConnection;
import bpm.repository.api.model.RepositoryConnection;

public class ConnectionDigester {
	private List<RepositoryConnection> list = new ArrayList<RepositoryConnection>();
	
	@SuppressWarnings("unchecked")
	public ConnectionDigester(String path) throws IOException, SAXException{
		try {
			FileReader f = new FileReader(path);
			Document doc = DocumentHelper.parseText(IOUtils.toString(new FileInputStream(path), "UTF-8")); //$NON-NLS-1$
//			Digester dig = new Digester();
//			dig.setValidating(false);
//			dig.setClassLoader(Activator.class.getClassLoader());
			String root = "RepositoryConnections"; //$NON-NLS-1$
			
			
			for(Element c : (List<Element>)doc.getRootElement().elements("Connection")){ //$NON-NLS-1$
				RepositoryConnection repCon = new AxisRepositoryConnection();
				repCon.setName(c.element("name").getStringValue()); //$NON-NLS-1$
				repCon.setDesc(c.element("description").getStringValue()); //$NON-NLS-1$
				repCon.setHost(c.element(Messages.ConnectionDigester_5).getStringValue());
				repCon.setPort(c.element("port").getStringValue()); //$NON-NLS-1$
				repCon.setUsername(c.element("username").getStringValue()); //$NON-NLS-1$
				repCon.setPassword(c.element("password").getStringValue()); //$NON-NLS-1$
				
				list.add(repCon);
			}
			
//			dig.addObjectCreate(root, ArrayList.class);
//			dig.addObjectCreate(root + "/Connection", RepositoryConnection.class);
//			dig.addCallMethod(root + "/Connection/name", "setName", 0);
//			dig.addCallMethod(root + "/Connection/description", "setDesc", 0);
//			dig.addCallMethod(root + "/Connection/host", "setHost", 0);
//			dig.addCallMethod(root + "/Connection/port", "setPort", 0);
//			dig.addCallMethod(root + "/Connection/username", "setUsername", 0);
//			dig.addCallMethod(root + "/Connection/password", "setPassword", 0);
//			dig.addSetNext(root + "/Connection", "add");
			
//			list = (ArrayList<RepositoryConnection>)dig.parse(f);
			f.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FileNotFoundException(Messages.ConnectionDigester_0 + path + Messages.ConnectionDigester_10);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(Messages.ConnectionDigester_11 + path);
			
		}catch (DocumentException e) {
			e.printStackTrace();
			throw new SAXException(Messages.ConnectionDigester_12 + path);
		}

	}
	
	public List<RepositoryConnection> getConnections(){
		return list;
	}
	
	
}
