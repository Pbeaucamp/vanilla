package org.freeolap;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.SideValue;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.preferences.PreferenceConstants;



public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	
	private Shell sh;

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		sh = configurer.getWindow().getShell();
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(600, 400));
		configurer.setShowCoolBar(true);
		
		configurer.setShowFastViewBars(true);
		
		configurer.setShowStatusLine(true);
		configurer.setShowPerspectiveBar(false);

	}

	public void postWindowOpen() {
		super.postWindowOpen();
		
		IStatusLineManager statusline = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage(null, "Ready"); //$NON-NLS-1$
		
		FreemetricsPlugin .getDefault().getPreferenceStore().setValue(PreferenceConstants.P_INSTALLATION_FOLDER, Platform.getInstallLocation().getURL().getPath());
		FreemetricsPlugin plugin = FreemetricsPlugin.getDefault();		

		plugin.getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
			Shell shell = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell();

			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equalsIgnoreCase(PreferenceConstants.P_LANG)) {
					if (MessageDialog.openQuestion(
							shell,
							LanguageText.ApplicationWorkbenchWindowAdvisor_Confirmation,
					LanguageText.ApplicationWorkbenchWindowAdvisor_You_Must_restart_to_apply)) {
						IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
						configurer.getWindow().getWorkbench().restart();

					}
				}
			}

		});
//		try {
//			plugin.loadSecurity();
//		} catch (Exception e) {
//			MessageDialog.openError(sh, LanguageText.ApplicationWorkbenchWindowAdvisor_Error, LanguageText.ApplicationWorkbenchWindowAdvisor_Unable_to_read_security);
//			e.printStackTrace();
//		}

		plugin.login(sh);
		
		
		
	}

	
	@Override
	public void postWindowCreate() {
		super.postWindowCreate();
		
		/*
		 * remove unwanted contribution-items
		 */
		IMenuManager mm = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
		for (IContributionItem mItems : mm.getItems()){
			if (mItems.getId().equals("file")){ //$NON-NLS-1$
				for(IContributionItem i : ((IMenuManager)mItems).getItems()){
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
		}
	}
	
	@Override
	public boolean preWindowShellClose() {

		Shell shell = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell();
		
		
		
		
		if (shell.getText().contains("*")){ //$NON-NLS-1$
			MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL);
			mb.setText(LanguageText.ApplicationWorkbenchWindowAdvisor_Confirm_Exit);
			mb.setMessage(LanguageText.ApplicationWorkbenchWindowAdvisor_Save_your_schema);
			int flag = mb.open();
			if (flag == SWT.YES){
				FileDialog dd = new FileDialog(shell, SWT.SAVE);
				dd.setFilterExtensions(new String[] {"*.fasd"}); //$NON-NLS-1$
				dd.setText(LanguageText.ApplicationWorkbenchWindowAdvisor_Save_Project_As___);

				String path = dd.open();
				if (path != null) {
					FAModel model = FreemetricsPlugin.getDefault().getFAModel();

					if (!path.endsWith(".fasd")) //$NON-NLS-1$
						path += ".fasd";  //$NON-NLS-1$

					File file = new File(path);
					try {
						FileWriter fw = new FileWriter(file);
						fw.write(model.getFAXML());
						fw.close();
						if (FreemetricsPlugin.getDefault().isMondrianImport())
							FreemetricsPlugin.getDefault().setMondrianImport(false);

						shell.setText("Vanilla Analysis Designer - " + path); //$NON-NLS-1$
						FreemetricsPlugin.getDefault().setPath(path);
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(shell.getShell(), LanguageText.ApplicationWorkbenchWindowAdvisor_Error, LanguageText.ApplicationWorkbenchWindowAdvisor_Failed_To_Write_Data+ex.getMessage());
					}
				}
			}
			else if (flag == SWT.CANCEL){
				return false;
			}
			else{
				return true;
			}

		}

		return super.preWindowShellClose();
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
			System.out.println(LanguageText.ApplicationWorkbenchWindowAdvisor_8 + shit);
			e.printStackTrace();
		}
	}

}

