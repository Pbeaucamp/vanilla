package bpm.gateway.ui.views.property.sections.database.nagios;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.inputs.NagiosDb;
import bpm.gateway.core.transformations.inputs.NagiosRequest;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class NagiosDefinitionSection extends AbstractPropertySection {

	private NagiosDb model;
	private CheckboxTableViewer tableNagiosRequest;
	private List<NagiosRequest> requests;
	
	private NagiosRequest currentRequest;
	
	public NagiosDefinitionSection() {
		
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblInfos = getWidgetFactory().createLabel(composite, Messages.NagiosDefinitionSection_0);
		lblInfos.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		tableNagiosRequest = new CheckboxTableViewer(getWidgetFactory().createTable(composite, 
				SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.CHECK));
		tableNagiosRequest.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tableNagiosRequest.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
			
			@Override
			public void dispose() { }
			
			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<NagiosRequest> requests = (List<NagiosRequest>)inputElement;
				return requests.toArray(new NagiosRequest[requests.size()]);
			}
		});
		tableNagiosRequest.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((NagiosRequest)element).getName();
			}
			
		});
		tableNagiosRequest.addCheckStateListener(new ICheckStateListener(){
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				NagiosRequest request = (NagiosRequest)event.getElement();
				if(event.getChecked()){
					if(currentRequest != null){
						tableNagiosRequest.setChecked(currentRequest, false);
					}
					currentRequest = request;
					model.setNagiosRequest(request);
				}
				else {
					model.setNagiosRequest(null);
				}
				model.refreshDescriptor();
			}
			
		});
		
		requests = NagiosDb.buildNagiosRequest();
		tableNagiosRequest.setInput(requests);
	}
	
	@Override
	public void refresh() {
		if (tableNagiosRequest != null && !tableNagiosRequest.getTable().isDisposed()){
			if(model.getRequest() != null){
				for(NagiosRequest req : requests){
					if(req.getName().equals(model.getRequest().getName())){
						currentRequest = req;
						tableNagiosRequest.setChecked(req, true);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (NagiosDb)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
}
