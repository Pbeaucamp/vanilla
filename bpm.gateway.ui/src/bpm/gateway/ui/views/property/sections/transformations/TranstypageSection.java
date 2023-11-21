package bpm.gateway.ui.views.property.sections.transformations;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.transformations.AlterationInfo;
import bpm.gateway.core.transformations.AlterationStream;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class TranstypageSection extends AbstractPropertySection{

	private static final String PROPERTY_FIELD = "field"; //$NON-NLS-1$
	private static final String PROPERTY_FORMAT = "format"; //$NON-NLS-1$
	private static final String PROPERTY_CLASSNAME = "classname"; //$NON-NLS-1$
	private static final String PROPERTY_BASE_CLASSNAME = "baseClassName"; //$NON-NLS-1$
	
	private static final String[] javaClass = new String[]{
		String.class.getName(), Date.class.getName(),
		Integer.class.getName(),Long.class.getName(), 
		Float.class.getName(),Double.class.getName(), Boolean.class.getName()}; 
	
	private TableViewer viewer;
	
	private Node node;
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);
		
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout());
		
		
		
		Table t = getWidgetFactory().createTable(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		
		viewer = new TableViewer(t);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<AlterationInfo> l = (List<AlterationInfo>)inputElement;
				return l.toArray(new AlterationInfo[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					
					return ((AlterationInfo)element).getStreamElement().name;
				case 1 :
					return ((AlterationInfo)element).getStreamElement().className;
				case 2 :
					return ((AlterationInfo)element).getClassName();
				case 3:
					return ((AlterationInfo)element).getFormat();
				}
				return Messages.TranstypageSection_0;
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
		
		TableColumn fieldName = new TableColumn(t, SWT.NONE);
		fieldName.setText(Messages.TranstypageSection_5);
		fieldName.setWidth(200);
		
		TableColumn oClassName = new TableColumn(t, SWT.NONE);
		oClassName.setText(Messages.TranstypageSection_6);
		oClassName.setWidth(200);
		
		TableColumn className = new TableColumn(t, SWT.NONE);
		className.setText(Messages.TranstypageSection_7);
		className.setWidth(200);
		
		
		TableColumn format = new TableColumn(t, SWT.NONE);
		format.setText(Messages.TranstypageSection_8);
		format.setWidth(200);
		
		
		
		viewer.setColumnProperties(new String[]{PROPERTY_FIELD, PROPERTY_BASE_CLASSNAME, PROPERTY_CLASSNAME, PROPERTY_FORMAT});
		viewer.setCellModifier(new CellModifier());

		viewer.setCellEditors(new CellEditor[]{
				new TextCellEditor(viewer.getTable()),
				new TextCellEditor(viewer.getTable()),
				new ComboBoxCellEditor(viewer.getTable(),javaClass , SWT.READ_ONLY), 
				new ComboBoxCellEditor(viewer.getTable(),AlterationStream.DATE_FORMATS , SWT.READ_ONLY)});
		
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
		AlterationStream tr = (AlterationStream)node.getGatewayModel();
		viewer.setInput(tr.getAlterations());
		
	}
	public class CellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if (property.equals(PROPERTY_FIELD) || property.equals(PROPERTY_BASE_CLASSNAME)){
				return false;
			}
			
			return true;
		}

		public Object getValue(Object element, String property) {
			AlterationInfo ai = (AlterationInfo)element;
			
			if (property.equals(PROPERTY_FORMAT)){
				
				for(int i = 0; i < AlterationStream.DATE_FORMATS.length; i++){
					if (AlterationStream.DATE_FORMATS[i].equals(ai.getFormat())){
						return i;
					}
				}
				
			}
			
			if (property.equals(PROPERTY_CLASSNAME)){
				
				for(int i = 0; i < javaClass.length; i++){
					if (javaClass[i].equals(ai.getClassName())){
						return i;
					}
				}
				
			}
			
			
			return -1;
		}

		public void modify(Object element, String property, Object value) {
			AlterationInfo ai = (AlterationInfo)((TableItem)element).getData();
			
			
			if (property.equals(PROPERTY_FORMAT)){
				Integer i = (Integer)value;
				if (i >= 0){
					ai.setFormat(AlterationStream.DATE_FORMATS[i]);
				}
				
			}
			else if (property.equals(PROPERTY_CLASSNAME)){
				Integer i = (Integer)value;
				if (i >= 0){
					ai.setClassName(javaClass[i]);
				}
			}
			
			viewer.refresh();
		}
	}
}
