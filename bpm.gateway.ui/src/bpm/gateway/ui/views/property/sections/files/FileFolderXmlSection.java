package bpm.gateway.ui.views.property.sections.files;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.model.properties.FileXMLProperties;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class FileFolderXmlSection extends AbstractPropertySection {

	private FileFolderReader transfo;
	private Text rootTag;
	private Text rowTag;

	
	public FileFolderXmlSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		

		Label l = getWidgetFactory().createLabel(composite, Messages.FileXMLGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		rootTag = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		rootTag.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rootTag.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				transfo.setXmlRootTag(rootTag.getText());
				
			}
		});
		
		
		
		Label l3 = getWidgetFactory().createLabel(composite, Messages.FileXMLGeneralSection_2);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		rowTag = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		rowTag.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		rowTag.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				transfo.setXmlRowTag(rowTag.getText());
				
			}
		});

		
		
	}
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (FileFolderReader)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void refresh() {
		rootTag.setText(transfo.getXmlRootTag()); 
		rowTag.setText(transfo.getXmlRowTag());
	}

}
