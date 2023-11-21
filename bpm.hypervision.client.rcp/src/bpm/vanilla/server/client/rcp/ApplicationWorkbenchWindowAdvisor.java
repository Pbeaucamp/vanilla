package bpm.vanilla.server.client.rcp;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolBarImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.dialogs.LoginWizard;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowFastViewBars(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(true);
		configurer.setTitle(configurer.getTitle());// + " - GSIS Beta Version");
		// configurer.setTitle("Vanilla Server Manager");
	}

	@Override
	public void openIntro() {
		MWindow mWindow = ((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
		EModelService modelService = mWindow.getContext().get(EModelService.class);

		ToolBarImpl repotool = (ToolBarImpl) modelService.find("bpm.repository.ui.toolbar1", mWindow); //$NON-NLS-1$
		repotool.setVisible(false);
//		Object searchField = modelService.find("SearchField", mWindow); //$NON-NLS-1$
//		Object searchField1 = modelService.find("Search-PS Glue", mWindow);
		// Object spacer1 = modelService.find("PerspectiveSpacer", mWindow);
		// Object spacer2 = modelService.find("Spacer Glue", mWindow);

//		((Control) (((ToolControlImpl) searchField).getWidget())).dispose();
//		((Control) (((ToolControlImpl) searchField1).getWidget())).dispose();
	}

	// private void overrideFastViewBar(){
	// FastViewBar bar =
	// ((WorkbenchWindow)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()).getFastViewBar();
	//
	// // final ToolBar tbar = new ToolBar((Composite)bar.getControl(),
	// SWT.HORIZONTAL);
	// for(Control w : ((Composite)bar.getControl()).getChildren()){
	// if (w instanceof ToolBar){
	// //((ToolBar)w).setVisible(false);
	// }
	// }
	//
	// final List<Button> items = new ArrayList<Button>();
	// IPerspectiveDescriptor lastPerspective = null;
	// for(IPerspectiveDescriptor pd :
	// Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives()){
	//
	// // skip this Perspective we want it be the last button of the toolbar
	//    		if (pd.getId().equals("bpm.vanilla.server.client.ui.clustering.menu.perspectives.PerspectiveMenu")){ //$NON-NLS-1$
	// lastPerspective = pd;
	// continue;
	// }
	//
	// final IPerspectiveDescriptor _pd = pd;
	// final Button i = new Button((Composite)bar.getControl(), SWT.TOGGLE);
	// i.setImage(ImageDescriptor.createFromImageData(pd.getImageDescriptor().getImageData().scaledTo(16,
	// 16)).createImage());
	// i.setToolTipText(pd.getLabel());
	// i.addSelectionListener(new SelectionAdapter(){
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	//
	// for(Button it : items){
	// if (i != it){
	// it.setSelection(false);
	// }
	// }
	// Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
	// }
	// });
	// i.setEnabled(false);
	//
	// items.add(i);
	// }
	//
	// /*
	// * add the Menu As Last Perspective Button
	// */
	// if (lastPerspective != null){
	// final Button i = new Button((Composite)bar.getControl(), SWT.TOGGLE);
	// final IPerspectiveDescriptor _pd = lastPerspective;
	// items.add(i);
	// ;
	//
	// i.setImage(ImageDescriptor.createFromImageData(lastPerspective.getImageDescriptor().getImageData().scaledTo(16,
	// 16)).createImage());
	// i.setToolTipText(lastPerspective.getLabel());
	// i.addSelectionListener(new SelectionAdapter(){
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	//
	// for(Button it : items){
	// if (i != it){
	// it.setSelection(false);
	// }
	// }
	// Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
	// }
	// });
	// }
	//
	// bar.update(true);
	//
	// bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().setBarButtons(items);
	// }

	private void loginRepository(Shell sh) {
		LoginWizard wiz = new LoginWizard();
		WizardDialog dial = new WizardDialog(sh, wiz) {
			@Override
			protected void createButtonsForButtonBar(Composite parent) {
				super.createButtonsForButtonBar(parent);
				getButton(IDialogConstants.FINISH_ID).setText(Messages.VanillaView_44);
			}
		};
		if (dial.open() != WizardDialog.OK) {
			Activator.getDefault().getWorkbench().close();
		}
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		// overrideFastViewBar();

		// Shell sh = Activator.getDefault().getWorkbench()
		// .getActiveWorkbenchWindow().getShell();
		// loginRepository(sh);

		/*
		 * hide contributions from bpm.vanilla.repository.ui the menu
		 * contributions should be contrinuted by a
		 * bpm.vanilla.repository.ui.contribution plugin TODO
		 */
		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		for (IContributionItem mItems : mm.getItems()) {
			if (mItems.getId().contains("repository")) {// contribution //$NON-NLS-1$
				mm.remove(mItems.getId());
				mm.update(true);
			}

			if (mItems.getId().equals("file")) { //$NON-NLS-1$
				for (IContributionItem i : ((IMenuManager) mItems).getItems()) {
					if (i.getId() != null && i.getId().startsWith("bpm.repository")) { //$NON-NLS-1$
						((IMenuManager) mItems).remove(i.getId());
					}

				}
			}

			// remove some tool bar items
			ICoolBarManager cm = getWindowConfigurer().getActionBarConfigurer().getCoolBarManager();
			if (cm != null) {
				for (IContributionItem ci : cm.getItems()) {

					if (ci.getId().contains("bpm.repository")) { //$NON-NLS-1$
						try {
							cm.remove(ci.getId());
						} catch(Exception e) { }
					}

				}
				cm.update(true);
			}
		}
	}

}
