package bpm.birep.admin.client.content.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.item.DialogLinkedDocument;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.viewers.GroupViewerFilter;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class LinkedDocumentsView extends ViewPart implements ISelectionListener {
	public final static String ID = "bpm.birep.admin.client.content.views.LinkedDocumentsView"; //$NON-NLS-1$
	private ProgressBar progressBar;
	protected Label currentState;
	private GroupViewerFilter groupFilter;
	private TableViewer tableViewer;
	private CheckboxTreeViewer viewerSecurity;
	private Text filterContent;
	private Button applyFilter;
	protected Object selectedObject;
	protected List<Group> currentgroups = new ArrayList<Group>();
	protected List<Group> currentAutorizedGroups = new ArrayList<Group>();
	private boolean isGroupLoaded = false;
	private boolean isAutorizedGroupLoaded = false;
	private GroupLoader groupLoader = new GroupLoader();
	private AuthorizedGroupLoader authorizedGroupLoader = new AuthorizedGroupLoader();
	protected ToolItem loadGroups, stopLoadGroups;

	private Object selection;

	public LinkedDocumentsView() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createToolbar(main);
		createLinkedDocumentsViewers(main);
		createGroupToolbar(main);
		createFilter(main);
		createSecurityViewer(main);

		currentState = new Label(main, SWT.NONE);
		currentState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

	}

	private void createToolbar(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		bar.setLayout(new GridLayout());

		ToolItem addDocument = new ToolItem(bar, SWT.PUSH);
		addDocument.setToolTipText(Messages.LinkedDocumentsView_1);
		addDocument.setImage(Activator.getDefault().getImageRegistry().get("documents")); //$NON-NLS-1$
		addDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!(selectedObject != null && selectedObject instanceof TreeItem))
					return;

				FileDialog fd = new FileDialog(getSite().getShell());
				String path = fd.open();

				if (path != null) {
					RepositoryItem it = ((TreeItem) selectedObject).getItem();
					DialogLinkedDocument dial = new DialogLinkedDocument(getSite().getShell(), it, new File(path));

					if (dial.open() == DialogLinkedDocument.OK) {
						refreshLinkedDocuments();
					}
				}

			}

		});

		ToolItem del = new ToolItem(bar, SWT.PUSH);
		del.setToolTipText(Messages.LinkedDocumentsView_3);
		del.setImage(Activator.getDefault().getImageRegistry().get("del")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!(selectedObject != null && selectedObject instanceof TreeItem))
					return;

				if (selection == null || !(selection instanceof LinkedDocument))
					return;

				LinkedDocument linked = (LinkedDocument) selection;

				try {
					Activator.getDefault().getRepositoryApi().getAdminService().removeLinkedDocument(linked);
					refreshLinkedDocuments();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.LinkedDocumentsView_5, e1.getMessage());
				}
			}
		});
	}

	protected void refreshLinkedDocuments() {
		tableViewer.setInput(new ArrayList<LinkedDocument>());
		RepositoryItem it = ((TreeItem) selectedObject).getItem();

		List<LinkedDocument> linked;
		try {
			linked = Activator.getDefault().getRepositoryApi().getAdminService().getLinkedDocuments(it.getId());
			tableViewer.setInput(linked);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getSite().getShell(), Messages.LinkedDocumentsView_6, e.getMessage());
		}

	}

	private void createSecurityViewer(Composite composite) {
		Label l = new Label(composite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.LinkedDocumentsView_7);

		viewerSecurity = new CheckboxTreeViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewerSecurity.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		viewerSecurity.setContentProvider(new ITreeContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getChildren(Object parentElement) {
				if (parentElement == currentgroups) {
					List<Group> l = new ArrayList<Group>((List<Group>) parentElement);

					List<Group> toRemove = new ArrayList<Group>();
					for (Group g : l) {
						if (g.getParentId() != null) {
							toRemove.add(g);
						}
					}
					l.removeAll(toRemove);

					return l.toArray(new Group[l.size()]);
				}
				else {
					List<Group> l = new ArrayList<Group>();
					for (Group g : currentgroups) {
						if (((Group) parentElement).getId().equals(g.getParentId())) {
							l.add(g);
						}
					}
					if (l.size() == 0) {
						return null;
					}
					return l.toArray(new Group[l.size()]);
				}

			}

			public Object getParent(Object element) {
				if (element instanceof Group && ((Group) element).getParentId() == null) {
					return currentgroups;
				}
				else {
					for (Group g : currentgroups) {
						if (element instanceof Group && g.getId().equals(((Group) element).getParentId())) {
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				Object[] p = getChildren(element);

				return p != null && p.length > 0;
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Group> l = new ArrayList<Group>((List<Group>) inputElement);

				List<Group> toRemove = new ArrayList<Group>();
				for (Group g : l) {
					if (g.getParentId() != null) {
						toRemove.add(g);
					}
				}
				l.removeAll(toRemove);

				return l.toArray(new Group[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		viewerSecurity.setLabelProvider(new LabelProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof Group) {
					return ((Group) element).getName();
				}
				return ""; //$NON-NLS-1$
			}

		});

		viewerSecurity.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {

				if (selection == null) {
					return;
				}
				IRepositoryApi api = Activator.getDefault().getRepositoryApi();

				if (event.getChecked()) {
					try {
						api.getAdminService().addGroupForLinkedDocument((Group) event.getElement(), (LinkedDocument) selection);
					} catch (Exception e) {
						e.printStackTrace();

					}

				}
				else {
					try {
						api.getAdminService().removeGroupForLinkedDocument((Group) event.getElement(), (LinkedDocument) selection);
					} catch (Exception e) {
						e.printStackTrace();

					}

				}

			}

		});
		viewerSecurity.setInput(currentgroups);

	}

	private void createLinkedDocumentsViewers(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.LinkedDocumentsView_9);

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<LinkedDocument> l = (List<LinkedDocument>) inputElement;
				return l.toArray(new LinkedDocument[l.size()]);
			}

			@Override
			public void dispose() {

			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		tableViewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
					case 0:
						return ((LinkedDocument) element).getName();
					case 1:
						if (((LinkedDocument) element).getComment() != null)
							return ((LinkedDocument) element).getComment();
						else
							return ""; //$NON-NLS-1$
					case 2:
						return ((LinkedDocument) element).getFormat();
					case 3:
						return ((LinkedDocument) element).getVersion() + ""; //$NON-NLS-1$

				}
				return ""; //$NON-NLS-1$
			}

			@Override
			public void addListener(ILabelProviderListener listener) {

			}

			@Override
			public void dispose() {

			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {

			}

		});

		TableColumn name = new TableColumn(tableViewer.getTable(), SWT.NONE);
		name.setText(Messages.LinkedDocumentsView_13);
		name.setWidth(200);

		TableColumn comment = new TableColumn(tableViewer.getTable(), SWT.NONE);
		comment.setText(Messages.LinkedDocumentsView_14);
		comment.setWidth(250);

		TableColumn format = new TableColumn(tableViewer.getTable(), SWT.NONE);
		format.setText(Messages.LinkedDocumentsView_15);
		format.setWidth(80);

		TableColumn version = new TableColumn(tableViewer.getTable(), SWT.NONE);
		version.setText(Messages.LinkedDocumentsView_16);
		version.setWidth(80);

		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				selection = ((IStructuredSelection) event.getSelection()).getFirstElement();

				if (selection != null) {
					LinkedDocument doc = (LinkedDocument) selection;
					try {
						viewerSecurity.setInput(new ArrayList<Group>());
						viewerSecurity.setInput(currentgroups);
						if (doc != null) {

							List<Group> autorized = Activator.getDefault().getRepositoryApi().getAdminService().getAuthorizedGroupsForLinkedDocument(doc.getId());
							for (Group i : currentgroups) {
								for (Group g : autorized) {
									Group _i = i;
									if (g.getId().equals(i.getId())) {
										viewerSecurity.setChecked(_i, true);
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.LinkedDocumentsView_17, e.getMessage());

					}
				}

			}
		});

		// createContextMenu();

	}

	private void createFilter(Composite parent) {
		Composite filterBar = new Composite(parent, SWT.NONE);
		filterBar.setLayout(new GridLayout(2, false));
		filterBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		applyFilter = new Button(filterBar, SWT.TOGGLE);
		applyFilter.setToolTipText(Messages.LinkedDocumentsView_18);
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
							viewerSecurity.addFilter(groupFilter);
						}
						else {
							viewerSecurity.removeFilter(groupFilter);
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
							viewerSecurity.refresh();
						}
					}
				});
			}
		});

	}

	private void createGroupToolbar(Composite parent) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		bar.setLayout(new GridLayout());

		loadGroups = new ToolItem(bar, SWT.PUSH);
		loadGroups.setToolTipText(Messages.LinkedDocumentsView_20);
		loadGroups.setImage(reg.get("long_load")); //$NON-NLS-1$
		loadGroups.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				isGroupLoaded = false;
				groupLoader = new GroupLoader();
				currentgroups.clear();
				loadGroups.setEnabled(false);
				stopLoadGroups.setEnabled(true);
				groupLoader.start();

				viewerSecurity.refresh();

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
									viewerSecurity.refresh();
								}

							});

						}
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								loadGroups.setEnabled(true);
								stopLoadGroups.setEnabled(false);
							}
						});

					}
				};

				t.start();
			}

		});
		loadGroups.setEnabled(false);

		stopLoadGroups = new ToolItem(bar, SWT.PUSH);
		stopLoadGroups.setToolTipText(Messages.LinkedDocumentsView_22);
		stopLoadGroups.setImage(reg.get("stop_long_load")); //$NON-NLS-1$
		stopLoadGroups.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				loadGroups.setEnabled(false);
				stopLoadGroups.setEnabled(false);

				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						currentState.setText(Messages.LinkedDocumentsView_24);
						groupLoader.performStop();
						try {
							groupLoader.join();
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
						loadGroups.setEnabled(true);
						currentState.setText(Messages.LinkedDocumentsView_25);
					}
				});

			}

		});
		stopLoadGroups.setEnabled(false);

		progressBar = new ProgressBar(parent, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

	}

	@Override
	public void setFocus() {

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		StructuredSelection ss = (StructuredSelection) selection;

		currentgroups.clear();
		viewerSecurity.setInput(currentgroups);

		if (ss.isEmpty()) {
			selectedObject = null;

			return;
		}
		else {
			selectedObject = ss.getFirstElement();
		}

		if (selectedObject instanceof TreeItem) {
			RepositoryItem it = ((TreeItem) selectedObject).getItem();
			loadGroups.setEnabled(true);
			stopLoadGroups.setEnabled(false);
			try {
				initGroups();

				List<LinkedDocument> linked = Activator.getDefault().getRepositoryApi().getAdminService().getLinkedDocuments(it.getId());
				tableViewer.setInput(linked);
			} catch (Exception e) {
				e.printStackTrace();
				tableViewer.setInput(new ArrayList<LinkedDocument>());
			}
		}

	}

	private void initGroups() throws Exception {
		int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);
		final List<Group> l = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(0, step);
		currentgroups.addAll(l);
		isGroupLoaded = l.size() == 0;

		viewerSecurity.refresh();
		currentState.setText("Only " + step + " firsts Groups loaded. Use the Load Button to get all."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private class GroupLoader extends Thread {
		private boolean killThread = false;
		private int beginAt = 0;

		public GroupLoader() {
			this.setName(Messages.LinkedDocumentsView_28);
		}

		public void run() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					currentState.setText(Messages.LinkedDocumentsView_29);
				}
			});

			while (!killThread && !isGroupLoaded && selectedObject != null) {

				List<Group> l = null;
				try {
					l = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(beginAt, 20);
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
				beginAt += l.size();
				currentgroups.addAll(l);
				isGroupLoaded = l.size() == 0;

				if (killThread) {
					return;
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
					if (isGroupLoaded) {
						currentState.setText(Messages.LinkedDocumentsView_30);
						viewerSecurity.setInput(currentgroups);

						isAutorizedGroupLoaded = false;
						authorizedGroupLoader = new AuthorizedGroupLoader();
						currentAutorizedGroups.clear();
						loadGroups.setEnabled(false);
						stopLoadGroups.setEnabled(true);
						authorizedGroupLoader.start();

						viewerSecurity.refresh();

						Thread t = new Thread() {
							public void run() {
								while (authorizedGroupLoader.isAlive()) {
									try {
										sleep(3000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											viewerSecurity.refresh();
										}

									});

								}

							}
						};

						t.start();
					}

				}
			});
		}

		public void performStop() {
			killThread = true;
		}
	}

	private class AuthorizedGroupLoader extends Thread {
		private boolean killThread = false;
		private int beginAt = 0;

		public AuthorizedGroupLoader() {
			this.setName(Messages.LinkedDocumentsView_31);
		}

		public void run() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					currentState.setText(Messages.LinkedDocumentsView_32);
				}
			});

			while (!killThread && !isAutorizedGroupLoaded && selection != null) {
				List<Group> l = null;
				try {

					l = Activator.getDefault().getRepositoryApi().getAdminService().getAuthorizedGroupsForLinkedDocument(((LinkedDocument) selection).getId());
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
				beginAt += l.size();
				currentAutorizedGroups.addAll(l);
				isAutorizedGroupLoaded = l.size() == 0;

				if (killThread) {
					return;
				}

				if (isAutorizedGroupLoaded) {
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
					if (isAutorizedGroupLoaded) {
						currentState.setText(Messages.LinkedDocumentsView_33);

						for (Group i : currentgroups) {
							for (Group g : currentAutorizedGroups) {
								final Group _i = i;
								if (g.getId().equals(i.getId())) {
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											viewerSecurity.setChecked(_i, true);
										}
									});

								}
							}

						}
					}

				}
			});
		}
	}

}
