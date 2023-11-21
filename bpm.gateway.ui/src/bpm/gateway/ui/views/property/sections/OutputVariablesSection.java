package bpm.gateway.ui.views.property.sections;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.viewer.StreamDescriptorStructuredContentProvider;


public class OutputVariablesSection extends AbstractPropertySection {

	private Transformation transfo;
	private TableViewer table;
	private ComboBoxCellEditor combo;
	private static final String NONE_VARIABLE = "--- None ---"; //$NON-NLS-1$
	
	
	public OutputVariablesSection() {
		
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		parent.setLayout(new GridLayout());
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTable(composite);
		
	}
	@Override
	public void refresh() {
		List<String> l = new ArrayList<String>();
		l.add(NONE_VARIABLE);
		
		for(Variable v : ResourceManager.getInstance().getVariables(Variable.LOCAL_VARIABLE)){
			l.add(v.getName());
		}
		for(Variable v : transfo.getDocument().getVariables()){
			l.add(v.getName());
		}
		
		combo.setItems(l.toArray(new String[l.size()]));
		
		try{
			table.setInput(transfo.getDescriptor(transfo));
		}catch(Exception e){
			table.setInput(null);
		}
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.transfo = ((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	
	private void createTable(Composite parent){
		table = new TableViewer(getWidgetFactory().createTable(parent, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION));
		table.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setContentProvider(new StreamDescriptorStructuredContentProvider());
		table.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0){
					return new String(((StreamElement)element).name);
				}
				else if (columnIndex == 1){
					Variable v = transfo.getOutputVariable((StreamElement)element);
					if (v == null){
						return NONE_VARIABLE;
					}
					else {
						return v.getName(); 
					}
					
					
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
				
				
			}

			public void dispose() {
				
				
			}

			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
				
			}
			
		});
		
		TableColumn element = new TableColumn(table.getTable(), SWT.NONE);
		element.setText(Messages.OutputVariablesSection_1);
		element.setWidth(200);
		
		TableColumn variable = new TableColumn(table.getTable(), SWT.NONE);
		variable.setText(Messages.OutputVariablesSection_2);
		variable.setWidth(200);
		
		table.setColumnProperties(new String[]{"field", "variable"}); //$NON-NLS-1$ //$NON-NLS-2$
		combo = new ComboBoxCellEditor(table.getTable(), new String[]{NONE_VARIABLE});
		table.setCellEditors(new CellEditor[]{null, combo});
		
		table.setCellModifier(new ICellModifier(){

			public boolean canModify(Object element, String property) {
				return property.equals("variable"); //$NON-NLS-1$
			}

			public Object getValue(Object element, String property) {
				Variable v = transfo.getOutputVariable((StreamElement)element);
				if (v == null){
					return 0;
				}
				for( int i = 1; i < combo.getItems().length; i++){
					if (combo.getItems()[i].equals(v.getName())){
						return i;
					}
				}
				return 0;
				
			}

			public void modify(Object element, String property, Object value) {
				String vName = combo.getItems()[(Integer)value];
				Variable variable = ResourceManager.getInstance().getVariable(vName);
				if (variable == null){
					variable = transfo.getDocument().getVariable(vName);
				}
				if (((Integer)value).intValue() == 0){
					transfo.removeOutputVariable(variable);
				}
				else{
					
					transfo.addOutputVariable((StreamElement)(((TableItem)element).getData()), variable);
				}
					
				table.refresh();
				
				
				
			}
			
		});
		
	}

}
