package bpm.gateway.ui.views.property.sections.mdm;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.data.viz.core.IDataVizComponent;
import bpm.data.viz.core.RemoteDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;

public class DataPreparationSection extends AbstractPropertySection {

	private DataPreparationComposite dataPrepComposite;
	private DataPreparationInput transfo;
	private Node node;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.dataPrepComposite = new DataPreparationComposite(parent, getWidgetFactory(), SWT.NONE);
	}
	
	@Override
	public void refresh() {
		
		IDataVizComponent datavizRemote = new RemoteDataVizComponent(Activator.getDefault().getRepositoryConnection()); 
		
		try {
			List<DataPreparation> dataPreps = datavizRemote.getDataPreparations();
			dataPrepComposite.refresh(dataPreps);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof NodePart);
		this.transfo = (DataPreparationInput) ((Node) ((NodePart) input).getModel()).getGatewayModel();
		this.node = (Node) ((NodePart) input).getModel();
		
		dataPrepComposite.setNode(node);
	}
}
