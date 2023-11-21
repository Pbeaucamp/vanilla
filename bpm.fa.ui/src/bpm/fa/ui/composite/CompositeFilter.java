package bpm.fa.ui.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.ui.Messages;
import bpm.fa.ui.dialogs.DialogLevelValues;
import bpm.fa.ui.ktable.CubeModel;
import de.kupzog.ktable.KTable;

public class CompositeFilter {

	private final FormToolkit toolkit = new FormToolkit(Display.getDefault());;
	private Form form;
	
	private Composite client;
//	private FilterManager filterManager;
	private CubeModel cubeModel;
	private KTable ktable;
	private HashMap<Hierarchy, Composite> filterComposites = new HashMap<Hierarchy, Composite>();
	
	
	public void init(KTable table, OLAPCube cube, CubeModel cubeModel){
		this.cubeModel = cubeModel;
		this.ktable = table;
		if (client != null && !client.isDisposed()){
			client.dispose();
			
			client = toolkit.createComposite(form.getBody());
			client.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
			client.setLayout(new GridLayout());
			
		}
		filterComposites.clear();
		
		
		List<String> elements =  new ArrayList<String>();
		elements.addAll(cube.getMdx().getCols());
		elements.addAll(cube.getMdx().getRows());
		
		for(String s : elements){
			Hierarchy h = ItemElementHelper.getHierarchy(cube, s);
			
			if (h == null){
				continue;
			}
			if (filterComposites.get(h) == null){
				createHierarchy(h);
			}
			
		}
		
		form.layout(true);
		form.getParent().getParent().layout();
	}
	
	public Composite createContent(Composite parent){
		
		form = toolkit.createForm(parent);
		
//		form.setText("BI Object Migration");
		form.getBody().setLayout(new GridLayout(1, false));
//		toolkit.decorateFormHeading(form);
		
		client = toolkit.createComposite(form.getBody());
		client.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		client.setLayout(new GridLayout());
		
		
//		fillToolbar();
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
//		form.getToolBarManager().update(true);
		return form;
	}
	
	private void createHierarchy(final Hierarchy h){
		Composite c = toolkit.createComposite(client);
		c.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		c.setLayout(new GridLayout(2, false));
		
		final Button destroy = toolkit.createButton(c, "", SWT.CHECK); //$NON-NLS-1$
		destroy.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		destroy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO : launch filter and refresh ktable
				cubeModel.enableFilters(h, destroy.getSelection());
				ktable.redraw();
				ktable.getParent().layout(true);
			}
		});
		
//		Label l = toolkit.createLabel(c, "- " + h.getUniqueName());
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		

		final ExpandableComposite exp = toolkit.createExpandableComposite(c, ExpandableComposite.TWISTIE  | ExpandableComposite.FOCUS_TITLE | ExpandableComposite.TITLE_BAR);
		exp.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		exp.setText(h.getUniqueName());
		exp.addExpansionListener(new IExpansionListener() {
			
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				
				
			}
			
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				
//				exp.redraw();
				form.getParent().getParent().layout(true);
			}
		});
//		exp.setLayout(new GridLayout());
		
//		Hyperlink link = toolkit.createHyperlink(c, filterManager.getFilterText(h), SWT.WRAP);
//		link.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		createMemberViewer(exp, h);
		
		filterComposites.put(h, c);
	}
	
	
	private void createMemberViewer(ExpandableComposite main, final Hierarchy hiera){
		Composite parent = toolkit.createComposite(main);
		parent.setLayout(new GridLayout(3, false));
//		parent.setLayoutData(new GridData());
		main.setClient(parent);
		
		
		for(Level l : hiera.getLevel()){
			final Level lvl = l;
			//TODO : remove once MLA fix this mess
			if (l.getUniqueName().equals(hiera.getUniqueName() + ".[All]")){ //$NON-NLS-1$
				continue;
			}
			
			final Button b = toolkit.createButton(parent, "", SWT.CHECK); //$NON-NLS-1$
			b.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false));
			b.setEnabled(false);
			
			
			final Hyperlink h = toolkit.createHyperlink(parent, l.getName(), SWT.NONE);
			try{
				h.setText(lvl.getName() + " " + cubeModel.getFilterManager().getFiltered(hiera, lvl).size() + Messages.CompositeFilter_0); //$NON-NLS-1$
			}catch(Exception ex){
				
			}
			
			h.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2 , 1));
			h.addHyperlinkListener(new IHyperlinkListener() {
				
				@Override
				public void linkExited(HyperlinkEvent e) {
					
					
				}
				
				@Override
				public void linkEntered(HyperlinkEvent e) {
					
					
				}
				
				@Override
				public void linkActivated(HyperlinkEvent e) {
					
					
					DialogLevelValues d = new DialogLevelValues(client.getShell(), cubeModel, hiera, lvl);
					if ( d.open() == DialogLevelValues.OK){
						List l = cubeModel.getFilterManager().getFiltered(hiera, lvl);
						h.setText(lvl.getName() + " " + l.size() + Messages.CompositeFilter_6); //$NON-NLS-1$
						b.setEnabled(!l.isEmpty());
						
						cubeModel.applyFilters();
						ktable.redraw();
						ktable.getParent().layout(true);
					}
					
					
					
				}
			});
			
			
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					cubeModel.getFilterManager().enable(lvl, b.getSelection());
					
					cubeModel.applyFilters();
					ktable.redraw();
					ktable.getParent().layout(true);
				}
			});
		}
		
//		Label l = toolkit.createLabel(parent, "Member Level");
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
//		
//		final ComboViewer levelViewer = new ComboViewer(parent, SWT.READ_ONLY);
//		levelViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
//		levelViewer.setContentProvider(new ArrayContentProvider());
//		levelViewer.setLabelProvider(new LabelProvider(){
//			@Override
//			public String getText(Object element) {
//				return ((Level)element).getName();
//			}
//		});
//		levelViewer.setInput(hiera.getLevel());
		
		
		
		
//		final CheckboxTableViewer viewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.V_SCROLL | SWT.VIRTUAL);
//		viewer.setContentProvider(new ArrayContentProvider());
//		viewer.setLabelProvider(new StructureLabelProvider());
//		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		
//		levelViewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				b.setEnabled(!levelViewer.getSelection().isEmpty());
//				Level l = (Level)((IStructuredSelection)levelViewer.getSelection()).getFirstElement();
//				viewer.setInput(cubeModel.getFilterManager().getOLAPMembers(hiera, l));
//				List lst = cubeModel.getFilterManager().getFiltered(hiera, l);
//				viewer.setCheckedElements(lst.toArray(new Object[lst.size()]));
//				viewer.refresh();
//			}
//		});
		
//		viewer.addCheckStateListener(new ICheckStateListener() {
//			
//			@Override
//			public void checkStateChanged(CheckStateChangedEvent event) {
//				OLAPMember mb = (OLAPMember)event.getElement();
//				
//				if (event.getChecked()){
//					cubeModel.getFilterManager().addFilter(mb);
//					
//				}
//				else{
//					cubeModel.getFilterManager().removeFilter(mb);
//				}
//				
//			}
//		});
//		
		
		
	}
	
	
	
	private void fillToolbar(){
		Action add = new Action(Messages.CompositeFilter_7){
			public void run(){}
		};
		add.setToolTipText(add.getText());
		
		Action del = new Action(Messages.CompositeFilter_8){
			public void run(){}
		};
		del.setToolTipText(add.getText());
	
		form.getToolBarManager().add(add);
		form.getToolBarManager().add(del);
	}
	
}
