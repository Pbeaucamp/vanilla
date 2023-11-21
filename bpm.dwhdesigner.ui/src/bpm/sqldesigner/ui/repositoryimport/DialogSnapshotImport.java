package bpm.sqldesigner.ui.repositoryimport;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.model.ClustersManager;

public class DialogSnapshotImport extends Dialog{


	private CheckboxTreeViewer viewer;
	private DocumentSnapshot importedSnapshot;
	private Combo strategie;

	private HashMap<DatabaseCluster, DwhViewImportStrategie> clusterUpdateStrategie = new HashMap<DatabaseCluster, DwhViewImportStrategie>();
	
	
	
	public DialogSnapshotImport(Shell parentShell, DocumentSnapshot importedSnapshot) {
		super(parentShell);
		this.importedSnapshot = importedSnapshot;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogSnapshotImport_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		strategie = new Combo(main, SWT.READ_ONLY);
		strategie.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		strategie.setItems(new String[]{DwhViewImportStrategie.CREATE_NEW_CLUSTER.name(), DwhViewImportStrategie.OVERRIDE_MATCHING_DWH_VIEW.name()});
		strategie.select(0);
		strategie.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				viewer.getControl().setEnabled(strategie.getSelectionIndex() != 0);
				
			}

		});
		
		l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogSnapshotImport_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false,2, 1));
		
		
		viewer = new CheckboxTreeViewer(main, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true,2 , 1));
		viewer.setContentProvider(new ITreeContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
			
			public boolean hasChildren(Object element) {
				
				return false;
			}
			
			public Object getParent(Object element) {
				
				return null;
			}
			
			public Object[] getChildren(Object parentElement) {
				
				return null;
			}
		});
		
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Node)element).getName();
			}
		});
		
		viewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (!((DatabaseCluster)element).getDatabaseConnection().getDataBaseName().equals(importedSnapshot.getDatabaseConnection().getDataBaseName())){
					return false;
				}
			
				
				if (!((DatabaseCluster)element).getDatabaseConnection().getDriverName().equals(importedSnapshot.getDatabaseConnection().getDriverName())){
					return false;
				}
				
				return true;
			
			}
		});
		viewer.setInput(ClustersManager.getInstance().getClusters().values());
		
		
		return main;
	}
	
	@Override
	protected void okPressed() {
		for(Object o : viewer.getCheckedElements()){
			clusterUpdateStrategie.put((DatabaseCluster)o, DwhViewImportStrategie.valueOf(strategie.getText()));
		}
		super.okPressed();
	}
	
	public HashMap<DatabaseCluster, DwhViewImportStrategie> getStrategies(){
		return clusterUpdateStrategie;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogSnapshotImport_2);
		getShell().setSize(600, 400);
	}
}
