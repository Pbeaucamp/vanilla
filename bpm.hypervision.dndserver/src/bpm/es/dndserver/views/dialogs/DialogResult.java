package bpm.es.dndserver.views.dialogs;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.Message;
import bpm.es.dndserver.views.providers.MessageViewerContentProvider;
import bpm.es.dndserver.views.providers.MessageViewerLabelProvider;

public class DialogResult extends Dialog {
	
	private List<Message> messages;
	private TableViewer viewer;
	
	private Color red = Display.getDefault().getSystemColor(SWT.COLOR_RED); 
	private Color yellow = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW); 
	
	private static String[] columnTitles = {
		Messages.DialogResult_0,
		Messages.DialogResult_1, 
		Messages.DialogResult_2,  
		};
	
	public DialogResult(Shell parentShell, List<Message> msgs) {
		super(parentShell);
		
		this.messages = msgs;
	}
	
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		//getShell().set
	}
	
    protected int getShellStyle() {
        int ret = super.getShellStyle();
        return ret | SWT.RESIZE;
    }

	@Override
	public int open() {
		return super.open();
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout());
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TableViewer(content, SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.getTable().setLinesVisible(false);
		viewer.getTable().setHeaderVisible(true);
		
		viewer.setContentProvider(new MessageViewerContentProvider());
		viewer.setLabelProvider(new MessageViewerLabelProvider());
		
		for (int i = 0; i < columnTitles.length; i++) {
			TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
			column.setText(columnTitles[i]);
			column.setWidth(120);
		}
		
		viewer.setInput(messages);
		
		for (TableItem itm : viewer.getTable().getItems()) {
			if (((Message)itm.getData()).getLevel() == Message.ERROR)
				itm.setBackground(red);
			else if (((Message)itm.getData()).getLevel() == Message.WARNING)
				itm.setBackground(yellow);
			//itm.setBackground(color)
		}
		
		for (TableColumn col : viewer.getTable().getColumns()) {
			col.pack();
		}
		
		//viewer.
		return content;
	}

}
