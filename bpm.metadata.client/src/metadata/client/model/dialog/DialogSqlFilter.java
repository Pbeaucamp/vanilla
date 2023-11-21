package metadata.client.model.dialog;


import java.util.List;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.scripting.Variable;

public abstract class  DialogSqlFilter extends Dialog {

	protected SqlQueryFilter filter;
	protected Text column, query, filterName;
	protected IDataStreamElement origin;
	protected IDataStream table; 
	protected IBusinessTable businessTable;
	
	protected ContentProposalAdapter proposalAdapter;
	
	public DialogSqlFilter(Shell parentShell) {
		super(parentShell);
		
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogSqlFilter(Shell parentShell, IDataStream table) {
		super(parentShell);
		this.table = table;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogSqlFilter(Shell parentShell, SqlQueryFilter filter) {
		super(parentShell);
		this.filter = filter;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogSqlFilter(Shell parentShell, IBusinessTable businessTable) {
		super(parentShell);
		this.businessTable = businessTable;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(3, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogSqlFilter_1);
		
		filterName = new Text(container, SWT.BORDER );
		filterName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		filterName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(isFilled());
			}
		});
		
		
		Composite c = new Composite(container, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		c.setLayout(new GridLayout(3, false));
		
		l = new Label(c, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogSqlFilter_0); //$NON-NLS-1$
		
		column = new Text(c, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		column.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		column.setEnabled(false);
		
		
		Button b = new Button(c, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectElement dial = null;
				
				if (table != null){
					dial = new DialogSelectElement(getShell(), table);
				}
				else if (filter != null && filter.getOrigin() != null){
					dial = new DialogSelectElement(getShell(), filter.getOrigin().getDataStream());
				}
				else if (businessTable != null){
					dial = new DialogSelectElement(getShell(), businessTable);
				}
				else{
					dial = new DialogSelectElement(getShell());
				}
				
				if (dial.open() == DialogSelectElement.OK){
					
					origin = dial.getDataStreamElement();
					column.setText(origin.getName());
				}
				getButton(IDialogConstants.OK_ID).setEnabled(isFilled());
			}
			
		});
		
		
		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l1.setText(Messages.DialogSqlFilter_2); //$NON-NLS-1$
		
		Button test = new Button(container, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		test.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					
					String filterString = query.getText();
					
					
					for(Variable v : Activator.getDefault().getModel().getVariables()){
						if (filterString.contains(v.getSymbol())){
							filterString = filterString.replace(v.getSymbol(), " "); //$NON-NLS-1$
						}
					}
					
					String querySql = "select * from " + origin.getDataStream().getOrigin().getName(); //$NON-NLS-1$
					querySql += " where " + origin.getOrigin().getName() + " " + filterString; //$NON-NLS-1$ //$NON-NLS-2$
					
					for(Variable v : Activator.getDefault().getModel().getVariables()){
						if (filterString.contains(v.getSymbol())){
							querySql = querySql.replace(v.getSymbol(), "''"); //$NON-NLS-1$
							
						}
					}
					
					origin.getDataStream().getOrigin().getConnection().executeQuery(querySql, 1, null);
					MessageDialog.openInformation(getShell(), Messages.DialogSqlFilter_6, Messages.DialogSqlFilter_7); //$NON-NLS-1$ //$NON-NLS-2$
					
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.DialogSqlFilter_8, ex.getMessage()); //$NON-NLS-1$
				}
			}
			
		});
		test.setText(Messages.DialogSqlFilter_5);
		
		query = new Text(container,SWT.BORDER | SWT.WRAP | SWT.MULTI);
		query.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 2));
		query.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				Button b = getButton(IDialogConstants.OK_ID);
				if (b != null){
					b.setEnabled(isFilled());
				}
				
				
			}
		});
		
		fillDatas();
		createContentAssist();
		return parent;
	}
	
	
	private void createContentAssist(){
		List<Variable>  vars = Activator.getDefault().getModel().getVariables();
		
		String[] symbols = new String[vars.size()];
		for(int i = 0; i < vars.size(); i++){
			symbols[i] = vars.get(i).getSymbol();
		}
		
		proposalAdapter = new ContentProposalAdapter(
				query, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(symbols),
				null, 
				null);;
	}
	
	protected void fillDatas(){
		if (filter != null){
		
			filterName.setText(filter.getName());
			origin = filter.getOrigin();
			if (origin != null){
				column.setText(filter.getOrigin().getName());
			}
			
			query.setText(filter.getQuery());
		}
	}
	
	public SqlQueryFilter getFilter(){
		return filter;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 500);
		getShell().setText(Messages.DialogSqlFilter_9); //$NON-NLS-1$
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(filter != null);
	}
	
	private boolean isFilled(){
		return origin != null && !"".equals(query.getText()) && !"".equals(filterName.getText()); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
