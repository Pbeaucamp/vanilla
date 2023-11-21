package bpm.gateway.ui.views.property.sections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.webservice.WebServiceHelper;
import bpm.gateway.core.transformations.webservice.WebServiceInput;
import bpm.gateway.ui.composites.service.ServiceViewerComposite;
import bpm.gateway.ui.composites.service.ServiceViewerComposite.TypeViewer;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.beans.service.IService;

public class WebServiceInputSection extends AbstractPropertySection {

	private Text webServiceUrl;
	private Button browseWebservice;
	private CCombo methods;
	private ServiceViewerComposite serviceViewer;
//	private TableViewer viewer;

	private WebServiceInput webServiceTransfo;
	private HashMap<String, List<IService>> webServiceMethods;

	public WebServiceInputSection() {

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.webServiceTransfo = (WebServiceInput) ((Node) ((NodePart) input).getModel()).getGatewayModel();
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {

		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.WebServiceInputSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		webServiceUrl = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		webServiceUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		browseWebservice = getWidgetFactory().createButton(composite, Messages.FmdtInputSection_2, SWT.PUSH);
		browseWebservice.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browseWebservice.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String url = webServiceUrl.getText();
				webServiceTransfo.setWebServiceUrl(url);
				fillMethods();
			}

		});

		Label l2 = getWidgetFactory().createLabel(composite, Messages.WebServiceInputSection_1);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		methods = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		methods.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		methods.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				webServiceTransfo.setMethodName(methods.getText());
				loadMethodInfo();
			}

		});
		
		serviceViewer = new ServiceViewerComposite(composite, SWT.NONE, TypeViewer.TYPE_WEB_SERVICE_INPUT, null);
		serviceViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
	}

	private void loadMethodInfo() {
		String methodName = methods.getText();
		webServiceTransfo.setMethodName(methodName);
		
		if(webServiceMethods != null){
			for (Entry<String, List<IService>> method : webServiceMethods.entrySet()) {
				if (method.getKey().equals(methodName)) {
					serviceViewer.setInput(method.getValue());
					webServiceTransfo.setParameters(method.getValue());
				}
			}
		}
	}

	@Override
	public void refresh() {

		if (webServiceTransfo.getWebServiceUrl() != null && !webServiceTransfo.getWebServiceUrl().isEmpty()) {
			webServiceUrl.setText(webServiceTransfo.getWebServiceUrl());
		}
		if (webServiceTransfo.getMethodName() != null && !webServiceTransfo.getMethodName().isEmpty()) {
			if (methods.getItemCount() == 0) {
				fillMethods();
			}
		}
	}

	private void fillMethods() {
		String url = webServiceUrl.getText();
		webServiceMethods = WebServiceHelper.getMethodNames(url);

		int index = -1;
		int i = 0;
		methods.removeAll();
		if(webServiceMethods != null){
			for (Entry<String, List<IService>> method : webServiceMethods.entrySet()) {
				methods.add(method.getKey());
				if (method.getKey().equals(webServiceTransfo.getMethodName())) {
					index = i;
					serviceViewer.setInput(webServiceTransfo.getParameters() != null ? webServiceTransfo.getParameters() : new ArrayList<IService>());
				}
				i++;
			}
		}
		
		if(index != -1){
			methods.select(index);
		}
	}
	
	
}
