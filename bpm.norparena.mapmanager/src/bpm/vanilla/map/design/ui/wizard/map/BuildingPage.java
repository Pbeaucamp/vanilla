package bpm.vanilla.map.design.ui.wizard.map;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IImage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.repository.ui.dialogs.DialogDirectoryPicker;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;

public class BuildingPage extends WizardPage  {
	
	private boolean edit;
	
	private Text label;
	private Combo type;
	private Text latitude;
	private Text longitude;
	private Text surface;
	private Text sizeX;
	private Text sizeY;
	private Text imageUrl;
	
	private Button addImage;
	
	private IBuilding building;
	private IImage image;
	
	private IRepositoryApi sock;
	private IVanillaAPI vanillaApi;
	private int repositoryId;
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
			if (e.getSource().equals(label)) {
				building.setLabel(label.getText());
			}			
		}
		
	};
	
	protected BuildingPage(String pageName){
		super(pageName);
	}
	
	protected BuildingPage(String pageName, IBuilding building, IRepositoryApi sock, int repositoryId, boolean bo) {
		super(pageName);
		this.building = building;
		this.edit = bo;
		this.image = building.getImage();
		this.sock = sock;
		this.repositoryId = repositoryId;
		this.vanillaApi = Activator.getDefault().getVanillaApi();
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Label l0 = new Label(main, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.BuildingPage_0);
		
		label = new Text(main, SWT.BORDER);
		label.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l1 = new Label(main, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.BuildingPage_1);
		
		type = new Combo(main, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(IBuilding.BUILDING_TYPES);
		type.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				building.setType(type.getText());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.BuildingPage_2);
		
		latitude = new Text(main, SWT.BORDER);
		latitude.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		latitude.setText(String.valueOf(building.getLatitude()));
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.BuildingPage_3);
		
		longitude = new Text(main, SWT.BORDER);
		longitude.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		longitude.setText(String.valueOf(building.getLongitude()));
		
		Label l4 = new Label(main, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.BuildingPage_4);
		
		sizeX = new Text(main, SWT.BORDER);
		sizeX.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sizeX.setText(String.valueOf(building.getSizeX()));
		
		Label l5 = new Label(main, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.BuildingPage_5);
		
		sizeY = new Text(main, SWT.BORDER);
		sizeY.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		sizeY.setText(String.valueOf(building.getSizeY()));
		
		Label l6 = new Label(main, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.BuildingPage_6);
		
		surface = new Text(main, SWT.BORDER);
		surface.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		surface.setText(String.valueOf(building.getSurface()));
		
		Composite imageComp = new Composite(main, SWT.NONE);
		imageComp.setLayout(new GridLayout(3, false));
		imageComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		Label l7 = new Label(imageComp, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.BuildingPage_7);
		
		imageUrl = new Text(imageComp, SWT.BORDER);
		imageUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		imageUrl.setEditable(false);
		
		addImage = new Button(imageComp, SWT.PUSH);
		addImage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addImage.setText(Messages.BuildingPage_8);
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
		        fd.setText(Messages.BuildingPage_9);
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
							        
							        building.setImage(image);
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
											throw new Exception(Messages.BuildingPage_19 + s);
										}
										
										sock.getAdminService().addGroupForItem(groupId, result.getId());
											
										SecuredCommentObject secComment = new SecuredCommentObject();
										secComment.setGroupId(groupId);
										secComment.setObjectId(result.getId());
										secComment.setType(Comment.ITEM);
										sock.getDocumentationService().addSecuredCommentObject(secComment);
									}catch(Exception ex2){
										ex2.printStackTrace();
										errorSecu.append(Messages.BuildingPage_20 + s + " : " + ex2.getMessage() + "\n"); //$NON-NLS-2$ //$NON-NLS-3$
									}
									
									
								}
				        	}
			        	}catch(Exception ex){
			        		ex.printStackTrace();
			        		MessageDialog.openError(getShell(), Messages.BuildingPage_23, ex.getMessage());
			        	}
			        	
			        	
			        }
		        }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		if(edit){
			label.setText(building.getLabel());
			latitude.setText(building.getLatitude()+""); //$NON-NLS-1$
			longitude.setText(building.getLongitude()+""); //$NON-NLS-1$
			sizeX.setText(building.getSizeX()+""); //$NON-NLS-1$
			sizeY.setText(building.getSizeY()+""); //$NON-NLS-1$
			surface.setText(building.getSurface()+""); //$NON-NLS-1$
		}
		label.addModifyListener(listener);
		
		// page setting
		setControl(main);
		setPageComplete(false);
	}
	
	protected void setBuildingInformations() {
		building.setLabel(label.getText());
		building.setLatitude(Double.parseDouble(latitude.getText()));
		building.setLongitude(Double.parseDouble(longitude.getText()));
		building.setSurface(Double.parseDouble(surface.getText()));
		building.setSizeX(Double.parseDouble(sizeX.getText()));
		building.setSizeY(Double.parseDouble(sizeY.getText()));
	}

	@Override
	public boolean isPageComplete() {
		return !(label.getText().trim().equals("")); //$NON-NLS-1$
	}
}
