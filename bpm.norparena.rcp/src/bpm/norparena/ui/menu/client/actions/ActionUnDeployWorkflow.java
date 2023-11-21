package bpm.norparena.ui.menu.client.actions;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.Messages;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ActionUnDeployWorkflow extends Action{
	private RepositoryItem item;
	private String groupname = ""; //$NON-NLS-1$
	
	public ActionUnDeployWorkflow(){
		super(Messages.ActionUnDeployWorkflow_1);
	}
	
	public void setDirectoryItem(RepositoryItem item){
		this.item = item;
	}
	
	public void setGroupName(String groupname){
		this.groupname = groupname;
	}
	
	public void run(){
		Shell sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		try{
			String biRepositoryUrl = Activator.getDefault().getRepositoryContext().getRepository().getUrl();
			String biRepositoryUser = Activator.getDefault().getRepositoryContext().getVanillaContext().getLogin();
			String biRepositoryPassword = Activator.getDefault().getRepositoryContext().getVanillaContext().getPassword();
			
			String vanillaUrl = Activator.getDefault().getRepositoryContext().getVanillaContext().getVanillaUrl();
			
			
			String fullUrl = vanillaUrl + "/Workflow" + "?user=" + biRepositoryUser + "&password=" + biRepositoryPassword + "&biRepPath=" + new URL(biRepositoryUrl).getPath().substring(1) + "&action=undeploy" + "&itemId=" + item.getId() + "&group=" + groupname; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			URL url = new URL(fullUrl);
			
			HttpURLConnection sock = (HttpURLConnection) url.openConnection();
			
			sock.setDoInput(true);
			sock.setDoOutput(true);
			sock.setRequestMethod("GET"); //$NON-NLS-1$
			sock.setRequestProperty("Content-type", "text/xml;charset=UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
	
			
				
			InputStream is = sock.getInputStream();
			sock.connect();
			
	//		check if the use is identofie
			String server = IOUtils.toString(is, "UTF-8"); //$NON-NLS-1$
			is.close();
			
			sock.disconnect();

			if (server.contains("<error>")){ //$NON-NLS-1$
				server = server.substring(server.indexOf("<error>") + 7, server.indexOf("</error>")); //$NON-NLS-1$ //$NON-NLS-2$
				throw new Exception(server);
			}
			
			MessageDialog.openInformation(sh, Messages.ActionUnDeployWorkflow_16, Messages.ActionUnDeployWorkflow_13);
		}catch(Exception e){
			e.printStackTrace();
			MessageDialog.openError(sh, Messages.ActionUnDeployWorkflow_0, e.getMessage());
		}
				
		
	}
}
