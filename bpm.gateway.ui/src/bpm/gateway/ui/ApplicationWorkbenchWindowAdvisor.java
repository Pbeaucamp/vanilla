package bpm.gateway.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.gateway.ui.dialogs.utils.DialogWelcome;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.preferences.PreferencesConstants;
import bpm.gateway.ui.views.ConsoleRuntime;
import bpm.gateway.ui.views.ModelViewPart;
import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.repository.ui.versionning.VersionningManager;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public static final String ERROR_COLOR_KEY = "bpm.gateway.ui.colors.errorfield"; //$NON-NLS-1$
	public static final String WARN_COLOR_KEY = "bpm.gateway.ui.colors.warnfield"; //$NON-NLS-1$
	public static final String DEBUG_COLOR_KEY = "bpm.gateway.ui.colors.debugfield"; //$NON-NLS-1$
	
	public static final String TRASH_LINK_KEY = "bpm.gateway.ui.colors.trashconnection"; //$NON-NLS-1$
	public static final String ANNOTE_COLOR_KEY = "bpm.gateway.ui.colors.annote"; //$NON-NLS-1$
	public static final String GID_COLOR_KEY = "bpm.gateway.ui.colors.globale"; //$NON-NLS-1$
	
	
	private IPartListener listener;
	
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle("BI Gateway"); //$NON-NLS-1$

        initColorRegistry();
    }
    
    
    private void initColorRegistry(){
    	ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
    	registry.put("bpm.gateway.ui.colors.standard", new RGB(255,255,255)); //$NON-NLS-1$
    	registry.put(ERROR_COLOR_KEY, new RGB(255, 0, 50));
    	registry.put(WARN_COLOR_KEY, new RGB(255,125, 0));
    	registry.put("bpm.gateway.ui.colors.mappingexists", new RGB(0,25, 255)); //$NON-NLS-1$
    	registry.put(DEBUG_COLOR_KEY, new RGB(20,255, 50));
    	registry.put(TRASH_LINK_KEY, new RGB(255,0, 50));
    	registry.put(ANNOTE_COLOR_KEY, new RGB(205,20, 250));
    	registry.put(GID_COLOR_KEY, new RGB(217,208, 253));
    	
    	
    }

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		Activator .getDefault().getPreferenceStore().setValue(PreferencesConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().toString().substring(6));
		Activator.getDefault().getPreferenceStore().setDefault(PreferencesConstants.P_MAX_ROW_PER_CHUNK, 10000);
		
		listener = new IPartListener(){

			public void partActivated(IWorkbenchPart part) {
				if (part instanceof GatewayEditorPart){
					ModelViewPart view = (ModelViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
					
					if (view != null){
						view.refresh();
					}
					//update the properties
					ISourceProviderService service = (ISourceProviderService) bpm.vanilla.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
					SessionSourceProvider sourceProvider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
					try{
						sourceProvider.setDirectoryItemId(bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId());
					}catch(Exception ex){
						ex.printStackTrace();
						sourceProvider.setDirectoryItemId(null);
					}
					GatewayEditorInput input = (GatewayEditorInput)((GatewayEditorPart) part).getEditorInput();
					Activator.getDefault().getSessionSourceProvider().setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(input.getName()) != null);
					
				}
				
			}

			public void partBroughtToTop(IWorkbenchPart part) {
				ModelViewPart view = (ModelViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
				
				if (view != null){
					view.refresh();
				}
				
			}

			public void partClosed(IWorkbenchPart part) {
				/*
				 * check if their is still a project opened
				 * to enable/disable the export on repository command
				 */
				ISourceProviderService service = (ISourceProviderService) bpm.vanilla.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
				SessionSourceProvider sourceProvider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
				sourceProvider.setModelOpened(Activator.getDefault().getCurrentModel() != null);

			}

			public void partDeactivated(IWorkbenchPart part) {
				ModelViewPart view = (ModelViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
				
				if (view != null){
					view.refresh();
				}
				
			}

			public void partOpened(IWorkbenchPart part) {
				
				
			}
    		
    	};
    	
    	Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(listener);
//    	Variable v = ResourceManager.getInstance().getVariable(Variable.ENVIRONMENT_VARIABLE, "PREFERENCES_SERVER"); //$NON-NLS-1$
//    	v.setValue(Activator.getDefault().getPreferenceStore().getString(PreferencesConstants.P_PREFERENCE_URL));
    	Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setMaximized(true);
    	
    	
    	if (Activator.getDefault().getPreferenceStore().getBoolean(PreferencesConstants.P_SHOW_MENU_AT_STARTUP)){
    		DialogWelcome d = new DialogWelcome(getWindowConfigurer().getWindow().getShell());
    		d.open();
    	}
    	ConsolePlugin.getDefault().getConsoleManager().addConsoles( new IConsole[] { new ConsoleRuntime("Console", null) } ); //$NON-NLS-1$


    	
	}

	@Override
	public boolean preWindowShellClose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removePartListener(listener);
		return super.preWindowShellClose();
	}

	
	@Override
	public void postWindowCreate() {
		super.postWindowCreate();
		// remove preference pages 
		PreferenceManager pm = getWindowConfigurer().getWindow().getWorkbench().getPreferenceManager();

		for(IPreferenceNode n : pm.getRootSubNodes()){
			if (!n.getId().startsWith("bpm.")){ //$NON-NLS-1$
				pm.remove(n.getId());
			}
		
		}
	}
	
	@Override
	public void openIntro() {
		try {
			MWindow mWindow = ((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
			EModelService modelService = mWindow.getContext().get(EModelService.class);
			
//			removeShit("SearchField", mWindow, modelService); //$NON-NLS-1$
//			removeShit("Search-PS Glue", mWindow, modelService); //$NON-NLS-1$
//			removeShit("Spacer Glue", mWindow, modelService); //$NON-NLS-1$
			removeShit("PerspectiveSpacer", mWindow, modelService); //$NON-NLS-1$
			
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
			e.printStackTrace();
		}
	}
    
}
