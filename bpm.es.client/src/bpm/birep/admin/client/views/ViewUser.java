package bpm.birep.admin.client.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
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
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.DialogLdap;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeObject;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.birep.admin.client.trees.TreeUser;
import bpm.birep.admin.client.viewers.GroupViewerFilter;
import bpm.vanilla.platform.core.beans.User;

public class ViewUser extends ViewPart {
	public static final String ID = "bpm.birep.admin.client.viewuser"; //$NON-NLS-1$

	private TreeViewer viewer;
	private Action addUser, delUser, importFromLdap;

	private ProgressBar progressBar;

	private ToolItem load, stopLoad;
	private Label currentState;
	private GroupViewerFilter groupFilter;

	/*
	 * filter Widgets
	 */

	private Text filterContent;
	private Button applyFilter;

	private UsersLoader userLoader = new UsersLoader();

	public ViewUser() {
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTree(container);
		getSite().setSelectionProvider(viewer);

		setDnd();

		initTree();
	}

	private void createFilter(Composite parent) {
		Composite filterBar = new Composite(parent, SWT.NONE);
		filterBar.setLayout(new GridLayout(2, false));
		filterBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		applyFilter = new Button(filterBar, SWT.TOGGLE);
		applyFilter.setToolTipText(Messages.Client_Views_ViewUser_1);
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

	private void createProgressBar(Composite parent) {
		progressBar = new ProgressBar(parent, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

	}

	private void createTree(Composite container) {
		createActions();
		createToolbar(container);
		createProgressBar(container);
		createFilter(container);

		viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setInput(new TreeParent("")); //$NON-NLS-1$

		currentState = new Label(container, SWT.NONE);
		currentState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

	}

	private int begin = 0;
	private int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);
	private boolean isUserLoaded = false;
	private List<User> currentusers = new ArrayList<User>();

	@Override
	public void setFocus() {
	}

	public void refresh() {
		// GCH if you change this i wont gonna be really a peacefull guy with
		// your nuts
		viewer.refresh();
	}

	public void initTree() {
		try {
			List<User> l = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers(begin, step);
			currentusers.addAll(l);

			TreeParent root = (TreeParent) viewer.getInput();
			for (User g : l) {
				root.addChild(new TreeUser(g));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		viewer.refresh();

		currentState.setText("Only " + step + " firsts User loaded. Use the Load Button to get all."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void createToolbar(Composite parent) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();

		ToolBar toolbar = new ToolBar(parent, SWT.NONE);

		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setToolTipText(Messages.Client_Views_ViewUser_6);
		add.setImage(reg.get("add")); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addUser.run();
			}

		});

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.Client_Views_ViewUser_8);
		del.setImage(reg.get("del")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox messageBox = new MessageBox(getSite().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setMessage(Messages.ViewUser_0);
				messageBox.setText(Messages.ViewUser_1);
				int response = messageBox.open();
				if (response == SWT.YES) {
					delUser.run();
				}
			}
		});

		ToolItem imp = new ToolItem(toolbar, SWT.PUSH);
		imp.setToolTipText(Messages.Client_Views_ViewUser_10);
		imp.setImage(reg.get("import")); //$NON-NLS-1$
		imp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importFromLdap.run();
			}
		});

		load = new ToolItem(toolbar, SWT.PUSH);
		load.setToolTipText(Messages.Client_Views_ViewUser_12);
		load.setImage(reg.get("long_load")); //$NON-NLS-1$
		load.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				load.setEnabled(false);
				fill();
			}
		});

		stopLoad = new ToolItem(toolbar, SWT.PUSH);
		stopLoad.setToolTipText(Messages.Client_Views_ViewUser_14);
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

	private void fill() {

		begin = 0;
		isUserLoaded = false;
		currentusers.clear();
		viewer.setInput(new TreeParent("")); //$NON-NLS-1$

		userLoader = new UsersLoader();

		load.setEnabled(false);
		stopLoad.setEnabled(true);

		Thread t = new Thread() {
			public void run() {

				userLoader.start();
				while (userLoader.isAlive()) {
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
				currentState.setText(Messages.Client_Views_ViewUser_17);
				userLoader.shutDown();
				try {
					userLoader.join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				load.setEnabled(true);
				currentState.setText(Messages.Client_Views_ViewUser_0);
			}
		});

	}

	private void createActions() {
		addUser = new Action(Messages.Client_Views_ViewUser_19) {
			public void run() {
				User u = new User();
				int i = 1;
				Boolean exist = true;
				while (exist) {
					String name = ("UserName" + i); //$NON-NLS-1$
					String login = ("UserLogin" + i); //$NON-NLS-1$
					User user = null;
					try {
						user = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserByLogin(login);
					} catch (Exception e) {
						// e.printStackTrace();
					}
					if (user == null) {
						u.setLogin(login);
						u.setName(name);
						exist = false;
					}
					else {
						i++;
					}
				}
				try {
					Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addUser(u);
					User user = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserByLogin(u.getLogin());
					user.setPassword(""); //$NON-NLS-1$
					((TreeParent) viewer.getInput()).addChild(new TreeUser(user));
					viewer.refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		delUser = new Action(Messages.Client_Views_ViewUser_22) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				for (Object o : ss.toList()) {
					if (o instanceof TreeUser) {
						// from repository database
						User u = ((TreeUser) o).getUser();
						try {
							Activator.getDefault().getVanillaApi().getVanillaSecurityManager().delUser(u);
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}

						((TreeUser) o).getParent().removeChild((TreeUser) o);
						viewer.refresh();
						ViewGroup v = (ViewGroup) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewGroup.ID);
						v.refresh();
					}
				}
			}
		};

		importFromLdap = new Action(Messages.Client_Views_ViewUser_23) {
			public void run() {
				DialogLdap dial = new DialogLdap(getSite().getShell(), DialogLdap.USERS);

				try {
					if (dial.open() == DialogLdap.OK) {

						for (Object s : dial.getValues()) {
							if (s instanceof User) {
								User g = (User) s;
								boolean exist = false;

								for (Object o : ((TreeParent) viewer.getInput()).getChildren()) {
									if (((TreeObject) o).getName().equals(g.getName())) {
										exist = true;
										break;
									}
								}
								if (!exist) {
									Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addUser(g);
									((TreeParent) viewer.getInput()).addChild(new TreeUser(g));
								}

							}

						}

						viewer.refresh();

					}
				} catch (Exception e) {
					e.printStackTrace();
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

				if (o instanceof TreeUser) {
					event.data = "USR/" + ((TreeUser) o).getUser().getId(); //$NON-NLS-1$
				}

			}

			public void dragFinished(DragSourceEvent event) {
				event.data = null;
			}
		});
	}

	private class UsersLoader extends Thread {
		private boolean shutDown = false;

		public void shutDown() {
			shutDown = true;
		}

		public void run() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					currentState.setText(Messages.Client_Views_ViewUser_24);
				}
			});
			while (!isUserLoaded) {

				try {
					List<User> l = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers(begin, step);
					begin += l.size();
					currentusers.addAll(l);
					isUserLoaded = l.size() == 0;

					TreeParent root = (TreeParent) viewer.getInput();
					for (User g : l) {
						root.addChild(new TreeUser(g));
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							viewer.refresh();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					final String message = e.getMessage();

					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openError(getSite().getShell(), Messages.Client_Views_ViewUser_25, message);
						}
					});
				}

			}
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					currentState.setText(Messages.Client_Views_ViewUser_26);
				}
			});
		}
	}
}
