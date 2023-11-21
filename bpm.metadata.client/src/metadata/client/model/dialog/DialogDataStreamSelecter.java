package metadata.client.model.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.sql.SQLDataSource;

public class DialogDataStreamSelecter extends Dialog{
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);
	
	private CheckboxTableViewer viewer;
	private ComboViewer dataSourceViewer;
	
	private Button check, uncheck;
	
	private HashMap<IDataStream, String> businessTableNames; 
	private BusinessModel businessModel;
	
	private Label errorMessage;
	
	public DialogDataStreamSelecter(Shell parentShell, IBusinessModel model) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		this.businessModel = (BusinessModel)model;

		
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogDataStreamSelecter_0);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	private void createViewer(Composite parent){
		viewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {	}
			
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.setComparator(new ViewerComparator(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				String s1 = ((IDataStream)e1).getName();
				String s2 = ((IDataStream)e2).getName();
				return super.compare(viewer, s1, s2);
			}
		});
		viewer.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateButtons();
				
			}
		});
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		TableViewerColumn dataStreamCol = new TableViewerColumn(viewer, SWT.NONE);
		dataStreamCol.getColumn().setText(Messages.DialogDataStreamSelecter_1);
		dataStreamCol.getColumn().setWidth(300);
		dataStreamCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IDataStream)element).getName();
			}
			
			@Override
			public Color getBackground(Object element) {
				return super.getBackground(element);
			}
		});
		
		TableViewerColumn busTableCol = new TableViewerColumn(viewer, SWT.NONE);
		busTableCol.getColumn().setText(Messages.DialogDataStreamSelecter_2);
		busTableCol.getColumn().setWidth(250);
		busTableCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				String s = businessTableNames.get((IDataStream)element);
				if (s == null){
					s = ((IDataStream)element).getName();
				}
				return s;
			}
			
			@Override
			public Color getBackground(Object element) {
				if (doesModelHaveTableNamed(getText(element))){
					return RED;
				}
				return super.getBackground(element);
			}
		});
		busTableCol.setEditingSupport(new EditingSupport(viewer) {
			
			TextCellEditor editor = new TextCellEditor(viewer.getTable());
			@Override
			protected void setValue(Object element, Object value) {
				businessTableNames.put((IDataStream)element, (String)value);
				viewer.refresh();
				updateButtons();
			}
			
			@Override
			protected Object getValue(Object element) {
				return businessTableNames.get(element);
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return viewer.getChecked(element);
			}
		});
	}
	
	private Control createDataSourceViewer(Composite parent){
		Composite bar= new Composite(parent, SWT.NONE);
		bar.setLayout(new GridLayout(2, false));
	
		Label l = new Label(bar, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogDataStreamSelecter_3);
		
		dataSourceViewer = new ComboViewer(bar, SWT.READ_ONLY);
		dataSourceViewer.getControl().setLayoutData(new GridData());
		dataSourceViewer.setContentProvider(new IStructuredContentProvider() {

			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {	}
			
			public Object[] getElements(Object inputElement) {
				Collection<?> c = (Collection<?>)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		dataSourceViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IDataSource)element).getName();
			}
		});
		dataSourceViewer.setComparator(new ViewerComparator(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				String s1 = ((IDataSource)e1).getName();
				String s2 = ((IDataSource)e2).getName();
				return super.compare(viewer, s1, s2);
			}
		});
	
		dataSourceViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				businessTableNames = new HashMap<IDataStream, String>();
				
				IDataSource ds = (IDataSource)((IStructuredSelection)dataSourceViewer.getSelection()).getFirstElement();
				
				for(IDataStream d : ds.getDataStreams()){
					businessTableNames.put(d, d.getName());
				}
				
				viewer.setInput(businessTableNames.keySet());
				
			}
		});
		return bar;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		
		check = new Button(main, SWT.PUSH);
		check.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		check.setText(Messages.DialogDataStreamSelecter_4);
		check.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setCheckedElements(((IStructuredContentProvider)viewer.getContentProvider()).getElements(viewer.getInput()));
				viewer.refresh();
				updateButtons();
			}
		});
		
		
		uncheck = new Button(main, SWT.PUSH);
		uncheck.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		uncheck.setText(Messages.DialogDataStreamSelecter_5);
		uncheck.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setCheckedElements(new Object[]{});
				viewer.refresh();
				updateButtons();
			}
		});
		
		
		createDataSourceViewer(main).setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		createViewer(main);
		
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		errorMessage = new Label(main, SWT.NONE);
		errorMessage.setVisible(false);
		errorMessage.setForeground(RED);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
		
		fillDataSourceViewer();
		return main;
	}

	private void fillDataSourceViewer(){
		List<IDataSource> l = new ArrayList<IDataSource>();
		
		for(IDataSource ds : Activator.getDefault().getModel().getDataSources()){
			if (ds instanceof SQLDataSource){
				l.add(ds);
			}
		}
		
		dataSourceViewer.setInput(l);
		if (!l.isEmpty()){
			dataSourceViewer.setSelection(new StructuredSelection(l.get(0)));
		}
	}
	
	private boolean doesModelHaveTableNamed(String name){
		return businessModel.getBusinessTable(name) != null;
	}
	
	
	private void updateButtons(){
		
		boolean empty = true;
		for(IDataStream s : businessTableNames.keySet()){
			
			if (!viewer.getChecked(s)){
				continue;
			}
			empty = false;
			
			if (doesModelHaveTableNamed(businessTableNames.get(s))){
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				setErrorMessage(Messages.DialogDataStreamSelecter_6 + businessTableNames.get(s));
				return;
			}
			for(String _s : businessTableNames.values()){
				if (businessTableNames.get(s) != _s && s.equals(_s)){
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setErrorMessage(Messages.DialogDataStreamSelecter_7 + _s);
					return;
				}
			}
		}
		setErrorMessage(null);
		
		
		getButton(IDialogConstants.OK_ID).setEnabled(!empty);
	}
	
	private void setErrorMessage(String message){
		if (message == null){
			errorMessage.setVisible(false);
		}
		else{
			errorMessage.setText(message);
			errorMessage.setVisible(true);
		}
	}


	public HashMap<IDataStream, String> getBusinessTablesToCreate(){
		return businessTableNames;
		
	}
	
	@Override
	protected void okPressed() {
		List<IDataStream> l = new ArrayList<IDataStream>(businessTableNames.keySet());
		
		for(IDataStream a : l){
			if (!viewer.getChecked(a)){
				businessTableNames.remove(a);
			}
		}
		super.okPressed();
	}
}
