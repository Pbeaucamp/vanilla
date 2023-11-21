package bpm.metadata.ui.query.resources.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.ui.query.i18n.Messages;

public class DialogRelationStrategySelection extends Dialog {

	private CheckboxTableViewer viewer;
	private IBusinessPackage pack;
	private List<RelationStrategy> selectedStrategies;
	
	public DialogRelationStrategySelection(Shell parentShell, List<RelationStrategy> selected, IBusinessPackage pack) {
		super(parentShell);
		
		this.pack = pack;
		this.selectedStrategies = selected;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(620, 400);
		getShell().setText(Messages.DialogRelationStrategySelection_0);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(1, false));
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lbl = new Label(root, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lbl.setText(Messages.DialogRelationStrategySelection_1);

		viewer = CheckboxTableViewer.newCheckList(root, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new ITreeContentProvider() {
			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<RelationStrategy>)inputElement).toArray();
			}

			@Override
			public void dispose() {}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		});
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		TableViewerColumn colName = new TableViewerColumn(viewer, SWT.BORDER);
		colName.getColumn().setText(Messages.DialogRelationStrategySelection_2);
		colName.getColumn().setWidth(150);
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((RelationStrategy)element).getName();
			}
		});
		
		TableViewerColumn colTables = new TableViewerColumn(viewer, SWT.BORDER);
		colTables.getColumn().setText(Messages.DialogRelationStrategySelection_3);
		colTables.getColumn().setWidth(150);
		colTables.setLabelProvider(new OwnerDrawLabelProvider() {
			
			@Override
			protected void paint(Event event, Object element) {
				StringBuilder buf = new StringBuilder();
				
				for(String table : ((RelationStrategy)element).getTableNames()) {
					buf.append(table + "\n"); //$NON-NLS-1$
				}
				event.gc.drawText(buf.toString(), event.x, event.y, true);
			}
			
			@Override
			protected void measure(Event event, Object element) {
				
				StringBuilder buf = new StringBuilder();
				
				for(String table : ((RelationStrategy)element).getTableNames()) {
					buf.append(table + "\n"); //$NON-NLS-1$
				}
				
				Point size = event.gc.textExtent(buf.toString());
				event.width = viewer.getTable().getColumn(event.index).getWidth();
				int lines = size.x / event.width + 1;
				event.height = size.y * lines;
			}
		});
		
		TableViewerColumn colRels = new TableViewerColumn(viewer, SWT.BORDER);
		colRels.getColumn().setText(Messages.DialogRelationStrategySelection_6);
		colRels.getColumn().setWidth(300);
	
		colRels.setLabelProvider(new OwnerDrawLabelProvider() {
			
			@Override
			protected void paint(Event event, Object element) {
				StringBuilder buf = new StringBuilder();
				
				for(String rel : ((RelationStrategy)element).getRelationKeys()) {
					buf.append(pack.getBusinessModel().getRelationByKey(rel).getName() + "\n"); //$NON-NLS-1$
				}
				event.gc.drawText(buf.toString(), event.x, event.y, true);
			}
			
			@Override
			protected void measure(Event event, Object element) {
				
				StringBuilder buf = new StringBuilder();
				
				for(String rel : ((RelationStrategy)element).getRelationKeys()) {
					buf.append(pack.getBusinessModel().getRelationByKey(rel).getName() + "\n"); //$NON-NLS-1$
				}
				
				Point size = event.gc.textExtent(buf.toString());
				event.width = viewer.getTable().getColumn(event.index).getWidth();
				int lines = size.x / event.width + 1;
				event.height = size.y * lines;
			}
		});
		
		fillData();
		
		return root;
	}
	
	private void fillData() {
		viewer.setInput(pack.getBusinessModel().getRelationStrategies());
		
		viewer.setCheckedElements(selectedStrategies.toArray());
	}
	
	@Override
	protected void okPressed() {
		Object[] strats = (Object[]) viewer.getCheckedElements();
		selectedStrategies = new ArrayList<RelationStrategy>();
		for(Object s : strats) {
			selectedStrategies.add((RelationStrategy) s);
		}
		super.okPressed();
	}

	public List<RelationStrategy> getSelectedStrategies() {
		return selectedStrategies;
	}
	

}
