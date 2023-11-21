package bpm.fasd.ui.measure.definition.composite.aggregationpart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.olap.OLAPMeasure;

import bpm.fasd.ui.measure.definition.Messages;

public class DefaultAggregationPartComposite extends Composite {

	public OLAPMeasure selectedMeasure;
	
	public DefaultAggregationPartComposite(Composite parent, int style) {
		super(parent, style);
		
		Label lbl = new Label(this, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lbl.setText(Messages.DefaultAggregationPartComposite_0);
		
	}
	
	public OLAPMeasure getMeasure() {
		return selectedMeasure;
	}
	
	public void setMeasure(OLAPMeasure measure) {
		this.selectedMeasure = measure;
	}
}
