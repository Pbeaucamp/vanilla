package bpm.metadata.birt.contribution.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import bpm.metadata.birt.contribution.helper.BirtDependency;
import bpm.metadata.birt.contribution.helper.BirtReport;
import bpm.metadata.birt.oda.ui.Activator;
import bpm.metadata.birt.oda.ui.trees.TreeContentProvider;
import bpm.metadata.birt.oda.ui.trees.TreeDirectory;
import bpm.metadata.birt.oda.ui.trees.TreeItem;
import bpm.metadata.birt.oda.ui.trees.TreeLabelProvider;
import bpm.metadata.birt.oda.ui.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogSave extends Dialog {

	private Text host, login, password;
	private TreeViewer viewer;
	private String xml;
	private IRepositoryApi sock;
	// private Button ok;
	private Text file;
	// private Combo groups;
	private Text itemName;

	private ComboViewer groupNames, repositoryViewer;

	private TableViewer viewerDep;
	private BirtReport reportFile;

	private static Color color = new Color(null, 255, 255, 255);

	public DialogSave(Shell parentShell) {
		super(parentShell);

	}

	private void updateButtons() {
		try {
			boolean b = reportFile != null && !viewer.getSelection().isEmpty() && ((((IStructuredSelection) viewer.getSelection()).getFirstElement() instanceof TreeDirectory && !itemName.getText().isEmpty()) || ((IStructuredSelection) viewer.getSelection()).getFirstElement() instanceof TreeItem);
			getButton(IDialogConstants.OK_ID).setEnabled(b);
		} catch (Exception ex) {
			ex.printStackTrace();
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite c = new Composite(container, SWT.NONE);
		c.setLayout(new GridLayout(3, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label l0 = new Label(c, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Activator.getResourceString("DialogSave.0")); //$NON-NLS-1$

		file = new Text(c, SWT.BORDER);
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		file.setEnabled(false);
		file.setForeground(color); // FP IDEA

		Button d = new Button(c, SWT.PUSH);
		d.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		d.setText(Activator.getResourceString("DialogSave.1")); //$NON-NLS-1$
		d.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);

				fd.setFilterPath(Platform.getLocation().toOSString());
				fd.setFilterExtensions(new String[] { "*.rptdesign", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$

				String s = fd.open();
				if (s != null) {
					file.setText(s);
					try {
						FileInputStream fis = new FileInputStream(s);

						xml = IOUtils.toString(fis, "UTF-8");
						fis.close();

						reportFile = new BirtReport(new File(s), xml);

						viewerDep.setInput(reportFile.getDependencies());
						updateButtons();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		Label l02 = new Label(c, SWT.NONE);
		l02.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l02.setText(Activator.getResourceString("DialogSave.2")); //$NON-NLS-1$

		Table tabDep = new Table(c, SWT.BORDER | SWT.FULL_SELECTION);
		tabDep.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		TableColumn col1 = new TableColumn(tabDep, SWT.NONE);
		col1.setText("Birt include name");
		col1.setWidth(100);
		TableColumn col2 = new TableColumn(tabDep, SWT.NONE);
		col2.setText("Filename");
		col2.setWidth(450);
		// col1.setText("Description");

		viewerDep = new TableViewer(tabDep);

		viewerDep.setContentProvider(new ArrayContentProvider());

		viewerDep.setLabelProvider(new ITableLabelProvider() {
			public void removeListener(ILabelProviderListener listener) {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void dispose() {
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0)
					return ((BirtDependency) element).getFileName();
				else if (columnIndex == 1) {
					return ((BirtDependency) element).hasError() ? ((BirtDependency) element).getErrmsg() : ((BirtDependency) element).getDepFile().getName();
				}
				else
					return "";
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		viewerDep.setContentProvider(new ArrayContentProvider());

		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Activator.getResourceString("ConnectionComposite.VanillaUrl")); //$NON-NLS-1$

		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.setText(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL) != null ? ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL) : "http://localhost:7171/VanillaRuntime");

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Activator.getResourceString("ConnectionComposite.Username")); //$NON-NLS-1$

		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.setText("system");

		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Activator.getResourceString("DialogSave.5")); //$NON-NLS-1$

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.setText("system");

		Button b = new Button(container, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
		b.setText("Connection");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BaseVanillaContext vctx = new BaseVanillaContext(host.getText(), login.getText(), password.getText());

				IVanillaAPI api = new RemoteVanillaPlatform(vctx);

				User user = null;
				try {
					try {
						user = api.getVanillaSecurityManager().authentify("", vctx.getLogin(), vctx.getPassword(), false);
					} catch (Exception ex) {
						repositoryViewer.setInput(Collections.EMPTY_LIST);
						groupNames.setInput(Collections.EMPTY_LIST);
						MessageDialog.openError(getShell(), "Connection", "Connection failed.\n" + ex.getMessage());
						return;
					}
					try {
						repositoryViewer.setInput(api.getVanillaRepositoryManager().getUserRepositories(vctx.getLogin()));
					} catch (Exception ex) {
						ex.printStackTrace();
						repositoryViewer.setInput(Collections.EMPTY_LIST);
						MessageDialog.openError(getShell(), "Repository Loading", "Failed to load repositories.\n" + ex.getMessage());
					}

					try {
						groupNames.setInput(api.getVanillaSecurityManager().getGroups(user));
					} catch (Exception ex) {
						ex.printStackTrace();
						groupNames.setInput(Collections.EMPTY_LIST);
						MessageDialog.openError(getShell(), "Group Loading", "Failed to load groups.\n" + ex.getMessage());

					}
				} finally {
					updateButtons();
				}

			}
		});

		l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Repository"); //$NON-NLS-1$

		repositoryViewer = new ComboViewer(container, SWT.READ_ONLY);
		repositoryViewer.setContentProvider(new ArrayContentProvider());
		repositoryViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Repository) element).getName();
			}
		});
		repositoryViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Activator.getResourceString("ConnectionComposite.10")); //$NON-NLS-1$

		groupNames = new ComboViewer(container, SWT.READ_ONLY);
		groupNames.setContentProvider(new ArrayContentProvider());
		groupNames.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
		groupNames.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button test = new Button(container, SWT.PUSH);
		test.setText(Activator.getResourceString("DialogSave.6")); //$NON-NLS-1$
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		test.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					//					Log.info("connect"); //$NON-NLS-1$
					createInput();
				} catch (Exception ex) {
					ex.printStackTrace();
					//					Log.error("error ", ex); //$NON-NLS-1$
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Activator.getResourceString("DialogSave.9"), ex)); //$NON-NLS-1$
					MessageDialog.openError(getShell(), Activator.getResourceString("DialogSave.25"), ex.getMessage()); //$NON-NLS-1$ FP IDEA
				} finally {
					updateButtons();
				}

			}

		});

		viewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeDirectory)) {
					if (ss.getFirstElement() instanceof TreeItem) {
						TreeItem item = (TreeItem) ss.getFirstElement();
						if (item.getItem().getType() == IRepositoryApi.CUST_TYPE && item.getItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {

							itemName.setEnabled(false);
						}
						else {
							itemName.setEnabled(false);
						}
					}
					else {
						itemName.setEnabled(false);
					}
				}
				else {
					itemName.setEnabled(true);
				}
				updateButtons();
			}

		});

		Label l9 = new Label(container, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l9.setText(Activator.getResourceString("DialogSave.11")); //$NON-NLS-1$

		itemName = new Text(container, SWT.BORDER);
		itemName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		itemName.setEnabled(false);
		itemName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateButtons();

			}
		});
		return container;
	}

	private void createInput() throws Exception {

		try {
			if (repositoryViewer.getSelection().isEmpty() || groupNames.getSelection().isEmpty()) {
				return;
			}

			Repository r = (Repository) ((IStructuredSelection) repositoryViewer.getSelection()).getFirstElement();
			Group g = (Group) ((IStructuredSelection) groupNames.getSelection()).getFirstElement();

			sock = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(host.getText(), login.getText(), password.getText()), g, r));

		} catch (Exception ex) {
			ex.printStackTrace();
			sock = null;
			viewer.setInput(new TreeParent("root"));
			throw ex;
		}

		// IRepository rep = sock.getRepository(IRepositoryConnection.CUST_TYPE,
		// IRepositoryConnection.BIRT_REPORT_SUBTYPE);
		final TreeParent root = new TreeParent("root"); //$NON-NLS-1$
		IRunnableWithProgress r = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

				try {

					bpm.vanilla.platform.core.repository.Repository rep = new bpm.vanilla.platform.core.repository.Repository(sock, IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE);

					for (RepositoryDirectory d : rep.getRootDirectories()) {
						TreeDirectory tp = new TreeDirectory(d);
						root.addChild(tp);
						buildChilds(rep, tp);

						for (RepositoryItem di : rep.getItems(d)) {
							TreeItem ti = new TreeItem(di);
							tp.addChild(ti);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		};
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.run(false, false, r);
		} catch (InvocationTargetException e) {
			// Operation was canceled
		} catch (InterruptedException e) {
			// Handle the wrapped exception
		}
		viewer.setInput(root);
	}

	private void buildChilds(bpm.vanilla.platform.core.repository.Repository rep, TreeDirectory parent) {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();

		try {
			for (RepositoryDirectory d : rep.getChildDirectories(dir)) {
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);
				for (RepositoryItem di : rep.getItems(d)) {
					TreeItem ti = new TreeItem(di);
					td.addChild(ti);
				}
				buildChilds(rep, td);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

		if (ss.getFirstElement() instanceof TreeDirectory) {
			RepositoryDirectory directory = ((TreeDirectory) ss.getFirstElement()).getDirectory();
			StringBuffer errors = new StringBuffer();
			try {
				Group g = (Group) ((IStructuredSelection) groupNames.getSelection()).getFirstElement();
				RepositoryItem item = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE, directory, itemName.getText(), "", "", "", reportFile.getRawXml(), true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

				for (BirtDependency dep : reportFile.getDependencies()) {
					if (!dep.hasError()) {
						File f = dep.getDepFile();

						if (dep.isSharedResource()) {
							sock.getRepositoryService().addSharedFile(dep.getFileName(), new FileInputStream(f));
						}
						else {
							LinkedDocument ld = null;
							try {
								ld = sock.getDocumentationService().attachDocumentToItem(item, new FileInputStream(f), dep.getFileName(), "Dependency of " + itemName.getText(), dep.getDepFile().getName().substring(dep.getDepFile().getName().lastIndexOf(".") + 1), "");
								sock.getAdminService().addGroupForLinkedDocument(g, ld);
							} catch (Exception ex) {
								if (ld == null) {
									errors.append("Failed to attach file " + dep.getFileName() + "\n");
								}
								else {
									errors.append("Failed to authorize access for group " + g.getName() + " on " + dep.getFileName() + "\n");
								}
							}
						}
					}
				}
				
				bpm.metadata.birt.contribution.Activator.getDefault().addItemPathMapping(reportFile.getFile().getAbsolutePath(), item.getId());
				bpm.metadata.birt.contribution.Activator.getDefault().setRepositoryApi(sock);

				if (!errors.toString().isEmpty()) {
					throw new Exception(errors.toString());
				}

			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Activator.getResourceString("DialogSave.25"), e.getMessage()); //$NON-NLS-1$
				return;
			}

			super.okPressed();
		}
		else if (ss.getFirstElement() instanceof TreeItem) {
			if (MessageDialog.openConfirm(getShell(), "Confirmation", "If you confirm, the selected item will be replace by the report!")) {
				RepositoryItem item = ((TreeItem) ss.getFirstElement()).getItem();

				StringBuffer errors = new StringBuffer();
				try {
					// fileName = file.getText();
					Group g = (Group) ((IStructuredSelection) groupNames.getSelection()).getFirstElement();
					sock.getRepositoryService().updateModel(item, reportFile.getRawXml());

					for (BirtDependency dep : reportFile.getDependencies()) {
						if (!dep.hasError()) {
							File f = dep.getDepFile();
							// f.exists()
							LinkedDocument ld = null;
							try {
								ld = sock.getDocumentationService().attachDocumentToItem(item, new FileInputStream(f), dep.getFileName(), "Dependency of " + itemName.getText(), dep.getDepFile().getName().substring(dep.getDepFile().getName().lastIndexOf(".") + 1), "");
								sock.getAdminService().addGroupForLinkedDocument(g, ld);
							} catch (Exception ex) {
								if (ld == null) {
									errors.append("Failed to attach file " + dep.getFileName() + "\n");
								}
								else {
									errors.append("Failed to authorize access for group " + g.getName() + " on " + dep.getFileName() + "\n");
								}
							}

						}
					}
					
					bpm.metadata.birt.contribution.Activator.getDefault().addItemPathMapping(reportFile.getFile().getAbsolutePath(), item.getId());
					bpm.metadata.birt.contribution.Activator.getDefault().setRepositoryApi(sock);

					if (!errors.toString().isEmpty()) {
						throw new Exception(errors.toString());
					}

				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getShell(), Activator.getResourceString("DialogSave.25"), e.getMessage()); //$NON-NLS-1$
					return;
				}

				super.okPressed();
			}
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Activator.getResourceString("DialogSave.26"), true).setEnabled(false); //$NON-NLS-1$

		createButton(parent, IDialogConstants.CANCEL_ID, Activator.getResourceString("DialogSave.28"), false); // NON-NLS-1$
																												// FP
																												// IDEA
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 600);
		getShell().setText(Activator.getResourceString("DialogSave.27")); //$NON-NLS-1$
		setShellStyle(SWT.RESIZE);
	}

}
