package org.fasd.views;

import java.util.HashMap;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.security.SecurityDim;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.views.dialogs.DialogDimSecu;
import org.freeolap.FreemetricsPlugin;

public class RoleView extends ViewPart implements ISelectionListener {

	public static final String ID = "org.fasd.views.roleView"; //$NON-NLS-1$
	private Action add, del, edit;
	private ToolItem delIt, editIt;
	private ToolBar toolBar;
	private List roles, views, members;
	private Text description, access, hiera;
	private SecurityDim securityDim;
	private SecurityGroup selectedgroup;

	private HashMap<String, View> map = new HashMap<String, View>();
	private HashMap<String, SecurityGroup> groups = new HashMap<String, SecurityGroup>();

	public RoleView() {

	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(4, false));
		toolBar = new ToolBar(parent, SWT.NONE);
		toolBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 4, 1));

		Label l0 = new Label(parent, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(LanguageText.RoleView_SecurityGroup);

		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		l.setText(LanguageText.RoleView_DimensionsViews);

		roles = new List(parent, SWT.BORDER);
		roles.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 6));

		views = new List(parent, SWT.BORDER);
		views.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 6));

		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(LanguageText.RoleView_Description);

		description = new Text(parent, SWT.BORDER);
		;
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(LanguageText.RoleView_Hierarchy);

		hiera = new Text(parent, SWT.BORDER);
		hiera.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		hiera.setEnabled(false);

		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(LanguageText.RoleView_AccesMethod);

		access = new Text(parent, SWT.BORDER);
		access.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		access.setEnabled(false);

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		l1.setText(LanguageText.RoleView_MemberRestriction);

		members = new List(parent, SWT.BORDER);
		members.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		registerListeners();
		createActions();
		createToolbar();
	}

	private void registerListeners() {
		views.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				View v = map.get(((List) e.widget).getSelection()[0]);
				members.removeAll();
				for (String s : v.getMembers()) {
					members.add(s);
				}

				if (v.isAllowFullAccess()) {
					access.setText(LanguageText.RoleView_FullAcces);
				} else {
					access.setText(LanguageText.RoleView_NoneAccess);
				}

				description.setText(v.getDesc());
				hiera.setText(v.getHierarchy().getName());
			}

		});

		roles.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				map.clear();
				views.removeAll();

				selectedgroup = groups.get(roles.getSelection()[0]);

				if (securityDim == null)
					return;

				for (View v : securityDim.getListViews()) {
					if (v.getGroup() == selectedgroup) {
						views.add(v.getName());
						map.put(v.getName(), v);
					}
				}

			}

		});
	}

	@Override
	public void setFocus() {
		if (FreemetricsPlugin.getDefault().isRepositoryConnectionDefined()) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.RoleView_0 + FreemetricsPlugin.getDefault().getRepositoryConnection().getContext().getRepository().getUrl());
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(LanguageText.RoleView_1);
		}

	}

	private void createActions() {
		Image img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/obj_column.png"); //$NON-NLS-1$
		add = new Action(LanguageText.RoleView_AddView, ImageDescriptor.createFromImage(img)) {
			public void run() {
			}
		};
		add.setToolTipText(add.getText());

		img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/del.png"); //$NON-NLS-1$
		del = new Action(LanguageText.RoleView_12, ImageDescriptor.createFromImage(img)) {
			public void run() {
				if (views.getSelection().length > 0) {
					View view = map.get(views.getSelection()[0]);
					SecurityDim sd = FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().findSecurityDim(view.getParent().getDim());
					sd.removeView(view);
					fillData();
				}
			}
		};
		del.setToolTipText(del.getText());

		img = new Image(getSite().getShell().getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/stock_data-edit-table-16.png"); //$NON-NLS-1$
		edit = new Action(LanguageText.RoleView_14, ImageDescriptor.createFromImage(img)) {
			public void run() {
				if (views.getSelection().length > 0) {
					View view = map.get(views.getSelection()[0]);
					DialogDimSecu dial = new DialogDimSecu(RoleView.this.getSite().getShell(), view);
					dial.open();
					fillData();

				}

			}
		};
		edit.setToolTipText(edit.getText());
	}

	private void createToolbar() {

		delIt = new ToolItem(toolBar, SWT.PUSH);
		delIt.setToolTipText(del.getToolTipText());
		delIt.setImage(del.getImageDescriptor().createImage());
		delIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				del.run();
			}
		});

		editIt = new ToolItem(toolBar, SWT.PUSH);
		editIt.setToolTipText(edit.getToolTipText());
		editIt.setImage(edit.getImageDescriptor().createImage());
		editIt.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				edit.run();
			}
		});
	}

	public void fillData() {
		for (SecurityGroup g : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getSecurityGroups()) {
			groups.put(g.getName(), g);
		}

		roles.setItems(groups.keySet().toArray(new String[groups.size()]));

		views.removeAll();
		members.removeAll();
		description.setText(""); //$NON-NLS-1$
		access.setText(""); //$NON-NLS-1$

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection != null) {
			IStructuredSelection ss = ((IStructuredSelection) selection);
			Object o = ss.getFirstElement();
			OLAPDimension d = null;
			if (o instanceof TreeLevel) {
				d = ((TreeDim) ((TreeLevel) o).getParent().getParent()).getOLAPDimension();
			} else if (o instanceof TreeHierarchy) {
				d = ((TreeDim) ((TreeHierarchy) o).getParent()).getOLAPDimension();
			} else if (o instanceof TreeDim) {
				d = ((TreeDim) o).getOLAPDimension();
			}

			if (d == null)
				securityDim = null;
			else {
				for (SecurityDim sd : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getDimViews()) {
					if (sd.getDim() == d) {
						securityDim = sd;
					}
				}
			}

			fillData();

		}

	}

}
