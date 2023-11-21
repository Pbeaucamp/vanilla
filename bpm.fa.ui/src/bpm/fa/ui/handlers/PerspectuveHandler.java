package bpm.fa.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import bpm.fa.ui.Activator;
import bpm.fa.ui.perspective.Perspective;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class PerspectuveHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public PerspectuveHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		for(IPerspectiveDescriptor pd : window.getWorkbench().getPerspectiveRegistry().getPerspectives()){
			if (pd.getId().equals(Perspective.ID)){
				try {
					window.getActivePage().setPerspective(pd);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}
//		window.getActivePage().setPerspective(perspective)
		MessageDialog.openInformation(
				window.getShell(),
				"Ui",
				"Hello, Eclipse world");
		return null;
	}
}
