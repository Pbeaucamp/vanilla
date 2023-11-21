package bpm.birep.admin.client.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.dialog.DialogAddWidgets;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.birep.admin.client.trees.TreeWidget;
import bpm.birep.admin.client.trees.TreeWidgetLabelProvider;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.core.beans.WidgetsGroups;

public class ViewWidgets extends ViewPart {

	public static final Color COLOR_LOCKED = new Color(Display.getDefault(), 250, 25, 120);
	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 8, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$

	public static final String ID = "bpm.birep.admin.client.views.ViewWidgets"; //$NON-NLS-1$

	private TreeViewer viewer;
	private CheckboxTreeViewer groups;

	protected List<Group> currentgroups = new ArrayList<Group>();

	@Override
	public void createPartControl(Composite parent) {

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		createToolbar(main);
		createTree(main);

		loadGroupsFromVanilla();
		groups.setInput(currentgroups);

	}

	private void createToolbar(Composite main) {

		ToolBar toolbar = new ToolBar(main, SWT.NONE);
		toolbar.setLayout(new GridLayout());

		ToolItem addWidget = new ToolItem(toolbar, SWT.PUSH);
		addWidget.setToolTipText(Messages.ViewWidgets_1);
		addWidget.setImage(Activator.getDefault().getImageRegistry().get("widget")); //$NON-NLS-1$
		addWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				DialogAddWidgets dial = new DialogAddWidgets(getSite().getShell());
				dial.open();
				try {
					createInput();
				} catch (Exception e1) {
					e1.printStackTrace();
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
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeWidget)) {
					return;
				}
				if (ss.getFirstElement() instanceof TreeWidget) {

					Widgets widget = ((TreeWidget) ss.getFirstElement()).getData();

					try {
						Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().delWidget(widget);

//						List<WidgetsGroups> lsWidget = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().getWidgetGroupByWidgetId(widget.getWidgetId());
//						for (WidgetsGroups w : lsWidget) {
//							Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().delWidgetGroup(w);
//						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				try {
					createInput();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void createTree(Composite main) {

		Composite folder = new Composite(main, SWT.NONE);
		folder.setLayout(new GridLayout(2, true));
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		Label treeWi = new Label(folder, SWT.NONE);
		treeWi.setText(Messages.ViewWidgets_3);
		treeWi.setLayoutData(new GridData());

		Label authorisedGroup = new Label(folder, SWT.NONE);
		authorisedGroup.setText(Messages.ViewWidgets_4);
		authorisedGroup.setLayoutData(new GridData());

		viewer = new TreeViewer(folder, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new TreeWidgetLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {

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

			@Override
			public Image getImage(Object element) {
				return super.getImage(element);
			}

		});
		viewer.addSelectionChangedListener(selLst);

		groups = new CheckboxTreeViewer(folder, SWT.H_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.BORDER);
		groups.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		groups.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
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

			@Override
			public boolean hasChildren(Object element) {
				for (Group g : currentgroups) {
					if (((Group) element).getId().equals(g.getParentId())) {
						return true;
					}
				}
				return false;
			}

			@Override
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

			@Override
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

		});
		groups.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
		groups.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeWidget)) {
					return;
				}

				if (ss.getFirstElement() instanceof TreeWidget) {
					Group group = (Group) event.getElement();

					Widgets widget = ((TreeWidget) ss.getFirstElement()).getData();

					if (event.getChecked()) {
						WidgetsGroups widgetGroup = new WidgetsGroups();
						widgetGroup.setWidgetId(widget.getWidgetId());
						widgetGroup.setIdGroup(group.getId());

//						try {
//							Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().addWidgetGroup(widgetGroup);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
					}
					else if (!event.getChecked()) {
						WidgetsGroups widgetGroup = new WidgetsGroups();
						widgetGroup.setWidgetId(widget.getWidgetId());
						widgetGroup.setIdGroup(group.getId());

						WidgetsGroups exist = null;
						try {
//							// /WHAT THE FUCK - OMG
//							exist = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().findIfWidgetGroupExist(exist);
//							if (exist.getIdGroup() == widgetGroup.getIdGroup() && exist.getWidgetId() == widgetGroup.getWidgetId()) {
//								Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().delWidgetGroup(exist);
//							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		try {
			createInput();
		} catch (Exception e) {
			MessageDialog.openError(getSite().getShell(), Messages.ViewTree_49, e.getMessage());
		}
	}

	private void createInput() throws Exception {
//		TreeParent<TreeWidget> treeNetvibes = new TreeParent<TreeWidget>(Widgets.NETVIBES);
//		List<Widgets> netvibesWidgets = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().getWidgetByType(Widgets.NETVIBES);
//		for (Widgets widget : netvibesWidgets) {
//			TreeWidget treeWidget = new TreeWidget(widget.getName(), widget);
//			treeNetvibes.addChild(treeWidget);
//		}
//
//		TreeParent<TreeWidget> treeFluxRss = new TreeParent<TreeWidget>(Widgets.FLUX_RSS);
//		List<Widgets> fluxRssWidgets = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().getWidgetByType(Widgets.FLUX_RSS);
//		for (Widgets widget : fluxRssWidgets) {
//			TreeWidget treeWidget = new TreeWidget(widget.getName(), widget);
//			treeFluxRss.addChild(treeWidget);
//		}
//
//		TreeParent<TreeWidget> treeMore = new TreeParent<TreeWidget>(Widgets.AUTRE);
//		List<Widgets> moreWidgets = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().getWidgetByType(Widgets.AUTRE);
//		for (Widgets widget : moreWidgets) {
//			TreeWidget treeWidget = new TreeWidget(widget.getName(), widget);
//			treeMore.addChild(treeWidget);
//		}
//
//		TreeParent<TreeParent<TreeWidget>> treeWidgets = new TreeParent<TreeParent<TreeWidget>>(Widgets.WIDGETS);
//		treeWidgets.addChild(treeNetvibes);
//		treeWidgets.addChild(treeFluxRss);
//		treeWidgets.addChild(treeMore);
//
//		viewer.setInput(treeWidgets);
	}

	@Override
	public void setFocus() {
	}

	private void loadGroupsFromVanilla() {

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

			@Override
			public void run() {
				currentgroups.clear();
				try {
					currentgroups.addAll(Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups());
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewWidgets_5, Messages.ViewWidgets_6 + ex.getMessage());
				}
			}
		});

	}

	protected void initGroups(Widgets widgets) throws Exception {
//		List<WidgetsGroups> widgetGroup = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().getWidgetGroupByWidgetId(widgets.getWidgetId());
//
//		for (Group g : (List<Group>) groups.getInput()) {
//			boolean find = false;
//
//			for (WidgetsGroups w : widgetGroup) {
//				if (w.getIdGroup() == g.getId()) {
//					find = true;
//					break;
//				}
//			}
//			groups.setChecked(g, find);
//		}
	}

	private ISelectionChangedListener selLst = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection) event.getSelection();
			if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeWidget)) {
				for (Group g : (List<Group>) groups.getInput()) {
					groups.setChecked(g, false);
				}
				return;
			}

			if (ss.getFirstElement() instanceof TreeWidget) {
				try {
					initGroups((Widgets) ((TreeWidget) ss.getFirstElement()).getData());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
}
