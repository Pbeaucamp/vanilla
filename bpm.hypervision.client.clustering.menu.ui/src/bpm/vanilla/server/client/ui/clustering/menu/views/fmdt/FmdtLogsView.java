package bpm.vanilla.server.client.ui.clustering.menu.views.fmdt;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

import bpm.vanilla.platform.core.beans.FMDTQueryBean;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.icons.Icons;
import bpm.vanilla.server.client.ui.clustering.menu.views.fmdt.FmdtLogLabelProvider.Type;

public class FmdtLogsView extends ViewPart {
	public static final String ID = "bpm.vanilla.server.client.ui.clustering.menu.views.fmdt.FmdtLogsView"; //$NON-NLS-1$
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private TableViewer queryViewer;
	private ComboViewer repositoryViewer;// , itemViewer;
	private FmdtLogFilter filter = new FmdtLogFilter();

	public FmdtLogsView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Form frmUnitedolapModelsManagement = formToolkit.createForm(parent);
		frmUnitedolapModelsManagement.setImage(Activator.getDefault().getImageRegistry().get(Icons.MDX_QUERY_MGMT));
		formToolkit.paintBordersFor(frmUnitedolapModelsManagement);
		frmUnitedolapModelsManagement.setText(Messages.FmdtLogsView_1);
		frmUnitedolapModelsManagement.getBody().setLayout(new GridLayout(1, false));
		formToolkit.decorateFormHeading(frmUnitedolapModelsManagement);

		Composite composite = new Composite(frmUnitedolapModelsManagement.getBody(), SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);

		Section sctnFilters = formToolkit.createSection(composite, Section.TWISTIE | Section.TITLE_BAR);
		sctnFilters.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.paintBordersFor(sctnFilters);
		sctnFilters.setText(Messages.FmdtLogsView_2);
		sctnFilters.setExpanded(false);

		Composite composite_1 = new Composite(sctnFilters, SWT.NONE);
		formToolkit.adapt(composite_1);
		formToolkit.paintBordersFor(composite_1);
		sctnFilters.setClient(composite_1);
		composite_1.setLayout(new GridLayout(2, true));

		Group grpFmdtModelOrigin = new Group(composite_1, SWT.NONE);
		grpFmdtModelOrigin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpFmdtModelOrigin.setText(Messages.FmdtLogsView_3);
		grpFmdtModelOrigin.setBounds(0, 0, 70, 82);
		formToolkit.adapt(grpFmdtModelOrigin);
		formToolkit.paintBordersFor(grpFmdtModelOrigin);
		grpFmdtModelOrigin.setLayout(new GridLayout(2, false));

		final Button btnRepository = new Button(grpFmdtModelOrigin, SWT.CHECK);
		formToolkit.adapt(btnRepository, true, true);
		btnRepository.setText(Messages.FmdtLogsView_4);

		repositoryViewer = new ComboViewer(grpFmdtModelOrigin, SWT.READ_ONLY);
		repositoryViewer.setContentProvider(new ArrayContentProvider());
		repositoryViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Repository) element).getName();
			}
		});
		repositoryViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					filter.setRepositoryId(null);
					filter.setDirectoryItemId(null);
				}
				else {
					filter.setRepositoryId(((Repository) ((IStructuredSelection) event.getSelection()).getFirstElement()).getId());
					filter.setDirectoryItemId(null);
				}
				queryViewer.refresh();
			}
		});

		Combo combo = repositoryViewer.getCombo();
		combo.setEnabled(false);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.paintBordersFor(combo);

		// filter Item behavior
		btnRepository.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				repositoryViewer.getControl().setEnabled(btnRepository.getSelection());

				try {
					repositoryViewer.setInput(Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories());
				} catch (Exception ex) {
					repositoryViewer.setInput(Collections.EMPTY_LIST);
					ex.printStackTrace();
					MessageDialog.openInformation(getSite().getShell(), Messages.FmdtLogsView_5, Messages.FmdtLogsView_6 + ex.getMessage());
				}

				if (!btnRepository.getSelection()) {
					filter.setRepositoryId(null);
					filter.setDirectoryItemId(null);

					queryViewer.refresh();
				}
			}
		});

		Group grpExecutiondate = new Group(composite_1, SWT.NONE);
		grpExecutiondate.setLayout(new GridLayout(1, false));
		grpExecutiondate.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		grpExecutiondate.setText(Messages.FmdtLogsView_7);
		grpExecutiondate.setBounds(0, 0, 70, 82);
		formToolkit.adapt(grpExecutiondate);
		formToolkit.paintBordersFor(grpExecutiondate);

		Composite composite_2 = new Composite(grpExecutiondate, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		formToolkit.adapt(composite_2);
		formToolkit.paintBordersFor(composite_2);
		composite_2.setLayout(new GridLayout(2, false));

		final Button btnFrom = new Button(composite_2, SWT.CHECK);
		btnFrom.setBounds(0, 0, 75, 25);
		formToolkit.adapt(btnFrom, true, true);
		btnFrom.setText(Messages.FmdtLogsView_8);

		final DateTime dateTime = new DateTime(composite_2, SWT.BORDER);
		dateTime.setEnabled(false);
		dateTime.setBounds(0, 0, 85, 24);
		formToolkit.adapt(dateTime);
		formToolkit.paintBordersFor(dateTime);
		dateTime.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Calendar c = Calendar.getInstance();
				c.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
				filter.setFrom(c.getTime());
				
				queryViewer.refresh();
			}
		});

		final Button btnTo = new Button(composite_2, SWT.CHECK);
		btnTo.setEnabled(false);
		btnTo.setBounds(0, 0, 75, 25);
		formToolkit.adapt(btnTo, true, true);
		btnTo.setText(Messages.FmdtLogsView_9);

		btnFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dateTime.setEnabled(btnFrom.getSelection());

				if (btnFrom.getSelection()) {
					Calendar c = Calendar.getInstance();
					c.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
					filter.setFrom(c.getTime());
				}
				else {
					filter.setFrom(null);
					filter.setTo(null);
				}

				btnTo.setEnabled(btnFrom.getSelection());
				btnTo.setSelection(false);

				queryViewer.refresh();
			}
		});


		final DateTime dateTime_1 = new DateTime(composite_2, SWT.BORDER);
		dateTime_1.setEnabled(false);
		dateTime_1.setBounds(0, 0, 85, 24);
		formToolkit.adapt(dateTime_1);
		formToolkit.paintBordersFor(dateTime_1);
		dateTime_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Calendar c = Calendar.getInstance();
				c.set(dateTime_1.getYear(), dateTime_1.getMonth(), dateTime_1.getDay());
				filter.setTo(c.getTime());
				
				queryViewer.refresh();
			}
		});

		btnTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dateTime_1.setEnabled(btnFrom.getSelection());

				if (btnTo.getSelection()) {
					Calendar c = Calendar.getInstance();
					c.set(dateTime_1.getYear(), dateTime_1.getMonth(), dateTime_1.getDay());
					filter.setTo(c.getTime());
				}
				else {
					filter.setTo(null);
				}

				queryViewer.refresh();
			}
		});

		Section sctnLoggedQueries = formToolkit.createSection(composite, Section.TITLE_BAR | Section.DESCRIPTION);
		sctnLoggedQueries.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		formToolkit.paintBordersFor(sctnLoggedQueries);
		sctnLoggedQueries.setText(Messages.FmdtLogsView_10);
		sctnLoggedQueries.setDescription(Messages.FmdtLogsView_11);

		Composite composite_3 = new Composite(sctnLoggedQueries, SWT.NONE);
		formToolkit.adapt(composite_3);
		formToolkit.paintBordersFor(composite_3);
		sctnLoggedQueries.setClient(composite_3);
		composite_3.setLayout(new GridLayout(2, false));

		ToolBar toolbar = createToolbar(composite_3);
		formToolkit.adapt(toolbar);
		formToolkit.paintBordersFor(toolbar);
		toolbar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));

		queryViewer = new TableViewer(composite_3, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.VIRTUAL);
		queryViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (queryViewer.getSelection().isEmpty()) {
					return;
				}
				IStructuredSelection ss = (IStructuredSelection) queryViewer.getSelection();

				DialogFmdtQuery d = new DialogFmdtQuery(getSite().getShell(), (FMDTQueryBean) ss.getFirstElement());
				d.open();

			}
		});
		queryViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		queryViewer.setContentProvider(new ArrayContentProvider());
		queryViewer.getTable().setHeaderVisible(true);
		queryViewer.getTable().setLinesVisible(true);
		queryViewer.setFilters(new ViewerFilter[] { filter });
		formToolkit.paintBordersFor(queryViewer.getTable());

		/*
		 * create Table
		 */
		TableViewerColumn col = new TableViewerColumn(queryViewer, SWT.LEFT);
		col.getColumn().setText(Messages.FmdtLogsView_12);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FmdtLogLabelProvider(Type.REP));

		col = new TableViewerColumn(queryViewer, SWT.LEFT);
		col.getColumn().setText(Messages.FmdtLogsView_13);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FmdtLogLabelProvider(Type.IT));

		col = new TableViewerColumn(queryViewer, SWT.LEFT);
		col.getColumn().setText(Messages.FmdtLogsView_14);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new FmdtLogLabelProvider(Type.GROUP));

		col = new TableViewerColumn(queryViewer, SWT.LEFT);
		col.getColumn().setText(Messages.FmdtLogsView_15);
		col.getColumn().setWidth(110);
		col.setLabelProvider(new FmdtLogLabelProvider(Type.DATE));

		col = new TableViewerColumn(queryViewer, SWT.LEFT);
		col.getColumn().setText(Messages.FmdtLogsView_16);
		col.getColumn().setWidth(50);
		col.setLabelProvider(new FmdtLogLabelProvider(Type.TIME));

		col = new TableViewerColumn(queryViewer, SWT.LEFT);
		col.getColumn().setText(Messages.FmdtLogsView_0);
		col.getColumn().setWidth(50);
		col.setLabelProvider(new FmdtLogLabelProvider(Type.WEIGHT));

	}

	private ToolBar createToolbar(Composite composite_3) {
		ToolBarManager tbm = new ToolBarManager(SWT.VERTICAL);

		Action reload = new Action() {
			public void run() {
				IRunnableWithProgress r = new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.setTaskName(Messages.FmdtLogsView_17);
						monitor.beginTask(Messages.FmdtLogsView_18, 4);
						try {
							monitor.subTask(Messages.FmdtLogsView_28);
							List<bpm.vanilla.platform.core.beans.Group> g = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
							monitor.worked(1);
							monitor.subTask(Messages.FmdtLogsView_29);
							List<Repository> r = Activator.getDefault().getVanillaApi().getVanillaRepositoryManager().getRepositories();
							monitor.worked(1);

							FmdtLogLabelProvider.shareVanillaObjects(g, r);
							monitor.subTask(Messages.FmdtLogsView_18);
							Object datas = Activator.getDefault().getVanillaApi().getVanillaLoggingManager().getFmdtQueries(300);
							monitor.subTask(Messages.FmdtLogsView_19);
							queryViewer.setInput(datas);
							monitor.worked(2);
						} catch (Throwable ex) {
							ex.printStackTrace();
							queryViewer.setInput(Collections.EMPTY_LIST);
							MessageDialog.openError(getSite().getShell(), Messages.FmdtLogsView_20, Messages.FmdtLogsView_21 + ex.getMessage());
						}

					}
				};
				IProgressService service = PlatformUI.getWorkbench().getProgressService();
				try {
					service.run(false, false, r);
				} catch (Throwable e) {
					MessageDialog.openError(getSite().getShell(), Messages.FmdtLogsView_22, Messages.FmdtLogsView_23 + e.getMessage());
				}
			}
		};
		reload.setToolTipText(Messages.FmdtLogsView_24);
		reload.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.RESET));

		tbm.add(reload);

		Action delete = new Action() {
			public void run() {

				for (Object o : ((IStructuredSelection) queryViewer.getSelection()).toList()) {
					try {
						Activator.getDefault().getVanillaApi().getVanillaLoggingManager().deleteFmdtQuery((FMDTQueryBean) o);
					} catch (Exception ex) {
					}
				}
				try {
					queryViewer.setInput(Activator.getDefault().getVanillaApi().getVanillaLoggingManager().getFmdtQueries(300));
				} catch (Exception ex) {
					ex.printStackTrace();
					queryViewer.setInput(Collections.EMPTY_LIST);
					MessageDialog.openError(getSite().getShell(), Messages.FmdtLogsView_25, Messages.FmdtLogsView_26 + ex.getMessage());
				}
			}
		};
		delete.setToolTipText(Messages.FmdtLogsView_27);
		delete.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.CLEAR_CACHE));

		tbm.add(delete);

		return tbm.createControl(composite_3);
	}

	@Override
	public void setFocus() {
		

	}
}
