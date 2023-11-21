package bpm.fasd.ui.measure.definition.composite.aggregationpart;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.aggregation.ClassicAggregation;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.listener.ModifyListenerNotifier;
import bpm.fasd.ui.measure.definition.tools.DialogPickCol;

public class ClassicAggregationPartComposite extends Composite {

	private Text txtOrigin;
	private Combo cbAgg;
	
	private DataObjectItem originItem;
	
	private ModifyListenerNotifier listener;
	private FAModel model;
	
	public ClassicAggregationPartComposite(Composite parent, int style, ModifyListenerNotifier listener, FAModel mod) {
		super(parent, style);
		this.listener = listener;
		this.setLayout(new GridLayout(3,false));
		this.model = mod;
		
		Label lblOrigin = new Label(this, SWT.NONE);
		lblOrigin.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblOrigin.setText(Messages.ClassicAggregationPartComposite_0);
		
		txtOrigin = new Text(this, SWT.BORDER);
		txtOrigin.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		txtOrigin.addModifyListener(listener);
		
		Button btnOrigin = new Button(this, SWT.NONE);
		btnOrigin.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnOrigin.setText("..."); //$NON-NLS-1$
		
		btnOrigin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogPickCol dial = new DialogPickCol(ClassicAggregationPartComposite.this.getShell(), model);
				if(dial.open() == Dialog.OK) {
					originItem = dial.getItem();
					txtOrigin.setText(dial.getItem().getName());
				}
			}
		});

		
		Label lblAgg = new Label(this, SWT.NONE);
		lblAgg.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblAgg.setText(Messages.ClassicAggregationPartComposite_2);
		
		cbAgg = new Combo(this, SWT.READ_ONLY);
		cbAgg.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		cbAgg.setItems(new String[]{"avg", "min", "max", "sum", "count", "distinct-count"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		cbAgg.addModifyListener(listener);
	}

	public DataObjectItem getMeasureOriginItem() {
		return originItem;
	}
	
	public String getMeasureAggregator() {
		return cbAgg.getText();
	}
	
	public void clearFields() {
		cbAgg.select(0);
		txtOrigin.setText(""); //$NON-NLS-1$
		originItem = null;
	}

	public void setAggregation(ClassicAggregation lvlAgg) {
		cbAgg.setText(lvlAgg.getAggregator());
		txtOrigin.setText(lvlAgg.getOrigin().getName());
		originItem = lvlAgg.getOrigin();
	}
	
	public boolean canFinish() {
		
		if(txtOrigin.getText() != null && !txtOrigin.getText().equals("")) { //$NON-NLS-1$
			return true;
		}
		
		return false;
	}

	public void setEditedMeasureData(OLAPMeasure measure) {
		cbAgg.setText(measure.getAggregator());
		originItem = measure.getOrigin();
		txtOrigin.setText(measure.getOrigin().getName());
	}
}
