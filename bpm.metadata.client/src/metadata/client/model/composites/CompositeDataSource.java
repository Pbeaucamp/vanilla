package metadata.client.model.composites;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeConnection;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreeRelation;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;

public class CompositeDataSource extends Composite {
	
	private TreeViewer viewer;
	private Composite container, details;
	private Text name;	
	private IDataSource dataSource;
	
	public CompositeDataSource(Composite parent, int style, IDataSource ds) {
		super(parent, style);
		this.dataSource = ds;
		this.setLayout(new GridLayout());
		createPageContent(this);
		fillData();
		
	}

	
	private void createPageContent(Composite parent){
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, true));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite c = new Composite(container, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeDataSource_0); //$NON-NLS-1$
		
		name = new Text(c, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		viewer = new TreeViewer(container, SWT.BORDER);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){


			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				Object o = ss.getFirstElement();
				if (o instanceof TreeDataStream){
					//properties details
					if (details != null && !details.isDisposed())
						details.dispose();
					
					IDataStream d = ((TreeDataStream)o).getDataStream();
					
					details = new CompositeDataStream(container, SWT.NONE, viewer,  true, d);
					details.setLayoutData(new GridData(GridData.FILL_BOTH));
					container.layout();
				}
				else if (o instanceof TreeDataStreamElement){
					if (details != null && !details.isDisposed())
						details.dispose();
					
					IDataStreamElement d = ((TreeDataStreamElement)o).getDataStreamElement();
					
					details = new CompositeDataStreamElement(container, SWT.NONE, viewer, true, d);
					details.setLayoutData(new GridData(GridData.FILL_BOTH));
					container.layout();
				}
				else if (o instanceof TreeRelation){
					if (details != null && !details.isDisposed())
						details.dispose();
					
					Relation r = ((TreeRelation)o).getRelation();
					IDataSource ds = ((TreeDataSource)((TreeParent)o).getParent().getParent()).getDataSource();
					details = new CompositeRelation(container, SWT.NONE, viewer, true, ds, r);
					details.setLayoutData(new GridData(GridData.FILL_BOTH));
					container.layout();
					((CompositeRelation)details).createModel();
				}
				
			}
			
		});
		viewer.setSorter(new ViewerSorter(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1.getClass() != e2.getClass()){
					if (e1 instanceof TreeConnection){
						return -1;
					}
					else if (e2 instanceof TreeConnection){
						return 1;
					}
					else if (e1 instanceof TreeDataStream){
						return -1;
					}
					else if (e2 instanceof TreeDataStream){
						return 1;
					}
				}
				
				return super.compare(viewer, e1, e2);
			}
		});
		details= new Composite(container, SWT.NONE);
		details.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
	}
	
	protected void fillData(){
		if (dataSource != null){
			TreeParent root = new TreeParent(""); //$NON-NLS-1$

			TreeDataSource tds = new TreeDataSource(dataSource);
			root.addChild(tds);
			
			viewer.setInput(root);
			
			name.setText(dataSource.getName());

		}
		
	}
	
	public void setDataSource(){
		((AbstractDataSource)dataSource).setName(name.getText());
	}
}
