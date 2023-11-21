package bpm.workflow.ui.wizards.biwmanagement;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.wizards.page.RepositoryExportPage;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.icons.IconsNames;
import bpm.workflow.ui.viewer.TreeContentProvider;
import bpm.workflow.ui.viewer.TreeDirectory;
import bpm.workflow.ui.viewer.TreeItem;
import bpm.workflow.ui.viewer.TreeLabelProvider;
import bpm.workflow.ui.viewer.TreeParent;

public class PageRepository extends RepositoryExportPage {

	protected TreeViewer viewer;
	protected ComboViewer cbGroups;
	protected List<Group> groupList;
	protected String selectedGroup = ""; //$NON-NLS-1$
	protected ToolItem start;

	public PageRepository(String pageName) {
		super(pageName);

	}

	public boolean isPageComplete() {
		return true;
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(false);

		if(Activator.getDefault().isRepositoryConnectionDefined()) {
			try {
				createModel();
			} catch(Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.PageRepository_1, e.getMessage());
			}
		}

		setPageComplete(false);

	}

	public void createPageContent(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, true));
		ToolBar toolbar = new ToolBar(container, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		fillToolbar(toolbar);

		cbGroups = new ComboViewer(container, SWT.READ_ONLY);
		cbGroups.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		cbGroups.setContentProvider(new ArrayContentProvider());
		cbGroups.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Group) element).getName();
			}
		});
		cbGroups.getCombo().addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				selectedGroup = cbGroups.getCombo().getText();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		viewer = new TreeViewer(container, SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());

		createModel();

	}

	public void fillContent(IRepositoryApi sock) {
		Activator.getDefault().setRepositoryConnection(sock);
		createModel();
	}

	public void createModel() {

		try {

			groupList = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
			cbGroups.setInput(groupList.toArray(new Group[groupList.size()]));

			IRepository rep = new Repository(Activator.getDefault().getRepositoryConnection(), IRepositoryApi.BIW_TYPE);
			List<RepositoryDirectory> list = rep.getRootDirectories();
			TreeParent root = new TreeParent("root"); //$NON-NLS-1$

			for(RepositoryDirectory d : list) {
				TreeDirectory tp = new TreeDirectory(((RepositoryDirectory) d));
				root.addChild(tp);
				buildChilds(tp, rep);
				for(RepositoryItem di : rep.getItems(d)) {
					TreeItem ti = new TreeItem((RepositoryItem) di);
					tp.addChild(ti);
				}
			}

			viewer.setInput(root);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void buildChilds(TreeDirectory parent, IRepository rep) throws Exception {

		RepositoryDirectory dir = ((TreeDirectory) parent).getDirectory();

		for(RepositoryDirectory d : rep.getChildDirectories(dir)) {
			TreeDirectory td = new TreeDirectory(d);
			parent.addChild(td);
			for(RepositoryItem di : rep.getItems(d)) {
				TreeItem ti = new TreeItem(di);
				td.addChild(ti);
			}
			buildChilds(td, rep);
		}

	}

	protected void fillToolbar(ToolBar toolbar) {
		start = new ToolItem(toolbar, SWT.PUSH);
		start.setToolTipText(Messages.PageRepository_3);
		try {
			start.setImage(new Image(Display.getCurrent(), IconsNames.START_16));;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		start.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				if(ss.getFirstElement() instanceof TreeItem) {
					RepositoryItem item = ((TreeItem) ss.getFirstElement()).getItem();
					try {
						if(selectedGroup.equalsIgnoreCase("")) { //$NON-NLS-1$
							throw new Exception(Messages.PageRepository_5);
						}
						ManagementHelper.run(getShell(), selectedGroup, item.getId(), ManagementHelper.ACTION_START);
					} catch(Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), Messages.PageRepository_6, e1.getMessage());
					}
				}

			}
		});

	}

	public boolean isCurrentPage() {
		return super.isCurrentPage();
	}

}
