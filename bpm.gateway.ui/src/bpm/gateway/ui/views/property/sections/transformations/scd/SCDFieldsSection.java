package bpm.gateway.ui.views.property.sections.transformations.scd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class SCDFieldsSection extends AbstractPropertySection {

	private Node node;
	
	private CCombo technicalField;
	
	private CCombo dateStreamField;
	private CCombo dateStartField;
	private CCombo dateStopField;
	private Text maxYear;
	
	
	private CCombo activeField;
	private Text activeValue;
	private Text inactiveValue;
	
	private Button dbInc, computeMax;
	
	public SCDFieldsSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		/*
		 * Technical Key Definition
		 */
		Group g = getWidgetFactory().createGroup(parent, Messages.SCDFieldsSection_0);
		g.setLayout(new GridLayout(2, false));
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = getWidgetFactory().createLabel(g, Messages.SCDFieldsSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		technicalField = getWidgetFactory().createCCombo(g, SWT.READ_ONLY | SWT.BORDER | SWT.FLAT);
		technicalField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		technicalField.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((SlowChangingDimension2)node.getGatewayModel()).setTargetKeyIndex(technicalField.getSelectionIndex());
			}
			
		});
		
		dbInc = getWidgetFactory().createButton(g, SlowChangingDimension2.SEQUENCE_TYPE[SlowChangingDimension2.DB_AUTO_INC_SEQUENCE], SWT.RADIO);
		dbInc.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		dbInc.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dbInc.getSelection()){
					((SlowChangingDimension2)node.getGatewayModel()).setSequenceType(SlowChangingDimension2.DB_AUTO_INC_SEQUENCE);
				}
				else{
					((SlowChangingDimension2)node.getGatewayModel()).setSequenceType(SlowChangingDimension2.MAX_INC_SEQUENCE);
				}
			}
			
		});
		
		computeMax = getWidgetFactory().createButton(g, SlowChangingDimension2.SEQUENCE_TYPE[SlowChangingDimension2.MAX_INC_SEQUENCE], SWT.RADIO);
		computeMax.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		computeMax.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dbInc.getSelection()){
					((SlowChangingDimension2)node.getGatewayModel()).setSequenceType(SlowChangingDimension2.DB_AUTO_INC_SEQUENCE);
				}
				else{
					((SlowChangingDimension2)node.getGatewayModel()).setSequenceType(SlowChangingDimension2.MAX_INC_SEQUENCE);
				}
			}
			
		});

		/*
		 * TODO : enable it
		 */
		Button seq = getWidgetFactory().createButton(g, Messages.SCDFieldsSection_2, SWT.RADIO);
		seq.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		seq.setEnabled(false);
		

		/*
		 * Date Field Definition
		 */
		Group g2 = getWidgetFactory().createGroup(parent, Messages.SCDFieldsSection_3);
		g2.setLayout(new GridLayout(2, false));
		g2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = getWidgetFactory().createLabel(g2, Messages.SCDFieldsSection_4);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dateStreamField = getWidgetFactory().createCCombo(g2, SWT.READ_ONLY | SWT.BORDER | SWT.FLAT);
		dateStreamField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dateStreamField.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((SlowChangingDimension2)node.getGatewayModel()).setInputDateField(dateStreamField.getSelectionIndex());
			}
			
		});
		
		Label l3 = getWidgetFactory().createLabel(g2, Messages.SCDFieldsSection_5);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dateStartField = getWidgetFactory().createCCombo(g2, SWT.BORDER | SWT.READ_ONLY);
		dateStartField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dateStartField.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((SlowChangingDimension2)node.getGatewayModel()).setTargetStartDateIndex(dateStartField.getSelectionIndex());
			}
			
		});

		
		Label l4 = getWidgetFactory().createLabel(g2, Messages.SCDFieldsSection_6);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dateStopField = getWidgetFactory().createCCombo(g2, SWT.BORDER | SWT.READ_ONLY);
		dateStopField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dateStopField.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((SlowChangingDimension2)node.getGatewayModel()).setTargetStopDateIndex(dateStopField.getSelectionIndex());
			}
			
		});
		
		Label l10 = getWidgetFactory().createLabel(g2, Messages.SCDFieldsSection_7);
		l10.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		maxYear = getWidgetFactory().createText(g2, "", SWT.BORDER ); //$NON-NLS-1$
		maxYear.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		maxYear.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				try{
					int i = Integer.parseInt(maxYear.getText());
					((SlowChangingDimension2)node.getGatewayModel()).setMaximumYear(i);
				}catch(Exception ex){
					
				}
			}

		});


		/*
		 * Active Field Definition
		 */
		Group g4 = getWidgetFactory().createGroup(parent, Messages.SCDFieldsSection_9);
		g4.setLayout(new GridLayout(2, false));
		g4.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l7 = getWidgetFactory().createLabel(g4, Messages.SCDFieldsSection_10);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		activeField = getWidgetFactory().createCCombo(g4, SWT.BORDER | SWT.READ_ONLY);
		activeField.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		activeField.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				((SlowChangingDimension2)node.getGatewayModel()).setTargetActiveIndex(activeField.getSelectionIndex());
			}
			
		});

		Label l8 = getWidgetFactory().createLabel(g4, Messages.SCDFieldsSection_11);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		activeValue = getWidgetFactory().createText(g4, "", SWT.BORDER); //$NON-NLS-1$
		activeValue.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		activeValue.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				((SlowChangingDimension2)node.getGatewayModel()).setActiveValue(activeValue.getText());
			}
			
		});
		
		Label l9 = getWidgetFactory().createLabel(g4, Messages.SCDFieldsSection_13);
		l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		inactiveValue = getWidgetFactory().createText(g4, "", SWT.BORDER); //$NON-NLS-1$
		inactiveValue.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inactiveValue.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				((SlowChangingDimension2)node.getGatewayModel()).setInactiveValue(inactiveValue.getText());
			}
			
		});

	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
	}
	
	@Override
	public void refresh() {
		
		/*
		 * fill the Combos
		 */
		SlowChangingDimension2 scd = (SlowChangingDimension2)node.getGatewayModel();
		List<String> scdFieldsNames = new ArrayList<String>();
		
		
		try {
			for(StreamElement e : scd.getDescriptor(scd).getStreamElements()){
				scdFieldsNames.add(e.name);
			}
		} catch (ServerException e1) {
			
			e1.printStackTrace();
		}
		
		
		technicalField.setItems(scdFieldsNames.toArray(new String[scdFieldsNames.size()]));
		if (scd.getTargetKeyIndex() != null){
			technicalField.select(scd.getTargetKeyIndex());
		}
		
		dateStartField.setItems(scdFieldsNames.toArray(new String[scdFieldsNames.size()]));
		if (scd.getTargetStartDateIndex() != null){
			dateStartField.select(scd.getTargetStartDateIndex());
		}
		
		dateStopField.setItems(scdFieldsNames.toArray(new String[scdFieldsNames.size()]));
		if (scd.getTargetStopDateIndex() != null){
			dateStopField.select(scd.getTargetStopDateIndex());
		}
		
		activeField.setItems(scdFieldsNames.toArray(new String[scdFieldsNames.size()]));
		if (scd.getTargetActiveIndex() != null){
			activeField.select(scd.getTargetActiveIndex());
		}
		
		if (scd.getActiveValue() != null){
			activeValue.setText(scd.getActiveValue());
		}
		else{
			activeValue.setText(""); //$NON-NLS-1$
		}
		
		if (scd.getInactiveValue() != null){
			inactiveValue.setText(scd.getInactiveValue());
		}
		else{
			inactiveValue.setText(""); //$NON-NLS-1$
		}
		
		if (scd.getMaximumYear() != null){
			maxYear.setText(scd.getMaximumYear() + ""); //$NON-NLS-1$
		}
		
		switch(scd.getSequenceType()){
		case SlowChangingDimension2.DB_AUTO_INC_SEQUENCE:
			dbInc.setSelection(true);
			break;
		case SlowChangingDimension2.MAX_INC_SEQUENCE:
			computeMax.setSelection(true);
			break;
		}
		
		List<String> inputFieldsNames = new ArrayList<String>();
		
		if (!scd.getInputs().isEmpty()){
			
			try {
				for(StreamElement e : scd.getInputs().get(0).getDescriptor(scd).getStreamElements()){
					inputFieldsNames.add(e.name);
				}
			} catch (ServerException e) {
				
				e.printStackTrace();
			}
			dateStreamField.setItems(inputFieldsNames.toArray(new String[inputFieldsNames.size()]));
			if (scd.getInputDateField() != null){
				dateStreamField.select(scd.getInputDateField());
			}
		}
		
		
		
	}
}
