package bpm.fd.repository.ui.composites;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.repository.ui.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class CompositeItem extends Composite {

	private RepositoryItem item;
	
	private Text name, comment, type, internalVersion, publicVersion;
	private Text modifier, modification, creation;
	
	public CompositeItem(Composite parent, int style, RepositoryItem item) {
		super(parent, style);
		this.item = item;
		
		
		this.setLayout(new GridLayout(2, false));
		
		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(Messages.CompositeItem_0);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				CompositeItem.this.notifyListeners(SWT.Modify, new Event());
				
			}
		});
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.CompositeItem_1);
		
		type = new Text(this, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setEnabled(false);
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		l5.setText(Messages.CompositeItem_2);
		
		comment = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));

		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText(Messages.CompositeItem_4);
		
		internalVersion = new Text(this, SWT.BORDER);
		internalVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l8 = new Label(this, SWT.NONE);
		l8.setLayoutData(new GridData());
		l8.setText(Messages.CompositeItem_5);
		
		publicVersion = new Text(this, SWT.BORDER);
		publicVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
//		Label l10 = new Label(this, SWT.NONE);
//		l10.setLayoutData(new GridData());
//		l10.setText("Created By");
//		
//		creator = new Text(this, SWT.BORDER);
//		creator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		
		

		Label l11 = new Label(this, SWT.NONE);
		l11.setLayoutData(new GridData());
		l11.setText(Messages.CompositeItem_6);
		
		creation = new Text(this, SWT.BORDER);
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creation.setEnabled(false);
		
		
		Label l12 = new Label(this, SWT.NONE);
		l12.setLayoutData(new GridData());
		l12.setText(Messages.CompositeItem_7);
		
		modifier = new Text(this, SWT.BORDER);
		modifier.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Label l13 = new Label(this, SWT.NONE);
		l13.setLayoutData(new GridData());
		l13.setText(Messages.CompositeItem_8);
		
		modification = new Text(this, SWT.BORDER);
		modification.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		modification.setEnabled(false);

	}
	
	public void fillData(){
		name.setText(item.getItemName());
		comment.setText(item.getComment());
		type.setText(IRepositoryApi.TYPES_NAMES[item.getType()]);
		internalVersion.setText(item.getInternalVersion());
		publicVersion.setText(item.getPublicVersion());
		//creator.setText(item.getCreator());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
		creation.setText(sdf.format(item.getDateCreation()));
		if (item.getDateModification() != null)
			modification.setText(sdf.format(item.getDateModification()));
		
		modifier.setText(item.getModifiedBy() + "");
		
		if (item.getOwnerId() == null){ //$NON-NLS-1$
//			creator.setEnabled(true);
			modifier.setEnabled(false);
		}
		else{
			//creator.setEnabled(false);
			modifier.setEnabled(true);
		}
			
	}
	
	public RepositoryItem setObjects(){
		if (item == null){
			item = new RepositoryItem();
		}
		item.setItemName(name.getText());
		item.setComment(comment.getText());
		item.setDateModification(Calendar.getInstance().getTime());		
		item.setInternalVersion(internalVersion.getText());
		item.setPublicVersion(publicVersion.getText());
		return item;
	}

	public boolean isFilled() {
		return !name.getText().isEmpty();
	}

}
