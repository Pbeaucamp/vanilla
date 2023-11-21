package org.fasd.views;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.FAModel;
import org.fasd.olap.SecurityProvider;
import org.freeolap.FreemetricsPlugin;

public class SecurityProviderView extends ViewPart {
	public static final String ID = "org.fasd.views.securityproviderview"; //$NON-NLS-1$

	private ListViewer viewer;
	private ToolBar toolBar;
	private Observer schObserver;
	private Action add, del;
	private ToolItem addIt, delIt;
	private UndoContext undoContext;

	public SecurityProviderView() {
		initializeOperationHistory();
	}

	public void registerObservable(Observable o) {
		o.addObserver(schObserver);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		toolBar = new ToolBar(parent, SWT.NONE);
		toolBar.setLayoutData(new GridData(SWT.FILL));

		viewer = new ListViewer(parent, SWT.BORDER);
		viewer.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof SecurityProvider) {
					return ((SecurityProvider) element).getName();
				}
				return null;
			}

		});
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				SecurityProvider[] data = (SecurityProvider[]) inputElement;
				return data;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
		createActions();
		fillToolbar();

		// listenner
		setListenner(viewer);
		registerObservable(FreemetricsPlugin.getDefault().getFAModel());
		refresh();

	}

	@Override
	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.SecurityProviderView_0 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.SecurityProviderView_1);
		}

	}

	private void initializeOperationHistory() {
		undoContext = new ObjectUndoContext(this);
	}

	public IOperationHistory getOperationHistory() {
		return PlatformUI.getWorkbench().getOperationSupport().getOperationHistory();
	}

	public void refresh() {
		if (viewer.getControl().isDisposed()) {
			return;
		}
		FAModel model = FreemetricsPlugin.getDefault().getFAModel();

		if (model == null) {
			return; // there is no data
		}
		List<SecurityProvider> root = FreemetricsPlugin.getDefault().getSecurity().getSecurityProviders();
		viewer.setInput(root.toArray(new SecurityProvider[root.size()]));

	}

	private void createActions() {
		Image img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/add.png"); //$NON-NLS-1$
		add = new Action(LanguageText.SecurityProviderView_AddSecurityProvider, ImageDescriptor.createFromImage(img)) {
			public void run() {
				FreemetricsPlugin.getDefault().getSecurity().addSecurityProvider(new SecurityProvider(LanguageText.SecurityProviderView_NewSecurityProvider));
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		};
		add.setToolTipText(add.getText());

		img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png"); //$NON-NLS-1$
		del = new Action(LanguageText.SecurityProviderView_DelServerConnection, ImageDescriptor.createFromImage(img)) {
			public void run() {
				Object o = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (o instanceof SecurityProvider) {
					FreemetricsPlugin.getDefault().getSecurity().removeSecurityProvider((SecurityProvider) o);
					FreemetricsPlugin.getDefault().getFAModel().setChanged();
				}
			}
		};
		del.setToolTipText(del.getText());
	}

	private void fillToolbar() {
		addIt = new ToolItem(toolBar, SWT.PUSH);
		addIt.setToolTipText(add.getToolTipText());
		addIt.setImage(add.getImageDescriptor().createImage());
		addIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				add.run();
			}
		});

		delIt = new ToolItem(toolBar, SWT.PUSH);
		delIt.setToolTipText(del.getToolTipText());
		delIt.setImage(del.getImageDescriptor().createImage());
		delIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				del.run();
			}
		});

	}

	private void setListenner(ListViewer list) {
		list.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					// set detail to empty
				}

				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					// we only take one
					Object o = selection.getFirstElement();

					DetailView view = ((DetailView) SecurityProviderView.this.getSite().getWorkbenchWindow().getActivePage().findView(DetailView.ID));
					view.selectionChanged(SecurityProviderView.this, selection);
				}
			}
		});

		schObserver = new Observer() {
			public void update(Observable arg0, Object arg1) {
				if (!viewer.getControl().isDisposed() && viewer.getContentProvider() != null) {
					refresh();
				}

			}

		};
	}
}
