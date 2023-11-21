package bpm.fd.design.ui.css.wizard;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class NewCssPage extends WizardPage{
	
	private Text resourceName;
	private ComboViewer bacgroundPictureResource;
	private Button useBackgroundImage;
	
	private Button browseFile;
	private Button fromFileSystem;
	private Button fromResources;
	
	private Text filePath;
	private Canvas preview;
	private Image imageData;
	
	
	public NewCssPage(String pageName) {
		super(pageName);
		
		
		setDescription(Messages.NewCssPage_0); //NON-NLS-1
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.NewCssPage_1);
		
		resourceName = new Text(main, SWT.BORDER);
		resourceName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		resourceName.setText("cssResource.css"); //$NON-NLS-1$
		resourceName.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
		
	
		useBackgroundImage = new Button(main, SWT.CHECK);
		useBackgroundImage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		useBackgroundImage.setText(Messages.NewCssPage_3);
		useBackgroundImage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				bacgroundPictureResource.getControl().setEnabled(useBackgroundImage.getSelection());
				bacgroundPictureResource.setInput(Activator.getDefault().getProject().getResources(FileImage.class));
				updateButtons();
				getContainer().updateButtons();
			}
		});
		
		
		Group group = new Group(main, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setText(Messages.NewCssPage_4);
		group.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		
		fromResources = new Button(group, SWT.RADIO);
		fromResources.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 3, 1));
		fromResources.setText(Messages.NewCssPage_5);
		fromResources.setSelection(true);
		fromResources.setEnabled(false);
		fromResources.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bacgroundPictureResource.getControl().setEnabled(fromResources.getSelection());
				browseFile.setEnabled(!fromResources.getSelection());
				getContainer().updateButtons();
			}
		});
		
		fromFileSystem = new Button(group, SWT.RADIO);
		fromFileSystem.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 3, 1));
		fromFileSystem.setText(Messages.NewCssPage_6);
		fromFileSystem.setEnabled(false);
		fromFileSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bacgroundPictureResource.getControl().setEnabled(!fromFileSystem.getSelection());
				browseFile.setEnabled(fromFileSystem.getSelection());
				getContainer().updateButtons();
			}
		});
		
		
		l = new Label(group, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.NewCssPage_7);
		
		bacgroundPictureResource = new ComboViewer(group, SWT.READ_ONLY);
		bacgroundPictureResource.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		bacgroundPictureResource.getControl().setEnabled(false);
		bacgroundPictureResource.setContentProvider(new ListContentProvider<IResource>());
		bacgroundPictureResource.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((IResource)element).getName();
			}
			
		});
		bacgroundPictureResource.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
				IStructuredSelection ss = (IStructuredSelection)bacgroundPictureResource.getSelection();
				if (!ss.isEmpty()){
					try {
						NewCssPage.this.imageData = ImageDescriptor.createFromURL(new URL("file://" + ((IResource)ss.getFirstElement()).getFile().getAbsolutePath())).createImage(); //$NON-NLS-1$
						
						preview.redraw();
					} catch (MalformedURLException e1) {
						
						e1.printStackTrace();
					}
				}
				
				
				getContainer().updateButtons();
				
			}
		});
		
		l = new Label(group, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.NewCssPage_9);
		
		filePath = new Text(group, SWT.BORDER);
		filePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		filePath.setEnabled(false);
		
		browseFile = new Button(group, SWT.PUSH);
		browseFile.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		browseFile.setText("..."); //$NON-NLS-1$
		browseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(getShell(), SWT.OPEN);
				dial.setFilterExtensions(FileImage.extensions);
				String fName = dial.open();
				if (fName == null){
					return;
				}
				
				filePath.setText(fName);
				;
				try {
					NewCssPage.this.imageData = ImageDescriptor.createFromURL(new URL("file://" + fName)).createImage(); //$NON-NLS-1$
					
					preview.redraw();
				} catch (MalformedURLException e1) {
					
					e1.printStackTrace();
				}
				getContainer().updateButtons();
			}
		});
		
		
		preview = new Canvas(group, SWT.BORDER);
		preview.addPaintListener(new PaintListener() {
			
			public void paintControl(PaintEvent e) {
				if (NewCssPage.this.imageData != null){
					e.gc.drawImage(imageData, 0, 0);
				}
				
			}
		});
		preview.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		
		setControl(main);
		
	}

	@Override
	public boolean isPageComplete() {
		
		
		
		FdProject project = Activator.getDefault().getProject();
		for(IResource r : project.getResources()){
			
			if (r.getName().equals(resourceName.getText())){
				setErrorMessage(Messages.NewCssPage_12 + resourceName.getText() + Messages.NewCssPage_13);
				return false;
			}
			
			
		}
		if (useBackgroundImage.getSelection() && getImageResource() == null){
			setErrorMessage(Messages.NewCssPage_14);
			
			return false;
		}
		setErrorMessage(null);
		return true;
	}
	
	protected String getCssResourceName(){
		return resourceName.getText();
	}
	
	protected IResource getImageResource(){
		if (!useBackgroundImage.getSelection()){
			return null;
		}
		if (bacgroundPictureResource.getSelection().isEmpty()){
			if (!filePath.getText().equals("")){ //$NON-NLS-1$
				File f = new File(filePath.getText());
				
				FileImage r = new FileImage(f.getName(), f);
				return r;
			}
			return null;
		}
		return (IResource)((IStructuredSelection)bacgroundPictureResource.getSelection()).getFirstElement();
	}
	
	
	private void updateButtons(){
		if (useBackgroundImage == null){
			return;
		}
		if (useBackgroundImage.getSelection()){
			fromFileSystem.setEnabled(true);
			fromResources.setEnabled(true);
			
			if (fromFileSystem.getSelection()){
				browseFile.setEnabled(true);
				bacgroundPictureResource.getControl().setEnabled(false);
			}
			else{
				bacgroundPictureResource.getControl().setEnabled(true);
				browseFile.setEnabled(false);
			}
		}
		else{
			fromFileSystem.setEnabled(false);
			fromResources.setEnabled(false);
		}
	}
}
