package bpm.birep.admin.client.historic;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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
import adminbirep.icons.Icons;
import bpm.birep.admin.client.preferences.PreferenceConstants;
import bpm.birep.admin.client.preferences.TableViewerRowColor;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.viewers.GroupViewerFilter;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.SecurityProperties;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;

public class SecurityManager extends ViewPart implements ISelectionListener {
	public static final String ID = "bpm.es.gedmanager.views.securitymanager"; //$NON-NLS-1$

	//XXX : should be a tableViewer but it is bugged!!!
	private CheckboxTreeViewer viewerSecurity;
	private org.eclipse.swt.widgets.Group grOptionHistoric;

	private TableViewer tableViewer;
	private GroupViewerFilter groupFilter;
	private Object selectedObject;

	private Text filterContent;
	private Button applyFilter;
	private ToolItem loadGroups, stopLoadGroups;
	private ProgressBar progressBar;
	private Label currentState;
	private GroupLoader groupLoader = new GroupLoader();

	private ReportHistoricComponent historicComponent;

	private Button availableGedButton;
	private Button realtimeGedButton;
	private Button createEntry;

	public SecurityManager() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);

		historicComponent = new RemoteHistoricReportComponent(Activator.getDefault().getRepositoryApi().getContext().getVanillaContext());
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

	private void createToolbar(Composite parent) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		ToolBar bar = new ToolBar(parent, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		bar.setLayout(new GridLayout());

		loadGroups = new ToolItem(bar, SWT.PUSH);
		loadGroups.setToolTipText(Messages.SecurityManager_0);
		loadGroups.setImage(reg.get(Icons.LONG_LOAD));
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
		stopLoadGroups.setToolTipText(Messages.SecurityManager_1);
		stopLoadGroups.setImage(reg.get(Icons.STOP_LONG_LOAD));
		stopLoadGroups.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				loadGroups.setEnabled(false);
				stopLoadGroups.setEnabled(false);

				BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
					public void run() {
						currentState.setText(Messages.SecurityManager_3);
						groupLoader.performStop();
						try {
							groupLoader.join();
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
						loadGroups.setEnabled(true);
						currentState.setText(Messages.SecurityManager_9);
					}
				});

			}

		});
		stopLoadGroups.setEnabled(false);

		progressBar = new ProgressBar(parent, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		org.eclipse.swt.widgets.Group compGedOptions = new org.eclipse.swt.widgets.Group(composite, SWT.NONE);
		compGedOptions.setLayout(new GridLayout(4, false));
		compGedOptions.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		compGedOptions.setText(Messages.CompositeItem_11);
		
		Label _l51 = new Label(compGedOptions, SWT.NONE);
		_l51.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		_l51.setText(Messages.CompositeItem_12);
		
		availableGedButton = new Button(compGedOptions, SWT.CHECK);
		availableGedButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label _l52 = new Label(compGedOptions, SWT.NONE);
		_l52.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		_l52.setText(Messages.CompositeItem_13);
		
		realtimeGedButton = new Button(compGedOptions, SWT.CHECK);
		realtimeGedButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	
		Label ll = new Label(compGedOptions, SWT.NONE);
		ll.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		ll.setText(Messages.CompositeItem_14);
		
		createEntry = new Button(compGedOptions, SWT.CHECK);
		createEntry.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		grOptionHistoric = new org.eclipse.swt.widgets.Group(composite, SWT.NONE);
		grOptionHistoric.setLayout(new GridLayout(2, false));
		grOptionHistoric.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		grOptionHistoric.setText(Messages.SecurityManager_8);
		
		createToolbar(grOptionHistoric);
		createFilter(grOptionHistoric);

		Label l = new Label(grOptionHistoric, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.SecurityManager_5);

		viewerSecurity = new CheckboxTreeViewer(grOptionHistoric, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
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
						if (g.getId().equals(((Group) element).getParentId())) {
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

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

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
		viewerSecurity.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewerSecurity.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {

				if (selectedObject == null) {
					return;
				}
				try {
					if (event.getChecked()) {
						historicComponent.grantHistoricAccess(((Group) event.getElement()).getId(), ((TreeItem) selectedObject).getItem().getId(), Activator.getDefault().getCurrentRepository().getId());
					}
					else {
						historicComponent.removeHistoricAccess(((Group) event.getElement()).getId(), ((TreeItem) selectedObject).getItem().getId(), Activator.getDefault().getCurrentRepository().getId());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		viewerSecurity.setInput(currentgroups);
		createTableHistoric(grOptionHistoric);

		currentState = new Label(grOptionHistoric, SWT.NONE);
		currentState.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
	}

	private void createTableHistoric(Composite parent) {

		Label l = new Label(grOptionHistoric, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.SecurityManager_12);

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new TableViewerRowColor());

		TableColumn name = new TableColumn(tableViewer.getTable(), SWT.NONE);
		name.setText(Messages.SecurityManager_14);
		name.setWidth(150);

		TableColumn format = new TableColumn(tableViewer.getTable(), SWT.NONE);
		format.setText(Messages.SecurityManager_15);
		format.setWidth(150);

		TableColumn date = new TableColumn(tableViewer.getTable(), SWT.NONE);
		date.setText(Messages.SecurityManager_16);
		date.setWidth(150);

		TableColumn owner = new TableColumn(tableViewer.getTable(), SWT.NONE);
		owner.setText(Messages.SecurityManager_161);
		owner.setWidth(150);

		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		createContextMenu();
	}

	private void createFilter(Composite parent) {
		Composite filterBar = new Composite(parent, SWT.NONE);
		filterBar.setLayout(new GridLayout(2, false));
		filterBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		applyFilter = new Button(filterBar, SWT.TOGGLE);
		applyFilter.setToolTipText(Messages.SecurityManager_17);
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		applyFilter.setImage(reg.get(Icons.FILTER));
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

	private void createContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				IStructuredSelection ss = (IStructuredSelection) tableViewer.getSelection();
				final Object o = ss.getFirstElement();

				if (o instanceof DocumentVersion) {
					menuMgr.add(new Action(Messages.SecurityManager_18) {

						@SuppressWarnings("unchecked")
						public void run() {
							try {
								historicComponent.removeDocumentVersion((DocumentVersion)o);
								((List<DocumentVersion>) tableViewer.getInput()).remove(o);

								tableViewer.refresh();
							} catch (Exception e) {
								e.printStackTrace();
								MessageDialog.openError(getSite().getShell(), Messages.SecurityManager_19, e.getMessage());
							}
						}
					});
					menuMgr.add(new Action(Messages.SecurityManager_30) {
						public void run() {
							try {

								IObjectIdentifier identifier = new ObjectIdentifier(Activator.getDefault().getCurrentRepository().getId(), ((TreeItem) selectedObject).getItem().getId());
								List<Integer> lstGroups = historicComponent.getGroupsAuthorizedByItemId(identifier);
								
								if(lstGroups != null) {
									List<SecuredCommentObject> secs = new ArrayList<SecuredCommentObject>();
									for(Integer grId : lstGroups) {
										SecuredCommentObject secObject = new SecuredCommentObject();
										secObject.setGroupId(grId);
										secObject.setObjectId(((DocumentVersion) o).getId());
										secObject.setType(Comment.DOCUMENT_VERSION);
										secs.add(secObject);
									}
									Activator.getDefault().getRepositoryApi().getDocumentationService().addSecuredCommentObjects(secs);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					});
					menuMgr.add(new Action(Messages.SecurityManager_31) {
						public void run() {
							try {
								Activator.getDefault().getRepositoryApi().getDocumentationService().removeSecuredCommentObjects(((DocumentVersion)o).getId(), Comment.DOCUMENT_VERSION);
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});

		tableViewer.getControl().setMenu(menuMgr.createContextMenu(tableViewer.getControl()));
	}

	@Override
	public void setFocus() {}

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
			if (it.isReport()) {
				availableGedButton.setEnabled(true);
				realtimeGedButton.setEnabled(true);
				createEntry.setEnabled(true);
				tableViewer.getTable().setEnabled(true);
				
				if (it.isAvailableGed()) {
					availableGedButton.setSelection(true);
					if (it.isRealtimeGed()) {
						realtimeGedButton.setSelection(true);

						createEntry.setSelection(it.isCreateEntry());
					}
					else {
						realtimeGedButton.setSelection(false);
						
						createEntry.setEnabled(false);
					}
				}
				else {
					availableGedButton.setSelection(false);
					realtimeGedButton.setEnabled(false);
					createEntry.setEnabled(false);
				}

				availableGedButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						realtimeGedButton.setEnabled(availableGedButton.getSelection());
						if (!availableGedButton.getSelection()) {
							realtimeGedButton.setSelection(false);
							createEntry.setSelection(false);
						}
						createEntry.setEnabled(realtimeGedButton.getSelection());
						
						if(selectedObject != null && ((TreeItem) selectedObject).getItem() != null
								&&((TreeItem) selectedObject).getItem() instanceof RepositoryItem){
							
							RepositoryItem item = ((TreeItem) selectedObject).getItem();
							item.setRealtimeGed(realtimeGedButton.getSelection());
							item.setAvailableGed(availableGedButton.getSelection());
							item.setCreateEntry(createEntry.getSelection());
							
							try {
								Activator.getDefault().getRepositoryApi().getAdminService().update(item);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				});

				realtimeGedButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						createEntry.setEnabled(realtimeGedButton.getSelection());
						if (!realtimeGedButton.getSelection()) {
							createEntry.setSelection(false);
						}
						
						if(selectedObject != null && ((TreeItem) selectedObject).getItem() != null
								&&((TreeItem) selectedObject).getItem() instanceof RepositoryItem){
							
							RepositoryItem item = ((TreeItem) selectedObject).getItem();
							item.setRealtimeGed(realtimeGedButton.getSelection());
							item.setAvailableGed(availableGedButton.getSelection());
							item.setCreateEntry(createEntry.getSelection());
							
							try {
								Activator.getDefault().getRepositoryApi().getAdminService().update(item);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				
				createEntry.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if(selectedObject != null && ((TreeItem) selectedObject).getItem() != null
								&&((TreeItem) selectedObject).getItem() instanceof RepositoryItem){
							
							RepositoryItem item = ((TreeItem) selectedObject).getItem();
							item.setRealtimeGed(realtimeGedButton.getSelection());
							item.setAvailableGed(availableGedButton.getSelection());
							item.setCreateEntry(createEntry.getSelection());
							
							try {
								Activator.getDefault().getRepositoryApi().getAdminService().update(item);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				});
				
				grOptionHistoric.setEnabled(true);
				loadGroups.setEnabled(true);
				stopLoadGroups.setEnabled(false);
				try {
					initGroups();

					IObjectIdentifier identifier = new ObjectIdentifier(Activator.getDefault().getCurrentRepository().getId(), it.getId());
					List<Integer> groupsId = historicComponent.getGroupsAuthorizedByItemId(identifier);

					try {
						for (Group i : currentgroups) {
							boolean checked = false;
							for (int g : groupsId) {
								if (g == i.getId()) {
									checked = true;
									break;
								}
							}
							viewerSecurity.setChecked(i, checked);
						}
						viewerSecurity.refresh();
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.SecurityManager_22, e.getMessage());
					}

					showHistoric(it);

				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.SecurityManager_22, e.getMessage());
					tableViewer.setInput(new ArrayList<DocumentVersion>());
				}
			}
			else if (it.getType() == IRepositoryApi.REPORTS_GROUP) {
				availableGedButton.setEnabled(false);
				realtimeGedButton.setEnabled(false);
				createEntry.setEnabled(false);
				
				grOptionHistoric.setEnabled(true);
				loadGroups.setEnabled(true);
				stopLoadGroups.setEnabled(false);
				try {
					initGroups();

					IObjectIdentifier identifier = new ObjectIdentifier(Activator.getDefault().getCurrentRepository().getId(), it.getId());
					List<Integer> groupsId = historicComponent.getGroupsAuthorizedByItemId(identifier);

					try {
						for (Group i : currentgroups) {
							boolean checked = false;
							for (int g : groupsId) {
								if (g == i.getId()) {
									checked = true;
									break;
								}
							}
							viewerSecurity.setChecked(i, checked);
						}
						viewerSecurity.refresh();
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.SecurityManager_22, e.getMessage());
					}

				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.SecurityManager_22, e.getMessage());
					tableViewer.setInput(new ArrayList<DocumentVersion>());
				}
				
				tableViewer.getTable().setEnabled(false);
			}
			else {
				tableViewer.setInput(new ArrayList<DocumentVersion>());
				loadGroups.setEnabled(false);
				stopLoadGroups.setEnabled(false);
				grOptionHistoric.setEnabled(false);
			}
		}
		else {
			grOptionHistoric.setEnabled(false);
		}
	}

	/**
	 * load all groups in the viewer and check them if necesseary
	 */

	private boolean isGroupLoaded = false;

	private List<Group> currentgroups = new ArrayList<Group>();

	private void initGroups() throws Exception {
		int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);
		IVanillaSecurityManager securityManager = Activator.getDefault().getVanillaApi().getVanillaSecurityManager();
		final List<Group> l = securityManager.getGroups(0, step);
		currentgroups.addAll(l);
		isGroupLoaded = l.size() == 0;

		if (selectedObject != null) {

			RepositoryItem it = ((TreeItem) selectedObject).getItem();

			IObjectIdentifier identifier = new ObjectIdentifier(Activator.getDefault().getCurrentRepository().getId(), it.getId());

			try {

				for (int g : historicComponent.getGroupsAuthorizedByItemId(identifier)) {
					for (Group i : l) {
						final Group _i = i;
						if (g == i.getId()) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									viewerSecurity.setChecked(_i, true);
								}
							});
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getSite().getShell(), Messages.SecurityManager_23, e.getMessage());
			}

		}
		viewerSecurity.refresh();
		currentState.setText("Only " + step + " firsts Groups loaded. Use the Load Button to get all."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * shows for all available groups
	 * 
	 * @param item
	 * @throws Exception
	 */
	private void showHistoric(RepositoryItem item) throws Exception {
		SecurityProperties security = new SecurityProperties();

		IVanillaSecurityManager securityManager = Activator.getDefault().getVanillaApi().getVanillaSecurityManager();

		User currentUser = securityManager.getUserByLogin(Activator.getDefault().getLogin());

		for (Group group : currentgroups) {
			security.addGroup(group.getId() + ""); //$NON-NLS-1$
		}

		security.setUserId("" + currentUser.getId()); //$NON-NLS-1$ //server will determine if superuser or not 
		security.setRepositoryId("" + 1); //$NON-NLS-1$ // this should be ignored anyway

		//security.setRepositoryId(Activator.getDefault().getRepositoryContext().getRepository().getId() + ""); //$NON-NLS-1$
		IObjectIdentifier identifier = new ObjectIdentifier(Activator.getDefault().getCurrentRepository().getId(), item.getId());

		List<DocumentVersion> histoVersions = new ArrayList<DocumentVersion>();
		List<GedDocument> histos = historicComponent.getReportHistoric(identifier, -1);
		if (histos != null && !histos.isEmpty()) {
			for (GedDocument histo : histos) {
				if (histo.getDocumentVersions() != null) {
					for (DocumentVersion doc : histo.getDocumentVersions()) {
						histoVersions.add(doc);
					}
				}
			}
		}
		tableViewer.setLabelProvider(new TableViewerRowColor());
		if (histoVersions != null) {
			tableViewer.setInput(histoVersions);
		}
		tableViewer.getLabelProvider(0);
	}


	private class GroupLoader extends Thread {
		private boolean killThread = false;
		private int beginAt = 0;

		public GroupLoader() {
			this.setName("GroupManagementView GroupLoader"); //$NON-NLS-1$
		}

		public void run() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					currentState.setText(Messages.SecurityManager_2);
				}
			});
			currentgroups.clear();
			while (!killThread && !isGroupLoaded && selectedObject != null) {

				List<Group> l = null;
				try {
					int step = Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_ROWS_BY_CHUNK);
					if (step == 0) {
						step = 50;
					}
					IVanillaSecurityManager securityManager = Activator.getDefault().getVanillaApi().getVanillaSecurityManager();
					l = securityManager.getGroups(beginAt, step);
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

					RepositoryItem it = ((TreeItem) selectedObject).getItem();

					try {

						IObjectIdentifier identifier = new ObjectIdentifier(Activator.getDefault().getCurrentRepository().getId(), it.getId());

						for (Group i : l) {
							for (int g : historicComponent.getGroupsAuthorizedByItemId(identifier)) {
								final Group _i = i;
								if (g == i.getId()) {
									Display.getDefault().asyncExec(new Runnable() {
										public void run() {
											viewerSecurity.setChecked(_i, true);

										}
									});

								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(getSite().getShell(), "Error when getting Authorized groups", e.getMessage()); //$NON-NLS-1$

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
					if (isGroupLoaded) {
						currentState.setText(Messages.SecurityManager_4);
					}
				}
			});
		}

		public void performStop() {
			killThread = true;
		}
	}
}