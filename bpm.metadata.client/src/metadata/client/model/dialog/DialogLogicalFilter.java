package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.List;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;
import bpm.metadata.resource.Filter;

public class DialogLogicalFilter extends Dialog {

	private Filter filter;
	private IDataStream stream;
	private IBusinessTable businessTable;
	
	private ListViewer columnsList;
	private CheckboxTableViewer values;
	private Text filterName;
	
	private IDataStreamElement previouslySelected;
	
	public DialogLogicalFilter(Shell parentShell, IDataStream stream) {
		super(parentShell);
		this.stream = stream;
		setShellStyle(SWT.RESIZE | SWT.TITLE);
	}
	
	public DialogLogicalFilter(Shell parentShell, IBusinessTable businessTable) {
		super(parentShell);
		this.businessTable = businessTable;
		setShellStyle(SWT.RESIZE | SWT.TITLE);
	}
	
	public Filter getFilter(){
		return filter;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		l2.setText(Messages.DialogLogicalFilter_0);  //$NON-NLS-1$
		
		filterName = new Text(c, SWT.BORDER);
		filterName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				activateButton();
				
			}
		});
		
		l2 = new Label(c, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l2.setText(Messages.DialogLogicalFilter_0);  //$NON-NLS-1$
		
		Label l3 = new Label(c, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l3.setText(Messages.DialogLogicalFilter_1); //$NON-NLS-1$
		
		columnsList = new ListViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		columnsList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		columnsList.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<IDataStreamElement> c = (List<IDataStreamElement>)inputElement;
				
				return c.toArray(new IDataStreamElement[c.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		columnsList.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return((IDataStreamElement)element).getName();
			}
			
		});
		columnsList.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)columnsList.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				
				IDataStreamElement e = (IDataStreamElement)ss.getFirstElement();
				previouslySelected = e;
				try{
					values.setInput(e.getDistinctValues());
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogLogicalFilter_2, ex.getMessage());
				}
				activateButton();
			}
			
		});

		
		values = new CheckboxTableViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		values.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		values.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<String> l = (List<String>)inputElement;
				return l.toArray(new String[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		
		boolean onOlap = false;
		if(stream != null && stream instanceof UnitedOlapDataStream) {
			onOlap = true;
		}
		if(businessTable != null && businessTable instanceof UnitedOlapBusinessTable) {
			onOlap = true;
		}
		
		if(onOlap) {
			values.setLabelProvider(new LabelProvider(){
				@Override
				public String getText(Object element) {
					if(previouslySelected != null) {
						String el = (String) element;
						return el.substring(previouslySelected.getDataStream().getOrigin().getName().length() + 1);
						 
					}
					return super.getText(element);
				}
			});
		}
		else {
			values.setLabelProvider(new LabelProvider(){
				@Override
				public String getText(Object element) {
					if (element !=  null){
						return (String)element;
					}
					else{
						return ""; //$NON-NLS-1$
					}
					
				}
			});
		}
		values.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				activateButton();
				
			}
		});
		
		return c;
	}

	private void fillDatas(){
		List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
		
		if (stream != null){
			for(IDataStreamElement e : stream.getElements()){
				l.add(e);
			}
		}
		
		if (businessTable != null){
			for(IDataStreamElement e : businessTable.getColumns("none")){ //$NON-NLS-1$
				l.add(e);
			}
		}
		
		
		columnsList.setInput(l);
	}

	@Override
	protected void okPressed() {
		if (filter == null){
			filter = new Filter();
		}
		
		IStructuredSelection ss = (IStructuredSelection)columnsList.getSelection();
		if (ss.isEmpty()){
			return;
		}
		
		filter.setOrigin((IDataStreamElement)ss.getFirstElement());
		
		for(Object o : values.getCheckedElements()){
			filter.addValue((String)o);
		}
		filter.setName(filterName.getText());
		super.okPressed();
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		fillDatas();
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	private void activateButton(){
		boolean filled = !filterName.getText().trim().equals("") && !columnsList.getSelection().isEmpty() && values.getCheckedElements().length > 0; //$NON-NLS-1$
		getButton(IDialogConstants.OK_ID).setEnabled(filled);
		
	}
	
}
