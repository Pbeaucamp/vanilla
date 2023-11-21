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

public class CompositeFont extends CompositeCssPart{

	private Combo style;
	private Combo weigth;
	private Combo family;
	private Combo unite;
	private Text size;
	private String previousCss;
	
	public CompositeFont(Composite parent, int style) {
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
		l.setText(Messages.CompositeFont_0);
		
		family = new Combo(main, SWT.READ_ONLY);
		family.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		family.setItems(new String[]{ "sans-serif", "serif","cursive", "fantasy", "monospace"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		family.select(0);
		family.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeFont.this;
				CompositeFont.this.notifyListeners(99, event);
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeFont_6);
		
		style = new Combo(main, SWT.READ_ONLY);
		style.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		style.setItems(new String[]{"normal", "italic", "oblique"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		style.select(0);
		style.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeFont.this;
				CompositeFont.this.notifyListeners(99, event);
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeFont_10);
		
		weigth = new Combo(main, SWT.READ_ONLY);
		weigth.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		weigth.setItems(new String[]{"normal", "bold", "bolder", "light", "lighter"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		weigth.select(0);
		weigth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeFont.this;
				CompositeFont.this.notifyListeners(99, event);
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeFont_16);
		
		size = new Text(main, SWT.BORDER);
		size.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		size.setText("10"); //$NON-NLS-1$
		size.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Event event = new Event();
				event.widget = CompositeFont.this;
				CompositeFont.this.notifyListeners(99, event);
			}
		});
		
		unite = new Combo(main, SWT.READ_ONLY);
		unite.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		unite.setItems(new String[]{"px", "ex", "em", "pt", "mm", "cm"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		unite.select(0);
		unite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = new Event();
				event.widget = CompositeFont.this;
				CompositeFont.this.notifyListeners(99, event);
			}
		});

	}

	@Override
	public String getCssToString() {
		StringBuffer buf = new StringBuffer();
		buf.append("\tfont-family:" + family.getText() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tfont-size:" + size.getText() + unite.getText() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tfont-style:" + style.getText() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\tfont-weight:" + weigth.getText() + ";\n"); //$NON-NLS-1$ //$NON-NLS-2$
		
		String css = buf.toString();
		previousCss = css;
		return css;
	}

	@Override
	public String getPreviousCss() {
		if(previousCss != null) {
			return previousCss;
		}
		return getCssToString();
	}

}
