package bpm.birep.admin.client.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.DialogLdap;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeGroup;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeObject;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.birep.admin.client.trees.TreeRole;
import bpm.birep.admin.client.trees.TreeUser;
import bpm.birep.admin.client.viewers.GroupViewerFilter;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Role;
import bpm.vanilla.platform.core.beans.RoleGroup;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;

public class ViewGroup extends ViewPart {
	public static final String ID = "bpm.birep.admin.client.viewgroup"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action addGroup, delGroup, importFromLdap;

	private ProgressBar progressBar;

	private ToolItem load, stopLoad;
	private Label currentState;
	private GroupViewerFilter groupFilter;

	/*
	 * filter Widgets
	 */

	private Text filterContent;
	private Button applyFilter;

	public ViewGroup() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		createTree(container);
		getSite().setSelectionProvider(viewer);

		setDnd();

		// try {
		// initTree();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private void createProgressBar(Composite parent) {
		progressBar = new ProgressBar(parent, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

	}

	private void createTree(Composite container) {
		createActions();
		createToolbar(container);
		createProgressBar(container);
		createFilter(container);

		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(new TreeParent("root")); //$NON-NLS-1$
		currentState = new Label(container, SWT.NONE);
		currentState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
	}

	private void createFilter(Composite parent) {
		Composite filterBar = new Composite(parent, SWT.NONE);
		filterBar.setLayout(new GridLayout(2, false));
		filterBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		applyFilter = new Button(filterBar, SWT.TOGGLE);
		applyFilter.setToolTipText(Messages.Client_Views_ViewGroup_2);
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		applyFilter.setImage(reg.get("filter")); //$NON-NLS-1$
		applyFilter.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		applyFilter.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						if (applyFilter.getSelection()) {
							groupFilter = new GroupViewerFilter(filterContent.getText());
							viewer.addFilter(groupFilter);
						}
						else {
							viewer.removeFilter(groupFilter);
						}
					}
				});
			}

		});

		filterContent = new Text(filterBar, SWT.BORDER);
		filterContent.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterContent.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						if (groupFilter != null) {
							groupFilter.setValue(filterContent.getText());
							viewer.refresh();
						}
					}
				});

			}

		});
	}

	private int begin = 0;
	private int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);;
	private boolean isGroupLoaded = false;
	private List<Group> currentgroups = new ArrayList<Group>();

	private GroupLoader groupLoader = new GroupLoader();

	private class GroupLoader extends Thread {
		private boolean shutDown = false;

		public void shutDown() {
			shutDown = true;
		}

		public void run() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					currentState.setText(Messages.Client_Views_ViewGroup_4);
				}
			});
			while (!isGroupLoaded && !shutDown) {

				List<Group> l;
				try {
					l = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(begin, step);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				begin += l.size();
				currentgroups.addAll(l);
				isGroupLoaded = l.size() == 0;

				TreeParent root = (TreeParent) viewer.getInput();
				for (Group g : l) {
					if (g.getParentId() == null) {
						TreeGroup tg = new TreeGroup(g);
						root.addChild(tg);

						try {
							for (User u : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsersForGroup(g)) {
								tg.addChild(new TreeUser(u));
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}

						try {
							for (Role r : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getRolesForGroup(g)) {
								tg.addChild(new TreeRole(r));
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}

						try {
							buildChilds(tg);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				if (isGroupLoaded) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							int pbMax = progressBar.getMaximum();
							progressBar.setSelection(pbMax);

						}
					});
				}
				else {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							int pbMax = progressBar.getMaximum();
							progressBar.setSelection((progressBar.getSelection() + 10) % pbMax);
						}
					});
				}

			}
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					currentState.setText(Messages.Client_Views_ViewGroup_5);
				}
			});
		}
	}

	private void fill() {

		begin = 0;
		isGroupLoaded = false;
		currentgroups.clear();
		viewer.setInput(new TreeParent("")); //$NON-NLS-1$

		groupLoader = new GroupLoader();
		groupLoader.start();
		load.setEnabled(false);
		stopLoad.setEnabled(true);
		Thread t = new Thread() {
			public void run() {

				while (groupLoader.isAlive()) {
					try {
						sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							viewer.refresh();
						}
					});
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						load.setEnabled(true);
						stopLoad.setEnabled(false);
					}
				});

			}
		};

		t.start();
	}

	private void stopFill() {

		load.setEnabled(false);
		stopLoad.setEnabled(false);

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				currentState.setText(Messages.Client_Views_ViewGroup_7);
				groupLoader.shutDown();
				try {
					groupLoader.join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				load.setEnabled(true);
				currentState.setText(Messages.Client_Views_ViewGroup_8);
			}
		});

	}

	private void buildChilds(TreeGroup root) throws Exception {
		for (Group g : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getChilds(root.getGroup())) {
			if (g.getParentId() != null && g.getParentId().equals(root.getGroup().getId())) {
				TreeGroup tg = new TreeGroup(g);
				root.addChild(tg);

				for (User u : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsersForGroup(g)) {
					tg.addChild(new TreeUser(u));
				}

				for (Role r : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getRolesForGroup(g)) {
					tg.addChild(new TreeRole(r));
				}

				buildChilds(tg);

			}
		}
	}

	@Override
	public void setFocus() {
	}

	public void refresh() {
		viewer.refresh();
	}

	private void createToolbar(Composite parent) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();

		ToolBar toolbar = new ToolBar(parent, SWT.NONE);

		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.Client_Views_ViewGroup_9);
		add.setImage(reg.get("add")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addGroup.run();
			}

		});

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.Client_Views_ViewGroup_11);
		del.setImage(reg.get("del")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setMessage(Messages.ViewGroup_0);
				messageBox.setText(Messages.ViewGroup_1);
				int response = messageBox.open();
				if (response == SWT.YES) {
					delGroup.run();
				}
			}
		});

		ToolItem imp = new ToolItem(toolbar, SWT.PUSH);
		imp.setToolTipText(Messages.Client_Views_ViewGroup_13);
		imp.setImage(reg.get("import")); //$NON-NLS-1$
		imp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importFromLdap.run();
			}
		});

		load = new ToolItem(toolbar, SWT.PUSH);
		load.setToolTipText(Messages.Client_Views_ViewGroup_15);
		load.setImage(reg.get("long_load")); //$NON-NLS-1$
		load.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				load.setEnabled(false);
				fill();
			}
		});

		stopLoad = new ToolItem(toolbar, SWT.PUSH);
		stopLoad.setToolTipText(Messages.Client_Views_ViewGroup_17);
		stopLoad.setImage(reg.get("stop_long_load")); //$NON-NLS-1$
		stopLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopLoad.setEnabled(false);
				stopFill();
			}
		});
		stopLoad.setEnabled(false);

	}

	private void createActions() {
		addGroup = new Action(Messages.Client_Views_ViewGroup_19) {
			public void run() {

				InputDialog id = new InputDialog(getSite().getShell(), Messages.Client_Views_ViewGroup_20, Messages.Client_Views_ViewGroup_21, "newGroupName", null); //$NON-NLS-1$
				if (id.open() == InputDialog.CANCEL) {
					return;
				}

				Group g = new Group();
				g.setName(id.getValue());

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.getFirstElement() instanceof TreeGroup) {
					g.setParentId(((TreeGroup) ss.getFirstElement()).getGroup().getId());
				}

				try {

					int grId = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addGroup(g);

					for (Object o : ((TreeParent) viewer.getInput()).childList()) {
						if (o instanceof TreeGroup) {
							if (((TreeGroup) o).getGroup().getId() == grId) {
								return;
							}
						}
					}
					g.setId(grId);
					TreeGroup newGroup = new TreeGroup(g);

					if (g.getParentId() != null) {
						((TreeParent) ss.getFirstElement()).addChild(newGroup);
					}
					else {
						((TreeParent) viewer.getInput()).addChild(new TreeGroup(g));
					}

					if (!ss.isEmpty() && ss.getFirstElement() instanceof TreeGroup) {
						for (Role r : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getRolesForGroup(((TreeGroup) ss.getFirstElement()).getGroup())) {
							try {
								RoleGroup rg = new RoleGroup();
								rg.setRoleId(r.getId());
								rg.setGroupId(g.getId());
								Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addRoleGroup(rg);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

					if (!ss.isEmpty() && ss.getFirstElement() instanceof TreeGroup) {

						for (Object o : ((TreeParent) ss.getFirstElement()).getChildren()) {
							if (o instanceof TreeRole) {
								newGroup.addChild(new TreeRole(((TreeRole) o).getRole()));
							}
						}

					}

					viewer.refresh();

				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewGroup_23, e1.getMessage());
				}
			}

		};

		delGroup = new Action(Messages.Client_Views_ViewGroup_24) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				for (Object o : ss.toList()) {
					if (o instanceof TreeGroup) {
						Group g = ((TreeGroup) o).getGroup();

						try {
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().delGroup(g);
							((TreeGroup) o).getParent().removeChild((TreeGroup) o);
							refresh();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					else if (o instanceof TreeUser) {
						TreeUser tu = (TreeUser) o;

						TreeGroup tg = (TreeGroup) tu.getParent();

						try {
							UserGroup ug = new UserGroup();
							ug.setUserId(tu.getUser().getId());
							ug.setGroupId(tg.getGroup().getId());
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().delUserGroup(ug);
							tg.removeChild(tu);
							viewer.refresh();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					else if (o instanceof TreeRole) {
						TreeRole tu = (TreeRole) o;

						TreeGroup tg = (TreeGroup) tu.getParent();

						try {
							RoleGroup rg = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getRoleGroups(tu.getRole().getId(), tg.getGroup().getId());
							if (rg != null) {
								Activator.getDefault().getVanillaApi().getVanillaSecurityManager().delRoleGroup(rg);
							}
							tg.removeChild(tu);
							viewer.refresh();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

			}
		};

		importFromLdap = new Action(Messages.Client_Views_ViewGroup_25) {
			public void run() {
				DialogLdap dial = new DialogLdap(getSite().getShell(), DialogLdap.GROUPS);

				if (dial.open() == DialogLdap.OK) {

					for (Object s : dial.getValues()) {
						if (s instanceof Group) {
							Group g = (Group) s;
							boolean exist = false;

							for (Object o : ((TreeParent) viewer.getInput()).getChildren()) {
								if (((TreeObject) o).getName().equals(g.getName())) {
									exist = true;
									break;
								}
							}
							if (!exist) {
								try {
									Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addGroup(g);
									((TreeParent) viewer.getInput()).addChild(new TreeGroup(g));
								} catch (Exception e) {
									e.printStackTrace();
								}

							}

						}

					}

					viewer.refresh();

				}
			}
		};
	}

	private void setDnd() {
		DropTarget target = new DropTarget(viewer.getTree(), DND.DROP_COPY);
		target.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		target.addDropListener(new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {
				Object o = ((TreeItem) event.item).getData();
				String[] buf = ((String) event.data).split("/"); //$NON-NLS-1$

				if (!(o instanceof TreeGroup)) {
					return;
				}

				TreeGroup tGroup = (TreeGroup) o;

				if (buf[0].equals("USR")) { //$NON-NLS-1$

					try {
						User user = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserById(Integer.parseInt(buf[1]));

						UserGroup ug = new UserGroup();
						ug.setUserId(user.getId());
						ug.setGroupId(tGroup.getGroup().getId());
						Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addUserGroup(ug);

						TreeUser tu = new TreeUser(user);
						tGroup.addChild(tu);
						viewer.refresh();
					} catch (Exception e) {
						MessageDialog.openInformation(getSite().getShell(), Messages.Client_Views_ViewGroup_26, e.getMessage());
						e.printStackTrace();
					}
				}

				else if (buf[0].equals("ROL")) { //$NON-NLS-1$

					try {
						Role role = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getRoleById(Integer.parseInt(buf[1]));

						boolean found = false;
						if (tGroup.getChildren() != null) {
							for (Object item : tGroup.getChildren()) {
								if (item instanceof TreeRole) {
									if (((TreeRole) item).getRole().getId().equals(role.getId())) {
										found = true;
										break;
									}
								}
							}
						}

						if (!found) {
							RoleGroup rg = new RoleGroup();
							rg.setRoleId(role.getId());
							rg.setGroupId(tGroup.getGroup().getId());
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addRoleGroup(rg);

							TreeRole tu = new TreeRole(role);
							tGroup.addChild(tu);
							viewer.refresh();
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						MessageDialog.openInformation(getSite().getShell(), Messages.Client_Views_ViewGroup_27, e.getMessage());
						e.printStackTrace();
					}
				}

			}

			public void dropAccept(DropTargetEvent event) {
				Object o = ((TreeItem) event.item).getData();
				if (!(o instanceof TreeItem))
					event = null;

			}

		});

	}

	public void initTree() throws Exception {
		List<Group> l = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(begin, step);

		currentgroups.addAll(l);

		TreeParent root = (TreeParent) viewer.getInput();
		for (Group g : l) {
			if (g.getParentId() == null) {
				TreeGroup tg = new TreeGroup(g);
				root.addChild(tg);

				for (User u : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsersForGroup(g)) {
					tg.addChild(new TreeUser(u));
				}

				for (Role r : Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getRolesForGroup(g)) {
					tg.addChild(new TreeRole(r));
				}

				buildChilds(tg);
			}
		}
		viewer.refresh();
		currentState.setText("Only " + step + " firsts Groups loaded. Use the Load Button to get all."); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
