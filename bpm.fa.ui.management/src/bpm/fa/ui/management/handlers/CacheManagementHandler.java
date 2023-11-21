package bpm.fa.ui.management.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import bpm.fa.ui.management.views.ViewCache;

public class CacheManagementHandler extends AbstractHandler {

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);


			try {
				window.getActivePage().showView(ViewCache.ID);
			} catch (PartInitException e) {
				
				e.printStackTrace();
			}
		return null;
	}

	
}
