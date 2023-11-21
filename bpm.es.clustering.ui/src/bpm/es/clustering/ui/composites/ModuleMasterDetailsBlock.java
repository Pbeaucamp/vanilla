package bpm.es.clustering.ui.composites;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import bpm.es.clustering.ui.Activator;
import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.icons.Icons;
import bpm.es.clustering.ui.view.ModelHelper;
import bpm.es.clustering.ui.view.ModuleViewerContentProvider;
import bpm.es.clustering.ui.view.ModuleViewerLabelProvider;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.Server;

public class ModuleMasterDetailsBlock extends MasterDetailsBlock {

	private TreeViewer viewer;
	//XXX : replace delAction,addModule by start/stop component
	private Action /*delAction, addModule, */refreshAction;
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText(Messages.ModuleMasterDetailsBlock_0);
		section.setDescription(Messages.ModuleMasterDetailsBlock_1);
		
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		
		viewer = new TreeViewer(toolkit.createTree(section, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL));
		viewer.setAutoExpandLevel(3);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ModuleViewerContentProvider());
		viewer.setLabelProvider(new ModuleViewerLabelProvider());
		
		toolkit.paintBordersFor(viewer.getTree());
		section.setClient(viewer.getTree());
		
		//final SectionPart sectionPart = new SectionPart(section);
		final SectionPart sectionPart = new SectionPartWithListener(section, this);
		managedForm.addPart(sectionPart);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
//				delAction.setEnabled(!viewer.getSelection().isEmpty() && ((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof VanillaPlatformModule);
				managedForm.fireSelectionChanged(sectionPart, viewer.getSelection());
				managedForm.getForm().layout(true);
				
			}
		});
		
		
		viewer.setInput(ModelHelper.createModel());
		
	}

	public Object getInput(){
		return viewer.getInput();
	}
		
	private void createViewerMenu(){
		MenuManager mgr = new MenuManager();
		mgr.add(refreshAction);
		
		viewer.getTree().setMenu(mgr.createContextMenu(viewer.getTree()));
	}
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		final ScrolledForm form = managedForm.getForm();
		
		refreshAction = new Action(Messages.ModuleMasterDetailsBlock_2, Action.AS_PUSH_BUTTON){
			public void run(){
				refreshData();
			}
		};
		refreshAction.setDescription(Messages.ModuleMasterDetailsBlock_3);
		refreshAction.setImageDescriptor(reg.getDescriptor(Icons.REFRESH));
		
		form.getToolBarManager().add(refreshAction);

		createViewerMenu();
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(Repository.class, new RepositoryDefinitionDetailPage());
		detailsPart.registerPage(Server.class, new ModuleDetailPage());
		detailsPart.registerPage(IVanillaServerManager.class, new ServerClientDetailPage());
		
	}

	protected void refreshData() {
		viewer.setInput(ModelHelper.createModel());
	}
}
