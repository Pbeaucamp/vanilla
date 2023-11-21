package bpm.gateway.ui.views.property.sections.files;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogComboString;

public class FileFolderXlsSection extends AbstractPropertySection {

	private FileFolderReader transfo;
	private Text sheetName;
	private Button skipFirstRow;
	
	
	
	public FileFolderXlsSection() {
		
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		skipFirstRow = getWidgetFactory().createButton(composite, Messages.FileXLSInputSection_0, SWT.CHECK);
		skipFirstRow.setToolTipText(Messages.FileXLSInputSection_1);
		
		skipFirstRow.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		skipFirstRow.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				transfo.setSkipFirstRow(skipFirstRow.getSelection());
				

			}
			
		});
		Label l = getWidgetFactory().createLabel(composite, Messages.FileXLSGeneralSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		Composite c = getWidgetFactory().createComposite(composite);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		sheetName = getWidgetFactory().createText(c, ""); //$NON-NLS-1$
		sheetName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sheetName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				transfo.setXlsSheetName(sheetName.getText());
				
			}
		});
		
		Button b = getWidgetFactory().createButton(c, "...", SWT.PUSH); //$NON-NLS-1$
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getPart().getSite().getShell();
				try {
					
					
					
					List<String> names = FileXLSHelper.getWorkSheetsNames(transfo);
					
					DialogComboString d = new DialogComboString(sh, names,transfo.getXlsSheetName());
					
					if (d.open() == Dialog.OK){
						transfo.setXlsSheetName(d.getValue());
						refresh();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openWarning(sh, Messages.FileXLSGeneralSection_3, Messages.FileXLSGeneralSection_4 + e1.getMessage());
				}
						
			}

			
		});
		
	}
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = (FileFolderReader) ((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void refresh() {
		if (transfo.getXlsSheetName() != null){
			sheetName.setText(transfo.getXlsSheetName());
		}
		else{
			sheetName.setText(""); //$NON-NLS-1$
		}
		 
		skipFirstRow.setSelection(transfo.isSkipFirstRow());
		
	}
	
	
	
}
