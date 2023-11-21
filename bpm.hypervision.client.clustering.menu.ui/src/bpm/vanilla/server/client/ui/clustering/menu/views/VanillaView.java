package bpm.vanilla.server.client.ui.clustering.menu.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.perspectives.PerspectiveFmdtLogs;
import bpm.vanilla.server.client.ui.clustering.menu.stress.ProfilingPerspective;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives.CachePerspective;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives.CacheTransfertPerspective;
import bpm.vanilla.server.client.ui.clustering.menu.uolap.perspectives.ManagementPerspective;

public class VanillaView extends ViewPart {
	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.views.VanillaView"; //$NON-NLS-1$
	private static final Font descriptionFont = new Font(Display.getDefault(), "Arial", 8, SWT.ITALIC); //$NON-NLS-1$
	private static final Font menuFont = new Font(Display.getDefault(), "Arial", 10, SWT.BOLD);; //$NON-NLS-1$

	private FormToolkit formToolkit;
	private ScrolledForm form;
//	private Button loginBt;
	private Button logoutBt;
	private Button monitorServer;
	private Button servicesDeclaration;
	private Button dndoButton;
	private Button uolapQueryMgmtButton;

	private Button transfertUolapCache;
	private Button fmdtQueries;
	private Button profileCluster;
	private Button repositoryContent;
//	private Button massReporting;

	private Button uolapCache;

	private Composite toolsPage;

	public VanillaView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		formToolkit = new FormToolkit(parent.getDisplay());

		form = formToolkit.createScrolledForm(parent);

		form.getBody().setLayout(new GridLayout());
		form.setText(Messages.VanillaView_47);
		formToolkit.decorateFormHeading(form.getForm());
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));

		createPlatformToolsPage(form.getBody());
	}

	private void createPlatformToolsPage(Composite parent) {
		toolsPage = formToolkit.createComposite(parent);
		toolsPage.setLayout(new GridLayout());
		toolsPage.setLayoutData(new GridData(GridData.FILL_BOTH));
		formToolkit.adapt(toolsPage);
		formToolkit.paintBordersFor(toolsPage);

		/*
		 * login section
		 */
		Section section = formToolkit.createSection(toolsPage, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		section.setText(Messages.VanillaView_3);
		section.setDescription(Messages.VanillaView_4);

		Composite c = formToolkit.createComposite(section);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

//		loginBt = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
//		loginBt.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 1, 2));
//		loginBt.setImage(Activator.getDefault().getImageRegistry().get("login")); //$NON-NLS-1$
//
//		Label l = formToolkit.createLabel(c, Messages.VanillaView_7);
//		l.setLayoutData(new GridData());
//		l.setFont(menuFont);
//
//		l = formToolkit.createLabel(c, Messages.VanillaView_8);
//		l.setLayoutData(new GridData());
//		l.setFont(descriptionFont);

		logoutBt = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		logoutBt.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false, 1, 2));
		logoutBt.setImage(Activator.getDefault().getImageRegistry().get("logout")); //$NON-NLS-1$
//		logoutBt.setEnabled(false);

		Label l = formToolkit.createLabel(c, Messages.VanillaView_11);
		l.setLayoutData(new GridData());
		l.setFont(menuFont);

		l = formToolkit.createLabel(c, Messages.VanillaView_12);
		l.setLayoutData(new GridData());
		l.setFont(descriptionFont);

		section.setClient(c);
		/*
		 * Plateforme Managements Section
		 */

		section = formToolkit.createSection(toolsPage, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData());
		section.setText(Messages.VanillaView_13);
		section.setDescription(Messages.VanillaView_14);

		c = formToolkit.createComposite(section);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		monitorServer = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		monitorServer.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		monitorServer.setEnabled(false);
		monitorServer.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.vanilla.server.ui.perspectives.MainPerspective").getImageDescriptor().createImage()); //$NON-NLS-1$

		l = formToolkit.createLabel(c, Messages.VanillaView_17);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);
		// formToolkit.adapt(l, true, true);

		l = formToolkit.createLabel(c, Messages.VanillaView_18);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);
		// formToolkit.adapt(l, true, true);

		servicesDeclaration = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		servicesDeclaration.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		servicesDeclaration.setEnabled(false);
		servicesDeclaration.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.es.clustering.ui.perspectives.VanillaServices").getImageDescriptor().createImage()); //$NON-NLS-1$

		l = formToolkit.createLabel(c, Messages.VanillaView_21);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);
		// formToolkit.adapt(l, true, true);

		l = formToolkit.createLabel(c, Messages.VanillaView_22);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);
		// formToolkit.adapt(l, true, true);

		dndoButton = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		dndoButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		dndoButton.setEnabled(false);
		dndoButton.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.es.dndserver.perspective").getImageDescriptor().createImage()); //$NON-NLS-1$

		l = formToolkit.createLabel(c, Messages.VanillaView_25);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);
		// formToolkit.adapt(l, true, true);

		l = formToolkit.createLabel(c, Messages.VanillaView_26);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);
		// formToolkit.adapt(l, true, true);

		fmdtQueries = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		fmdtQueries.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		fmdtQueries.setEnabled(false);
		fmdtQueries.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(PerspectiveFmdtLogs.ID).getImageDescriptor().createImage());

		l = formToolkit.createLabel(c, Messages.VanillaView_28);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);

		l = formToolkit.createLabel(c, Messages.VanillaView_29);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);

		profileCluster = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		profileCluster.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		profileCluster.setEnabled(false);
		profileCluster.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(ProfilingPerspective.ID).getImageDescriptor().createImage());

		l = formToolkit.createLabel(c, Messages.VanillaView_0);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);

		l = formToolkit.createLabel(c, Messages.VanillaView_1);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);

		repositoryContent = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		repositoryContent.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
		repositoryContent.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.vanilla.server.ui.perspectives.ContentPerspective").getImageDescriptor().createImage()); //$NON-NLS-1$

		l = formToolkit.createLabel(c, Messages.VanillaView_9);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);

		l = formToolkit.createLabel(c, Messages.VanillaView_10);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);
		section.setClient(c);

		/*
		 * massreport
		 */
//		massReporting = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
//		massReporting.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		massReporting.setEnabled(false);
//		massReporting.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(MassReportigPerspective.ID).getImageDescriptor().createImage());
//
//		l = formToolkit.createLabel(c, "Mass Reporting");
//		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		l.setFont(menuFont);
//
//		l = formToolkit.createLabel(c, "Check the state of Mass Reporting operations.");
//		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
//		l.setFont(descriptionFont);
//		section.setClient(c);

		/*
		 * UnitedOlap Mgmt
		 */
		section = formToolkit.createSection(toolsPage, Section.EXPANDED | Section.TITLE_BAR | Section.TWISTIE);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData());
		section.setText(Messages.VanillaView_30);
		section.setDescription(Messages.VanillaView_31);
		section.setExpanded(false);

		c = formToolkit.createComposite(section);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		transfertUolapCache = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		transfertUolapCache.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		transfertUolapCache.setEnabled(false);
		transfertUolapCache.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(CacheTransfertPerspective.ID).getImageDescriptor().createImage());

		l = formToolkit.createLabel(c, Messages.VanillaView_33);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);

		l = formToolkit.createLabel(c, Messages.VanillaView_34);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);

		uolapQueryMgmtButton = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		uolapQueryMgmtButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		uolapQueryMgmtButton.setEnabled(false);
		uolapQueryMgmtButton.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(ManagementPerspective.ID).getImageDescriptor().createImage());

		l = formToolkit.createLabel(c, Messages.VanillaView_36);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);

		l = formToolkit.createLabel(c, Messages.VanillaView_37);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);

		section.setClient(c);

		uolapCache = formToolkit.createButton(c, "", SWT.PUSH); //$NON-NLS-1$
		uolapCache.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 2));
//		uolapCache.setEnabled(false);
		uolapCache.setImage(Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(CachePerspective.ID).getImageDescriptor().createImage());

		l = formToolkit.createLabel(c, Messages.VanillaView_39);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(menuFont);

		l = formToolkit.createLabel(c, Messages.VanillaView_40);
		l.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		l.setFont(descriptionFont);

		section.setClient(c);

		/*
		 * listeners
		 */
//		massReporting.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				try {
//					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(MassReportigPerspective.ID);
//
//					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);
//
//				} catch (Throwable ex) {
//					ex.printStackTrace();
//				}
//
//			}
//		});

		profileCluster.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(ProfilingPerspective.ID);

					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});

		repositoryContent.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.vanilla.server.ui.perspectives.ContentPerspective"); //$NON-NLS-1$

					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});
		
		uolapCache.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(CachePerspective.ID);

					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});
		fmdtQueries.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(PerspectiveFmdtLogs.ID);

					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});
		transfertUolapCache.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(CacheTransfertPerspective.ID);

					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});

		uolapQueryMgmtButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(ManagementPerspective.ID);

					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});

		dndoButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.es.dndserver.perspective"); //$NON-NLS-1$

					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});
		servicesDeclaration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.es.clustering.ui.perspectives.VanillaServices"); //$NON-NLS-1$
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

				} catch (Throwable ex) {
					ex.printStackTrace();
				}

			}
		});
		monitorServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IPerspectiveDescriptor pd = Activator.getDefault().getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("bpm.vanilla.server.ui.perspectives.MainPerspective"); //$NON-NLS-1$
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(pd);

			}
		});

//		loginBt.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				LoginWizard wiz = new LoginWizard();
//				WizardDialog dial = new WizardDialog(getSite().getShell(), wiz) {
//					@Override
//					protected void createButtonsForButtonBar(Composite parent) {
//						super.createButtonsForButtonBar(parent);
//						getButton(IDialogConstants.FINISH_ID).setText(Messages.VanillaView_44);
//					}
//				};
//				if (dial.open() == WizardDialog.OK) {
//					loginBt.setEnabled(false);
//					logoutBt.setEnabled(true);
//					dndoButton.setEnabled(true);
//					monitorServer.setEnabled(true);
//					servicesDeclaration.setEnabled(true);
//					transfertUolapCache.setEnabled(true);
//					uolapQueryMgmtButton.setEnabled(true);
//					fmdtQueries.setEnabled(true);
//					uolapCache.setEnabled(true);
//					profileCluster.setEnabled(true);
////					massReporting.setEnabled(true);
//					updateFastView(true);
//					form.layout();
//				}
//			}
//		});

		logoutBt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				loginBt.setEnabled(true);
//				logoutBt.setEnabled(false);
//				dndoButton.setEnabled(false);
//				monitorServer.setEnabled(false);
//				servicesDeclaration.setEnabled(false);
//				transfertUolapCache.setEnabled(false);
//				uolapQueryMgmtButton.setEnabled(false);
//				fmdtQueries.setEnabled(false);
//				uolapCache.setEnabled(false);
//				profileCluster.setEnabled(false);
////				massReporting.setEnabled(false);
//				updateFastView(false);
				MessageDialog.openInformation(getSite().getShell(), Messages.VanillaView_45, Messages.VanillaView_46);
				Activator.getDefault().getWorkbench().close();
			}
		});
	}
	
//	public void updateFastView(boolean enable) {
//    	List<Button> buttons = Activator.getDefault().getBarButtons();
//    	
//    	if(buttons != null && !buttons.isEmpty()) {
//    		int last = buttons.size() - 1;
//    		for(int i=0; i<buttons.size(); i++) {
//    			if(i != last) {
//    				buttons.get(i).setEnabled(enable);
//    			}
//    		}
//    	}
//	}

	@Override
	public void setFocus() {

	}
}
