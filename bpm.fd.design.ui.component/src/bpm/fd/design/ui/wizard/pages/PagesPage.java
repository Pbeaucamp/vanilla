package bpm.fd.design.ui.wizard.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.icons.Icons;

public class PagesPage extends WizardPage {
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);
	private CheckboxTableViewer viewer;
	private List<String> names = new ArrayList<String>();

	public PagesPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);

	}

	private void createPageContent(Composite parent) {

		Composite bar = new Composite(parent, SWT.NONE);
		bar.setLayout(new GridLayout());
		bar.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));

		Button add = new Button(bar, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.CENTER, GridData.END, false, false));
		add.setToolTipText(Messages.PagesPage_0);
		add.setImage(Activator.getDefault().getImageRegistry().get(Icons.add));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = "model_" + (names.size() + 1);
				names.add(name); //$NON-NLS-1$
				viewer.refresh();
				viewer.setChecked(name, true);
				viewer.refresh();
				getContainer().updateButtons();
			}

		});

		final Button del = new Button(bar, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.CENTER, GridData.END, false, false));
		del.setToolTipText(Messages.PagesPage_2);
		del.setImage(Activator.getDefault().getImageRegistry().get(Icons.delete));
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				for(Object o : ((IStructuredSelection) viewer.getSelection()).toList()) {
					names.remove(o);
				}

				viewer.refresh();
				getContainer().updateButtons();
			}
		});

		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.PagesPage_3);

		viewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.V_SCROLL);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				Collection<Object> c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				del.setEnabled(!viewer.getSelection().isEmpty());

			}
		});
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(names);

		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setWidth(400);
		col.setLabelProvider(new ColumnLabelProvider());
		col.setEditingSupport(new EditingSupport(viewer) {
			TextCellEditor editor = new TextCellEditor(viewer.getTable());

			@Override
			protected void setValue(Object element, Object value) {

				if(getPreviousPage() instanceof ProjectDefintionPage) {
					String s = ((ProjectDefintionPage) getPreviousPage()).getProjectDescriptor().getModelName();
					if(s.equals(value)) {
						setErrorMessage(Messages.PagesPage_5);
						getContainer().updateButtons();
						return;
					}
				}

				List l = ((List) viewer.getInput());
				boolean check = viewer.getChecked(element);
				l.set(l.indexOf(element), value);
				viewer.refresh();
				viewer.setChecked(value, check);

				for(String s : names) {
					for(String ss : names) {
						if(s != ss && s.equals(ss)) {
							setErrorMessage(Messages.PagesPage_4);
							getContainer().updateButtons();
							return;
						}
					}
				}
				setErrorMessage(null);
				getContainer().updateButtons();
			}

			@Override
			protected Object getValue(Object element) {
				return (String) element;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

	}

	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}

	public HashMap<String, Boolean> getNames() {
		HashMap<String, Boolean> models = new LinkedHashMap<String, Boolean>();
		for(String s : names) {
			models.put(s, viewer.getChecked(s));
		}
		return models;
	}

}
