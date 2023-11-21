package bpm.fasd.ui.measure.definition.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPMeasure;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.composite.aggregationpart.ClassicAggregationPartComposite;
import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;
import bpm.fasd.ui.measure.definition.listener.ModifyListenerNotifier;

/**
 * A composite to define a classic measure
 * @author Marc Lanquetin
 *
 */
public class ClassicMeasureComposite extends BaseAggregationComposite implements IAggregationComposite {

	private MeasureInformationsComposite infoComposite;
	private ClassicAggregationPartComposite aggComposite;
	
	public ClassicMeasureComposite(Composite parent, int style, MeasureDefinitionDialog dial, FAModel model) {
		super(parent, style, dial, model);
	}

	@Override
	public OLAPMeasure getMeasure() {
		OLAPMeasure measure = new OLAPMeasure();
		
		measure.setDesc(infoComposite.getMeasureDescription());
		measure.setName(infoComposite.getMeasureName());
		measure.setLabel(infoComposite.getMeasureLabelItem());
		measure.setFormatstr(infoComposite.getMeasureFormat());
		
		measure.setOrigin(aggComposite.getMeasureOriginItem());
		measure.setAggregator(aggComposite.getMeasureAggregator());
		
		return measure;
	}

	@Override
	public boolean canFinish() {
		if(infoComposite.getMeasureName() != null
				&& !infoComposite.getMeasureName().equals("") //$NON-NLS-1$
				&& aggComposite.getMeasureAggregator() != null
				&& !aggComposite.getMeasureAggregator().equals("") //$NON-NLS-1$
				&& aggComposite.getMeasureOriginItem() != null) {
			return true;
		}
		return false;
	}

	@Override
	protected void createCompositeContent() {
		this.setLayout(new GridLayout(1,false));
		
		Label lblTitle = new Label(this, SWT.NONE);
		lblTitle.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblTitle.setText(Messages.ClassicMeasureComposite_2);
		
		ModifyListenerNotifier listener = new ModifyListenerNotifier(parentDial);

		infoComposite = new MeasureInformationsComposite(this, SWT.NONE, listener, model);
		infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		aggComposite = new ClassicAggregationPartComposite(this, SWT.NONE, listener, model);
		aggComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
	}

	@Override
	public void setEditedMeasureData(OLAPMeasure measure) {
		infoComposite.setEditedMeasureData(measure);
		aggComposite.setEditedMeasureData(measure);
	}


}
