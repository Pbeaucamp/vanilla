package bpm.fasd.ui.measure.definition.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.OlapDynamicMeasure;
import org.fasd.olap.aggregation.CalculatedAggregation;
import org.fasd.olap.aggregation.ClassicAggregation;
import org.fasd.olap.aggregation.IMeasureAggregation;
import org.fasd.olap.aggregation.LastAggregation;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.composite.aggregationpart.ClassicAggregationPartComposite;
import bpm.fasd.ui.measure.definition.composite.aggregationpart.CompositeFormula;
import bpm.fasd.ui.measure.definition.composite.aggregationpart.DefaultAggregationPartComposite;
import bpm.fasd.ui.measure.definition.composite.aggregationpart.LastAggregationPartComposite;
import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;
import bpm.fasd.ui.measure.definition.listener.ModifyListenerNotifier;

public class StackedAggregationsComposite extends Composite {

	private ClassicAggregationPartComposite classicComposite;
	private LastAggregationPartComposite lastComposite;
	private CompositeFormula calculatedComposite;
	
	private Composite compositeNoLevel;
	private Composite defaultAggregationParComposite;
	
	private StackLayout stackedLayout;
	private Composite stackedComposite;
	
	private Button btnValidClassic;
	private Button btnValidLast;
	private Button btnValidCalc;
	private Button btnValidDefaut;
	
	private OLAPSchema schema;
	
	private Combo cbAggType;
	
	private OlapDynamicMeasure measure = new OlapDynamicMeasure();
	
	private String lvlUname;
	
	private Label lblLevelSelected;
	private Composite compLvlAggType;
	
	private Composite parent;
	private MeasureDefinitionDialog dial;
	
	public StackedAggregationsComposite(Composite parent, int style, FAModel model, MeasureDefinitionDialog dial) {
		super(parent, style);
		
		this.dial = dial;
		
		measure.setAggregator(""); //$NON-NLS-1$
		
		this.parent = parent;
		
		this.setLayout(new GridLayout(2,false));
		
		compLvlAggType = new Composite(this, SWT.NONE);
		compLvlAggType.setLayout(new GridLayout(2,false));
		compLvlAggType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		compLvlAggType.setVisible(false);
		
		lblLevelSelected = new Label(compLvlAggType, SWT.NONE);
		lblLevelSelected.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		lblLevelSelected.setText(""); //$NON-NLS-1$
		
		Label lblTypeAgg = new Label(compLvlAggType, SWT.NONE);
		lblTypeAgg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblTypeAgg.setText(Messages.StackedAggregationsComposite_2);
		((GridData)lblTypeAgg.getLayoutData()).verticalIndent = 10;
		
		cbAggType = new Combo(compLvlAggType, SWT.READ_ONLY);
		cbAggType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		cbAggType.setItems(new String[]{"classic","last/first","calculated"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		((GridData)cbAggType.getLayoutData()).verticalIndent = 10;
		
		cbAggType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(lvlUname != null && !lvlUname.equals("")) { //$NON-NLS-1$
					if(cbAggType.getText().equals("classic")) { //$NON-NLS-1$
						stackedLayout.topControl = classicComposite; 
					}
					else if(cbAggType.getText().equals("last/first")) { //$NON-NLS-1$
						stackedLayout.topControl = lastComposite; 
					}
					else if(cbAggType.getText().equals("calculated")) { //$NON-NLS-1$
						stackedLayout.topControl = calculatedComposite; 
					}
					stackedComposite.layout();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		stackedLayout = new StackLayout();
		stackedComposite = new Composite(this, SWT.NONE);
		stackedComposite.setLayout(stackedLayout);
		stackedComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		ModifyListenerNotifier listener = new ModifyListenerNotifier(dial);
		
		compositeNoLevel = new Composite(stackedComposite, SWT.NONE);
		compositeNoLevel.setLayout(new GridLayout());
		compositeNoLevel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		
		Label lblNoLevel = new Label(compositeNoLevel, SWT.NONE);
		lblNoLevel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lblNoLevel.setText(Messages.StackedAggregationsComposite_10);
		
		classicComposite = new ClassicAggregationPartComposite(stackedComposite, SWT.NONE, listener, model);
		classicComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		
		lastComposite = new LastAggregationPartComposite(stackedComposite, SWT.NONE, listener, model.getOLAPSchema().getDimensions(), model);
		lastComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		
		calculatedComposite = new CompositeFormula(stackedComposite, SWT.NONE, model,listener);
		calculatedComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		
		defaultAggregationParComposite = new DefaultAggregationPartComposite(stackedComposite, SWT.NONE);
		defaultAggregationParComposite.setLayout(new GridLayout());
		defaultAggregationParComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
		
		stackedLayout.topControl = compositeNoLevel;
		
		createValidButtons();
	}

	private void createValidButtons() {
		btnValidClassic = new Button(classicComposite, SWT.PUSH);
		btnValidClassic.setLayoutData(new GridData(SWT.CENTER, SWT.END, false, false, 3, 1));
		btnValidClassic.setText(Messages.StackedAggregationsComposite_11);
		btnValidClassic.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IMeasureAggregation aggreg = null;
				ClassicAggregationPartComposite selectedComp = (ClassicAggregationPartComposite) stackedLayout.topControl;
				if(!selectedComp.canFinish()) {
					MessageBox msgBox = new MessageBox(StackedAggregationsComposite.this.getShell(), SWT.ICON_WARNING);
					msgBox.setMessage(Messages.StackedAggregationsComposite_12);
					msgBox.open();
				}
				else {
					aggreg = new ClassicAggregation();
					((ClassicAggregation)aggreg).setAggregator(selectedComp.getMeasureAggregator());
					((ClassicAggregation)aggreg).setOrigin(selectedComp.getMeasureOriginItem());
					aggreg.setLevel(lvlUname);
					if(lvlUname.equals("default_agg")) { //$NON-NLS-1$
						createDefaultAggregation(aggreg);
					}
					else {
						measure.addAggregation(aggreg);
					}
					refreshDimensionTree(aggreg);
					dial.updateButtonsState();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnValidLast = new Button(lastComposite, SWT.PUSH);
		btnValidLast.setLayoutData(new GridData(SWT.CENTER, SWT.END, false, false, 3, 1));
		btnValidLast.setText(Messages.StackedAggregationsComposite_14);
		btnValidLast.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IMeasureAggregation aggreg = null;
				LastAggregationPartComposite selectedComp = (LastAggregationPartComposite) stackedLayout.topControl;
				if(!selectedComp.canFinish()) {
					MessageBox msgBox = new MessageBox(StackedAggregationsComposite.this.getShell(), SWT.ICON_WARNING);
					msgBox.setMessage(Messages.StackedAggregationsComposite_15);
					msgBox.open();
				}
				else {
					aggreg = new LastAggregation();
					((LastAggregation)aggreg).setAggregator(selectedComp.getMeasureAggregator());
					((LastAggregation)aggreg).setOrigin(selectedComp.getMeasureOriginItem());
					((LastAggregation)aggreg).setRelatedDimension(selectedComp.getMeasureDimensionForLast());
					aggreg.setLevel(lvlUname);
					if(lvlUname.equals("default_agg")) { //$NON-NLS-1$
						createDefaultAggregation(aggreg);
					}
					else {
						measure.addAggregation(aggreg);
					}
					refreshDimensionTree(aggreg);
					dial.updateButtonsState();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnValidCalc = new Button(calculatedComposite, SWT.PUSH);
		btnValidCalc.setLayoutData(new GridData(SWT.CENTER, SWT.END, false, false, 3, 1));
		btnValidCalc.setText(Messages.StackedAggregationsComposite_17);
		btnValidCalc.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IMeasureAggregation aggreg = null;
				CompositeFormula selectedComp = (CompositeFormula) stackedLayout.topControl;
				aggreg = new CalculatedAggregation();
				((CalculatedAggregation)aggreg).setFormula(selectedComp.getFormula());
				aggreg.setLevel(lvlUname);
				if(lvlUname.equals("default_agg")) { //$NON-NLS-1$
					createDefaultAggregation(aggreg);
				}
				else {
					measure.addAggregation(aggreg);
				}
				refreshDimensionTree(aggreg);
				dial.updateButtonsState();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		btnValidDefaut = new Button(defaultAggregationParComposite, SWT.PUSH);
		btnValidDefaut.setLayoutData(new GridData(SWT.CENTER, SWT.END, false, false, 1, 1));
		btnValidDefaut.setText(Messages.StackedAggregationsComposite_19);
		btnValidDefaut.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				measure.setFormula(""); //$NON-NLS-1$
				measure.setType(""); //$NON-NLS-1$
				measure.setAggregator(""); //$NON-NLS-1$
				measure.setOrigin(null);
				measure.setLastTimeDimensionName(""); //$NON-NLS-1$
				
				OLAPMeasure me = ((DefaultAggregationPartComposite)defaultAggregationParComposite).getMeasure();
				measure.setFormula(me.getFormula());
				measure.setType(me.getType());
				measure.setAggregator(me.getAggregator());
				measure.setOrigin(me.getOrigin());
				measure.setLastTimeDimensionName(me.getLastTimeDimensionName());
				
				IMeasureAggregation aggreg = null;
				if(me.getType().equals("calculated")) { //$NON-NLS-1$
					aggreg = new CalculatedAggregation();
					((CalculatedAggregation)aggreg).setFormula(me.getFormula());
					((CalculatedAggregation)aggreg).setLevel(me.getName());
				}
				else {
					if(me.getLastTimeDimensionName() != null && !me.getLastTimeDimensionName().equals("")) { //$NON-NLS-1$
						aggreg = new LastAggregation();
						((LastAggregation)aggreg).setLevel(me.getName());
						((LastAggregation)aggreg).setAggregator(me.getAggregator());
						((LastAggregation)aggreg).setOrigin(me.getOrigin());
						((LastAggregation)aggreg).setRelatedDimension(me.getLastTimeDimensionName());
					}
					else {
						aggreg = new ClassicAggregation();
						((ClassicAggregation)aggreg).setLevel(me.getName());
						((ClassicAggregation)aggreg).setAggregator(me.getAggregator());
						((ClassicAggregation)aggreg).setOrigin(me.getOrigin());
					}
				}
				
				refreshDimensionTree(aggreg);
				dial.updateButtonsState();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}
	
	public void createDefaultAggregation(IMeasureAggregation agg) {
		if(agg instanceof ClassicAggregation) {
			measure.setAggregator(((ClassicAggregation)agg).getAggregator());
			measure.setOrigin(((ClassicAggregation)agg).getOrigin());
		}
		else if(agg instanceof LastAggregation) {
			measure.setAggregator(((LastAggregation)agg).getAggregator());
			measure.setOrigin(((LastAggregation)agg).getOrigin());
			measure.setLastTimeDimensionName(((LastAggregation)agg).getRelatedDimension());
		}
		else if(agg instanceof CalculatedAggregation) {
			measure.setFormula(((CalculatedAggregation)agg).getFormula());
			measure.setType("calculated"); //$NON-NLS-1$
		}
		dial.updateButtonsState();
	}

	public OlapDynamicMeasure getMeasure() {
		return measure;
	}
	
	public void setLevelSelected(OLAPLevel lvl, OLAPMeasure mea) {
		classicComposite.clearFields();
		lastComposite.clearFields();
		calculatedComposite.clear();
		
		if(lvl == null && mea == null) {
			stackedLayout.topControl = compositeNoLevel;
			compLvlAggType.setVisible(false);
			stackedComposite.layout();
			return;
		}
		else if(lvl == null) {
			compLvlAggType.setVisible(false);
			((DefaultAggregationPartComposite)defaultAggregationParComposite).setMeasure(mea);
			stackedLayout.topControl = defaultAggregationParComposite;
			stackedComposite.layout();
			return;
		}
		else {
			compLvlAggType.setVisible(true);
		}
		
		lvlUname = "["+lvl.getParent().getParent().getName()+"."+lvl.getParent().getName()+"].[" + lvl.getName() +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		lblLevelSelected.setText(Messages.StackedAggregationsComposite_31 + lvlUname);
		
		if(measure.getAggregations() != null) {
			for(IMeasureAggregation aggreg : measure.getAggregations()) {
				if(aggreg.getLevel().equals(lvlUname)) {
					IMeasureAggregation lvlAgg = aggreg;
					if(lvlAgg instanceof ClassicAggregation) {
						classicComposite.setAggregation((ClassicAggregation)lvlAgg);
						cbAggType.select(0);
						stackedLayout.topControl = classicComposite; 
						stackedComposite.layout();
					}
					else if(lvlAgg instanceof LastAggregation) {
						lastComposite.setAggregation((LastAggregation)lvlAgg);
						cbAggType.select(1);
						stackedLayout.topControl = lastComposite; 
						stackedComposite.layout();
					}
					else if(lvlAgg instanceof CalculatedAggregation) {
						calculatedComposite.setFormula(((CalculatedAggregation)lvlAgg).getFormula());
						cbAggType.select(2);
						stackedLayout.topControl = calculatedComposite; 
						stackedComposite.layout();
					}
					return;
				}
			}
		}
		
		cbAggType.select(0);
		stackedLayout.topControl = classicComposite;
		stackedComposite.layout();
	}

	public void setDefault() {
		classicComposite.clearFields();
		lastComposite.clearFields();
		calculatedComposite.clear();
		
		lblLevelSelected.setText(Messages.StackedAggregationsComposite_32);
		
		lvlUname = "default_agg"; //$NON-NLS-1$
		cbAggType.select(0);
		stackedLayout.topControl = classicComposite;
		stackedComposite.layout();
		
		compLvlAggType.setVisible(true);
	}
	
	public void refreshDimensionTree(IMeasureAggregation agg) {
		if(parent instanceof DynamicAggregationComposite) {
			((DynamicAggregationComposite)parent).refreshDimensionTree(agg);
		}
	}

	public void setEditedMeasureData(OlapDynamicMeasure editedMeasure) {
		measure = editedMeasure;
	}
}
