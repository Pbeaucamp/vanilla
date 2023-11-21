package bpm.es.dndserver.test;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import bpm.es.dndserver.Messages;

public class WelcomeView extends ViewPart {
	
	public static final String ID = "bpm.es.sessionmanager.views.SessionView"; //$NON-NLS-1$
	private TableViewer tableViewer;
	
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private LogViewer logView;
	
	public WelcomeView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.WelcomeView_1);
		
		form.getBody().setLayout(new GridLayout(2, false));

		Composite pannelLeft = toolkit.createComposite(form.getBody(), SWT.NONE);
		pannelLeft.setLayout(new GridLayout());
		pannelLeft.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		Composite pannelRight = toolkit.createComposite(form.getBody(), SWT.NONE);
		pannelRight.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		pannelRight.setLayout(new GridLayout());

		//createLeftView(toolkit, pannelLeft);
		createMainView(toolkit, pannelRight);
		createToolbar();
		
		loadData();
	}
	
	/**
	 * Main View
	 * @param parent
	 */
	private void createMainView(FormToolkit toolkit, Composite parent) {
		logView = new LogViewer(toolkit, parent);
	}
	
	private void createToolbar(){
		
		Action refresh = new Action(){
			public void run(){
				try {
					loadData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		refresh.setId(ID + Messages.WelcomeView_2);
		refresh.setText(Messages.WelcomeView_3);
		refresh.setToolTipText(Messages.WelcomeView_4);
		//refresh.setImageDescriptor(bpm.es.sessionmanager.Activator.getDefault().getImageRegistry().getDescriptor(IconsName.REFRESH));
		
		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(refresh);
	}
	
	private void loadData() {
		
		try {
//			SessionManager manager = new SessionManager(Activator.getDefault().getManager());
//			manager.loadData();
//			userView.showData(manager);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			MessageDialog.openError(form.getShell(), Messages.WelcomeView_5, 
					"" + e.getMessage() + Messages.WelcomeView_7 + //$NON-NLS-1$
							Messages.WelcomeView_8);
		}
	}

	@Override
	public void setFocus() {
		
	}

}
