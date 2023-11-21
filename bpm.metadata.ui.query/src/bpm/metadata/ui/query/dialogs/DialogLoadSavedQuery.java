package bpm.metadata.ui.query.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.query.SavedQuery;
import bpm.metadata.ui.query.i18n.Messages;

public class DialogLoadSavedQuery extends Dialog {
	
	private Text txtDescription;
	
	private SavedQuery selectedQuery;
	private List<SavedQuery> queries;
	
	public DialogLoadSavedQuery(Shell parentShell, List<SavedQuery> queries) {
		super(parentShell);
		this.queries = queries;
	}
	
	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		getShell().setText(Messages.DialogLoadSavedQuery_0);
		getShell().setSize(380, 270);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		mainComp.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(mainComp, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblName.setText(Messages.DialogLoadSavedQuery_1);
		
		final ComboViewer cbQueries = new ComboViewer(mainComp, SWT.BORDER);
		cbQueries.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cbQueries.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<SavedQuery> savedQueries = (List<SavedQuery>) inputElement;
				return savedQueries.toArray(new SavedQuery[savedQueries.size()]);
			}
		});
		cbQueries.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((SavedQuery) element).getName();
			}
		});
		cbQueries.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) cbQueries.getSelection();
				if(ss != null && !ss.isEmpty() && ss.getFirstElement() instanceof SavedQuery) {
					selectedQuery = (SavedQuery)ss.getFirstElement();
					txtDescription.setText(selectedQuery.getDescription());
				}
			}
		});
		cbQueries.setInput(queries);
		
		txtDescription = new Text(mainComp, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		txtDescription.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		txtDescription.setEnabled(false);
		txtDescription.setText(Messages.DialogLoadSavedQuery_2);
		
		return parent;
	}
	
	public SavedQuery getSelectedQuery() {
		return selectedQuery;
	}
}
