package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.Relation;

public class DialogImportRelation extends Dialog {

	private static Color messageColor = new Color(Display.getDefault(), 255, 0 , 0);
	
	private Text infoText;
	private ComboViewer datasourcesViewer;
	private CheckboxTableViewer relationViewer;
	private List<Relation> selected = new ArrayList<Relation>();
	
	private Collection<Relation> existingRelation = null;
	
	public DialogImportRelation(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogImportRelation(Shell parentShell, Collection<Relation> existingRelation) {
		super(parentShell);
		this.existingRelation = existingRelation; 
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout( 2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogImportRelation_0);
		
		datasourcesViewer = new ComboViewer(c, SWT.READ_ONLY);
		datasourcesViewer.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		datasourcesViewer.setContentProvider(new IStructuredContentProvider(){

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}

			public Object[] getElements(Object inputElement) {
				Collection<IDataSource> l = (Collection<IDataSource>)inputElement;
				return l.toArray(new IDataSource[l.size()]);
			}
			
		});
		datasourcesViewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IDataSource)element).getName();
			}
			
		});
		datasourcesViewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)datasourcesViewer.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				
				IDataSource ds = (IDataSource)ss.getFirstElement();
				
				relationViewer.setInput(ds.getRelations());
				
				if (existingRelation != null){
					for(Relation r : ds.getRelations()){
						for(Relation t : existingRelation){
							if (t.isUsingTable(r.getLeftTable()) && t.isUsingTable(r.getRightTable())){
								relationViewer.setGrayed(r, true);
								
							}
						}
					}
					
				}
				relationViewer.refresh();
				
			}
			
		});
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l2.setText(Messages.DialogImportRelation_1);
		
		
		relationViewer = CheckboxTableViewer.newCheckList(c, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		relationViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		relationViewer.setContentProvider(new IStructuredContentProvider(){

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}

			public Object[] getElements(Object inputElement) {
				Collection<Relation> l = (Collection<Relation>)inputElement;
				return l.toArray( new Relation[l.size()]);
			}
			
		});
		relationViewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Relation)element).getName();
			}
			
		});
		relationViewer.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				Relation r = (Relation)event.getElement();
				
				if (relationViewer.getGrayed(r)){
					relationViewer.setChecked(r, false);
					infoText.setText(Messages.DialogImportRelation_2);
				}
				else{
					infoText.setText(""); //$NON-NLS-1$
				}
				
			}
			
		});
		
		
		infoText = new Text(c, SWT.WRAP | SWT.MULTI);
		infoText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		infoText.setEditable(false);
		infoText.setForeground(messageColor);
		
		return c;
	}
	
	private void setInput(){
		datasourcesViewer.setInput(Activator.getDefault().getModel().getDataSources());
		
		
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogImportRelation_4);
		setInput();
	}

	@Override
	protected void okPressed() {
		for(Object o : relationViewer.getCheckedElements()){
			selected.add(((Relation)o).copy());
		}
		super.okPressed();
	}
	
	public List<Relation> getRelations(){
		return selected;
	}

}
