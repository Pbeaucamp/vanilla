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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.layer.logical.sql.SQLDataSource;


public class CompositeDS extends Composite {
	
	private Text name, description;
	private IDataSource ds;
	private Viewer v;
	
	public CompositeDS(Composite parent, int style, Viewer v, IDataSource ds) {
		super(parent, style);
		this.ds = ds;
		this.v = v;
		buildContent();
		fillData();
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout());
		
		Composite container = new Composite(this, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.CompositeDS_0); //$NON-NLS-1$
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 2));
		l2.setText(Messages.CompositeDS_1); //$NON-NLS-1$
		
		description = new Text(container, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 2));
		
		
		Group g = new Group(container, SWT.NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2,1));
		g.setText(Messages.CompositeDS_2); //$NON-NLS-1$
		
		final Button sql = new Button(g, SWT.RADIO);
		sql.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sql.setText("Sql"); //$NON-NLS-1$
		sql.setEnabled(true);
		sql.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ds instanceof SQLDataSource){
					sql.setSelection(true);
				}
				else {
					sql.setSelection(false);
				}
			}
			
		});
		
		
		Button xml = new Button(g, SWT.RADIO);
		xml.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		xml.setText("Xml"); //$NON-NLS-1$
		xml.setEnabled(false);
		
		Button xls = new Button(g, SWT.RADIO);
		xls.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		xls.setText("Xls"); //$NON-NLS-1$
		xls.setEnabled(false);
		
		
		final Button olap = new Button(g, SWT.RADIO);
		olap.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		olap.setText("Olap"); //$NON-NLS-1$
		olap.setEnabled(true);
		olap.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (ds instanceof UnitedOlapDatasource){
					olap.setSelection(true);
				}
				else{
					olap.setSelection(false);
				}
			}
			
		});
		
		//set the type
		if (ds instanceof SQLDataSource){
			sql.setSelection(true);
		}
		else if (ds instanceof UnitedOlapDatasource){
			olap.setSelection(true);
		}
	
		Button ok = new Button(container, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		ok.setText(Messages.CompositeDS_7); //$NON-NLS-1$
		ok.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((AbstractDataSource)ds).setName(name.getText());
				v.refresh();
				Activator.getDefault().setChanged();
			}
			
		});
		
		
		
	}
	
	private void fillData(){
		if (ds != null){
			name.setText(ds.getName());
		}
	}
}
