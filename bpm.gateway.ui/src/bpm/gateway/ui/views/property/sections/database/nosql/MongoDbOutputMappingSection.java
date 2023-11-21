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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.database.nosql.IOutputNoSQL;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.gef.part.NodePart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.views.property.sections.database.nosql.NosqlOutputMappingSection.ColumnTypeModifier;

public class MongoDbOutputMappingSection extends AbstractPropertySection{

	public IOutputNoSQL model;
	private CCombo inputCombo;
	private TableViewer viewer;
	private Transformation selectedInput;

	private ComboBoxCellEditor comboEditor = new ComboBoxCellEditor();
	
	private List<Transformation> inputs = new ArrayList<Transformation>();
	
	public MongoDbOutputMappingSection(){}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.model = (IOutputNoSQL)((Node)((NodePart) input).getModel()).getGatewayModel();
	}
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		parent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout());
		
		/*
		 * for choosing the input transformation
		 */
		Composite inputChoice = getWidgetFactory().createComposite(composite);
		inputChoice.setLayout(new GridLayout(2, false));
		inputChoice.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = getWidgetFactory().createLabel(inputChoice, Messages.MongoDbOutputMappingSection_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
	
		inputCombo = getWidgetFactory().createCCombo(inputChoice, SWT.READ_ONLY);
		inputCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inputCombo.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Transformation t : inputs){
					if (t.getName().equals(inputCombo.getText())){
						try {
							selectedInput = t;
							viewer.setInput(t.getDescriptor(t).getStreamElements());
						} catch (ServerException e1) {
							e1.printStackTrace();
						}
						break;
					}
				}
			}
		});
		
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
					if(selectedInput != null && model != null && model.getMappingsFor(selectedInput) != null
							&& model.getMappingsFor(selectedInput).get(((StreamElement)element).name) != null){
						return model.getMappingsFor(selectedInput).get(((StreamElement)element).name);
					}
					else {
						return ""; //$NON-NLS-1$
					}
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
		name.setText(Messages.MongoDbOutputMappingSection_2);
		name.setWidth(100);
		
		TableColumn valueField = new TableColumn(viewer.getTable(), SWT.NONE);
		valueField .setText(Messages.MongoDbOutputMappingSection_3);
		valueField .setWidth(150);
		
		viewer.setColumnProperties(new String[]{"Field Input Name", "Field Output Name"}); //$NON-NLS-1$ //$NON-NLS-2$
		viewer.setCellModifier(new ColumnTypeModifier());

		viewer.setCellEditors(new CellEditor[]{
				null,
				comboEditor,
				});
	}
	
	public class ColumnTypeModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if (property.equals("Field Output Name")){ //$NON-NLS-1$
				return true;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if (property.equals("Field Output Name")){ //$NON-NLS-1$
				String elName = null;
				try {
					elName = model.getMappingsFor(selectedInput).get(((StreamElement)element).name);
				
					if(elName != null && !elName.isEmpty()){
						int i = 0;
						for(StreamElement el : model.getDescriptor(model).getStreamElements()){
							if(elName.equals(el.name)){
								return i;
							}
							i++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
			
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals("Field Output Name")){ //$NON-NLS-1$
				if((Integer)value == -1){
					String stringValue = ((CCombo)comboEditor.getControl()).getText();
					if(stringValue != null && !stringValue.isEmpty()){
						model.createMapping(selectedInput, ((TableItem)element).getText(), stringValue);
					}
					else {
						model.deleteMapping(selectedInput, ((TableItem)element).getText());
					}
				}
				else {
					try {
						StreamElement el = model.getDescriptor(model).getStreamElements().get((Integer)value);
						model.createMapping(selectedInput, ((TableItem)element).getText(), el.name);
					} catch (ServerException e) {
						e.printStackTrace();
						model.deleteMapping(selectedInput, ((TableItem)element).getText());
					}
				}
			}
			viewer.refresh();
		}
		
	}
}
