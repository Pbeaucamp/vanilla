package bpm.vanilla.repository.ui;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.versionning.VersionningManager;

public class CheckinHandler extends AbstractHandler{
	public static final String COMMAND_ID = "bpm.repository.ui.commands.disconnection";
	
	public CheckinHandler(){
		super.setBaseEnabled(false);
	}
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();

		
		
		String fileName = Activator.getDefault().getDesignerActivator().getCurrentModelFileName();
		
		Properties p = VersionningManager.getInstance().getCheckoutInfos(fileName);
		
		
		if (p == null){
			MessageDialog.openInformation(window.getShell(), Messages.CheckinAction_0, Messages.CheckinAction_1);
			return  null;
		}
		
		
		String repositoryUrl = p.getProperty("serverUrl"); //$NON-NLS-1$
		String repositoryUser = p.getProperty("username"); //$NON-NLS-1$
		String repositoryPassword = p.getProperty("password"); //$NON-NLS-1$
		String directoryItemId = p.getProperty("directoryItemId"); //$NON-NLS-1$
		String groupId = p.getProperty("groupId"); //$NON-NLS-1$
		
		IRepositoryApi sock = Activator.getDefault().getDesignerActivator().getRepositoryConnection();

		RepositoryItem repIt = null;
		
		try{
			repIt = sock.getRepositoryService().getDirectoryItem(Integer.parseInt(directoryItemId));
		}catch(Exception ex){
			MessageDialog.openError(window.getShell(), Messages.CheckinAction_7, Messages.CheckinAction_8 + ex.getMessage());
			return null;
		}

		
		
		
		String xml = Activator.getDefault().getDesignerActivator().getCurrentModelXml();
		
		
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			sock.getVersioningService().checkIn(repIt, "", bis); //$NON-NLS-1$
			
			MessageDialog.openInformation(window.getShell(), Messages.CheckinAction_10, Messages.CheckinAction_11);
			VersionningManager.getInstance().performCheckin(fileName);
			
			ISourceProviderService service = (ISourceProviderService) window.getService(ISourceProviderService.class);
			SessionSourceProvider sessionProvider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);

			sessionProvider.setCheckedIn(false);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(window.getShell(), Messages.CheckinAction_12, e.getMessage());
			VersionningManager.getInstance().saveChekout(fileName, sock, repIt);
		}
		
		
		
		
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		boolean old = isEnabled();
		
		try {
			//check if connected to vanilla
			Object o = ((IEvaluationContext)evaluationContext).getVariable(SessionSourceProvider.CHECK_IN_STATE);
			
			
			boolean enabled = SessionSourceProvider.enabled.equals(o);

			
			if (old != enabled){
				super.setBaseEnabled(enabled);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
}
