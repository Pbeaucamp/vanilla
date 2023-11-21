package bpm.es.clustering.ui.composites;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.progress.IProgressService;

import bpm.es.clustering.ui.Messages;
import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.core.components.IVanillaComponent.Status;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;

public class ModuleDetailPage implements IDetailsPage {

	private IManagedForm managedForm;
	private Text name;
	private Text url;

	private Label lstatus;
	private Text status;

	private Button startButton;
	private Button stopButton;

	private Server input;

	private ModuleDetailPage us = this;

	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		Section s = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		s.setLayout(new GridLayout());
		s.setLayoutData(new GridData(GridData.FILL_BOTH));
		s.setText(Messages.ModuleDetailPage_0);
		s.setDescription(Messages.ModuleDetailPage_1);

		Composite client = toolkit.createComposite(s);
		client.setLayout(new GridLayout(2, false));
		client.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = toolkit.createLabel(client, Messages.ModuleDetailPage_2);
		l.setLayoutData(new GridData());

		name = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setEnabled(false);

		l = toolkit.createLabel(client, Messages.ModuleDetailPage_4);
		l.setLayoutData(new GridData());

		url = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.setEnabled(false);

		lstatus = toolkit.createLabel(client, Messages.ModuleDetailPage_5);
		lstatus.setLayoutData(new GridData());

		status = toolkit.createText(client, "", SWT.FLAT); //$NON-NLS-1$
		status.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		status.setEnabled(false);

		startButton = toolkit.createButton(client, Messages.ModuleDetailPage_6, SWT.PUSH);
		startButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IRunnableWithProgress r = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							monitor.beginTask(Messages.ModuleDetailPage_3, 2);

							Activator.getDefault().getVanillaApi().getVanillaSystemManager().startNodeComponent(input);

							Thread.sleep(1500);

							StructuredSelection ss = new StructuredSelection(new RefreshSelection());

							managedForm.fireSelectionChanged(us, ss);
						} catch (Exception ex) {
							throw new InvocationTargetException(ex);
						}
					}
				};

				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				try {
					service.run(false, false, r);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(name.getShell(), Messages.ModuleDetailPage_8, Messages.ModuleDetailPage_90 + ": " + ex.getCause().getMessage()); //$NON-NLS-1$
				}
			}
		});
		stopButton = toolkit.createButton(client, Messages.ModuleDetailPage_7, SWT.PUSH);
		stopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				IRunnableWithProgress r = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							monitor.beginTask(Messages.ModuleDetailPage_10, 2);

							Activator.getDefault().getVanillaApi().getVanillaSystemManager().stopNodeComponent(input);

							Thread.sleep(1500);

							StructuredSelection ss = new StructuredSelection(new RefreshSelection());

							managedForm.fireSelectionChanged(us, ss);
						} catch (Exception ex) {
							throw new InvocationTargetException(ex);
						}
					}
				};

				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				try {
					service.run(false, false, r);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(name.getShell(), Messages.ModuleDetailPage_8, Messages.ModuleDetailPage_91 + ": " + ex.getCause().getMessage()); //$NON-NLS-1$
				}
				try {
				} catch (Exception ex) {
				}
			}
		});

		s.setClient(client);

		toolkit.paintBordersFor(s);

		managedForm.addPart(new SectionPart(s));
		parent.layout();

	}

	public void commit(boolean onSave) {
	}

	public void dispose() {
	}

	public void initialize(IManagedForm form) {
		managedForm = form;
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;

		if (ss.isEmpty()) {
			input = null;
		}
		else {
			input = (Server) ss.getFirstElement();
		}

		update();

	}

	private void update() {
		if (input == null) {
			name.setText(""); //$NON-NLS-1$
			url.setText(""); //$NON-NLS-1$
			status.setText(""); //$NON-NLS-1$
		}
		else {
			name.setText(input.getName() != null ? input.getName() : ""); //$NON-NLS-1$
			url.setText(input.getUrl() != null ? input.getUrl() : ""); //$NON-NLS-1$
			if (input.getComponentStatus() == null) {
				status.setEnabled(false);
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
				status.setText(Messages.ModuleDetailPage_12);
			}
			else {
				status.setEnabled(false);
				if (input.getComponentStatus().equals(Status.STARTED.getStatus())) {
					startButton.setEnabled(false);
					stopButton.setEnabled(true);
				}
				else if (input.getComponentStatus().equals(Status.STOPPED.getStatus())) {
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
				}
				else {
					startButton.setEnabled(false);
					stopButton.setEnabled(false);
				}
				status.setText(input.getComponentStatus()); //$NON-NLS-1$

			}

		}
	}

}
