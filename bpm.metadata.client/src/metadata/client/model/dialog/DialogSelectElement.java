package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeBusinessTable;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreeResource;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IResource;

public class DialogSelectElement extends Dialog {

	private IDataStreamElement col;
	
	private TreeViewer viewer;
	private IDataStream table;
	private IBusinessTable businessTable;
	
	public DialogSelectElement(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public DialogSelectElement(Shell parentShell, IDataStream table) {
		super(parentShell);
		this.table = table;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	public DialogSelectElement(Shell parentShell, IBusinessTable table) {
		super(parentShell);
		this.businessTable = table;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogSelectElement_0); //$NON-NLS-1$
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TreeViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.setContentProvider(new TreeContentProvider());
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IResource || element instanceof TreeResource){
					return false;
				}
				return true;
			}
		});
		createInput();
		
		return c;
	}
	
	private void createInput(){
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		if (businessTable != null){
			root.addChild(new TreeBusinessTable(businessTable, "none")); //$NON-NLS-1$
		}
		else if (table == null){
			for(IDataSource ds : Activator.getDefault().getModel().getDataSources()){
				root.addChild(new TreeDataSource(ds));
			}
		}
		else{
			root.addChild(new TreeDataStream(table));
		}
		
		
		viewer.setInput(root);
	}
	
	public IDataStreamElement getDataStreamElement(){
		return col;
	}

	@Override
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		
		if(ss.isEmpty()){
			super.cancelPressed();
		}
		
		if (ss.getFirstElement() instanceof TreeDataStreamElement){
			col = ((TreeDataStreamElement)ss.getFirstElement()).getDataStreamElement();
		}
		super.okPressed();
	}
	
	

}
