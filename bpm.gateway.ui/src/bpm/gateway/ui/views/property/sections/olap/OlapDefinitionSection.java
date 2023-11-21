package bpm.gateway.ui.views.property.sections.olap;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.olap.IOlap;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogRepositoryObject;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class OlapDefinitionSection extends AbstractPropertySection{
	private IOlap transfo;
	private RepositoryItem item;
	
	private Text directoryItem;
	private Button browser;
	
	private CCombo cubeName;
	private TabbedPropertySheetPage propertyPage;
	
	private ComboSelectionListener listener = new ComboSelectionListener();
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        Assert.isTrue(((Node)((NodePart) input).getModel()).getGatewayModel() instanceof IOlap);
        this.transfo = (IOlap)(((Node)((NodePart) input).getModel()).getGatewayModel());
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		this.propertyPage = tabbedPropertySheetPage;
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		
		Label l = getWidgetFactory().createLabel(composite, Messages.OlapDefinitionSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		directoryItem = getWidgetFactory().createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		directoryItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		directoryItem.setEnabled(false);
		
		browser = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
		browser.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browser.setEnabled(false);
		browser.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogRepositoryObject dial = new DialogRepositoryObject(
						getPart().getSite().getShell(), IRepositoryApi.FASD_TYPE);
				
				if (dial.open() == Dialog.OK){
					item = dial.getRepositoryItem();
					directoryItem.setText(item.getItemName());
					transfo.setDirectoryItemId(item.getId());
					transfo.setDirectoryItemName(item.getName());
					try {
						loadCube();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					
				}
			}
			
		});
		
		
		l = getWidgetFactory().createLabel(composite, Messages.OlapDefinitionSection_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		cubeName = getWidgetFactory().createCCombo(composite, SWT.READ_ONLY);
		cubeName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
				
		
		
	}
	
	
	
	
	@Override
	public void refresh() {
		cubeName.removeSelectionListener(listener);
		if(transfo.getDirectoryItemName() != null) {
			directoryItem.setText(transfo.getDirectoryItemName());
		}
		
		if (!((Transformation)transfo).getDocument().isRepositoryContextSet() && Activator.getDefault().getRepositoryContext() != null){
			((Transformation)transfo).getDocument().setRepositoryContext(Activator.getDefault().getRepositoryContext());
		}
		browser.setEnabled(Activator.getDefault().getRepositoryContext() != null);
		if (transfo.getDirectoryItemId() != null){
			try{
				loadCube();
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getPart().getSite().getShell(), Messages.OlapDefinitionSection_4, ex.getMessage());
			}
			
			
		}
		else{
			cubeName.setItems(new String[]{});
			
		}
		
		cubeName.addSelectionListener(listener);
		
	}
	
	
	private void loadCube()throws Exception{
	
		
		List<String> l = ((Transformation)transfo).getDocument().getOlapHelper().getCubeNames(true, transfo);
		cubeName.setItems(l.toArray(new String[l.size()]));
		if (transfo.getCubeName() != null){
			for(int i = 0; i < cubeName.getItemCount(); i++){
				if (cubeName.getItem(i).equals(transfo.getCubeName())){
					cubeName.select(i);
					break;
				}
			}
		}
		
		
	}
	
	private class ComboSelectionListener extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == cubeName){
				transfo.setCubeName(cubeName.getText());
				try {
					loadCube();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				for(ISection s : propertyPage.getCurrentTab().getSections()){
					s.refresh();
				}
			}
			
			
		}
		
	}
	
	
}
