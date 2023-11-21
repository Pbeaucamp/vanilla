package bpm.es.sessionmanager.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.LogRepository;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.views.providers.LogContentProvider;
import bpm.es.sessionmanager.views.providers.LogLabelProvider;
import bpm.es.sessionmanager.views.providers.LogSorter;
import bpm.vanilla.platform.core.repository.RepositoryLog;

public class LogViewerRepository {
	
	private SessionManager manager;
	private int userId;
	
	private Composite generalRepository;
	
	private TreeViewer viewer;
	
	private Button showByDay;
	private Button showByObject;
	private Button showByApplication;
	
	private SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, d MMM yyyy"); //$NON-NLS-1$
	private SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm aaa"); //$NON-NLS-1$
	private SimpleDateFormat fullFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa"); //$NON-NLS-1$
	
	public LogViewerRepository(Composite parent, LogViewer daddy) {
		
		createComposite(parent);
	}
	
	public Composite getComposite() {
		return generalRepository;
	}

	private void createComposite(Composite parent) {
		generalRepository = new Composite(parent, SWT.NO_SCROLL);
		generalRepository.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		generalRepository.setLayout(new GridLayout());
		
		Composite buttonBar = new Composite(generalRepository, SWT.NONE);
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		buttonBar.setLayout(new GridLayout(5, false));
		
//		Label lrep = new Label(buttonBar, SWT.NONE);
//		lrep.setText("Repository");
//		lrep.setLayoutData(new GridData());
		
		
		
		showByDay = new Button(buttonBar, SWT.TOGGLE);
		showByDay.setLayoutData(new GridData());
		showByDay.setText(Messages.LogViewerRepository_3);
		showByDay.setSelection(true);
		showByDay.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				LogViewer.showingNow = LogViewer.ORDER_BY_DAY;
				viewer.getControl().dispose();
				viewer = null;
				createLogViewerByDay(generalRepository);
				//general.redraw();
				generalRepository.layout();
				showData(manager, userId);
				showByDay.setSelection(true);
				showByObject.setSelection(false);
				showByApplication.setSelection(false);
			}
		});

		showByObject = new Button(buttonBar, SWT.TOGGLE);
		showByObject.setLayoutData(new GridData());
		showByObject.setText(Messages.LogViewerRepository_4);
		showByObject.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				LogViewer.showingNow = LogViewer.ORDER_BY_ID;
				viewer.getControl().dispose();
				viewer = null;
				createLogViewerByObject(generalRepository);
				generalRepository.layout();
				showData(manager, userId);
				showByDay.setSelection(false);
				showByObject.setSelection(true);
				showByApplication.setSelection(false);
			}
		});
		
		showByApplication = new Button(buttonBar, SWT.TOGGLE);
		showByApplication.setLayoutData(new GridData());
		showByApplication.setText(Messages.LogViewerRepository_5);
		showByApplication.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				LogViewer.showingNow = LogViewer.ORDER_BY_APP;
				viewer.getControl().dispose();
				viewer = null;
				createLogViewerByApplication(generalRepository);
				generalRepository.layout();
				showData(manager, userId);
				showByDay.setSelection(false);
				showByObject.setSelection(false);
				showByApplication.setSelection(true);
			}
		});
		
		createLogViewerByDay(generalRepository);
		
		//repositoryLogSection.setClient(generalRepository);
	}
	
	private void createLogViewerByObject(Composite composite) {
		viewer = new TreeViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		viewer.setContentProvider(new LogContentProvider());
		viewer.setLabelProvider(new LogLabelProvider());
		
		TreeViewerColumn colId = new TreeViewerColumn(viewer, SWT.CENTER);
		colId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					return (String) element;
				}
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return "" + log.getLog().getObjectName(); //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colId.getColumn().setText(Messages.LogViewerRepository_8);
		colId.getColumn().setWidth(150);
		
		TreeViewerColumn colDate = new TreeViewerColumn(viewer, SWT.CENTER);
		colDate.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
//				if (element instanceof String) {
//					return (String) element;
//				}
//				else 
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					Date d = log.getLog().getTime();
					if (d != null)
						return fullFormat.format(d);
					else
						return "unknown"; //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colDate.getColumn().setText(Messages.LogViewerRepository_11);
		colDate.getColumn().setWidth(150);
		
		TreeViewerColumn colApp = new TreeViewerColumn(viewer, SWT.CENTER);
		colApp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getAppName();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colApp.getColumn().setText(Messages.LogViewerRepository_13);
		colApp.getColumn().setWidth(100);
		
		TreeViewerColumn colOp = new TreeViewerColumn(viewer, SWT.CENTER);
		colOp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getOperation();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colOp.getColumn().setText(Messages.LogViewerRepository_15);
		colOp.getColumn().setWidth(100);
		
		TreeViewerColumn colTime = new TreeViewerColumn(viewer, SWT.CENTER);
		colTime.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getDelay() + "ms"; //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colTime.getColumn().setText(Messages.LogViewerRepository_18);
		colTime.getColumn().setWidth(100);
		
		TreeViewerColumn colIp = new TreeViewerColumn(viewer, SWT.CENTER);
		colIp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getClientIp();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colIp.getColumn().setText(Messages.LogViewerRepository_20);
		colIp.getColumn().setWidth(100);
		
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		
		viewer.setSorter(new LogSorter());
	}
	
	private void createLogViewerByDay(Composite composite) {
		viewer = new TreeViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		viewer.setContentProvider(new LogContentProvider());
		viewer.setLabelProvider(new LogLabelProvider());
		
		TreeViewerColumn colDate = new TreeViewerColumn(viewer, SWT.CENTER);
		colDate.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof RepositoryLog) {
					Date date = ((RepositoryLog)element).getTime();
					if (date != null)
						return dayFormat.format(date);
					else 
						return "unknown"; //$NON-NLS-1$
				}
				else if (element instanceof String) {
					return (String) element;
				}
				else if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					Date d = log.getLog().getTime();
					if (d != null)
						return hourFormat.format(d);
					else
						return "unknown"; //$NON-NLS-1$
				}
				
				return "case not treated"; //$NON-NLS-1$
			}
		});
		colDate.getColumn().setText(Messages.LogViewerRepository_24);
		colDate.getColumn().setWidth(150);
		
		TreeViewerColumn colApp = new TreeViewerColumn(viewer, SWT.CENTER);
		colApp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getAppName();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colApp.getColumn().setText(Messages.LogViewerRepository_26);
		colApp.getColumn().setWidth(100);
		
		TreeViewerColumn colOp = new TreeViewerColumn(viewer, SWT.CENTER);
		colOp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getOperation();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colOp.getColumn().setText(Messages.LogViewerRepository_28);
		colOp.getColumn().setWidth(100);
		
		TreeViewerColumn colTime = new TreeViewerColumn(viewer, SWT.CENTER);
		colTime.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getDelay() + "ms"; //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colTime.getColumn().setText(Messages.LogViewerRepository_31);
		colTime.getColumn().setWidth(100);
		
		TreeViewerColumn colId = new TreeViewerColumn(viewer, SWT.CENTER);
		colId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return "" + log.getLog().getObjectName(); //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colId.getColumn().setText(Messages.LogViewerRepository_34);
		colId.getColumn().setWidth(150);
		
		TreeViewerColumn colIp = new TreeViewerColumn(viewer, SWT.CENTER);
		colIp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getClientIp();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colIp.getColumn().setText(Messages.LogViewerRepository_36);
		colIp.getColumn().setWidth(100);
		
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		
		viewer.setSorter(new LogSorter());
	}
	
	private void createLogViewerByApplication(Composite composite) {
		viewer = new TreeViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		viewer.setContentProvider(new LogContentProvider());
		viewer.setLabelProvider(new LogLabelProvider());
		
		TreeViewerColumn colApp = new TreeViewerColumn(viewer, SWT.CENTER);
		colApp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					return "" + element; //$NON-NLS-1$
				}
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getAppName();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colApp.getColumn().setText(Messages.LogViewerRepository_39);
		colApp.getColumn().setWidth(100);
		
		TreeViewerColumn colDate = new TreeViewerColumn(viewer, SWT.CENTER);
		colDate.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					Date d = log.getLog().getTime();
					if (d != null)
						return fullFormat.format(d);
					else
						return "unknown"; //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colDate.getColumn().setText(Messages.LogViewerRepository_42);
		colDate.getColumn().setWidth(150);
		
		TreeViewerColumn colOp = new TreeViewerColumn(viewer, SWT.CENTER);
		colOp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getOperation();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colOp.getColumn().setText(Messages.LogViewerRepository_44);
		colOp.getColumn().setWidth(100);
		
		TreeViewerColumn colId = new TreeViewerColumn(viewer, SWT.CENTER);
		colId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return "" + log.getLog().getObjectName(); //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colId.getColumn().setText(Messages.LogViewerRepository_47);
		colId.getColumn().setWidth(150);
		
		TreeViewerColumn colTime = new TreeViewerColumn(viewer, SWT.CENTER);
		colTime.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getDelay() + "ms"; //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colTime.getColumn().setText(Messages.LogViewerRepository_50);
		colTime.getColumn().setWidth(100);
		
		TreeViewerColumn colIp = new TreeViewerColumn(viewer, SWT.CENTER);
		colIp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogRepository) {
					LogRepository log = (LogRepository) element;
					return log.getLog().getClientIp();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colIp.getColumn().setText(Messages.LogViewerRepository_52);
		colIp.getColumn().setWidth(100);
		
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		
		viewer.setSorter(new LogSorter());
	}
	
	public void showData(SessionManager manager, int userId) {
		this.userId = userId;
		this.manager = manager;
		
		Object data = null;
		try {
			data = manager.getVanillaLogsByItems();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		viewer.setInput(data);
	}
	
//	public boolean isExpanded() {
//		return repositoryLogSection.isExpanded();	
//	}
//	
//	public void setExpanded(boolean show) {
//		if (show)
//			repositoryLogSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		else
//			repositoryLogSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
//		
//		repositoryLogSection.layout();
//		
//		repositoryLogSection.setExpanded(show);
//	}
}
