package metadata.client.model.dialog;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStreamElement;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.SqlQueryFilter;

public class DialogGenericFilter extends Dialog{
	
	private IDataStream dataStream;
	private Combo filterType;
	private static final String TYPE_NAMES[] = new String[]{"Filter", "ComplexFilter", "SqlFilter"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final int FILTER = 0;
	private static final int COMPLEX_FILTER = 1;
	private static final int SQL_FILTER = 2;
	
	private IFilter createdFilter;
	private Text filterName;
	private Button createFilter, editFilter;
	private Label errorMessage;
	
	public DialogGenericFilter(Shell parentShell, IDataStream dataStream) {
		super(parentShell);
		this.dataStream = dataStream;
		setShellStyle(getShellStyle() |  SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogGenericFilter_3);
		
		filterType = new Combo(main, SWT.READ_ONLY);
		if(dataStream instanceof UnitedOlapDataStream) {
			filterType.setItems(new String[]{TYPE_NAMES[FILTER]});
		}
		else {
			filterType.setItems(TYPE_NAMES);
		}
		filterType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createdFilter = null;
				filterName.setText(""); //$NON-NLS-1$
				editFilter.setEnabled(false);
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		});
		
		Composite bar = new Composite(main, SWT.NONE);
		bar.setLayout(new GridLayout(2, true));
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		createFilter = new Button(bar, SWT.PUSH);
		createFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		createFilter.setText(Messages.DialogGenericFilter_5);
		createFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog d = null;
				switch(filterType.getSelectionIndex()){
				case FILTER:
					d = new DialogLogicalFilter(getShell(), dataStream);
					if (d.open() == DialogLogicalFilter.OK){
						createdFilter = ((DialogLogicalFilter )d).getFilter();
						filterName.setText(createdFilter.getName());
						
					}
					break;
				case SQL_FILTER:
					d = new DialogSqlFilter2(getShell(), dataStream);
					if (d.open() == DialogSqlFilter.OK){
						createdFilter = ((DialogSqlFilter )d).getFilter();
						filterName.setText(createdFilter.getName());
						
					}
					break;
				case COMPLEX_FILTER:
					d = new DialogComplexFilter(getShell(), dataStream);
					if (d.open() == DialogComplexFilter.OK){
						createdFilter = ((DialogComplexFilter )d).getFilter();
						filterName.setText(createdFilter.getName());
						
					}
					break;
					
				}
				checkIntegrity();
			}
		});
		
		editFilter = new Button(bar, SWT.PUSH);
		editFilter.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		editFilter.setText(Messages.DialogGenericFilter_6);
		editFilter.setEnabled(false);
		editFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (createdFilter == null){
					return;
				}
				Dialog d = null;
				if (createdFilter instanceof ComplexFilter){
					d = new DialogComplexFilter(getShell(), (ComplexFilter)createdFilter);
				}
				else if (createdFilter instanceof SqlQueryFilter){
					d = new DialogSqlFilter2(getShell(), (SqlQueryFilter)createdFilter);
					
				}
				else if (createdFilter instanceof Filter){
					
					d = new DialogFilter(getShell(), (Filter)createdFilter, ((Filter)createdFilter).getOrigin() instanceof UnitedOlapDataStreamElement);
				}
				else{
					return;
				}
				if (d.open() == Dialog.OK){
					filterName.setText(createdFilter.getName());
					checkIntegrity();
				}
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogGenericFilter_7);
		
		filterName = new Text(main, SWT.BORDER);
		filterName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filterName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				if (createdFilter != null){
					createdFilter.setName(filterName.getText());
				}
				checkIntegrity();
			}
		});
		
		errorMessage = new Label(main, SWT.NONE);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		return main;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogGenericFilter_8);
		getShell().setSize(600, 400);
	}
	
	public IFilter getFilter(){
		return createdFilter;
	}
	
	private void checkIntegrity(){
		if (createdFilter == null){
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			errorMessage.setText(Messages.DialogGenericFilter_9);
			errorMessage.setVisible(true);
			editFilter.setEnabled(false);
			return;
		}
		else{
			editFilter.setEnabled(true);
		}
		
		for(IFilter ds : dataStream.getGenericFilters()){
			if (ds.getName().equals(filterName.getText())){
				errorMessage.setText(Messages.DialogGenericFilter_10 + dataStream.getName() + Messages.DialogGenericFilter_11 + filterName.getText());
				errorMessage.setVisible(true);
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			}
		}
		
		errorMessage.setVisible(false);
		errorMessage.setText(""); //$NON-NLS-1$
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
}

