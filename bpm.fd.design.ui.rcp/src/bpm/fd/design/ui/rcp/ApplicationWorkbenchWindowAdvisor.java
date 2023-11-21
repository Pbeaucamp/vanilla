package bpm.fd.design.ui.rcp;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.datas.ViewDictionary;
import bpm.fd.design.ui.editor.part.palette.ViewPalette;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.design.ui.project.views.ProjectView;
import bpm.fd.design.ui.rcp.dialogs.DialogWelcome;
import bpm.fd.design.ui.rcp.preferences.Preferences;
import bpm.fd.repository.ui.handlers.ExportHandler;
import bpm.fd.repository.ui.handlers.ImportHandler;
import bpm.fd.repository.ui.handlers.UpdateHandler;
import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.repository.ui.versionning.VersionningManager;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private IPartListener2 partListener;
	
	private static enum HandlerType{
		Import, Export, Update;
	}
	private static class RepositoryHandlerExpression extends Expression{
		HandlerType type;
		RepositoryHandlerExpression(HandlerType type){
			this.type = type;
		}
		@Override
		public EvaluationResult evaluate(IEvaluationContext context)
				throws CoreException {
			switch (type) {
			case Import:
				if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryContext() != null){
					return EvaluationResult.TRUE;
				}
				return EvaluationResult.FALSE;
			case Export:
				if (bpm.fd.repository.ui.Activator.getDefault().getRepositoryContext() != null &&
						bpm.fd.repository.ui.Activator.getDefault().getCurrentModel() != null){
					return EvaluationResult.TRUE;
				}
				return EvaluationResult.FALSE;
				
			case Update:
				if (bpm.fd.repository.ui.Activator.getDefault().getCurrentModelDirectoryItemId() != null && 
						bpm.fd.repository.ui.Activator.getDefault().getRepositoryContext() != null){
							return EvaluationResult.TRUE;
						}
				return EvaluationResult.FALSE;

			
		}
			return null;
		}
	}
	
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar(true);
        configurer.setShowFastViewBars(true);
        
        configurer.setShowStatusLine(false);
        configurer.setTitle(Messages.ApplicationWorkbenchWindowAdvisor_0);
        partListener = new IPartListener2(){

			public void partActivated(IWorkbenchPartReference partRef) {
				if (partRef.getId().equals(FdEditor.ID)){
					ProjectView v = (ProjectView)partRef.getPage().findView(ProjectView.ID);
					v.setContent(Activator.getDefault().getProject());
					
					ViewDictionary vd = (ViewDictionary)partRef.getPage().findView(ViewDictionary.ID);
					vd.setContent(Activator.getDefault().getProject());
					
					
//					ViewComponent vv = (ViewComponent)partRef.getPage().findView(ViewComponent.ID);
//					if (vv != null){
//						vv.setContent();
//					}
					partRef.getPage().getWorkbenchWindow().getShell().setText( Messages.ApplicationWorkbenchWindowAdvisor_1 + Activator.getDefault().getProject().getProjectDescriptor().getProjectName());
					
					//update the properties
					
					ISourceProviderService service = (ISourceProviderService) bpm.fd.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
					SessionSourceProvider sourceProvider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
					
					try{
						sourceProvider.setDirectoryItemId(bpm.fd.repository.ui.Activator.getDefault().getCurrentModelDirectoryItemId());
					}catch(Exception ex){
						ex.printStackTrace();
						sourceProvider.setDirectoryItemId(null);
					}
					sourceProvider.setModelOpened(bpm.fd.repository.ui.Activator.getDefault().getCurrentModel() != null);
					
				}
				
				
			}

			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				
				
			}

			public void partClosed(IWorkbenchPartReference partRef) {
				if (partRef.getId().equals(FdEditor.ID)){
					/*
					 * check if their is still a project opened
					 * to enable/disable the export on repository command
					 */
					ISourceProviderService service = (ISourceProviderService) bpm.vanilla.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
					SessionSourceProvider sourceProvider = (SessionSourceProvider)service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
					sourceProvider.setModelOpened(Activator.getDefault().getProject() != null);
					
					ProjectView v = (ProjectView)partRef.getPage().findView(ProjectView.ID);
					
					
					v.setContent(Activator.getDefault().getProject());
					
					ViewDictionary vd = (ViewDictionary)partRef.getPage().findView(ViewDictionary.ID);
					vd.setContent(Activator.getDefault().getProject());
					
//					ViewComponent vv = (ViewComponent)partRef.getPage().findView(ViewComponent.ID);
//					vv.setContent();
					
					/*
					 * check if the closed editor represent a main FdModel
					 * then check if the Project have been checkedout
					 */
					
					FdProjectEditorInput input = null;
					
					try{
						input = (FdProjectEditorInput)((FdEditor)partRef.getPart(false)).getEditorInput();
					}catch(Exception ex){
						ex.printStackTrace();
						return;
					}
					
					
					Properties p = VersionningManager.getInstance().getCheckoutInfos((new File(input.getModel().getProject().getProjectDescriptor().getProjectName())).getAbsolutePath());
					
					if (p == null){
						return;
					}
					
					
					 bpm.fd.repository.ui.Activator.getDefault().fireModelClosed((new File(input.getModel().getProject().getProjectDescriptor().getProjectName())).getAbsolutePath(), input.getModel().getProject());
					
					
				}
				
			}

			public void partDeactivated(IWorkbenchPartReference partRef) {
				
				
			}

			public void partHidden(IWorkbenchPartReference partRef) {
				
				
			}

			public void partInputChanged(IWorkbenchPartReference partRef) {
				
				
			}

			public void partOpened(IWorkbenchPartReference partRef) {
				
				
			}

			public void partVisible(IWorkbenchPartReference partRef) {
				
				
			}
			
		};
		configurer.getWindow().addPageListener(new IPageListener(){

			/* (non-Javadoc)
			 * @see org.eclipse.ui.IPageListener#pageActivated(org.eclipse.ui.IWorkbenchPage)
			 */
			public void pageActivated(IWorkbenchPage page) {
					
			}

			/* (non-Javadoc)
			 * @see org.eclipse.ui.IPageListener#pageClosed(org.eclipse.ui.IWorkbenchPage)
			 */
			public void pageClosed(IWorkbenchPage page) {
				page.removePartListener(partListener);				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.ui.IPageListener#pageOpened(org.eclipse.ui.IWorkbenchPage)
			 */
			public void pageOpened(IWorkbenchPage page) {
				page.addPartListener(partListener);					
			}
			
		});

    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
	 */
	@Override
	public void postWindowOpen() {
		
		
		super.postWindowOpen();
		
		try{
			String prefPath = System.getProperty("user.home") + "/.bpmconseil/serie4/freedashboard/designer/.metadata/.plugins/org.eclipse.core.runtime/.settings"; //$NON-NLS-1$ //$NON-NLS-2$
			
			File f = new File(prefPath);
			if (!f.exists()){
				f.mkdirs();
			}
			PrintWriter pw = new PrintWriter(prefPath + "/bpm.fd.design.ui.rcp.prefs"); //$NON-NLS-1$
			pw.write("installFolder=" + Platform.getInstallLocation().getURL().getPath()); //$NON-NLS-1$
			pw.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		IPreferenceStore store = bpm.fd.design.ui.rcp.Activator.getDefault().getPreferenceStore();
		
		if (store.getBoolean(Preferences.SHOW_WELCOME_AT_STARTUP)){
			DialogWelcome dial = new DialogWelcome(getWindowConfigurer().getWindow().getShell());
			dial.open();
		}
		
		/*
		 * here we force to use the bpm.fd.repository.ui handlers
		 * because FD has a different behavior for the Management of the model(multifiles)
		 *  
		 */
		IHandlerService s =  (IHandlerService)bpm.fd.design.ui.rcp.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);

		s.activateHandler(ExportHandler.COMMAND_ID, new ExportHandler(), new RepositoryHandlerExpression(HandlerType.Export));
		s.activateHandler(ImportHandler.COMMAND_ID, new ImportHandler(), new RepositoryHandlerExpression(HandlerType.Import));
		s.activateHandler(UpdateHandler.COMMAND_ID, new UpdateHandler(), new RepositoryHandlerExpression(HandlerType.Update));
		
		
		getWindowConfigurer().getWindow().getActivePage().addPartListener(new IPartListener() {
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				
				
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				
				
			}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				bpm.fd.design.ui.rcp.Activator.getDefault().getSessionSourceProvider().editorChanged();
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof FdEditor){
					try {
						
						for(IViewReference r: Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences()){
							if (r.getId().equals(ViewPalette.ID)){
								((ViewPalette)r.getView(true)).activateForInput(
										((FdEditor)part).getStructureEditor(), 
										(FdProjectEditorInput)((FdEditor)part).getEditorInput());
								break;
							}
						}
						bpm.fd.design.ui.rcp.Activator.getDefault().getSessionSourceProvider().editorChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part instanceof FdEditor){
					try {
						
						for(IViewReference r: Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences()){
							if (r.getId().equals(ViewPalette.ID)){
								((ViewPalette)r.getView(true)).activateForInput(
										((FdEditor)part).getStructureEditor(), 
										(FdProjectEditorInput)((FdEditor)part).getEditorInput());
								break;
							}
						}
						bpm.fd.design.ui.rcp.Activator.getDefault().getSessionSourceProvider().editorChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		});
	}
		

	@Override
	public void postWindowCreate() {
		super.postWindowCreate();
		//XXX : did not find another way to remove this plugins dependencies contributions
		// remove some menu items 
		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		for (IContributionItem mItems : mm.getItems()){
			if (	mItems .getId().equals("navigate") || //$NON-NLS-1$
					mItems .getId().equals("additions") || // marker //$NON-NLS-1$
					mItems .getId().equals("org.eclipse.search.menu")) {// contribution //$NON-NLS-1$
				mm.remove(mItems .getId());
				mm.update(true);
			}
			if (mItems.getId().equals("help")){ //$NON-NLS-1$
				
				for(IContributionItem i : ((IMenuManager)mItems).getItems()){
					if (i.getId().equals("org.eclipse.ui.actions.showKeyAssistHandler")){ //$NON-NLS-1$
						((IMenuManager)mItems).remove(i.getId());
						((IMenuManager)mItems).update(true);
					}
				}
			}
			if (mItems.getId().equals("file")){ //$NON-NLS-1$
				for(IContributionItem i : ((IMenuManager)mItems).getItems()){
					if (i.getId() != null && ((i.getId().startsWith("org.eclipse") && ! (i.getId().startsWith("org.eclipse.ui.file.save")))|| i.getId().equals("converstLineDelimitersTo"))){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						((IMenuManager)mItems).remove(i.getId());
					}
					if (i.getId() != null && i.getId().equals("org.eclipse.ui.actions.showKeyAssistHandler")){ //$NON-NLS-1$
						((IMenuManager)mItems).remove(i.getId());
						((IMenuManager)mItems).update(true);
					}
					if (i.getId() != null && i.getId().equals("bpm.repository.actions.Import")){ //$NON-NLS-1$
						((IMenuManager)mItems).remove(i.getId());
						((IMenuManager)mItems).update(true);
					}
					if (i.getId() != null && i.getId().equals("bpm.repository.actions.Export")){ //$NON-NLS-1$
						((IMenuManager)mItems).remove(i.getId());
						((IMenuManager)mItems).update(true);
					}
				}
			}
			
			
			// remove some tool bar items 
			ICoolBarManager cm = getWindowConfigurer().getActionBarConfigurer().getCoolBarManager();

			String[] items = new String[] {
				"org.eclipse.ui.edit.text.actionSet.navigation", //$NON-NLS-1$
				"org.eclipse.ui.edit.text.actionSet.annotationNavigation", //$NON-NLS-1$
				"org.eclipse.search.searchActionSet" }; //$NON-NLS-1$
			for (IContributionItem ci : cm.getItems()){
				for(String s : items){
					if (ci.getId().equals(s)){
						ci.dispose();
//						cm.remove(ci.getId());
					}
//					else if(ci.getId().equals("bpm.repository.ui.toolbar1")){
//						ci.dispose();
//					}
				}
			}
			cm.update(true); 
		}
		

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
			
			removeShit("SearchField", mWindow, modelService);
			removeShit("Search-PS Glue", mWindow, modelService);
			removeShit("Spacer Glue", mWindow, modelService);
			removeShit("PerspectiveSpacer", mWindow, modelService);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		try {
//			IHandlerService s =  (IHandlerService)bpm.fd.design.ui.rcp.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
//			
//			s.activateHandler(ExportHandler.COMMAND_ID, new ExportHandler(), new RepositoryHandlerExpression(HandlerType.Export));
//			s.activateHandler(ImportHandler.COMMAND_ID, new ImportHandler(), new RepositoryHandlerExpression(HandlerType.Import));
//			s.activateHandler(UpdateHandler.COMMAND_ID, new UpdateHandler(), new RepositoryHandlerExpression(HandlerType.Update));
//		} catch(Exception e) {
//			
//		}
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
