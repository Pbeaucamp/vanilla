package bpm.vanilla.map.design.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IAddress;


public class DialogAddress extends Dialog {

	private List<IAddress> addresses;
	private IAddress address;
	
	private boolean edit;
	
	private Text label;
	private Combo cbAddressType;
	private Text street1;
	private Text street2;
	private Text bloc;
	private Text arrondissement;
	private Text zipCode;
	private Text inseeCode;
	private Text city;
	private Text country;
	private Button btnSelectParent;
	
	public DialogAddress(Shell parentShell, List<IAddress> addresses, 
			IAddress address, boolean bo) {
		super(parentShell);
		this.address = address;
		this.addresses = addresses;
		this.edit = bo;
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
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogAddress_0);
		
		label = new Text(main, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label lblCbAddressType = new Label(main, SWT.NONE);
		lblCbAddressType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblCbAddressType.setText(Messages.DialogAddress_1);
		
		cbAddressType = new Combo(main, SWT.PUSH);
		cbAddressType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cbAddressType.setItems(IAddress.ADDRESS_TYPES);
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogAddress_2);
		
		street1 = new Text(main, SWT.BORDER);
		street1.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		street1.addModifyListener(listener);
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.DialogAddress_3);
		
		street2 = new Text(main, SWT.BORDER);
		street2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		street1.addModifyListener(listener);
		
		Label l10 = new Label(main, SWT.NONE);
		l10.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l10.setText(Messages.DialogAddress_4);
		
		bloc = new Text(main, SWT.BORDER);
		bloc.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		street1.addModifyListener(listener);
		
		Label l11 = new Label(main, SWT.NONE);
		l11.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l11.setText(Messages.DialogAddress_5);
		
		arrondissement = new Text(main, SWT.BORDER);
		arrondissement.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		street1.addModifyListener(listener);
		
		Label l4 = new Label(main, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.DialogAddress_6);
		
		zipCode = new Text(main, SWT.BORDER);
		zipCode.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		zipCode.setText("0"); //$NON-NLS-1$
//		street1.addModifyListener(listener);
		
		Label l5 = new Label(main, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.DialogAddress_8);
		
		inseeCode = new Text(main, SWT.BORDER);
		inseeCode.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		inseeCode.setText("0"); //$NON-NLS-1$
//		street1.addModifyListener(listener);
		
		Label l6 = new Label(main, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.DialogAddress_10);
		
		city = new Text(main, SWT.BORDER);
		city.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		street1.addModifyListener(listener);
		
		Label l7 = new Label(main, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.DialogAddress_11);
		
		country = new Text(main, SWT.BORDER);
		country.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		street1.addModifyListener(listener);
		
		btnSelectParent = new Button(main, SWT.PUSH);
		btnSelectParent.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		btnSelectParent.setText(Messages.DialogAddress_12);
		btnSelectParent.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogSelectAddressParent dial = new DialogSelectAddressParent(getShell(), addresses, 
						address, edit);
				if(dial.open() == Dialog.OK){
					
				}
			}
		});

		if(edit){
			label.setText(address.getLabel());
			cbAddressType.setText(address.getAddressType());
			street1.setText(address.getStreet1());
			street2.setText(address.getStreet2());
			bloc.setText(address.getBloc());
			arrondissement.setText(address.getArrondissement());
			zipCode.setText(address.getZipCode()+""); //$NON-NLS-1$
			inseeCode.setText(address.getINSEECode()+""); //$NON-NLS-1$
			city.setText(address.getCity());
			country.setText(address.getCountry());
		}
		
		return main;
	}	
	
	@Override
	protected void okPressed() {
		if(!(label.getText().trim().equals(Messages.DialogAddress_15))){
			address.setLabel(label.getText());
			address.setAddressType(cbAddressType.getText());
			address.setStreet1(street1.getText());
			address.setStreet2(street2.getText());
			address.setBloc(bloc.getText());
			address.setArrondissement(arrondissement.getText());
			address.setZipCode(Integer.parseInt(zipCode.getText()));
			address.setINSEECode(Integer.parseInt(inseeCode.getText()));
			address.setCity(city.getText());
			address.setCountry(country.getText());
			super.okPressed();
		}
	}


}
