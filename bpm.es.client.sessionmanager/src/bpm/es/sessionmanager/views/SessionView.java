package bpm.es.sessionmanager.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.icons.IconsName;
import bpm.vanilla.platform.core.repository.Repository;

public class SessionView extends ViewPart {

	public static final String ID = "bpm.es.sessionmanager.views.SessionView"; //$NON-NLS-1$

	private FormToolkit toolkit;
	private ScrolledForm form;

	private LogViewer logView;

	public SessionView() {}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.SessionView_1);

		form.getBody().setLayout(new GridLayout());

		Composite pannelRight = toolkit.createComposite(form.getBody(), SWT.NONE);
		pannelRight.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		pannelRight.setLayout(new GridLayout());

		createRightView(toolkit, pannelRight);
		createToolbar();

		loadData();
	}

	/**
	 * Log View
	 * 
	 * @param parent
	 */
	private void createRightView(FormToolkit toolkit, Composite parent) {
		logView = new LogViewer(toolkit, parent);
	}

	private void createToolbar() {

		Action refresh = new Action() {
			public void run() {
				try {
					loadData();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};

		refresh.setId(ID + ".refresh"); //$NON-NLS-1$
		refresh.setText(Messages.SessionView_3);
		refresh.setToolTipText(Messages.SessionView_4);
		refresh.setImageDescriptor(bpm.es.sessionmanager.Activator.getDefault().getImageRegistry().getDescriptor(IconsName.REFRESH));

		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(refresh);
	}

	private void loadData() {

		try {
			SessionManager manager = new SessionManager(new Repository(Activator.getDefault().getRepositoryApi()));
			manager.loadData();
			logView.showData(manager, 0);
		} catch(Throwable e) {
			e.printStackTrace(System.err);
			MessageDialog.openError(form.getShell(), Messages.SessionView_5, Messages.SessionView_6 + e.getMessage() + Messages.SessionView_7 + Messages.SessionView_8);
		}
	}

	@Override
	public void setFocus() {

	}

}
