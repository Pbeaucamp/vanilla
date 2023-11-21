package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.jsdt.ui.JavaScriptUI;
import org.eclipse.wst.jsdt.ui.text.JavaScriptSourceViewerConfiguration;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.sections.tools.ComponentProposalProvider;

public class DialogEvent extends Dialog{
	

	private static final Font JAVASCRIPT_FONT = new Font(Display.getDefault(), "Courier New", 10, SWT.NORMAL); //$NON-NLS-1$
	private SourceViewer sourceViewer;
	private String content;
	
	public DialogEvent(Shell parentShell, String content) {
		super(parentShell);
		this.content = content;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		sourceViewer = new SourceViewer(main, new VerticalRuler(10), SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		sourceViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		sourceViewer.getTextWidget().setFont(JAVASCRIPT_FONT);
		
		KeyStroke key = null;
		try{
			key = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$
		}catch(Exception ex){
			
		}
		
	
		ContentProposalAdapter proposal = new ContentProposalAdapter(sourceViewer.getTextWidget(), new TextContentAdapter(), 
				new ComponentProposalProvider(new ArrayList<IComponentDefinition>()),
				key,
				null);
		

		sourceViewer.configure(new JavaScriptSourceViewerConfiguration(JavaScriptUI.getColorManager(), Activator.getDefault().getPreferenceStore(), null, null));

		return main;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogEvent_0);
		getShell().setSize(400, 300);
		sourceViewer.setDocument(new Document());
		sourceViewer.getDocument().set(content);
	}
	
	@Override
	protected void okPressed() {
		content = sourceViewer.getTextWidget().getText();
		super.okPressed();
	}
	
	public String getContent(){
		return content;
	}
}
