package bpm.vanilla.map.design.ui.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IBuildingFloor;
import bpm.vanilla.map.core.design.IImage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryPicker;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;

public class DialogFloor extends Dialog {

	private IBuilding building;
	private IBuildingFloor floor;
	private IImage image;
	
	private IRepositoryApi sock;
	private IVanillaAPI vanillaApi;
	private int repositoryId;
	
	private boolean edit;
	
	private Text label;
	private Text level;
	private Text imageUrl;

	private Button addImage;
	
	public DialogFloor(Shell parentShell, IBuilding building, IBuildingFloor floor, 
			IRepositoryApi sock, int repositoryId, boolean bo) {
		super(parentShell);
		this.building = building;
		this.floor = floor;
		this.edit = bo;
		this.image = floor.getImage();
		this.sock = sock;
		this.repositoryId = repositoryId;
		this.vanillaApi = Activator.getDefault().getVanillaApi();
	}
	
	
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 400);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.DialogFloor_0);
		
		label = new Text(main, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));		
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogFloor_1);
		
		level = new Text(main, SWT.BORDER);
		level.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Composite imageComp = new Composite(main, SWT.NONE);
		imageComp.setLayout(new GridLayout(3, false));
		imageComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		Label l3 = new Label(imageComp, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.DialogFloor_2);
		
		imageUrl = new Text(imageComp, SWT.BORDER);
		imageUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		imageUrl.setEditable(false);	
		
		addImage = new Button(imageComp, SWT.PUSH);
		addImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addImage.setText(Messages.DialogFloor_3);
		addImage.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(image == null){
			        try {
						image = Activator.getDefault().getFactoryMap().createImage();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
		        fd.setText(Messages.DialogFloor_4);
		        fd.setFilterPath("C:/"); //$NON-NLS-1$
//		        String[] filterExt = { "*.jpg", "*.png", ".gif", "*.*" };
//		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        
		        if(selected != null && !selected.isEmpty()){
			        RepositoryDirectory dir = null;
			        DialogDirectoryPicker dialDirPicker = new DialogDirectoryPicker(getShell(), sock);
			        if(dialDirPicker.open() == Dialog.OK){
			        	dir = dialDirPicker.getDirectory();
			        	
			        	
			        	try{
			        		DialogNewItem dialItem = new DialogNewItem(getShell(), vanillaApi.getVanillaSecurityManager().getGroups());
				        	if(dialItem.open() == Dialog.OK){
				        		
				        		Properties props = dialItem.getProperties();
				        		String[] groupNames = props.getProperty(DialogNewItem.P_GROUP).split(";"); //$NON-NLS-1$
				        		
				        		imageUrl.setText(selected);
								RepositoryItem result = null;
								try {
									File file = new File(selected);
									result = sock.getRepositoryService().addExternalDocumentWithDisplay(dir, 
											props.getProperty("name"),  //$NON-NLS-1$
											props.getProperty("desc"), //$NON-NLS-1$
											props.getProperty("version"), //$NON-NLS-1$
											"", //$NON-NLS-1$
											new FileInputStream(file),true, 
											props.getProperty("author")); //$NON-NLS-1$
		
									image.setImageItemId(result.getId());
									image.setImageRepositoryId(repositoryId);

							        floor.setImage(image);
								} catch (Exception ex) {
									throw new Exception(Messages.DialogFloor_14+ex.getMessage(), ex);
								}
								
								/*
								 * create access and run for all groups
								 */
								
								StringBuilder errorSecu = new StringBuilder();
								
								for(String s : groupNames){
									
									try{
										Integer groupId = null;
										
										try{
											groupId = vanillaApi.getVanillaSecurityManager().getGroupByName(s).getId();
										}catch(Exception ex){
											throw new Exception(Messages.DialogFloor_15 + s);
										}
										
										sock.getAdminService().addGroupForItem(groupId, result.getId());
											
										SecuredCommentObject secComment = new SecuredCommentObject();
										secComment.setGroupId(groupId);
										secComment.setObjectId(result.getId());
										secComment.setType(Comment.ITEM);
										sock.getDocumentationService().addSecuredCommentObject(secComment);
									}catch(Exception ex2){
										ex2.printStackTrace();
										errorSecu.append(Messages.DialogFloor_16 + s + " : " + ex2.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
									}
								}
				        	}
			        	}catch(Exception ex){
			        		ex.printStackTrace();
			        		MessageDialog.openError(getShell(), Messages.DialogFloor_5, ex.getMessage());
			        	}
			        	
			        	
			        }
		        }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		if(edit){
			label.setText(floor.getLabel());
			level.setText(floor.getLevel()+""); //$NON-NLS-1$
		}

		return main;
	}	
	
	@Override
	protected void okPressed() {
		if(!(label.getText().trim().equals(""))){ //$NON-NLS-1$
			floor.setLabel(label.getText());
			floor.setLevel(Integer.parseInt(level.getText()));
			building.addFloor(floor);
			int numberOfFloor = building.getNbFloors();
			building.setNbFloors(numberOfFloor+1);
			super.okPressed();
		}
	}


}
