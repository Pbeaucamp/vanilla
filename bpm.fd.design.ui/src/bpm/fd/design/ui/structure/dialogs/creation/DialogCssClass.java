package bpm.fd.design.ui.structure.dialogs.creation;


import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.css.core.internal.encoding.CSSDocumentLoader;
import org.eclipse.wst.css.ui.StructuredTextViewerConfigurationCSS;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.resources.FileCSS;
import bpm.fd.api.core.model.resources.IResource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.structure.dialogs.creation.css.CompositeBorder;
import bpm.fd.design.ui.structure.dialogs.creation.css.CompositeCssPart;
import bpm.fd.design.ui.structure.dialogs.creation.css.CompositeFont;
import bpm.fd.design.ui.structure.dialogs.creation.css.CompositeText;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;
import bpm.vanilla.designer.ui.common.IDesignerActivator;
import bpm.vanilla.platform.core.IRepositoryContext;
/**
 * @deprecated : new css popup must be used
 * @author ludo
 *
 */
public abstract class DialogCssClass extends Dialog{

	private CompositeBorder border;
	private CompositeFont font;
	private CompositeText text;
	private Text  className, color, bgColor;
	private Color col = new Color(Display.getDefault(), 0, 0, 0);
	private Color bgcol= new Color(Display.getDefault(), 255, 255, 255);
	
	private String name;
	private String codeCss;
	
	private ComboViewer cssResource;
	
	private TabItem cssPreview;
	
	private TabItem previewHtml;
	
	private IDocument cssDocument;
	private StructuredTextViewer txtViewer;
	private Browser browser;
	
	private IBaseElement element;
	
	private String actualCss;
	
	private Button btnBgColor, btnColor;
	private String previousBgColor = "#ffffff";
	private String previousColor = "#000000";
	
	public DialogCssClass(Shell parentShell, IBaseElement element, String cssClass) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.element = element;
		this.actualCss = cssClass;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		folder.setLayout(new GridLayout());
		
		
		TabItem general = new TabItem(folder, SWT.NONE);
		general.setText(Messages.DialogCssClass_0);
		general.setControl(createGeneral(folder));
		
		TabItem text = new TabItem(folder, SWT.NONE);
		text.setText(Messages.DialogCssClass_1);
		text.setControl(createText(folder));
		
		TabItem font = new TabItem(folder, SWT.NONE);
		font.setText(Messages.DialogCssClass_2);
		font.setControl(createFont(folder));
		
		TabItem border = new TabItem(folder, SWT.NONE);
		border.setText(Messages.DialogCssClass_3);
		border.setControl(createBorder(folder));
		
		cssPreview = new TabItem(folder, SWT.NONE);
		cssPreview.setText("Css");
		cssPreview.setControl(createCssPreview(folder));
		
		previewHtml = new TabItem(folder, SWT.NONE);
		previewHtml.setText("Preview");
		previewHtml.setControl(createHtmlPreview(folder));
		
		folder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item.equals(cssPreview)) {
					cssDocument.set(actualCss);
					txtViewer.refresh();
				}
				else if(e.item.equals(previewHtml)) {
					try {
						Browser.clearSessions();
						String html = createPreview();
						browser.setText(html);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		return main;
	}
	
	private String createPreview() throws Exception {
		
		String css = "";
		if(actualCss != null) {
			css = actualCss;
		}
		else {
			css = getCss();
		}
		
		/* HTMLGenerator generator = new HTMLGenerator(element, name, css);
		
		IRepositoryContext ctx = getRepositoryContext();
		
		String html = generator.generateHtml(ctx); 
		return html;*/return null;
	}

	private IRepositoryContext getRepositoryContext() {
		try{
			Bundle bundle = Platform.getBundle("bpm.fd.repository.ui"); //$NON-NLS-1$
			String activator = (String)bundle.getHeaders().get(Constants.BUNDLE_ACTIVATOR);

			Class<?> activatorClass = bundle.loadClass(activator);
			Method method = activatorClass.getMethod("getDefault"); //$NON-NLS-1$
			IDesignerActivator<FdProject> designer = (IDesignerActivator<FdProject>)method.invoke(activatorClass);
			if (designer != null){
				return designer.getRepositoryContext();
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		return null;
	}

	private Control createHtmlPreview(TabFolder folder) {
		try {
			browser = new Browser(folder, SWT.MOZILLA);
		} catch (Exception e) {
			e.printStackTrace();
			browser = new Browser(folder, SWT.NONE);
		}
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		return browser;
	}

	private Control createCssPreview(TabFolder folder) {
		Composite main = new Composite(folder, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		actualCss = getCss();
		
		txtViewer = new StructuredTextViewer(main, null, null, false, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		CSSDocumentLoader a = new CSSDocumentLoader();
		cssDocument = a.createNewStructuredDocument();
		String css = actualCss;
		cssDocument.set(css);
		txtViewer.setDocument(cssDocument);
		
		StructuredTextViewerConfigurationCSS conf = new StructuredTextViewerConfigurationCSS();
		txtViewer.configure(conf);

		cssDocument.addDocumentListener(new IDocumentListener() {
			
			@Override
			public void documentChanged(DocumentEvent event) {
				actualCss = event.getDocument().get();
			}
			
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
		});
		
		return main;
	}
	
	private String loadCssCodeFromFile(String className){
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (part instanceof FdEditor){
			StructuredTextEditor ed = ((FdEditor)part).getCssPage((FileCSS)((IStructuredSelection)cssResource.getSelection()).getFirstElement());
			String fullCss = ((StructuredTextViewer)ed.getTextViewer()).getTextWidget().getText();
			
			int i = fullCss.indexOf("." + className+"{");
			if (i >= 0){
				int e = fullCss.indexOf("}", i);
				if (e>= 0){
					return fullCss.substring(i, e + 1);
				}
			}
		}
		return null;
	}
	
	private String getCss() {
		name = className.getText().trim();
		
		if (codeCss == null){
			codeCss = loadCssCodeFromFile(name);
		}
		if (codeCss == null){
			StringBuffer buf = new StringBuffer();
			buf.append("#" + name + "{\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append(border.getCssToString());
			buf.append(text.getCssToString());
			buf.append(font.getCssToString());
			
			buf.append("\tcolor:#" + CompositeBorder.convertColorInHexa(col) + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("\tbackground-color:#" + CompositeBorder.convertColorInHexa(bgcol) + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("}\n"); //$NON-NLS-1$
			
			codeCss = buf.toString();
		}
		

		return codeCss;
	}


	private Composite createGeneral(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogCssClass_4);
		l.setLayoutData(new GridData());
		
		cssResource = new ComboViewer(main, SWT.READ_ONLY);
		cssResource.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		cssResource.setContentProvider(new ListContentProvider<IResource>());
		cssResource.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IResource)element).getName();
			}
		});
		cssResource.setInput(Activator.getDefault().getProject().getResources(FileCSS.class));
		try{
			cssResource.setSelection(new StructuredSelection(((List)cssResource.getInput()).get(0)));
		}catch(Exception ex){
			//getButton(IDialogConstants.OK_ID).setEnabled(false);
		}
		
		l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogCssClass_5);
		l.setLayoutData(new GridData());
		
		className= new Text(main, SWT.BORDER);
		className.setText(element.getId());
		className.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		className.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Event event = new Event();
				event.widget = className;
				className.notifyListeners(99, event);
			}
		});
		className.addListener(99, cssChangeListener);
		className.setEnabled(false);
		
		l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogCssClass_7);
		l.setLayoutData(new GridData());
		
		color = new Text(main, SWT.BORDER);
		color.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		color.setBackground(col);
		
		btnColor = new Button(main, SWT.PUSH);
		btnColor.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnColor.setText(Messages.DialogCssClass_8);
		btnColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog d = new ColorDialog(getShell());
					RGB rgb = d.open();
					
					if (rgb != null){
						if (col != null){
							col.dispose();
						}
						
						col = new Color(Display.getDefault(), rgb);
						color.setBackground(col);
						Event event = new Event();
						event.widget = btnColor;
						btnColor.notifyListeners(99, event);
					}
				}
		});
		btnColor.addListener(99, cssChangeListener);
		
		l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogCssClass_9);
		l.setLayoutData(new GridData());
		
		bgColor = new Text(main, SWT.BORDER);
		bgColor.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bgColor.setBackground(bgcol);
		
		btnBgColor = new Button(main, SWT.PUSH);
		btnBgColor.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		btnBgColor.setText("..."); //$NON-NLS-1$
		btnBgColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog d = new ColorDialog(getShell());
					RGB rgb = d.open();
					
					if (rgb != null){
						if (bgcol != null){
							bgcol.dispose();
						}
						
						bgcol = new Color(Display.getDefault(), rgb);
						bgColor.setBackground(bgcol);
						Event event = new Event();
						event.widget = btnBgColor;
						btnBgColor.notifyListeners(99, event);
					}
				}
		});
		btnBgColor.addListener(99, cssChangeListener);
		
		return main;
	}
	
	private Composite createBorder(Composite parent){
		border = new CompositeBorder(parent, SWT.NONE);
		border.setLayout(new GridLayout());
		border.setLayoutData(new GridData(GridData.FILL_BOTH));
		border.addListener(99, cssChangeListener);
		return border;
	}
	
	private Composite createText(Composite parent){
		text = new CompositeText(parent, SWT.NONE);
		text.setLayout(new GridLayout());
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.addListener(99, cssChangeListener);
		return text;
	}
	
	private Composite createFont(Composite parent){
		font = new CompositeFont(parent, SWT.NONE);
		font.setLayout(new GridLayout());
		font.setLayoutData(new GridData(GridData.FILL_BOTH));
		font.addListener(99, cssChangeListener);
		return font;
	}
	
	private Listener cssChangeListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			try {
				if(event.widget != null) {
					if(event.widget instanceof CompositeCssPart) {
						CompositeCssPart css = (CompositeCssPart) event.widget;
						String previous = css.getPreviousCss();
						String[] line = previous.split("\\\n");
						for(int i = 0; i < line.length ; i++) {
							actualCss = actualCss.replace(line[i] + "\n", "");
						}
						
						int index = actualCss.lastIndexOf("}");
						
						String cssToAdd = css.getCssToString();
						String res = "";
						String[] cssLine = cssToAdd.split("\\\n");
						for(int i = 0; i < cssLine.length ; i++) {
							String l = cssLine[i];
							if(!actualCss.contains(l.split(":")[0])) {
								res += l + "\n";
							}
						}
						
						actualCss = new StringBuilder(actualCss).insert(index, res).toString();
					}
					else if(event.widget instanceof Button) {
						Button btn = (Button) event.widget;
						
						String line = "";
						if(btn.equals(btnBgColor)) {
							line = "	background-color:#"+CompositeBorder.convertColorInHexa(bgcol) + ";\n";
						}
						else if(btn.equals(btnColor)) {
							line = "	color:#"+CompositeBorder.convertColorInHexa(col) + ";\n";
						}
						
						String[] actualLines = actualCss.split("\n");
						String res = "";
						for(int i = 0; i < actualLines.length ; i++) {
							if(btn.equals(btnBgColor) && actualLines[i].startsWith("	background-color:#")) {
								res+=line;
							}
							else if(btn.equals(btnColor) && actualLines[i].startsWith("	color:#")) {
								res+=line;
							}
							else {
								res+=actualLines[i] + "\n";
							}
						}
						
						actualCss = res;
					}
					else if(event.widget instanceof Text) {
						Text text = (Text) event.widget;
						
						String name = text.getText();
						
						String res = "";
						String[] actualLines = actualCss.split("\n");
						for(int i = 0; i < actualLines.length ; i++) {
							if(actualLines[i].startsWith(".")) {
								res+="." + name + "{\n";
							}
							else {
								res+=actualLines[i] + "\n";
							}
						}
						actualCss=res;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogCssClass_11);
		
	}
	
	@Override
	protected void okPressed() {
		if(actualCss != null) {
			codeCss = actualCss;
		}
		else {
			codeCss = getCss();
		}
		
		IEditorPart part = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (part instanceof FdEditor){
			StructuredTextEditor ed = ((FdEditor)part).getCssPage((FileCSS)((IStructuredSelection)cssResource.getSelection()).getFirstElement());
			((StructuredTextViewer)ed.getTextViewer()).getTextWidget().setText(((StructuredTextViewer)ed.getTextViewer()).getTextWidget().getText() + codeCss);
			ed.getTextViewer().refresh();
		}
		
		super.okPressed();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
