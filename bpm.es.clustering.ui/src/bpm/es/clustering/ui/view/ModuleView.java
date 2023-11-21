package bpm.es.clustering.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.composites.ModuleMasterDetailsBlock;
import bpm.es.clustering.ui.composites.VisualMappingComposite;
import bpm.es.clustering.ui.gef.GefModel;
import bpm.es.clustering.ui.icons.Icons;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;

public class ModuleView extends ViewPart {
	private int folderTabIndex = 0;

	private FormToolkit formToolkit;
	private ModuleMasterDetailsBlock block;
	private VisualMappingComposite graphComposite;

	private Action snapshot;

//	private ScrollingGraphicalViewer viewer;

	@Override
	public void createPartControl(Composite parent) {

		formToolkit = new FormToolkit(parent.getDisplay());

		CTabFolder folder = new CTabFolder(parent, SWT.BOTTOM);
		folder.setLayout(new GridLayout());
//		folder.setLayoutData(new GridData());

		ScrolledForm form = formToolkit.createScrolledForm(folder);
		form.setLayout(new GridLayout());
		form.setText(Messages.ModuleView_1);
		formToolkit.decorateFormHeading(form.getForm());

		block = new ModuleMasterDetailsBlock();
		ManagedForm managedForm = new ManagedForm(formToolkit, form);
		block.createContent(managedForm);

		CTabItem tabTree = new CTabItem(folder, SWT.NONE);
		tabTree.setText(Messages.ModuleView_0);
		tabTree.setControl(form);

		graphComposite = new VisualMappingComposite(folder, SWT.NONE);
		graphComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		CTabItem tabGraph = new CTabItem(folder, SWT.NONE);
		tabGraph.setText(Messages.ModuleView_2);
		tabGraph.setControl(graphComposite);

		
		folder.setSelection(0);
		folder.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (folderTabIndex == 0) {
					folderTabIndex = 1;
					graphComposite.setInput((GefModel) block.getInput());
					
					updateButton(true);
				}
				else {
					folderTabIndex = 0;

					updateButton(false);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) { }
		});

		createActions();
		createToolbar();
	}

	private void createActions() {
		snapshot = new ActionSnapshot(getSite().getShell(), graphComposite);
		snapshot.setText(Messages.ModuleView_3);
		snapshot.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.SAVE));
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(snapshot);

		updateButton(false);
	}

	public void updateButton(boolean enable) {
		snapshot.setEnabled(enable);
	}

	@Override
	public void setFocus() {

	}

}
