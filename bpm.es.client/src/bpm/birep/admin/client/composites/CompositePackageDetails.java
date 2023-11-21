package bpm.birep.admin.client.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import adminbirep.Messages;
import bpm.vanilla.workplace.core.model.ExportDetails;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class CompositePackageDetails extends Composite{
	
	private Button includeReports;
	private Button includeGroups;
	private Button includeRoles;
	private Button includeGrants;
	
	public CompositePackageDetails(Composite parent, int style){
		super(parent, style);
		buildContent();
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout(2, true));
		
		Group generalComposite = new Group(this, SWT.NONE);
		generalComposite.setLayout(new GridLayout());
		generalComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		generalComposite.setText(Messages.CompositePackageDetails_0);
		
		includeReports = new Button (generalComposite, SWT.CHECK);
		includeReports.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeReports.setText(Messages.CompositePackageDetails_1);
		includeReports.setEnabled(false);
		
		Group securityComposite = new Group(this, SWT.NONE);
		securityComposite.setLayout(new GridLayout());
		securityComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		securityComposite.setText(Messages.CompositePackageDetails_13);
		
		includeGroups = new Button (securityComposite, SWT.CHECK);
		includeGroups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeGroups.setText(Messages.CompositePackageDetails_14);
		includeGroups.setEnabled(false);

		
		includeRoles = new Button (securityComposite, SWT.CHECK);
		includeRoles.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeRoles.setText(Messages.CompositePackageDetails_15);
		includeRoles.setEnabled(false);
		
		
		includeGrants = new Button (securityComposite, SWT.CHECK);
		includeGrants.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		includeGrants.setText(Messages.CompositePackageDetails_16);
		includeGrants.setEnabled(false);
	}
	
	public void fillDatas(ExportDetails details){
		includeGrants.setSelection(details.isIncludeGrants());
		includeGroups.setSelection(details.isIncludeGroups());
		includeReports.setSelection(details.isIncludeReports());
		includeRoles.setSelection(details.isIncludeRoles());
	}
	
	public void fillDatas(VanillaPackage vanillaPackage){
		includeGrants.setSelection(vanillaPackage.isIncludeGrants());
		includeGroups.setSelection(vanillaPackage.isIncludeGroups());
		includeReports.setSelection(vanillaPackage.isIncludeHistorics());
		includeRoles.setSelection(vanillaPackage.isIncludeRoles());
	}

}
