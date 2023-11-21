package bpm.vanilla.server.ui.views.actions.gateway;

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

import bpm.vanilla.platform.core.components.GatewayComponent;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState.StepInfos;
import bpm.vanilla.platform.core.remote.impl.components.internal.SimpleRunTaskId;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.views.VisualConstants;
import bpm.vanilla.server.ui.views.actions.ActionFullDescription;

public class DialogGatewayStepsInfos extends Dialog {
	
	private static final String NAME = "stepName"; //$NON-NLS-1$
	private static final String READED_ROWS = "stepReadedRows"; //$NON-NLS-1$
	private static final String PROCESSED_ROWS = "stepProcessedRows"; //$NON-NLS-1$
	private static final String BUFFERED_ROWS = "stepBufferedRows"; //$NON-NLS-1$
	private static final String STATE = "stepState"; //$NON-NLS-1$
	private static final String START_TIME = "stepStartTime"; //$NON-NLS-1$
	private static final String STOP_TIME = "stepStopTime"; //$NON-NLS-1$
	private static final String DURATION = "stepDuration"; //$NON-NLS-1$
	private static final String WARNINGS_NUMBER = "stepWarningsNumber"; //$NON-NLS-1$
	private static final String ERRORS_NUMBER = "stepErrorsNumber"; //$NON-NLS-1$

	private static Color ERROR = new Color(Display.getDefault(), 250, 25, 0);
	private static Color OK = new Color(Display.getDefault(), 0, 250, 0);
	private static Color WARN = new Color(Display.getDefault(), 0, 128, 255);

	private String[] cols = new String[]{NAME, READED_ROWS, PROCESSED_ROWS, BUFFERED_ROWS, STATE, START_TIME, STOP_TIME, DURATION, WARNINGS_NUMBER, ERRORS_NUMBER};
	private GatewayRuntimeState runtimeState;
	private Integer taskId;
	
	private StepInfos selectedStep;

	private TableViewer viewer;
	private Monitor monitor;

	public DialogGatewayStepsInfos(Shell parentShell, int taskId, GatewayRuntimeState runtimeState) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.runtimeState = runtimeState;
		this.taskId = taskId;
	}

	public DialogGatewayStepsInfos(Shell parentShell, GatewayRuntimeState runtimeState) {
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
					StepInfos step = ((StepInfos)element);
					if(s.equals(NAME)) {
						return step.getStepName();
					}
					else if(s.equals(READED_ROWS)) {
						return step.getReadRows() + ""; //$NON-NLS-1$
					}
					else if(s.equals(PROCESSED_ROWS)) {
						return step.getProcessedRows() + ""; //$NON-NLS-1$
					}
					else if(s.equals(BUFFERED_ROWS)) {
						return step.getBufferedRows() + ""; //$NON-NLS-1$
					}
					else if(s.equals(STATE)) {
						return step.getState();
					}
					else if(s.equals(START_TIME)) {
						return step.getStartTime() + ""; //$NON-NLS-1$
					}
					else if(s.equals(STOP_TIME)) {
						return step.getStopTime() + ""; //$NON-NLS-1$
					}
					else if(s.equals(DURATION)) {
						return step.getDuration() + ""; //$NON-NLS-1$
					}
					else if(s.equals(WARNINGS_NUMBER)) {
						return step.getWarningNumber() + ""; //$NON-NLS-1$
					}
					else if(s.equals(ERRORS_NUMBER)) {
						return step.getErrorNumber() + ""; //$NON-NLS-1$
					}
					else {
						return ""; //$NON-NLS-1$
					}
				}

				@Override
				public Color getBackground(Object element) {
					if (((StepInfos)element).getWarningNumber() > 0) {
						return WARN;
					}
					if (((StepInfos)element).getErrorNumber() > 0) {
						return ERROR;
					}
					return OK;
				}
			});
		}
		createTaskContextMenu();
		return viewer.getTable();
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(1000, 300);
		getShell().setText(Messages.DialogStepsInfos_16);
		viewer.setInput(runtimeState.getStepsInfo());
		
		if(taskId != null) {
			monitor = new Monitor();
			monitor.start();
		}
	}

	@Override
	protected void okPressed() {
		if(monitor != null) {
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
		MenuManager mgr = new MenuManager("Actions", "bpm.vanilla.server.ui.views.DialogGatewayStepsInfos.contextMenu"); //$NON-NLS-1$ //$NON-NLS-2$

		mgr.add(new ActionFullDescription(this));
		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				if(selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof StepInfos) {
					selectedStep = (StepInfos)selection.getFirstElement();
				}
				
				for (IContributionItem i : manager.getItems()) {
					if(VisualConstants.ACTION_SHOW_LOGS.equals(i.getId())) {
						i.setVisible(selectedStep != null && selectedStep.getLogs() != null && !selectedStep.getLogs().isEmpty());
					}
				}
				
				manager.update(true);
			}
		});
		
		IMenuService mSvc = (IMenuService) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getService(IMenuService.class);
		mSvc.populateContributionManager(mgr, "bpm.vanilla.server.ui.views.DialogGatewayStepsInfos.contextMenu"); //$NON-NLS-1$
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
					runtimeState = ((GatewayComponent)Activator.getDefault().getRemoteServerManager()).getRunState(new SimpleRunTaskId(taskId));
				} catch (Exception e) {
					e.printStackTrace();
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						viewer.setInput(runtimeState.getStepsInfo());
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
