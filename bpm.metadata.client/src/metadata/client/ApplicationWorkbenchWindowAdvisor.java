package metadata.client;

import metadata.client.actions.ActionSave;
import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogWelcome;
import metadata.client.preferences.PreferenceConstants;
import metadataclient.Activator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
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

    @Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @Override
	public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle("VanillaMetaData"); //$NON-NLS-1$
        configurer.setShowPerspectiveBar(false);
    }

	@Override
	public boolean preWindowShellClose() {
		if (Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getText().contains("*")){ //$NON-NLS-1$
			
			MessageDialog d = new MessageDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ApplicationWorkbenchWindowAdvisor_2, 
					null,Messages.ApplicationWorkbenchWindowAdvisor_3,
					MessageDialog.QUESTION, new String[]{IDialogConstants.OK_LABEL, IDialogConstants.CLOSE_LABEL, IDialogConstants.CANCEL_LABEL}, 0
					);
			int i = SWT.DEFAULT;
			switch((i = d.open())){
			case 0:
				new ActionSave(Messages.ApplicationWorkbenchWindowAdvisor_4, Activator.getDefault().getFileName()).run();
				break;
			case 1:
				break;
			default:
				return false;
	
			}
			
		}
		
		return super.preWindowShellClose();
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		
		Activator.getDefault().registerListeners();
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().toString().substring(6));
		
		
		IStatusLineManager statusline = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage(null, Messages.ApplicationWorkbenchWindowAdvisor_1); //$NON-NLS-1$
		
		
		Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		
		
		if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_SHOW_MENU_AT_STARTUP)){
			DialogWelcome dial = new DialogWelcome(shell);
			dial.open();
		}
		
	}

	@Override
	public IWorkbenchWindowConfigurer getWindowConfigurer() {
		return super.getWindowConfigurer();
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
