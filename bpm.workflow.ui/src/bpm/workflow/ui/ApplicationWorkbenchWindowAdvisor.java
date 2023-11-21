package bpm.workflow.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.repository.ui.versionning.VersionningManager;
import bpm.workflow.ui.dialogs.DialogWelcome;
import bpm.workflow.ui.editors.WorkflowEditorInput;
import bpm.workflow.ui.editors.WorkflowMultiEditorPart;
import bpm.workflow.ui.preferences.PreferencesConstants;
import bpm.workflow.ui.views.ModelViewPart;
import bpm.workflow.ui.views.ResourceViewPart;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public static final String Loop_COLOR_KEY = "bpm.gateway.ui.colors.globale"; //$NON-NLS-1$

	private IPartListener listener;

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1300, 800));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		initColorRegistry();
	}

	private void initColorRegistry() {
		ColorRegistry registry = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		registry.put("bpm.workflow.ui.colors.mappingexists", new RGB(0, 25, 255)); //$NON-NLS-1$
		registry.put(Loop_COLOR_KEY, new RGB(231, 238, 239));
	}

	@Override
	public void postWindowOpen() {

		Activator.getDefault().getPreferenceStore().setValue(PreferencesConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().getPath());

		listener = new IPartListener() {

			public void partActivated(IWorkbenchPart part) {
				if(part instanceof WorkflowMultiEditorPart) {
					ModelViewPart view = (ModelViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);

					if(view != null) {
						view.refresh();
					}
					// update the properties
					ISourceProviderService service = (ISourceProviderService) bpm.vanilla.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
					SessionSourceProvider sourceProvider = (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
					try {
						if(sourceProvider.getDirectoryItemId() == null) {
							sourceProvider.setDirectoryItemId(bpm.vanilla.repository.ui.Activator.getDefault().getDesignerActivator().getCurrentModelDirectoryItemId());
						}
					} catch(Exception ex) {
						ex.printStackTrace();
						sourceProvider.setDirectoryItemId(null);
					}

					WorkflowEditorInput input = (WorkflowEditorInput) ((WorkflowMultiEditorPart) part).getEditorInput();
					sourceProvider.setCheckedIn(VersionningManager.getInstance().getCheckoutInfos(input.getFileName()) != null);

					ResourceViewPart v = (ResourceViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);

					if(v != null) {
						v.refresh();
					}
				}

			}

			public void partBroughtToTop(IWorkbenchPart part) {
				ModelViewPart view = (ModelViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);

				if(view != null) {
					view.refresh();
				}

			}

			public void partClosed(IWorkbenchPart part) {
				/*
				 * check if their is still a project opened to enable/disable the export on repository command
				 */
				ISourceProviderService service = (ISourceProviderService) bpm.vanilla.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
				SessionSourceProvider sourceProvider = (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
				sourceProvider.setModelOpened(Activator.getDefault().getCurrentModel() != null);

			}

			public void partDeactivated(IWorkbenchPart part) {
				ModelViewPart view = (ModelViewPart) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);

				if(view != null) {
					view.refresh();
				}

			}

			public void partOpened(IWorkbenchPart part) {
				/*
				 * check if their is still a project opened to enable/disable the export on repository command
				 */
				ISourceProviderService service = (ISourceProviderService) bpm.vanilla.repository.ui.Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(ISourceProviderService.class);
				SessionSourceProvider sourceProvider = (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
				sourceProvider.setModelOpened(Activator.getDefault().getCurrentModel() != null);

			}

		};

		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(listener);

		/*
		 * Launch the Welcome Dialog
		 */
		if(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow() != null) {
			if(getWindowConfigurer().getWindow().getShell() != null) {
				DialogWelcome d = new DialogWelcome(getWindowConfigurer().getWindow().getShell());
				d.open();
			}
		}
		super.postWindowOpen();
	}
	
	@Override
	public void openIntro() {
		try {
			MWindow mWindow = ((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
			EModelService modelService = mWindow.getContext().get(EModelService.class);
			
			removeShit("SearchField", mWindow, modelService); //$NON-NLS-1$
			removeShit("Search-PS Glue", mWindow, modelService); //$NON-NLS-1$
			removeShit("Spacer Glue", mWindow, modelService); //$NON-NLS-1$
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
//			Control control = (Control)searchField.getWidget();
//			Composite parent = control.getParent();
//			control.dispose();
		} catch (Exception e) {
			System.out.println("Couldn't remove this shit : " + shit); //$NON-NLS-1$
			e.printStackTrace();
		}
	}

}
