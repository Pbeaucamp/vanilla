package bpm.es.pack.manager.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.dialog.DialogText;
import bpm.es.pack.manager.I18N.Messages;
import bpm.es.pack.manager.imp.PackageManager;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.ImportHistoric;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class ViewHistoricImport extends ViewPart implements ISelectionListener {
	public static final String ID = "bpm.es.pack.manager.views.ViewHistoricImport"; //$NON-NLS-1$

	private static final Color errorColor = new Color(Display.getDefault(), 240, 10, 30);
	private static final Color warningColor = new Color(Display.getDefault(), 200, 150, 30);
	private static final Color succeedColor = new Color(Display.getDefault(), 20, 220, 30);

	private TableViewer viewer;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private class Decorator extends DecoratingLabelProvider implements ITableLabelProvider {

		public Decorator(ILabelProvider provider, ILabelDecorator decorator) {
			super(provider, decorator);
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return sdf.format(((ImportHistoric) element).getDate());
			}
			else if (columnIndex == 1) {
				String l = ((ImportHistoric) element).getLogs();
				if (l.toLowerCase().contains("error")) { //$NON-NLS-1$
					return Messages.ViewHistoricImport_0;
				}
				else if (l.toLowerCase().contains("warning")) { //$NON-NLS-1$
					return Messages.ViewHistoricImport_1;
				}
				else {
					return Messages.ViewHistoricImport_2;
				}

			}
			return null;
		}

		@Override
		public Color getForeground(Object element) {
			String l = ((ImportHistoric) element).getLogs();
			if (l.toLowerCase().contains("error")) { //$NON-NLS-1$
				return errorColor;
			}
			else if (l.toLowerCase().contains("warning")) { //$NON-NLS-1$
				return warningColor;
			}
			else {
				return succeedColor;
			}
		}

	}

	public ViewHistoricImport() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<ImportHistoric> l = (List<ImportHistoric>) inputElement;
				return l.toArray(new ImportHistoric[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		viewer.setLabelProvider(new Decorator(new LabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return ((ImportHistoric) e1).getDate().compareTo(((ImportHistoric) e2).getDate());
			}
		});
		viewer.getTable().setHeaderVisible(true);

		TableColumn colDate = new TableColumn(viewer.getTable(), SWT.NONE);
		colDate.setText(Messages.ViewHistoricImport_3);
		colDate.setWidth(150);

		TableColumn colState = new TableColumn(viewer.getTable(), SWT.NONE);
		colState.setText(Messages.ViewHistoricImport_4);
		colState.setWidth(200);

		buildContextMenu();
	}

	private void buildContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				final IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				manager.add(new Action(Messages.ViewHistoricImport_5) {
					public void run() {
						ImportHistoric i = (ImportHistoric) ss.getFirstElement();
						DialogText d = new DialogText(getSite().getShell(), i.getLogs());
						d.open();
					}
				});

			}

		});
		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));

	}

	@Override
	public void setFocus() {
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof ViewPackage) {
			PackageManager packageManager = ((ViewPackage) part).getPackageManager();

			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.getFirstElement() instanceof ExportDetails) {
				viewer.setInput(packageManager.getHistoric((ExportDetails) ss.getFirstElement()));
			}
			else if (ss.getFirstElement() instanceof VanillaPackage) {
				viewer.setInput(packageManager.getHistoric((VanillaPackage) ss.getFirstElement()));
			}

		}
		else {
			viewer.setInput(new ArrayList<ImportHistoric>());
		}
	}

}
