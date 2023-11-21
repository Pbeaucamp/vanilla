package bpm.fd.design.ui.editor.style;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.resources.FileImage;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.tools.ColorManager;

public class DialogBackground extends Dialog{
	
	private ComboViewer resources;
	private Combo repeat;
	private Text color;
	
	private CssClass css;
	private Canvas picture;
	public DialogBackground(Shell parentShell, CssClass css) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.css= css;
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Background Image");
		
		resources = new ComboViewer(main, SWT.READ_ONLY);
		resources.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		resources.setContentProvider(new ArrayContentProvider());
		
		resources.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof String){
					return (String)element;
				}
				return ((IResource)element).getName();
			}
		});
		
		resources.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection )event.getSelection();
				if (ss.isEmpty() || ss.getFirstElement() instanceof String){
					css.setValue(CssConstants.backgroundImage.getName(), null);
				}
				else{
					IResource r = (IResource)ss.getFirstElement();
					css.setValue(CssConstants.backgroundImage.getName(), "url(" + r.getName() + ")");
				}
				picture.redraw();
			}
		});
		
		Button addPicture = new Button(main, SWT.PUSH);
		addPicture.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		addPicture.setText("...");
		addPicture.setToolTipText("Import a pictue as a resource");
		addPicture.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell());
				d.setFilterExtensions(new String[]{"*.bmp", "*.jpg", "*.gif", "*.png"});
				
				String fileName = d.open();
				if (fileName == null){
					return;
				}
				
				File f = new File(fileName);
			
				for(IResource r : Activator.getDefault().getProject().getResources()){
					if (r.getName().equals(f.getName())){
						MessageDialog.openInformation(getShell(), "Resource creation", "A resource with this name already exists.");
						return;
					}
				}
				
				//add the to the project
				IWorkspace workspace = ResourcesPlugin.getWorkspace(); 
				IWorkspaceRoot r = workspace.getRoot();
				IProject p = r.getProject(Activator.getDefault().getProject().getProjectDescriptor().getProjectName());
				IFile file = p.getFile(f.getName());
								
				
				try{
					FileInputStream fis = new FileInputStream(f);
					file.create(fis, true, null);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				FileImage cssF = new FileImage(file.getName(), file.getLocation().toFile());
				Activator.getDefault().getProject().addResource(cssF);

				refreshInput();
				
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Repeat Mode");
		
		repeat = new Combo(main, SWT.READ_ONLY);
		repeat.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		repeat.setItems(CssConstants.backgroundRepeat.getValues());
		repeat.select(0);
		
		picture = new Canvas(main, SWT.NONE);
		picture.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		picture.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				if (resources.getSelection().isEmpty()){
					return;
				}
				
				try{
					IResource r = (IResource)((IStructuredSelection)resources.getSelection()).getFirstElement();
					Image im = new Image(e.display, new FileInputStream(r.getFile()));
					e.gc.drawImage(
							im, 
							0, 
							0, 
							im.getImageData().width, 
							im.getImageData().height, 
							0, 
							0, 
							picture.getClientArea().width, 
							picture.getClientArea().height);
					im.dispose();
					
					//we set a minèheight for the picture
					css.setValue("min-height", im.getImageData().height + "px");
				}catch(Exception ex){
					
				}
				
				e.gc.dispose();
			}
		});
		picture.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				picture.redraw();
				
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				
				
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Background");
		
		color = new Text(main, SWT.BORDER);
		color.setEditable(false);
		color.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
		b.setText("...");
		b.setToolTipText("Change background color");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog d = new ColorDialog(getShell());
				RGB rgb = getRgb(css.getValue(CssConstants.backgroundColor.getName()));
				d.setRGB(rgb);
				
				rgb = d.open();
				Color c = null;
				if (rgb != null){
					c = ColorManager.getColorRegistry().get(getColorCode(rgb));
					if (c == null){
						ColorManager.getColorRegistry().put(getColorCode(rgb), rgb);
						c = ColorManager.getColorRegistry().get(getColorCode(rgb));
					}
					css.setValue(CssConstants.backgroundColor.getName(), getColorCode(rgb));
					color.setBackground(c);
					picture.setBackground(c);
				}
				else{
					color.setBackground(null);
					picture.setBackground(null);
					css.setValue(CssConstants.backgroundColor.getName(), null);
				}
				
				
				
			}
		});
		
		return main;
	}
	
	private void refreshInput(){
		List l = new ArrayList();
		l.add("NONE");
		l.addAll(Activator.getDefault().getProject().getResources(FileImage.class));
		resources.setInput(l);
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText("Css Background - " + css.getName());
		getShell().setSize(600, 400);
		
		refreshInput();
		String imgName = css.getValue(CssConstants.backgroundImage.getName());
		if (imgName == null){
			return;
		}
		for(IResource r : Activator.getDefault().getProject().getResources(FileImage.class)){
			if (imgName.contains(r.getName())){
				resources.setSelection(new StructuredSelection(r));
				
				String repeat = css.getValue(CssConstants.backgroundRepeat.getName());
				if (repeat != null){
					for(int i = 0; i < this.repeat.getItemCount(); i++){
						if (this.repeat.getItem(i).equals(repeat)){
							this.repeat.select(i);
							break;
						}
					}
				}
				
				break;
			}
		}
		//color
		RGB rgb = getRgb(css.getValue(CssConstants.backgroundColor.getName()));
		if (rgb != null){
			Color c = ColorManager.getColorRegistry().get(css.getValue(CssConstants.backgroundColor.getName()));
			if (c == null){
				ColorManager.getColorRegistry().put(css.getValue(CssConstants.backgroundColor.getName()), rgb);
				c = ColorManager.getColorRegistry().get(css.getValue(CssConstants.backgroundColor.getName()));
			}
			picture.setBackground(c);
		}
	}
	
	@Override
	protected void okPressed() {
		css.setValue(CssConstants.backgroundRepeat.getName(), repeat.getText());
		super.okPressed();
	}
	
	private String getColorCode(RGB color){
		StringBuffer buf = new StringBuffer("#");
		String r = Integer.toHexString(color.red);
		if (r.length() == 1){
			r = "0" + r; //$NON-NLS-1$
		}
		String b = Integer.toHexString(color.blue);
		if (b.length() == 1){
			b = "0" + b; //$NON-NLS-1$
		}
		String g = Integer.toHexString(color.green);
		if (g.length() == 1){
			g = "0" + g; //$NON-NLS-1$
		}	
		buf.append(r); buf.append(g);buf.append(b);
		return buf.toString();
	}
	private RGB getRgb(String colorCode){
		try{
			String code = colorCode.replace("#", "");
			int r = Integer.parseInt(code.substring(0, 2), 16);
			int g = Integer.parseInt(code.substring(2, 4), 16);
			int b = Integer.parseInt(code.substring(4, 6), 16);
			
			return new RGB(r, g, b);
			
		}catch(Exception ex){
			return null;
		}
	}
}
