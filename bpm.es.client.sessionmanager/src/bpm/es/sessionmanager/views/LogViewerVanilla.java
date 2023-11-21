package bpm.es.sessionmanager.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.IndexedDay;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.dialogs.PropertiesDialog;
import bpm.es.sessionmanager.views.providers.LogContentProvider;
import bpm.es.sessionmanager.views.providers.LogLabelProvider;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.VanillaLogsProps;

public class LogViewerVanilla {
	
	private SessionManager manager;
	
	private Composite generalVanilla;
	
	private TreeViewer viewer;
	
	private SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm aaa"); //$NON-NLS-1$
	
	private ComboViewer comboUsers;
	private ComboViewer comboLevels;
	private ComboViewer comboApplications;
	
	private DateTime dateStart;
	private DateTime dateEnd;
	
	private Text filterText;
	
	private Button btnFilter;
	
	public LogViewerVanilla(Composite parent) {
		
		createComposite(parent);
	}
	
	public Composite getComposite() {
		return generalVanilla;
	}

	private void createComposite(Composite parent) {
		generalVanilla = new Composite(parent, SWT.NO_SCROLL);
		generalVanilla.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		generalVanilla.setLayout(new GridLayout());
		
		Composite buttonBar = new Composite(generalVanilla, SWT.NONE);
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		buttonBar.setLayout(new GridLayout(6, true));
		
		Label lbl = new Label(buttonBar, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("User");
		
		lbl = new Label(buttonBar, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Level");
		
//		lbl = new Label(buttonBar, SWT.NONE);
//		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		lbl.setText("Type");
		
		lbl = new Label(buttonBar, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("Start date");
		
		lbl = new Label(buttonBar, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lbl.setText("End date");
		
		lbl = new Label(buttonBar, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		lbl.setText("Filter");
		
		comboUsers = new ComboViewer(buttonBar, SWT.READ_ONLY);
		comboUsers.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		comboUsers.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((User)element).getName();
			}
		});
		comboUsers.setContentProvider(new ArrayContentProvider());
		comboUsers.addSelectionChangedListener(comboFilterListener);
		
		comboLevels = new ComboViewer(buttonBar, SWT.READ_ONLY);
		comboLevels.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		comboLevels.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Level)element).getLevel();
			}
		});
		comboLevels.setContentProvider(new ArrayContentProvider());
		comboLevels.addSelectionChangedListener(comboFilterListener);
		
//		comboApplications = new ComboViewer(buttonBar, SWT.READ_ONLY);
//		comboApplications.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		comboApplications.setLabelProvider(new LabelProvider() {
//			@Override
//			public String getText(Object element) {
//				return "";
//			}
//		});
//		comboApplications.setContentProvider(new ArrayContentProvider());
//		comboApplications.addSelectionChangedListener(comboFilterListener);
		
		dateStart = new DateTime(buttonBar, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);
		dateStart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		dateStart.addSelectionListener(dateListener);
		
		dateEnd = new DateTime(buttonBar, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);
		dateEnd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		dateStart.addSelectionListener(dateListener);
		
		filterText = new Text(buttonBar, SWT.BORDER);
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		btnFilter = new Button(buttonBar, SWT.TOGGLE);
		btnFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnFilter.setText("Filter");
		btnFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					filterData(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		createLogViewerByDay(generalVanilla);
		
		try {
			fillToolbar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	SelectionListener dateListener = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				filterData(false);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}
	};
	
	ISelectionChangedListener comboFilterListener = new ISelectionChangedListener() {		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			try {
				filterData(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private void fillToolbar() throws Exception {
		comboUsers.setInput(adminbirep.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUsers());
		
		List<Level> levels = new ArrayList<Level>();
		levels.add(Level.DEBUG);
		levels.add(Level.INFO);
		levels.add(Level.WARN);
		levels.add(Level.ERROR);
		
		comboLevels.setInput(levels);
	}

	private void filterData(boolean fromBtnFilter) throws Exception {
		if(btnFilter.getSelection()) {
			//get the selected filters
			User selectedUser = (User) ((IStructuredSelection)comboUsers.getSelection()).getFirstElement();
			Level selectedLevel = (Level) ((IStructuredSelection)comboLevels.getSelection()).getFirstElement();
			
			Calendar startDateCalendar = Calendar.getInstance();
			startDateCalendar.set(Calendar.DAY_OF_MONTH, dateStart.getDay());
			startDateCalendar.set(Calendar.MONTH, dateStart.getMonth());
			startDateCalendar.set(Calendar.YEAR, dateStart.getYear());
			
			Calendar endDateCalendar = Calendar.getInstance();
			endDateCalendar.set(Calendar.DAY_OF_MONTH, dateEnd.getDay());
			endDateCalendar.set(Calendar.MONTH, dateEnd.getMonth());
			endDateCalendar.set(Calendar.YEAR, dateEnd.getYear());
			
			viewer.setInput(manager.getFilteredVanillaLogs(selectedUser, selectedLevel, startDateCalendar, endDateCalendar, filterText.getText()));
		}
		else if(fromBtnFilter) {
			showData(manager);
		}
	}

	private void setupListeners() {
		viewer.getTree().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				
				if (ss.getFirstElement() instanceof VanillaLogs) {
					VanillaLogs vlog = (VanillaLogs) ss.getFirstElement();
					showProperties(vlog);
				}
			}
		});
		
//		viewer.setSorter(new LogSorter());
	}
	
	private void showProperties(VanillaLogs vlog) {
		List<VanillaLogsProps> vprops;
		
		try {
			vprops = manager.getPropertiesForVanillaLog(vlog);
			if (vprops == null)
				throw new Exception(Messages.LogViewerVanilla_9);
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(viewer.getTree().getShell(), 
					Messages.LogViewerVanilla_10, Messages.LogViewerVanilla_11 + ex.getMessage());
			return;
		}
		
		if (vprops.isEmpty()) {
			MessageDialog.openInformation(viewer.getTree().getShell(), 
					Messages.LogViewerVanilla_12, Messages.LogViewerVanilla_13);
			return;
		}
		
		PropertiesDialog dialog = new PropertiesDialog(viewer.getControl().getShell(), 
				vprops);
		
		dialog.open();
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
				
				if (element instanceof String) {
					return (String) element;
				}
				else if (element instanceof VanillaLogs) {
					VanillaLogs log = (VanillaLogs) element;
					Date d = log.getDate();
					if (d != null)
						return hourFormat.format(d);
					else
						return "unknown"; //$NON-NLS-1$
				}
				
				return "case not treated"; //$NON-NLS-1$
			}
		});
		colDate.getColumn().setText(Messages.LogViewerVanilla_33);
		colDate.getColumn().setWidth(150);
		
		TreeViewerColumn colApp = new TreeViewerColumn(viewer, SWT.CENTER);
		colApp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof VanillaLogs) {
					VanillaLogs log = (VanillaLogs) element;
					return log.getApplication();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colApp.getColumn().setText(Messages.LogViewerVanilla_35);
		colApp.getColumn().setWidth(100);
		
		TreeViewerColumn colLevel = new TreeViewerColumn(viewer, SWT.CENTER);
		colLevel.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof VanillaLogs) {
					VanillaLogs log = (VanillaLogs) element;
					return log.getLevel();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colLevel.getColumn().setText(Messages.LogViewerVanilla_37);
		colLevel.getColumn().setWidth(100);
		
		TreeViewerColumn colOp = new TreeViewerColumn(viewer, SWT.CENTER);
		colOp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof VanillaLogs) {
					VanillaLogs log = (VanillaLogs) element;
					return log.getOperation();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colOp.getColumn().setText(Messages.LogViewerVanilla_39);
		colOp.getColumn().setWidth(100);
		
		TreeViewerColumn colTime = new TreeViewerColumn(viewer, SWT.CENTER);
		colTime.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof VanillaLogs) {
					VanillaLogs log = (VanillaLogs) element;
					return log.getDelay() + Messages.LogViewerVanilla_40;
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colTime.getColumn().setText(Messages.LogViewerVanilla_42);
		colTime.getColumn().setWidth(100);
		
		TreeViewerColumn colId = new TreeViewerColumn(viewer, SWT.CENTER);
		colId.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof VanillaLogs) {
					VanillaLogs log = (VanillaLogs) element;
					return "" + log.getObjectName(); //$NON-NLS-1$
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colId.getColumn().setText(Messages.LogViewerVanilla_45);
		colId.getColumn().setWidth(150);
		
		TreeViewerColumn colIp = new TreeViewerColumn(viewer, SWT.CENTER);
		colIp.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof VanillaLogs) {
					VanillaLogs log = (VanillaLogs) element;
					return log.getClientIp();
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		colIp.getColumn().setText(Messages.LogViewerVanilla_47);
		colIp.getColumn().setWidth(100);
		
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		
		setupListeners();
	}
	
	public void showData(SessionManager manager) throws Exception {
		this.manager = manager;
		
		IndexedDay data = manager.getVanillaLogsByDay();
		
		viewer.setInput(data);
	}
}
