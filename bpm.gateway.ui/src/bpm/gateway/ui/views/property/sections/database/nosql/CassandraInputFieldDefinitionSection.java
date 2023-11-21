package bpm.gateway.ui.views.property.sections.database.nosql;

import java.util.ArrayList;
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
import org.eclipse.jface.viewers.Viewer;
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
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.nosql.IFieldsDefinition;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.server.userdefined.Variable.SQLTYPE;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;

public class CassandraInputFieldDefinitionSection extends AbstractPropertySection {

	private IFieldsDefinition model;
	
	private TableViewer viewer;
	private ComboBoxCellEditor comboEditor = new ComboBoxCellEditor();
	
	public CassandraInputFieldDefinitionSection() {
		
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		viewer = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() { }

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }	
		});
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				case 0:
					return ((StreamElement)element).name;
				case 1:
					return ((StreamElement)element).typeName;
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) { }

			public void dispose() { }

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) { }
		});
		viewer.getTable().setHeaderVisible(true);
		
		TableColumn name = new TableColumn(viewer.getTable(), SWT.NONE);
		name.setText(Messages.CassandraInputFieldDefinitionSection_0);
		name.setWidth(100);
		
		TableColumn valueField = new TableColumn(viewer.getTable(), SWT.NONE);
		valueField .setText(Messages.CassandraInputFieldDefinitionSection_1);
		valueField .setWidth(150);
		
		viewer.setColumnProperties(new String[]{"Field Name", "Field Type"}); //$NON-NLS-1$ //$NON-NLS-2$
		viewer.setCellModifier(new ColumnTypeModifier());

		viewer.setCellEditors(new CellEditor[]{
				null,
				comboEditor,
				});
	}
	
	@Override
	public void refresh() {
		fillComboEditor();
		try {
			if(model.getDescriptor(model) != null && model.getDescriptor(model).getStreamElements() != null){
				viewer.setInput(model.getDescriptor(model).getStreamElements());
			}
			else {
				viewer.setInput(new ArrayList<StreamElement>());
			}
		} catch (ServerException e) {
			e.printStackTrace();
			viewer.setInput(new ArrayList<StreamElement>());
		}
	}
	
	private void fillComboEditor(){
		
		List<String> sqlTypes = new ArrayList<String>();
		for(SQLTYPE type : Variable.SQLTYPE.values()){
			sqlTypes.add(type.getTypeName());
		}
		
		comboEditor = new ComboBoxCellEditor(viewer.getTable(), sqlTypes.toArray(new String[sqlTypes.size()]));
		viewer.setCellEditors(new CellEditor[]{ null, comboEditor });
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (IFieldsDefinition)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	public class ColumnTypeModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if (property.equals("Field Type")){ //$NON-NLS-1$
				return true;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("Field Name")){ //$NON-NLS-1$
				return ((StreamElement)element).name;
			}
			else if (property.equals("Field Type")){ //$NON-NLS-1$
				String typeName = ((StreamElement)element).typeName;
				if(typeName != null && typeName.isEmpty()){
					int i = 0;
					for(SQLTYPE type : SQLTYPE.values()){
						if(typeName.equalsIgnoreCase(type.getTypeName())){
							return i;
						}
						i++;
					}
				}
				return 0;
			}
			
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("Field Type")){ //$NON-NLS-1$
				SQLTYPE type = SQLTYPE.values()[(Integer)value];
				
				((StreamElement)((TableItem)element).getData()).typeName = type.getTypeName();
				((StreamElement)((TableItem)element).getData()).className = type.getJavaClassName();
				
				model.addColumnType(((StreamElement)((TableItem)element).getData()).name, type.getTypeName());
			}
			viewer.refresh();
		}
		
	}
}
