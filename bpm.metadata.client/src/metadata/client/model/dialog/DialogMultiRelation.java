package metadata.client.model.dialog;

import metadata.client.i18n.Messages;
import metadata.client.model.composites.CompositeMultiRelation;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.MultiDSRelation;
import bpm.metadata.layer.logical.RelationException;
import bpm.metadata.layer.logical.sql.SQLRelation;

public class DialogMultiRelation extends Dialog {

	private CompositeMultiRelation composite;
	private MultiDSRelation relation;
	
	
	public DialogMultiRelation(Shell parentShell, MultiDSRelation relation) {
		super(parentShell);
		this.relation = relation;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (relation == null){
			relation = new MultiDSRelation();
		}
		
		composite = new CompositeMultiRelation(parent, SWT.NONE, true, relation);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		return parent;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogMultiRelation_0); //$NON-NLS-1$
		getShell().setSize(800, 600);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);
	}



	@Override
	protected void okPressed() {
		if (!relation.getJoins().isEmpty()){
			
			if (relation.getLeftDataSource() == relation.getRightDataSource()){
				SQLRelation sqlRelation = new SQLRelation();
				sqlRelation.setDataStreams(relation.getLeftDataSource());
				
				for(Join j : relation.getJoins()){
					try {
						sqlRelation.add(j.getLeftElement(), j.getRightElement(), j.getOuter());
						relation.getLeftDataSource().addRelation(sqlRelation);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (RelationException e) {
						e.printStackTrace();
					}
				}
				
			}
			else{
				Activator.getDefault().getModel().addMultiRelation(relation);
			}
			
		}
		super.okPressed();
	}
	
	

}
