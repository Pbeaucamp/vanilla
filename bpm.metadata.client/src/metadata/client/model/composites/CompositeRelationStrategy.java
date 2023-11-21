package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.List;

import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.Relation;

public class CompositeRelationStrategy extends Composite {

	private IBusinessModel model;
	private RelationStrategy strategy;
	
	private Text txtName;
	
	private CheckboxTreeViewer tableViewer;
	private CheckboxTreeViewer relationViewer;

	public CompositeRelationStrategy(Composite parent, int style, IBusinessModel model, RelationStrategy strategy) {
		super(parent, style);
		
		this.strategy = strategy;
		this.model = model;
		
		Composite root = new Composite(parent, SWT.NONE);
		
		root.setLayout(new GridLayout(2,true));
		root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblName = new Label(root, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL ,SWT.FILL, false, false));
		lblName.setText("Name");
		
		txtName = new Text(root, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL ,SWT.FILL, true, false));
		
		Label lblTables = new Label(root, SWT.NONE);
		lblTables.setLayoutData(new GridData(SWT.FILL ,SWT.FILL, false, false));
		lblTables.setText("Tables");
		
		Label lblRelations = new Label(root, SWT.NONE);
		lblRelations.setLayoutData(new GridData(SWT.FILL ,SWT.FILL, false, false));
		lblRelations.setText("Relations");
		
		tableViewer = new CheckboxTreeViewer(root, SWT.BORDER | SWT.V_SCROLL);
		tableViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IDataStream)element).getName();
			}
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get("bus_table");
			}
		});
		
		tableViewer.setContentProvider(new ITreeContentProvider() {			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}
			
			@Override
			public void dispose() {	}
			
			@Override
			public Object[] getElements(Object inputElement) {
				List<IDataStream> tables = (List<IDataStream>) inputElement;
				return tables.toArray();
			}
			
			@Override
			public boolean hasChildren(Object element) {
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}
		});
		
		
		
		relationViewer = new CheckboxTreeViewer(root, SWT.BORDER | SWT.V_SCROLL);
		relationViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		relationViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Relation)element).getName();
			}
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get("relation");
			}
		});
		
		relationViewer.setContentProvider(new ITreeContentProvider() {		
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			@Override
			public void dispose() {}
			
			@Override
			public Object[] getElements(Object inputElement) {
				List<Relation> tables = (List<Relation>) inputElement;
				return tables.toArray();
			}
			
			@Override
			public boolean hasChildren(Object element) {
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				return null;
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}
		});
		
		fillData();
		
	}
	
	private void fillData() {
		List<IDataStream> tables = model.getModel().getDataSources().iterator().next().getDataStreams();
		List<Relation> relations = model.getRelations();
		
		tableViewer.setInput(tables);
		relationViewer.setInput(relations);

		if(strategy != null) {
			txtName.setText(strategy.getName());
			
			List<IDataStream> selectedTables = new ArrayList<IDataStream>();
			for(String tableName : strategy.getTableNames()) {
				for(IDataStream table : tables) {
					if(table.getName().equals(tableName)) {
						selectedTables.add(table);
						break;
					}
				}
			}
			tableViewer.setCheckedElements(selectedTables.toArray());
			
			List<Relation> selectedRelations = new ArrayList<Relation>();
			for(String key : strategy.getRelationKeys()) {
				for(Relation relation : relations) {
					if(relation.getRelationKey().equals(key)) {
						selectedRelations.add(relation);
						break;
					}
				}
			}
			
			relationViewer.setCheckedElements(selectedRelations.toArray());
		}
		
	}
	
	public void okPressed() {
		if(strategy == null) {
			strategy = new RelationStrategy();
		}
		else {
			model.getRelationStrategies().remove(strategy);
		}
		
		strategy.setName(txtName.getText());
		
		List<String> tableNames = new ArrayList<String>();
		for(Object table : (Object[])tableViewer.getCheckedElements()) {
			tableNames.add(((IDataStream)table).getName());
		}
		
		List<String> relationKeys = new ArrayList<String>();
		for(Object relation : (Object[])relationViewer.getCheckedElements()) {
			relationKeys.add(((Relation)relation).getRelationKey());
		}
		
		strategy.setTableNames(tableNames);
		strategy.setRelationKeys(relationKeys);
		
		model.getRelationStrategies().add(strategy);
	}

}
