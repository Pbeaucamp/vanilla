package bpm.fd.design.ui.rcp.dialogs;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.rcp.Messages;
import bpm.fd.design.ui.rcp.action.ActionOpenFdProject;

public class DialogOpenProject extends Dialog {

	private ListViewer viewer;
	private Action delete;

	public DialogOpenProject(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		ImageRegistry reg = Activator.getDefault().getImageRegistry();

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Canvas l = new Canvas(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		l.setBackgroundImage(reg.get(Icons.small_splash));

		Composite c = new Composite(main, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lb = new Label(c, SWT.NONE);
		lb.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		lb.setText(Messages.DialogOpenProject_0);

		viewer = new ListViewer(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getControl().setToolTipText(Messages.DialogOpenProject_1);
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {

				return (Object[]) inputElement;
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

		});
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				IProject p = (IProject) element;
				return p.getName() + "-" + p.getLocation().toOSString(); //$NON-NLS-1$
			}

		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if(ss.isEmpty()) {
					return;
				}

				IProject project = (IProject) ss.getFirstElement();
				ActionOpenFdProject a;
				try {
					a = new ActionOpenFdProject(project);
					a.run();
				} catch(Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogOpenProject_3, Messages.DialogOpenProject_4 + e.getCause().getMessage() + "\n" + e.getMessage()); //$NON-NLS-3$ //$NON-NLS-1$
				}

			}

		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getButton(IDialogConstants.BACK_ID).setEnabled(!event.getSelection().isEmpty());
				getButton(IDialogConstants.NEXT_ID).setEnabled(!event.getSelection().isEmpty());
			}
		});

		createContextMenu();
		return c;
	}

	private void createContextMenu() {

		delete = new Action("delete") { //$NON-NLS-1$
			public void run() {
				boolean b = MessageDialog.openQuestion(getShell(), Messages.DialogOpenProject_7, Messages.DialogOpenProject_8);
				for(IProject p : (List<IProject>) ((IStructuredSelection) viewer.getSelection()).toList()) {
					try {
						if(b) {
							p.delete(true, true, null);
						}
						else {
							p.delete(true, null);
						}

					} catch(Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.DialogOpenProject_9, ex.getCause().getMessage());
					}

				}

				setDatas();

			}
		};

		MenuManager mgr = new MenuManager();
		mgr.add(delete);
		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				if(viewer.getSelection().isEmpty()) {
					delete.setEnabled(false);
				}
				else {
					delete.setEnabled(true);
				}

			}
		});

		viewer.getList().setMenu(mgr.createContextMenu(viewer.getControl()));
	}

	protected void initializeBounds() {

		this.getShell().setText(Messages.DialogOpenProject_10);
		setDatas();
		getShell().setSize(400, 500);

	}

	private void setDatas() {
		/*
		 * load RecentFiles
		 */
		viewer.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
		Button b = createButton(parent, IDialogConstants.BACK_ID, IDialogConstants.OPEN_LABEL, false);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				StringBuffer errors = new StringBuffer();

				for(Object o : ss.toList()) {
					IProject project = (IProject) o;
					ActionOpenFdProject a;
					try {
						a = new ActionOpenFdProject(project);
						a.run();
					} catch(Exception ex) {
						ex.printStackTrace();
						errors.append(Messages.DialogOpenProject_5 + project.getName() + " : " + ex.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				if(errors.length() <= 0) {
					MessageDialog.openError(getShell(), Messages.DialogOpenProject_12, errors.toString());
				}
			}
		});
		b.setEnabled(false);
		b = createButton(parent, IDialogConstants.NEXT_ID, Messages.DialogOpenProject_13, false);
		b.setEnabled(false);
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(MessageDialog.openQuestion(getShell(), Messages.DialogOpenProject_14, Messages.DialogOpenProject_15)) {
					StringBuffer error = new StringBuffer();
					for(IProject p : (List<IProject>) ((IStructuredSelection) viewer.getSelection()).toList()) {
						try {
							p.delete(true, true, null);

						} catch(Exception ex) {
							ex.printStackTrace();
							error.append(Messages.DialogOpenProject_16 + p.getName() + " : " + ex.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
						}

					}
					if(error.length() > 0) {
						MessageDialog.openError(getShell(), Messages.DialogOpenProject_9, error.toString());
					}

					setDatas();
				}
			}
		});
	}

}
