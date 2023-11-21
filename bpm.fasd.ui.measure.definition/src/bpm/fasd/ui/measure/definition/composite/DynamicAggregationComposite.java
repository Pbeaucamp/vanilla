package bpm.fasd.ui.measure.definition.composite;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.OlapDynamicMeasure;
import org.fasd.olap.aggregation.CalculatedAggregation;
import org.fasd.olap.aggregation.ClassicAggregation;
import org.fasd.olap.aggregation.IMeasureAggregation;
import org.fasd.olap.aggregation.LastAggregation;
import org.fasd.utils.trees.TreeDim;
import org.fasd.utils.trees.TreeDimGroup;
import org.fasd.utils.trees.TreeHierarchy;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeMes;
import org.fasd.utils.trees.TreeParent;
import org.fasd.utils.trees.TreeRoot;

import bpm.fasd.ui.measure.definition.Messages;
import bpm.fasd.ui.measure.definition.dialog.MeasureDefinitionDialog;
import bpm.fasd.ui.measure.definition.tools.TreeContentProvider;
import bpm.fasd.ui.measure.definition.tools.TreeLabelProvider;

public class DynamicAggregationComposite extends Composite {

	private TreeViewer dimensionTree;
	private StackedAggregationsComposite aggComposite;
	private FAModel model;
	
	private TreeLevel selectedLevel;
	private TreeMes selectedMes;
	
	private TreeRoot rootMes;
	private TreeRoot rootDim;
	private MeasureDefinitionDialog dial;
	
	public DynamicAggregationComposite(Composite parent, int style, MeasureDefinitionDialog dial, FAModel model) {
		super(parent, style);
		
		this.model = model;
		
		this.setLayout(new GridLayout(2,false));
		
		createDimensionTree();
		
		createAggregationComposite(dial);
	}

	private void createAggregationComposite(MeasureDefinitionDialog dial) {
		this.dial = dial;
		aggComposite = new StackedAggregationsComposite(this, SWT.BORDER, model, dial);
		aggComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	private void createDimensionTree() {
		
		Composite treeComposite = new Composite(this, SWT.BORDER);
		treeComposite.setLayout(new GridLayout());
		treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		
//		Button btnDefault = new Button(treeComposite, SWT.PUSH);
//		btnDefault.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//		btnDefault.setText("Create default aggregation");
//		btnDefault.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				aggComposite.setDefault();
//			}
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//			}
//		});
		
		
		dimensionTree = new TreeViewer(treeComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		dimensionTree.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		dimensionTree.setContentProvider(new TreeContentProvider());
		dimensionTree.setLabelProvider(new TreeLabelProvider());
		dimensionTree.setUseHashlookup(true);
		dimensionTree.setAutoExpandLevel(4);
		TreeParent root = createModel();
		dimensionTree.setInput(root);
		
		dimensionTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection() instanceof IStructuredSelection) {
		           IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		           //we only take one
		           Object o = selection.getFirstElement();
		           if(o instanceof TreeLevel) {
		        	   selectedMes = null;
		        	   selectedLevel = (TreeLevel) o;
		        	   OLAPLevel lvl = ((TreeLevel)o).getOLAPLevel();
		        	   aggComposite.setLevelSelected(lvl, null);
		           }
		           else if(o instanceof TreeMes) {
		        	   TreeMes tmes = (TreeMes) o;
		        	   selectedMes = tmes;
		        	   OLAPMeasure mes = ((TreeMes)o).getOLAPMeasure();
		        	   aggComposite.setLevelSelected(null, mes);
		           }
		           else {
		        	   selectedMes = null;
		        	   aggComposite.setLevelSelected(null, null);
		           }
				}
			}
		});
	}

	private TreeParent createModel() {
		
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		rootDim = new TreeRoot(Messages.DynamicAggregationComposite_0, model.getOLAPSchema());
		//dims outside groups
		List<OLAPDimension> dims = model.getOLAPSchema().getDimensions();
		for (int i=0; i < dims.size(); i++) {
			if (dims.get(i).getGroupId() == null || dims.get(i).getGroupId().equals("")){ //$NON-NLS-1$
				TreeDim tdim = new TreeDim(dims.get(i));
				for (int j=0; j < dims.get(i).getHierarchies().size(); j++) {
					OLAPHierarchy hiera = dims.get(i).getHierarchies().get(j);
					TreeHierarchy thiera = new TreeHierarchy(hiera);
						
					TreeLevel lvl = null;
						
					for (int k=0; k < hiera.getLevels().size(); k++) {
							lvl = new TreeLevel(hiera.getLevels().get(k));
							thiera.addChild(lvl);
					}
						
					tdim.addChild(thiera);
				}
					
				rootDim.addChild(tdim);
			}
		}
		root.addChild(rootDim);
		
		//dims inside groups
		for(OLAPDimensionGroup group : model.getOLAPSchema().getDimensionGroups())
			root.addChild(createGroups(group));
		
		rootMes = new TreeRoot(Messages.DynamicAggregationComposite_1, model.getOLAPSchema());
		for(OLAPMeasure mes : model.getOLAPSchema().getMeasures()) {
			if(!(mes instanceof OlapDynamicMeasure)) {
				TreeMes tm = new TreeMes(mes);
				rootMes.addChild(tm);
			}
		}
		
		root.addChild(rootMes);
		
		return root;
	}

	private TreeDimGroup createGroups(OLAPDimensionGroup group){
		TreeDimGroup tdg = new TreeDimGroup(group);
		
		for(OLAPDimension d : model.getOLAPSchema().getDimensions()){
				if (group.getId().equals(d.getGroupId())){
					TreeDim td = new TreeDim(d);
					for (OLAPHierarchy h : td.getOLAPDimension().getHierarchies()) {
						TreeHierarchy thiera = new TreeHierarchy(h);
						for (OLAPLevel l :h.getLevels()) {
								TreeLevel lvl = new TreeLevel(l);
								thiera.addChild(lvl);
						}
							
						td.addChild(thiera);
					}
					tdg.addChild(td);
				}
			
		}
			
		for(OLAPGroup g : group.getChilds()){
			tdg.addChild(createGroups((OLAPDimensionGroup)g));
		}	
		return tdg;
	}
	
	public OlapDynamicMeasure getMeasure() {
		return aggComposite.getMeasure();
	}

	public void refreshDimensionTree(IMeasureAggregation agg) {
		if(selectedMes != null) {
			
			for(Object o : rootMes.getChildren()) {
				if(o instanceof TreeMes) {
					((TreeMes)o).setName(((TreeMes)o).getName().replace(" -> Default", "")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
			selectedMes.setName(selectedMes.getName() + " -> Default"); //$NON-NLS-1$
			dimensionTree.refresh();
		}
		else if(selectedLevel != null) {
			String aggType = ""; //$NON-NLS-1$
			if(agg instanceof ClassicAggregation) {
				aggType = ((ClassicAggregation)agg).getAggregator();
			}
			else if(agg instanceof LastAggregation) {
				aggType = ((LastAggregation)agg).getAggregator();
			}
			else if (agg instanceof CalculatedAggregation) {
				aggType = "calculated"; //$NON-NLS-1$
			}
			
			if(selectedLevel.getName().indexOf(" -> ") >= 0) { //$NON-NLS-1$
				selectedLevel.setName(selectedLevel.getName().substring(0, selectedLevel.getName().indexOf(" -> "))); //$NON-NLS-1$
			}
			selectedLevel.setName(selectedLevel.getName() + " -> " + aggType); //$NON-NLS-1$
			
			dimensionTree.refresh();
		}
	}

	public void setEditedMeasureData(OlapDynamicMeasure measure) {
		
		aggComposite.setEditedMeasureData(measure);
		
		//set level labels
		for(Object tr : rootDim.getChildren()) {
			if(tr instanceof TreeParent) {
				for(Object trs : ((TreeParent)tr).getChildren()) {
					if(trs instanceof TreeParent) {
						for(Object trss : ((TreeParent)trs).getChildren()) {
							if(trss instanceof TreeLevel) {
								TreeLevel treeLvl = (TreeLevel) trss;
								OLAPLevel lvl = treeLvl.getOLAPLevel();
								String lvlUname = "["+lvl.getParent().getParent().getName()+"."+lvl.getParent().getName()+"].[" + lvl.getName() +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
								for(IMeasureAggregation agg : measure.getAggregations()) {
									if(agg.getLevel().equals(lvlUname)) {
										
										treeLvl.setName(treeLvl.getName() + " -> " + agg.getTreeLabel()); //$NON-NLS-1$
										
									}
								}
							}
						}
					}
				}
			}
		}
		
		//set the default measure
		for(Object o : rootMes.getChildren()) {
			if(o instanceof TreeMes) {
				TreeMes tm = (TreeMes) o;
				OLAPMeasure mes = tm.getOLAPMeasure();
				
				if(mes.getType().equals(measure.getType())
						&& mes.getAggregator().equals(measure.getAggregator())
						&& (mes.getLastTimeDimensionName() == null || mes.getLastTimeDimensionName().equals(measure.getLastTimeDimensionName()))
						&& mes.getFormula().equals(measure.getFormula())
						&& mes.getOrigin() == measure.getOrigin()) {
					tm.setName(tm.getName() + " -> Default"); //$NON-NLS-1$
					break;
				}
				
			}
		}
		
		dimensionTree.refresh();
	}
	
}
