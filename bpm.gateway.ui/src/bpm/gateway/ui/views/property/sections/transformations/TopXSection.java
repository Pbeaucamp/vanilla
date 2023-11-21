package bpm.gateway.ui.views.property.sections.transformations;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.TopXTransformation;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class TopXSection extends AbstractPropertySection {
	protected Node node;
	
	private ComboViewer fieldViewer;
	private Text numberRows;
	private Button ascending, descending, nosort;

	private ISelectionChangedListener cboListener = new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection ss = (IStructuredSelection)fieldViewer.getSelection();
			
			if (ss.isEmpty()){
				((TopXTransformation)node.getGatewayModel()).setField("-1"); //$NON-NLS-1$
			}
			else{
				((TopXTransformation)node.getGatewayModel()).setField((StreamElement)ss.getFirstElement());
			}
			
			
		}
	};
	
	private ModifyListener modLIstener = new ModifyListener() {
		
		public void modifyText(ModifyEvent e) {
			try{
				int i = Integer.parseInt(numberRows.getText());
				((TopXTransformation)node.getGatewayModel()).setX(i);
				numberRows.setBackground(null);
			}catch(NumberFormatException ex){
				ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
				Color color = reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
				numberRows.setBackground(color);
			}
			
		}
	};
	
	
	private SelectionListener sl = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (nosort.getSelection()){
				((TopXTransformation)node.getGatewayModel()).setSorting(TopXTransformation.NONE);
			}
			else if (ascending.getSelection()){
				((TopXTransformation)node.getGatewayModel()).setSorting(TopXTransformation.ASC);
			}
			else if (descending.getSelection()){
				((TopXTransformation)node.getGatewayModel()).setSorting(TopXTransformation.DSC);
			}
		}
	};
	
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		//parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		Composite main = getWidgetFactory().createComposite(parent, SWT.NONE);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		main.setLayout(new GridLayout(2, false));
		
		Label l = getWidgetFactory().createLabel(main, Messages.TopXSection_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		fieldViewer = new ComboViewer(getWidgetFactory().createCCombo(main, SWT.READ_ONLY));
		fieldViewer.getCCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fieldViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((StreamElement)element).name;
			}
		});
		fieldViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		
		l = getWidgetFactory().createLabel(main, Messages.TopXSection_2);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		numberRows = getWidgetFactory().createText(main, ""); //$NON-NLS-1$
		numberRows.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Group g = getWidgetFactory().createGroup(parent, Messages.TopXSection_4);
		//g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		g.setLayout(new GridLayout());
		
		nosort = getWidgetFactory().createButton(g, Messages.TopXSection_5, SWT.RADIO);
		nosort.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ascending = getWidgetFactory().createButton(g, Messages.TopXSection_6, SWT.RADIO);
		ascending.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		descending = getWidgetFactory().createButton(g, Messages.TopXSection_7, SWT.RADIO);
		descending.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

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
		
		fieldViewer.removeSelectionChangedListener(cboListener);
		numberRows.removeModifyListener(modLIstener);
		nosort.removeSelectionListener(sl);
		ascending.removeSelectionListener(sl);
		descending.removeSelectionListener(sl);
		
		TopXTransformation tr = ((TopXTransformation)node.getGatewayModel());
		try{
			fieldViewer.setInput(tr.getDescriptor(tr).getStreamElements());
			try{
				fieldViewer.setSelection(new StructuredSelection(tr.getDescriptor(tr).getStreamElements().get(tr.getField())));
			}catch(Exception ex){
				fieldViewer.setSelection(new StructuredSelection());
			}
			numberRows.setText(tr.getX() + ""); //$NON-NLS-1$
			
			
			
			if (tr.getSorting().equals(TopXTransformation.NONE)){
				nosort.setSelection(true);
			}
			else if (tr.getSorting().equals(TopXTransformation.ASC)){
				ascending.setSelection(true);
			}
			else{
				descending.setSelection(true);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			fieldViewer.setInput(new ArrayList<StreamElement>());
			fieldViewer.setSelection(new StructuredSelection());
			
		}
		
		
		fieldViewer.addSelectionChangedListener(cboListener);
		numberRows.addModifyListener(modLIstener);
		nosort.addSelectionListener(sl);
		ascending.addSelectionListener(sl);
		descending.addSelectionListener(sl);
	}

	
	
	
}
