package bpm.gateway.ui.views.property.sections.transformations.norparena;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.vanillamaps.VanillaMapAddressOutput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class NorparenaOutputAdressSection extends AbstractPropertySection{
	private Node node;
	private List inputList;
	
	private ComboViewer arrondissement;
	private ComboViewer bloc;
	private ComboViewer city;
	private ComboViewer country;
	private ComboViewer inseeCode;
	private ComboViewer label;
	private ComboViewer street1;
	private ComboViewer street2;
	private ComboViewer zipCode;
	
	
	private ViewerListener  selectionListener = new ViewerListener();
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite main = getWidgetFactory().createComposite(parent);
		main.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		arrondissement = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		arrondissement.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		arrondissement.setContentProvider(new ArrayContentProvider());
		arrondissement.setLabelProvider(new AdressLabelProvider());
		
		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		bloc = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		bloc.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bloc.setContentProvider(new ArrayContentProvider());
		bloc.setLabelProvider(new AdressLabelProvider());

		
		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		city = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		city.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		city.setContentProvider(new ArrayContentProvider());
		city.setLabelProvider(new AdressLabelProvider());

		
		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		country = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		country.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		country.setContentProvider(new ArrayContentProvider());
		country.setLabelProvider(new AdressLabelProvider());

		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_4);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		inseeCode = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		inseeCode.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inseeCode.setContentProvider(new ArrayContentProvider());
		inseeCode.setLabelProvider(new AdressLabelProvider());

		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_5);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		label = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		label.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		label.setContentProvider(new ArrayContentProvider());
		label.setLabelProvider(new AdressLabelProvider());

		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_6);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		street1 = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		street1.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		street1.setContentProvider(new ArrayContentProvider());
		street1.setLabelProvider(new AdressLabelProvider());

		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_7);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		street2 = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		street2.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		street2.setContentProvider(new ArrayContentProvider());
		street2.setLabelProvider(new AdressLabelProvider());

		l = getWidgetFactory().createLabel(main, Messages.NorparenaOutputAdressSection_8);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		zipCode = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		zipCode.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		zipCode.setContentProvider(new ArrayContentProvider());
		zipCode.setLabelProvider(new AdressLabelProvider());

	}
	@Override
	public void refresh() {
		
		arrondissement.removeSelectionChangedListener(selectionListener);
		bloc.removeSelectionChangedListener(selectionListener);
		city.removeSelectionChangedListener(selectionListener);
		country.removeSelectionChangedListener(selectionListener);
		inseeCode.removeSelectionChangedListener(selectionListener);
		label.removeSelectionChangedListener(selectionListener);
		street1.removeSelectionChangedListener(selectionListener);
		street2.removeSelectionChangedListener(selectionListener);
		zipCode.removeSelectionChangedListener(selectionListener);
		
		
		inputList = new ArrayList();
		inputList.add(""); //$NON-NLS-1$
		if (!node.getGatewayModel().getInputs().isEmpty()){
			try{
				inputList.addAll(node.getGatewayModel().getInputs().get(0).getDescriptor(node.getGatewayModel()).getStreamElements());
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		VanillaMapAddressOutput tr = (VanillaMapAddressOutput)node.getGatewayModel();
		
		try{
			arrondissement.setInput(inputList);
			if (tr.getInputArrondissementIndex() == null || tr.getInputArrondissementIndex() < 0){
				arrondissement.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				arrondissement.setSelection(new StructuredSelection(inputList.get(tr.getInputArrondissementIndex() + 1)));
			}
			bloc.setInput(inputList);
			if (tr.getInputArrondissementIndex() == null || tr.getInputArrondissementIndex() < 0){
				bloc.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				bloc.setSelection(new StructuredSelection(inputList.get(tr.getInputBlocIndex() + 1)));
			}
			
			city.setInput(inputList);
			if (tr.getInputCityIndex() == null || tr.getInputCityIndex() < 0){
				city.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				city.setSelection(new StructuredSelection(inputList.get(tr.getInputCityIndex() + 1)));
			}
			
			country.setInput(inputList);
			if (tr.getInputCountryIndex() == null || tr.getInputCountryIndex() < 0){
				country.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				country.setSelection(new StructuredSelection(inputList.get(tr.getInputCountryIndex() + 1)));
			}
			
			inseeCode.setInput(inputList);
			if (tr.getInputInseeCodeIndex() == null || tr.getInputInseeCodeIndex() < 0){
				inseeCode.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				inseeCode.setSelection(new StructuredSelection(inputList.get(tr.getInputInseeCodeIndex() + 1)));
			}
			label.setInput(inputList);
			if (tr.getInputLabelIndex() == null || tr.getInputLabelIndex() < 0){
				label.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				label.setSelection(new StructuredSelection(inputList.get(tr.getInputLabelIndex() + 1)));
			}
			street1.setInput(inputList);
			if (tr.getInputStreet1Index() == null || tr.getInputStreet1Index() < 0){
				street1.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				street1.setSelection(new StructuredSelection(inputList.get(tr.getInputStreet1Index() + 1)));
			}
			street2.setInput(inputList);
			if (tr.getInputStreet2Index() == null || tr.getInputStreet2Index() < 0){
				street2.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				street2.setSelection(new StructuredSelection(inputList.get(tr.getInputStreet2Index() + 1)));
			}
			
			zipCode.setInput(inputList);
			if (tr.getInputZipcodeIndex() == null || tr.getInputZipcodeIndex() < 0){
				zipCode.setSelection(new StructuredSelection(inputList.get(0)));
			}
			else{
				zipCode.setSelection(new StructuredSelection(inputList.get(tr.getInputZipcodeIndex() + 1)));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		
		arrondissement.addSelectionChangedListener(selectionListener);
		bloc.addSelectionChangedListener(selectionListener);
		city.addSelectionChangedListener(selectionListener);
		country.addSelectionChangedListener(selectionListener);
		inseeCode.addSelectionChangedListener(selectionListener);
		label.addSelectionChangedListener(selectionListener);
		street1.addSelectionChangedListener(selectionListener);
		street2.addSelectionChangedListener(selectionListener);
		zipCode.addSelectionChangedListener(selectionListener);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
       
	}
	
	
	private class AdressLabelProvider extends LabelProvider{
		@Override
		public String getText(Object element) {
			if (element instanceof String){
				return "--- None ---"; //$NON-NLS-1$
			}
			return ((StreamElement)element).name;
		}
	}
	
	private class ViewerListener implements ISelectionChangedListener{

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			Object o = ((IStructuredSelection)event.getSelection()).getFirstElement();
			int pos = inputList.indexOf(o) - 1;
			if (event.getSelectionProvider() == arrondissement){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputArrondissementIndex(pos);		
			}
			else if(event.getSelectionProvider() == bloc){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputBlocIndex(pos);		
			}
			else if (event.getSelectionProvider() == city){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputCityIndex(pos);
			}
			else if (event.getSelectionProvider() == country){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputCountryIndex(pos);
			}
			else if (event.getSelectionProvider() == inseeCode){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputInseeCodeIndex(pos);
			}
			else if (event.getSelectionProvider() == label){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputLabelIndex(pos);
			}
			else if (event.getSelectionProvider() == street1){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputStreet1Index(pos);
			}
			else if (event.getSelectionProvider() == street2){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputStreet2Index(pos);
			}
			else if (event.getSelectionProvider() == zipCode){
				((VanillaMapAddressOutput)node.getGatewayModel()).setInputZipcodeIndex(pos);
			}
			
				
		}	
		
	}
}
