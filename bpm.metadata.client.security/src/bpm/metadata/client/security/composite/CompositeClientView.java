package bpm.metadata.client.security.composite;

import java.util.List;

import metadata.client.helper.GroupHelper;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import bpm.metadata.client.security.viewers.FMDTClientTreeProvider;
import bpm.metadata.client.security.viewers.FMDTLabelProvider;
import bpm.metadata.client.security.viewers.FMDTViewerComparator;

public class CompositeClientView extends Composite{
	


	private Combo groups;
	private FormToolkit toolkit;
	private TreeViewer modelViewer;
	
	public CompositeClientView(Composite parent, int style, FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;
		this.setLayout(new GridLayout(2, false));
		createContent();
	}

	public void loadGroups(){
		List<String> l = GroupHelper.getGroups(0,100);
		groups.setItems(l.toArray(new String[l.size()]));
	}
	
	private void createContent(){
		Label l = new Label(this, SWT.NONE);
		l.setText("Select a Security Group");
		l.setLayoutData(new GridData());
		
		groups = new Combo(this, SWT.READ_ONLY);
		groups.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				modelViewer.setInput(groups.getText());
			}
		});
		
		modelViewer = new TreeViewer(toolkit.createTree(this, SWT.BORDER | SWT.V_SCROLL  | SWT.VIRTUAL));
		modelViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		modelViewer.setLabelProvider(new FMDTLabelProvider());
		
		modelViewer.setContentProvider(new FMDTClientTreeProvider());
		modelViewer.setComparator(new FMDTViewerComparator());
		
		modelViewer.setAutoExpandLevel(3);

	}

	public ISelectionProvider getViewer() {
		return modelViewer;
	}
}
