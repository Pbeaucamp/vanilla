package bpm.fasd.ui.measure.definition.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OlapDynamicMeasure;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.composite.CalculatedMeasureComposite;
import bpm.fasd.ui.measure.definition.composite.ClassicMeasureComposite;
import bpm.fasd.ui.measure.definition.composite.DynamicMeasureComposite;
import bpm.fasd.ui.measure.definition.composite.IAggregationComposite;
import bpm.fasd.ui.measure.definition.composite.LastMeasureComposite;

/**
 * A dialog which allows to create all kind of measures
 * @author Marc Lanquetin
 *
 */
public class MeasureDefinitionDialog extends Dialog {

	private StackLayout definitionLayout;
	
	private ClassicMeasureComposite classic;
	private LastMeasureComposite last;
	private DynamicMeasureComposite dynamic;
	private CalculatedMeasureComposite calculated;
	
	private Composite typeComposite;
	
	private Composite typeDefinition;
	
	private FAModel model;
	
	private Combo cbTypeAgg;
	
	private OLAPMeasure measure;
	
	private OLAPMeasure editedMeasure;
	
	public MeasureDefinitionDialog(Shell parentShell, FAModel model, OLAPMeasure editedMeasure) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.model = model;
		
		this.editedMeasure = editedMeasure;
		

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1,false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createTypeComposite(mainComposite);
		
		createDefinitionComposite(mainComposite);
			
		return mainComposite;
	}

	/**
	 * Create the stacked composite with definition for the chosen measure type
	 * @param mainComposite
	 */
	private void createDefinitionComposite(Composite mainComposite) {
		typeDefinition = new Composite(mainComposite, SWT.NONE);
		definitionLayout = new StackLayout();
		typeDefinition.setLayout(definitionLayout);
		typeDefinition.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		classic = new ClassicMeasureComposite(typeDefinition, SWT.BORDER,this, model);
		classic.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		last = new LastMeasureComposite(typeDefinition, SWT.BORDER,this, model);
		last.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		dynamic = new DynamicMeasureComposite(typeDefinition, SWT.BORDER,this, model);
		dynamic.setLayoutData(new GridData(GridData.FILL,GridData.FILL, true, true));
		
		calculated = new CalculatedMeasureComposite(typeDefinition, SWT.BORDER,this, model);
		calculated.setLayoutData(new GridData(GridData.FILL,GridData.FILL, true, true));
		
		if(editedMeasure != null) {
			//dynamic measure
			if(editedMeasure instanceof OlapDynamicMeasure) {
				dynamic.setEditedMeasureData(editedMeasure);
				definitionLayout.topControl = dynamic;
				cbTypeAgg.setText(TypeDefinitionListener.DYNAMIC);
			}
			else {
				//calculated measure
				if(editedMeasure.getType().equals("calculated")) { //$NON-NLS-1$
					calculated.setEditedMeasureData(editedMeasure);
					definitionLayout.topControl = calculated;
					cbTypeAgg.setText(TypeDefinitionListener.CALCULATED);
				}
				else {
					//last/first measure
					if(editedMeasure.getLastTimeDimensionName() != null && !editedMeasure.getLastTimeDimensionName().equals("")) { //$NON-NLS-1$
						last.setEditedMeasureData(editedMeasure);
						definitionLayout.topControl = last;
						cbTypeAgg.setText(TypeDefinitionListener.LAST);
					}
					//classic measure
					else {
						classic.setEditedMeasureData(editedMeasure);
						definitionLayout.topControl = classic;
						cbTypeAgg.setText(TypeDefinitionListener.CLASSIC);
					}
				}
			}
		}
	}

	/**
	 * Create a composite which contains buttons to choose measure type
	 * @param mainComposite
	 */
	private void createTypeComposite(Composite mainComposite) {
		
		typeComposite = new Composite(mainComposite, SWT.BORDER);
		typeComposite.setLayout(new GridLayout(2,false));
		typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		Label lblAggType = new Label(typeComposite, SWT.NONE);
		lblAggType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblAggType.setText(Messages.MeasureDefinitionDialog_2);
		
		cbTypeAgg = new Combo(typeComposite, SWT.READ_ONLY);
		cbTypeAgg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cbTypeAgg.setItems(new String[]{TypeDefinitionListener.CLASSIC, TypeDefinitionListener.LAST, TypeDefinitionListener.CALCULATED, TypeDefinitionListener.DYNAMIC});
		cbTypeAgg.addSelectionListener(new TypeDefinitionListener());
	}

	@Override
	protected void okPressed() {
		IAggregationComposite comp = (IAggregationComposite) definitionLayout.topControl;
		OLAPMeasure resultMeasure = comp.getMeasure();
		
		if(resultMeasure instanceof OlapDynamicMeasure) {
			OlapDynamicMeasure mes = (OlapDynamicMeasure) resultMeasure;
			
			if(!(createDynamicMeasureDialog(mes).open() == Dialog.OK)) {
				resultMeasure = null;
			}
		}
		
		
		
		if(resultMeasure != null) {
			
			this.measure = resultMeasure;
			super.okPressed();
//		
//			model.getOLAPSchema().addMeasure(resultMeasure);
//			
//			FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getListMeasures().setChanged();
//			FreemetricsPlugin.getDefault().getFAModel().setChanged();
//			
//			
		}
		
	}

	private class TypeDefinitionListener implements SelectionListener {
		
		private static final String CLASSIC = "classic"; //$NON-NLS-1$
		private static final String LAST = "last/first"; //$NON-NLS-1$
		private static final String DYNAMIC = "dynamic"; //$NON-NLS-1$
		private static final String CALCULATED = "calculated"; //$NON-NLS-1$
		
		public TypeDefinitionListener() {
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if(cbTypeAgg.getText().equals(CLASSIC)) {
				definitionLayout.topControl = classic;
			}
			else if(cbTypeAgg.getText().equals(LAST)) {
				definitionLayout.topControl = last;
			}
			else if(cbTypeAgg.getText().equals(DYNAMIC)) {
				definitionLayout.topControl = dynamic;
			}
			else if(cbTypeAgg.getText().equals(CALCULATED)) {
				definitionLayout.topControl = calculated;
			}
			
			typeDefinition.layout();
			updateButtonsState();
		}
	}
	
	public void updateButtonsState() {
		IAggregationComposite comp = (IAggregationComposite) definitionLayout.topControl;
		if(comp != null && getButton(IDialogConstants.OK_ID) != null) {
			if(comp.canFinish()) {
				getButton(IDialogConstants.OK_ID).setEnabled(true);
			}
			else {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			}
		}
		else if(getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
	}

	@Override
	public int open() {
		int i = 0;
		try {
			i = super.open();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		updateButtonsState();
		return i;
	}
	
	public FAModel getFaModel() {
		return model;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(950, 720);
		super.initializeBounds();
	}
	
	private Dialog createDynamicMeasureDialog(OlapDynamicMeasure mes) {
		return new DynamicMeasureDialog(this.getShell(), mes, model);
	}
	
	public OLAPMeasure getMeasure() {
		return measure;
	}
	
}
