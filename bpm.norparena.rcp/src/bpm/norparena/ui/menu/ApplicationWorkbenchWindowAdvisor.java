package bpm.norparena.ui.menu;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolBarImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

import bpm.norparena.ui.menu.client.preferences.PreferenceConstants;
import bpm.vanilla.platform.core.beans.User;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private Shell sh;

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}
	
//    private void loginRepository() {
//		Rectangle r = sh.getBounds();
//		DialogRepository dial = new DialogRepository(sh);
//	
//		if (dial.open() != DialogRepository.OK){
//			Activator.getDefault().getWorkbench().close();
//    	}
//    }

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowMenuBar(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowFastViewBars(false);
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("Norparena"); //$NON-NLS-1$
		
		
		
		
	}
	@Override
	public void postWindowCreate() {
		//Remove the repository menu
		super.postWindowCreate();
		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		IContributionItem menuItem = mm.find("bpm.repository.ui.menu");
		
		if(menuItem != null) {
			menuItem.setVisible(false);
			mm.update(true);
		}
	}
    private void overrideFastViewBar(){
//    	WorkbenchWindow window = (WorkbenchWindow)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
//    	FastViewBar bar = window.getFastViewBar();
//    	
//    	final List<Button> items = new ArrayList<Button>();
//    	IPerspectiveDescriptor lastPerspective = null;
//    	for(IPerspectiveDescriptor pd : Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
//    		
//    		// skip this Perspective we want it be the last button of the toolbar
//    		if (pd.getId().equals("bpm.norparena.ui.menu.WelcomePerspective")){ //$NON-NLS-1$
//    			lastPerspective = pd;
//    			continue;
//    		}
//    		
//    		final IPerspectiveDescriptor _pd = pd;
//    		final Button i = new Button((Composite)bar.getControl(), SWT.TOGGLE);
//    		items.add(i);
//    		;
//    		
//    		i.setImage(ImageDescriptor.createFromImageData(pd.getImageDescriptor().getImageData().scaledTo(16, 16)).createImage());
//    		i.setToolTipText(pd.getLabel());
//    		i.addSelectionListener(new SelectionAdapter(){
//    			@Override
//    			public void widgetSelected(SelectionEvent e) {
//    				
//    				for(Button it : items){
//    					if (i != it){
//    						it.setSelection(false);
//    					}
//    				}
//    				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
//        		}
//    		});
//    		
//    		
//    	    		
//    	}
//    	
//    	/*
//    	 * add the Menu As Last Perspective Button
//    	 */
//    	if (lastPerspective != null){
//    		final Button i = new Button((Composite)bar.getControl(), SWT.TOGGLE);
//    		final IPerspectiveDescriptor _pd = lastPerspective;
//    		items.add(i);
//    		;
//    		
//    		i.setImage(ImageDescriptor.createFromImageData(lastPerspective.getImageDescriptor().getImageData().scaledTo(16, 16)).createImage());
//    		i.setToolTipText(lastPerspective.getLabel());
//    		i.addSelectionListener(new SelectionAdapter(){
//    			@Override
//    			public void widgetSelected(SelectionEvent e) {
//    				
//    				for(Button it : items){
//    					if (i != it){
//    						it.setSelection(false);
//    					}
//    				}
//    				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
//        		}
//    		});
//    	}
//    	
//    	bar.update(true);
    }
    
    /**
     * Update user in db
     * @param user
     * @throws Exception
     */
    private void updateUser(User user) throws Exception {
    	Activator.getDefault().getRemote().getVanillaSecurityManager().updateUser(user);
    }
	
    @Override
	public void postWindowOpen() {
    	Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().toString().substring(6));
    	
    	super.postWindowOpen();
    	
    	sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
    	
//    	loginRepository();
		
		overrideFastViewBar();
		
//		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
//		IContributionItem menuItem = mm.find("bpm.repository.ui.menu");
//		
//		if(menuItem != null) {
//			menuItem.setVisible(false);
//			mm.update(true);
//		}
		
		//remove preferences pages
		PreferenceManager pm = getWindowConfigurer().getWindow().getWorkbench().getPreferenceManager();
		
		for(IPreferenceNode n : pm.getRootSubNodes()){
			if (!n.getId().startsWith("bpm.forms.design.ui.vanillacontext")){ //$NON-NLS-1$
				pm.remove(n.getId());
			}
		
		}
    }
    
	@Override
	public void openIntro() {
		MWindow mWindow = ((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
		EModelService modelService = mWindow.getContext().get(EModelService.class);
		
		ToolBarImpl repotool = (ToolBarImpl) modelService.find("bpm.repository.ui.toolbar1", mWindow); //$NON-NLS-1$
		repotool.setVisible(false);
		Object searchField = modelService.find("SearchField", mWindow); //$NON-NLS-1$
		Object searchField1 = modelService.find("Search-PS Glue", mWindow);
//		Object spacer1 = modelService.find("PerspectiveSpacer", mWindow);
//		Object spacer2 = modelService.find("Spacer Glue", mWindow);
		
		((Control)(((ToolControlImpl)searchField).getWidget())).dispose();
		((Control)(((ToolControlImpl)searchField1).getWidget())).dispose();
//		((Control)(((ToolControlImpl)spacer1).getWidget())).dispose();
//		((Control)(((ToolControlImpl)spacer2).getWidget())).dispose();
		MTrimBar trimBar = modelService.getTrim((MTrimmedWindow) mWindow, SideValue.TOP);
		
		
//		trimBar.getChildren().remove(searchField1);
//		trimBar.getChildren().remove(searchField);
//		trimBar.getChildren().remove(repotool);
//		trimBar.getChildren().remove(spacer1);
//		trimBar.getChildren().remove(spacer2);
	}
}
