package bpm.gateway.ui.views.property.sections.files;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.server.file.FileVCLHelper;
import bpm.gateway.core.server.file.FileWekaHelper;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.server.file.FileXMLHelper;
import bpm.gateway.core.server.ldap.LdapHelper;
import bpm.gateway.core.transformations.files.FileFolderHelper;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.LdapInput;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.dialogs.utils.fields.DialogFieldsValues;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class FileOutputSection extends AbstractPropertySection {

	private Node node;
//	private StreamComposite streamComposite;
	private Button bBrowse;
	
	public FileOutputSection() {
		
	}

	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, true));
	
		bBrowse = getWidgetFactory().createButton(composite, Messages.FileOutputSection_0, SWT.PUSH);
		bBrowse.setImage( Activator.getDefault().getImageRegistry().get(IconsNames.browse_datas_16));
		bBrowse.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		bBrowse.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					Transformation os = node.getGatewayModel();
					Shell sh = getPart().getSite().getShell();
					
					
					try {
						
						List<List<Object>> lst = null;
						
						if (os instanceof DataBaseInputStream || os instanceof DataBaseInputStream){
							lst = DataBaseHelper.getValues((DataStream)os, 100);
						}
						else if (os instanceof FileOutputWeka){
							lst = FileWekaHelper.getValues((FileOutputWeka)os, 0, 100);
						}
						else if (os instanceof FileCSV){
							lst = FileCSVHelper.getValues((FileCSV)os, 0, 100);
						}
						else if (os instanceof FileXLS){
							lst = FileXLSHelper.getValues((FileXLS)os, 0, 100);
						}
						else if (os instanceof FileXML){
							lst = FileXMLHelper.getValues((FileXML)os, 0, 100);
						}
						else if (os instanceof FileVCL){
							lst = FileVCLHelper.getValues((FileVCL)os, 0, 100);
						}
						else if (os instanceof LdapInput){
							lst = LdapHelper.getValues((LdapInput)os, 0, 100);
						}
						else if (os instanceof FileFolderReader){
							lst = FileFolderHelper.getValues((FileFolderReader)os, 0, 100);
						}
						
						DialogBrowseContent dial = new DialogBrowseContent(sh, lst, os.getDescriptor(os).getStreamElements());
						dial.open();
						
						
					} catch (Throwable e1) {
						e1.printStackTrace();
						MessageDialog.openError(sh, Messages.FileOutputSection_1, Messages.FileOutputSection_2 + e1.getMessage());
					}
					
				}
				
			});		
		
		

		 Button bDistcintValues = getWidgetFactory().createButton(composite, Messages.FileOutputSection_3, SWT.PUSH);
		 bDistcintValues.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		 bDistcintValues.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					DataStream os = (DataStream)node.getGatewayModel();
					Shell sh = getPart().getSite().getShell();

					DialogFieldsValues dial = new DialogFieldsValues(sh, os);
					dial.open();

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
		
		Transformation transfo = node.getGatewayModel();
//		bBrowse.setEnabled(!(node.getGatewayModel() instanceof FileOutputXML));
			
//		try {
//			streamComposite.fillDatas(transfo.getDescriptor().getStreamElements());
//		} catch (ServerException e) {
//			
//			e.printStackTrace();
//		}
	}


	@Override
	public void aboutToBeShown() {
//		node.addPropertyChangeListener(listenerConnection);
		super.aboutToBeShown();
	}


	@Override
	public void aboutToBeHidden() {
//		if (node != null){
//			node.removePropertyChangeListener(listenerConnection);
//		}
		super.aboutToBeHidden();
	}
}
