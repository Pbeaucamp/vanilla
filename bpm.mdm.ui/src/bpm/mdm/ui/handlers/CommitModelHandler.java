package bpm.mdm.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CommitModelHandler extends AbstractHandler {
	private boolean enabled = false;
	/**
	 * The constructor.
	 */
	public CommitModelHandler() {
		Activator.getDefault().getControler().setModelHandler(this);
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		Shell parent = window.getShell();
		try {
			Activator.getDefault().getMdmProvider().persistModel(Activator.getDefault().getMdmProvider().getModel());
			MessageDialog.openInformation(parent, Messages.CommitModelHandler_0, Messages.CommitModelHandler_1);

			Activator.getDefault().getControler().setModelReloaded();
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(parent, Messages.CommitModelHandler_2, Messages.CommitModelHandler_3 + e.getMessage());
		}
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		if (evaluationContext instanceof Boolean){
			this.enabled = (Boolean)evaluationContext;
			HandlerEvent handlerEvent = new HandlerEvent(this, this.enabled, !this.enabled);
			fireHandlerChanged(handlerEvent);
		}
	}
}
