package bpm.gateway.ui.composites.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.service.wizard.DialogServiceCalculation;
import bpm.gateway.ui.resource.service.wizard.ServiceInputPage;
import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.core.beans.service.IService;
import bpm.vanilla.platform.core.beans.service.ServiceInputData;
import bpm.vanilla.platform.core.beans.service.ServiceOutputData;

public class ServiceViewerComposite extends Composite {
	
	public enum TypeViewer {
		TYPE_VANILLA_INPUT, TYPE_VANILLA_OUTPUT, TYPE_WEB_SERVICE_INPUT
	}
	
	private ServiceInputPage inputPage;
	private TableViewer viewer;
	private TypeViewer typeViewer;
	private List<IService> data = new ArrayList<IService>();
	
	public ServiceViewerComposite(Composite parent, int style, TypeViewer typeViewer, ServiceInputPage inputPage) {
		super(parent, style);
		this.typeViewer = typeViewer;
		this.inputPage = inputPage;
		setLayout(new GridLayout(1, true));
		if(typeViewer == TypeViewer.TYPE_VANILLA_INPUT || typeViewer == TypeViewer.TYPE_VANILLA_OUTPUT){
			buildToolbar();
		}
		buildContent();
		
		setInput(data);
	}
	
	private void buildToolbar() {
		ToolBar bar = new ToolBar(this, SWT.FLAT);
		bar.setLayoutData(new GridData());
		
		ToolItem it = new ToolItem(bar, SWT.PUSH);
		it.setToolTipText(Messages.ServiceViewerComposite_0);
		it.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		it.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IService newService = createServiceEntity();
				data.add(newService);
				viewer.refresh();
			}
		});
		
		final ToolItem delt = new ToolItem(bar, SWT.PUSH);
		delt.setToolTipText(Messages.ServiceViewerComposite_1);
		delt.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		delt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)viewer.getSelection()).toList()){
					data.remove(o);
				}
				viewer.refresh();
			}
		});
		
		if(typeViewer == TypeViewer.TYPE_VANILLA_OUTPUT){
			final ToolItem edit = new ToolItem(bar, SWT.PUSH);
			edit.setToolTipText(Messages.ServiceViewerComposite_2);
			edit.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));
			edit.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Object selection = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
					if(selection != null && selection instanceof IService){
						IService selectedOutput = (IService)selection;
						List<IService> inputs = inputPage.getIServices();
						DialogServiceCalculation dial = new DialogServiceCalculation(getShell(), inputs, selectedOutput);
						if(dial.open() == Dialog.OK){
							viewer.refresh();
						}
					}
				}
			});
		}
	}

	private void buildContent(){
		viewer = new TableViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));

		TableViewerColumn name = new TableViewerColumn(viewer, SWT.NONE);
		name.getColumn().setText(Messages.ServiceViewerComposite_3);
		name.getColumn().setWidth(200);
		name.setEditingSupport(new SupplierCellModifier(viewer, SupplierCellModifier.NAME, typeViewer));

		TableViewerColumn typeField = new TableViewerColumn(viewer, SWT.NONE);
		typeField.getColumn().setText(Messages.ServiceViewerComposite_4);
		typeField.getColumn().setWidth(150);
		typeField.setEditingSupport(new SupplierCellModifier(viewer, SupplierCellModifier.TYPE, typeViewer));

		if(typeViewer != TypeViewer.TYPE_VANILLA_INPUT){
			TableViewerColumn valueField = new TableViewerColumn(viewer, SWT.NONE);
			valueField.getColumn().setText(Messages.ServiceViewerComposite_5);
			valueField.getColumn().setWidth(150);
			valueField.setEditingSupport(new SupplierCellModifier(viewer, SupplierCellModifier.VALUE, typeViewer));
		}
		
		viewer.setContentProvider(new IStructuredContentProvider() {

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List l = (List) inputElement;
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
		viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
				case SupplierCellModifier.NAME:
					return ((IService) element).getName();
				case SupplierCellModifier.TYPE:
					return Variable.TYPE_NAMES[((IService) element).getType()];
				case SupplierCellModifier.VALUE:
					return ((IService) element).getValue();
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
		viewer.getTable().setHeaderVisible(true);
	}

	private IService createServiceEntity() {
		if(typeViewer == TypeViewer.TYPE_VANILLA_INPUT){
			ServiceInputData newService = new ServiceInputData();
			newService.setName("newColumn"); //$NON-NLS-1$
			newService.setType(0);
			return newService;
		}
		else if(typeViewer == TypeViewer.TYPE_VANILLA_OUTPUT){
			ServiceOutputData newService = new ServiceOutputData();
			newService.setName("newColumn"); //$NON-NLS-1$
			newService.setType(0);
			return newService;
		}
		return null;
	}
	
	public void setInput(List<IService> input){
		this.data = input;
		this.viewer.setInput(input);
	}

	public List<IService> getInput() {
		return data;
	}
}
