package bpm.fd.design.ui.editor.style;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.vanilla.platform.core.utils.IOWriter;

public class DialogCssProperties extends Dialog{
	private String cssClassName;
//	private ComposantCssEditor editor;
	private CssComposite editor;
	
	private CssClass css;
	private IResource resource;
	private String originalCssCode;
	
	public DialogCssProperties(Shell parentShell, String cssClassName) {
		super(parentShell);
		this.cssClassName = cssClassName;
		setShellStyle(getShellStyle());
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
//		editor = new ComposantCssEditor(parent, SWT.NONE);
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		editor = new CssComposite(main, SWT.NONE);
		editor.setLayoutData(new GridData(GridData.FILL_BOTH));
		return editor;
	}
	
	@Override
	protected void okPressed() {
		String csscode  = CssHelper.writeCssClass(css, originalCssCode);
		
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (part instanceof FdEditor){
			StructuredTextEditor ed = ((FdEditor)part).getCssPage((FileCSS)resource);
			((StructuredTextViewer)ed.getTextViewer()).getTextWidget().setText(csscode);
			ed.getTextViewer().refresh();
		}
		
		super.okPressed();
	}
	
	
	@Override
	protected void initializeBounds() {
		getShell().setText("Css Class - "+ cssClassName );
		getShell().setSize(700, 500);
		
		//read the cssFrom projetc cssFile
		for(IResource r : Activator.getDefault().getProject().getResources(FileCSS.class)){
			
			try{
				FileInputStream fis = new FileInputStream(r.getFile());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				IOWriter.write(fis, bos, true, true);
				originalCssCode  = bos.toString("UTF-8");
				resource = r;
				css = CssHelper.readCssClass(cssClassName, originalCssCode);
				if (css != null){
					editor.fill(css);
					return;
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
		}
		
		
	}

	public CssClass getCss(){
		return css;
	}
}
