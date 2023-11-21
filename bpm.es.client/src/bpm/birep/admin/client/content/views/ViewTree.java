package bpm.birep.admin.client.content.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;
import adminbirep.icons.StepsImagesUrl;
import bpm.birep.admin.client.dialog.DialogDirectory;
import bpm.birep.admin.client.dialog.DialogXmlViewer;
import bpm.birep.admin.client.dialog.SearchReplaceRepository;
import bpm.birep.admin.client.dialog.item.DialogCustom;
import bpm.birep.admin.client.dialog.item.DialogFile;
import bpm.birep.admin.client.dialog.item.DialogJasper;
import bpm.birep.admin.client.dialog.item.DialogUrl;
import bpm.birep.admin.client.disco.DisconnectedWizard;
import bpm.birep.admin.client.helpers.PDFGenerator;
import bpm.birep.admin.client.reportsgroup.ReportsGroupWizard;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeObject;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ViewTree extends ViewPart {
	public static Object selectedObject;

	public static final Color COLOR_LOCKED = new Color(Display.getDefault(), 250, 25, 120);
	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$

	public static final String ID = "bpm.birep.admin.client.content.views.tree"; //$NON-NLS-1$
	protected TreeViewer viewer;
	protected TableViewer tableViewer;
	private TreeItem moving;

	protected Combo filterType;
	protected Text filterName;

	protected RepositoryItem copiedReportObject;

	private Action runObject, stopObject;
	private Action unlock, copyReportToFdDico, insertReportInDico;

	protected ViewerFilter lockedFilter, adressableFilter;
	
	private TabFolder folder;

	@Override
	public void createPartControl(Composite container) {
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		createFilters();
		createToolbar(container);
		createTree(container);
		getSite().setSelectionProvider(viewer);
		
		createActions();
		createContextMenu();
		setDnd();

		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(new ViewActionRefresh(this));
		try {
			createInput();
		} catch (Exception e) {
			MessageDialog.openError(getSite().getShell(), Messages.ViewTree_49, e.getMessage());
		}
		viewer.getTree().redraw();
	}

	private void createActions() {

		copyReportToFdDico = new Action(Messages.ViewTree_2) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeItem)) {
					return;
				}

				copiedReportObject = ((TreeItem) ss.getFirstElement()).getItem();

			}
		};

		insertReportInDico = new Action(Messages.ViewTree_3) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty() || copiedReportObject == null) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeItem)) {
					return;
				}

				RepositoryItem it = ((TreeItem) ss.getFirstElement()).getItem();

				String componentName = null;

				InputDialog d = new InputDialog(getSite().getShell(), Messages.ViewTree_4, Messages.ViewTree_5, "componentName", null); //$NON-NLS-1$
				if (d.open() == InputDialog.OK) {
					componentName = d.getValue();
				}
				else {
					return;
				}

				try {

					String xml = Activator.getDefault().getRepositoryApi().getRepositoryService().loadModel(it);
					Document doc = DocumentHelper.parseText(xml);
					Element report = doc.getRootElement().addElement("componentReport"); //$NON-NLS-1$
					Element reportType = null;

					if (copiedReportObject.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
						reportType = report.addElement("birtReport"); //$NON-NLS-1$
					}
					else if (copiedReportObject.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE) {
						reportType = report.addElement("jFreeReport"); //$NON-NLS-1$

					}

					reportType.addElement("name").setText(componentName); //$NON-NLS-1$
					Element reportData = reportType.addElement("repository-data"); //$NON-NLS-1$
					reportData.addElement("repositoryUrl").setText(Activator.getDefault().getRepositoryApi().getContext().getRepository().getUrl()); //$NON-NLS-1$
					reportData.addElement("directoryItemId").setText(copiedReportObject.getId() + ""); //$NON-NLS-1$ //$NON-NLS-2$

					ByteArrayOutputStream b = new ByteArrayOutputStream();
					XMLWriter w = new XMLWriter(b, OutputFormat.createPrettyPrint());
					w.write(doc);
					w.close();
					String s = b.toString();

					Activator.getDefault().getRepositoryApi().getRepositoryService().updateModel(it, s);
					copiedReportObject = null;

					MessageDialog.openInformation(getSite().getShell(), Messages.ViewTree_15, Messages.ViewTree_16);

				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewTree_17, e.getMessage());
				}

			}
		};

		runObject = new Action(Messages.ViewTree_18) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeItem)) {
					return;
				}

				RepositoryItem item = ((TreeItem) ss.getFirstElement()).getItem();
				Boolean oldState = item.isOn();
				item.setOn(true);
				try {
					Activator.getDefault().getRepositoryApi().getAdminService().update(item);
					viewer.refresh();
					tableViewer.refresh();
				} catch (Exception e) {
					item.setOn(oldState);
					e.printStackTrace();
				}

			}
		};

		stopObject = new Action(Messages.ViewTree_19) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeItem)) {
					return;
				}

				RepositoryItem item = ((TreeItem) ss.getFirstElement()).getItem();
				Boolean oldState = item.isOn();
				item.setOn(false);
				try {
					Activator.getDefault().getRepositoryApi().getAdminService().update(item);
					viewer.refresh();
					tableViewer.refresh();
				} catch (Exception e) {
					item.setOn(oldState);
					e.printStackTrace();
				}

			}
		};

		unlock = new Action(Messages.ViewTree_36) {
			public void run() {
				RepositoryItem it = ((TreeItem) ((IStructuredSelection) viewer.getSelection()).getFirstElement()).getItem();
				try {
					Activator.getDefault().getRepositoryApi().getVersioningService().unlock(it);
					MessageDialog.openInformation(getSite().getShell(), Messages.ViewTree_37, Messages.ViewTree_38);
					it.setLockId(0);
					viewer.refresh();
					tableViewer.refresh();
				} catch (Exception e) {
					MessageDialog.openError(getSite().getShell(), Messages.ViewTree_40, e.getMessage());

				}

			}
		};

		unlock.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("unlock")); //$NON-NLS-1$

	}

	public void refresh() {
		viewer.refresh();
	}

	@Override
	public void setFocus() {
	}

	protected void createTree(Composite container) {
		// createActions();

		Composite main = new Composite(container, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Button bFilter = new Button(main, SWT.CHECK);
		bFilter.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		bFilter.setText(Messages.ViewTree_42);

		bFilter.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				filterType.setEnabled(bFilter.getSelection());

				ViewerFilter[] f = viewer.getFilters();
				for (ViewerFilter vf : f) {
					if (vf instanceof TypeViewerFilter) {
						viewer.removeFilter(vf);
						tableViewer.removeFilter(vf);
					}

				}
				if (bFilter.getSelection()) {

					TypeViewerFilter vf = new TypeViewerFilter(filterType.getSelectionIndex());
					viewer.addFilter(vf);
					tableViewer.addFilter(vf);

				}
				viewer.refresh();
				tableViewer.refresh();
			}

		});

		filterType = new Combo(main, SWT.READ_ONLY);
		filterType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterType.setEnabled(false);
		filterType.setItems(filterTypeNames);
		filterType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean changed = false;
				for (ViewerFilter f : viewer.getFilters()) {
					if (f instanceof TypeViewerFilter) {

						int type = -1;
						int subtype = -1;
						switch (filterType.getSelectionIndex()) {
							case 0:
								type = IRepositoryApi.FASD_TYPE;
								break;
							case 1:
								type = IRepositoryApi.FAV_TYPE;
								break;
							case 2:
								type = IRepositoryApi.FAR_TYPE;
								break;
							case 3:
								type = IRepositoryApi.FWR_TYPE;
								break;
							case 4:
								type = IRepositoryApi.CUST_TYPE;
								subtype = IRepositoryApi.BIRT_REPORT_SUBTYPE;
								break;
							case 5:
								type = IRepositoryApi.CUST_TYPE;
								subtype = IRepositoryApi.JASPER_REPORT_SUBTYPE;
								break;
							case 6:
								type = IRepositoryApi.FMDT_TYPE;
								break;
							case 7:
								type = IRepositoryApi.FD_TYPE;
								break;
							case 8:
								type = IRepositoryApi.FD_DICO_TYPE;
								break;
							case 9:
								type = IRepositoryApi.GTW_TYPE;
								break;
							case 10:
								type = IRepositoryApi.WKB_TYPE;
								break;
							case 11:
								type = IRepositoryApi.BIW_TYPE;
								break;
							case 12:
								type = IRepositoryApi.MAP_TYPE;
								break;
							case 13:
								type = IRepositoryApi.CUST_TYPE;
								subtype = IRepositoryApi.ORBEON_XFORMS;
								break;
							case 14:
								type = IRepositoryApi.EXTERNAL_DOCUMENT;
								break;
							case 15:
								type = IRepositoryApi.URL;
								break;
							case 16:
								type = IRepositoryApi.REPORTS_GROUP;
								break;
							case 17:
								type = IRepositoryApi.KPI_THEME;
								break;
						}

						if (((TypeViewerFilter) f).type != type || ((TypeViewerFilter) f).subtype != subtype) {
							((TypeViewerFilter) f).subtype = subtype;
							((TypeViewerFilter) f).type = type;
							changed = true;
						}
					}
				}
				if (changed) {
					viewer.refresh();
					tableViewer.refresh();
				}

			}

		});

		final Button bFilterName = new Button(main, SWT.CHECK);
		bFilterName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		bFilterName.setText(Messages.ViewTree_43);
		bFilterName.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				filterName.setEnabled(bFilterName.getSelection());

				ViewerFilter[] f = viewer.getFilters();
				for (ViewerFilter vf : f) {
					if (vf instanceof NameViewerFilter) {
						viewer.removeFilter(vf);
						tableViewer.removeFilter(vf);
					}

				}
				if (bFilterName.getSelection()) {

					NameViewerFilter vf = new NameViewerFilter(filterName.getText());
					viewer.addFilter(vf);
					tableViewer.addFilter(vf);

				}
				viewer.refresh();
				tableViewer.refresh();
			}

		});

		filterName = new Text(main, SWT.BORDER);
		filterName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterName.setEnabled(false);
		filterName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				for (ViewerFilter f : viewer.getFilters()) {
					if (f instanceof NameViewerFilter) {
						((NameViewerFilter) f).name = new String(filterName.getText());
						viewer.refresh();
						tableViewer.refresh();
						return;

					}
				}

			}

		});

		folder = new TabFolder(main, SWT.NONE);
//		folder.setLayout(new GridLayout(2, false));
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		ISelectionChangedListener selLst = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeItem)) {
					if (event.getSource() == viewer) {
						tableViewer.removeSelectionChangedListener(this);
						StructuredSelection sel = new StructuredSelection();
						tableViewer.setSelection(sel);
						tableViewer.addSelectionChangedListener(this);
					}
					return;
				}

				if (event.getSource() == viewer) {
					tableViewer.removeSelectionChangedListener(this);
					tableViewer.setSelection(event.getSelection(), true);
					tableViewer.addSelectionChangedListener(this);
				}
				else if (event.getSource() == tableViewer) {
					viewer.removeSelectionChangedListener(this);
					viewer.setSelection(event.getSelection(), true);
					viewer.addSelectionChangedListener(this);
				}

			}

		};
		
		Composite treeComp = new Composite(folder, SWT.NONE);
		treeComp.setLayout(new GridLayout());
		treeComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		viewer = new TreeViewer(treeComp, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new TreeLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {

			@Override
			public Color getForeground(Object element) {
				if (element instanceof TreeItem) {
					if (((TreeItem) element).getItem().getLockId() != null && ((TreeItem) element).getItem().getLockId().intValue() > 0) {
						return COLOR_LOCKED;
					}
				}
				return super.getBackground(element);
			}

			@Override
			public Font getFont(Object element) {
				if (element instanceof TreeItem) {
					if (((TreeItem) element).getItem().getLockId() != null && ((TreeItem) element).getItem().getLockId().intValue() == 1) {
						return FONT_ACTIVE_CONNECTION;
					}
				}
				return super.getFont(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof TreeItem) {
					if (((TreeItem) element).getItem().getLockId() != null && ((TreeItem) element).getItem().getLockId().intValue() == 1) {
						return super.getText(element) + Messages.ViewTree_45;
					}
				}
				return super.getText(element);
			}

		});
		viewer.addSelectionChangedListener(selLst);

		TabItem tree = new TabItem(folder, SWT.NONE);
		tree.setText(Messages.ViewTree_46);
		tree.setControl(treeComp);

		tableViewer = new TableViewer(folder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof TreeParent) {
					List l = getChilds((TreeParent) inputElement);
					return l.toArray(new Object[l.size()]);
				}
				return null;
			}

			private List<TreeItem> getChilds(TreeParent parent) {
				List<TreeItem> l = new ArrayList<TreeItem>();
				for (Object o : parent.getChildren()) {
					if (o instanceof TreeItem) {
						l.add((TreeItem) o);
					}
					if (o instanceof TreeDirectory) {
						l.addAll(getChilds((TreeDirectory) o));
					}
				}

				return l;
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		tableViewer.setLabelProvider(new DecoratingLabelProvider(new TreeLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {

			@Override
			public Color getForeground(Object element) {
				if (element instanceof TreeItem) {
					if (((TreeItem) element).getItem().getLockId() != null && ((TreeItem) element).getItem().getLockId().intValue() == 1) {
						return COLOR_LOCKED;
					}
				}
				return super.getBackground(element);
			}

			@Override
			public Font getFont(Object element) {
				if (element instanceof TreeItem) {
					if (((TreeItem) element).getItem().getLockId() != null && ((TreeItem) element).getItem().getLockId().intValue() == 1) {
						return FONT_ACTIVE_CONNECTION;
					}
				}
				return super.getFont(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof TreeItem) {
					String n = ((TreeItem) element).getFullName();
					if (((TreeItem) element).getItem().getLockId() != null && ((TreeItem) element).getItem().getLockId().intValue() == 1) {
						return n + Messages.ViewTree_47;
					}
					else {
						return n;
					}
				}
				return super.getText(element);
			}

		});
		tableViewer.addSelectionChangedListener(selLst);

		TabItem table = new TabItem(folder, SWT.NONE);
		table.setText(Messages.ViewTree_48);
		table.setControl(tableViewer.getTable());


	}

	public void createInput() {

		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		try {

			Repository repository = new Repository(Activator.getDefault().getRepositoryApi());

			StringBuffer warnings = new StringBuffer();
			for (RepositoryDirectory d : repository.getRootDirectories()) {
				TreeDirectory tp = new TreeDirectory(d);
				root.addChild(tp);
				warnings.append(buildChilds(repository, tp));
				// XXX
				for (RepositoryItem it : repository.getItems(d)) {
					TreeItem ti = new TreeItem(it);
					tp.addChild(ti);
				}

			}
			if (warnings.length() > 0) {
				MessageDialog.openWarning(getSite().getShell(), Messages.ViewTree_51, warnings.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openWarning(getSite().getShell(), Messages.ViewTree_52 + Activator.getDefault().getCurrentRepository().getUrl(), e.getMessage());

		}

		viewer.setInput(root);
		tableViewer.setInput(root);
	}

	protected String buildChilds(Repository rep, TreeDirectory parent) {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();
		StringBuffer errors = new StringBuffer();

		try {
			for (RepositoryDirectory d : rep.getChildDirectories(dir)) {
				TreeDirectory td = new TreeDirectory(d);
				parent.addChild(td);

				try {
					for (RepositoryItem it : rep.getItems(d)) {
						TreeItem ti = new TreeItem(it);
						td.addChild(ti);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					errors.append(" - Error when getting " + d.getName() + " items : " + ex.getMessage() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

				errors.append(buildChilds(rep, td));

			}
		} catch (Exception ex1) {
			ex1.printStackTrace();
			errors.append(" - Error when getting " + dir.getName() + " childs : " + ex1.getMessage() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		}

		return errors.toString();

	}

	private void createToolbar(Composite parent) {
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayout(new GridLayout());

		ToolItem addDir = new ToolItem(toolbar, SWT.PUSH);
		addDir.setToolTipText(Messages.ViewTree_59);
		addDir.setImage(Activator.getDefault().getImageRegistry().get("folder")); //$NON-NLS-1$
		addDir.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// find the type
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				TreeParent tp = null;

				if (ss.getFirstElement() instanceof TreeDirectory) {
					tp = (TreeParent) ss.getFirstElement();
				}

				int type = 0;
				DialogDirectory dial = null;

				try {
					dial = new DialogDirectory(getSite().getShell(), Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups(), type);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewTree_61, Messages.ViewTree_62 + ex.getMessage());

					return;
				}

				RepositoryDirectory parent = null;
				if (ss.getFirstElement() instanceof TreeDirectory) {
					parent = ((TreeDirectory) ss.getFirstElement()).getDirectory();
				}

				if (dial.open() == DialogDirectory.OK) {
					RepositoryDirectory d = dial.getDirectory();
					try {
						d = Activator.getDefault().getRepositoryApi().getRepositoryService().addDirectory(d.getName(), d.getComment(), parent);
						createInput();
					} catch (Exception e1) {
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_63, e1.getMessage());
						e1.printStackTrace();
					}

					try {
						Group gr = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupByName(dial.getGroupName());
						Activator.getDefault().getRepositoryApi().getAdminService().addGroupForDirectory(gr.getId(), d.getId());
					} catch (Exception ex) {
					}
				}
			}
		});

		ToolItem addDocument = new ToolItem(toolbar, SWT.PUSH);
		addDocument.setToolTipText(Messages.ViewTree_65);
		addDocument.setImage(Activator.getDefault().getImageRegistry().get("documents")); //$NON-NLS-1$
		addDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeDirectory)) {
					return;
				}

				RepositoryDirectory dir = ((TreeDirectory) ss.getFirstElement()).getDirectory();

				FileDialog fd = new FileDialog(getSite().getShell());
				String path = fd.open();

				if (path != null) {
					try {
						DialogFile dial = new DialogFile(getSite().getShell(), dir, new File(path));
						if (dial.open() == DialogFile.OK) {
							createInput();
						}

					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_67, e1.getMessage());
					}
				}

			}

		});

		ToolItem addUrl = new ToolItem(toolbar, SWT.PUSH);
		addUrl.setToolTipText(Messages.ViewTree_68);
		addUrl.setImage(Activator.getDefault().getImageRegistry().get("addlink")); //$NON-NLS-1$
		addUrl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeDirectory)) {
					return;
				}

				RepositoryDirectory dir = ((TreeDirectory) ss.getFirstElement()).getDirectory();

				DialogUrl dial = new DialogUrl(getSite().getShell(), dir);
				if (dial.open() == DialogUrl.OK) {
					createInput();
				}

			}

		});
		
		ToolItem addVanillaItem = new ToolItem(toolbar, SWT.PUSH);
		addVanillaItem.setToolTipText("Ajouter un objet Vanilla");
		addVanillaItem.setImage(Activator.getDefault().getImageRegistry().get("add")); //$NON-NLS-1$
		addVanillaItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeDirectory)) {
					return;
				}

				RepositoryDirectory dir = ((TreeDirectory) ss.getFirstElement()).getDirectory();

				FileDialog fd = new FileDialog(getSite().getShell());
				fd.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
				String path = fd.open();

				if (path != null) {
					try {
						FileInputStream fis = new FileInputStream(path);
						int type = -1;
						if(path.endsWith(".gateway")) {
							type = IRepositoryApi.GTW_TYPE;
						}
						else if(path.endsWith(".biw")) {
							type = IRepositoryApi.BIW_TYPE;
						}
						else if(path.endsWith(".freemetadata")) {
							type = IRepositoryApi.FMDT_TYPE;
						}
						else {
							return;
						}
						
						DialogCustom dial = new DialogCustom(getSite().getShell(), dir, fis, type, -1);
						if (dial.open() == DialogCustom.OK) {
							createInput();
						}

					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_74, Messages.ViewTree_75 + e1.getMessage());
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_76, Messages.ViewTree_77 + e1.getMessage());
					}

				}
			}
		});

		ToolItem addBirt = new ToolItem(toolbar, SWT.PUSH);
		addBirt.setToolTipText(Messages.ViewTree_70);
		addBirt.setImage(Activator.getDefault().getImageRegistry().get("birt")); //$NON-NLS-1$
		addBirt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeDirectory)) {
					return;
				}

				RepositoryDirectory dir = ((TreeDirectory) ss.getFirstElement()).getDirectory();

				FileDialog fd = new FileDialog(getSite().getShell());
				fd.setFilterExtensions(new String[] { "*.rptdesign", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				String path = fd.open();

				if (path != null) {
					try {
						FileInputStream fis = new FileInputStream(path);

						DialogCustom dial = new DialogCustom(getSite().getShell(), dir, fis, IRepositoryApi.CUST_TYPE, IRepositoryApi.BIRT_REPORT_SUBTYPE);
						if (dial.open() == DialogCustom.OK) {
							createInput();
						}

					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_74, Messages.ViewTree_75 + e1.getMessage());
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_76, Messages.ViewTree_77 + e1.getMessage());
					}

				}
			}

		});

		ToolItem addJasper = new ToolItem(toolbar, SWT.PUSH);
		addJasper.setToolTipText(Messages.ViewTree_1);
		addJasper.setImage(Activator.getDefault().getImageRegistry().get("jasper")); //$NON-NLS-1$
		addJasper.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				if (!(ss.getFirstElement() instanceof TreeDirectory)) {
					return;
				}

				RepositoryDirectory dir = ((TreeDirectory) ss.getFirstElement()).getDirectory();

				FileDialog fd = new FileDialog(getSite().getShell());
				fd.setFilterExtensions(new String[] { "*.jrxml", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
				String path = fd.open();

				if (path != null) {
					try {
						FileInputStream fis = new FileInputStream(path);
						String xml = IOUtils.toString(fis);

						Document document = DocumentHelper.parseText(xml);
						Element root = document.getRootElement();

						xml = root.asXML();

						DialogJasper dial = new DialogJasper(getSite().getShell(), dir, xml);
						if (dial.open() == DialogJasper.OK) {
							createInput();
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_8, Messages.ViewTree_9);
					} catch (IOException e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_10, Messages.ViewTree_11);
					} catch (DocumentException e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_12, Messages.ViewTree_13);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}

		});

		ToolItem addReportsGroup = new ToolItem(toolbar, SWT.PUSH);
		addReportsGroup.setToolTipText(Messages.ViewTree_7);
		addReportsGroup.setImage(Activator.getDefault().getImageRegistry().get(Icons.REPORTS_GROUP)); //$NON-NLS-1$
		addReportsGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				List<RepositoryItem> items = new ArrayList<RepositoryItem>();

				for (Object object : ss.toList()) {
					if (object instanceof TreeItem && ((TreeItem) object).getItem().getType() == IRepositoryApi.CUST_TYPE) {
						items.add(((TreeItem) object).getItem());
					}
					else {
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_24, Messages.ViewTree_39);
						return;
					}
				}
				
				ReportsGroupWizard wiz = new ReportsGroupWizard(items);
				WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
				dial.setMinimumPageSize(800, 600);
				if (dial.open() != WizardDialog.OK) {
					createInput();
					return;
				}
			}

		});

		ToolItem del = new ToolItem(toolbar, SWT.PUSH);
		del.setToolTipText(Messages.ViewTree_98);
		del.setImage(Activator.getDefault().getImageRegistry().get("del")); //$NON-NLS-1$
		del.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				try {
					Object[] obj = ss.toArray();

					for (Object o : obj) {
						try {
							if (ss.getFirstElement() instanceof TreeDirectory) {
								Activator.getDefault().getRepositoryApi().getRepositoryService().delete(((TreeDirectory) o).getDirectory());
							}
							else if (ss.getFirstElement() instanceof TreeItem) {
								Activator.getDefault().getRepositoryApi().getRepositoryService().delete(((TreeItem) o).getItem());
							}
						} catch (Exception ex) {

						}
					}
					createInput();
				} catch (Exception ex) {
					MessageDialog.openError(getSite().getShell(), Messages.ViewTree_100, ex.getMessage());
				}

			}

		});

		ToolItem viewXml = new ToolItem(toolbar, SWT.PUSH);
		viewXml.setToolTipText(Messages.ViewTree_101);
		viewXml.setImage(Activator.getDefault().getImageRegistry().get("edit")); //$NON-NLS-1$
		viewXml.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeItem)) {
					return;
				}

				RepositoryItem it = ((TreeItem) ss.getFirstElement()).getItem();

				DialogXmlViewer dial = new DialogXmlViewer(getSite().getShell(), it);
				if (dial.open() == DialogXmlViewer.OK && dial.isModified()) {
					try {
						Activator.getDefault().getRepositoryApi().getRepositoryService().updateModel(it, dial.getXml());
						MessageDialog.openInformation(getSite().getShell(), Messages.ViewTree_103, Messages.ViewTree_104);
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_105, e1.getMessage());
					}
				}
			}

		});

		ToolItem startIt = new ToolItem(toolbar, SWT.PUSH);
		startIt.setToolTipText(Messages.ViewTree_106);
		startIt.setImage(Activator.getDefault().getImageRegistry().get("start")); //$NON-NLS-1$
		startIt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runObject.run();
			}
		});

		ToolItem stopIt = new ToolItem(toolbar, SWT.PUSH);
		stopIt.setToolTipText(Messages.ViewTree_108);
		stopIt.setImage(Activator.getDefault().getImageRegistry().get("stop")); //$NON-NLS-1$
		stopIt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				stopObject.run();
			}

		});

		final ToolItem showLocked = new ToolItem(toolbar, SWT.CHECK);
		showLocked.setToolTipText(Messages.ViewTree_110);
		showLocked.setImage(Activator.getDefault().getImageRegistry().get("lock")); //$NON-NLS-1$
		showLocked.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (showLocked.getSelection()) {
					viewer.addFilter(lockedFilter);
					tableViewer.addFilter(lockedFilter);
				}
				else {
					viewer.removeFilter(lockedFilter);
					tableViewer.removeFilter(lockedFilter);
				}
				viewer.refresh();
				tableViewer.refresh();

			}

		});

		ToolItem searchreplace = new ToolItem(toolbar, SWT.PUSH);
		searchreplace.setToolTipText(Messages.ViewTree_112);
		searchreplace.setImage(Activator.getDefault().getImageRegistry().get("searchreplace")); //$NON-NLS-1$
		searchreplace.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SearchReplaceRepository dial = new SearchReplaceRepository(getSite().getShell());
				if (dial.open() == Dialog.OK) {
					createInput();
				}
			}

		});

		ToolItem createDiscoPackage = new ToolItem(toolbar, SWT.PUSH);
		createDiscoPackage.setToolTipText(Messages.ViewTree_14);
		createDiscoPackage.setImage(Activator.getDefault().getImageRegistry().get("package")); //$NON-NLS-1$
		createDiscoPackage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				List<RepositoryItem> items = new ArrayList<RepositoryItem>();

				for (Object object : ss.toList()) {
					if (object instanceof TreeItem 
							&& (((TreeItem) object).getItem().getType() == IRepositoryApi.CUST_TYPE
							|| ((TreeItem) object).getItem().getType() == IRepositoryApi.FAV_TYPE
							|| ((TreeItem) object).getItem().getType() == IRepositoryApi.FWR_TYPE
							|| ((TreeItem) object).getItem().getType() == IRepositoryApi.FD_TYPE
							|| ((TreeItem) object).getItem().getType() == IRepositoryApi.FD_DICO_TYPE
							|| ((TreeItem) object).getItem().getType() == IRepositoryApi.FASD_TYPE
							|| ((TreeItem) object).getItem().getType() == IRepositoryApi.EXTERNAL_DOCUMENT
							|| ((TreeItem) object).getItem().getType() == IRepositoryApi.EXTERNAL_FILE)) {
						items.add(((TreeItem) object).getItem());
					}
					else {
						MessageDialog.openError(getSite().getShell(), Messages.ViewTree_24, Messages.ViewTree_31);
						return;
					}
				}

				List<Group> groups = new ArrayList<Group>();
				try {
					groups = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				DisconnectedWizard wiz = new DisconnectedWizard(items);
				WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
				dial.setMinimumPageSize(800, 600);
				if (dial.open() != WizardDialog.OK) {
					return;
				}
			}
		});
		
		ToolItem exportPdf = new ToolItem(toolbar, SWT.PUSH);
		exportPdf.setToolTipText(Messages.ViewTree_6);
		exportPdf.setImage(Activator.getDefault().getImageRegistry().get(Icons.PICTURE)); //$NON-NLS-1$
		exportPdf.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getSite().getShell(), SWT.SAVE);
        		fd.setFilterExtensions(new String[]{"*.*"}); //$NON-NLS-1$
        		
        		String s = fd.open();
        		if (s != null){
        			try{
        				OutputStream os = new FileOutputStream(s);
        				PDFGenerator.generatePdf(os, StepsImagesUrl.getImages(), (TreeParent<?>) viewer.getInput());
        				os.close();
        			}catch(Exception ex){
        				ex.printStackTrace();
        			}
        			
        		}
			}
		});
	}

	protected void createFilters() {
		lockedFilter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof TreeDirectory) {
					return true;
				}
				RepositoryItem it = ((TreeItem) element).getItem();
				return it.getLockId() != null && it.getLockId() > 0;

			}
		};

	}

	protected void createContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new ViewerMenuListener(viewer));
		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));

		menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new ViewerMenuListener(tableViewer));
		tableViewer.getControl().setMenu(menuMgr.createContextMenu(tableViewer.getControl()));

	}

	private void setDnd() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		viewer.addDropSupport(ops, transfers, new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			@SuppressWarnings("unchecked")
			public void drop(DropTargetEvent event) {
				Object o = ((org.eclipse.swt.widgets.TreeItem) event.item).getData();
				if (!(o instanceof TreeDirectory) || moving == null) {
					return;
				}

				RepositoryItem di = moving.getItem();

				di.setDirectoryId(((TreeDirectory) o).getDirectory().getId());
				try {
					Activator.getDefault().getRepositoryApi().getAdminService().update(di);

					moving.getParent().removeChild(moving);
					((TreeDirectory) o).addChild(moving);
					viewer.refresh();
					moving = null;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			public void dropAccept(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

		});

		viewer.addDragSupport(ops, transfers, new ItemDragerListener(viewer));
		tableViewer.addDragSupport(ops, transfers, new ItemDragerListener(tableViewer));
	}

	public class ViewerMenuListener implements IMenuListener {

		private Viewer _viewer;

		public ViewerMenuListener(Viewer v) {
			this._viewer = v;
		}

		public void menuAboutToShow(IMenuManager mgr) {
			IStructuredSelection ss = (IStructuredSelection) _viewer.getSelection();
			Object o = ss.getFirstElement();

			if (o instanceof TreeItem) {
				RepositoryItem itm = ((TreeItem) o).getItem();
				if (itm.getType() != IRepositoryApi.FAV_TYPE && itm.getSubtype() != IRepositoryApi.ORBEON_XFORMS) {
					mgr.add(runObject);
					mgr.add(stopObject);

				}

				if (itm.isLocked()) {
					mgr.add(unlock);
				}

				if (itm.getType() == IRepositoryApi.CUST_TYPE && itm.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
					mgr.add(copyReportToFdDico);
				}

				if (itm.getType() == IRepositoryApi.FD_DICO_TYPE && copiedReportObject != null) {

					insertReportInDico.setText(Messages.ViewTree_114 + itm.getItemName() + Messages.ViewTree_115);
					mgr.add(insertReportInDico);
				}

			}

			mgr.add(new Separator());

			mgr.update();
		}
	}

	public class TypeViewerFilter extends ViewerFilter {

		private int type = -1;
		private int subtype = -1;

		public TypeViewerFilter(int type) {
			this.type = type;

		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TreeItem) {
				if (type == IRepositoryApi.CUST_TYPE) {
					return ((TreeItem) element).getItem().getType() == type && ((TreeItem) element).getItem().getSubtype() == subtype;
				}
				else {
					return ((TreeItem) element).getItem().getType() == type;
				}

			}
			return true;
		}

	}

	public class NameViewerFilter extends ViewerFilter {

		private String name = ""; //$NON-NLS-1$

		public NameViewerFilter(String name) {
			this.name = name;

		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TreeItem) {
				return ((TreeObject) element).getName().toLowerCase().contains(name.toLowerCase());
			}
			return true;
		}

	}

	public class DrillableViewerFilter extends ViewerFilter {

		public DrillableViewerFilter() {

		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TreeItem) {

				// return ((TreeItem) element).getItem().getIsDrillable() == 1;

			}
			return true;
		}

	}

	private static final String[] filterTypeNames = new String[] { "VanillaAnalysis Schema", //$NON-NLS-1$
			"VanillaAnalysis View", //$NON-NLS-1$
			"VanillaAnalysis Report", //$NON-NLS-1$
			"VanillaReport", //$NON-NLS-1$
			"Birt Report", //$NON-NLS-1$
			"Jasper Report", //$NON-NLS-1$
			"MetaData", //$NON-NLS-1$
			"Dashboard", //$NON-NLS-1$
			"Dashboard Dictionary", //$NON-NLS-1$
			"Gateway", //$NON-NLS-1$
			"Workbook", //$NON-NLS-1$
			"Workflow", //$NON-NLS-1$
			"Map", //$NON-NLS-1$
			"Orbeon Forms", //$NON-NLS-1$
			"External Document", //$NON-NLS-1$
			"Link", //$NON-NLS-1$
			"Reports Group", //$NON-NLS-1$
			"Kpi Theme" //$NON-NLS-1$
	};

	public void filterForAnalycticSupport() {
		ItemFilter f = new ItemFilter();

		this.tableViewer.setFilters(new ViewerFilter[] { f });
		this.tableViewer.refresh();
		this.viewer.setFilters(new ViewerFilter[] { f });
		this.viewer.refresh();
	}

	private static class ItemFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof TreeItem) {
				RepositoryItem it = ((TreeItem) element).getItem();
				switch (it.getType()) {
					case IRepositoryApi.FASD_TYPE:
					case IRepositoryApi.FD_TYPE:
					case IRepositoryApi.FWR_TYPE:
					case IRepositoryApi.FAV_TYPE:
						return true;
					case IRepositoryApi.CUST_TYPE:
						return it.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE;
				}
				return false;
			}
			return true;
		}

	}

	private class ItemDragerListener implements DragSourceListener {
		private Viewer viewer;

		public ItemDragerListener(Viewer viewer) {
			this.viewer = viewer;
		}

		public void dragStart(DragSourceEvent event) {
			// nothing, DnD always possible
		}

		public void dragSetData(DragSourceEvent event) {

			StructuredSelection ss = (StructuredSelection) this.viewer.getSelection();
			Object o = ss.getFirstElement();

			if (!(o instanceof TreeDirectory)) {
				event.data = ((TreeItem) o).getItem().getId() + ""; //$NON-NLS-1$
				moving = (TreeItem) o;
				event.doit = true;
			}

		}

		public void dragFinished(DragSourceEvent event) {

		}
	}

	public void removeFilters() {
		this.tableViewer.setFilters(new ViewerFilter[] {});
		this.tableViewer.refresh();
		this.viewer.setFilters(new ViewerFilter[] {});
		this.viewer.refresh();

	}
}
