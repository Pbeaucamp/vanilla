package bpm.gateway.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.IServerConnection;
import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.TreeObject;
import bpm.gateway.ui.viewer.TreeStaticObject;
import bpm.gateway.ui.views.resources.actions.ActionBrowseTables;
import bpm.gateway.ui.views.resources.actions.ActionConnectionActivation;
import bpm.gateway.ui.views.resources.actions.ActionCreateConnection;
import bpm.gateway.ui.views.resources.actions.ActionDelete;
import bpm.gateway.ui.views.resources.actions.ActionModify;

public class ResourceViewPart extends ViewPart implements ITabbedPropertySheetPageContributor, PropertyChangeListener {

	public static final String ID = "bpm.gateway.ui.views.resources"; //$NON-NLS-1$

	public static final Font FONT_ACTIVE_CONNECTION = new Font(Display.getCurrent(), "Arial", 10, SWT.BOLD | SWT.ITALIC); //$NON-NLS-1$

	private TreeViewer resourcesViewer;

	private Action delete, connectionActivation, createConnection, modifyParameter, browseTables;

	public ResourceViewPart() {
		ResourceManager.getInstance().addPropertyChangeListener(this);
	}

	@Override
	public void dispose() {
		ResourceManager.getInstance().removePropertyChangeListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());

		resourcesViewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		resourcesViewer.setContentProvider(new ResourcesContentProvider());
		LabelProvider labelProvider = new LabelProvider() {

			@Override
			public String getText(Object element) {

				if (element instanceof TreeObject) {
					return ((TreeObject) element).getName();
				}
				else if (element instanceof Server) {
					return ((Server) element).getName();
				}
				else if (element instanceof IServerConnection) {
					return ((IServerConnection) element).getName();
				}
				else if (element instanceof Variable) {
					return ((Variable) element).getOuputName();
				}
				else if (element instanceof Parameter) {
					return ((Parameter) element).getOuputName();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				return ResourceViewHelper.getImageFor(element);
			}

		};

		DecoratingLabelProvider decoProvider = new DecoratingLabelProvider(labelProvider, null) {

			@Override
			public Font getFont(Object element) {
				if (element instanceof IServerConnection) {

					IServerConnection sock = (IServerConnection) element;
					if (sock.getServer().getCurrentConnection(null) == sock) {
						return FONT_ACTIVE_CONNECTION;
					}

				}
				return null;

			}

		};

		resourcesViewer.setLabelProvider(decoProvider);
		ResourceViewHelper.createTree(resourcesViewer);

		createActions();
		createContextMenu();

		getSite().setSelectionProvider(resourcesViewer);
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(this);
		return super.getAdapter(adapter);
	}

	@Override
	public void setFocus() {
		resourcesViewer.getTree().setFocus();

	}

	/**
	 * refresh the TreeViewer
	 */
	public void refresh() {
		resourcesViewer.refresh();

	}

	private void createContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				IStructuredSelection ss = (IStructuredSelection) resourcesViewer.getSelection();
				Object o = ss.getFirstElement();

				if (!(o instanceof TreeStaticObject)) {
					menuMgr.add(delete);
				}

				if (o instanceof IServerConnection) {
					menuMgr.add(connectionActivation);
					if (((IServerConnection) o).getServer().getCurrentConnection(null) == o) {
						connectionActivation.setEnabled(false);
					}
					else {
						connectionActivation.setEnabled(true);
					}
				}

				if (o instanceof Server) {
					menuMgr.add(createConnection);
				}

				if (o instanceof Parameter) {
					menuMgr.add(modifyParameter);
				}

				if (o instanceof DataBaseServer || o instanceof DataBaseConnection) {
					menuMgr.add(browseTables);
				}
			}
		});

		resourcesViewer.getControl().setMenu(menuMgr.createContextMenu(resourcesViewer.getControl()));
	}

	private void createActions() {
		delete = new ActionDelete(Messages.ResourceViewPart_2, resourcesViewer);
		connectionActivation = new ActionConnectionActivation(Messages.ResourceViewPart_3, resourcesViewer);
		createConnection = new ActionCreateConnection(Messages.ResourceViewPart_4, resourcesViewer);
		modifyParameter = new ActionModify(Messages.ResourceViewPart_0, resourcesViewer);
		browseTables = new ActionBrowseTables(Messages.ResourceViewPart_1, resourcesViewer);
	}

	public String getContributorId() {
		return ID;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}
}
