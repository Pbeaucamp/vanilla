package bpm.gateway.ui.views.property.sections.olap;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.olap.IOlapDimensionable;
import bpm.gateway.core.transformations.olap.OlapHelper;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class OlapInputDefinitionSection extends AbstractPropertySection{
	private IOlapDimensionable transfo;
	
	private CCombo dimensionName;
	private CCombo hierarchyName;
	private ComboSelectionListener listener = new ComboSelectionListener();
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Assert.isTrue(((Node)((NodePart) input).getModel()).getGatewayModel() instanceof IOlapDimensionable);
        this.transfo = (IOlapDimensionable)(((Node)((NodePart) input).getModel()).getGatewayModel());
	}
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		Label l = getWidgetFactory().createLabel(composite, Messages.OlapInputDefinitionSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dimensionName = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		dimensionName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		l = getWidgetFactory().createLabel(composite, Messages.OlapInputDefinitionSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		hierarchyName = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		hierarchyName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

	}
	@Override
	public void refresh() {
		dimensionName.removeSelectionListener(listener);
		hierarchyName.removeSelectionListener(listener);
		if (transfo.getDirectoryItemId() != null){
			try{
				loadDimension();
				loadHierarchie();
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getPart().getSite().getShell(), Messages.OlapInputDefinitionSection_2, ex.getMessage());
			}
		}
		else{
			dimensionName.setItems(new String[]{});
		}
		hierarchyName.addSelectionListener(listener);
		dimensionName.addSelectionListener(listener);
	}
	
	private void loadDimension() throws Exception{
		if (transfo.getCubeName() == null){
			dimensionName.setItems(new String[]{});
			return;
		}
		List<String> l = ((Transformation)transfo).getDocument().getOlapHelper().getDimensionNames(true, transfo);
		dimensionName.setItems(l.toArray(new String[l.size()]));
		
		if (transfo.getDimensionName() != null){
			for(int i = 0; i < dimensionName.getItemCount(); i++){
				if (dimensionName.getItem(i).equals(transfo.getDimensionName())){
					dimensionName.select(i);
					break;
				}
			}
		}
		else if (dimensionName.getItemCount() > 0){
			dimensionName.select(0);
			transfo.setDimensionName(dimensionName.getItem(0));
			
		}
		

	}
	
	private void loadHierarchie() throws Exception{
		if (transfo.getCubeName() == null || transfo.getDimensionName() == null){
			hierarchyName.setItems(new String[]{});
			return;
		}
		List<String> l = ((Transformation)transfo).getDocument().getOlapHelper().getHierarchyNames(true, transfo);
		hierarchyName.setItems(l.toArray(new String[l.size()]));
		
		if (transfo.getDimensionName() != null){
			for(int i = 0; i < hierarchyName.getItemCount(); i++){
				if (hierarchyName.getItem(i).equals(transfo.getHierarchieName())){
					hierarchyName.select(i);
					break;
				}
			}
		}
		else if (l.size() > 0){
			hierarchyName.select(0);
			transfo.setHierarchieName(hierarchyName.getItem(0));
			
		}
	}
	
	private class ComboSelectionListener extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == dimensionName){
				transfo.setDimensionName(dimensionName.getText());
				try {
					loadHierarchie();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
			else if (e.widget == hierarchyName){
				transfo.setHierarchieName(hierarchyName.getText());
			}
			
		}
		
	}
}
