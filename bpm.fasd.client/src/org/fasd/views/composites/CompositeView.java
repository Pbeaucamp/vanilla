package org.fasd.views.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.fasd.i18N.LanguageText;
import org.fasd.security.SecurityGroup;
import org.fasd.security.View;
import org.fasd.views.dialogs.DialogMember;
import org.freeolap.FreemetricsPlugin;

public class CompositeView {
	private Text name, desc;
	private Button ok, cancel;
	private Button allowAccess, allowFullAccess;
	private View view;
	private ListViewer viewer;
	private Combo group;
	private SecurityGroup g;
	private HashMap<String, SecurityGroup> secuGroups = new HashMap<String, SecurityGroup>();

	private List<String> input = new ArrayList<String>();

	public CompositeView(final Composite parent, View v, IViewSite s) {
		view = v;

		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		Label lbl = new Label(container, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.NONE));
		lbl.setText(LanguageText.CompositeView_0);

		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lb2 = new Label(container, SWT.NONE);
		lb2.setLayoutData(new GridData(SWT.NONE));
		lb2.setText(LanguageText.CompositeView_1);

		desc = new Text(container, SWT.BORDER);
		desc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lb3 = new Label(container, SWT.NONE);
		lb3.setLayoutData(new GridData(SWT.NONE));
		lb3.setText(LanguageText.CompositeView_2);

		group = new Combo(container, SWT.BORDER);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (SecurityGroup g : FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().getSecurityGroups()) {
			secuGroups.put(g.getName(), g);
			group.add(g.getName());
		}
		group.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				g = secuGroups.get(group.getText());
			}

		});

		allowAccess = new Button(container, SWT.CHECK);
		allowAccess.setLayoutData(new GridData());
		allowAccess.setText(LanguageText.CompositeView_3);

		allowFullAccess = new Button(container, SWT.CHECK);
		allowFullAccess.setLayoutData(new GridData());
		allowFullAccess.setText(LanguageText.CompositeView_4);

		Button t = new Button(container, SWT.PUSH);
		t.setLayoutData(new GridData());
		t.setText(LanguageText.CompositeView_5);
		t.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DialogMember dial = new DialogMember(container.getShell(), view.getParent());
				if (dial.open() == Dialog.OK) {
					if (!input.contains(dial.getMember())) {
						input.add(dial.getMember());
						viewer.setInput(input.toArray(new String[input.size()]));
					}

				}
			}
		});

		Button d = new Button(container, SWT.PUSH);
		d.setLayoutData(new GridData());
		d.setText(LanguageText.CompositeView_6);
		d.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Object ss = ((StructuredSelection) viewer.getSelection()).getFirstElement();
				if (ss instanceof String) {
					for (String m : input) {
						if (m.equals(ss)) {
							input.remove(m);
							viewer.setInput(input.toArray(new String[input.size()]));
							break;
						}
					}
				}
			}
		});

		viewer = new ListViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return (String) element;
			}

		});
		viewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof String[]) {
					return (String[]) inputElement;
				}
				return null;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		input.addAll(view.getMembers());
		viewer.setInput(input.toArray(new String[input.size()]));

		ok = new Button(container, SWT.PUSH);
		ok.setText(LanguageText.CompositeView_7);
		ok.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				view.setName(name.getText());
				view.setDesc(desc.getText());
				view.setAllowAccess(allowAccess.getSelection());
				view.setAllowFullAccess(allowFullAccess.getSelection());
				if (g != null)
					view.setGroup((SecurityGroup) FreemetricsPlugin.getDefault().getFAModel().getOLAPSchema().findSecurityGroup(g.getId()));

				for (String s : input) {
					boolean flag = false;
					for (String ss : view.getMembers()) {
						flag |= s.equals(ss);
					}
					if (!flag)
						view.addMember(s);
				}

				for (String s : view.getMembers()) {
					boolean flag = false;
					for (String ss : input) {
						flag |= s.equals(ss);
					}
					if (!flag)
						view.removeMember(s);
				}
				FreemetricsPlugin.getDefault().getFAModel().setChanged();
			}
		});

		cancel = new Button(container, SWT.PUSH);
		cancel.setText(LanguageText.CompositeView_8);
		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fillData();
			}
		});

		fillData();
	}

	public void fillData() {
		name.setText(view.getName());
		desc.setText(view.getDesc());
		allowAccess.setSelection(view.isAllowAccess());
		allowFullAccess.setSelection(view.isAllowFullAccess());

		if (view.getGroup() == null)
			return;

		for (int i = 0; i < group.getItemCount(); i++) {
			for (String s : secuGroups.keySet()) {
				if (view.getGroup().getName().equals(s)) {
					group.select(i);
					break;
				}
			}
		}

	}
}
