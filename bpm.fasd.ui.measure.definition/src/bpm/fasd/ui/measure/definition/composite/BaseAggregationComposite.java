package bpm.fasd.ui.measure.definition.composite;

import org.eclipse.swt.widgets.Composite;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPMeasure;

import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;

public abstract class BaseAggregationComposite extends Composite {

	protected MeasureDefinitionDialog parentDial;
	protected FAModel model;
	
	public BaseAggregationComposite(Composite parent, int style, MeasureDefinitionDialog dial, FAModel model) {
		super(parent, style);
		this.parentDial = dial;
		this.model = model;
		createCompositeContent();
	}

	protected abstract void createCompositeContent();
	
	public abstract void setEditedMeasureData(OLAPMeasure measure);
}
