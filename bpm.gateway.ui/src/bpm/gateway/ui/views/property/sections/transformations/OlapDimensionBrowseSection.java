package bpm.gateway.ui.views.property.sections.transformations;

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

import bpm.gateway.core.transformations.olap.OlapDimensionExtractor;
import bpm.gateway.core.transformations.olap.OlapHelper;
import bpm.gateway.ui.dialogs.utils.fields.DialogFieldsValues;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogBrowseContent;

public class OlapDimensionBrowseSection extends AbstractPropertySection{
	private OlapDimensionExtractor input;
	
	private Button browse, distinct;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		
		browse = getWidgetFactory().createButton(composite, Messages.OlapDimensionBrowseSection_0, SWT.NONE);
		browse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell sh = getPart().getSite().getShell();
				
				
				try {
					
					List<List<Object>> lst = input.getDocument().getOlapHelper().getValues(input);
					
					
					DialogBrowseContent dial = new DialogBrowseContent(sh, lst, input.getDescriptor(input).getStreamElements());
					dial.open();
					
					
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(sh, Messages.OlapDimensionBrowseSection_1, Messages.OlapDimensionBrowseSection_2 + e1.getMessage());
				}
			}
		});
		
		distinct = getWidgetFactory().createButton(composite, Messages.OlapDimensionBrowseSection_3, SWT.NONE);
		distinct.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		distinct.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Shell sh = getPart().getSite().getShell();

				DialogFieldsValues dial = new DialogFieldsValues(sh, input);
				dial.open();
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		
		super.refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.input = (OlapDimensionExtractor)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	
}
