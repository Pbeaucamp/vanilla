package bpm.vanilla.map.design.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IAddressRelation;
import bpm.vanilla.map.design.ui.viewers.TreeAddressContentProvider;

public class DialogSelectAddressParent extends Dialog {

	private List<IAddress> addresses;
	private IAddress address;
	
	private TreeViewer addressTree;
	
	private boolean edit;
	
	public DialogSelectAddressParent(Shell parentShell, List<IAddress> addresses, IAddress address,
			boolean edit) {
		super(parentShell);
		this.addresses = addresses;
		this.address = address;
		this.edit = edit;
	}
	
	
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 400);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		createTableAddress(main);
		
		return main;
	}
	
	public void createTableAddress(Composite addressContent){
		Label lblSelectParent = new Label(addressContent, SWT.NONE);
		lblSelectParent.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblSelectParent.setText(Messages.DialogSelectAddressParent_0);
		
		addressTree = new TreeViewer(addressContent, SWT.FULL_SELECTION);
		addressTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		addressTree.setContentProvider(new TreeAddressContentProvider());
		
		buildTreeAddressColumn(addressTree);

	    //We set the width of the address label column bigger than the other
		addressTree.getTree().getColumn(0).setWidth(150);
		for (int i = 1, n = addressTree.getTree().getColumnCount(); i < n; i++) {
			addressTree.getTree().getColumn(i).setWidth(100);
		}
		
		addressTree.getTree().setHeaderVisible(true);
		addressTree.getTree().setLinesVisible(true);
	    
	    addressTree.setInput(addresses);
	}
	
	public void buildTreeAddressColumn(TreeViewer addressTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(addressTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.DialogSelectAddressParent_1);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(addressTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.DialogSelectAddressParent_2);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getAddressType();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(addressTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.DialogSelectAddressParent_3);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getBloc();
				return null;
			}
		});
		
		TreeViewerColumn columnArrondissement = new TreeViewerColumn(addressTree, SWT.NONE);
		columnArrondissement.getColumn().setText(Messages.DialogSelectAddressParent_4);
		columnArrondissement.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getArrondissement();
				return null;
			}
		});
		
		TreeViewerColumn columnStreet1 = new TreeViewerColumn(addressTree, SWT.NONE);
		columnStreet1.getColumn().setText(Messages.DialogSelectAddressParent_5);
		columnStreet1.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getStreet1();
				return null;
			}
		});
		
		TreeViewerColumn columnStreet2 = new TreeViewerColumn(addressTree, SWT.NONE);
		columnStreet2.getColumn().setText(Messages.DialogSelectAddressParent_6);
		columnStreet2.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getStreet2();
				return null;
			}
		});
		
		TreeViewerColumn columnZipCode = new TreeViewerColumn(addressTree, SWT.NONE);
		columnZipCode.getColumn().setText(Messages.DialogSelectAddressParent_7);
		columnZipCode.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return String.valueOf(((IAddress)element).getZipCode());
				return null;
			}
		});
		
		TreeViewerColumn columnInseeCode = new TreeViewerColumn(addressTree, SWT.NONE);
		columnInseeCode.getColumn().setText(Messages.DialogSelectAddressParent_8);
		columnInseeCode.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return String.valueOf(((IAddress)element).getINSEECode());
				return null;
			}
		});
		
		TreeViewerColumn columnCity = new TreeViewerColumn(addressTree, SWT.NONE);
		columnCity.getColumn().setText(Messages.DialogSelectAddressParent_9);
		columnCity.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getCity();
				return null;
			}
		});
		
		TreeViewerColumn columnCountry = new TreeViewerColumn(addressTree, SWT.NONE);
		columnCountry.getColumn().setText(Messages.DialogSelectAddressParent_10);
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
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection)addressTree.getSelection();
		
		if(edit && !ss.isEmpty()){
			IAddress addressParent = (IAddress)ss.getFirstElement();
			if(address.getAddressRelation() == null){
				//We create a new relation between two address and we set it to the child
				IAddressRelation relation = null;
				try {
					relation = Activator.getDefault().getFactoryMap().createAddressRelation();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				relation.setParentId(addressParent.getId());
				relation.setChildId(address.getId());
				address.setAddressRelation(relation);
			}
			else{
				address.getAddressRelation().setParentId(addressParent.getId());
			}
			super.okPressed(); 
		}
		else if (!ss.isEmpty()){
			IAddress addressParent = (IAddress)ss.getFirstElement();
			
			//We create a new relation between two address and we set it to the child
			IAddressRelation relation = null;
			try {
				relation = Activator.getDefault().getFactoryMap().createAddressRelation();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			relation.setParentId(addressParent.getId());
			address.setAddressRelation(relation);
			
			super.okPressed(); 
		}
	}
}
