package metadata.client.wizards.measure;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.resource.complex.FmdtMeasure;

public class DialogMeasure extends Dialog{
	private Text name;
	
	private CompositeMeasureEditor editor;
	private IDataSource dataSource;
	private FmdtMeasure measure;
	
	public DialogMeasure(Shell parentShell, IDataSource dataSource) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.dataSource = dataSource;
	}
	
	public DialogMeasure(Shell parentShell, FmdtMeasure measure) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.measure = measure;
		this.dataSource = measure.getDataSource();
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogMeasure_0);
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getButton(IDialogConstants.OK_ID).setEnabled(!"".equals(name.getText())); //$NON-NLS-1$
				
			}
		});
		
		editor = new CompositeMeasureEditor(main, SWT.NONE);
		editor.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		
		
		return main;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogMeasure_2);
		if (measure != null){
			name.setText(measure.getName());
			if (measure.getScript() != null){
				editor.setInput(new DocumentMeasure(Activator.getDefault().getModel(), dataSource, measure.getScript()), dataSource);
			}
			
		}
		else{
			editor.setInput(null, dataSource);
		}
	}
	
	@Override
	protected void okPressed() {
		if (measure == null){
			measure = new FmdtMeasure();
			measure.setDataSource((SQLDataSource)dataSource);
		}
		measure.setName(name.getText());
		measure.setScript(editor.getScript());
		measure.build(Activator.getDefault().getModel());
	
		super.okPressed();
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	public FmdtMeasure getMeasure(){
		return measure;
	}
}
