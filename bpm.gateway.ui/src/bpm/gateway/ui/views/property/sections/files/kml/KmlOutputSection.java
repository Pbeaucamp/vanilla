package bpm.gateway.ui.views.property.sections.files.kml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.kml.KMLOutput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class KmlOutputSection extends AbstractPropertySection  {
	private ComboViewer altitude, longitude, latitude, description, name;
	private KMLOutput kml;
	
	 
	private class KmlContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			
			return (Object[])inputElement;
		}

		public void dispose() {
			
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}
		
	};
	
	private class KmlLabelProvider extends LabelProvider{
		@Override
		public String getText(Object element) {
			if (element instanceof StreamElement){
				return ((StreamElement)element).name;
			}
			else if (element instanceof String){
				return element.toString();
			}
			return ""; //$NON-NLS-1$
		}
	}
	
	
	private ISelectionChangedListener lst = new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event) {
			Object[] inputs =  getViewerContent(kml);
			
			Integer i = null;
			Object e = ((IStructuredSelection)event.getSelection()).getFirstElement();
			int _i = 0;
			for(Object o : inputs){
				if (o == e){
					i = _i;
					break;
				}
				else{
					_i++;
				}
			}
			
			i--;
			if (event.getSelectionProvider() == altitude){
				kml.setCoordinateAltitudeIndex(i);
			}
			if (event.getSelectionProvider() == longitude){
				kml.setCoordinateLongitudeIndex(i);		
			}
			if (event.getSelectionProvider() == latitude){
				kml.setCoordinateLatitudeIndex(i);
			}
			if (event.getSelectionProvider() == name){
				kml.setNameIndex(i);
			}
			if (event.getSelectionProvider() == description){
				kml.setDescriptionIndex(i);
			}
			
		}
	};
	
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(composite, Messages.KmlOutputSection_1);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		
		l = getWidgetFactory().createLabel(composite, Messages.KmlOutputSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		name = new ComboViewer(getWidgetFactory().createCCombo(composite, SWT.READ_ONLY));
		name.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setContentProvider(new KmlContentProvider());
		name.setLabelProvider(new KmlLabelProvider());
		
		
		
		l = getWidgetFactory().createLabel(composite, Messages.KmlOutputSection_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		description = new ComboViewer(getWidgetFactory().createCCombo(composite, SWT.READ_ONLY));
		description.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		description.setContentProvider(new KmlContentProvider());
		description.setLabelProvider(new KmlLabelProvider());

		l = getWidgetFactory().createLabel(composite, Messages.KmlOutputSection_4);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		longitude = new ComboViewer(getWidgetFactory().createCCombo(composite, SWT.READ_ONLY));
		longitude.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		longitude.setContentProvider(new KmlContentProvider());
		longitude.setLabelProvider(new KmlLabelProvider());
		
		l = getWidgetFactory().createLabel(composite, Messages.KmlOutputSection_5);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		latitude = new ComboViewer(getWidgetFactory().createCCombo(composite, SWT.READ_ONLY));
		latitude.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		latitude.setContentProvider(new KmlContentProvider());
		latitude.setLabelProvider(new KmlLabelProvider());
		
		l = getWidgetFactory().createLabel(composite, Messages.KmlOutputSection_6);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		altitude = new ComboViewer(getWidgetFactory().createCCombo(composite, SWT.READ_ONLY));
		altitude.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		altitude.setContentProvider(new KmlContentProvider());
		altitude.setLabelProvider(new KmlLabelProvider());
	}
	
	private Object[] getViewerContent(KMLOutput kml){

		List<Object> values = new ArrayList<Object>();
		values.add(Messages.KmlOutputSection_7);
		
		List<StreamElement> streamElements = new ArrayList<StreamElement>();
		try {
			if(kml != null && kml.getInputs() != null && !kml.getInputs().isEmpty() && kml.getInputs().get(0) != null 
					&& kml.getInputs().get(0).getDescriptor(kml) != null && kml.getInputs().get(0).getDescriptor(kml).getStreamElements() != null){
				streamElements = kml.getInputs().get(0).getDescriptor(kml).getStreamElements();
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
		for(StreamElement e:streamElements){
			values.add(e);
		}
		
		Object[] vals = values.toArray(new Object[values.size()]);
		return vals;
	}

	@Override
	public void refresh() {
		longitude.removeSelectionChangedListener(lst);
		latitude.removeSelectionChangedListener(lst);
		altitude.removeSelectionChangedListener(lst);
		name.removeSelectionChangedListener(lst);
		description.removeSelectionChangedListener(lst);
		
		Object[] vals = getViewerContent(kml);
			
		longitude.setInput(vals);
		latitude.setInput(vals);
		altitude.setInput(vals);
		description.setInput(vals);
		name.setInput(vals);
		
		List<StreamElement> streamElements = new ArrayList<StreamElement>();
		try {
			if(kml != null && kml.getInputs() != null && !kml.getInputs().isEmpty() && kml.getInputs().get(0) != null 
					&& kml.getInputs().get(0).getDescriptor(kml) != null && kml.getInputs().get(0).getDescriptor(kml).getStreamElements() != null){
				streamElements = kml.getInputs().get(0).getDescriptor(kml).getStreamElements();
			}
		} catch (ServerException e) {
			e.printStackTrace();
		}
		
		
		try{
			altitude.setSelection(new StructuredSelection(streamElements.get(kml.getCoordinateAltitudeIndex())));
		}catch (Exception e) {
			altitude.setSelection(new StructuredSelection(vals[0]));
		}
	
		try{
			longitude.setSelection(new StructuredSelection(streamElements.get(kml.getCoordinateLongitudeIndex())));
		}catch (Exception e) {
			longitude.setSelection(new StructuredSelection(vals[0]));
		}
	
		
		
		try{
			latitude.setSelection(new StructuredSelection(streamElements.get(kml.getCoordinateLatitudeIndex())));
		}catch (Exception e) {
			latitude.setSelection(new StructuredSelection(vals[0]));
		}
		
		try{
			name.setSelection(new StructuredSelection(streamElements.get(kml.getNameIndex())));
		}catch (Exception e) {
			name.setSelection(new StructuredSelection(vals[0]));
		}
		
		try{
			description.setSelection(new StructuredSelection(streamElements.get(kml.getDescriptionIndex())));
		}catch (Exception e) {
			description.setSelection(new StructuredSelection(vals[0]));
		}
		
		longitude.addSelectionChangedListener(lst);
		latitude.addSelectionChangedListener(lst);
		altitude.addSelectionChangedListener(lst);
		name.addSelectionChangedListener(lst);
		description.addSelectionChangedListener(lst);
	}
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.kml = (KMLOutput)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
}
