package metadata.client.views;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeBusinessModel;
import metadata.client.model.composites.CompositeBusinessPackage;
import metadata.client.model.composites.CompositeBusinessTable;
import metadata.client.model.composites.CompositeComplexFilter;
import metadata.client.model.composites.CompositeConnectionSql;
import metadata.client.model.composites.CompositeDS;
import metadata.client.model.composites.CompositeDataStream;
import metadata.client.model.composites.CompositeDataStreamElement;
import metadata.client.model.composites.CompositeFilter;
import metadata.client.model.composites.CompositeLoV;
import metadata.client.model.composites.CompositeMultiRelation;
import metadata.client.model.composites.CompositePrompt;
import metadata.client.model.composites.CompositePromptSql;
import metadata.client.model.composites.CompositeRelation;
import metadata.client.model.composites.CompositeSavedQuery;
import metadata.client.model.composites.CompositeSqlFilter;
import metadata.client.scripting.CompositeScript;
import metadata.client.scripting.CompositeVariable;
import metadata.client.trees.TreeBusinessTable;
import metadata.client.trees.TreeComplexFilter;
import metadata.client.trees.TreeConnection;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeFilter;
import metadata.client.trees.TreeLov;
import metadata.client.trees.TreeModel;
import metadata.client.trees.TreeMultiRelation;
import metadata.client.trees.TreePackage;
import metadata.client.trees.TreePrompt;
import metadata.client.trees.TreeRelation;
import metadata.client.trees.TreeResource;
import metadata.client.trees.TreeSavedQuery;
import metadataclient.Activator;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStreamElement;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.scripting.Script;
import bpm.metadata.scripting.Variable;

public class ViewProperties extends ViewPart {
	public static final String ID = "metadataclient.view.properties"; //$NON-NLS-1$
	
	private Composite properties;
	private Composite parent;
	private ISelectionChangedListener listener;
	
	
	public ViewProperties() {
	}

	public void registerAsListener(Viewer viewer){
		viewer.addSelectionChangedListener(listener);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		properties = new Composite(parent, SWT.NONE);
		properties.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		listener = new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()){
					return;
				}
				
				//find the ViewTree to getTheViewer
				ViewTree view = (ViewTree)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
				Viewer 	viewer = null;
				if (view != null){
					viewer = view.getViewer();
				}
				
				
				
				IStructuredSelection ss = (IStructuredSelection)event.getSelection();
				Object o = ss.getFirstElement();
				
				Composite parent = ViewProperties.this.parent;
				if (properties != null){
					properties.dispose();
				}
				
				if (o instanceof TreeDataSource){
					properties = new CompositeDS(parent, SWT.NONE, viewer, ((TreeDataSource)o).getDataSource());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreeConnection){
					if (((TreeConnection)o).getConnection() instanceof  SQLConnection){
						properties = new CompositeConnectionSql(parent, SWT.NONE, false, viewer, (SQLConnection)((TreeConnection)o).getConnection(), (SQLDataSource)((TreeDataSource)((TreeConnection)o).getParent()).getDataSource());
						properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					}
					
					parent.layout();
				}
				else if (o instanceof TreeBusinessTable){
					properties = new CompositeBusinessTable(parent, SWT.NONE, viewer, /*(AbstractBusinessTable)*/((TreeBusinessTable)o).getTable());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreeModel){
					properties = new CompositeBusinessModel(parent, SWT.NONE, viewer, true, (BusinessModel)((TreeModel)o).getModel());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				
				else if (o instanceof TreeLov){
					properties = new CompositeLoV(parent, SWT.NONE, viewer, true, ((TreeLov)o).getLov());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
					
				}
				else if (o instanceof TreeFilter){
					properties = new CompositeFilter(parent, SWT.NONE, viewer, true, ((TreeFilter)o).getFilter(), ((TreeFilter)o).getFilter().getOrigin() instanceof UnitedOlapDataStreamElement);
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreePrompt){
					if(((TreePrompt)o).getPrompt().getGotoDataStreamElement() != null) {
						properties = new CompositePrompt(parent, SWT.NONE, viewer, ((TreePrompt)o).getPrompt(), true);
						properties.setLayoutData(new GridData(GridData.FILL_BOTH));
						parent.layout();
					}
					else {
						properties = new CompositePromptSql(parent, SWT.NONE, viewer, ((TreePrompt)o).getPrompt(), true);
						properties.setLayoutData(new GridData(GridData.FILL_BOTH));
						parent.layout();
					}
				}
				else if (o instanceof TreeComplexFilter){
					properties = new CompositeComplexFilter(parent, SWT.NONE, viewer, ((TreeComplexFilter)o).getFilter(), true);
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreeResource && ((TreeResource)o).getResource() instanceof SqlQueryFilter){
					properties = new CompositeSqlFilter(parent, SWT.NONE, viewer, (SqlQueryFilter)((TreeResource)o).getResource(), true);
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
					
				}
				else if (o instanceof TreeDataStream){
					properties = new CompositeDataStream(parent, SWT.NONE, viewer, true, ((TreeDataStream)o).getDataStream());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreeDataStreamElement){
					properties = new CompositeDataStreamElement(parent, SWT.NONE, viewer, true, ((TreeDataStreamElement)o).getDataStreamElement());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreeRelation){
					properties = new CompositeRelation(parent, SWT.NONE, viewer, false, ((TreeRelation)o).getRelation());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
					((CompositeRelation)properties).createModel();
				}
				else if (o instanceof TreeMultiRelation){
					properties = new CompositeMultiRelation(parent, SWT.NONE, true, ((TreeMultiRelation)o).getRelation());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreePackage){
					properties = new CompositeBusinessPackage(parent, SWT.NONE, viewer, true, ((TreePackage)o).getPackage());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof TreeSavedQuery){
					properties = new CompositeSavedQuery(parent, SWT.NONE, viewer, ((TreeSavedQuery)o).getQuery());
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof Variable){
					properties = new CompositeVariable(parent, SWT.NONE, (Variable)o, viewer);
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
				else if (o instanceof Script){
					properties = new CompositeScript(parent, SWT.NONE, (Script)o, viewer);
					properties.setLayoutData(new GridData(GridData.FILL_BOTH));
					parent.layout();
				}
			}

			
		};
	}

	@Override
	public void setFocus() {
		if (Activator.getDefault().getRepositoryContext() != null){
			getViewSite().getActionBars().getStatusLineManager().setMessage(Messages.ViewProperties_1 + Activator.getDefault().getRepositoryConnection().getContext().getRepository().getUrl()); //$NON-NLS-1$
		}
		else{
			getViewSite().getActionBars().getStatusLineManager().setMessage(Messages.ViewProperties_2); //$NON-NLS-1$
		}

	}

	
		
}
