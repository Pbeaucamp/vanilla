package bpm.vanilla.server.ui.views.actions.gateway;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.menus.IMenuService;

import bpm.vanilla.platform.core.beans.tasks.TaskInfo;
import bpm.vanilla.platform.core.components.WorkflowService;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState;
import bpm.vanilla.platform.core.components.workflow.WorkflowInstanceState.StepInfos;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunIdentifier;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.views.VisualConstants;
import bpm.vanilla.server.ui.views.actions.ActionFullDescription;

public class DialogWorkflowStepsInfos extends Dialog {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

	private static final String NAME = "stepName"; //$NON-NLS-1$
	private static final String STATE = "stepState"; //$NON-NLS-1$
	private static final String START_TIME = "stepStartTime"; //$NON-NLS-1$
	private static final String STOP_TIME = "stepStopTime"; //$NON-NLS-1$
	private static final String DURATION = "stepDuration"; //$NON-NLS-1$
	private static final String NATURE = "nature"; //$NON-NLS-1$
	private static final String MANUAL_URL = "manualUrl"; //$NON-NLS-1$

	private static Color ERROR = new Color(Display.getDefault(), 250, 25, 0);
	private static Color OK = new Color(Display.getDefault(), 0, 250, 0);
//	private static Color WARN = new Color(Display.getDefault(), 0, 128, 255);

	private String[] cols = new String[] { NAME, STATE, START_TIME, STOP_TIME, DURATION, NATURE, MANUAL_URL };
	private WorkflowInstanceState runtimeState;
	private TaskInfo task;
	
	private StepInfos selectedStep;

	private TableViewer viewer;
	private Monitor monitor;

	public DialogWorkflowStepsInfos(Shell parentShell, TaskInfo task, WorkflowInstanceState runtimeState) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.runtimeState = runtimeState;
		this.task = task;
	}

	public DialogWorkflowStepsInfos(Shell parentShell, WorkflowInstanceState runtimeState) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.runtimeState = runtimeState;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<StepInfos> l = (List<StepInfos>) inputElement;
				return l.toArray(new Object[l.size()]);
			}
		});
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);

		for (final String s : cols) {
			TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
			col.getColumn().setWidth(100);
			String txt = new String(s);
			if (txt.startsWith("step")) { //$NON-NLS-1$
				txt = txt.substring(4);
			}
			col.getColumn().setText(txt);
			col.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					StepInfos step = ((StepInfos) element);
					if (s.equals(NAME)) {
						return step.getStepName();
					}
					else if (s.equals(STATE)) {
						switch (step.getState()) {
							case INITIAL:
								return Messages.DialogWorkflowStepsInfos_7;
							case ENDED:
								return Messages.DialogWorkflowStepsInfos_8;
							case FAILED:
								return Messages.DialogWorkflowStepsInfos_9;
							case READY:
								return Messages.DialogWorkflowStepsInfos_10;
							case RUNNING:
								return Messages.DialogWorkflowStepsInfos_11;
							case STARTED:
								return Messages.DialogWorkflowStepsInfos_12;
							case SUSPENDED:
								return Messages.DialogWorkflowStepsInfos_13;
							case UNKNOWN:
								return Messages.DialogWorkflowStepsInfos_14;
							case WAITING:
								return Messages.DialogWorkflowStepsInfos_15;

							default:
								return ""; //$NON-NLS-1$
						}
					}
					else if (s.equals(START_TIME)) {
						if (step.getStartDate() != null) {
							return sdf.format(step.getStartDate());
						}
						else {
							return ""; //$NON-NLS-1$
						}
					}
					else if (s.equals(STOP_TIME)) {
						if (step.getStopedDate() != null) {
							return sdf.format(step.getStopedDate());
						}
						else {
							return ""; //$NON-NLS-1$
						}
					}
					else if (s.equals(DURATION)) {
						return getElapsedTime(step.getDuration());
					}
					else if (s.equals(NATURE)) {
						switch (step.getNature()) {
							case Automatic:
								return Messages.DialogWorkflowStepsInfos_19;
							case Manuel:
								return Messages.DialogWorkflowStepsInfos_20;
							default:
								return ""; //$NON-NLS-1$
						}
					}
					else if (s.equals(MANUAL_URL)) {
						return step.getManualStepUrl();
					}
					else {
						return ""; //$NON-NLS-1$
					}
				}

				@Override
				public Color getBackground(Object element) {
					switch (((StepInfos) element).getState()) {
						case FAILED:
							return ERROR;

						default:
							return OK;
					}
				}
			});
		}
		createTaskContextMenu();
		return viewer.getTable();
	}

	public String getElapsedTime(Long time) {
		if (time == null) {
			return " - - "; //$NON-NLS-1$
		}
		else {
			Double mill = time.doubleValue() % 1000;

			double secTmp = Math.floor(time.doubleValue() / 1000);
			Double sec = secTmp % 60;

			double minTmp = Math.floor(secTmp / 60);
			Double min = minTmp % 60;

			double hTmp = Math.floor(minTmp / 60);
			Double h = hTmp % 24;

			return h.intValue() + Messages.DialogWorkflowStepsInfos_0 + min.intValue() + Messages.DialogWorkflowStepsInfos_25 + sec.intValue() + Messages.DialogWorkflowStepsInfos_26 + mill.intValue() + Messages.DialogWorkflowStepsInfos_27;
		}
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(1000, 300);
		getShell().setText(Messages.DialogWorkflowStepsInfos_28);
		viewer.setInput(runtimeState.getStepInfos());

		if (task != null) {
			monitor = new Monitor();
			monitor.start();
		}
	}

	@Override
	protected void okPressed() {
		if (monitor != null) {
			monitor.active = false;
			try {
				monitor.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	private void createTaskContextMenu() {
		MenuManager mgr = new MenuManager("Actions", "bpm.vanilla.server.ui.views.DialogWorkflowStepsInfos.contextMenu"); //$NON-NLS-1$ //$NON-NLS-2$

		mgr.add(new ActionFullDescription(this));
		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				if(selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof StepInfos) {
					selectedStep = (StepInfos)selection.getFirstElement();
				}
				
				for (IContributionItem i : manager.getItems()) {
					if(VisualConstants.ACTION_SHOW_LOGS.equals(i.getId())) {
						i.setVisible(selectedStep != null && selectedStep.getFailureCause() != null && !selectedStep.getFailureCause().isEmpty());
					}
				}
				
				manager.update(true);
			}
		});
		
		IMenuService mSvc = (IMenuService) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(IMenuService.class);
		mSvc.populateContributionManager(mgr, "bpm.vanilla.server.ui.views.DialogWorkflowStepsInfos.contextMenu"); //$NON-NLS-1$
		viewer.getTable().setMenu(mgr.createContextMenu(viewer.getControl()));
	}

	public StepInfos getSelectedStep() {
		return selectedStep;
	}

	private class Monitor extends Thread {
		private boolean active = true;

		public void run() {
			while (active) {

				try {
					runtimeState = ((WorkflowService) Activator.getDefault().getRemoteServerManager()).getInfos(new SimpleRunIdentifier(task.getId()), task.getItemId(), task.getRepositoryId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						viewer.setInput(runtimeState.getStepInfos());
						viewer.refresh();
					}
				});

				try {
					Thread.sleep(2000);
				} catch (Exception ex) {
				}
			}
		}
	}
}
