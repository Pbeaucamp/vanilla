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
import org.fasd.olap.OlapDynamicMeasure;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;
import bpm.fasd.ui.measure.definition.listener.ModifyListenerNotifier;

/**
 * A composite to define a dynamic measure
 * A dynamic measure allows to choose an aggregation for each level of dimensions
 * @author Marc Lanquetin
 *
 */
public class DynamicMeasureComposite extends BaseAggregationComposite implements IAggregationComposite {

	private MeasureInformationsComposite infoComposite;
	private DynamicAggregationComposite dynAggComposite;
	
	public DynamicMeasureComposite(Composite parent, int style, MeasureDefinitionDialog dial, FAModel model) {
		super(parent, style, dial, model);

	}

	@Override
	public OLAPMeasure getMeasure() {
		OlapDynamicMeasure mes = dynAggComposite.getMeasure();
		
		mes.setName(infoComposite.getMeasureName());
		
		return mes;
	}

	@Override
	public boolean canFinish() {
		OlapDynamicMeasure measure = dynAggComposite.getMeasure();
		if(measure.getType().equals("physical")) { //$NON-NLS-1$
			if(infoComposite.getMeasureName() != null
					&& !infoComposite.getMeasureName().equals("") //$NON-NLS-1$
					&& measure.getAggregator() != null
					&& !measure.getAggregator().equals("")) { //$NON-NLS-1$
				return true;
			}
		}
		else {
			if(infoComposite.getMeasureName() != null
					&& !infoComposite.getMeasureName().equals("") //$NON-NLS-1$
					&& measure.getFormula() != null
					&& !measure.getFormula().equals("")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	@Override
	protected void createCompositeContent() {
		
		this.setLayout(new GridLayout());
		
		Label lblTitle = new Label(this, SWT.NONE);
		lblTitle.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		lblTitle.setText(Messages.DynamicMeasureComposite_5);
		
		TabFolder folder = new TabFolder(this, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL,GridData.FILL, true, true, 1, 1));
		
		ModifyListenerNotifier listener = new ModifyListenerNotifier(parentDial);

		infoComposite = new MeasureInformationsComposite(folder, SWT.NONE, listener, model);
		infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		dynAggComposite = new DynamicAggregationComposite(folder, SWT.NONE, parentDial, model);
		dynAggComposite.setLayoutData(new GridData(SWT.FILL,SWT.FILL, true, true));
		
		TabItem infoItem = new TabItem(folder, SWT.NONE, 0);
		infoItem.setText(Messages.DynamicMeasureComposite_6);
		infoItem.setControl(infoComposite);
		
		TabItem formulaItem = new TabItem(folder, SWT.NONE, 1);
		formulaItem.setText(Messages.DynamicMeasureComposite_7);
		formulaItem.setControl(dynAggComposite);
		
		folder.setSelection(0);
	}

	@Override
	public void setEditedMeasureData(OLAPMeasure measure) {
		infoComposite.setEditedMeasureData(measure);
		dynAggComposite.setEditedMeasureData((OlapDynamicMeasure) measure);
	}

}
