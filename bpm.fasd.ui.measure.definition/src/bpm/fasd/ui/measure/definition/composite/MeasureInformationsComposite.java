package bpm.fasd.ui.measure.definition.composite;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPMeasure;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.listener.ModifyListenerNotifier;
import bpm.fasd.ui.measure.definition.tools.DialogPickCol;

public class MeasureInformationsComposite extends Composite {

	private Text txtName;
	private Text txtDesc;
	private Text txtLabel;
	private Text txtFormat;
	
	private DataObjectItem labelItem;
	private ModifyListenerNotifier listener;
	private OLAPMeasure editedMeasure;
	private FAModel model;
	
	public MeasureInformationsComposite(Composite parent, int style, ModifyListenerNotifier listener, FAModel mod) {
		super(parent, style);
		this.listener = listener;
		this.setLayout(new GridLayout(3,false));
		this.model = mod;
		
		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblName.setText(Messages.MeasureInformationsComposite_0);
		
		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		txtName.addModifyListener(listener);
		
		
		Label lblDesc = new Label(this, SWT.NONE);
		lblDesc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblDesc.setText(Messages.MeasureInformationsComposite_1);
		
		txtDesc = new Text(this, SWT.BORDER);
		txtDesc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		
		Label lblLabel = new Label(this, SWT.NONE);
		lblLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblLabel.setText(Messages.MeasureInformationsComposite_2);
		
		txtLabel = new Text(this, SWT.BORDER);
		txtLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		Button btnLabel = new Button(this, SWT.NONE);
		btnLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnLabel.setText("..."); //$NON-NLS-1$
		
		btnLabel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(MeasureInformationsComposite.this.getShell(), model);
				if(dial.open() == Dialog.OK) {
					labelItem = dial.getItem();
					txtLabel.setText(dial.getItem().getName());
				}
			}
		});
		
		
		Label lblFormat = new Label(this, SWT.NONE);
		lblFormat.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblFormat.setText(Messages.MeasureInformationsComposite_4);
		
		txtFormat = new Text(this, SWT.BORDER);
		txtFormat.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		
		
	}
	
	public String getMeasureName() {
		return txtName.getText();
	}
	
	public String getMeasureDescription() {
		return txtDesc.getText();
	}
	
	public String getMeasureFormat() {
		return txtFormat.getText();
	}

	public DataObjectItem getMeasureLabelItem() {
		return labelItem;
	}
	
	public void setEditedMeasureData(OLAPMeasure measure) {
		this.editedMeasure = measure;
		txtName.setText(editedMeasure.getName());
		txtDesc.setText(editedMeasure.getDesc());
		txtFormat.setText(editedMeasure.getFormatstr());
		if(editedMeasure.getLabel() != null) {
			txtLabel.setText(editedMeasure.getLabel().getName());
			labelItem = editedMeasure.getLabel();
		}
	}
}
