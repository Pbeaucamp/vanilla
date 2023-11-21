package bpm.fd.repository.ui.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;

import bpm.fd.design.ui.Activator;
import bpm.fd.repository.ui.Messages;
import bpm.repository.api.model.RepositoryConnection;

/**
 * This class is a Helper to manage the Repository connections from a local file
 * @author LCA
 *
 */
public class RepositoryConnectionLoader {

	private static HashMap<String, RepositoryConnection> map = new HashMap<String, RepositoryConnection>();
	private static final String connectionFileName = "/resources/connections.xml"; //$NON-NLS-1$
	
	private static void load() {
		try {
			List<RepositoryConnection> list = new ConnectionDigester(Platform.getInstallLocation().getURL().getPath() + connectionFileName).getConnections();
			for(RepositoryConnection r: list){
				map.put(r.getName(), r);
			}
			
		} catch (Exception e) {
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.RepositoryConnectionLoader_1, e.getMessage());
			e.printStackTrace();
		}
	}


	public static HashMap<String, RepositoryConnection> getConnections(){
		if (map.isEmpty()){
			load();
		}
		
		return map;
	}
	
	public static RepositoryConnection getConnection(String name){
		for(String k : map.keySet()){
			if (k.equals(name)){
				return map.get(k);
			}
		}
		return null;
	}


	public static void save() {
		try {
			FileWriter fw;
			fw = new FileWriter(Platform.getInstallLocation().getURL().getPath() + connectionFileName);
			fw.write("<RepositoryConnections>"); //$NON-NLS-1$
			
			for(RepositoryConnection r : map.values()){
				fw.write(r.getXML());
			}
			fw.write("</RepositoryConnections>"); //$NON-NLS-1$
			fw.close();
			
		} catch (IOException e) {
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.RepositoryConnectionLoader_4, Messages.RepositoryConnectionLoader_5);
			e.printStackTrace();
		}
		
	}
}
