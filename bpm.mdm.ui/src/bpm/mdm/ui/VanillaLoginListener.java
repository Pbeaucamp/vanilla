package bpm.mdm.ui;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.mdm.ui.session.SessionSourceProvider;
import bpm.vanilla.designer.ui.common.IRepositoryContextChangedListener;
import bpm.vanilla.platform.core.IRepositoryContext;

public class VanillaLoginListener implements IRepositoryContextChangedListener{

	@Override
	public void repositoryContextChanged(IRepositoryContext oldValue,
			IRepositoryContext newValue) {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		
		ISourceProviderService service = (ISourceProviderService) window.getService(ISourceProviderService.class);
		SessionSourceProvider sessionSourceProvider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.SESSION_STATE);
		sessionSourceProvider.setLoggedIn(newValue != null);
		
		//the vanilla url may have changed, we need 
		//to re-connect on the Mdm
		Activator.getDefault().resetMdmProvider();
		
	}

}
