package metadata.client.model.composites;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.query.SavedQuery;

public class CompositeSavedQuery extends Composite {
	
	private Viewer v;
	private Text name, description;
	
	private SavedQuery query;
	
	public CompositeSavedQuery(Composite parent, int style, Viewer v, SavedQuery query) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		this.query = query;
		this.v = v;

		createContent();
		fillDatas();
	}
	
	private void createContent(){
		Composite mainComp = new Composite(this, SWT.NONE);
		mainComp.setLayout(new GridLayout(2, false));
		mainComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		Label l = new Label(mainComp, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeBusinessTable_17); //$NON-NLS-1$
		
		name = new Text(mainComp, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(mainComp, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		l2.setText(Messages.CompositeBusinessTable_18); //$NON-NLS-1$
		
		description = new Text(mainComp, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Button ok = new Button(this, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
		ok.setText(Messages.CompositeBusinessTable_22); //$NON-NLS-1$
		ok.addSelectionListener(new SelectionAdapter(){
			
			public void widgetSelected(SelectionEvent e) {
				String savedName = name.getText();
				String savedDescription = description.getText();
				
				query.setName(savedName);
				query.setDescription(savedDescription);
				
				if (v != null){
					v.refresh();
					Activator.getDefault().setChanged();
				}
			}
		});
		
		Button cancel = new Button(this, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,	false));
		cancel.setText(Messages.CompositeBusinessTable_23); //$NON-NLS-1$
		cancel.addSelectionListener(new SelectionAdapter(){
			
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
			}
		});
	}
	
	private void fillDatas(){
		if (query != null){
			name.setText(query.getName());
			description.setText(query.getDescription());	
		}
	}
}
