package bpm.workflow.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.workflow.runtime.model.activities.TaskListType;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.icons.IconsNames;
import bpm.workflow.ui.viewer.TreeContentProvider;
import bpm.workflow.ui.viewer.TreeDirectory;
import bpm.workflow.ui.viewer.TreeItem;
import bpm.workflow.ui.viewer.TreeLabelProvider;
import bpm.workflow.ui.viewer.TreeParent;

/**
 * Dialog for browing the objects in the repository
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class DialogRepositoryObject extends Dialog {

	private TreeViewer treeViewer;
	private RepositoryItem item;
	private int type;
	private Combo server;
	private boolean isBIRT = false;
	private boolean isJasper = false;
	private boolean isFWR = false;

	private TaskListType taskListType;

	/**
	 * Create a dialog for browsing the reports in the repository
	 * 
	 * @param parent
	 *            Shell
	 * @param repository
	 *            Server
	 * @param type
	 *            of the report
	 */
	public DialogRepositoryObject(Shell parentShell, int _type) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.type = _type;
	}

	public DialogRepositoryObject(Shell parentShell, TaskListType _type) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.taskListType = _type;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		buildTree();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		ImageRegistry reg = Activator.getDefault().getImageRegistry();

		Composite filter = new Composite(parent, SWT.NONE);
		filter.setLayout(new GridLayout(2, false));
		filter.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		Button refresh = new Button(container, SWT.PUSH);
		refresh.setText(Messages.DialogRepositoryObject_0);
		refresh.setImage(reg.get(IconsNames.REFRESH));
		refresh.setToolTipText(Messages.DialogRepositoryObject_1);
		refresh.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1));
		refresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					if(Activator.getDefault().getRepositoryConnection() == null) {
						throw new Exception(Messages.DialogRepositoryObject_3);
					}
					Activator.getDefault().setRepository(new Repository(Activator.getDefault().getRepositoryConnection()));
					// Activator.getDefault().getListeRep().refresh();
					buildTree();
					treeViewer.refresh();
				} catch(Exception ex) {
					ex.printStackTrace();
					MessageDialog.openWarning(getShell(), Messages.DialogRepositoryObject_4, Messages.DialogRepositoryObject_5 + ex.getMessage());
				} finally {

				}
			}
		});

		SelectionAdapter adaptder = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(server.getText().equalsIgnoreCase("Jasper")) { //$NON-NLS-1$
					isJasper = true;
				}
				else if(server.getText().equalsIgnoreCase("Birt")) { //$NON-NLS-1$
					isBIRT = true;
				}
				else if(server.getText().equalsIgnoreCase("FreeWebReport")) { //$NON-NLS-1$
					isFWR = true;
				}

				buildTree();

				isBIRT = false;
				isFWR = false;
				isJasper = false;

			}

		};

		if(type == 780) {
			Label labelLabel = new Label(container, SWT.NONE); //$NON-NLS-1$
			labelLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
			labelLabel.setText(Messages.DialogRepositoryObject_2);

			server = new Combo(container, SWT.READ_ONLY);
			server.add("Jasper"); //$NON-NLS-1$
			server.add("FreeWebReport"); //$NON-NLS-1$
			server.add("Birt"); //$NON-NLS-1$
			server.add("ALL"); //$NON-NLS-1$
			server.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			server.addSelectionListener(adaptder);

		}

		Label search = new Label(container, SWT.NONE);
		search.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		search.setText(Messages.DialogRepositoryObject_11);

		Text description1 = new Text(container, SWT.BORDER);
		description1.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));

		treeViewer = new TreeViewer(container);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) treeViewer.getSelection();

				if(ss.isEmpty()) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					return;
				}

				if(ss.getFirstElement() instanceof TreeItem) {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
				else {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
				}

			}

		});

		return container;
	}

	private void buildTree() {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		IRepository rep;

		try {
			rep = Activator.getDefault().getRepository();

			for(RepositoryDirectory dir : rep.getRootDirectories()) {
				TreeDirectory td = new TreeDirectory(dir);
				buildChilds(td, rep);

				for(RepositoryItem it : rep.getItems(dir)) {
					if(taskListType != null) {
						if(it.getType() == IRepositoryApi.TASK_LIST) {
							// if (taskListType.name().equals(((DirectoryItem)it).getItem().getProperty().getModelType())){
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
							// }

						}

					}
					else if(isBIRT) {
						if(it.getSubtype() > 0) {
							if(it.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
					}
					else if(isJasper) {
						if(it.getSubtype() > 0) {
							if(it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
					}
					else if(isFWR) {
						if(it.getType() == IRepositoryApi.FWR_TYPE) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);

						}
					}

					else {
						if(type == IRepositoryApi.FMDT_TYPE) {
							if(it.getType() == IRepositoryApi.FMDT_TYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
						if(type == 880) {
							if(it.getSubtype() > 0) {
								if(it.getSubtype() == IRepositoryApi.ORBEON_XFORMS) {
									TreeItem ti = new TreeItem(it);
									td.addChild(ti);
								}
							}
						}
						if(type == 680) {

							if(it.getType() == IRepositoryApi.BIW_TYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
						if(type == 580) {

							if(it.getType() == IRepositoryApi.URL) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
						if(type == 980) {

							if(it.getType() == IRepositoryApi.GTW_TYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
						if(type == 480) {

							RepositoryItem o = it;
							if(o.getModelType() != null) {
								if(o.getModelType().equalsIgnoreCase(IRepositoryApi.MODEL_TYPE_NAMES[IRepositoryApi.MODEL_TYPE_FD_VANILLA_FORM])) {
									TreeItem ti = new TreeItem(it);
									td.addChild(ti);
								}
							}

						}
						if(type == 780) {
							if(it.getSubtype() > 0) {
								if(it.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
									TreeItem ti = new TreeItem(it);
									td.addChild(ti);
								}
								else if(it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
									TreeItem ti = new TreeItem(it);
									td.addChild(ti);
								}
							}
							else if(it.getType() == IRepositoryApi.FWR_TYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}

						if(type == -1) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}

					}

				}
				root.addChild(td);
			}
		} catch(Exception e) {
			e.printStackTrace();
			this.close();
			MessageDialog.openWarning(getShell(), Messages.DialogRepositoryObject_6, Messages.DialogRepositoryObject_7 + e.getMessage());
			// MessageDialog.openError(getShell(), Messages.DialogRepositoryObject_13, e.getMessage());
		}

		this.treeViewer.setInput(root);

	}

	private void buildChilds(TreeDirectory parent, IRepository rep) throws Exception {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();

		for(RepositoryDirectory d : rep.getChildDirectories(dir)) {
			TreeDirectory td = new TreeDirectory(d);
			parent.addChild(td);
			for(RepositoryItem it : rep.getItems(d)) {
				if(it.getType() == IRepositoryApi.TASK_LIST && this.taskListType != null) {
					// if (taskListType.name().equals(((DirectoryItem)it).getItem().getProperty().getModelType())){
					TreeItem ti = new TreeItem(it);
					td.addChild(ti);
					// }

				}
				if(isBIRT) {
					if(it.getSubtype() > 0) {
						if(it.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}
					}
				}
				else if(isJasper) {
					if(it.getSubtype() > 0) {
						if(it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}
					}
				}
				else if(isFWR) {
					if(it.getType() == IRepositoryApi.FWR_TYPE) {
						TreeItem ti = new TreeItem(it);
						td.addChild(ti);

					}
				}

				else {
					if(type == IRepositoryApi.FMDT_TYPE) {
						if(it.getType() == IRepositoryApi.FMDT_TYPE) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}
					}
					if(type == 880) {
						if(it.getSubtype() > 0) {
							if(it.getSubtype() == IRepositoryApi.ORBEON_XFORMS) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
					}
					if(type == 680) {

						if(it.getType() == IRepositoryApi.BIW_TYPE) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}
					}
					if(type == 580) {

						if(it.getType() == IRepositoryApi.URL) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}
					}
					if(type == 980) {

						if(it.getType() == IRepositoryApi.GTW_TYPE) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}
					}
					if(type == 480) {

						RepositoryItem o = it;
						if(o.getModelType() != null) {
							if(o.getModelType().equalsIgnoreCase(IRepositoryApi.MODEL_TYPE_NAMES[IRepositoryApi.MODEL_TYPE_FD_VANILLA_FORM])) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}

					}
					if(type == 780) {
						if(it.getSubtype() > 0) {
							if(it.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
							else if(it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
								TreeItem ti = new TreeItem(it);
								td.addChild(ti);
							}
						}
						else if(it.getType() == IRepositoryApi.FWR_TYPE) {
							TreeItem ti = new TreeItem(it);
							td.addChild(ti);
						}
					}

					if(type == -1) {
						TreeItem ti = new TreeItem(it);
						td.addChild(ti);
					}

				}
			}
			buildChilds(td, rep);
		}

	}

	@Override
	protected void okPressed() {
		item = ((TreeItem) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement()).getItem();

		super.okPressed();
	}

	/**
	 * 
	 * @return the Idirectoryitem selected
	 */
	public RepositoryItem getRepositoryItem() {
		return item;
	}
}
