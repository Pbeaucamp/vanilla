package bpm.gateway.ui.views.resources.sections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.freemetrics.FreemetricServer;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.composite.connection.CompositeConnectionSql;

public class DataBaseSection extends AbstractPropertySection {

	private CompositeConnectionSql compositeSql;
	private DataBaseConnection dataBaseConnection;
	private Group freeMetricComposite;
	private Text freemetricsLogin, freemetricsPassword;
	private Listener listener;
	private ModifyListener modifyListener;
	private Button apply, cancel;

	public DataBaseSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, true));

		compositeSql = new CompositeConnectionSql(composite, SWT.NONE);
		compositeSql.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		
		freeMetricComposite = new Group(composite, SWT.NONE);
		freeMetricComposite.setLayout(new GridLayout(2, false));
		freeMetricComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		freeMetricComposite.setText(Messages.ConnectionPage_0);
		freeMetricComposite.setBackground(white);

		Label l = new Label(freeMetricComposite, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ConnectionPage_1);
		l.setBackground(white);

		freemetricsLogin = new Text(freeMetricComposite, SWT.BORDER);
		freemetricsLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		freemetricsLogin.setBackground(white);

		Label l2 = new Label(freeMetricComposite, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.ConnectionPage_2);
		l2.setBackground(white);

		freemetricsPassword = new Text(freeMetricComposite, SWT.BORDER | SWT.PASSWORD);
		freemetricsPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		freemetricsPassword.setBackground(white);

		apply = new Button(composite, SWT.PUSH);
		apply.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		apply.setText(Messages.DataBaseSection_0);
		apply.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				compositeSql.performChanges();

				if (dataBaseConnection.getServer() != null && dataBaseConnection.getServer() instanceof FreemetricServer) {
					FreemetricServer fmServer = (FreemetricServer) dataBaseConnection.getServer();
					fmServer.setFmLogin(freemetricsLogin.getText());
					fmServer.setFmPassword(freemetricsPassword.getText());
				}

				apply.setEnabled(false);
				cancel.setEnabled(false);

				try {
					try {
						compositeSql.testConnection();
					} catch (Exception ex) {
						throw ex;
					}

					compositeSql.setError(Messages.DataBaseSection_1, null);
				} catch (Exception ex) {
					ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
					compositeSql.setError(Messages.DataBaseSection_2, reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY));

				}
			}

		});

		cancel = new Button(composite, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText(Messages.DataBaseSection_3);
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
				apply.setEnabled(false);
				cancel.setEnabled(false);
			}

		});

		listener = new Listener() {

			public void handleEvent(Event event) {
				apply.setEnabled(true);
				cancel.setEnabled(true);
			}

		};

		modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				apply.setEnabled(true);
				cancel.setEnabled(true);
			}
		};

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof DataBaseConnection);
		this.dataBaseConnection = ((DataBaseConnection) input);
	}

	@Override
	public void refresh() {
		compositeSql.removeListener(SWT.Selection, listener);
		freemetricsLogin.removeModifyListener(modifyListener);
		freemetricsPassword.removeModifyListener(modifyListener);

		compositeSql.setInput(dataBaseConnection);

		if (dataBaseConnection.getServer() != null && dataBaseConnection.getServer() instanceof FreemetricServer) {
			FreemetricServer fmServer = (FreemetricServer) dataBaseConnection.getServer();

			freemetricsLogin.setText(fmServer.getFmLogin());
			freemetricsPassword.setText(fmServer.getFmPassword());

			freemetricsLogin.setEnabled(true);
			freemetricsPassword.setEnabled(true);

			freeMetricComposite.setEnabled(true);
		}
		else {
			freemetricsLogin.setText(""); //$NON-NLS-1$
			freemetricsPassword.setText(""); //$NON-NLS-1$

			freemetricsLogin.setEnabled(false);
			freemetricsPassword.setEnabled(false);

			freeMetricComposite.setEnabled(false);
		}

		compositeSql.addListener(SWT.Selection, listener);
		freemetricsLogin.addModifyListener(modifyListener);
		freemetricsPassword.addModifyListener(modifyListener);

		apply.setEnabled(false);
		cancel.setEnabled(false);

		super.refresh();
	}

	@Override
	public void aboutToBeHidden() {
		if (compositeSql != null && !compositeSql.isDisposed()) {
			compositeSql.removeListener(SWT.Selection, listener);
		}

		if (freemetricsLogin != null && !freemetricsLogin.isDisposed()) {
			freemetricsLogin.addModifyListener(modifyListener);
		}

		if (freemetricsPassword != null && !freemetricsPassword.isDisposed()) {
			freemetricsPassword.addModifyListener(modifyListener);
		}

		super.aboutToBeHidden();

	}

	@Override
	public void aboutToBeShown() {
		compositeSql.addListener(SWT.Selection, listener);
		freemetricsLogin.addModifyListener(modifyListener);
		freemetricsPassword.addModifyListener(modifyListener);
		super.aboutToBeShown();
	}

}
