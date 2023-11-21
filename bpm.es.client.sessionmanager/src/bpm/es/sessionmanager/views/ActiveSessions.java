package bpm.es.sessionmanager.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.icons.IconsName;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;

public class ActiveSessions extends ViewPart {
	public static final String ID = "bpm.enterpriseservices.sessionmanager.views.ActivesSessions"; //$NON-NLS-1$
	private TableViewer tableViewer;

	public ActiveSessions() {}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		tableViewer = new TableViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<VanillaSession> l = (List<VanillaSession>) inputElement;
				return l.toArray(new VanillaSession[l.size()]);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		});
		tableViewer.setLabelProvider(new ITableLabelProvider() {

			private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm,ss"); //$NON-NLS-1$

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex) {
					case 0:
						return ((VanillaSession) element).getUuid();
					case 1:
						int uId = ((VanillaSession) element).getUser().getId();
						User u;
						try {
							u = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getUserById(uId);
							return u.getName();
						} catch(Exception e) {
							e.printStackTrace();
							return ""; //$NON-NLS-1$
						}

					case 2:
						return ""; //$NON-NLS-1$

					case 3:
						return ""; //$NON-NLS-1$

					case 4:
						if(((VanillaSession) element).getCreationDate() != null)
							return sdf.format(((VanillaSession) element).getCreationDate());
						else
							return ""; //$NON-NLS-1$

					case 5:
						if(((VanillaSession) element).getCreationDate() != null) {
							Date d = new Date(((VanillaSession) element).getCreationDate().getTime() + ((VanillaSession) element).getTimeOut());
							return sdf.format(d);
						}

						else
							return ""; //$NON-NLS-1$

				}
				return ""; //$NON-NLS-1$
			}

			public void addListener(ILabelProviderListener listener) {

			}

			public void dispose() {

			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {

			}

		});

		TableColumn name = new TableColumn(tableViewer.getTable(), SWT.NONE);
		name.setText(Messages.ActiveSessions_8);
		name.setWidth(50);

		TableColumn user = new TableColumn(tableViewer.getTable(), SWT.NONE);
		user.setText(Messages.ActiveSessions_9);
		user.setWidth(100);

		TableColumn group = new TableColumn(tableViewer.getTable(), SWT.NONE);
		group.setText(Messages.ActiveSessions_10);
		group.setWidth(100);

		TableColumn rep = new TableColumn(tableViewer.getTable(), SWT.NONE);
		rep.setText(Messages.ActiveSessions_11);
		rep.setWidth(100);

		TableColumn creationDate = new TableColumn(tableViewer.getTable(), SWT.NONE);
		creationDate.setText(Messages.ActiveSessions_12);
		creationDate.setWidth(180);

		TableColumn actionDate = new TableColumn(tableViewer.getTable(), SWT.NONE);
		actionDate.setText(Messages.ActiveSessions_13);
		actionDate.setWidth(180);

		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		try {
			List<VanillaSession> actives = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getActiveSessions();
			tableViewer.setInput(actives);
		} catch(Exception e) {
			e.printStackTrace();
		}

		createToolbar();
	}

	private void createToolbar() {

		Action refresh = new Action() {
			public void run() {
				try {
					List<VanillaSession> actives = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getActiveSessions();
					tableViewer.setInput(actives);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};

		refresh.setId(ID + ".refresh"); //$NON-NLS-1$
		refresh.setText(Messages.ActiveSessions_15);
		refresh.setToolTipText(Messages.ActiveSessions_16);
		refresh.setImageDescriptor(bpm.es.sessionmanager.Activator.getDefault().getImageRegistry().getDescriptor(IconsName.REFRESH));

		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(refresh);
	}

	@Override
	public void setFocus() {}

}
