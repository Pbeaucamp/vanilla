package bpm.es.sessionmanager.views;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import adminbirep.Activator;
import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.SessionManager;
import bpm.es.sessionmanager.api.UserWrapper;
import bpm.es.sessionmanager.icons.IconsName;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;

public class UserViewer {

	private Section onlineUsersSection; 
	private Section offlineUsersSection;
	
	private TableViewer onlineUsersTable;
	private TableViewer offlineUsersTable;

	private SessionManager manager;
	
	private final long MILLSECS_PER_HOUR = 60 * 60 * 1000;
	private final long MILLSECS_PER_MIN = 60 * 1000;
	
	private LogViewer logView; 
	
	public UserViewer(FormToolkit toolkit, Composite parent) {
		
		offlineUsersSection = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR| Section.TWISTIE|Section.EXPANDED);
		offlineUsersSection.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		offlineUsersSection.setText(Messages.UserViewer_0);
		createOfflineSection(offlineUsersSection);
		
		setupListeners();
	}
	
	public void setLogView(LogViewer logView) {
		this.logView = logView;
	}
	
	public void createPropertiesSection(Composite parent) {}
	
	public void createOnlineSection(Composite parent) {
		onlineUsersTable = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		onlineUsersTable.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL));
		onlineUsersTable.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<VanillaSession> l = (List<VanillaSession>)inputElement;
				return l.toArray(new VanillaSession[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
		});
		onlineUsersTable.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
					case 0:
						int uId = ((VanillaSession)element).getUser().getId();
						User u;
						try {
							u = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserById(uId);
							return u.getName();
						} catch (Exception e) {
							e.printStackTrace();
							return ""; //$NON-NLS-1$
						}
					case 1:
						try {
							Date beginDate = ((VanillaSession)element).getCreationDate();
							Date now = new Date();
							long diff = now.getTime() - beginDate.getTime();
							String tmp = ""; //$NON-NLS-1$
							
							long hours = diff / MILLSECS_PER_HOUR;
							long minutes = diff / MILLSECS_PER_MIN;
							
							if (hours - 1 > 0)
								tmp += (hours - 1) + "h"; //$NON-NLS-1$
							if (minutes > 0)
								tmp += minutes + "m"; //$NON-NLS-1$
							
							return tmp;
							
						} catch (Exception e) {
							e.printStackTrace();
							return ""; //$NON-NLS-1$
						}
					}
				return ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {
				
			}

			public void dispose() {}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {}
			
		});
		
		TableColumn user = new TableColumn(onlineUsersTable.getTable(), SWT.NONE);
		user.setText(Messages.UserViewer_8);
		user.setWidth(100);
		
		TableColumn time = new TableColumn(onlineUsersTable.getTable(), SWT.NONE);
		time.setText(Messages.UserViewer_9);
		time.setWidth(50);
		
		onlineUsersTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		onlineUsersTable.getTable().setHeaderVisible(false);
		onlineUsersTable.getTable().setLinesVisible(false);
		
		onlineUsersSection.setClient(onlineUsersTable.getControl());
	}
	
	public void createOfflineSection(Composite parent) {
		offlineUsersTable = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		offlineUsersTable.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL));
		offlineUsersTable.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<UserWrapper> l = (List<UserWrapper>)inputElement;
				return l.toArray(new UserWrapper[l.size()]);
			}

			public void dispose() {	}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
		});
		offlineUsersTable.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				switch(columnIndex){
					case 0:
						if (((UserWrapper)element).isConnected())
							return bpm.es.sessionmanager.Activator.
								getDefault().getImageRegistry().get(IconsName.ONLINE);
						else
							return bpm.es.sessionmanager.Activator.
								getDefault().getImageRegistry().get(IconsName.OFFLINE);
				}
				return null;
			}
			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
					case 0:
						return ((UserWrapper)element).getUser().getName();
				}
				return ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {}

			public void dispose() {}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {}
			
		});

		TableColumn user = new TableColumn(offlineUsersTable.getTable(), SWT.NONE);
		user.setText(Messages.UserViewer_11);
		user.setWidth(150);
		
		offlineUsersTable.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		offlineUsersTable.getTable().setHeaderVisible(false);
		offlineUsersTable.getTable().setLinesVisible(false);
		
		offlineUsersSection.setClient(offlineUsersTable.getControl());
	}
	
	public void setupListeners() {
		
		offlineUsersTable.getTable().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) offlineUsersTable.getSelection();
				Object o = ss.getFirstElement();
				
				if (o instanceof UserWrapper) {
					int userId = ((UserWrapper)o).getUser().getId();
					try {
						logView.showData(manager, userId);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	public void showData(SessionManager manager) {
		this.manager = manager;
		offlineUsersTable.setInput(manager.getUsers());
	}
}
