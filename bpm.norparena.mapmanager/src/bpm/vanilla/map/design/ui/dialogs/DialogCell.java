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
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IImage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryPicker;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;

public class DialogCell extends Dialog {

	private IBuilding building;
	private IBuildingFloor floor;
	private ICell cell;
	private IImage image;
	
	private IRepositoryApi sock;
	private IVanillaAPI vanillaApi;
	private int repositoryId;
	
	private boolean edit = false;
	
	private Text label;
	private Text positionX;
	private Text positionY;
	private Text surface;
	private Text imageUrl;
	
	private Button addImage;
	
	
	public DialogCell(Shell parentShell, IBuilding building, IBuildingFloor floor, ICell cell, 
			IRepositoryApi sock, int repositoryId, boolean bo) {
		super(parentShell);
		this.building = building;
		this.floor = floor;
		this.cell = cell;
		this.edit = bo;
		this.image = cell.getImage();
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
		l.setText(Messages.DialogCell_0);
		
		label = new Text(main, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DialogCell_1);
		
		positionX = new Text(main, SWT.BORDER);
		positionX.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));		
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.DialogCell_2);
		
		positionY = new Text(main, SWT.BORDER);
		positionY.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));	
		
		Label l4 = new Label(main, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.DialogCell_3);
		
		surface = new Text(main, SWT.BORDER);
		surface.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Composite imageComp = new Composite(main, SWT.NONE);
		imageComp.setLayout(new GridLayout(3, false));
		imageComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		Label l5 = new Label(imageComp, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.DialogCell_4);
		
		imageUrl = new Text(imageComp, SWT.BORDER);
		imageUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		imageUrl.setEditable(false);
		
		addImage = new Button(imageComp, SWT.PUSH);
		addImage.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		addImage.setText(Messages.DialogCell_5);
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
		        fd.setText(Messages.DialogCell_6);
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
									
							        cell.setImage(image);
									
								} catch (Exception ex) {
									throw ex;
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
											throw new Exception(Messages.DialogCell_16 + s);
										}
										
										sock.getAdminService().addGroupForItem(groupId, result.getId());
											
										SecuredCommentObject secComment = new SecuredCommentObject();
										secComment.setGroupId(groupId);
										secComment.setObjectId(result.getId());
										secComment.setType(Comment.ITEM);
										sock.getDocumentationService().addSecuredCommentObject(secComment);
									}catch(Exception ex2){
										ex2.printStackTrace();
										errorSecu.append(Messages.DialogCell_17 + s + " : " + ex2.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
									}
									
									
								}
				        	}
			        	}catch(Exception ex){
			        		ex.printStackTrace();
			        		MessageDialog.openError(getShell(), Messages.DialogCell_7, ex.getMessage());
			        	}
			        	
			        }
		        }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});

		if(edit){
			label.setText(cell.getLabel());
			positionX.setText(cell.getPositionX()+""); //$NON-NLS-1$
			positionY.setText(cell.getPositionY()+""); //$NON-NLS-1$
			surface.setText(cell.getSurface()+""); //$NON-NLS-1$
			
		}
		
		return main;
	}	
	
	@Override
	protected void okPressed() {
		if(!(label.getText().trim().equals(""))){ //$NON-NLS-1$
			cell.setLabel(label.getText());
			cell.setPositionX(Double.parseDouble(positionX.getText()));
			cell.setPositionY(Double.parseDouble(positionY.getText()));
			cell.setSurface(Double.parseDouble(surface.getText()));
			floor.addCell(cell);
			building.addCell(cell);
			super.okPressed();
		}
	}


}
