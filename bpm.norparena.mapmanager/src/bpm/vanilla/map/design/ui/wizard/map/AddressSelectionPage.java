package bpm.vanilla.map.design.ui.wizard.map;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IMapDefinition;

import bpm.vanilla.map.design.ui.viewers.TreeAddressContentProvider;

public class AddressSelectionPage extends WizardPage  {
		
	private TreeViewer addressTree;
	
	private IMapDefinition mapDefinition;
	private IBuilding building;
	private List<IAddress> addresses;
	
	private boolean isBuilding;
	
	protected AddressSelectionPage(String pageName, IMapDefinition mapDef, List<IAddress> addresses, boolean isBuilding) {
		super(pageName);
		this.mapDefinition = mapDef;
		this.addresses = addresses;
		this.isBuilding = isBuilding;
	}
	
	protected AddressSelectionPage(String pageName, IBuilding building, List<IAddress> addresses, boolean isBuilding) {
		super(pageName);
		this.building = building;
		this.addresses = addresses;
		this.isBuilding = isBuilding;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Label filterNameLabel = new Label(main, SWT.NONE);
		filterNameLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		filterNameLabel.setText(Messages.AddressSelectionPage_0);
		
		addressTree = new TreeViewer(main, SWT.NONE);
		addressTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		addressTree.setContentProvider(new TreeAddressContentProvider());
		addressTree.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)addressTree.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IAddress address = (IAddress)ss.getFirstElement();
				
				if(isBuilding){
					building.setAddressId(address.getId());
				}
				else{
//					address.setMapDefinition(mapDefinition);
//					address.setMapDefinitionId(mapDefinition.getId());
				}
			}
		});
		
		buildTreeAddressColumn(addressTree);

	    //We set the width of the address label column bigger than the other
		addressTree.getTree().getColumn(0).setWidth(150);
		for (int i = 1, n = addressTree.getTree().getColumnCount(); i < n; i++) {
			addressTree.getTree().getColumn(i).setWidth(100);
		}
		
		addressTree.getTree().setHeaderVisible(true);
		addressTree.getTree().setLinesVisible(true);
		
		addressTree.setInput(addresses);
		
		// page setting
		setControl(main);
		setPageComplete(false);

	}
	
	public void buildTreeAddressColumn(TreeViewer addressTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(addressTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.AddressSelectionPage_1);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(addressTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.AddressSelectionPage_2);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getAddressType();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(addressTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.AddressSelectionPage_3);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getBloc();
				return null;
			}
		});
		
		TreeViewerColumn columnArrondissement = new TreeViewerColumn(addressTree, SWT.NONE);
		columnArrondissement.getColumn().setText(Messages.AddressSelectionPage_4);
		columnArrondissement.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getArrondissement();
				return null;
			}
		});
		
		TreeViewerColumn columnStreet1 = new TreeViewerColumn(addressTree, SWT.NONE);
		columnStreet1.getColumn().setText(Messages.AddressSelectionPage_5);
		columnStreet1.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getStreet1();
				return null;
			}
		});
		
		TreeViewerColumn columnStreet2 = new TreeViewerColumn(addressTree, SWT.NONE);
		columnStreet2.getColumn().setText(Messages.AddressSelectionPage_6);
		columnStreet2.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getStreet2();
				return null;
			}
		});
		
		TreeViewerColumn columnZipCode = new TreeViewerColumn(addressTree, SWT.NONE);
		columnZipCode.getColumn().setText(Messages.AddressSelectionPage_7);
		columnZipCode.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return String.valueOf(((IAddress)element).getZipCode());
				return null;
			}
		});
		
		TreeViewerColumn columnInseeCode = new TreeViewerColumn(addressTree, SWT.NONE);
		columnInseeCode.getColumn().setText(Messages.AddressSelectionPage_8);
		columnInseeCode.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return String.valueOf(((IAddress)element).getINSEECode());
				return null;
			}
		});
		
		TreeViewerColumn columnCity = new TreeViewerColumn(addressTree, SWT.NONE);
		columnCity.getColumn().setText(Messages.AddressSelectionPage_9);
		columnCity.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getCity();
				return null;
			}
		});
		
		TreeViewerColumn columnCountry = new TreeViewerColumn(addressTree, SWT.NONE);
		columnCountry.getColumn().setText(Messages.AddressSelectionPage_10);
		columnCountry.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getCountry();
				return null;
			}
		});
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}
}
