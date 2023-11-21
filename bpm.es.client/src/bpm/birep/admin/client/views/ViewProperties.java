package bpm.birep.admin.client.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.birep.admin.client.composites.CompositeDatasource;
import bpm.birep.admin.client.composites.CompositeDirectory;
import bpm.birep.admin.client.composites.CompositeGroup;
import bpm.birep.admin.client.composites.CompositeItem;
import bpm.birep.admin.client.composites.CompositePackageDetails;
import bpm.birep.admin.client.composites.CompositeRepository;
import bpm.birep.admin.client.composites.CompositeRole;
import bpm.birep.admin.client.composites.CompositeUser;
import bpm.birep.admin.client.composites.CompositeVariable;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.trees.TreeDatasource;
import bpm.birep.admin.client.trees.TreeDirectory;
import bpm.birep.admin.client.trees.TreeGroup;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeRepositoryDefinition;
import bpm.birep.admin.client.trees.TreeRole;
import bpm.birep.admin.client.trees.TreeUser;
import bpm.birep.admin.client.trees.TreeVariable;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.VanillaPackage;


/**
 * 
 * ere : modified parent composite to be a scrolled composite.
 * 
 * @author 
 *
 */
public class ViewProperties extends ViewPart implements ISelectionListener{
	public static final String ID = "bpm.birep.admin.client.viewproperpties"; //$NON-NLS-1$
	
	private ScrolledComposite properties;
	private Composite parent;
	
	public ViewProperties() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		properties = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		properties.setAlwaysShowScrollBars(false);
		properties.setExpandHorizontal(true);
		properties.setExpandVertical(true);
		
		properties.getVerticalBar().setIncrement(10);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removeSelectionListener(this);	
		super.dispose();
	}

	@Override
	public void setFocus() { }

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty()){
			properties.dispose();
			properties = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			properties.setAlwaysShowScrollBars(false);
			properties.setExpandHorizontal(true);
			properties.setExpandVertical(true);
			
			properties.getVerticalBar().setIncrement(10);
			
			parent.layout();
			
			ViewTree.selectedObject = null;
			
			return;
		}
		
		IStructuredSelection ss = (IStructuredSelection)selection;
		Object o = ss.getFirstElement();
		
		ViewTree.selectedObject = o;

		if (properties != null){
			properties.dispose();
			properties = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			properties.setAlwaysShowScrollBars(false);
			properties.setExpandHorizontal(true);
			properties.setExpandVertical(true);
			
			properties.getVerticalBar().setIncrement(10);
		}

		if (o instanceof ExportDetails){
			CompositePackageDetails details = new CompositePackageDetails(properties, SWT.NONE);
			details.setLayoutData(new GridData(GridData.FILL_BOTH));
			details.fillDatas((ExportDetails)o);
			properties.setContent(details);
			properties.setMinSize(details.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			parent.layout();
		}
		else if (o instanceof VanillaPackage){
			CompositePackageDetails details = new CompositePackageDetails(properties, SWT.NONE);
			details.setLayoutData(new GridData(GridData.FILL_BOTH));
			details.fillDatas((VanillaPackage)o);
			properties.setContent(details);
			properties.setMinSize(details.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			parent.layout();
		}
		else if (o instanceof TreeUser){			
			CompositeUser us = new CompositeUser(properties, SWT.NONE, ((TreeUser)o).getUser(), true);
			us.setLayoutData(new GridData(GridData.FILL_BOTH));
			properties.setContent(us);
			properties.setMinSize(us.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			parent.layout();
		}
		else if (o instanceof TreeGroup){
			CompositeGroup group = new CompositeGroup(properties, SWT.NONE, ((TreeGroup)o).getGroup(), true);
			group.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			properties.setContent(group);
			properties.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			parent.layout();
		}
		else if (o instanceof TreeItem ){
			CompositeItem item;
			item = new CompositeItem(properties, SWT.NONE, ((TreeItem)o).getItem());
			
			item.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			properties.setContent(item);
			properties.setMinSize(item.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			parent.layout();
		}
		else if (o instanceof TreeDirectory){
			CompositeDirectory directory = new CompositeDirectory(properties, SWT.NONE, ((TreeDirectory)o).getDirectory());
			directory.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			properties.setContent(directory);
			properties.setMinSize(directory.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			parent.layout();
		}
		else if (o instanceof TreeDatasource){
			CompositeDatasource ds = new CompositeDatasource(properties, SWT.NONE, ((TreeDatasource)o).getDataSource());
			ds.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			properties.setContent(ds);
			properties.setMinSize(ds.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			parent.layout();
		}
		else if (o instanceof TreeRole){
			CompositeRole role = new CompositeRole(properties, SWT.NONE, ((TreeRole)o).getRole());
			role.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			properties.setContent(role);
			properties.setMinSize(role.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			parent.layout();
		}
		else if (o instanceof TreeRepositoryDefinition){			
			CompositeRepository repository = new CompositeRepository(properties, SWT.NONE, ((TreeRepositoryDefinition)o).getRepository());
			repository.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			properties.setContent(repository);
			properties.setMinSize(repository.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			parent.layout();
		}
		else if (o instanceof TreeVariable) {
			CompositeVariable variable = new CompositeVariable(properties, SWT.NONE, ((TreeVariable)o).getVariable());
			variable.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			properties.setContent(variable);
			properties.setMinSize(variable.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			parent.layout();
		}
		else {
			properties.dispose();
			properties = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
			properties.setAlwaysShowScrollBars(false);
			properties.setExpandHorizontal(true);
			properties.setExpandVertical(true);
			
			parent.layout();
		}
	}

}
