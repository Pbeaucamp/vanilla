package bpm.fd.design.ui.structure.dialogs.creation.css;




import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.design.ui.Messages;

public class CompositeText extends CompositeCssPart{

	private Combo align;
	private Combo decorate;
	private Text indent;
	private Combo unite;
	private String previousCss;
	
	
	public CompositeText(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		createContent();
	}

	@Override
	protected void createContent() {
		Composite main = new Composite(this, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeText_0);
		
		align = new Combo(main, SWT.READ_ONLY);
		align.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		align.setItems(new String[]{"left", "right", "center", "justify"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		align.select(0);
		align.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeText.this;
				CompositeText.this.notifyListeners(99, event);
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeText_5);
		
		decorate = new Combo(main, SWT.READ_ONLY);
		decorate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		decorate.setItems(new String[]{"none", "underline", "overline", "line-through", "blink"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		decorate.select(0);
		decorate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeText.this;
				CompositeText.this.notifyListeners(99, event);
			}
		});

		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeText_11);
		
		indent = new Text(main, SWT.BORDER);
		indent.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		indent.setText("10"); //$NON-NLS-1$
		indent.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Event event = new Event();
				event.widget = CompositeText.this;
				CompositeText.this.notifyListeners(99, event);
			}
		});
		
		unite = new Combo(main, SWT.READ_ONLY);
		unite.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false));
		unite.setItems(new String[]{"px", "em", "px", "pt", "mm", "cm"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		unite.select(0);
		unite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeText.this;
				CompositeText.this.notifyListeners(99, event);
			}
		});

	}

	@Override
	public String getCssToString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\ttext-align:" + align.getText() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\ttext-decoration:" + decorate.getText() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\ttext-indent:" + indent.getText() + unite.getText() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
	
		String res = buf.toString();
		previousCss = res;
		
		return res;
	}

	@Override
	public String getPreviousCss() {
		if(previousCss != null) {
			return previousCss;
		}
		return getCssToString();
	}

}
