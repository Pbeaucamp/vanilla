package bpm.mdm.ui.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import bpm.mdm.ui.Activator;
import bpm.mdm.ui.perspectives.MdmDefinitionPerspective;
import bpm.mdm.ui.session.SessionSourceProvider;

public class MdmDesignHandler extends AbstractHandler  implements IElementUpdater {
	private IPerspectiveDescriptor original;
	public static final String COMMAND_ID = "bpm.mdm.ui.commands.designCommand";
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		//called by SessionSourceProvider at logout
		
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
//		
//		if ((event.getTrigger() instanceof SessionSourceProvider)){
//			if (original != null){
//				window.getActivePage().setPerspective(original);
//				original = null;
//			}
//		}
//		
//		else if (original == null ){
//			original = window.getActivePage().getPerspective();
			for (IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
				if (pd.getId().equals(MdmDefinitionPerspective.ID)){
					window.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
					break;
				}
			}
//		}
//		else {
//			window.getActivePage().setPerspective(original);
//			original = null;
//		}
		
		//force to refresh all UI that are linked to this handler
		ICommandService commandService = (ICommandService)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ICommandService.class);
		commandService.refreshElements(event.getCommand().getId(), null);
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(original != null);
	}
}
