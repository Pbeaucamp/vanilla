package bpm.birep.admin.client.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.DialogRole;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeGroup;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.birep.admin.client.trees.TreeRole;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Role;

public class ViewRole extends ViewPart {

	public static final String ID = "bpm.birep.admin.client.viewroles"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action addRole, delRole;

	public ViewRole() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTree(container);
		getSite().setSelectionProvider(viewer);

		setDnd();
		
		createInput();
	}

	private void createTree(Composite container) {
		createActions();
		createToolbar(container);

		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
	}

	public void createInput() {

		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		for (int i = 0; i < IRepositoryApi.TYPES_NAMES.length; i++) {
			TreeParent tp = new TreeParent(IRepositoryApi.TYPES_NAMES[i]);
			root.addChild(tp);

			try {
				for (Role r : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getRolesForApp(IRepositoryApi.TYPES_NAMES[i])) {
					TreeRole tg = new TreeRole(r);
					tp.addChild(tg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		viewer.setInput(root);
	}

	@Override
	public void setFocus() {
	}

	public void refresh() {
		createInput();
		viewer.refresh();
	}

	private void createToolbar(Composite parent) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();

		ToolBar toolbar = new ToolBar(parent, SWT.NONE);

		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.Client_Views_ViewRole_2);
		add.setImage(reg.get("add")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addRole.run();
			}

		});

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.Client_Views_ViewRole_4);
		del.setImage(reg.get("del")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delRole.run();
			}
		});

		ToolItem refresh = new ToolItem(toolbar, SWT.PUSH);
		refresh.setToolTipText(Messages.ViewRole_0);
		refresh.setImage(reg.get("refresh")); //$NON-NLS-1$
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
	}

	private void createActions() {
		addRole = new Action(Messages.Client_Views_ViewRole_6) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.getFirstElement() instanceof TreeGroup) {
					return;
				}

				DialogRole dial = new DialogRole(getSite().getShell());
				if (dial.open() == DialogRole.OK) {
					for (Role r : dial.getRoles()) {
						try {
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addRole(r);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					createInput();
				}
				viewer.refresh();
			}
		};

		delRole = new Action(Messages.Client_Views_ViewRole_7) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				for (Object o : ss.toList()) {
					if (o instanceof TreeRole) {
						Role g = ((TreeRole) o).getRole();
						try {
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().delRole(g);
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}

						((TreeRole) o).getParent().removeChild((TreeRole) o);
						viewer.refresh();

						/*
						 * refresh the GroupView
						 */
						ViewGroup vg = (ViewGroup) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
						if (vg != null) {
							vg.refresh();
						}

					}
				}
			}
		};

	}

	private void setDnd() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		DragSource dragSource = new DragSource(viewer.getControl(), ops);
		dragSource.setTransfer(transfers);
		dragSource.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				// nothing, DnD always possible
			}

			public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();

				if (o instanceof TreeRole) {
					event.data = "ROL/" + ((TreeRole) o).getRole().getId(); //$NON-NLS-1$
				}

			}

			public void dragFinished(DragSourceEvent event) {
				event.data = null;
			}
		});
	}
}
