package bpm.profiling.rcp;

import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);
        configurer.setTitle("Bi Profiler");
    }
    
	@Override
	public void openIntro() {
		try {
			MWindow mWindow = ((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
			EModelService modelService = mWindow.getContext().get(EModelService.class);
			
			removeShit("SearchField", mWindow, modelService);
			removeShit("Search-PS Glue", mWindow, modelService);
			removeShit("Spacer Glue", mWindow, modelService);
			removeShit("PerspectiveSpacer", mWindow, modelService);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeShit(String shit, MWindow mWindow, EModelService modelService) {
		try {
			MToolControl searchField = (MToolControl) modelService.find(shit, mWindow); //$NON-NLS-1$
			MTrimBar trimBar = modelService.getTrim((MTrimmedWindow) mWindow, SideValue.TOP);
			trimBar.getChildren().remove(searchField);
			Control control = (Control)searchField.getWidget();
			Composite parent = control.getParent();
			control.dispose();
		} catch (Exception e) {
//			System.out.println("Couldn't remove this shit : " + shit);
//			e.printStackTrace();
		}
	}
}
