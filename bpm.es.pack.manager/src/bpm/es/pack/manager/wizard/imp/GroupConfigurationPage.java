package bpm.es.pack.manager.wizard.imp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import adminbirep.Activator;
import adminbirep.icons.Icons;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.utils.ItemImage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class GroupConfigurationPage extends WizardPage {

	private TreeViewer content;

	private Composite groupPart;

	private CheckboxTreeViewer groups;
	private CheckboxTreeViewer runnableGrps;
	private Button btnCheckReplaceOld;

	private ToolBar toolbarRunnableGroup;

	private VanillaPackage vanillaPackage;

	protected Object selectedObject;
	protected List<Group> currentgroups = new ArrayList<Group>();
	private boolean isGroupLoaded = false;

	private HashMap<Integer, Group> newGroupIds;

	protected GroupConfigurationPage(String pageName, VanillaPackage vanillaPackage) {
		super(pageName);
		this.vanillaPackage = vanillaPackage;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		createReplaceOldCheck(main);
		
		createToolbar(main);
		createTree(main);

		groupPart = new Composite(main, SWT.NONE);
		groupPart.setLayout(new GridLayout());
		groupPart.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		groupPart.setVisible(false);

		buildGroupPart(groupPart);

		setControl(main);
		refreshInput();
		try {
			initGroups();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		runnableGrps.setInput(currentgroups);
		groups.setInput(currentgroups);
	}
	
	private void createReplaceOldCheck(Composite parent) {
		btnCheckReplaceOld = new Button(parent, SWT.CHECK);
		btnCheckReplaceOld.setText(bpm.es.pack.manager.I18N.Messages.GroupConfigurationPage_4);
	}

	private void createToolbar(Composite parent) {
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		toolbar.setLayout(new GridLayout(2, false));

		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setText(Messages.GroupConfigurationPage_0);
		checkAll.setToolTipText(Messages.GroupConfigurationPage_1);
		checkAll.setImage(Activator.getDefault().getImageRegistry().get(Icons.CHECK));
		checkAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<PlaceImportDirectory> rootDirs = vanillaPackage.getDirectories();
				checkAllDirectory(rootDirs);

				List<PlaceImportItem> rootItems = vanillaPackage.getItems();
				checkAllItem(rootItems);

				selectedObject = null;
				refreshInput();

				groupPart.setVisible(false);
			}
		});

		ToolItem unckeckAll = new ToolItem(toolbar, SWT.PUSH);
		unckeckAll.setText(Messages.GroupConfigurationPage_2);
		unckeckAll.setToolTipText(Messages.GroupConfigurationPage_3);
		unckeckAll.setImage(Activator.getDefault().getImageRegistry().get(Icons.NO_CHECK));
		unckeckAll.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<PlaceImportDirectory> rootDirs = vanillaPackage.getDirectories();
				unCheckAllDirectory(rootDirs);

				List<PlaceImportItem> rootItems = vanillaPackage.getItems();
				unCheckAllItem(rootItems);

				selectedObject = null;
				refreshInput();

				groupPart.setVisible(false);
			}
		});
	}

	private void unCheckAllDirectory(List<PlaceImportDirectory> dirs) {
		if (dirs != null) {
			for (PlaceImportDirectory dir : dirs) {
				dir.removeAllAvailableGroupId();

				unCheckAllDirectory(dir.getChildsDir());
				unCheckAllItem(dir.getChildsItems());
			}
		}
	}

	private void unCheckAllItem(List<PlaceImportItem> items) {
		if (items != null) {
			for (PlaceImportItem item : items) {
				selectedObject = item;

				item.removeAllAvailableGroupId();

				if (isSelectedItemReport()) {
					item.removeAllRunnableGroupId();
				}
			}
		}
	}

	private void checkAllDirectory(List<PlaceImportDirectory> dirs) {
		if (dirs != null) {
			for (PlaceImportDirectory dir : dirs) {
				dir.removeAllAvailableGroupId();
				for (Group gr : currentgroups) {
					dir.addAvailableGroupId(gr.getId());
				}

				checkAllDirectory(dir.getChildsDir());
				checkAllItem(dir.getChildsItems());
			}
		}
	}

	private void checkAllItem(List<PlaceImportItem> items) {
		if (items != null) {
			for (PlaceImportItem item : items) {
				selectedObject = item;

				item.removeAllAvailableGroupId();
				for (Group gr : currentgroups) {
					item.addAvailableGroupId(gr.getId());
				}

				if (isSelectedItemReport()) {
					item.removeAllRunnableGroupId();
					for (Group gr : currentgroups) {
						item.addRunnableGroupId(gr.getId());
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void refreshInput() {
		List col = new ArrayList();
		col.addAll(vanillaPackage.getDirectories() != null ? vanillaPackage.getDirectories() : new ArrayList<PlaceImportDirectory>());
		col.addAll(vanillaPackage.getItems() != null ? vanillaPackage.getItems() : new ArrayList<PlaceImportItem>());
		content.setInput(col);
	}

	private void createTree(Composite parent) {
		Label lblContent = new Label(parent, SWT.NONE);
		lblContent.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));
		lblContent.setText(Messages.GroupConfigurationPage_5);
		
		content = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		content.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 6));
		content.setContentProvider(new ITreeContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				Collection c = ((Collection) inputElement);
				return c.toArray(new Object[c.size()]);
			}

			public boolean hasChildren(Object element) {
				if (element instanceof PlaceImportDirectory) {
					boolean hasChild = false;
					hasChild = !((PlaceImportDirectory) element).getChildsDir().isEmpty();
					if (hasChild) {
						return hasChild;
					}
					else {
						return !((PlaceImportDirectory) element).getChildsItems().isEmpty();
					}
				}
				return false;
			}

			@SuppressWarnings("unchecked")
			public Object[] getChildren(Object parentElement) {
				List l = new ArrayList();
				l.addAll(((PlaceImportDirectory) parentElement).getChildsDir());
				l.addAll(((PlaceImportDirectory) parentElement).getChildsItems());
				return l.toArray(new Object[l.size()]);
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}
		});
		content.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();

				if (ss.isEmpty()) {
					selectedObject = null;

					toolbarRunnableGroup.setEnabled(false);
					runnableGrps.getControl().setEnabled(false);
					return;
				}

				groupPart.setVisible(true);

				update(ss.getFirstElement());
			}
		});
		content.setLabelProvider(new TreeLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PlaceImportItem) {
					return ((PlaceImportItem) element).getItem().getItemName();
				}
				else if (element instanceof PlaceImportDirectory) {
					return ((PlaceImportDirectory) element).getName();
				}
				return ""; //$NON-NLS-1$
			}

			@Override
			public Image getImage(Object element) {
				ImageRegistry reg = Activator.getDefault().getImageRegistry();
				if (element instanceof PlaceImportItem) {
					int type = ((PlaceImportItem) element).getItem().getType();
					return reg.get(ItemImage.getKeyForType(type, ((PlaceImportItem) element).getItem()));
				}
				else if (element instanceof PlaceImportDirectory) {
					return reg.get(Icons.FOLDER);
				}
				return null; //$NON-NLS-1$
			}
		});
	}

	private void buildGroupPart(Composite parent) {
		Label lblGroup = new Label(parent, SWT.NONE);
		lblGroup.setText(Messages.GroupConfigurationPage_6);

		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.GroupConfigurationPage_7);
		it.setImage(Activator.getDefault().getImageRegistry().get(Icons.CHECK));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					List l = ((List) groups.getInput());
					groups.setCheckedElements(l.toArray(new Object[l.size()]));
				} catch (Exception ex) {

				}

			}
		});

		it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.GroupConfigurationPage_8);
		it.setImage(Activator.getDefault().getImageRegistry().get(Icons.NO_CHECK));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					groups.setCheckedElements(new Object[] {});
				} catch (Exception ex) {

				}

			}
		});

		groups = new CheckboxTreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.BORDER);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		groups.setContentProvider(new ITreeContentProvider() {

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
						if (g.getId().equals(((Group) element).getParentId())) {
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				for (Group g : currentgroups) {
					if (((Group) element).getId().equals(g.getParentId())) {
						return true;
					}
				}
				return false;
			}

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
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}

		});
		groups.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				Integer groupId = ((Group) event.getElement()).getId();
				if (event.getChecked()) {
					if (selectedObject instanceof PlaceImportDirectory) {
						((PlaceImportDirectory) selectedObject).addAvailableGroupId(groupId);
					}
					else if (selectedObject instanceof PlaceImportItem) {
						((PlaceImportItem) selectedObject).addAvailableGroupId(groupId);
					}
				}
				else {
					if (selectedObject instanceof PlaceImportDirectory) {
						((PlaceImportDirectory) selectedObject).removeAvailableGroupId(groupId);
					}
					else if (selectedObject instanceof PlaceImportItem) {
						((PlaceImportItem) selectedObject).removeAvailableGroupId(groupId);
					}
				}
			}

		});
		

		Label lblRunGroup = new Label(parent, SWT.NONE);
		lblRunGroup.setText(Messages.GroupConfigurationPage_9);

		toolbarRunnableGroup = new ToolBar(parent, SWT.NONE);
		toolbarRunnableGroup.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		it = new ToolItem(toolbarRunnableGroup, SWT.PUSH);
		it.setToolTipText(Messages.GroupConfigurationPage_10);
		it.setImage(Activator.getDefault().getImageRegistry().get(Icons.CHECK));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {

					List l = ((List) runnableGrps.getInput());
					runnableGrps.setCheckedElements(l.toArray(new Object[l.size()]));
				} catch (Exception ex) {

				}

			}
		});

		it = new ToolItem(toolbarRunnableGroup, SWT.PUSH);
		it.setToolTipText(Messages.GroupConfigurationPage_11);
		it.setImage(Activator.getDefault().getImageRegistry().get(Icons.NO_CHECK));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					runnableGrps.setCheckedElements(new Object[] {});
				} catch (Exception ex) {

				}

			}
		});

		runnableGrps = new CheckboxTreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.BORDER);
		runnableGrps.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		runnableGrps.setContentProvider(new ITreeContentProvider() {

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
						if (g.getId().equals(((Group) element).getParentId())) {
							return g;
						}
					}
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				for (Group g : currentgroups) {
					if (((Group) element).getId().equals(g.getParentId())) {
						return true;
					}
				}
				return false;
			}

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
		runnableGrps.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}

		});
		runnableGrps.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				Integer groupId = ((Group) event.getElement()).getId();
				if (event.getChecked()) {
					if (selectedObject instanceof PlaceImportItem) {
						((PlaceImportItem) selectedObject).addRunnableGroupId(groupId);
					}
				}
				else {
					if (selectedObject instanceof PlaceImportItem) {
						((PlaceImportItem) selectedObject).removeRunnableGroupId(groupId);
					}
				}

			}

		});
		

		
	}

	protected void initGroups() throws Exception {
		int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);
		final List<Group> l = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(0, step);

		currentgroups.addAll(l);

		if (selectedObject != null) {

			try {
				for (Group i : l) {
					List<Group> authorizedGroups = new ArrayList<Group>();
					List<Group> runnableGroups = new ArrayList<Group>();
					if (selectedObject instanceof PlaceImportItem) {
						for (Integer grId : ((PlaceImportItem) selectedObject).getAvailableGroupsId()) {
							for (Group gr : currentgroups) {
								if (grId.equals(gr.getId())) {
									authorizedGroups.add(gr);
									break;
								}
							}
						}

						for (Integer grId : ((PlaceImportItem) selectedObject).getRunnableGroupsId()) {
							for (Group gr : currentgroups) {
								if (grId.equals(gr.getId())) {
									runnableGroups.add(gr);
									break;
								}
							}
						}
					}
					else if (selectedObject instanceof PlaceImportDirectory) {
						for (Integer grId : ((PlaceImportDirectory) selectedObject).getAvailableGroupsId()) {
							for (Group gr : currentgroups) {
								if (grId.equals(gr.getId())) {
									authorizedGroups.add(gr);
									break;
								}
							}
						}
					}

					for (Group g : authorizedGroups) {
						final Group _i = i;
						if (g.getId().equals(i.getId())) {
							groups.setChecked(_i, true);
						}
					}

					for (Group g : runnableGroups) {
						final Group _i = i;
						if (g.getId().equals(i.getId())) {
							runnableGrps.setChecked(_i, true);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.Client_Views_GroupManagementView_48, e.getMessage());

			}
		}
		groups.refresh();
		if (isSelectedItemReport()) {
			runnableGrps.refresh();
		}

//		currentState.setText("Only " + step + " firsts Groups loaded. Use the Load Button to get all."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private boolean isSelectedItemReport() {
		if (selectedObject != null && selectedObject instanceof PlaceImportItem) {
			PlaceImportItem item = (PlaceImportItem) selectedObject;
			if (item.getItem().getType() == IRepositoryApi.FD_TYPE || item.getItem().getType() == IRepositoryApi.FWR_TYPE || (item.getItem().getType() == IRepositoryApi.CUST_TYPE)) {
				return true;
			}
		}
		return false;
	}

	private void update(Object object) {
		currentgroups = new ArrayList<Group>();
		groups.setInput(currentgroups);
		try {
			runnableGrps.setInput(currentgroups);
//			currentState.setText(""); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (object instanceof PlaceImportItem) {
			selectedObject = object;
//			btnLoadGroups.setEnabled(true);
//			btnStopLoadGroups.setEnabled(false);

			if (isRunnable()) {
				toolbarRunnableGroup.setEnabled(true);
				runnableGrps.getControl().setEnabled(true);
			}
			else {
				toolbarRunnableGroup.setEnabled(false);
				runnableGrps.getControl().setEnabled(false);
			}
		}
		else if (object instanceof PlaceImportDirectory) {
			selectedObject = object;
//			btnLoadGroups.setEnabled(true);
//			btnStopLoadGroups.setEnabled(false);
			toolbarRunnableGroup.setEnabled(false);
			runnableGrps.getControl().setEnabled(false);
		}
		else {
			selectedObject = null;
//			btnLoadGroups.setEnabled(false);
//			btnStopLoadGroups.setEnabled(false);
			toolbarRunnableGroup.setEnabled(false);
			runnableGrps.getControl().setEnabled(false);
			return;
		}
		try {
			initGroups();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isRunnable() {
		if (selectedObject != null && selectedObject instanceof PlaceImportItem) {
			PlaceImportItem item = (PlaceImportItem) selectedObject;
			if (item.getItem().getType() == IRepositoryApi.FD_TYPE 
					|| item.getItem().getType() == IRepositoryApi.FWR_TYPE 
					|| (item.getItem().getType() == IRepositoryApi.CUST_TYPE)
					|| (item.getItem().getType() == IRepositoryApi.BIW_TYPE)
					|| (item.getItem().getType() == IRepositoryApi.EXTERNAL_DOCUMENT)
					|| (item.getItem().getType() == IRepositoryApi.FASD_TYPE)
					|| (item.getItem().getType() == IRepositoryApi.FAV_TYPE)
					|| (item.getItem().getType() == IRepositoryApi.GTW_TYPE)) {
				return true;
			}
		}
		return false;
	}

	public boolean replaceOld(){
		return btnCheckReplaceOld.getSelection();
	}

	public class GroupLoader extends Thread {

		private boolean killThread = false;
		private int beginAt = 0;

		public GroupLoader() {
			this.setName(Messages.Client_Views_GroupManagementView_17);
		}

		public void reInit() {
			beginAt = 0;
		}

		public void run() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
//					currentState.setText(Messages.Client_Views_GroupManagementView_18);
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

				if (selectedObject != null) {
					try {
						for (Group i : l) {
							if (killThread) {
								return;
							}
							List<Group> authorizedGroups = new ArrayList<Group>();
							List<Group> runnableGroups = new ArrayList<Group>();
							if (selectedObject instanceof PlaceImportItem) {
								for (Integer grId : ((PlaceImportItem) selectedObject).getAvailableGroupsId()) {
									for (Group gr : currentgroups) {
										if (grId.equals(gr.getId())) {
											authorizedGroups.add(gr);
											break;
										}
									}
								}

								for (Integer grId : ((PlaceImportItem) selectedObject).getRunnableGroupsId()) {
									for (Group gr : currentgroups) {
										if (grId.equals(gr.getId())) {
											runnableGroups.add(gr);
											break;
										}
									}
								}
							}
							else if (selectedObject instanceof PlaceImportDirectory) {
								for (Integer grId : ((PlaceImportDirectory) selectedObject).getAvailableGroupsId()) {
									for (Group gr : currentgroups) {
										if (grId.equals(gr.getId())) {
											authorizedGroups.add(gr);
											break;
										}
									}
								}
							}

							for (Group g : authorizedGroups) {
								if (killThread) {
									return;
								}
								final Group _i = i;
								if (g.getId().equals(i.getId())) {
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											groups.setChecked(_i, true);

										}
									});
								}
							}

							for (Group g : runnableGroups) {
								if (killThread) {
									return;
								}
								final Group _i = i;
								if (g.getId().equals(i.getId())) {
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											runnableGrps.setChecked(_i, true);
										}
									});
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(getShell(), Messages.Client_Views_GroupManagementView_19, e.getMessage());

					}
				}

				if (isGroupLoaded) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
//							int pbMax = progressBar.getMaximum();
//							progressBar.setSelection(pbMax);

						}
					});
				}
				else {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
//							int pbMax = progressBar.getMaximum();
//							progressBar.setSelection((progressBar.getSelection() + 10) % pbMax);
						}
					});
				}

			}

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (isGroupLoaded) {
//						currentState.setText(Messages.Client_Views_GroupManagementView_20);
					}
				}
			});
		}

		public void performStop() {
			killThread = true;
		}
	}

	public void setGroupMap(HashMap<Integer, Group> newGroupIds) {
		this.newGroupIds = newGroupIds;
		replaceOldGroupIds(vanillaPackage.getDirectories(), vanillaPackage.getItems());
	}
	
	private void replaceOldGroupIds(List<PlaceImportDirectory> dirs, List<PlaceImportItem> items) {
		for(PlaceImportDirectory dir : dirs) {
			List<Integer> toRm = new ArrayList<Integer>();
			List<Integer> toAdd = new ArrayList<Integer>();
			for(Integer i : dir.getAvailableGroupsId()) {
				if(newGroupIds.get(i) != null && newGroupIds.get(i).getId().intValue() != i.intValue()) {
					toAdd.add(newGroupIds.get(i).getId());
					toRm.add(i);
				}
				
			}
			toRm.removeAll(toAdd);
			dir.getAvailableGroupsId().remove(toRm);
			dir.getAvailableGroupsId().addAll(toAdd);
			
			replaceOldGroupIds(dir.getChildsDir(), dir.getChildsItems());
		}
		for(PlaceImportItem item : items) {
			List<Integer> toRm = new ArrayList<Integer>();
			List<Integer> toAdd = new ArrayList<Integer>();
			for(Integer i : item.getAvailableGroupsId()) {
				if(newGroupIds.get(i) != null && newGroupIds.get(i).getId().intValue() != i.intValue()) {
					toAdd.add(newGroupIds.get(i).getId());
					toRm.add(i);
				}
				
			}
			toRm.removeAll(toAdd);
			item.getAvailableGroupsId().remove(toRm);
			item.getAvailableGroupsId().addAll(toAdd);
			
			toRm = new ArrayList<Integer>();
			toAdd = new ArrayList<Integer>();
			for(Integer i : item.getRunnableGroupsId()) {
				if(newGroupIds.get(i) != null && newGroupIds.get(i).getId().intValue() != i.intValue()) {
					toAdd.add(newGroupIds.get(i).getId());
					toRm.add(i);
				}
				
			}
			toRm.removeAll(toAdd);
			item.getRunnableGroupsId().remove(toRm);
			item.getRunnableGroupsId().addAll(toAdd);
		}
	}
}
