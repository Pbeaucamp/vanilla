package adminbirep;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolBarImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.views.ViewGroup;
import bpm.birep.admin.client.views.ViewRole;
import bpm.birep.admin.client.views.ViewUser;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	/*
	 * flag to know if the user, groups and role views must be inited(loading
	 * contents automatically) or not
	 */
	private boolean administrationViewsInited = false;

	private Shell sh;

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);

	}

	@Override
	public void postWindowOpen() {
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().toString().substring(6));

		super.postWindowOpen();

		sh = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();

		// loginRepository();

		initWindows();

		// Activator.expirationDate = v.getExpiratrionDate();
		Activator.expirationDate = ""; //$NON-NLS-1$

		overrideFastViewBar();

		/*
		 * hide contributions from bpm.vanilla.repository.ui the menu
		 * contributions should be contrinuted by a
		 * bpm.vanilla.repository.ui.contribution plugin TODO
		 */

		// IMenuManager mm =
		// getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		// for (IContributionItem mItems : mm.getItems()) {
		//			if (mItems.getId().contains("repository")) {// contribution //$NON-NLS-1$
		// mm.remove(mItems.getId());
		// mm.update(true);
		// }
		//
		//			if (mItems.getId().equals("file")) { //$NON-NLS-1$
		// for (IContributionItem i : ((IMenuManager) mItems).getItems()) {
		//					if (i.getId() != null && i.getId().startsWith("bpm.repository")) { //$NON-NLS-1$
		// ((IMenuManager) mItems).remove(i.getId());
		// }
		//
		// }
		// }
		//
		// // remove some tool bar items
		// ICoolBarManager cm =
		// getWindowConfigurer().getActionBarConfigurer().getCoolBarManager();
		//
		// for (IContributionItem ci : cm.getItems()) {
		//
		//				if (ci.getId().contains("bpm.repository")) { //$NON-NLS-1$
		// cm.remove(ci.getId());
		// }
		//
		// }
		// cm.update(true);
		// }
	}

	private void overrideFastViewBar() {
		// FastViewBar bar = ((WorkbenchWindow)
		// Activator.getDefault().getWorkbench().getActiveWorkbenchWindow()).getFastViewBar();
		//
		// final List<Button> items = new ArrayList<Button>();
		// IPerspectiveDescriptor lastPerspective = null;
		// for (IPerspectiveDescriptor pd :
		// Activator.getDefault().getWorkbench().getPerspectiveRegistry().getPerspectives())
		// {
		//
		//			if (pd.getId().equals("bpm.es.ui.menu.WelcomePerspective")) { //$NON-NLS-1$
		// lastPerspective = pd;
		// continue;
		// }
		//
		// final IPerspectiveDescriptor _pd = pd;
		// final Button i = new Button((Composite) bar.getControl(),
		// SWT.TOGGLE);
		// items.add(i);
		// ;
		//
		// i.setImage(ImageDescriptor.createFromImageData(pd.getImageDescriptor().getImageData().scaledTo(16,
		// 16)).createImage());
		// i.setToolTipText(pd.getLabel());
		// i.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// for (Button it : items) {
		// if (i != it) {
		// it.setSelection(false);
		// }
		// }
		// Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
		// }
		// });
		//
		// }
		//
		// /*
		// * add the Menu As Last Perspective Button
		// */
		// if (lastPerspective != null) {
		// final Button i = new Button((Composite) bar.getControl(),
		// SWT.TOGGLE);
		// final IPerspectiveDescriptor _pd = lastPerspective;
		// items.add(i);
		// ;
		//
		// i.setImage(ImageDescriptor.createFromImageData(lastPerspective.getImageDescriptor().getImageData().scaledTo(16,
		// 16)).createImage());
		// i.setToolTipText(lastPerspective.getLabel());
		// i.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// for (Button it : items) {
		// if (i != it) {
		// it.setSelection(false);
		// }
		// }
		// Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(_pd);
		// }
		// });
		// }
		//
		// bar.update(true);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setShowMenuBar(true);
		configurer.setShowPerspectiveBar(true);
		configurer.setShowFastViewBars(true);
		configurer.setTitle(Messages.ApplicationWorkbenchWindowAdvisor_6);

		IPreferenceStore p = Activator.getDefault().getPreferenceStore();
		String s = p.getString(PreferenceConstants.P_ROWS_BY_CHUNK);
		int i = p.getInt(PreferenceConstants.P_ROWS_BY_CHUNK);

		getWindowConfigurer().getWindow().addPerspectiveListener(new IPerspectiveListener() {

			public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
			}

			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				if (perspective.getId().equals("adminBIRep.perspective") && !administrationViewsInited) { //$NON-NLS-1$
					IViewPart part = page.findView(ViewUser.ID);
					// try {
					// ((ViewUser) part).initTree();
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					part = page.findView(ViewRole.ID);
					// try {
					// ((ViewRole) part).refresh();
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					part = page.findView(ViewGroup.ID);
					try {
						((ViewGroup) part).initTree();
					} catch (Exception e) {
						e.printStackTrace();
					}
					administrationViewsInited = true;
				}

			}
		});

	}

	private void initWindows() {
		Object o = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewUser.ID);
		if (o instanceof ViewUser) {
			ViewUser vu = (ViewUser) o;
			if (vu != null) {
				vu.initTree();
			}

		}

		ViewGroup vg = (ViewGroup) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
		if (vg != null) {
			try {
				vg.initTree();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ViewRole vr = (ViewRole) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewRole.ID);
		if (vr != null) {
			vr.createInput();
		}
	}

	@Override
	public void openIntro() {
		MWindow mWindow = ((WorkbenchWindow) PlatformUI.getWorkbench().getActiveWorkbenchWindow()).getModel();
		EModelService modelService = mWindow.getContext().get(EModelService.class);

		ToolBarImpl repotool = (ToolBarImpl) modelService.find("bpm.repository.ui.toolbar1", mWindow); //$NON-NLS-1$
		if (repotool != null) {
			repotool.setVisible(false);
		}

		// Hide menu from bpm.repository.ui
		MMenuElement menuEl = findMenuElement("org.eclipse.ui.main.menu", mWindow); //$NON-NLS-1$
		if (menuEl != null && menuEl.getWidget() != null && menuEl.getWidget() instanceof Menu) {
			Menu menu = (Menu) menuEl.getWidget();
			if (menu.getData() != null && menu.getData() instanceof MenuManager) {
				MenuManager menuManager = (MenuManager) menu.getData();
				IContributionItem item = menuManager.find("bpm.repository.ui.menu"); //$NON-NLS-1$
				if (item != null) {
					menuManager.remove(item);
				}
			}
		}

		// Object spacer1 = modelService.find("PerspectiveSpacer", mWindow);
		// Object spacer2 = modelService.find("Spacer Glue", mWindow);

		Object searchField = modelService.find("SearchField", mWindow); //$NON-NLS-1$
		if (searchField != null && ((ToolControlImpl) searchField).getWidget() != null) {
			((Control) (((ToolControlImpl) searchField).getWidget())).dispose();
		}
		
		Object searchField1 = modelService.find("Search-PS Glue", mWindow); //$NON-NLS-1$
		if (searchField1 != null && ((ToolControlImpl) searchField1).getWidget() != null) {
			((Control) (((ToolControlImpl) searchField1).getWidget())).dispose();
		}
	}

	private MMenuElement findMenuElement(String id, MUIElement searchRoot) {
		if (id == null) {
			throw new IllegalArgumentException("id is null!"); //$NON-NLS-1$
		}

		if (id.length() == 0) {
			throw new IllegalArgumentException("Empty string is not allowed in id."); //$NON-NLS-1$
		}

		if (searchRoot instanceof MMenuElement && id.equals(searchRoot.getElementId())) {
			return (MMenuElement) searchRoot;
		}

		if (searchRoot instanceof MTrimmedWindow) {
			MMenuElement findMenu = findMenuElement(id, ((MTrimmedWindow) searchRoot).getMainMenu());
			if (findMenu != null) {
				return findMenu;
			}
		}
		else if (searchRoot instanceof MPart) {
			List<MMenu> menus = ((MPart) searchRoot).getMenus();
			for (MMenu mm : menus) {
				MMenuElement findMenu = findMenuElement(id, mm);
				if (findMenu != null) {
					return findMenu;
				}
			}
		}
		else if (searchRoot instanceof MMenu) {
			List<MMenuElement> children = ((MMenu) searchRoot).getChildren();
			for (MMenuElement me : children) {
				MMenuElement findMenu = findMenuElement(id, me);
				if (findMenu != null) {
					return findMenu;
				}
			}
		}
		return null;
	}
}
