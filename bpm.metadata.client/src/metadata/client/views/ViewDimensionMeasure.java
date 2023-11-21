package metadata.client.views;

import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeModel;
import metadata.client.trees.TreeParent;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataStreamElement;

public class ViewDimensionMeasure extends ViewPart {

	public static final String ID = "bpm.metadata.client.viewdimmes";
	
	private TreeViewer viewer;
	
	private Color measureColor = new Color(Display.getDefault(), 0, 0, 183);
	private Color dimensionColor = new Color(Display.getDefault(), 0, 183, 0);
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.setContentProvider(new TreeContentProvider());
		
		DecoratingLabelProvider decoLabelProvider = new DecoratingLabelProvider(new TreeLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {
			@Override
			public Color getForeground(Object element) {
				if(element instanceof TreeDataStreamElement) {
					if(((TreeDataStreamElement)element).getDataStreamElement().getType().getParentType() == IDataStreamElement.Type.DIMENSION || ((TreeDataStreamElement)element).getDataStreamElement().getType().getParentType() == IDataStreamElement.Type.GEO || ((TreeDataStreamElement)element).getDataStreamElement().getType().getParentType() == IDataStreamElement.Type.DATE) {
						return dimensionColor;
					}
					else if(((TreeDataStreamElement)element).getDataStreamElement().getType().getParentType() == IDataStreamElement.Type.MEASURE) {
						return measureColor;
					}
				}
				
				return super.getForeground(element);
			}
		};
		
		viewer.setLabelProvider(decoLabelProvider);
		
		viewer.setInput(getTreeInput());
	}
	
	private TreeParent getTreeInput() {
		MetaData model = Activator.getDefault().getModel();
		
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		// create DataSource Branch
		TreeParent dataSources = new TreeParent("DataSources"); //$NON-NLS-1$
		for(AbstractDataSource ds : model.getDataSources()){
			dataSources.addChild(new TreeDataSource(ds,false,true));
		}
		root.addChild(dataSources);
		
		TreeParent models = new TreeParent("Business Models"); //$NON-NLS-1$
		root.addChild(models);
		for(IBusinessModel m : Activator.getDefault().getModel().getBusinessModels()){
			models.addChild(new TreeModel(m, "none", true)); //$NON-NLS-1$
		}
		
		return root;
	}

	@Override
	public void setFocus() {
		viewer.setInput(getTreeInput());
		viewer.refresh();
	}

	public Viewer getViewer() {
		return viewer;
	}
	
	

}
