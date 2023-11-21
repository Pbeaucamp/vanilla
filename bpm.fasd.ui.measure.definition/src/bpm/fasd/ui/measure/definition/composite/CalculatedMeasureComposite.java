package bpm.fasd.ui.measure.definition.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPMeasure;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.composite.aggregationpart.CompositeFormula;
import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;
import bpm.fasd.ui.measure.definition.listener.ModifyListenerNotifier;

public class CalculatedMeasureComposite extends BaseAggregationComposite implements IAggregationComposite {
	
	private MeasureInformationsComposite infoComposite;
	private CompositeFormula formulaComposite;
	
	public CalculatedMeasureComposite(Composite parent, int style, MeasureDefinitionDialog dial, FAModel model) {
		super(parent, style, dial, model);
	}

	@Override
	public OLAPMeasure getMeasure() {
		OLAPMeasure measure = new OLAPMeasure();
		
		measure.setDesc(infoComposite.getMeasureDescription());
		measure.setName(infoComposite.getMeasureName());
		measure.setLabel(infoComposite.getMeasureLabelItem());
		measure.setFormatstr(infoComposite.getMeasureFormat());
		
		measure.setType("calculated"); //$NON-NLS-1$
		measure.setFormula(formulaComposite.getFormula());
		
		return measure;
	}

	@Override
	public boolean canFinish() {
		if(infoComposite.getMeasureName() != null
				&& !infoComposite.getMeasureName().equals("") //$NON-NLS-1$
				&& formulaComposite.getFormula() != null
				&& !formulaComposite.getFormula().equals("")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	@Override
	protected void createCompositeContent() {
		
		this.setLayout(new GridLayout());
		
		Label lblTitle = new Label(this, SWT.NONE);
		lblTitle.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 3, 1));
		lblTitle.setText(Messages.CalculatedMeasureComposite_3);
		
		TabFolder folder = new TabFolder(this, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL,GridData.FILL, true, true, 1, 1));
		
		ModifyListenerNotifier listener = new ModifyListenerNotifier(parentDial);

		infoComposite = new MeasureInformationsComposite(folder, SWT.NONE, listener, model);
		infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		formulaComposite = new CompositeFormula(folder, SWT.NONE, model, listener);
		formulaComposite.setLayoutData(new GridData(GridData.FILL,GridData.FILL, true, true));
		
		TabItem infoItem = new TabItem(folder, SWT.NONE, 0);
		infoItem.setText(Messages.CalculatedMeasureComposite_4);
		infoItem.setControl(infoComposite);
		
		TabItem formulaItem = new TabItem(folder, SWT.NONE, 1);
		formulaItem.setText(Messages.CalculatedMeasureComposite_5);
		formulaItem.setControl(formulaComposite);
		
		folder.setSelection(0);
		
	}

	@Override
	public void setEditedMeasureData(OLAPMeasure measure) {
		infoComposite.setEditedMeasureData(measure);
		formulaComposite.setFormula(measure.getFormula());
	}

}
