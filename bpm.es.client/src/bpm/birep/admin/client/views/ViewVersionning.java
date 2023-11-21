package bpm.birep.admin.client.views;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.dialog.DialogXmlViewer;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Revision;

public class ViewVersionning extends ViewPart implements ISelectionListener {

	public static final String ID = "bpm.birep.admin.client.views.VersionningView"; //$NON-NLS-1$
	private TableViewer viewer;
	private Action revertToVersion, editXml;

	private RepositoryItem selected = null;
	
	private IVanillaAPI vanillaApi;

	public ViewVersionning() {
		this.vanillaApi = Activator.getDefault().getVanillaApi();
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removeSelectionListener(this);	
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new TableViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
					case 0:
						return ((Revision) element).getRevisionNumber() + ""; //$NON-NLS-1$
					case 1:
						return getUser(((Revision) element).getUserId());
					case 2:
						return ((Revision) element).getDate() + ""; //$NON-NLS-1$
//					case 3:
//						return ((Revision) element).getComment();
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

		});
		viewer.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<Revision> l = (List<Revision>) inputElement;
				return l.toArray(new Revision[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn number = new TableColumn(viewer.getTable(), SWT.NONE);
		number.setText(Messages.ViewVersionning_4);
		number.setWidth(100);

		TableColumn user = new TableColumn(viewer.getTable(), SWT.NONE);
		user.setText(Messages.ViewVersionning_5);
		user.setWidth(200);

		TableColumn date = new TableColumn(viewer.getTable(), SWT.NONE);
		date.setText(Messages.ViewVersionning_6);
		date.setWidth(100);

		viewer.getTable().setHeaderVisible(true);

		this.getViewSite().setSelectionProvider(viewer);

		/*
		 * contextual Menu
		 */
		createActions();

		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				if (!viewer.getSelection().isEmpty() && selected != null) {
					menuMgr.add(revertToVersion);
					menuMgr.add(editXml);
//					menuMgr.add(changeComment);
				}
			}
		});

		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));
	}

	protected String getUser(int userId) {
		try {
			User user = vanillaApi.getVanillaSecurityManager().getUserById(userId);
			return user != null ? user.getName() : Messages.ViewVersionning_0;
		} catch (Exception e) {
			e.printStackTrace();
			return Messages.ViewVersionning_1;
		}
	}

	private void createActions() {
		revertToVersion = new Action(Messages.ViewVersionning_8) {
			public void run() {
				Revision r = (Revision) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				try {

					InputDialog in = new InputDialog(getSite().getShell(), Messages.ViewVersionning_9, Messages.ViewVersionning_10, Messages.ViewVersionning_11 + r.getRevisionNumber(), null);
					if (!(in.open() == Dialog.OK)) {
						return;
					}

					Activator.getDefault().getRepositoryApi().getVersioningService().revertToRevision(selected, r.getRevisionNumber(), in.getValue());
					List<Revision> l = Activator.getDefault().getRepositoryApi().getVersioningService().getRevisions(selected);
					viewer.setInput(l);
					MessageDialog.openInformation(getSite().getShell(), Messages.ViewVersionning_12, Messages.ViewVersionning_13);

				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewVersionning_14, e.getMessage());
				}

			}
		};

		editXml = new Action(Messages.ViewVersionning_15) {
			public void run() {
				Revision r = (Revision) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				try {
					InputStream st = Activator.getDefault().getRepositoryApi().getVersioningService().getRevision(selected, r.getRevisionNumber());
					
					String xml = IOUtils.toString(st, "UTF-8"); //$NON-NLS-1$
					st.close();
					
					DialogXmlViewer dial = new DialogXmlViewer(getSite().getShell(), xml);
					dial.open();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewVersionning_16, e.getMessage());
				}

			}
		};
	}

	@Override
	public void setFocus() { }

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof ViewTree)) {
			return;
		}

		IStructuredSelection ss = (IStructuredSelection) selection;

		if (!(ss.getFirstElement() instanceof TreeItem)) {
			selected = null;
			return;
		}

		try {
			selected = ((TreeItem) ss.getFirstElement()).getItem();
			List<Revision> l = Activator.getDefault().getRepositoryApi().getVersioningService().getRevisions(selected);
			viewer.setInput(l);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
