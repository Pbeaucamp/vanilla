package bpm.gateway.ui.views.property.sections.files.kml;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class KmlInputSection extends AbstractPropertySection{
	private Button generateId;
	private Button extractIdFromPlacemarkername;
	
	private KMLInput kml;	
	private SelectionListener lst = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			kml.setGenerateIdPerFoundObject(generateId.getSelection());
		}
	};
	
	@Override
	public void createControls(Composite parent,TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Group composite = getWidgetFactory().createGroup(parent, Messages.KmlInputSection_0);
		composite.setLayout(new GridLayout());
		
		generateId = getWidgetFactory().createButton(composite, Messages.KmlInputSection_1, SWT.RADIO);
		generateId.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		extractIdFromPlacemarkername = getWidgetFactory().createButton(composite, Messages.KmlInputSection_2, SWT.RADIO);
		extractIdFromPlacemarkername.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		

	}
	@Override
	public void refresh() {
		generateId.removeSelectionListener(lst);
		extractIdFromPlacemarkername.removeSelectionListener(lst);
		
		generateId.setSelection(kml.isGenerateIdPerFoundObject());
		extractIdFromPlacemarkername.setSelection(!kml.isGenerateIdPerFoundObject());
		extractIdFromPlacemarkername.removeSelectionListener(lst);
		generateId.addSelectionListener(lst);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.kml = (KMLInput)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
}
