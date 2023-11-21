package bpm.gateway.ui.tools.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import bpm.gateway.core.StreamElement;
import bpm.gateway.ui.composites.utils.CompositeTableBrowser;
import bpm.gateway.ui.i18n.Messages;

public class DialogBrowseContent extends Dialog {

	private List<List<Object>> values;
	private List<String> colNames; 
	private CompositeTableBrowser composite ;
	
	public DialogBrowseContent(Shell parentShell, List<List<Object>> values, List<StreamElement> cols) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.values = values;
		
		colNames = new ArrayList<String>();
		
		for(StreamElement e : cols){
			colNames.add(e.name);
		}
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(false);
		sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		
				
		
		
		

		
		
		composite = new CompositeTableBrowser(sc, SWT.NONE, colNames, values);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		sc.setContent(composite);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		
		sc.setMinWidth(Display.getDefault().getPrimaryMonitor().getBounds().width);
		sc.setMinHeight(Display.getDefault().getPrimaryMonitor().getBounds().height);
		sc.setMinSize(600, 400);
		
		return composite;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
		getShell().setText(Messages.DialogBrowseContent_0);
		composite.fill();
		
	}

	
	
}
